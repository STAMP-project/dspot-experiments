/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.demo;


import FeedbackMessage.ERROR;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.tester.FormTester;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.geoserver.web.wicket.ParamResourceModel;
import org.junit.Assert;
import org.junit.Test;


public class ReprojectPageTest extends GeoServerWicketTestSupport {
    @Test
    public void testReprojectPoint() {
        tester.startPage(ReprojectPage.class);
        FormTester form = tester.newFormTester("form");
        form.setValue("sourceCRS:srs", "EPSG:4326");
        form.setValue("targetCRS:srs", "EPSG:32632");
        form.setValue("sourceGeom", "12 45");
        form.submit();
        tester.clickLink("form:forward", true);
        Assert.assertEquals(ReprojectPage.class, tester.getLastRenderedPage().getClass());
        Assert.assertEquals(0, tester.getMessages(ERROR).size());
        String tx = tester.getComponentFromLastRenderedPage("form:targetGeom").getDefaultModelObjectAsString();
        String[] ordinateStrings = tx.split("\\s+");
        Assert.assertEquals(736446.0261038465, Double.parseDouble(ordinateStrings[0]), 1.0E-6);
        Assert.assertEquals(4987329.504699742, Double.parseDouble(ordinateStrings[1]), 1.0E-6);
    }

    @Test
    public void testInvalidPoint() {
        tester.startPage(ReprojectPage.class);
        FormTester form = tester.newFormTester("form");
        form.setValue("sourceCRS:srs", "EPSG:4326");
        form.setValue("targetCRS:srs", "EPSG:32632");
        form.setValue("sourceGeom", "12 a45a");
        form.submit();
        tester.clickLink("form:forward", true);
        Assert.assertEquals(ReprojectPage.class, tester.getLastRenderedPage().getClass());
        Assert.assertEquals(1, tester.getMessages(ERROR).size());
        String message = getMessage().toString();
        String expected = new ParamResourceModel("GeometryTextArea.parseError", null).getString();
        Assert.assertEquals(expected, message);
    }

    @Test
    public void testReprojectLinestring() {
        tester.startPage(ReprojectPage.class);
        FormTester form = tester.newFormTester("form");
        form.setValue("sourceCRS:srs", "EPSG:4326");
        form.setValue("targetCRS:srs", "EPSG:32632");
        form.setValue("sourceGeom", "LINESTRING(12 45, 13 45)");
        form.submit();
        tester.clickLink("form:forward", true);
        Assert.assertEquals(ReprojectPage.class, tester.getLastRenderedPage().getClass());
        Assert.assertEquals(0, tester.getMessages(ERROR).size());
        String tx = tester.getComponentFromLastRenderedPage("form:targetGeom").getDefaultModelObjectAsString();
        Matcher matcher = Pattern.compile("LINESTRING \\(([\\d\\.]+) ([\\d\\.]+), ([\\d\\.]+) ([\\d\\.]+)\\)").matcher(tx);
        Assert.assertTrue(tx, matcher.matches());
        Assert.assertEquals(736446.0261038465, Double.parseDouble(matcher.group(1)), 1.0E-6);
        Assert.assertEquals(4987329.504699742, Double.parseDouble(matcher.group(2)), 1.0E-6);
        Assert.assertEquals(815261.4271666661, Double.parseDouble(matcher.group(3)), 1.0E-6);
        Assert.assertEquals(4990738.261612577, Double.parseDouble(matcher.group(4)), 1.0E-6);
    }

    @Test
    public void testInvalidGeometry() {
        tester.startPage(ReprojectPage.class);
        FormTester form = tester.newFormTester("form");
        form.setValue("sourceCRS:srs", "EPSG:4326");
        form.setValue("targetCRS:srs", "EPSG:32632");
        form.setValue("sourceGeom", "LINESTRING(12 45, 13 45");// missing ) at the end

        form.submit();
        tester.clickLink("form:forward", true);
        Assert.assertEquals(ReprojectPage.class, tester.getLastRenderedPage().getClass());
        Assert.assertEquals(1, tester.getMessages(ERROR).size());
        String message = getMessage().toString();
        String expected = new ParamResourceModel("GeometryTextArea.parseError", null).getString();
        Assert.assertEquals(expected, message);
    }

    @Test
    public void testPageParams() {
        tester.startPage(ReprojectPage.class, new PageParameters().add("fromSRS", "EPSG:4326").add("toSRS", "EPSG:32632"));
        String source = tester.getComponentFromLastRenderedPage("form:sourceCRS:srs").getDefaultModelObjectAsString();
        String target = tester.getComponentFromLastRenderedPage("form:targetCRS:srs").getDefaultModelObjectAsString();
        Assert.assertEquals("EPSG:4326", source);
        Assert.assertEquals("EPSG:32632", target);
    }
}

