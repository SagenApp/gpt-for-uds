package app.sagen.chatgptclient.gpt;


import app.sagen.chatgptclient.data.ChatCompletionRequestMessage;
import app.sagen.chatgptclient.data.ChatCompletionResponseChunk;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ChatGptClient {

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private final String apiKey;
    private final HttpClient httpClient;
    private final Gson gson;

    public ChatGptClient(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new GsonBuilder().create();
    }

    public Stream<ChatCompletionResponseChunk> streamChatCompletions(List<ChatCompletionRequestMessage> messages) {
        JsonElement messagesJsonElement = gson.toJsonTree(messages);
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", "gpt-4");
        requestBody.addProperty("stream", true);
        requestBody.add("messages", messagesJsonElement);
        requestBody.addProperty("temperature", 0.7);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

        try {
            HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.body()));

            // Parse the buffered stream and map to Stream<ChatCompletionResponse>
            return StreamSupport.stream(new JsonIterator(bufferedReader, gson).spliterator(), false);

        } catch (IOException e) {
            throw new UncheckedIOException("Error sending the request to the API", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread was interrupted while sending the request to the API", e);
        }
    }

}
