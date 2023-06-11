# SimpleWebp

A zero-dependency minimalist Java library for reading WEBP files.

It is implemented in Java and JavaFx and **independent** of Java AWT (ImageIO).
It can be easily used with Android.

## Feature

* Support for Java 8+;
* needs the `java.base` and `javafx.graphics` module;
* Very small (< 20 KiB);
* Supports reading uncompressed WEBP images;
* Support for optional alpha channels;

## Limitations 

* Currently only VP8L Webp is supported

## Adding SimpleWebp to your build

Maven:
```xml
<dependency>
  <groupId>net.burningtnt</groupId>
  <artifactId>simple-webp</artifactId>
  <version>0.1.0</version>
</dependency>
```

Gradle:
```kotlin
implementation("net.burningtnt:simple-webp:0.1.0")
```

## Usage

[`VP8LDeocder`](src/main/java/net/burningtnt/webp/vp8l/VP8LDecoder.java) is used to decode Webp

This is a simple example:

```java
WritableImage image = VP8LDecoder.decodeStream(this.getClass().getResourceAsStream("example.webp"));
```
