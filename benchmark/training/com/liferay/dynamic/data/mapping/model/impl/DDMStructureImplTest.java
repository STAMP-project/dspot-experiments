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
package com.liferay.dynamic.data.mapping.model.impl;


import LocaleUtil.BRAZIL;
import LocaleUtil.US;
import com.liferay.dynamic.data.mapping.BaseDDMTestCase;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.util.PropsValues;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 *
 *
 * @author Miguel Angelo Caldas Gallindo
 * @author Marcellus Tavares
 */
@PrepareForTest(PropsValues.class)
@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor({ "com.liferay.portal.kernel.xml.SAXReaderUtil", "com.liferay.portal.util.PropsValues" })
public class DDMStructureImplTest extends BaseDDMTestCase {
    @Test
    public void testGetAvailableLanguageIds() throws Exception {
        Set<Locale> availableLocales = createAvailableLocales(BRAZIL, US);
        DDMForm ddmForm = createDDMForm(availableLocales, US);
        DDMStructure structure = createStructure("Test Structure", ddmForm);
        String[] actualAvailableLanguageIds = structure.getAvailableLanguageIds();
        Assert.assertEquals(Arrays.toString(actualAvailableLanguageIds), 2, actualAvailableLanguageIds.length);
        for (String actualAvailableLanguageId : actualAvailableLanguageIds) {
            Locale actualAvailableLocale = LocaleUtil.fromLanguageId(actualAvailableLanguageId);
            Assert.assertTrue(availableLocales.toString(), availableLocales.contains(actualAvailableLocale));
        }
    }

    @Test
    public void testGetChildrenFieldNames() throws Exception {
        DDMForm ddmForm = createDDMForm();
        DDMFormField field1 = createTextDDMFormField("field1");
        addNestedTextDDMFormFields(field1, "child1Field1", "child2Field1");
        DDMFormField field2 = createTextDDMFormField("field2");
        DDMFormField field3 = createTextDDMFormField("field3");
        addDDMFormFields(ddmForm, field1, field2, field3);
        DDMStructure structure = createStructure("Test Structure", ddmForm);
        List<String> actualChildrenFieldNames = structure.getChildrenFieldNames("field1");
        Assert.assertEquals(actualChildrenFieldNames.toString(), 2, actualChildrenFieldNames.size());
        Assert.assertTrue(actualChildrenFieldNames.toString(), actualChildrenFieldNames.contains("child1Field1"));
        Assert.assertTrue(actualChildrenFieldNames.toString(), actualChildrenFieldNames.contains("child2Field1"));
    }

    @Test
    public void testGetDDMForm() throws Exception {
        DDMForm ddmForm = createDDMForm(createAvailableLocales(US), US);
        ddmForm.addDDMFormField(createTextDDMFormField("field1"));
        DDMStructure structure = createStructure("Test Structure", ddmForm);
        DDMForm ddmForm1 = structure.getDDMForm();
        ddmForm1.addDDMFormField(createTextDDMFormField("field2"));
        DDMForm ddmForm2 = structure.getDDMForm();
        Map<String, DDMFormField> ddmForm2FieldsMap = ddmForm2.getDDMFormFieldsMap(false);
        Assert.assertFalse(ddmForm2FieldsMap.containsKey("field2"));
    }

    @Test
    public void testGetDefaultLanguageId() throws Exception {
        DDMForm ddmForm = createDDMForm(createAvailableLocales(BRAZIL, US), BRAZIL);
        DDMStructure structure = createStructure("Test Structure", ddmForm);
        Assert.assertEquals(LocaleUtil.toLanguageId(BRAZIL), structure.getDefaultLanguageId());
    }

    @Test
    public void testGetFieldLabel() throws Exception {
        DDMForm ddmForm = createDDMForm(createAvailableLocales(BRAZIL, US), US);
        DDMFormField field = createTextDDMFormField("field");
        LocalizedValue label = field.getLabel();
        label.addString(BRAZIL, "Campo de Texto");
        label.addString(US, "Text Field");
        List<DDMFormField> ddmFormFields = ddmForm.getDDMFormFields();
        ddmFormFields.add(field);
        DDMStructure structure = createStructure("Test Structure", ddmForm);
        Assert.assertEquals("Campo de Texto", structure.getFieldLabel("field", BRAZIL));
        Assert.assertEquals("Text Field", structure.getFieldLabel("field", US));
    }

    @Test
    public void testGetFieldNames() throws Exception {
        DDMForm ddmForm = createDDMForm();
        DDMFormField field1 = createTextDDMFormField("field1");
        addNestedTextDDMFormFields(field1, "child1Field1", "child2Field1");
        DDMFormField field2 = createTextDDMFormField("field2");
        DDMFormField field3 = createTextDDMFormField("field3");
        addDDMFormFields(ddmForm, field1, field2, field3);
        DDMStructure structure = createStructure("Test Structure", ddmForm);
        Set<String> expectedFieldNames = new HashSet<>();
        expectedFieldNames.add("child1Field1");
        expectedFieldNames.add("child2Field1");
        expectedFieldNames.add("field1");
        expectedFieldNames.add("field2");
        expectedFieldNames.add("field3");
        Assert.assertEquals(expectedFieldNames, structure.getFieldNames());
    }

