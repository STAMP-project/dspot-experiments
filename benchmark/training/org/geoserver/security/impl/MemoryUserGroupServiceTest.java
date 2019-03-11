/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.impl;


import org.geoserver.test.SystemTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(SystemTest.class)
public class MemoryUserGroupServiceTest extends AbstractUserGroupServiceTest {
    @Test
    public void testInsert() throws Exception {
        super.testInsert();
        for (GeoServerUser user : store.getUsers()) {
            Assert.assertTrue(((user.getClass()) == (MemoryGeoserverUser.class)));
        }
        for (GeoServerUserGroup group : store.getUserGroups()) {
            Assert.assertTrue(((group.getClass()) == (MemoryGeoserverUserGroup.class)));
        }
    }
}

