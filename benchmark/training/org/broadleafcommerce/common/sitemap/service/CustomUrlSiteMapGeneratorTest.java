/**
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 *
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.common.sitemap.service;


import java.io.File;
import java.io.IOException;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.site.domain.SiteImpl;
import org.broadleafcommerce.common.sitemap.domain.CustomUrlSiteMapGeneratorConfiguration;
import org.broadleafcommerce.common.sitemap.exception.SiteMapException;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Custom URL site map generator tests
 *
 * @author Joshua Skorton (jskorton)
 */
public class CustomUrlSiteMapGeneratorTest extends SiteMapGeneratorTest {
    @Test
    public void testCustomUrlSiteMapGenerator() throws IOException, SiteMapException {
        CustomUrlSiteMapGeneratorConfiguration smgc = getConfiguration();
        testGenerator(smgc, new CustomUrlSiteMapGenerator());
        File file1 = fileService.getResource("/sitemap_index.xml");
        File file2 = fileService.getResource("/sitemap1.xml");
        File file3 = fileService.getResource("/sitemap2.xml");
        compareFiles(file1, "src/test/resources/org/broadleafcommerce/sitemap/custom/sitemap_index.xml");
        compareFiles(file2, "src/test/resources/org/broadleafcommerce/sitemap/custom/sitemap1.xml");
        compareFiles(file3, "src/test/resources/org/broadleafcommerce/sitemap/custom/sitemap2.xml");
    }

    @Test
    public void testSiteMapsWithSiteContext() throws IOException, SiteMapException {
        BroadleafRequestContext brc = new BroadleafRequestContext();
        BroadleafRequestContext.setBroadleafRequestContext(brc);
        Site site = new SiteImpl();
        site.setId(256L);
        brc.setSite(site);
        CustomUrlSiteMapGeneratorConfiguration smgc = getConfiguration();
        testGenerator(smgc, new CustomUrlSiteMapGenerator());
        File file1 = fileService.getResource("/sitemap_index.xml");
        File file2 = fileService.getResource("/sitemap1.xml");
        File file3 = fileService.getResource("/sitemap2.xml");
        Assert.assertThat(file1.getAbsolutePath(), CoreMatchers.containsString("site-256"));
        Assert.assertThat(file2.getAbsolutePath(), CoreMatchers.containsString("site-256"));
        Assert.assertThat(file3.getAbsolutePath(), CoreMatchers.containsString("site-256"));
        compareFiles(file1, "src/test/resources/org/broadleafcommerce/sitemap/custom/sitemap_index.xml");
        compareFiles(file2, "src/test/resources/org/broadleafcommerce/sitemap/custom/sitemap1.xml");
        compareFiles(file3, "src/test/resources/org/broadleafcommerce/sitemap/custom/sitemap2.xml");
        // Remove the request context from thread local so it doesn't get in the way of subsequent tests
        BroadleafRequestContext.setBroadleafRequestContext(null);
    }
}

