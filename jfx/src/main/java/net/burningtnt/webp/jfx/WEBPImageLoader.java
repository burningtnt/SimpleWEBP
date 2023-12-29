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
package net.burningtnt.webp.jfx;

import com.sun.javafx.iio.ImageFormatDescription;
import com.sun.javafx.iio.ImageFrame;
import com.sun.javafx.iio.ImageMetadata;
import com.sun.javafx.iio.ImageStorage;
import com.sun.javafx.iio.common.ImageDescriptor;
import com.sun.javafx.iio.common.ImageLoaderImpl;
import com.sun.javafx.iio.common.ImageTools;
import net.burningtnt.bcigenerator.api.BytecodeImpl;
import net.burningtnt.bcigenerator.api.BytecodeImplError;
import net.burningtnt.webp.SimpleWEBPLoader;
import net.burningtnt.webp.jfx.annotations.JavaFXAdapter;
import net.burningtnt.webp.utils.RGBABuffer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class WEBPImageLoader extends ImageLoaderImpl {
    private static final ImageDescriptor IMAGE_DESCRIPTOR = initImageDescriptor();

    public static ImageDescriptor getImageDescriptor() {
        return IMAGE_DESCRIPTOR;
    }

    /**
     * <p>Construct a {@code ImageDescriptor} with WEBP signature in JavaFX <a href="https://github.com/openjdk/jfx/blob/72deb62df704aa1baa355ad2e1428524cb978d6c/javafx-iio/src/com/sun/javafx/iio/common/ImageDescriptor.java#L39">72deb62df704aa1baa355ad2e1428524cb978d6c</a>.</p>
     * <p>Same as {@code new ImageDescriptor("WEBP", extensions, signatures)}.</p>
     *
     * @param extensions A constant which always be {@code "webp"}.
     * @param signatures A constant which always {@code new ImageFormatDescription.Signature[]{new ImageFormatDescription.Signature((byte) 'R', (byte) 'I', (byte) 'F', (byte) 'F')}}.
     * @return The {@code ImageDescriptor} with WEBP signature.
     * @throws IncompatibleClassChangeError If the adapter doesn't match the current JavaFX version.
     */
    @JavaFXAdapter(state = JavaFXAdapter.State.INVLUDED_BEFORE, commit = "72deb62df704aa1baa355ad2e1428524cb978d6c")
    @BytecodeImpl({
            "LABEL METHOD_HEAD",
            "NEW Lcom/sun/javafx/iio/common/ImageDescriptor;",
            "DUP",
            "LDC (STRING \"WEBP\")",
            "ALOAD 0",
            "ALOAD 1",
            "INVOKESPECIAL Lcom/sun/javafx/iio/common/ImageDescriptor;<init>(Ljava/lang/String;[Ljava/lang/String;[Lcom/sun/javafx/iio/ImageFormatDescription$Signature;)V",
            "LABEL RELEASE_PARAMETER",
            "ARETURN",
            "LOCALVARIABLE extensions [Ljava/lang/String; METHOD_HEAD RELEASE_PARAMETER 0",
            "LOCALVARIABLE signatures [Lcom/sun/javafx/iio/ImageFormatDescription/Signature; METHOD_HEAD RELEASE_PARAMETER 1",
            "MAXS 5 2"
    })
    @SuppressWarnings("unused")
    private static ImageDescriptor constructImageDescriptor1(String[] extensions, ImageFormatDescription.Signature[] signatures) throws IncompatibleClassChangeError {
        throw new BytecodeImplError();
    }

    /**
     * <p>Construct a {@code ImageDescriptor} with WEBP signature in JavaFX <a href="https://github.com/openjdk/jfx/blob/f326e78ffdfcbbc9085bc50a38e0b4454b757157/modules/javafx.graphics/src/main/java/com/sun/javafx/iio/common/ImageDescriptor.java#L39">f326e78ffdfcbbc9085bc50a38e0b4454b757157</a>.</p>
     * <p>Same as {@code new ImageDescriptor("WEBP", extensions, signatures, extensions)}.</p>
     * Construct a {@code ImageDescriptor} with WEBP signature.
     *
     * @param extensions   A constant which always be {@code "webp"}.
     * @param signatures   A constant which always {@code new ImageFormatDescription.Signature[]{new ImageFormatDescription.Signature((byte) 'R', (byte) 'I', (byte) 'F', (byte) 'F')}, mimeSubtypes}.
     * @param mimeSubtypes A constant which always be {@code "webp"}.
     * @return The {@code ImageDescriptor} with WEBP signature.
     * @throws IncompatibleClassChangeError If the adapter doesn't match the current JavaFX version.
     */
    @JavaFXAdapter(state = JavaFXAdapter.State.INVLUDED_AFTER, commit = "f326e78ffdfcbbc9085bc50a38e0b4454b757157")
    @BytecodeImpl({
            "LABEL METHOD_HEAD",
            "NEW Lcom/sun/javafx/iio/common/ImageDescriptor;",
            "DUP",
            "LDC (STRING \"WEBP\")",
            "ALOAD 0",
            "ALOAD 1",
            "ALOAD 2",
            "INVOKESPECIAL Lcom/sun/javafx/iio/common/ImageDescriptor;<init>(Ljava/lang/String;[Ljava/lang/String;[Lcom/sun/javafx/iio/ImageFormatDescription$Signature;[Ljava/lang/String;)V",
            "LABEL RELEASE_PARAMETER",
            "ARETURN",
            "LOCALVARIABLE extensions [Ljava/lang/String; METHOD_HEAD RELEASE_PARAMETER 0",
            "LOCALVARIABLE signatures [Lcom/sun/javafx/iio/ImageFormatDescription/Signature; METHOD_HEAD RELEASE_PARAMETER 1",
            "LOCALVARIABLE mimeSubtypes [Ljava/lang/String; METHOD_HEAD RELEASE_PARAMETER 2",
            "MAXS 6 3"
    })
    @SuppressWarnings("unused")
    private static ImageDescriptor constructImageDescriptor2(String[] extensions, ImageFormatDescription.Signature[] signatures, String[] mimeSubtypes) throws IncompatibleClassChangeError {
        throw new BytecodeImplError();
    }

    /**
     * <p>Construct a {@code ImageDescriptor} which adapts different JavaFX versions.</p>
     *
     * <p>If current JavaFX is lower than commit <a href="https://github.com/openjdk/jfx/blob/72deb62df704aa1baa355ad2e1428524cb978d6c/javafx-iio/src/com/sun/javafx/iio/common/ImageDescriptor.java#L39">72deb62df704aa1baa355ad2e1428524cb978d6c</a>, the signature of the constructor of ImageDescriptor is
     * <pre> {@code
     *     public ImageDescriptor(String formatName, String[] extensions, Signature[] signatures)
     * }</pre>
     * <p>
     * However, if current JavaFX is higher than commit <a href="https://github.com/openjdk/jfx/blob/f326e78ffdfcbbc9085bc50a38e0b4454b757157/modules/javafx.graphics/src/main/java/com/sun/javafx/iio/common/ImageDescriptor.java#L39">f326e78ffdfcbbc9085bc50a38e0b4454b757157</a>, the signature is
     * <pre> {@code
     *     public ImageDescriptor(String formatName, String[] extensions, Signature[] signatures, String[] mimeSubtypes)
     * }</pre>
     * </p>
     *
     * @return The {@code ImageDescriptor} with WEBP signature.
     * @throws UnsupportedOperationException If SimpleWEBP cannot adapt current JavaFX version.
     */
    private static ImageDescriptor initImageDescriptor() throws UnsupportedOperationException {
        final String[] extensions = {"webp"};
        final ImageFormatDescription.Signature[] signatures = {new ImageFormatDescription.Signature((byte) 'R', (byte) 'I', (byte) 'F', (byte) 'F')};
        try {
            return constructImageDescriptor1(extensions, signatures);
        } catch (IncompatibleClassChangeError e) {
            try {
                return constructImageDescriptor2(extensions, signatures, extensions);
            } catch (IncompatibleClassChangeError e2) {
                e2.addSuppressed(e);
                throw new UnsupportedOperationException("Cannot construct a ImageDescriptor.", e2);
            }
        }
    }

    private final InputStream inputStream;

    public WEBPImageLoader(InputStream inputStream) {
        super(getImageDescriptor());
        this.inputStream = inputStream;
    }

    @Override
    public void dispose() {
    }

    @Override
    public ImageFrame load(int imageIndex, int rWidth, int rHeight, boolean preserveAspectRatio, boolean smooth) throws IOException {
        RGBABuffer.AbsoluteRGBABuffer rgbaBuffer = SimpleWEBPLoader.decode(this.inputStream);

        int width = rgbaBuffer.getWidth(), height = rgbaBuffer.getHeight();

        int[] outWH = ImageTools.computeDimensions(width, height, rWidth, rHeight, preserveAspectRatio);
        rWidth = outWH[0];
        rHeight = outWH[1];

        ImageFrame imageFrame = new ImageFrame(
                ImageStorage.ImageType.RGBA,
                ByteBuffer.wrap(rgbaBuffer.getRGBAData()),
                width, height,
                width * 4, null,
                new ImageMetadata(
                        null, Boolean.TRUE, null, null, null, null, null,
                        rWidth, rHeight,
                        null, null, null
                )
        );

        return width != rWidth || height != rHeight ? ImageTools.scaleImageFrame(imageFrame, rWidth, rHeight, smooth) : imageFrame;
    }
}
