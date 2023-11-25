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
import com.sun.javafx.iio.ImageStorage;

import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public final class WEBPImageLoaderFactory implements ImageLoaderFactory {
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

    public static void setupListener() {
        MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
        try {
            LOOKUP.findVirtual(
                    ImageStorage.class,
                    "addImageLoaderFactory",
                    MethodType.methodType(
                            void.class,
                            ImageLoaderFactory.class
                    )
            ).invoke((ImageStorage) LOOKUP.findStatic(
                    ImageStorage.class,
                    "getInstance",
                    MethodType.methodType(
                            ImageStorage.class
                    )
            ).invoke(), instance);
        } catch (Throwable e) {
            try {
                LOOKUP.findStatic(
                        ImageStorage.class,
                        "addImageLoaderFactory",
                        MethodType.methodType(
                                void.class,
                                ImageLoaderFactory.class
                        )
                ).invoke(instance);
            } catch (Throwable e2) {
                e2.addSuppressed(e);
                throw new IllegalStateException("Cannot install WEBPImageLoader", e);
            }
        }
    }
}
