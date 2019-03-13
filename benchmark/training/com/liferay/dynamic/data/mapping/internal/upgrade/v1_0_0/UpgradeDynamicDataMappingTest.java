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
package com.liferay.dynamic.data.mapping.internal.upgrade.v1_0_0;


import LocaleUtil.BRAZIL;
import LocaleUtil.US;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesSerializer;
import com.liferay.dynamic.data.mapping.io.internal.DDMFormValuesJSONDeserializer;
import com.liferay.dynamic.data.mapping.io.internal.DDMFormValuesJSONSerializer;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeException;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.util.PropsValues;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;
import org.skyscreamer.jsonassert.JSONAssert;


/**
 *
 *
 * @author Marcellus Tavares
 */
@PowerMockIgnore("javax.xml.stream.*")
@PrepareForTest({ LocaleUtil.class, PropsValues.class })
@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor({ "com.liferay.portal.kernel.xml.SAXReaderUtil", "com.liferay.portal.util.PropsValues" })
public class UpgradeDynamicDataMappingTest extends PowerMockito {
    @Test
    public void testCreateNewFieldNameWithConflictingNewFieldName() throws Exception {
        Set<String> existingFieldNames = new HashSet<>();
        existingFieldNames.add("myna");
        existingFieldNames.add("myna1");
        Assert.assertEquals("myna2", _upgradeDynamicDataMapping.createNewDDMFormFieldName("?my/--na", existingFieldNames));
    }

    @Test
    public void testCreateNewFieldNameWithSupportedOldFieldName() throws Exception {
        Set<String> existingFieldNames = Collections.<String>emptySet();
        Assert.assertEquals("name", _upgradeDynamicDataMapping.createNewDDMFormFieldName("name/?--", existingFieldNames));
        Assert.assertEquals("firstName", _upgradeDynamicDataMapping.createNewDDMFormFieldName("first Name", existingFieldNames));
        Assert.assertEquals("this_is_a_field_name", _upgradeDynamicDataMapping.createNewDDMFormFieldName("this?*&_is///_{{a[[  [_]  ~'field'////>_<name", existingFieldNames));
    }

    @Test(expected = UpgradeException.class)
    public void testCreateNewFieldNameWithUnsupportedOldFieldName() throws Exception {
        Set<String> existingFieldNames = Collections.<String>emptySet();
        _upgradeDynamicDataMapping.createNewDDMFormFieldName("??????", existingFieldNames);
    }

    @Test
    public void testIsInvalidFieldName() {
        Assert.assertTrue(_upgradeDynamicDataMapping.isInvalidFieldName("/name?"));
        Assert.assertTrue(_upgradeDynamicDataMapping.isInvalidFieldName("_name--"));
        Assert.assertTrue(_upgradeDynamicDataMapping.isInvalidFieldName("name^*"));
        Assert.assertTrue(_upgradeDynamicDataMapping.isInvalidFieldName("name^*"));
        Assert.assertTrue(_upgradeDynamicDataMapping.isInvalidFieldName("my name"));
    }

    @Test
    public void testIsValidFieldName() {
        Assert.assertFalse(_upgradeDynamicDataMapping.isInvalidFieldName("name"));
        Assert.assertFalse(_upgradeDynamicDataMapping.isInvalidFieldName("name_"));
        Assert.assertFalse(_upgradeDynamicDataMapping.isInvalidFieldName("???"));
    }

    @Test
    public void testRenameInvalidDDMFormFieldNamesInJSON() throws Exception {
        long structureId = RandomTestUtil.randomLong();
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm();
        DDMFormField ddmFormField = DDMFormTestUtil.createTextDDMFormField("name", false, false, false);
        ddmFormField.setProperty("oldName", "name<!**>");
        ddmForm.addDDMFormField(ddmFormField);
        _upgradeDynamicDataMapping.populateStructureInvalidDDMFormFieldNamesMap(structureId, ddmForm);
        DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(ddmForm);
        ddmFormValues.addDDMFormFieldValue(DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue("name<!**>", "Joe Bloggs"));
        String serializedDDMFormValues = serialize(ddmFormValues);
        String updatedSerializedDDMFormValues = _upgradeDynamicDataMapping.renameInvalidDDMFormFieldNames(structureId, serializedDDMFormValues);
        DDMFormValues updatedDDMFormValues = deserialize(updatedSerializedDDMFormValues, ddmForm);
        List<DDMFormFieldValue> updatedDDMFormFieldValues = updatedDDMFormValues.getDDMFormFieldValues();
        Assert.assertEquals(updatedDDMFormFieldValues.toString(), 1, updatedDDMFormFieldValues.size());
        DDMFormFieldValue updatedDDMFormFieldValue = updatedDDMFormFieldValues.get(0);
        Value value = updatedDDMFormFieldValue.getValue();
        Assert.assertEquals("name", updatedDDMFormFieldValue.getName());
        Assert.assertEquals("Joe Bloggs", value.getString(Locale.US));
    }

