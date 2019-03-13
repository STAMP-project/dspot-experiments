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
public class RetrieverWhenThereAreConfigurationsFormSeveralFormsTest extends BaseFormNavigatorEntryConfigurationRetrieverTestCase {
    @Test
    public void testContainsValuesForForm1() {
        List<String> formNavigatorEntryKeys = formNavigatorEntryConfigurationRetriever.getFormNavigatorEntryKeys("form1", "general", "add").get();
        Assert.assertEquals(formNavigatorEntryKeys.toString(), 3, formNavigatorEntryKeys.size());
        Iterator<String> iterator = formNavigatorEntryKeys.iterator();
        Assert.assertEquals("formNavigatorEntryKey1", iterator.next());
        Assert.assertEquals("formNavigatorEntryKey2", iterator.next());
        Assert.assertEquals("formNavigatorEntryKey3", iterator.next());
    }

    @Test
    public void testContainsValuesForForm2() {
        List<String> formNavigatorEntryKeys = formNavigatorEntryConfigurationRetriever.getFormNavigatorEntryKeys("form2", "general", "add").get();
        Assert.assertEquals(formNavigatorEntryKeys.toString(), 3, formNavigatorEntryKeys.size());
        Iterator<String> iterator = formNavigatorEntryKeys.iterator();
        Assert.assertEquals("formNavigatorEntryKey4", iterator.next());
        Assert.assertEquals("formNavigatorEntryKey5", iterator.next());
        Assert.assertEquals("formNavigatorEntryKey6", iterator.next());
    }

    @Test
    public void testDoesContainValuesForEntry2IfItIsDeleted() {
        deleteConfiguration("form2");
        Optional<List<String>> formNavigatorEntryKeys = formNavigatorEntryConfigurationRetriever.getFormNavigatorEntryKeys("form2", "general", "update");
        Assert.assertFalse(formNavigatorEntryKeys.isPresent());
    }
}

