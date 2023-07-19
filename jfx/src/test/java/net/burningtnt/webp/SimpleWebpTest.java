package net.burningtnt.webp;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import net.burningtnt.webp.jfx.WEBPImageLoaderFactory;
import org.glavo.png.PNGType;
import org.glavo.png.PNGWriter;
import org.glavo.png.image.ArgbImageWrapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public final class SimpleWebpTest {
    private SimpleWebpTest() {
    }

    public static final String[] inputs = new String[]{
            "bangbang93",
            "bangbang93@2x",
            "chest",
            "chest@2x",
            "chicken",
            "chicken@2x",
            "command",
            "command@2x",
            "craft_table",
            "craft_table@2x",
            "discord",
            "discord@2x",
            "fabric",
            "fabric@2x",
            "forge",
            "forge@2x",
            "furnace",
            "furnace@2x",
            "gamerteam",
            "gamerteam@2x",
            "github",
            "github@2x",
            "glavo",
            "glavo@2x",
            "grass",
            "grass@2x",
            "icon",
            "icon@2x",
            "icon@8x",
            "kookapp",
            "kookapp@2x",
            "mcmod",
            "mcmod@2x",
            "quilt",
            "quilt@2x",
            "red_lnn",
            "red_lnn@2x",
            "yellow_fish",
            "yellow_fish@2x",
            "yushijinhun",
            "yushijinhun@2x"
    };

    private static String current = null;

    @Test
    public void main() throws IOException {
        WEBPImageLoaderFactory.setupListener();

        for (String input : inputs) {
            current = input;

            Image inputImage = new Image(Objects.requireNonNull(SimpleWebpTest.class.getResourceAsStream(String.format("/inputs/%s.webp", current))));
            Image desiresImage = new Image(Objects.requireNonNull(SimpleWebpTest.class.getResourceAsStream(String.format("/desires/%s.png", current))));

            Path output = Paths.get("build/tmp/test", current + ".png").toAbsolutePath();
            Files.deleteIfExists(output);

            new PNGWriter(Files.newOutputStream(output), PNGType.RGBA, PNGWriter.DEFAULT_COMPRESS_LEVEL).write(
                    new ArgbImageWrapper<>(inputImage, (int) inputImage.getWidth(), (int) inputImage.getHeight()) {
                        private final PixelReader pixelReader = inputImage.getPixelReader();

                        @Override
                        public int getArgb(int x, int y) {
                            return pixelReader.getArgb(x, y);
                        }
                    }
            );

            assertEquals(inputImage, desiresImage);
        }
    }

    private static void assertEquals(Image inputImage, Image desireImage) {
        Assertions.assertEquals(inputImage.getWidth(), desireImage.getWidth());
        Assertions.assertEquals(inputImage.getHeight(), desireImage.getHeight());

        PixelReader inputReader = inputImage.getPixelReader();
        PixelReader desireReader = inputImage.getPixelReader();

        for (int y = 0; y < (int) inputImage.getHeight(); y++) {
            for (int x = 0; x < (int) inputImage.getWidth(); x++) {
                if (inputReader.getArgb(x, y) != desireReader.getArgb(x, y)) {
                    Assertions.fail(String.format(
                            "[Test point #%s] Illegal pixel at [%d, %d], expected 0x%08x but found 0x%08x", current, x, y,
                            inputReader.getArgb(x, y), desireReader.getArgb(x, y)
                    ));
                }
            }
        }
    }
}
