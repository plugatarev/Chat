import java.io.ObjectOutputStream;

public record Client(String name, ObjectOutputStream writer) {}
