package app.sagen.chatgptclient.data;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public final class Delta {
    @SerializedName("role")
    private final String role;
    @SerializedName("content")
    private final String content;

    public Delta(
            String role,
            String content) {
        this.role = role;
        this.content = content;
    }

    @SerializedName("role")
    public String role() {
        return role;
    }

    @SerializedName("content")
    public String content() {
        return content;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Delta) obj;
        return Objects.equals(this.role, that.role) &&
                Objects.equals(this.content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(role, content);
    }

    @Override
    public String toString() {
        return "Delta[" +
                "role=" + role + ", " +
                "content=" + content + ']';
    }

}
