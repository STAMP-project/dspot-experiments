package com.networknt.registry.support.command;


import com.networknt.registry.Registry;
import com.networknt.registry.URL;
import com.networknt.registry.URLImpl;
import com.networknt.service.SingletonServiceFactory;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by stevehu on 2017-01-18.
 */
public class DirectRegistryTest {
    @Test
    public void testDirectRegistry() {
        Registry registry = SingletonServiceFactory.getBean(Registry.class);
        URL subscribeUrl = URLImpl.valueOf("light://localhost:8080/token");
        List<URL> urls = registry.discover(subscribeUrl);
        Assert.assertEquals(1, urls.size());
        subscribeUrl = URLImpl.valueOf("light://localhost:8080/code");
        urls = registry.discover(subscribeUrl);
        Assert.assertEquals(2, urls.size());
    }
}

