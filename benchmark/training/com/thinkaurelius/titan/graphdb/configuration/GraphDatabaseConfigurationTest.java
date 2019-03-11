package com.thinkaurelius.titan.graphdb.configuration;


import ConfigElement.ILLEGAL_CHARS;
import Configuration.EMPTY;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class GraphDatabaseConfigurationTest {
    @Test
    public void testUniqueNames() {
        Assert.assertFalse(StringUtils.containsAny(GraphDatabaseConfiguration.getOrGenerateUniqueInstanceId(EMPTY), ILLEGAL_CHARS));
    }
}

