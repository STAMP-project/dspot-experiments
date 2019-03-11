/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.template;


import freemarker.template.Configuration;
import freemarker.template.Template;
import java.io.StringWriter;
import org.geotools.feature.DefaultFeatureCollection;
import org.junit.Assert;
import org.junit.Test;


public class FeatureWrapperTest {
    DefaultFeatureCollection features;

    Configuration cfg;

    @Test
    public void testFeatureCollection() throws Exception {
        Template template = cfg.getTemplate("FeatureCollection.ftl");
        StringWriter out = new StringWriter();
        template.process(features, out);
        Assert.assertEquals("fid.1\nfid.2\nfid.3\n", out.toString().replaceAll("\r\n", "\n").replaceAll("\r", "\n"));
    }

    @Test
    public void testFeatureSimple() throws Exception {
        Template template = cfg.getTemplate("FeatureSimple.ftl");
        StringWriter out = new StringWriter();
        template.process(features.iterator().next(), out);
        // replace ',' with '.' for locales which use a comma for decimal point
        Assert.assertEquals("one\n1\n1.1\nPOINT (1 1)", out.toString().replace(',', '.').replaceAll("\r\n", "\n").replaceAll("\r", "\n"));
    }

    @Test
    public void testFeatureDynamic() throws Exception {
        Template template = cfg.getTemplate("FeatureDynamic.ftl");
        StringWriter out = new StringWriter();
        template.process(features.iterator().next(), out);
        // replace ',' with '.' for locales which use a comma for decimal point
        Assert.assertEquals("string=one\nint=1\ndouble=1.1\ngeom=POINT (1 1)\n", out.toString().replace(',', '.').replaceAll("\r\n", "\n").replaceAll("\r", "\n"));
    }

    @Test
    public void testFeatureSequence() throws Exception {
        Template template = cfg.getTemplate("FeatureSequence.ftl");
        StringWriter out = new StringWriter();
        template.process(features, out);
        Assert.assertEquals("three\none\n3", out.toString().replaceAll("\r\n", "\n").replaceAll("\r", "\n"));
    }
}

