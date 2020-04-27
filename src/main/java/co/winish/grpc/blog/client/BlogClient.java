package co.winish.grpc.blog.client;

import com.proto.blog.*;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;

import javax.net.ssl.SSLException;
import java.io.File;

public class BlogClient {
    private ManagedChannel channel;

    private void init(String address, int port) {
        try {
            channel = NettyChannelBuilder.forAddress(address, port)
                    .sslContext(GrpcSslContexts.forClient().trustManager(new File("ssl/ca.crt")).build())
                    .build();
        } catch (SSLException e) {
            e.printStackTrace();
        }

        System.out.println("Done init");
    }

    private void run() {
        init("localhost", 50053);

        //makeCreateBlogCall();
        //makeReadBlogCall();
        //makeUpdateBlogCall();
        //makeDeleteCall();
        makeListCall();

        channel.shutdown();
    }

    public static void main(String[] args) {
        System.out.println("Client started");

        BlogClient blogClient = new BlogClient();
        blogClient.run();
    }

    private void makeCreateBlogCall() {
        BlogServiceGrpc.BlogServiceBlockingStub syncClient = BlogServiceGrpc.newBlockingStub(channel);

        Blog blog = Blog.newBuilder()
                .setAuthorId("Ashcroft")
                .setTitle("Bittersweet symphony")
                .setContent("But I'm a million different people from one day to the next!")
                .build();

        CreateBlogResponse response = syncClient.createBlog(
                CreateBlogRequest.newBuilder()
                    .setBlog(blog)
                    .build()
        );

        System.out.println("Received response: " + response.toString());
    }

    private void makeReadBlogCall() {
        BlogServiceGrpc.BlogServiceBlockingStub syncClient = BlogServiceGrpc.newBlockingStub(channel);

        ReadBlogResponse response = syncClient.readBlog(
                ReadBlogRequest.newBuilder()
                .setId("5ea716487dc9792c29bb3edd")
                .build()
        );

        System.out.println(response.toString());
    }

    private void makeUpdateBlogCall() {
        BlogServiceGrpc.BlogServiceBlockingStub syncClient = BlogServiceGrpc.newBlockingStub(channel);

        UpdateBlogResponse response = syncClient.updateBlog(
                UpdateBlogRequest.newBuilder()
                .setBlog(Blog.newBuilder().setId("5ea7289d7dc97935159fae2f")
                        .setAuthorId("Ashcroft")
                        .setTitle("Lucky man")
                        .setContent("But I'm a lucky man...")
                        .build())
                .build()
        );

        System.out.println(response.toString());
    }

    private void makeDeleteCall() {
        BlogServiceGrpc.BlogServiceBlockingStub syncClient = BlogServiceGrpc.newBlockingStub(channel);

        DeleteBlogResponse response = syncClient.deleteBlog(
                DeleteBlogRequest.newBuilder()
                        .setId("5ea7289d7dc97935159fae2f")
                        .build()
        );

        System.out.println("Deleted: " + response.getId());
    }

    private void makeListCall() {
        BlogServiceGrpc.BlogServiceBlockingStub syncClient = BlogServiceGrpc.newBlockingStub(channel);

        syncClient.listBlogs(ListBlogsRequest.newBuilder().build())
                .forEachRemaining(response -> System.out.println(response.toString()));
    }
}
