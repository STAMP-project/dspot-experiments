/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.script.web;


import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.util.tester.FormTester;
import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.script.ScriptManager;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.geoserver.web.wicket.CodeMirrorEditor;
import org.junit.Assert;
import org.junit.Test;


public class ScriptEditPageTest extends GeoServerWicketTestSupport {
    @Test
    public void testLoad() {
        tester.assertRenderedPage(ScriptEditPage.class);
        tester.assertNoErrorMessage();
        tester.assertComponent("form:name", HiddenField.class);
        tester.assertComponent("form:type", HiddenField.class);
        tester.assertComponent("form:extension", HiddenField.class);
        tester.assertComponent("form:contents", CodeMirrorEditor.class);
    }

    @Test
    public void testValid() throws IOException {
        FormTester form = tester.newFormTester("form");
        form.setValue("contents:editorContainer:editorParent:editor", "geom.buffer(-1);");
        form.submit();
        tester.assertRenderedPage(ScriptPage.class);
        tester.assertNoErrorMessage();
        ScriptManager scriptManager = GeoServerExtensions.bean(ScriptManager.class);
        File file = new File(scriptManager.wps().dir(), "buffer.groovy");
        Assert.assertTrue(file.exists());
        Assert.assertEquals("geom.buffer(-1);", FileUtils.readFileToString(file));
    }

    @Test
    public void testContentsRequired() {
        FormTester form = tester.newFormTester("form");
        form.setValue("contents:editorContainer:editorParent:editor", "");
        form.submit();
        tester.assertRenderedPage(ScriptEditPage.class);
        tester.assertErrorMessages(new String[]{ "Field 'contents' is required." });
    }
}

