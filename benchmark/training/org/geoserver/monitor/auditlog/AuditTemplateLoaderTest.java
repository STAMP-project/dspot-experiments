/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.monitor.auditlog;


import freemarker.template.Configuration;
import java.io.File;
import java.io.IOException;
import org.geoserver.platform.GeoServerResourceLoader;
import org.geoserver.template.TemplateUtils;
import org.junit.Assert;
import org.junit.Test;


public class AuditTemplateLoaderTest {
    @Test
    public void testLoadDefaultTemplates() throws IOException {
        GeoServerResourceLoader rloader = new GeoServerResourceLoader(new File("./target"));
        AuditTemplateLoader tloader = new AuditTemplateLoader(rloader);
        Configuration config = TemplateUtils.getSafeConfiguration();
        config.setTemplateLoader(tloader);
        Assert.assertNotNull(config.getTemplate("header.ftl"));
        Assert.assertNotNull(config.getTemplate("content.ftl"));
        Assert.assertNotNull(config.getTemplate("footer.ftl"));
    }
}

