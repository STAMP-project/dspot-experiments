package com.bluelinelabs.logansquare.processor;


import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;


public class LowerCaseNamingPolicyTest {
    @Test
    public void generatedSource() {
        ASSERT.about(javaSource()).that(JavaFileObjects.forResource("model/good/LowerCaseNamingPolicyModel.java")).processedWith(new JsonAnnotationProcessor()).compilesWithoutError().and().generatesSources(JavaFileObjects.forResource("generated/LowerCaseNamingPolicyModel$$JsonObjectMapper.java"));
    }
}

