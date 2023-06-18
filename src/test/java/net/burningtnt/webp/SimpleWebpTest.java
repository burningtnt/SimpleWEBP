package net.burningtnt.webp;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import net.burningtnt.webp.jfx.WEBPImageLoaderFactory;
import org.glavo.png.PNGType;
import org.glavo.png.PNGWriter;
import org.glavo.png.image.ArgbImageWrapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;

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
    public void main() throws Exception {
        main(new String[]{});
    }

    public static void main(String[] args) throws Exception {
        WEBPImageLoaderFactory.setupListener();

        for (String input : inputs) {
            current = input;

            Image inputImage = new Image(Files.newInputStream(new File(String.format("src/test/resources/inputs/%s.webp", current)).getAbsoluteFile().toPath()));
            Image desiresImage = new Image(Files.newInputStream(new File(String.format("src/test/resources/desires/%s.png", current)).getAbsoluteFile().toPath()));

            File outputFile = new File(String.format("build/tmp/test/%s.png", current)).getAbsoluteFile();
            Files.deleteIfExists(outputFile.toPath());

            new PNGWriter(Files.newOutputStream(outputFile.toPath()), PNGType.RGBA, PNGWriter.DEFAULT_COMPRESS_LEVEL).write(
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