    @Test
    public void testGetFieldNamesWithParentStructure() throws Exception {
        DDMForm ddmForm = createDDMForm();
        DDMFormField field1 = createTextDDMFormField("field1");
        addNestedTextDDMFormFields(field1, "child1Field1", "child2Field1");
        DDMFormField field2 = createTextDDMFormField("field2");
        DDMFormField field3 = createTextDDMFormField("field3");
        addDDMFormFields(ddmForm, field1, field2, field3);
        DDMStructure parentStructure = createStructure("Parent Structure", ddmForm);
        DDMStructure childStructure = createStructure("Child Structure", "field4", "field5");
        childStructure.setParentStructureId(parentStructure.getStructureId());
        Set<String> expectedFieldNames = new HashSet<>();
        expectedFieldNames.add("child1Field1");
        expectedFieldNames.add("child2Field1");
        expectedFieldNames.add("field1");
        expectedFieldNames.add("field2");
        expectedFieldNames.add("field3");
        expectedFieldNames.add("field4");
        expectedFieldNames.add("field5");
        Assert.assertEquals(expectedFieldNames, childStructure.getFieldNames());
    }

    @Test
    public void testGetFieldTip() throws Exception {
        DDMForm ddmForm = createDDMForm(createAvailableLocales(BRAZIL, US), US);
        DDMFormField field = createTextDDMFormField("field");
        LocalizedValue tip = field.getTip();
        tip.addString(BRAZIL, "Dica para campo de texto.");
        tip.addString(US, "Tip for text field");
        List<DDMFormField> ddmFormFields = ddmForm.getDDMFormFields();
        ddmFormFields.add(field);
        DDMStructure structure = createStructure("Test Structure", ddmForm);
        Assert.assertEquals("Dica para campo de texto.", structure.getFieldTip("field", BRAZIL));
        Assert.assertEquals("Tip for text field", structure.getFieldTip("field", US));
    }

    @Test
    public void testGetFieldType() throws Exception {
        DDMForm ddmForm = createDDMForm(createAvailableLocales(US), US);
        addTextDDMFormFields(ddmForm, "field1");
        DDMStructure structure = createStructure("Test Structure", ddmForm);
        Assert.assertEquals("text", structure.getFieldType("field1"));
    }

    @Test
    public void testGetGetFieldDataType() throws Exception {
        DDMStructure structure = createStructure("TestStructure", createDDMForm("field1"));
        Assert.assertEquals("string", structure.getFieldDataType("field1"));
    }

    @Test
    public void testGetRootFieldNames() throws Exception {
        DDMForm ddmForm = createDDMForm();
        DDMFormField field1 = createTextDDMFormField("field1");
        addNestedTextDDMFormFields(field1, "child1Field1", "child2Field1");
        DDMFormField field2 = createTextDDMFormField("field2");
        DDMFormField field3 = createTextDDMFormField("field3");
        addDDMFormFields(ddmForm, field1, field2, field3);
        DDMStructure structure = createStructure("Test Structure", ddmForm);
        List<String> expectedFieldNames = new ArrayList<>();
        expectedFieldNames.add("field1");
        expectedFieldNames.add("field2");
        expectedFieldNames.add("field3");
        Assert.assertEquals(expectedFieldNames, structure.getRootFieldNames());
    }

    @Test
    public void testHasField() throws Exception {
        DDMStructure parentStructure = createStructure("Parent Structure", "field1", "field2");
        Assert.assertTrue(parentStructure.hasField("field1"));
        Assert.assertTrue(parentStructure.hasField("field2"));
        DDMStructure childStructure = createStructure("Child Structure", "field3", "field4");
        childStructure.setParentStructureId(parentStructure.getStructureId());
        Assert.assertTrue(childStructure.hasField("field1"));
        Assert.assertTrue(childStructure.hasField("field2"));
        Assert.assertTrue(childStructure.hasField("field3"));
        Assert.assertTrue(childStructure.hasField("field4"));
        Assert.assertFalse(childStructure.hasField("fieldNotFound"));
    }

    @Test
    public void testIsFieldRepeatable() throws Exception {
        DDMForm ddmForm = createDDMForm(createAvailableLocales(US), US);
        DDMFormField field1 = createTextDDMFormField("field1", "label", false, true, false);
        addDDMFormFields(ddmForm, field1);
        DDMStructure structure = createStructure("Test Structure", ddmForm);
        Assert.assertTrue(structure.getFieldRepeatable("field1"));
    }

    @Test
    public void testIsFieldRequired() throws Exception {
        DDMForm ddmForm = createDDMForm(createAvailableLocales(US), US);
        DDMFormField field1 = createTextDDMFormField("field1", "label", false, false, true);
        addDDMFormFields(ddmForm, field1);
        DDMStructure structure = createStructure("Test Structure", ddmForm);
        Assert.assertTrue(structure.getFieldRequired("field1"));
    }
}

