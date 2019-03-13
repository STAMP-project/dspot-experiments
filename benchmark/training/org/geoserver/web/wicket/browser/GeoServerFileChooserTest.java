/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.wicket.browser;


import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.Model;
import org.geoserver.data.test.MockData;
import org.geoserver.web.ComponentBuilder;
import org.geoserver.web.FormTestPage;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.geoserver.web.wicket.GeoServerDialog;
import org.geoserver.web.wicket.GeoServerDialog.DialogDelegate;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class GeoServerFileChooserTest extends GeoServerWicketTestSupport {
    private File root;

    private File one;

    private File two;

    private File child;

    @Test
    public void testLoad() {
        setupChooser(root);
        GeoServerWicketTestSupport.tester.assertRenderedPage(FormTestPage.class);
        GeoServerWicketTestSupport.tester.assertNoErrorMessage();
        GeoServerWicketTestSupport.tester.assertLabel("form:panel:fileTable:fileTable:fileContent:files:1:nameLink:name", "child/");
        Assert.assertEquals(1, size());
    }

    @Test
    public void testNullRoot() {
        setupChooser(null);
        // make sure it does not now blow out because of the null
        GeoServerWicketTestSupport.tester.assertRenderedPage(FormTestPage.class);
        GeoServerWicketTestSupport.tester.assertNoErrorMessage();
        GeoServerWicketTestSupport.tester.assertLabel("form:panel:breadcrumbs:path:0:pathItemLink:pathItem", ((getTestData().getDataDirectoryRoot().getName()) + "/"));
    }

    @Test
    public void testInDialog() throws Exception {
        GeoServerWicketTestSupport.tester.startPage(new FormTestPage(new ComponentBuilder() {
            public Component buildComponent(String id) {
                return new GeoServerDialog(id);
            }
        }));
        GeoServerWicketTestSupport.tester.assertRenderedPage(FormTestPage.class);
        GeoServerWicketTestSupport.tester.debugComponentTrees();
        GeoServerDialog dialog = ((GeoServerDialog) (GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("form:panel")));
        Assert.assertNotNull(dialog);
        dialog.showOkCancel(new org.apache.wicket.ajax.AjaxRequestHandler(GeoServerWicketTestSupport.tester.getLastRenderedPage()), new DialogDelegate() {
            @Override
            protected Component getContents(String id) {
                return new GeoServerFileChooser(id, new Model(root));
            }

            @Override
            protected boolean onSubmit(AjaxRequestTarget target, Component contents) {
                Assert.assertNotNull(contents);
                Assert.assertTrue((contents instanceof GeoServerFileChooser));
                return true;
            }
        });
        dialog.submit(new org.apache.wicket.ajax.AjaxRequestHandler(GeoServerWicketTestSupport.tester.getLastRenderedPage()));
    }

    @Test
    public void testHideFileSystem() throws Exception {
        GeoServerWicketTestSupport.tester.startPage(new FormTestPage(new ComponentBuilder() {
            public Component buildComponent(String id) {
                return new GeoServerFileChooser(id, new Model(), true);
            }
        }));
        GeoServerWicketTestSupport.tester.assertRenderedPage(FormTestPage.class);
        GeoServerWicketTestSupport.tester.assertNoErrorMessage();
        DropDownChoice<File> rootChoice = ((DropDownChoice<File>) (GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("form:panel:roots")));
        Assert.assertEquals(1, size());
        Assert.assertEquals(getDataDirectory().root(), rootChoice.getChoices().get(0));
    }

    @Test
    public void testAutocompleteDataDirectory() throws Exception {
        FileRootsFinder rootsFinder = new FileRootsFinder(true);
        File dir = getDataDirectory().get("/").dir();
        // looking for a match on basic polygons
        List<String> values = rootsFinder.getMatches(((MockData.CITE_PREFIX) + "/poly"), null).collect(Collectors.toList());
        Assert.assertEquals(1, values.size());
        Assert.assertEquals("file:cite/BasicPolygons.properties", values.get(0));
        // for the sake of checking, find a specific style with a file extension filter (the dd
        // contains both
        // raster.sld and raster.xml
        values = rootsFinder.getMatches("/styles/raster", new ExtensionFileFilter(".sld")).collect(Collectors.toList());
        Assert.assertEquals(1, values.size());
        Assert.assertEquals("file:styles/raster.sld", values.get(0));
    }

    @Test
    public void testAutocompleteDirectories() throws Exception {
        FileRootsFinder rootsFinder = new FileRootsFinder(true);
        File dir = getDataDirectory().get("/").dir();
        // look for a property file in the data dir full path (so, not a relative path match).
        // we should still get directories
        String rootPath = (dir.getCanonicalFile().getAbsolutePath()) + (File.separator);
        ExtensionFileFilter fileFilter = new ExtensionFileFilter(".properties");
        List<String> values = rootsFinder.getMatches(rootPath, fileFilter).collect(Collectors.toList());
        Assert.assertThat(values.size(), Matchers.greaterThan(0));
        Assert.assertEquals(new HashSet(values).size(), values.size());
        Assert.assertThat(values, Matchers.hasItem(("file://" + (new File(rootPath, MockData.CITE_PREFIX).getAbsolutePath()))));
        Assert.assertThat(values, Matchers.hasItem(("file://" + (new File(rootPath, MockData.SF_PREFIX).getAbsolutePath()))));
    }
}

