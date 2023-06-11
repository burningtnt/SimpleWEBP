package net.burningtnt.webp;

import javafx.scene.image.Image;
import net.burningtnt.webp.utils.AwtJavaFxTranslator;
import net.burningtnt.webp.vp8l.VP8LDecoder;
import org.glavo.png.PNGType;
import org.glavo.png.PNGWriter;
import org.glavo.png.image.ArgbImageWrapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;

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

    public static String getCurrent() {
        return current;
    }

    @Test
    public void main() throws Exception {
        main(new String[]{});
    }

    public static void main(String[] args) throws Exception {
        for (String input : inputs) {
            current = input;

            File outputFile = new File(String.format("build/tmp/test/%s.png", current)).getAbsoluteFile();
            Files.deleteIfExists(outputFile.toPath());

            ByteArrayOutputStream byteArrayOutputStream = decodeImage(
                    Files.newInputStream(new File(String.format("src/test/resources/inputs/%s.webp", current)).getAbsoluteFile().toPath())
            );

            Files.write(outputFile.toPath(), byteArrayOutputStream.toByteArray());

            Image inputImage = new Image(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
            Image desiresImage = new Image(Files.newInputStream(new File(String.format("src/test/resources/desires/%s.png", current)).getAbsoluteFile().toPath()));

            assertEquals(inputImage, desiresImage);
        }
    }

    private static ByteArrayOutputStream decodeImage(InputStream inputStream) throws IOException {
        Image inputImage;
        try {
            inputImage = VP8LDecoder.decodeStream(inputStream);
        } catch (Throwable e) {
            Assertions.fail(String.format("An error was thrown while parsing data %s.", current), e);
            throw new RuntimeException(); // Unreachable
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        new PNGWriter(byteArrayOutputStream, PNGType.RGBA, PNGWriter.DEFAULT_COMPRESS_LEVEL).write(
                new ArgbImageWrapper<Image>(inputImage, (int) inputImage.getWidth(), (int) inputImage.getHeight()) {
                    @Override
                    public int getArgb(int x, int y) {
                        byte[] rgba = new byte[4];
                        AwtJavaFxTranslator.getDataElementsAsByteArrayFromWritableImage(inputImage, x, y, rgba);
                        return (rgba[3] & 0xFF) << 24 | (rgba[0] & 0xFF) << 16 | (rgba[1] & 0xFF) << 8 | (rgba[2] & 0xFF);
                    }
                }
        );

        return byteArrayOutputStream;
    }

    private static void assertEquals(Image inputImage, Image targetImage) {
        Assertions.assertEquals(inputImage.getWidth(), targetImage.getWidth());
        Assertions.assertEquals(inputImage.getHeight(), targetImage.getHeight());

        byte[] inputRgba = new byte[4];
        byte[] desiresRgba = new byte[4];
        for (int y = 0; y < (int) inputImage.getHeight(); y++) {
            for (int x = 0; x < (int) inputImage.getWidth(); x++) {
                AwtJavaFxTranslator.getDataElementsAsByteArrayFromWritableImage(inputImage, x, y, inputRgba);
                AwtJavaFxTranslator.getDataElementsAsByteArrayFromWritableImage(targetImage, x, y, desiresRgba);

                if (!Arrays.equals(inputRgba, desiresRgba)) {
                    int inputRgbaInt = (inputRgba[3] & 0xFF) << 24 | (inputRgba[0] & 0xFF) << 16 | (inputRgba[1] & 0xFF) << 8 | (inputRgba[2] & 0xFF);
                    int targetRgbaInt = (desiresRgba[3] & 0xFF) << 24 | (desiresRgba[0] & 0xFF) << 16 | (desiresRgba[1] & 0xFF) << 8 | (desiresRgba[2] & 0xFF);

                    Assertions.fail(String.format("[Test point #%s] Illegal pixel at [%d, %d], expected 0x%08x but found 0x%08x", current, x, y, targetRgbaInt, inputRgbaInt));
                }
            }
        }
    }
}
