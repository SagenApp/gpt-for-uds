package app.sagen.chatgptclient.data;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public final class ChatCompletionResponseChunk {
    @SerializedName("id")
    private final String id;
    @SerializedName("system_fingerprint")
    private final String systemFingerprint;
    @SerializedName("textPart")
    private final String textPart;

    public ChatCompletionResponseChunk(
            String id,
            String systemFingerprint,
            String textPart) {
        this.id = id;
        this.systemFingerprint = systemFingerprint;
        this.textPart = textPart;
    }

    @SerializedName("id")
    public String id() {
        return id;
    }

    @SerializedName("system_fingerprint")
    public String systemFingerprint() {
        return systemFingerprint;
    }

    @SerializedName("textPart")
    public String textPart() {
        return textPart;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ChatCompletionResponseChunk) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.systemFingerprint, that.systemFingerprint) &&
                Objects.equals(this.textPart, that.textPart);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, systemFingerprint, textPart);
    }

    @Override
    public String toString() {
        return "ChatCompletionResponseChunk[" +
                "id=" + id + ", " +
                "systemFingerprint=" + systemFingerprint + ", " +
                "textPart=" + textPart + ']';
    }

}
