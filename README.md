# SimpleWebp

A zero-dependency minimalist Java library for reading WEBP files.

It is implemented in Java and **independent** of Java AWT (ImageIO) / JavaFx.
It can be easily used with Android.

## Feature

* Support for Java 8+;
* needs the `java.base` module;
* Very small (< 20 KiB);
* Supports reading VP8L WEBP images;
* Supports alpha channels;

## Limitations 

* Currently only VP8L Webp is supported

## Adding SimpleWEBP to your build

Please replace `%latest-commit-hash%` with the latest commit hash.
e.g. `f37fa363325003ec1e4b6bc5ee23e890f7db3ee6`

### Use SimpleWEBP without JavaFx / Java AWT (imageio):

Maven:
```xml
<dependency>
  <groupId>com.github.burningtnt.SimpleWEBP</groupId>
  <artifactId>SimpleWEBP</artifactId>
  <version>%latest-commit-hash%</version>
</dependency>
```

Gradle:
```kotlin
implementation("com.github.burningtnt.SimpleWEBP:SimpleWEBP:%latest-commit-hash%")
```

### Use SimpleWEBP in a JavaFx project:

```xml
<dependency>
  <groupId>com.github.burningtnt.SimpleWEBP</groupId>
  <artifactId>jfx</artifactId>
  <version>%latest-commit-hash%</version>
</dependency>
```

Gradle:
```kotlin
implementation("com.github.burningtnt.SimpleWEBP:jfx:%latest-commit-hash%")
```

### Use SimpleWEBP in a Java AWT (imageio) project:

```xml
<dependency>
  <groupId>com.github.burningtnt.SimpleWEBP</groupId>
  <artifactId>awt</artifactId>
  <version>%latest-commit-hash%</version>
</dependency>
```

Gradle:
```kotlin
implementation("com.github.burningtnt.SimpleWEBP:awt:%latest-commit-hash%")
```

## Usage

[`VP8LDeocder`](src/main/java/net/burningtnt/webp/vp8l/VP8LDecoder.java) is used to decode Webp.

This is a simple example to get the RGBA formatted image data:

```java
public final class Example {
    private Example() {
    }

    public static void main(String[] args) throws IOException {
        RGBABuffer rgbaBuffer = SimpleWEBPLoader.decodeStreamByImageLoaders(Example.class.getResourceAsStream("example.webp"));
    }
}
```

If you want to use it in JavaFX, you can install the [`WEBPImageLoader`](jfx/src/main/java/net/burningtnt/webp/jfx/WEBPImageLoaderFactory.java):

```java
public final class JavaFXExample {
    private JavaFXExample() {
    }
    
    public static void main(String[] args) throws IOException {
        WEBPImageLoaderFactory.setupListener();
        RGBABuffer rgbaBuffer = new Image(Example.class.getResourceAsStream("example.webp"));
    }
}
```
