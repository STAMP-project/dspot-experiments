/**
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.url.service;


import junit.framework.TestCase;
import org.broadleafcommerce.cms.url.domain.URLHandler;
import org.junit.Test;


/**
 * Test URL handling resolution.
 *
 * @author bpolster
 */
public class URLHandlerServiceTest extends TestCase {
    URLHandlerServiceImpl handlerService = new URLHandlerServiceImpl();

    @Test
    public void testFoundSimpleUrl() {
        URLHandler h = handlerService.checkForMatches("/simple_url");
        TestCase.assertTrue(h.getNewURL().equals("/NewSimpleUrl"));
    }

    @Test
    public void testFoundRegExUrl() {
        URLHandler h = handlerService.checkForMatches("/simple_regex");
        TestCase.assertTrue(h.getNewURL().equals("/NewSimpleRegex"));
    }

    @Test
    public void testForSubPackageBadMatchSimpleUrl() {
        URLHandler h = handlerService.checkForMatches("/simple_url/test");
        TestCase.assertTrue((h == null));
    }

    @Test
    public void testFoundBadMatchComplexUrl() {
        URLHandler h = handlerService.checkForMatches("/simple_regex/test");
        TestCase.assertTrue((h == null));
    }

    @Test
    public void testRegEx() {
        URLHandler h = handlerService.checkForMatches("/blogs/first/second");
        TestCase.assertTrue((h != null));
        TestCase.assertTrue(h.getNewURL().equals("/newblogs/second/first"));
    }

    @Test
    public void testRegExStartsWithSpecialRegExChar() {
        URLHandler h = handlerService.checkForMatches("/merchandise/shirts-tops/mens");
        String expectedNewURL = "/merchandise/shirts/mens";
        TestCase.assertTrue((h != null));
        TestCase.assertTrue(expectedNewURL.equals(h.getNewURL()));
    }
}

