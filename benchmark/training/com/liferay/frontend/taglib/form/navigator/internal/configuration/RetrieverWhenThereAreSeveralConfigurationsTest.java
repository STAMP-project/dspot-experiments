/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.frontend.taglib.form.navigator.internal.configuration;


import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Alejandro Tard?n
 */
public class RetrieverWhenThereAreSeveralConfigurationsTest extends BaseFormNavigatorEntryConfigurationRetrieverTestCase {
    @Test
    public void testContainsValuesForEntry1() {
        List<String> formNavigatorEntryKeys = formNavigatorEntryConfigurationRetriever.getFormNavigatorEntryKeys("form1", "general", "add").get();
        Assert.assertEquals(formNavigatorEntryKeys.toString(), 3, formNavigatorEntryKeys.size());
        Iterator<String> iterator = formNavigatorEntryKeys.iterator();
        Assert.assertEquals("formNavigatorEntryKey1", iterator.next());
        Assert.assertEquals("formNavigatorEntryKey2", iterator.next());
        Assert.assertEquals("formNavigatorEntryKey3", iterator.next());
    }

    @Test
    public void testContainsValuesForEntry2() {
        List<String> formNavigatorEntryKeys = formNavigatorEntryConfigurationRetriever.getFormNavigatorEntryKeys("form1", "general", "update").get();
        Assert.assertEquals(formNavigatorEntryKeys.toString(), 3, formNavigatorEntryKeys.size());
        Iterator<String> iterator = formNavigatorEntryKeys.iterator();
        Assert.assertEquals("formNavigatorEntryKey1", iterator.next());
        Assert.assertEquals("formNavigatorEntryKey4", iterator.next());
        Assert.assertEquals("formNavigatorEntryKey5", iterator.next());
    }

    @Test
    public void testReturnsEmptyOptionalForAnUnknownCategory() {
        Optional<List<String>> formNavigatorEntryKeys = formNavigatorEntryConfigurationRetriever.getFormNavigatorEntryKeys("form1", "unknownCategory", "add");
        Assert.assertFalse(formNavigatorEntryKeys.isPresent());
    }

    @Test
    public void testReturnsEmptyOptionalForAnUnknownContext() {
        Optional<List<String>> formNavigatorEntryKeys = formNavigatorEntryConfigurationRetriever.getFormNavigatorEntryKeys("form1", "general", "unknownContext");
        Assert.assertFalse(formNavigatorEntryKeys.isPresent());
    }

    @Test
    public void testReturnsEmptyOptionalForAnUnknownFormId() {
        Optional<List<String>> formNavigatorEntryKeys = formNavigatorEntryConfigurationRetriever.getFormNavigatorEntryKeys("unknownForm", "general", "add");
        Assert.assertFalse(formNavigatorEntryKeys.isPresent());
    }
}

