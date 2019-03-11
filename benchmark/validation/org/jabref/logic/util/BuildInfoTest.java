package org.jabref.logic.util;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class BuildInfoTest {
    @Test
    public void testDefaults() {
        BuildInfo buildInfo = new BuildInfo("asdf");
        Assertions.assertEquals("*unknown*", buildInfo.getVersion().getFullVersion());
    }

    @Test
    public void testFileImport() {
        BuildInfo buildInfo = new BuildInfo("/org/jabref/util/build.properties");
        Assertions.assertEquals("42", buildInfo.getVersion().getFullVersion());
    }

    @Test
    public void azureInstrumentationKeyIsNotEmpty() {
        BuildInfo buildInfo = new BuildInfo();
        Assertions.assertNotNull(buildInfo.getAzureInstrumentationKey());
        Assertions.assertNotEquals("", buildInfo.getAzureInstrumentationKey());
    }
}

