package net.burningtnt.webp.example;

import javafx.scene.image.Image;
import net.burningtnt.webp.vp8l.VP8LDecoder;

import java.io.IOException;

public final class Example {
    private Example() {
    }

    public static void main(String[] args) throws IOException {
        try {
            Image image = VP8LDecoder.decodeStream(Example.class.getResourceAsStream("example.webp"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
