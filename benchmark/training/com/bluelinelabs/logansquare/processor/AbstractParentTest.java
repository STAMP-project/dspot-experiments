package com.bluelinelabs.logansquare.processor;


import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;


public class AbstractParentTest {
    @Test
    public void generatedSource() {
        ASSERT.about(javaSource()).that(JavaFileObjects.forResource("model/good/AbstractParentModel.java")).processedWith(new JsonAnnotationProcessor()).compilesWithoutError().and().generatesSources(JavaFileObjects.forResource("generated/AbstractParentModel$$JsonObjectMapper.java"));
    }
}

