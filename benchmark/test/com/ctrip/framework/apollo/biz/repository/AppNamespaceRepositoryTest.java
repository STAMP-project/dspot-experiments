package com.ctrip.framework.apollo.biz.repository;


import com.ctrip.framework.apollo.biz.AbstractIntegrationTest;
import com.ctrip.framework.apollo.common.entity.AppNamespace;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class AppNamespaceRepositoryTest extends AbstractIntegrationTest {
    @Autowired
    private AppNamespaceRepository repository;

    @Test
    public void testFindByNameAndIsPublicTrue() throws Exception {
        AppNamespace appNamespace = repository.findByNameAndIsPublicTrue("fx.apollo.config");
        Assert.assertEquals("100003171", appNamespace.getAppId());
    }

    @Test
    public void testFindByNameAndNoPublicNamespace() throws Exception {
        AppNamespace appNamespace = repository.findByNameAndIsPublicTrue("application");
        Assert.assertNull(appNamespace);
    }
}

