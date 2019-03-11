package org.stagemonitor.tracing;


import org.junit.Assert;
import org.junit.Test;


public class BusinessTransactionNamingStrategyTest {
    @Test
    public void testGetBusinessTransationName() throws Exception {
        Assert.assertEquals("Say Hello", BusinessTransactionNamingStrategy.METHOD_NAME_SPLIT_CAMEL_CASE.getBusinessTransactionName("HelloController", "sayHello"));
        Assert.assertEquals("HelloController.sayHello", BusinessTransactionNamingStrategy.CLASS_NAME_DOT_METHOD_NAME.getBusinessTransactionName("HelloController", "sayHello"));
        Assert.assertEquals("HelloController#sayHello", BusinessTransactionNamingStrategy.CLASS_NAME_HASH_METHOD_NAME.getBusinessTransactionName("HelloController", "sayHello"));
    }
}

