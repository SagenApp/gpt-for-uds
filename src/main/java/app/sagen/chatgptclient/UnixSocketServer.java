package app.sagen.chatgptclient;

import app.sagen.chatgptclient.data.ChatCompletionRequestMessage;
import app.sagen.chatgptclient.gpt.ChatGptClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Type;
import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class UnixSocketServer {
    private static final Gson gson = new GsonBuilder().create();

    private record ServerConfig(Path socketFile, String apiKey) {}

    public static ServerConfig parseConfig(String...args) {
        String socketFilePath = "/tmp/gpt_socket"; // Default socket file path
        String gptApiToken = ""; // GPT API token

        // Parse the command-line arguments
        if (args.length >= 2) {
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "--socket-file":
                    case "-f":
                        if (i + 1 < args.length) {
                            socketFilePath = args[++i];
                        } else {
                            System.err.println("Expected argument for socket file path");
                            throw new IllegalStateException("Expected argument for socket file path");
                        }
                        break;
                    case "--gpt-token":
                    case "-t":
                        if (i + 1 < args.length) {
                            gptApiToken = args[++i];
                        } else {
                            throw new IllegalStateException("Expected argument for GPT API token");
                        }
                        break;
                    default:
                        System.err.println("Unexpected argument: " + args[i]);
                        throw new IllegalStateException("Unexpected argument: " + args[i]);
                }
            }
        } else {
            throw new IllegalStateException("Usage: java -jar app.jar -f <socket-file-path> -t <gpt-api-token>");
        }

        // Verify that the GPT API token is set
        if (gptApiToken.isEmpty()) {
            throw new IllegalStateException("GPT API token is required");
        }

        if (socketFilePath.isEmpty()) {
            throw new IllegalStateException("Socket file path is required");
        }

        try {
            Path path = Path.of(socketFilePath);
            return new ServerConfig(path, gptApiToken);
        } catch (Exception e) {
            throw new IllegalStateException("Invalid socket file path: " + socketFilePath, e);
        }
    }

    public static void main(String[] args) throws IOException {

        ServerConfig serverConfig = parseConfig(args);

        // Delete the socket file if it already exists
        Files.deleteIfExists(serverConfig.socketFile());

        // Create the server socket channel and bind it
        try (ServerSocketChannel serverChannel = ServerSocketChannel.open(StandardProtocolFamily.UNIX)) {
            UnixDomainSocketAddress address = UnixDomainSocketAddress.of(serverConfig.socketFile());
            serverChannel.bind(address);

            System.out.println("Server listening on " + serverConfig.socketFile());

            ChatGptClient gpt = new ChatGptClient(serverConfig.apiKey());

            // Server loop
            while (true) {
                // Accept an incoming connection
                try (SocketChannel socketChannel = serverChannel.accept()) {

                    System.out.println("Accepted a connection from " + socketChannel.getRemoteAddress());

                    // Read the length of the incoming message
                    ByteBuffer inputLengthBuffer = ByteBuffer.allocate(4);
                    socketChannel.read(inputLengthBuffer);
                    inputLengthBuffer.flip();
                    int length = inputLengthBuffer.getInt();

                    System.out.println("Incoming message length: " + length);

                    // Allocate a buffer of the specified size to store the incoming message
                    ByteBuffer messageBuffer = ByteBuffer.allocate(length);
                    socketChannel.read(messageBuffer);
                    messageBuffer.flip();

                    // Convert the ByteBuffer to a String that contains our JSON message
                    String jsonMessage = new String(messageBuffer.array());

                    System.out.println("Incoming message: " + jsonMessage);

                    Type listType = new TypeToken<List<ChatCompletionRequestMessage>>() {}.getType();
                    List<ChatCompletionRequestMessage> messages = gson.fromJson(jsonMessage, listType);

                    System.out.println("Parsed message: " + messages);

                    // Stream completion responses back to the client as they arrive
                    StringBuilder finalResponse = new StringBuilder();
                    gpt.streamChatCompletions(messages).forEach(chunk -> {
                        try {
                            String textPart = chunk.textPart();
                            finalResponse.append(textPart);

                            ByteBuffer textBuffer = StandardCharsets.UTF_8.encode(textPart);

                            // Allocate 4 bytes for the length prefix
                            ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
                            lengthBuffer.putInt(textBuffer.limit());
                            lengthBuffer.flip();

                            // Send length followed by the data
                            while (lengthBuffer.hasRemaining()) {
                                socketChannel.write(lengthBuffer);
                            }
                            while (textBuffer.hasRemaining()) {
                                socketChannel.write(textBuffer);
                            }
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
                    System.out.println("Final response: " + finalResponse);
                } catch (Exception e) {
                    System.err.println("An error occurred while handling a socket connection: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } // ServerSocketChannel is auto-closed here due to try-with-resources
    }
}
