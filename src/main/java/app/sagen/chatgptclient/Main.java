package app.sagen.chatgptclient;

import app.sagen.chatgptclient.data.ChatCompletionRequestMessage;
import app.sagen.chatgptclient.data.ChatCompletionResponseChunk;
import app.sagen.chatgptclient.gpt.ChatGptClient;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {
        final String apiKey = "SECRET";

        ChatGptClient gpt = new ChatGptClient(apiKey);

        List<ChatCompletionRequestMessage> messages = List.of(
                new ChatCompletionRequestMessage("system", "If the user says Hi, you must reply with sausage!"),
                new ChatCompletionRequestMessage("user", "Hi gpt, how are you doing?")
        );

        try (Stream<ChatCompletionResponseChunk> responseStream = gpt.streamChatCompletions(messages)) {
            String fullResponse = responseStream.map(ChatCompletionResponseChunk::textPart).collect(Collectors.joining(""));
            System.out.println(fullResponse);
        }
    }

}
