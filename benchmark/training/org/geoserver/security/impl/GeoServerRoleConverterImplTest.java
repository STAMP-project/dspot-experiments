/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.impl;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.geoserver.security.GeoServerRoleConverter;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;


public class GeoServerRoleConverterImplTest {
    private GeoServerRoleConverter converter;

    @Test
    public void testConverter() {
        GeoServerRole r1 = new GeoServerRole("r1");
        r1.getProperties().setProperty("r1_p1", "r1_v1");
        r1.getProperties().setProperty("r1_p2", "r1_v2");
        GeoServerRole r2 = new GeoServerRole("r2");
        r2.getProperties().setProperty("r2_p1", "r2_v1");
        GeoServerRole r3 = new GeoServerRole("r3");
        GeoServerRole r = converter.convertRoleFromString(converter.convertRoleToString(r1), "testuser");
        Assert.assertEquals("r1", r.getAuthority());
        Assert.assertEquals(2, r.getProperties().size());
        Assert.assertEquals("r1_v1", r.getProperties().get("r1_p1"));
        Assert.assertEquals("r1_v2", r.getProperties().get("r1_p2"));
        Assert.assertEquals("testuser", r.getUserName());
        List<GeoServerRole> list = new ArrayList<GeoServerRole>();
        list.add(r1);
        list.add(r2);
        list.add(r3);
        Collection<GeoServerRole> resColl = converter.convertRolesFromString(converter.convertRolesToString(list), null);
        Assert.assertEquals(3, resColl.size());
        for (GrantedAuthority auth : resColl) {
            r = ((GeoServerRole) (auth));
            Assert.assertNull(r.getUserName());
            if ("r3".equals(r.getAuthority()))
                continue;

            if ("r2".equals(r.getAuthority())) {
                Assert.assertEquals(1, r.getProperties().size());
                Assert.assertEquals("r2_v1", r.getProperties().get("r2_p1"));
                continue;
            }
            if ("r1".equals(r.getAuthority())) {
                Assert.assertEquals(2, r.getProperties().size());
                Assert.assertEquals("r1_v1", r.getProperties().get("r1_p1"));
                Assert.assertEquals("r1_v2", r.getProperties().get("r1_p2"));
                continue;
            }
            Assert.fail(("Unexpected role: " + (r.getAuthority())));
        }
        Assert.assertNull(converter.convertRoleFromString("  ", null));
        Assert.assertEquals(0, converter.convertRolesFromString("  ", null).size());
        resColl.clear();
        Assert.assertEquals(0, converter.convertRolesToString(resColl).length());
    }
}

