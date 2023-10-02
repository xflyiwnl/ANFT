package me.xflyiwnl.anft.request;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PostRequest implements Request {

    private URI uri;
    private String body;

    public PostRequest() {
    }

    public PostRequest url(String url) {
        uri = URI.create(url);
        return this;
    }

    public PostRequest body(String body) {
        this.body = body;
        return this;
    }

    @Override
    public HttpResponse<String> send() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body))
                .build();
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public HttpResponse<String> sendAsync() {
        return null;
    }

}
