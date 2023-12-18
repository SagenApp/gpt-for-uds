package app.sagen.chatgptclient.gpt;

import app.sagen.chatgptclient.data.ChatCompletionResponse;
import app.sagen.chatgptclient.data.ChatCompletionResponseChunk;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;

// Helper class to iterate over SSE responses from a BufferedReader and convert them to Stream
record JsonIterator(
        BufferedReader bufferedReader,
        Gson gson
) implements Iterable<ChatCompletionResponseChunk> {

    @Override
    public java.util.Iterator<ChatCompletionResponseChunk> iterator() {
        return new ChatCompletionResponseChunkIterator();
    }

    private class ChatCompletionResponseChunkIterator implements java.util.Iterator<ChatCompletionResponseChunk> {
        ChatCompletionResponseChunk nextElement;

        @Override
        public boolean hasNext() {
            try {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.startsWith("data:") && !line.equalsIgnoreCase("data: [DONE]")) {
                        if (parseLineAndUpdateNext(line)) {
                            return true;
                        }
                        // Ignore empty responses
                    }
                    // Else ignore empty lines or other parts of the SSE protocol like "id:" or "retry:"
                }
                return false;
            } catch (IOException e) {
                throw new UncheckedIOException("Error reading from the SSE stream", e);
            }
        }

        private boolean parseLineAndUpdateNext(String line) {
            String jsonContent = line.substring(5).trim(); // Remove "data:" prefix
            ChatCompletionResponse preliminaryNext = gson.fromJson(jsonContent, ChatCompletionResponse.class);
            if (
                    preliminaryNext.choices() != null
                            && !preliminaryNext.choices().isEmpty()
                            && preliminaryNext.choices().get(0).delta() != null
                            && preliminaryNext.choices().get(0).delta().content() != null
            ) {
                nextElement = new ChatCompletionResponseChunk(preliminaryNext.id(), preliminaryNext.systemFingerprint(), preliminaryNext.choices().get(0).delta().content());
                return true;
            }
            return false;
        }

        @Override
        public ChatCompletionResponseChunk next() {
            return nextElement;
        }
    }
}
