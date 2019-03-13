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


public class FileDataViewTest {
    private WicketTester tester;

    private File root;

    private File one;

    private File two;

    private File lastClicked;

    FileProvider fileProvider;

    @Test
    public void testLoad() throws Exception {
        tester.assertRenderedPage(FormTestPage.class);
        tester.assertNoErrorMessage();
        tester.assertLabel("form:panel:fileTable:fileContent:files:1:nameLink:name", "one.txt");
        tester.assertLabel("form:panel:fileTable:fileContent:files:2:nameLink:name", "two.sld");
        Assert.assertEquals(2, size());
    }

    @Test
    public void testClick() throws Exception {
        tester.clickLink("form:panel:fileTable:fileContent:files:1:nameLink");
        tester.assertRenderedPage(FormTestPage.class);
        tester.assertNoErrorMessage();
        Assert.assertEquals(one, lastClicked);
    }

    @Test
    public void testFilter() throws Exception {
        fileProvider.setFileFilter(new org.apache.wicket.model.Model(new ExtensionFileFilter(".txt")));
        tester.startPage(tester.getLastRenderedPage());
        tester.assertLabel("form:panel:fileTable:fileContent:files:3:nameLink:name", "one.txt");
        Assert.assertEquals(1, size());
    }

    @Test
    public void testSortByName() throws Exception {
        // order by inverse name
        tester.clickLink("form:panel:fileTable:nameHeader:orderByLink", true);
        tester.clickLink("form:panel:fileTable:nameHeader:orderByLink", true);
        tester.assertRenderedPage(FormTestPage.class);
        tester.assertLabel("form:panel:fileTable:fileContent:files:5:nameLink:name", "two.sld");
        tester.assertLabel("form:panel:fileTable:fileContent:files:6:nameLink:name", "one.txt");
    }
}

