package com.bluelinelabs.logansquare.processor;


import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;


public class DateModelTest {
    @Test
    public void generatedSource() {
        ASSERT.about(javaSource()).that(JavaFileObjects.forResource("model/good/DateModel.java")).processedWith(new JsonAnnotationProcessor()).compilesWithoutError().and().generatesSources(JavaFileObjects.forResource("generated/DateModel$$JsonObjectMapper.java"));
    }
}

