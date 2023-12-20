package app.sagen.chatgptclient.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

public final class ChatCompletionResponse {
    @SerializedName("id")
    private final String id;
    @SerializedName("object")
    private final String object;
    @SerializedName("created")
    private final long created;
    @SerializedName("model")
    private final String model;
    @SerializedName("system_fingerprint")
    private final String systemFingerprint;
    @SerializedName("choices")
    private final List<Choice> choices;

    public ChatCompletionResponse(
            String id,
            String object,
            long created,
            String model,
            String systemFingerprint,
            List<Choice> choices) {
        this.id = id;
        this.object = object;
        this.created = created;
        this.model = model;
        this.systemFingerprint = systemFingerprint;
        this.choices = choices;
    }

    @SerializedName("id")
    public String id() {
        return id;
    }

    @SerializedName("object")
    public String object() {
        return object;
    }

    @SerializedName("created")
    public long created() {
        return created;
    }

    @SerializedName("model")
    public String model() {
        return model;
    }

    @SerializedName("system_fingerprint")
    public String systemFingerprint() {
        return systemFingerprint;
    }

    @SerializedName("choices")
    public List<Choice> choices() {
        return choices;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ChatCompletionResponse) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.object, that.object) &&
                this.created == that.created &&
                Objects.equals(this.model, that.model) &&
                Objects.equals(this.systemFingerprint, that.systemFingerprint) &&
                Objects.equals(this.choices, that.choices);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, object, created, model, systemFingerprint, choices);
    }

    @Override
    public String toString() {
        return "ChatCompletionResponse[" +
                "id=" + id + ", " +
                "object=" + object + ", " +
                "created=" + created + ", " +
                "model=" + model + ", " +
                "systemFingerprint=" + systemFingerprint + ", " +
                "choices=" + choices + ']';
    }

}
