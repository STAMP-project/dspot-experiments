/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.wicket.browser;


import java.io.File;
import org.apache.wicket.util.tester.WicketTester;
import org.geoserver.web.FormTestPage;
import org.junit.Assert;
import org.junit.Test;


public class FileBreadcrumsTest {
    private WicketTester tester;

    private File root;

    private File leaf;

    private File lastClicked;

    @Test
    public void testLoad() throws Exception {
        tester.assertRenderedPage(FormTestPage.class);
        tester.assertNoErrorMessage();
        tester.assertLabel("form:panel:path:0:pathItemLink:pathItem", "test-breadcrumbs/");
        tester.assertLabel("form:panel:path:1:pathItemLink:pathItem", "one/");
        tester.assertLabel("form:panel:path:2:pathItemLink:pathItem", "two/");
        tester.assertLabel("form:panel:path:3:pathItemLink:pathItem", "three/");
    }

    @Test
    public void testFollowLink() throws Exception {
        tester.clickLink("form:panel:path:1:pathItemLink");
        tester.assertRenderedPage(FormTestPage.class);
        tester.assertNoErrorMessage();
        Assert.assertEquals(new File("target/test-breadcrumbs/one"), lastClicked);
        tester.assertLabel("form:panel:path:0:pathItemLink:pathItem", "test-breadcrumbs/");
        tester.assertLabel("form:panel:path:1:pathItemLink:pathItem", "one/");
        Assert.assertEquals(2, getList().size());
    }
}

