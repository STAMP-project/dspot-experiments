package com.querydsl.apt;


import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class IntegerExtensionsTest extends AbstractProcessorTest {
    private static final String packagePath = "src/test/apt/com/querydsl/";

    @Test
    public void process() throws IOException {
        List<String> sources = Arrays.asList(new File(IntegerExtensionsTest.packagePath, "IntegerExtensions.java").getPath(), new File(IntegerExtensionsTest.packagePath, "ExampleEntity2.java").getPath());
        process(QuerydslAnnotationProcessor.class, sources, "integerExtensions");
        String qtypeContent = Files.toString(new File("target/integerExtensions/com/querydsl/QExampleEntity2.java"), Charsets.UTF_8);
        // The superclass' id property is inherited, but can't be assigned to the custom QInteger
        Assert.assertTrue(qtypeContent.contains("public final ext.java.lang.QInteger id = new ext.java.lang.QInteger(_super.id);"));
    }
}

