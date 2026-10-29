package com.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

public final class HelperMethods {

    private HelperMethods() {} // static helpers only

    /** Reads a file from src/main/resources, e.g. "texts/messageForParagraph.txt". */
    public static String readResource(String path) {
        try (InputStream in = HelperMethods.class.getClassLoader().getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalArgumentException("Resource not found: " + path);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read resource: " + path, e);
        }
    }

    /** Like readResource, but joins line breaks and extra spaces into single spaces. */
    public static String readResourceAsOneLine(String path) {
        return readResource(path).strip().replaceAll("\\s+", " ");
    }
}


