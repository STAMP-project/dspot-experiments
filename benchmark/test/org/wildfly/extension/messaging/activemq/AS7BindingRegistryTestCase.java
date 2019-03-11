package org.wildfly.extension.messaging.activemq;


import java.util.UUID;
import org.jboss.msc.service.ServiceContainer;
import org.junit.Assert;
import org.junit.Test;
import org.wildfly.extension.messaging.activemq.jms.WildFlyBindingRegistry;


public class AS7BindingRegistryTestCase {
    private ServiceContainer container;

    /* https://issues.jboss.org/browse/AS7-4269 */
    @Test
    public void bindUnbindBind() throws Exception {
        WildFlyBindingRegistry registry = new WildFlyBindingRegistry(container);
        Object obj = new Object();
        String name = UUID.randomUUID().toString();
        Assert.assertNull(getBinderServiceFor(name));
        Assert.assertTrue(registry.bind(name, obj));
        Assert.assertNotNull(getBinderServiceFor(name));
        registry.unbind(name);
        Assert.assertNull(getBinderServiceFor(name));
        Assert.assertTrue(registry.bind(name, obj));
        Assert.assertNotNull(getBinderServiceFor(name));
    }
}

