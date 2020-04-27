package co.winish.grpc.blog.server;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.proto.blog.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;

public class BlogServiceImpl extends BlogServiceGrpc.BlogServiceImplBase {

    private MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
    private MongoDatabase database = mongoClient.getDatabase("blogdb");
    private MongoCollection<Document> collection = database.getCollection("blog");


    @Override
    public void createBlog(CreateBlogRequest request, StreamObserver<CreateBlogResponse> responseObserver) {
        Blog blog = request.getBlog();

        Document document = new Document("author_id", blog.getAuthorId())
                .append("title", blog.getTitle())
                .append("content", blog.getContent());

        collection.insertOne(document);
        String id = document.getObjectId("_id").toString();

        responseObserver.onNext(CreateBlogResponse.newBuilder()
                .setBlog(blog.toBuilder().setId(id).build())
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void readBlog(ReadBlogRequest request, StreamObserver<ReadBlogResponse> responseObserver) {
        Document document = null;

        try {
            document = collection.find(eq("_id", new ObjectId(request.getId()))).first();
        } catch (Exception e) {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("The blog with sent id is not found")
                            .asRuntimeException()
            );
        }

        if (document == null)
            responseObserver.onError(
                    Status.NOT_FOUND
                    .withDescription("The blog with sent id is not found")
                    .asRuntimeException()
            );
        else {
            Blog blog = documentToBlog(document);

            responseObserver.onNext(
                    ReadBlogResponse.newBuilder()
                            .setBlog(blog)
                            .build()
            );
            responseObserver.onCompleted();
        }
    }

    @Override
    public void updateBlog(UpdateBlogRequest request, StreamObserver<UpdateBlogResponse> responseObserver) {
        Blog blog = request.getBlog();
        Document document = null;

        try {
            document = collection.find(eq("_id", new ObjectId(blog.getId()))).first();
        } catch (Exception e) {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("The blog with sent id is not found")
                            .augmentDescription(e.getMessage())
                            .asRuntimeException()
            );
        }

        if (document == null)
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("The blog with sent id is not found")
                            .asRuntimeException()
            );
        else {
            Document replacement = new Document("author_id", blog.getAuthorId())
                    .append("title", blog.getTitle())
                    .append("content", blog.getContent())
                    .append("_id", new ObjectId(blog.getId()));

            collection.replaceOne(eq("_id", document.getObjectId("_id")), replacement);

            responseObserver.onNext(
                    UpdateBlogResponse.newBuilder()
                            .setBlog(documentToBlog(replacement))
                            .build()
            );
            responseObserver.onCompleted();
        }
    }

    private Blog documentToBlog(Document document) {
        return Blog.newBuilder()
                .setId(document.getObjectId("_id").toString())
                .setAuthorId(document.getString("author_id"))
                .setTitle(document.getString("title"))
                .setContent(document.getString("content"))
                .build();
    }
}
