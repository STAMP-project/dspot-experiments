package org.pac4j.saml.util;


import RuntimeConstants.INPUT_ENCODING;
import RuntimeConstants.OUTPUT_ENCODING;
import RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS;
import org.apache.velocity.app.VelocityEngine;
import org.junit.Assert;
import org.junit.Test;


public class VelocityEngineFactoryTest {
    @Test
    public void defaultProperties() {
        VelocityEngine engine = VelocityEngineFactory.getEngine();
        Assert.assertNotNull(engine);
        Assert.assertEquals("org.apache.velocity.runtime.resource.loader.StringResourceLoader", engine.getProperty("string.resource.loader.class"));
        Assert.assertEquals("org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader", engine.getProperty("classpath.resource.loader.class"));
        Assert.assertEquals(VelocityEngineFactoryTest.vector("classpath"), engine.getProperty("resource.loader"));
        Assert.assertEquals("UTF-8", engine.getProperty(INPUT_ENCODING));
        Assert.assertEquals("UTF-8", engine.getProperty(OUTPUT_ENCODING));
        Assert.assertEquals("net.shibboleth.utilities.java.support.velocity.SLF4JLogChute", engine.getProperty(RUNTIME_LOG_LOGSYSTEM_CLASS));
    }
}

