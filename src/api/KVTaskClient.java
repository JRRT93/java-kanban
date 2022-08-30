package api;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

    private long apiToken = -1;
    private final URL url;
    private final HttpClient client;

    public KVTaskClient(URL url) {
        client = HttpClient.newHttpClient();
        this.url = url;
        try {
            apiToken = registerAndGetToken();
        } catch (IOException | InterruptedException exception) {
            System.out.println(exception.getMessage());
        }
        System.out.println(apiToken);
    }
    private long registerAndGetToken() throws IOException, InterruptedException {
        URI uri = URI.create(url.toString() + "register");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return Long.parseLong(response.body());
    }

    public void put(String key, String json) throws IOException, InterruptedException {
        URI uri = URI.create(url.toString() + "save/" + key + "/?API_TOKEN=" + apiToken);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public String load(String key) throws IOException, InterruptedException {
        URI uri = URI.create(url.toString() + "load/" + key + "/?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }
}
