package com.baeldung.migration.junit5;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;


@Tag("annotations")
@Tag("junit5")
@RunWith(JUnitPlatform.class)
public class AnnotationTestExampleUnitTest {
    @Test
    public void shouldRaiseAnException() throws Exception {
        Assertions.assertThrows(Exception.class, () -> {
            throw new Exception("This is my expected exception");
        });
    }
}

