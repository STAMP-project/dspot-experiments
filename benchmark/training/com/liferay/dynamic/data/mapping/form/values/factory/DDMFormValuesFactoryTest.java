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
package com.liferay.dynamic.data.mapping.form.values.factory;


import LocaleUtil.BRAZIL;
import LocaleUtil.US;
import StringPool.BLANK;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldValueRequestParameterRetriever;
import com.liferay.dynamic.data.mapping.form.values.factory.internal.DDMFormValuesFactoryImpl;
import com.liferay.dynamic.data.mapping.io.internal.DDMFormValuesJSONSerializer;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.LocaleUtil;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;


/**
 *
 *
 * @author Marcellus Tavares
 */
@PrepareForTest(LocaleUtil.class)
@RunWith(PowerMockRunner.class)
public class DDMFormValuesFactoryTest extends PowerMockito {
    @Test
    public void testCreateDefaultWithEmptyRequest() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm();
        DDMFormField nameDDMFormField = DDMFormTestUtil.createTextDDMFormField("Name", true, false, false);
        LocalizedValue namePredefinedValue = createLocalizedValue("Joe", US);
        nameDDMFormField.setPredefinedValue(namePredefinedValue);
        DDMFormField phoneDDMFormField = DDMFormTestUtil.createTextDDMFormField("Phone", true, false, false);
        LocalizedValue phonePredefinedValue = createLocalizedValue("123", US);
        phoneDDMFormField.setPredefinedValue(phonePredefinedValue);
        nameDDMFormField.addNestedDDMFormField(phoneDDMFormField);
        ddmForm.addDDMFormField(nameDDMFormField);
        DDMFormValues expectedDDMFormValues = createDDMFormValues(ddmForm, createAvailableLocales(US), US);
        DDMFormFieldValue nameDDMFormFieldValue = createDDMFormFieldValue("gatu", "Name", namePredefinedValue);
        nameDDMFormFieldValue.addNestedDDMFormFieldValue(createDDMFormFieldValue("waht", "Phone", phonePredefinedValue));
        expectedDDMFormValues.addDDMFormFieldValue(nameDDMFormFieldValue);
        DDMFormValues actualDDMFormValues = _ddmFormValuesFactory.create(new MockHttpServletRequest(), ddmForm);
        List<DDMFormFieldValue> actualDDMFormFieldValues = actualDDMFormValues.getDDMFormFieldValues();
        // Name
        DDMFormFieldValue actualNameDDMFormFieldValue = actualDDMFormFieldValues.get(0);
        Value actualNameDDMFormFieldValueValue = actualNameDDMFormFieldValue.getValue();
        Assert.assertEquals(US, actualNameDDMFormFieldValueValue.getDefaultLocale());
        Assert.assertEquals("Joe", actualNameDDMFormFieldValueValue.getString(US));
        // Phone
        List<DDMFormFieldValue> actualPhoneDDMFormFieldValues = actualNameDDMFormFieldValue.getNestedDDMFormFieldValues();
        DDMFormFieldValue actualPhoneDDMFormFieldValue = actualPhoneDDMFormFieldValues.get(0);
        Value actualPhoneDDMFormFieldValueValue = actualPhoneDDMFormFieldValue.getValue();
        Assert.assertEquals(US, actualPhoneDDMFormFieldValueValue.getDefaultLocale());
        Assert.assertEquals("123", actualPhoneDDMFormFieldValueValue.getString(US));
    }

    @Test
    public void testCreateWithLocalizableFields() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm("Title", "Content");
        Set<Locale> availableLocales = createAvailableLocales(BRAZIL, US);
        Locale defaultLocale = LocaleUtil.US;
        ddmForm.setAvailableLocales(availableLocales);
        ddmForm.setDefaultLocale(defaultLocale);
        DDMFormValues expectedDDMFormValues = createDDMFormValues(ddmForm, availableLocales, defaultLocale);
        expectedDDMFormValues.addDDMFormFieldValue(createDDMFormFieldValue("wqer", "Title", createLocalizedValue("Title", "Titulo", US)));
        expectedDDMFormValues.addDDMFormFieldValue(createDDMFormFieldValue("thsy", "Content", createLocalizedValue("Content", "Conteudo", US)));
        MockHttpServletRequest mockHttpServletRequest = createMockHttpServletRequest(defaultLocale, availableLocales);
        // Title
        mockHttpServletRequest.addParameter("ddm$$Title$wqer$0$$en_US", "Title");
        mockHttpServletRequest.addParameter("ddm$$Title$wqer$0$$pt_BR", "Titulo");
        // Content
        mockHttpServletRequest.addParameter("ddm$$Content$thsy$0$$en_US", "Content");
        mockHttpServletRequest.addParameter("ddm$$Content$thsy$0$$pt_BR", "Conteudo");
        DDMFormValues actualDDMFormValues = _ddmFormValuesFactory.create(mockHttpServletRequest, ddmForm);
        assertEquals(expectedDDMFormValues, actualDDMFormValues);
    }

    @Test
    public void testCreateWithRepeatableAndLocalizableField() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm();
        Set<Locale> availableLocales = createAvailableLocales(BRAZIL, US);
        Locale defaultLocale = LocaleUtil.US;
        ddmForm.setAvailableLocales(availableLocales);
        ddmForm.setDefaultLocale(defaultLocale);
        DDMFormField ddmFormField = DDMFormTestUtil.createTextDDMFormField("Title", true, true, false);
        ddmForm.addDDMFormField(ddmFormField);
        DDMFormValues expectedDDMFormValues = createDDMFormValues(ddmForm, availableLocales, defaultLocale);
        expectedDDMFormValues.addDDMFormFieldValue(createDDMFormFieldValue("wqer", "Title", createLocalizedValue("Title 1", "Titulo 1", defaultLocale)));
        expectedDDMFormValues.addDDMFormFieldValue(createDDMFormFieldValue("fahu", "Title", createLocalizedValue("Title 2", "Titulo 2", defaultLocale)));
        MockHttpServletRequest mockHttpServletRequest = createMockHttpServletRequest(defaultLocale, availableLocales);
        // Title
        mockHttpServletRequest.addParameter("ddm$$Title$wqer$0$$en_US", "Title 1");
        mockHttpServletRequest.addParameter("ddm$$Title$wqer$0$$pt_BR", "Titulo 1");
        mockHttpServletRequest.addParameter("ddm$$Title$fahu$1$$en_US", "Title 2");
        mockHttpServletRequest.addParameter("ddm$$Title$fahu$1$$pt_BR", "Titulo 2");
        DDMFormValues actualDDMFormValues = _ddmFormValuesFactory.create(mockHttpServletRequest, ddmForm);
        assertEquals(expectedDDMFormValues, actualDDMFormValues);
    }

    @Test
    public void testCreateWithRepeatableAndLocalizableNestedField() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm();
        Set<Locale> availableLocales = createAvailableLocales(BRAZIL, US);
        Locale defaultLocale = LocaleUtil.US;
        ddmForm.setAvailableLocales(availableLocales);
        ddmForm.setDefaultLocale(defaultLocale);
        DDMFormField nameDDMFormField = DDMFormTestUtil.createTextDDMFormField("Name", true, true, false);
        DDMFormField phoneDDMFormField = DDMFormTestUtil.createTextDDMFormField("Phone", true, true, false);
        nameDDMFormField.addNestedDDMFormField(phoneDDMFormField);
        ddmForm.addDDMFormField(nameDDMFormField);
        DDMFormValues expectedDDMFormValues = createDDMFormValues(ddmForm, availableLocales, defaultLocale);
        DDMFormFieldValue paulDDMFormFieldValue = createDDMFormFieldValue("wqer", "Name", createLocalizedValue("Paul", "Paulo", defaultLocale));
        paulDDMFormFieldValue.addNestedDDMFormFieldValue(createDDMFormFieldValue("gatu", "Phone", createLocalizedValue("12", "34", defaultLocale)));
        paulDDMFormFieldValue.addNestedDDMFormFieldValue(createDDMFormFieldValue("hato", "Phone", createLocalizedValue("56", "78", defaultLocale)));
        expectedDDMFormValues.addDDMFormFieldValue(paulDDMFormFieldValue);
        DDMFormFieldValue joeDDMFormFieldValue = createDDMFormFieldValue("fahu", "Name", createLocalizedValue("Joe", "Joao", defaultLocale));
        joeDDMFormFieldValue.addNestedDDMFormFieldValue(createDDMFormFieldValue("jamh", "Phone", createLocalizedValue("90", "01", defaultLocale)));
        expectedDDMFormValues.addDDMFormFieldValue(joeDDMFormFieldValue);
        MockHttpServletRequest mockHttpServletRequest = createMockHttpServletRequest(defaultLocale, availableLocales);
        // Name
        mockHttpServletRequest.addParameter("ddm$$Name$wqer$0$$en_US", "Paul");
        mockHttpServletRequest.addParameter("ddm$$Name$wqer$0$$pt_BR", "Paulo");
        mockHttpServletRequest.addParameter("ddm$$Name$fahu$1$$en_US", "Joe");
        mockHttpServletRequest.addParameter("ddm$$Name$fahu$1$$pt_BR", "Joao");
        // Phone
        mockHttpServletRequest.addParameter("ddm$$Name$wqer$0#Phone$gatu$0$$en_US", "12");
        mockHttpServletRequest.addParameter("ddm$$Name$wqer$0#Phone$gatu$0$$pt_BR", "34");
        mockHttpServletRequest.addParameter("ddm$$Name$wqer$0#Phone$hato$1$$en_US", "56");
        mockHttpServletRequest.addParameter("ddm$$Name$wqer$0#Phone$hato$1$$pt_BR", "78");
        mockHttpServletRequest.addParameter("ddm$$Name$fahu$1#Phone$jamh$0$$en_US", "90");
        mockHttpServletRequest.addParameter("ddm$$Name$fahu$1#Phone$jamh$0$$pt_BR", "01");
        DDMFormValues actualDDMFormValues = _ddmFormValuesFactory.create(mockHttpServletRequest, ddmForm);
        assertEquals(expectedDDMFormValues, actualDDMFormValues);
    }

    @Test
    public void testCreateWithRepeatableAndLocalizableNestedFields() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm();
        Set<Locale> availableLocales = createAvailableLocales(BRAZIL, US);
        Locale defaultLocale = LocaleUtil.US;
        ddmForm.setAvailableLocales(availableLocales);
        ddmForm.setDefaultLocale(defaultLocale);
        DDMFormField nameDDMFormField = DDMFormTestUtil.createTextDDMFormField("Name", true, true, false);
        DDMFormField text1DDMFormField = DDMFormTestUtil.createTextDDMFormField("Text1", true, true, false);
        nameDDMFormField.addNestedDDMFormField(text1DDMFormField);
        DDMFormField text2DDMFormField = DDMFormTestUtil.createTextDDMFormField("Text2", true, true, false);
        nameDDMFormField.addNestedDDMFormField(text2DDMFormField);
        ddmForm.addDDMFormField(nameDDMFormField);
        DDMFormValues expectedDDMFormValues = createDDMFormValues(ddmForm, availableLocales, defaultLocale);
        DDMFormFieldValue paulDDMFormFieldValue = createDDMFormFieldValue("wqer", "Name", createLocalizedValue("Paul", "Paulo", defaultLocale));
        paulDDMFormFieldValue.addNestedDDMFormFieldValue(createDDMFormFieldValue("gatu", "Text1", createLocalizedValue("Text1 Paul One", "Text1 Paulo Um", defaultLocale)));
        paulDDMFormFieldValue.addNestedDDMFormFieldValue(createDDMFormFieldValue("hayt", "Text1", createLocalizedValue("Text1 Paul Two", "Text1 Paulo Dois", defaultLocale)));
        paulDDMFormFieldValue.addNestedDDMFormFieldValue(createDDMFormFieldValue("haby", "Text2", createLocalizedValue("Text2 Paul One", "Text2 Paulo Um", defaultLocale)));
        paulDDMFormFieldValue.addNestedDDMFormFieldValue(createDDMFormFieldValue("makp", "Text2", createLocalizedValue("Text2 Paul Two", "Text2 Paulo Dois", defaultLocale)));
        expectedDDMFormValues.addDDMFormFieldValue(paulDDMFormFieldValue);
        DDMFormFieldValue joeDDMFormFieldValue = createDDMFormFieldValue("fahu", "Name", createLocalizedValue("Joe", "Joao", defaultLocale));
        joeDDMFormFieldValue.addNestedDDMFormFieldValue(createDDMFormFieldValue("banm", "Text1", createLocalizedValue("Text1 Joe One", "Text1 Joao Um", defaultLocale)));
        joeDDMFormFieldValue.addNestedDDMFormFieldValue(createDDMFormFieldValue("bagj", "Text2", createLocalizedValue("Text2 Joe One", "Text2 Joao Um", defaultLocale)));
        expectedDDMFormValues.addDDMFormFieldValue(joeDDMFormFieldValue);
        MockHttpServletRequest mockHttpServletRequest = createMockHttpServletRequest(defaultLocale, availableLocales);
        // Name
        mockHttpServletRequest.addParameter("ddm$$Name$wqer$0$$en_US", "Paul");
        mockHttpServletRequest.addParameter("ddm$$Name$wqer$0$$pt_BR", "Paulo");
        mockHttpServletRequest.addParameter("ddm$$Name$fahu$1$$en_US", "Joe");
        mockHttpServletRequest.addParameter("ddm$$Name$fahu$1$$pt_BR", "Joao");
        // Text 1
        mockHttpServletRequest.addParameter("ddm$$Name$wqer$0#Text1$gatu$0$$en_US", "Text1 Paul One");
        mockHttpServletRequest.addParameter("ddm$$Name$wqer$0#Text1$gatu$0$$pt_BR", "Text1 Paulo Um");
        mockHttpServletRequest.addParameter("ddm$$Name$wqer$0#Text1$hayt$1$$en_US", "Text1 Paul Two");
        mockHttpServletRequest.addParameter("ddm$$Name$wqer$0#Text1$hayt$1$$pt_BR", "Text1 Paulo Dois");
        mockHttpServletRequest.addParameter("ddm$$Name$fahu$1#Text1$banm$0$$en_US", "Text1 Joe One");
        mockHttpServletRequest.addParameter("ddm$$Name$fahu$1#Text1$banm$0$$pt_BR", "Text1 Joao Um");
        // Text 2
        mockHttpServletRequest.addParameter("ddm$$Name$wqer$0#Text2$haby$0$$en_US", "Text2 Paul One");
        mockHttpServletRequest.addParameter("ddm$$Name$wqer$0#Text2$haby$0$$pt_BR", "Text2 Paulo Um");
        mockHttpServletRequest.addParameter("ddm$$Name$wqer$0#Text2$makp$1$$en_US", "Text2 Paul Two");
        mockHttpServletRequest.addParameter("ddm$$Name$wqer$0#Text2$makp$1$$pt_BR", "Text2 Paulo Dois");
        mockHttpServletRequest.addParameter("ddm$$Name$fahu$1#Text2$bagj$0$$en_US", "Text2 Joe One");
        mockHttpServletRequest.addParameter("ddm$$Name$fahu$1#Text2$bagj$0$$pt_BR", "Text2 Joao Um");
        DDMFormValues actualDDMFormValues = _ddmFormValuesFactory.create(mockHttpServletRequest, ddmForm);
        assertEquals(expectedDDMFormValues, actualDDMFormValues);
    }

    @Test
    public void testCreateWithRepeatableAndUnlocalizableNestedFields() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm();
        Set<Locale> availableLocales = createAvailableLocales(US);
        Locale defaultLocale = LocaleUtil.US;
        ddmForm.setAvailableLocales(availableLocales);
        ddmForm.setDefaultLocale(defaultLocale);
        DDMFormField nameDDMFormField = DDMFormTestUtil.createTextDDMFormField("Name", false, true, false);
        DDMFormField phoneDDMFormField = DDMFormTestUtil.createTextDDMFormField("Phone", false, true, false);
        DDMFormField extDDMFormField = DDMFormTestUtil.createTextDDMFormField("Ext", false, true, false);
        phoneDDMFormField.addNestedDDMFormField(extDDMFormField);
        nameDDMFormField.addNestedDDMFormField(phoneDDMFormField);
        ddmForm.addDDMFormField(nameDDMFormField);
        DDMFormValues expectedDDMFormValues = createDDMFormValues(ddmForm);
        DDMFormFieldValue paulDDMFormFieldValue = createDDMFormFieldValue("wqer", "Name", new UnlocalizedValue("Paul"));
        DDMFormFieldValue paulPhone1DDMFormFieldValue = createDDMFormFieldValue("gatu", "Phone", new UnlocalizedValue("1"));
        paulPhone1DDMFormFieldValue.addNestedDDMFormFieldValue(createDDMFormFieldValue("jkau", "Ext", new UnlocalizedValue("1.1")));
        paulPhone1DDMFormFieldValue.addNestedDDMFormFieldValue(createDDMFormFieldValue("amat", "Ext", new UnlocalizedValue("1.2")));
        paulDDMFormFieldValue.addNestedDDMFormFieldValue(paulPhone1DDMFormFieldValue);
        DDMFormFieldValue paulPhone2DDMFormFieldValue = createDDMFormFieldValue("hato", "Phone", new UnlocalizedValue("2"));
        paulPhone2DDMFormFieldValue.addNestedDDMFormFieldValue(createDDMFormFieldValue("hamp", "Ext", new UnlocalizedValue("2.1")));
        paulPhone2DDMFormFieldValue.addNestedDDMFormFieldValue(createDDMFormFieldValue("xzal", "Ext", new UnlocalizedValue("2.2")));
        paulPhone2DDMFormFieldValue.addNestedDDMFormFieldValue(createDDMFormFieldValue("kaly", "Ext", new UnlocalizedValue("2.3")));
        paulDDMFormFieldValue.addNestedDDMFormFieldValue(paulPhone2DDMFormFieldValue);
        expectedDDMFormValues.addDDMFormFieldValue(paulDDMFormFieldValue);
        DDMFormFieldValue joeDDMFormFieldValue = createDDMFormFieldValue("fahu", "Name", new UnlocalizedValue("Joe"));
        DDMFormFieldValue joePhone1DDMFormFieldValue = createDDMFormFieldValue("jakl", "Phone", new UnlocalizedValue("3"));
        joePhone1DDMFormFieldValue.addNestedDDMFormFieldValue(createDDMFormFieldValue("bagt", "Ext", new UnlocalizedValue("3.1")));
        joeDDMFormFieldValue.addNestedDDMFormFieldValue(joePhone1DDMFormFieldValue);
        expectedDDMFormValues.addDDMFormFieldValue(joeDDMFormFieldValue);
        MockHttpServletRequest mockHttpServletRequest = createMockHttpServletRequest(defaultLocale, availableLocales);
        // Name
        mockHttpServletRequest.addParameter("ddm$$Name$wqer$0$$en_US", "Paul");
        mockHttpServletRequest.addParameter("ddm$$Name$fahu$1$$en_US", "Joe");
        // Phone
        mockHttpServletRequest.addParameter("ddm$$Name$wqer$0#Phone$gatu$0$$en_US", "1");
        mockHttpServletRequest.addParameter("ddm$$Name$wqer$0#Phone$hato$1$$en_US", "2");
        mockHttpServletRequest.addParameter("ddm$$Name$fahu$1#Phone$jakl$0$$en_US", "3");
        // Ext
        mockHttpServletRequest.addParameter("ddm$$Name$wqer$0#Phone$gatu$0#Ext$jkau$0$$en_US", "1.1");
        mockHttpServletRequest.addParameter("ddm$$Name$wqer$0#Phone$gatu$0#Ext$amat$1$$en_US", "1.2");
        mockHttpServletRequest.addParameter("ddm$$Name$wqer$0#Phone$hato$1#Ext$hamp$0$$en_US", "2.1");
        mockHttpServletRequest.addParameter("ddm$$Name$wqer$0#Phone$hato$1#Ext$xzal$1$$en_US", "2.2");
        mockHttpServletRequest.addParameter("ddm$$Name$wqer$0#Phone$hato$1#Ext$kaly$2$$en_US", "2.3");
        mockHttpServletRequest.addParameter("ddm$$Name$fahu$1#Phone$jakl$0#Ext$bagt$0$$en_US", "3.1");
        DDMFormValues actualDDMFormValues = _ddmFormValuesFactory.create(mockHttpServletRequest, ddmForm);
        assertEquals(expectedDDMFormValues, actualDDMFormValues);
    }

    @Test
    public void testCreateWithRepeatableFieldSetAndNestedCheckbox() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm();
        DDMFormField fieldSetDDMFormField = DDMFormTestUtil.createDDMFormField("fieldset", "FieldSet", "fieldset", "", false, true, false);
        fieldSetDDMFormField.addNestedDDMFormField(DDMFormTestUtil.createDDMFormField("text", "Text", "text", "string", false, false, false));
        DDMFormField checkboxDDMFormField = DDMFormTestUtil.createDDMFormField("checkbox", "Checkbox", "checkbox", "boolean", false, false, false);
        LocalizedValue predefinedValue = checkboxDDMFormField.getPredefinedValue();
        predefinedValue.addString(US, "false");
        fieldSetDDMFormField.addNestedDDMFormField(checkboxDDMFormField);
        ddmForm.addDDMFormField(fieldSetDDMFormField);
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.addParameter("languageId", LocaleUtil.toLanguageId(US));
        // Parameters
        mockHttpServletRequest.addParameter("ddm$$fieldset$amay$0#text$mahy$0$$en_US", "Joe");
        mockHttpServletRequest.addParameter("ddm$$fieldset$amay$0#checkbox$wqer$0$$en_US", "true");
        mockHttpServletRequest.addParameter("ddm$$fieldset$mah7$1#text$kamy$0$$en_US", "Bob");
        DDMFormValues actualDDMFormValues = _ddmFormValuesFactory.create(mockHttpServletRequest, ddmForm);
        List<DDMFormFieldValue> actualDDMFormFieldValues = actualDDMFormValues.getDDMFormFieldValues();
        Assert.assertEquals(actualDDMFormFieldValues.toString(), 2, actualDDMFormFieldValues.size());
        DDMFormFieldValue fieldset1DDMFormFieldValue = actualDDMFormFieldValues.get(0);
        List<DDMFormFieldValue> fieldset1NestedDDMFormFieldValues = fieldset1DDMFormFieldValue.getNestedDDMFormFieldValues();
        assertEquals("Joe", fieldset1NestedDDMFormFieldValues.get(0), US);
        assertEquals("true", fieldset1NestedDDMFormFieldValues.get(1), US);
        DDMFormFieldValue fieldset2DDMFormFieldValue = actualDDMFormFieldValues.get(1);
        List<DDMFormFieldValue> fieldset2NestedDDMFormFieldValues = fieldset2DDMFormFieldValue.getNestedDDMFormFieldValues();
        assertEquals("Bob", fieldset2NestedDDMFormFieldValues.get(0), US);
        assertEquals("false", fieldset2NestedDDMFormFieldValues.get(1), US);
    }

    @Test
    public void testCreateWithRepeatableTransientParent() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm();
        Set<Locale> availableLocales = createAvailableLocales(BRAZIL, US);
        Locale defaultLocale = LocaleUtil.US;
        ddmForm.setAvailableLocales(availableLocales);
        ddmForm.setDefaultLocale(defaultLocale);
        DDMFormField separatorDDMFormField = DDMFormTestUtil.createDDMFormField("Separator", "Separator", "ddm-separator", BLANK, false, true, false);
        DDMFormField nameDDMFormField = DDMFormTestUtil.createTextDDMFormField("Name", true, false, false);
        separatorDDMFormField.addNestedDDMFormField(nameDDMFormField);
        ddmForm.addDDMFormField(separatorDDMFormField);
        DDMFormValues expectedDDMFormValues = createDDMFormValues(ddmForm, availableLocales, defaultLocale);
        DDMFormFieldValue separator1DDMFormFieldValue = createDDMFormFieldValue("wqer", "Separator", null);
        separator1DDMFormFieldValue.addNestedDDMFormFieldValue(createDDMFormFieldValue("gatu", "Name", createLocalizedValue("Joe", "Joao", defaultLocale)));
        expectedDDMFormValues.addDDMFormFieldValue(separator1DDMFormFieldValue);
        DDMFormFieldValue separator2DDMFormFieldValue = createDDMFormFieldValue("haby", "Separator", null);
        separator2DDMFormFieldValue.addNestedDDMFormFieldValue(createDDMFormFieldValue("hato", "Name", createLocalizedValue("Paul", "Paulo", defaultLocale)));
        expectedDDMFormValues.addDDMFormFieldValue(separator2DDMFormFieldValue);
        DDMFormFieldValue separator3DDMFormFieldValue = createDDMFormFieldValue("bajk", "Separator", null);
        separator3DDMFormFieldValue.addNestedDDMFormFieldValue(createDDMFormFieldValue("fahu", "Name", createLocalizedValue("Claude", "Claudio", defaultLocale)));
        expectedDDMFormValues.addDDMFormFieldValue(separator3DDMFormFieldValue);
        MockHttpServletRequest mockHttpServletRequest = createMockHttpServletRequest(defaultLocale, availableLocales);
        // Name
        mockHttpServletRequest.addParameter("ddm$$Separator$wqer$0#Name$gatu$0$$en_US", "Joe");
        mockHttpServletRequest.addParameter("ddm$$Separator$wqer$0#Name$gatu$0$$pt_BR", "Joao");
        mockHttpServletRequest.addParameter("ddm$$Separator$haby$1#Name$hato$0$$en_US", "Paul");
        mockHttpServletRequest.addParameter("ddm$$Separator$haby$1#Name$hato$0$$pt_BR", "Paulo");
        mockHttpServletRequest.addParameter("ddm$$Separator$bajk$2#Name$fahu$0$$en_US", "Claude");
        mockHttpServletRequest.addParameter("ddm$$Separator$bajk$2#Name$fahu$0$$pt_BR", "Claudio");
        DDMFormValues actualDDMFormValues = _ddmFormValuesFactory.create(mockHttpServletRequest, ddmForm);
        assertEquals(expectedDDMFormValues, actualDDMFormValues);
    }

    @Test
    public void testCreateWithTextAndUncheckedCheckboxField() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm();
        ddmForm.addDDMFormField(DDMFormTestUtil.createTextDDMFormField("Name", false, false, false));
        DDMFormField checkboxDDMFormField = DDMFormTestUtil.createDDMFormField("Boolean", "Boolean", "checkbox", "boolean", false, false, false);
        LocalizedValue predefinedValue = checkboxDDMFormField.getPredefinedValue();
        predefinedValue.addString(US, "false");
        ddmForm.addDDMFormField(checkboxDDMFormField);
        DDMFormValues expectedDDMFormValues = createDDMFormValues(ddmForm, createAvailableLocales(US), US);
        expectedDDMFormValues.addDDMFormFieldValue(createDDMFormFieldValue("amay", "Name", new UnlocalizedValue("Joe")));
        expectedDDMFormValues.addDDMFormFieldValue(createDDMFormFieldValue("wqer", "Boolean", new UnlocalizedValue("false")));
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.addParameter("languageId", LocaleUtil.toLanguageId(US));
        // Name
        mockHttpServletRequest.addParameter("ddm$$Name$wqer$0$$en_US", "Joe");
        DDMFormValues actualDDMFormValues = _ddmFormValuesFactory.create(mockHttpServletRequest, ddmForm);
        List<DDMFormFieldValue> actualDDMFormFieldValues = actualDDMFormValues.getDDMFormFieldValues();
        Assert.assertEquals(actualDDMFormFieldValues.toString(), 2, actualDDMFormFieldValues.size());
        assertEquals("Joe", actualDDMFormFieldValues.get(0), US);
        assertEquals("false", actualDDMFormFieldValues.get(1), US);
    }

    @Test
    public void testCreateWithTransientField() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm();
        ddmForm.addDDMFormField(DDMFormTestUtil.createDDMFormField("Paragraph", "Paragraph", "paragraph", BLANK, false, false, false));
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        DDMFormValues ddmFormValues = _ddmFormValuesFactory.create(mockHttpServletRequest, ddmForm);
        List<DDMFormFieldValue> ddmFormFieldValues = ddmFormValues.getDDMFormFieldValues();
        Assert.assertEquals(ddmFormFieldValues.toString(), 1, ddmFormFieldValues.size());
        DDMFormFieldValue ddmFormFieldValue = ddmFormFieldValues.get(0);
        Assert.assertEquals("Paragraph", ddmFormFieldValue.getName());
    }

    @Test
    public void testCreateWithUncheckedCheckboxAndTextFieldWithSimilarNames() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm();
        DDMFormField checkboxDDMFormField = DDMFormTestUtil.createDDMFormField("foo", "Foo", "checkbox", "boolean", false, false, false);
        LocalizedValue predefinedValue = checkboxDDMFormField.getPredefinedValue();
        predefinedValue.addString(US, "false");
        ddmForm.addDDMFormField(checkboxDDMFormField);
        ddmForm.addDDMFormField(DDMFormTestUtil.createTextDDMFormField("fooBar", "Foo Bar", false, false, false));
        DDMFormValues expectedDDMFormValues = createDDMFormValues(ddmForm, createAvailableLocales(US), US);
        expectedDDMFormValues.addDDMFormFieldValue(createDDMFormFieldValue("amay", "foo", new UnlocalizedValue("false")));
        expectedDDMFormValues.addDDMFormFieldValue(createDDMFormFieldValue("wqer", "fooBar", new UnlocalizedValue("Baz")));
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.addParameter("languageId", LocaleUtil.toLanguageId(US));
        // FooBar
        mockHttpServletRequest.addParameter("ddm$$fooBar$wqer$0$$en_US", "Baz");
        DDMFormValues actualDDMFormValues = _ddmFormValuesFactory.create(mockHttpServletRequest, ddmForm);
        List<DDMFormFieldValue> actualDDMFormFieldValues = actualDDMFormValues.getDDMFormFieldValues();
        Assert.assertEquals(actualDDMFormFieldValues.toString(), 2, actualDDMFormFieldValues.size());
        assertEquals("false", actualDDMFormFieldValues.get(0), US);
        assertEquals("Baz", actualDDMFormFieldValues.get(1), US);
    }

    private final DDMFormValuesFactory _ddmFormValuesFactory = new DDMFormValuesFactoryImpl();

    private final DDMFormValuesJSONSerializer _ddmFormValuesJSONSerializer = new DDMFormValuesJSONSerializer();

    @Mock
    private Language _language;

    private Locale _originalSiteDefaultLocale;

    @Mock
    private ServiceTrackerMap<String, DDMFormFieldValueRequestParameterRetriever> _serviceTrackerMap;
}

