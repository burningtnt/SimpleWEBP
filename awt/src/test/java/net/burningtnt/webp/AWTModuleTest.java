/*
 * Copyright 2023 Burning_TNT
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.burningtnt.webp;

import net.burningtnt.webp.awt.AWTImageLoader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;

public final class AWTModuleTest {
    private AWTModuleTest() {
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
    public void test() throws IOException {
        for (String input : inputs) {
            current = input;

            BufferedImage inputImage, desireImage;
            try (InputStream inputStream = Objects.requireNonNull(AWTModuleTest.class.getResourceAsStream(String.format("/inputs/%s.webp", current)))) {
                inputImage = AWTImageLoader.decode(inputStream);
            }
            try (InputStream inputStream = Objects.requireNonNull(AWTModuleTest.class.getResourceAsStream(String.format("/desires/%s.png", current)))) {
                desireImage = ImageIO.read(inputStream);
            }

            Path output = Paths.get("build/tmp/test", current + ".png").toAbsolutePath();
            Files.deleteIfExists(output);

            try (OutputStream outputStream = Files.newOutputStream(output)) {
                ImageIO.write(inputImage, "png", outputStream);
            }

//            assertEquals(inputImage, desireImage);
        }
    }

    private static void assertEquals(BufferedImage inputImage, BufferedImage desireImage) {
        Assertions.assertEquals(inputImage.getWidth(), desireImage.getWidth());
        Assertions.assertEquals(inputImage.getHeight(), desireImage.getHeight());

        Raster inputReader = inputImage.getRaster();
        Raster desireReader = desireImage.getRaster();
        int[] inputData = new int[4], desireData = new int[4];

        for (int y = 0; y < inputImage.getHeight(); y++) {
            for (int x = 0; x < inputImage.getWidth(); x++) {
                inputReader.getPixel(x, y, inputData);
                desireReader.getPixel(x, y, desireData);
                if (!Arrays.equals(inputData, 0, 3, desireData, 0, 3)) { // Ignore alpha
                    Assertions.fail(String.format(
                            "[Test point #%s] Illegal pixel at [%d, %d], expected [0x%02x,0x%02x,0x%02x,0x%02x] but found [0x%02x,0x%02x,0x%02x,0x%02x]", current, x, y,
                            inputData[0], inputData[1], inputData[2], inputData[3],
                            desireData[0], desireData[1], desireData[2], desireData[3]
                    ));
                }
            }
        }
    }
}
