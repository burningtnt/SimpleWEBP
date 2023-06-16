package net.burningtnt.webp.example;

import net.burningtnt.webp.vp8l.VP8LDecoder;

import java.io.IOException;

public final class Example {
    private Example() {
    }

    public static void main(String[] args) throws IOException {
        try {
            byte[] rgbaImageData = VP8LDecoder.decodeStream(Example.class.getResourceAsStream("example.webp")).getData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
