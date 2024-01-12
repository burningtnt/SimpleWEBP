# SimpleWEBP

A zero-dependency minimalist Java library for reading WEBP files.

It is implemented in Java and **independent** of Java AWT (ImageIO) / JavaFX.
It can be easily used with Android.

## Feature

* Supports Java 8+;
* Only requires the `java.base` module;
* Very small (< 25 KiB);
* Supports reading VP8L WEBP images;
* Supports alpha channels;

## Limitations

* Currently only VP8L WEBP is supported

## Adding SimpleWEBP to your build

Please configure [JitPack](https://jitpack.io/) as a repository.

Maven:

```xml

<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

Gradle:

```kotlin
repositories {
    maven(url = "https://jitpack.io")
}
```

Please replace `%latest-commit-hash%` with the latest commit hash.
e.g. `624c20367a05583af8066907ff95f5e8276ad7f4`

### Use SimpleWEBP without JavaFX / Java AWT (imageio):

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

### Use SimpleWEBP in a JavaFX project:

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

### Primitive Usage

```java
public final class Example {
    private Example() {
    }

    public static void main(String[] args) throws IOException {
        RGBABuffer rgbaBuffer = SimpleWEBPLoader.decode(Example.class.getResourceAsStream("example.webp"));
    }
}
```

### Usage with JavaFX

You should install the [`WEBPImageLoader`](jfx/src/main/java/net/burningtnt/webp/jfx/WEBPImageLoaderFactory.java).

```java
public final class JavaFXExample {
    private JavaFXExample() {
    }

    public static void main(String[] args) {
        WEBPImageLoaderFactory.setupListener();
        
        Image image = new Image(Example.class.getResourceAsStream("example.webp"));
    }
}
```

### Usage with Java AWT

```java
public final class AWTExample {
    private AWTExample() {
    }

    public static void main(String[] args) throws IOException {
        Image image = AWTImageLoader.decode(Example.class.getResourceAsStream("example.webp"));
    }
}
```