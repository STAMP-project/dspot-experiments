/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.wicket;


import java.util.ArrayList;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.geoserver.catalog.KeywordInfo;
import org.junit.Assert;
import org.junit.Test;


public class KeywordsEditorTest {
    WicketTester tester;

    ArrayList<KeywordInfo> keywords;

    @Test
    public void testRemove() throws Exception {
        // WicketHierarchyPrinter.print(tester.getLastRenderedPage(), true, false);
        FormTester ft = tester.newFormTester("form");
        ft.selectMultiple("panel:keywords", new int[]{ 0, 2 });
        tester.executeAjaxEvent("form:panel:removeKeywords", "click");
        Assert.assertEquals(1, keywords.size());
        Assert.assertEquals("two", keywords.get(0).getValue());
    }

    @Test
    public void testAdd() throws Exception {
        // WicketHierarchyPrinter.print(tester.getLastRenderedPage(), true, false);
        FormTester ft = tester.newFormTester("form");
        ft.setValue("panel:newKeyword", "four");
        ft.setValue("panel:lang", "en");
        ft.setValue("panel:vocab", "foobar");
        tester.executeAjaxEvent("form:panel:addKeyword", "click");
        Assert.assertEquals(4, keywords.size());
        Assert.assertEquals("four", keywords.get(3).getValue());
        Assert.assertEquals("en", keywords.get(3).getLanguage());
        Assert.assertEquals("foobar", keywords.get(3).getVocabulary());
    }
}

