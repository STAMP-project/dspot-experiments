package com.googlecode.lanterna.bundle;


import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


/**
 * To ensure our bundled default theme matches the theme definition file in resources
 */
public class DefaultThemeTest {
    @Test
    public void ensureResourceFileDefaultTestIsTheSameAsTheEmbeddedTest() throws IOException, IllegalAccessException, NoSuchFieldException {
        String embeddedDefinition = getEmbeddedDefinition();
        String resourceDefinition = getResourceDefinition();
        Assert.assertEquals(resourceDefinition, embeddedDefinition);
    }
}

