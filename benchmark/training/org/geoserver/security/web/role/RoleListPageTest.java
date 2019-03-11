/**
 * (c) 2014 - 2015 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.web.role;


import RoleListProvider.ParentPropertyName;
import java.util.List;
import org.apache.wicket.Component;
import org.geoserver.security.impl.GeoServerRole;
import org.geoserver.security.web.AbstractSecurityWicketTestSupport;
import org.geoserver.security.web.AbstractTabbedListPageTest;
import org.geoserver.web.wicket.GeoServerDataProvider.Property;
import org.junit.Assert;
import org.junit.Test;


public class RoleListPageTest extends AbstractTabbedListPageTest<GeoServerRole> {
    public static final String SECOND_COLUM_PATH = "itemProperties:1:component:link";

    @Test
    public void testEditParentRole() throws Exception {
        tester.startPage(listPage(getRoleServiceName()));
        GeoServerRole role = gaService.getRoleByName("ROLE_AUTHENTICATED");
        Assert.assertNotNull(role);
        List<Property<GeoServerRole>> props = new RoleListProvider(getRoleServiceName()).getProperties();
        Property<GeoServerRole> parentProp = null;
        for (Property<GeoServerRole> prop : props) {
            if (ParentPropertyName.equals(prop.getName())) {
                parentProp = prop;
                break;
            }
        }
        Component c = getFromList(RoleListPageTest.SECOND_COLUM_PATH, role, parentProp);
        Assert.assertNotNull(c);
        tester.clickLink(c.getPageRelativePath());
        tester.assertRenderedPage(EditRolePage.class);
        Assert.assertTrue(checkEditForm(role.getAuthority()));
    }

    @Test
    public void testReadOnlyService() throws Exception {
        listPage(getRoleServiceName());
        tester.assertVisible(getRemoveLink().getPageRelativePath());
        tester.assertVisible(getAddLink().getPageRelativePath());
        activateRORoleService();
        listPage(getRORoleServiceName());
        tester.assertInvisible(getRemoveLink().getPageRelativePath());
        tester.assertInvisible(getAddLink().getPageRelativePath());
    }
}

