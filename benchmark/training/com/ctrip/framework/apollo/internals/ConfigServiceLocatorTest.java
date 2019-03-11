package com.ctrip.framework.apollo.internals;


import com.ctrip.framework.apollo.core.dto.ServiceDTO;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ConfigServiceLocatorTest {
    @Test
    public void testGetConfigServicesWithSystemProperty() throws Exception {
        String someConfigServiceUrl = " someConfigServiceUrl ";
        String anotherConfigServiceUrl = " anotherConfigServiceUrl ";
        System.setProperty("apollo.configService", ((someConfigServiceUrl + ",") + anotherConfigServiceUrl));
        ConfigServiceLocator configServiceLocator = new ConfigServiceLocator();
        List<ServiceDTO> result = configServiceLocator.getConfigServices();
        Assert.assertEquals(2, result.size());
        Assert.assertEquals(someConfigServiceUrl.trim(), result.get(0).getHomepageUrl());
        Assert.assertEquals(anotherConfigServiceUrl.trim(), result.get(1).getHomepageUrl());
    }
}

