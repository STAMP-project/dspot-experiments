package com.ctrip.framework.apollo.portal.util;


import org.junit.Assert;
import org.junit.Test;


public class RoleUtilsTest {
    @Test
    public void testExtractAppIdFromMasterRoleName() throws Exception {
        Assert.assertEquals("someApp", RoleUtils.extractAppIdFromMasterRoleName("Master+someApp"));
        Assert.assertEquals("someApp", RoleUtils.extractAppIdFromMasterRoleName("Master+someApp+xx"));
        Assert.assertNull(RoleUtils.extractAppIdFromMasterRoleName("ReleaseNamespace+app1+application"));
    }

    @Test
    public void testExtractAppIdFromRoleName() throws Exception {
        Assert.assertEquals("someApp", RoleUtils.extractAppIdFromRoleName("Master+someApp"));
        Assert.assertEquals("someApp", RoleUtils.extractAppIdFromRoleName("ModifyNamespace+someApp+xx"));
        Assert.assertEquals("app1", RoleUtils.extractAppIdFromRoleName("ReleaseNamespace+app1+application"));
    }
}

