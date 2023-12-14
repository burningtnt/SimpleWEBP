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
     * Construct a {@code ImageDescriptor} with WEBP signature.
     * Same as {@code new ImageDescriptor("WEBP", extensions, signatures)}.
     * @param extensions A constant which always be {@code "webp"}.
     * @param signatures A constant which always {@code new ImageFormatDescription.Signature[]{new ImageFormatDescription.Signature((byte) 'R', (byte) 'I', (byte) 'F', (byte) 'F')}}.
     * @return The {@code ImageDescriptor} with WEBP signature.
     * @throws NoSuchMethodError If the adapter doesn't match the current JavaFX version.
     */
    @JavaFXAdapter
    @BytecodeImpl({
            "LABEL METHOD_HEAD",
            "NEW com/sun/javafx/iio/common/ImageDescriptor",
            "DUP",
            "LDC STRING \"WEBP\"",
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
    private static ImageDescriptor constructImageDescriptor1(String[] extensions, ImageFormatDescription.Signature[] signatures) throws NoSuchMethodError {
        throw new BytecodeImplError();
    }

    /**
     * Construct a {@code ImageDescriptor} with WEBP signature.
     * Same as {@code new ImageDescriptor("WEBP", extensions, signatures)}.
     * @param extensions A constant which always be {@code "webp"}.
     * @param signatures A constant which always {@code new ImageFormatDescription.Signature[]{new ImageFormatDescription.Signature((byte) 'R', (byte) 'I', (byte) 'F', (byte) 'F')}, mimeTypes}.
     * @param mimeTypes A constant which always be {@code "webp"}.
     * @return The {@code ImageDescriptor} with WEBP signature.
     * @throws NoSuchMethodError If the adapter doesn't match the current JavaFX version.
     */
    @JavaFXAdapter
    @BytecodeImpl({
            "LABEL METHOD_HEAD",
            "NEW com/sun/javafx/iio/common/ImageDescriptor",
            "DUP",
            "LDC STRING \"WEBP\"",
            "ALOAD 0",
            "ALOAD 1",
            "ALOAD 2",
            "INVOKESPECIAL Lcom/sun/javafx/iio/common/ImageDescriptor;<init>(Ljava/lang/String;[Ljava/lang/String;[Lcom/sun/javafx/iio/ImageFormatDescription$Signature;[Ljava/lang/String;)V",
            "LABEL RELEASE_PARAMETER",
            "ARETURN",
            "LOCALVARIABLE extensions [Ljava/lang/String; METHOD_HEAD RELEASE_PARAMETER 0",
            "LOCALVARIABLE signatures [Lcom/sun/javafx/iio/ImageFormatDescription/Signature; METHOD_HEAD RELEASE_PARAMETER 1",
            "LOCALVARIABLE mimeTypes [Ljava/lang/String; METHOD_HEAD RELEASE_PARAMETER 2",
            "MAXS 6 3"
    })
    @SuppressWarnings("unused")
    private static ImageDescriptor constructImageDescriptor2(String[] extensions, ImageFormatDescription.Signature[] signatures, String[] mimeTypes) throws NoSuchMethodError {
        throw new BytecodeImplError();
    }

    /**
     * Construct a {@code ImageDescriptor} which adapts different JavaFX versions.
     * @return The {@code ImageDescriptor} with WEBP signature.
     */
    private static ImageDescriptor initImageDescriptor() {
        try {
            return constructImageDescriptor1(
                    new String[]{"webp"},
                    new ImageFormatDescription.Signature[]{new ImageFormatDescription.Signature((byte) 'R', (byte) 'I', (byte) 'F', (byte) 'F')}
            );
        } catch (NoSuchMethodError e) {
            try {
                return constructImageDescriptor2(
                        new String[]{"webp"},
                        new ImageFormatDescription.Signature[]{new ImageFormatDescription.Signature((byte) 'R', (byte) 'I', (byte) 'F', (byte) 'F')},
                        new String[]{"webp"}
                );
            } catch (NoSuchMethodError e2) {
                e2.addSuppressed(e);
                throw new IllegalStateException("Cannot construct a ImageDescriptor.", e2);
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
        RGBABuffer.AbsoluteRGBABuffer rgbaBuffer = SimpleWEBPLoader.decodeStreamByImageLoaders(this.inputStream);

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