    @Test
    public void testRenameInvalidDDMFormFieldNamesInVMTemplate() {
        long structureId = RandomTestUtil.randomLong();
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm();
        DDMFormField ddmFormField = DDMFormTestUtil.createTextDDMFormField("name", false, false, false);
        ddmFormField.setProperty("oldName", "name*");
        ddmForm.addDDMFormField(ddmFormField);
        _upgradeDynamicDataMapping.populateStructureInvalidDDMFormFieldNamesMap(structureId, ddmForm);
        String updatedScript = _upgradeDynamicDataMapping.renameInvalidDDMFormFieldNames(structureId, "Hello $name*!");
        Assert.assertEquals("Hello $name!", updatedScript);
    }

    @Test
    public void testToJSONWithLocalizedAndNestedData() throws Exception {
        DDMForm ddmForm = new DDMForm();
        ddmForm.setAvailableLocales(createAvailableLocales(US));
        ddmForm.setDefaultLocale(US);
        DDMFormField textDDMFormField = new DDMFormField("Text", "text");
        textDDMFormField.setDataType("string");
        textDDMFormField.setLocalizable(true);
        textDDMFormField.setRepeatable(true);
        DDMFormField textAreaDDMFormField = new DDMFormField("TextArea", "textarea");
        textAreaDDMFormField.setDataType("string");
        textAreaDDMFormField.setLocalizable(true);
        textAreaDDMFormField.setRepeatable(true);
        textDDMFormField.addNestedDDMFormField(textAreaDDMFormField);
        ddmForm.addDDMFormField(textDDMFormField);
        // DDM form values
        DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);
        ddmFormValues.setAvailableLocales(createAvailableLocales(BRAZIL, US));
        ddmFormValues.setDefaultLocale(US);
        DDMFormFieldValue text1DDMFormFieldValue = createDDMFormFieldValue("srfa", "Text", createLocalizedValue("En Text Value 1", "Pt Text Value 1", US));
        text1DDMFormFieldValue.addNestedDDMFormFieldValue(createDDMFormFieldValue("elcy", "TextArea", createLocalizedValue("En Text Area Value 1", "Pt Text Area Value 1", US)));
        text1DDMFormFieldValue.addNestedDDMFormFieldValue(createDDMFormFieldValue("uxyj", "TextArea", createLocalizedValue("En Text Area Value 2", "Pt Text Area Value 2", US)));
        ddmFormValues.addDDMFormFieldValue(text1DDMFormFieldValue);
        DDMFormFieldValue text2DDMFormFieldValue = createDDMFormFieldValue("ealq", "Text", createLocalizedValue("En Text Value 2", "Pt Text Value 2", US));
        text2DDMFormFieldValue.addNestedDDMFormFieldValue(createDDMFormFieldValue("eepy", "TextArea", createLocalizedValue("En Text Area Value 3", "Pt Text Area Value 3", US)));
        ddmFormValues.addDDMFormFieldValue(text2DDMFormFieldValue);
        // XML
        Document document = SAXReaderUtil.createDocument();
        Element rootElement = document.addElement("root");
        rootElement.addAttribute("default-locale", "en_US");
        rootElement.addAttribute("available-locales", "en_US,pt_BR");
        addDynamicElementElement(rootElement, "Text", new String[]{ "En Text Value 1", "En Text Value 2" }, new String[]{ "Pt Text Value 1", "Pt Text Value 2" });
        addDynamicElementElement(rootElement, "TextArea", new String[]{ "En Text Area Value 1", "En Text Area Value 2", "En Text Area Value 3" }, new String[]{ "Pt Text Area Value 1", "Pt Text Area Value 2", "Pt Text Area Value 3" });
        addDynamicElementElement(rootElement, "_fieldsDisplay", new String[]{ "Text_INSTANCE_srfa,TextArea_INSTANCE_elcy," + ("TextArea_INSTANCE_uxyj,Text_INSTANCE_ealq," + "TextArea_INSTANCE_eepy") });
        String expectedJSON = serialize(ddmFormValues);
        DDMFormValues actualDDMFormValues = _upgradeDynamicDataMapping.getDDMFormValues(1L, ddmForm, document.asXML());
        String actualJSON = _upgradeDynamicDataMapping.toJSON(actualDDMFormValues);
        JSONAssert.assertEquals(expectedJSON, actualJSON, false);
    }

    @Test
    public void testToJSONWithLocalizedData() throws Exception {
        DDMForm ddmForm = new DDMForm();
        ddmForm.setAvailableLocales(createAvailableLocales(US));
        ddmForm.setDefaultLocale(US);
        DDMFormField textDDMFormField = new DDMFormField("Text", "text");
        textDDMFormField.setDataType("string");
        textDDMFormField.setLocalizable(true);
        textDDMFormField.setRepeatable(true);
        ddmForm.addDDMFormField(textDDMFormField);
        DDMFormField textAreaDDMFormField = new DDMFormField("TextArea", "textarea");
        textAreaDDMFormField.setDataType("string");
        textAreaDDMFormField.setLocalizable(true);
        textAreaDDMFormField.setRepeatable(true);
        ddmForm.addDDMFormField(textAreaDDMFormField);
        DDMFormField integerDDMFormField = new DDMFormField("Integer", "ddm-integer");
        integerDDMFormField.setDataType("integer");
        integerDDMFormField.setLocalizable(false);
        integerDDMFormField.setRepeatable(false);
        ddmForm.addDDMFormField(integerDDMFormField);
        // DDM form values
        DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);
        ddmFormValues.setAvailableLocales(createAvailableLocales(BRAZIL, US));
        ddmFormValues.setDefaultLocale(US);
        ddmFormValues.addDDMFormFieldValue(createDDMFormFieldValue("srfa", "Text", createLocalizedValue("En Text Value 1", "Pt Text Value 1", US)));
        ddmFormValues.addDDMFormFieldValue(createDDMFormFieldValue("ealq", "Text", createLocalizedValue("En Text Value 2", "Pt Text Value 2", US)));
        ddmFormValues.addDDMFormFieldValue(createDDMFormFieldValue("elcy", "TextArea", createLocalizedValue("En Text Area Value 1", "Pt Text Area Value 1", US)));
        ddmFormValues.addDDMFormFieldValue(createDDMFormFieldValue("uxyj", "TextArea", createLocalizedValue("En Text Area Value 2", "Pt Text Area Value 2", US)));
        ddmFormValues.addDDMFormFieldValue(createDDMFormFieldValue("eepy", "TextArea", createLocalizedValue("En Text Area Value 3", "Pt Text Area Value 3", US)));
        ddmFormValues.addDDMFormFieldValue(createDDMFormFieldValue("ckkp", "Integer", new UnlocalizedValue("1")));
        // XML
        Document document = SAXReaderUtil.createDocument();
        Element rootElement = document.addElement("root");
        rootElement.addAttribute("default-locale", "en_US");
        rootElement.addAttribute("available-locales", "en_US,pt_BR");
        addDynamicElementElement(rootElement, "Text", new String[]{ "En Text Value 1", "En Text Value 2" }, new String[]{ "Pt Text Value 1", "Pt Text Value 2" });
        addDynamicElementElement(rootElement, "TextArea", new String[]{ "En Text Area Value 1", "En Text Area Value 2", "En Text Area Value 3" }, new String[]{ "Pt Text Area Value 1", "Pt Text Area Value 2", "Pt Text Area Value 3" });
        addDynamicElementElement(rootElement, "Integer", new String[]{ "1" });
        addDynamicElementElement(rootElement, "_fieldsDisplay", new String[]{ "Text_INSTANCE_srfa,Text_INSTANCE_ealq," + ("TextArea_INSTANCE_elcy,TextArea_INSTANCE_uxyj," + "TextArea_INSTANCE_eepy,Integer_INSTANCE_ckkp") });
        String expectedJSON = serialize(ddmFormValues);
        DDMFormValues actualDDMFormValues = _upgradeDynamicDataMapping.getDDMFormValues(1L, ddmForm, document.asXML());
        String actualJSON = _upgradeDynamicDataMapping.toJSON(actualDDMFormValues);
        JSONAssert.assertEquals(expectedJSON, actualJSON, false);
    }

    @Test
    public void testToJSONWithoutLocalizedData() throws Exception {
        DDMForm ddmForm = new DDMForm();
        ddmForm.setAvailableLocales(createAvailableLocales(US));
        ddmForm.setDefaultLocale(US);
        DDMFormField textDDMFormField = new DDMFormField("Text", "text");
        textDDMFormField.setDataType("string");
        textDDMFormField.setLocalizable(false);
        textDDMFormField.setRepeatable(false);
        ddmForm.addDDMFormField(textDDMFormField);
        DDMFormField textAreaDDMFormField = new DDMFormField("TextArea", "textarea");
        textAreaDDMFormField.setDataType("string");
        textAreaDDMFormField.setLocalizable(false);
        textAreaDDMFormField.setRepeatable(true);
        ddmForm.addDDMFormField(textAreaDDMFormField);
        // DDM form values
        DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);
        ddmFormValues.setAvailableLocales(createAvailableLocales(US));
        ddmFormValues.setDefaultLocale(US);
        ddmFormValues.addDDMFormFieldValue(createDDMFormFieldValue("hcxo", "Text", new UnlocalizedValue("Text Value")));
        ddmFormValues.addDDMFormFieldValue(createDDMFormFieldValue("vfqd", "TextArea", new UnlocalizedValue("Text Area Value 1")));
        ddmFormValues.addDDMFormFieldValue(createDDMFormFieldValue("ycey", "TextArea", new UnlocalizedValue("Text Area Value 2")));
        ddmFormValues.addDDMFormFieldValue(createDDMFormFieldValue("habt", "TextArea", new UnlocalizedValue("Text Area Value 3")));
        // XML
        Document document = SAXReaderUtil.createDocument();
        Element rootElement = document.addElement("root");
        rootElement.addAttribute("default-locale", "en_US");
        rootElement.addAttribute("available-locales", "en_US");
        addDynamicElementElement(rootElement, "Text", new String[]{ "Text Value" });
        addDynamicElementElement(rootElement, "TextArea", new String[]{ "Text Area Value 1", "Text Area Value 2", "Text Area Value 3" });
        addDynamicElementElement(rootElement, "_fieldsDisplay", new String[]{ "Text_INSTANCE_hcxo,TextArea_INSTANCE_vfqd," + "TextArea_INSTANCE_ycey,TextArea_INSTANCE_habt" });
        String expectedJSON = serialize(ddmFormValues);
        DDMFormValues actualDDMFormValues = _upgradeDynamicDataMapping.getDDMFormValues(1L, ddmForm, document.asXML());
        String actualJSON = _upgradeDynamicDataMapping.toJSON(actualDDMFormValues);
        JSONAssert.assertEquals(expectedJSON, actualJSON, false);
    }

    @Test
    public void testToXMLWithoutLocalizedData() throws Exception {
        Map<String, String> expandoValuesMap = new HashMap<>();
        expandoValuesMap.put("Text", createLocalizationXML(new String[]{ "Joe Bloggs" }));
        String fieldsDisplay = "Text_INSTANCE_hcxo";
        expandoValuesMap.put("_fieldsDisplay", createLocalizationXML(new String[]{ fieldsDisplay }));
        String xml = _upgradeDynamicDataMapping.toXML(expandoValuesMap);
        Document document = SAXReaderUtil.read(xml);
        Map<String, Map<String, List<String>>> dataMap = toDataMap(document);
        Map<String, List<String>> actualTextData = dataMap.get("Text");
        assertEquals(ListUtil.toList(new String[]{ "Joe Bloggs" }), actualTextData.get("en_US"));
        Map<String, List<String>> actualFieldsDisplayData = dataMap.get("_fieldsDisplay");
        assertEquals(ListUtil.toList(new String[]{ fieldsDisplay }), actualFieldsDisplayData.get("en_US"));
    }

    @Test
    public void testToXMLWithRepeatableAndLocalizedData() throws Exception {
        Map<String, String> expandoValuesMap = new HashMap<>();
        expandoValuesMap.put("Text", createLocalizationXML(new String[]{ "A", "B", "C" }, new String[]{ "D", "E", "F" }));
        String fieldsDisplay = "Text_INSTANCE_hcxo,Text_INSTANCE_vfqd,Text_INSTANCE_ycey";
        expandoValuesMap.put("_fieldsDisplay", createLocalizationXML(new String[]{ fieldsDisplay }));
        String xml = _upgradeDynamicDataMapping.toXML(expandoValuesMap);
        Document document = SAXReaderUtil.read(xml);
        Map<String, Map<String, List<String>>> dataMap = toDataMap(document);
        Map<String, List<String>> actualTextData = dataMap.get("Text");
        assertEquals(ListUtil.toList(new String[]{ "A", "B", "C" }), actualTextData.get("en_US"));
        assertEquals(ListUtil.toList(new String[]{ "D", "E", "F" }), actualTextData.get("pt_BR"));
        Map<String, List<String>> actualFieldsDisplayData = dataMap.get("_fieldsDisplay");
        assertEquals(ListUtil.toList(new String[]{ fieldsDisplay }), actualFieldsDisplayData.get("en_US"));
    }

    private final DDMFormValuesDeserializer _ddmFormValuesDeserializer = new DDMFormValuesJSONDeserializer();

    private final DDMFormValuesSerializer _ddmFormValuesSerializer = new DDMFormValuesJSONSerializer();

    @Mock
    private Language _language;

    private UpgradeDynamicDataMapping _upgradeDynamicDataMapping;
}

