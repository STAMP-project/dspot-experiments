package com.tinkerpop.blueprints.util.io.graphson;


import ElementPropertyConfig.ElementPropertiesRule.EXCLUDE;
import ElementPropertyConfig.ElementPropertiesRule.INCLUDE;
import junit.framework.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Stephen Mallette (http://stephen.genoprime.com)
 */
public class ElementPropertyConfigTest {
    @Test
    public void shouldExcludeBoth() {
        final ElementPropertyConfig config = ElementPropertyConfig.excludeProperties(null, null);
        Assert.assertEquals(EXCLUDE, config.getVertexPropertiesRule());
        Assert.assertEquals(EXCLUDE, config.getEdgePropertiesRule());
    }

    @Test
    public void shouldIncludeBoth() {
        final ElementPropertyConfig config = ElementPropertyConfig.includeProperties(null, null);
        Assert.assertEquals(INCLUDE, config.getVertexPropertiesRule());
        Assert.assertEquals(INCLUDE, config.getEdgePropertiesRule());
    }
}

