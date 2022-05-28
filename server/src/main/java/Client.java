import java.io.ObjectOutputStream;
import java.util.Objects;

public record Client(String name, ObjectOutputStream writer) {
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Client) obj;
        return Objects.equals(this.name, that.name);
    }
}
