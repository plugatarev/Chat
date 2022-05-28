import java.io.ObjectOutputStream;
import java.util.Objects;

public record Client(String name, ObjectOutputStream writer) {}
