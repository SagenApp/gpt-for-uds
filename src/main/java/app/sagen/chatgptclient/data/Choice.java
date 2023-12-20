package app.sagen.chatgptclient.data;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public final class Choice {
    @SerializedName("index")
    private final int index;
    @SerializedName("delta")
    private final Delta delta;
    @SerializedName("logprobs")
    private final Object logprobs;
    @SerializedName("finish_reason")
    private final String finishReason;

    public Choice(
            int index,
            Delta delta,
            Object logprobs,  // Replace Object with actual type if needed
            String finishReason) {
        this.index = index;
        this.delta = delta;
        this.logprobs = logprobs;
        this.finishReason = finishReason;
    }

    @SerializedName("index")
    public int index() {
        return index;
    }

    @SerializedName("delta")
    public Delta delta() {
        return delta;
    }

    @SerializedName("logprobs")
    public Object logprobs() {
        return logprobs;
    }

    @SerializedName("finish_reason")
    public String finishReason() {
        return finishReason;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Choice) obj;
        return this.index == that.index &&
                Objects.equals(this.delta, that.delta) &&
                Objects.equals(this.logprobs, that.logprobs) &&
                Objects.equals(this.finishReason, that.finishReason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, delta, logprobs, finishReason);
    }

    @Override
    public String toString() {
        return "Choice[" +
                "index=" + index + ", " +
                "delta=" + delta + ", " +
                "logprobs=" + logprobs + ", " +
                "finishReason=" + finishReason + ']';
    }

}
