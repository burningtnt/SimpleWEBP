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

import java.io.InputStream;
import java.lang.invoke.MethodHandles;

public final class WEBPImageLoaderFactory implements ImageLoaderFactory {
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
    private static void addImageLoaderFactory1() throws NoSuchMethodError {
        throw new BytecodeImplError();
    }

    @BytecodeImpl({
            "LABEL METHOD_HEAD",
            "GETSTATIC Lnet/burningtnt/webp/jfx/WEBPImageLoaderFactory;instance:Lnet/burningtnt/webp/jfx/WEBPImageLoaderFactory;",
            "INVOKESTATIC Lcom/sun/javafx/iio/ImageStorage;addImageLoaderFactory(Lcom/sun/javafx/iio/ImageLoaderFactory;)V",
            "LABEL RELEASE_PARAMETER",
            "RETURN",
            "MAXS 1 0"
    })
    @SuppressWarnings("unused")
    private static void addImageLoaderFactory2() throws NoSuchMethodError {
        throw new BytecodeImplError();
    }

    public static void setupListener() {
        MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
        try {
            addImageLoaderFactory1();
        } catch (NoSuchMethodError e) {
            try {
                addImageLoaderFactory2();
            } catch (Throwable e2) {
                e2.addSuppressed(e);
                throw new IllegalStateException("Cannot install WEBPImageLoader", e2);
            }
        }
    }
}
