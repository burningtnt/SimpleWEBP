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
import com.sun.javafx.iio.ImageLoader;
import com.sun.javafx.iio.ImageLoaderFactory;
import net.burningtnt.bcigenerator.api.BytecodeImpl;
import net.burningtnt.bcigenerator.api.BytecodeImplError;
import net.burningtnt.webp.jfx.annotations.JavaFXAdapter;

import java.io.InputStream;

public final class WEBPImageLoaderFactory implements ImageLoaderFactory {
    /**
     * This field is used in the method implemented by Bytecode Implementation Generator.
     */
    @SuppressWarnings("unused")
    private static final WEBPImageLoaderFactory instance = new WEBPImageLoaderFactory();

    private WEBPImageLoaderFactory() {
    }

    @Override
    public ImageFormatDescription getFormatDescription() {
        return WEBPImageLoader.getImageDescriptor();
    }

    @Override
    public ImageLoader createImageLoader(InputStream input) {
        return new WEBPImageLoader(input);
    }

    /**
     * Add the instance of {@code WEBPImageLoaderFactory} into JavaFX <a href="https://github.com/openjdk/jfx/blob/171e484ca63bdfd50599417482eb704f71f10107/modules/javafx.graphics/src/main/java/com/sun/javafx/iio/ImageStorage.java#L199">171e484ca63bdfd50599417482eb704f71f10107</a>.
     * Same as {@code ImageStorage.addImageLoaderFactory(instance)}.
     * @throws NoSuchMethodError If the adapter doesn't match the current JavaFX version.
     */
    @JavaFXAdapter(state = JavaFXAdapter.State.INVLUDED_BEFORE, commit = "171e484ca63bdfd50599417482eb704f71f10107")
    @BytecodeImpl({
            "LABEL METHOD_HEAD",
            "GETSTATIC Lnet/burningtnt/webp/jfx/WEBPImageLoaderFactory;instance:Lnet/burningtnt/webp/jfx/WEBPImageLoaderFactory;",
            "INVOKESTATIC Lcom/sun/javafx/iio/ImageStorage;addImageLoaderFactory(Lcom/sun/javafx/iio/ImageLoaderFactory;)V",
            "LABEL RELEASE_PARAMETER",
            "RETURN",
            "MAXS 1 0"
    })
    @SuppressWarnings("unused")
    private static void addImageLoaderFactory1() throws NoSuchMethodError {
        throw new BytecodeImplError();
    }

    /**
     * Add the instance of {@code WEBPImageLoaderFactory} into JavaFX <a href="https://github.com/openjdk/jfx/blob/f326e78ffdfcbbc9085bc50a38e0b4454b757157/modules/javafx.graphics/src/main/java/com/sun/javafx/iio/ImageStorage.java#L215">f326e78ffdfcbbc9085bc50a38e0b4454b757157</a>.
     * Same as {@code ImageStorage.getInstance().addImageLoaderFactory(instance)}.
     * @throws NoSuchMethodError If the adapter doesn't match the current JavaFX version.
     */
    @JavaFXAdapter(state = JavaFXAdapter.State.INVLUDED_AFTER, commit = "f326e78ffdfcbbc9085bc50a38e0b4454b757157")
    @BytecodeImpl({
            "LABEL METHOD_HEAD",
            "INVOKESTATIC Lcom/sun/javafx/iio/ImageStorage;getInstance()Lcom/sun/javafx/iio/ImageStorage;",
            "GETSTATIC Lnet/burningtnt/webp/jfx/WEBPImageLoaderFactory;instance:Lnet/burningtnt/webp/jfx/WEBPImageLoaderFactory;",
            "INVOKEVIRTUAL Lcom/sun/javafx/iio/ImageStorage;addImageLoaderFactory(Lcom/sun/javafx/iio/ImageLoaderFactory;)V",
            "LABEL RELEASE_PARAMETER",
            "RETURN",
            "MAXS 2 0"
    })
    @SuppressWarnings("unused")
    private static void addImageLoaderFactory2() throws NoSuchMethodError {
        throw new BytecodeImplError();
    }

    /**
     * <p>Add the listener of SimpleWEBP into JavaFX which adapts different JavaFX versions.</p>
     *
     * <p>If current JavaFX is lower than commit <a href="https://github.com/openjdk/jfx/blob/72deb62df704aa1baa355ad2e1428524cb978d6c/javafx-iio/src/com/sun/javafx/iio/common/ImageDescriptor.java#L39">72deb62df704aa1baa355ad2e1428524cb978d6c</a>, the signature of the constructor of ImageDescriptor is
     * <pre> {@code
     *     public ImageDescriptor(String formatName, String[] extensions, Signature[] signatures)
     * }</pre>
     *
     * However, if current JavaFX is higher than commit <a href="https://github.com/openjdk/jfx/blob/f326e78ffdfcbbc9085bc50a38e0b4454b757157/modules/javafx.graphics/src/main/java/com/sun/javafx/iio/common/ImageDescriptor.java#L39">f326e78ffdfcbbc9085bc50a38e0b4454b757157</a>, the signature is
     * <pre> {@code
     *     public ImageDescriptor(String formatName, String[] extensions, Signature[] signatures, String[] mimeSubtypes)
     * }</pre>
     * </p>
     *
     * @throws UnsupportedOperationException If SimpleWEBP cannot adapt current JavaFX version.
     */
    public static void setupListener() throws UnsupportedOperationException {
        try {
            addImageLoaderFactory1();
        } catch (NoSuchMethodError e) {
            try {
                addImageLoaderFactory2();
            } catch (Throwable e2) {
                e2.addSuppressed(e);
                throw new UnsupportedOperationException("Cannot install WEBPImageLoader", e2);
            }
        }
    }
}
