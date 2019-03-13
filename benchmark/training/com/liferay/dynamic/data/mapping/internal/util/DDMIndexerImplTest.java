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
package com.liferay.dynamic.data.mapping.internal.util;


import com.liferay.dynamic.data.mapping.internal.test.util.DDMFixture;
import com.liferay.dynamic.data.mapping.io.internal.DDMFormJSONSerializer;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.util.DDMIndexer;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.search.test.util.indexing.DocumentFixture;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 *
 *
 * @author Lino Alves
 * @author Andr? de Oliveira
 */
@PrepareOnlyThisForTest({ DDMStructureLocalServiceUtil.class, ResourceBundleUtil.class })
@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor({ "com.liferay.dynamic.data.mapping.model.impl.DDMStructureModelImpl", "com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil" })
public class DDMIndexerImplTest {
    @Test
    public void testFormWithOneAvailableLocaleSameAsDefaultLocale() {
        Locale defaultLocale = LocaleUtil.JAPAN;
        Locale translationLocale = LocaleUtil.JAPAN;
        Set<Locale> availableLocales = Collections.singleton(defaultLocale);
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm(availableLocales, defaultLocale);
        String fieldName = "text1";
        String indexType = "text";
        ddmForm.addDDMFormField(createDDMFormField(fieldName, indexType));
        String fieldValue = "????";
        DDMFormFieldValue ddmFormFieldValue = createDDMFormFieldValue(fieldName, translationLocale, fieldValue, defaultLocale);
        Document document = createDocument();
        DDMStructure ddmStructure = createDDMStructure(ddmForm);
        DDMFormValues ddmFormValues = createDDMFormValues(ddmForm, ddmFormFieldValue);
        ddmIndexer.addAttributes(document, ddmStructure, ddmFormValues);
        Map<String, String> map = DDMIndexerImplTest._withSortableValues(Collections.singletonMap("ddm__text__NNNNN__text1_ja_JP", fieldValue));
        FieldValuesAssert.assertFieldValues(DDMIndexerImplTest._replaceKeys("NNNNN", String.valueOf(ddmStructure.getStructureId()), map), "ddm__text", document, fieldValue);
    }

    @Test
    public void testFormWithTwoAvailableLocalesAndFieldWithNondefaultLocale() {
        Locale defaultLocale = LocaleUtil.US;
        Locale translationLocale = LocaleUtil.JAPAN;
        Set<Locale> availableLocales = new HashSet<>(Arrays.asList(defaultLocale, translationLocale));
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm(availableLocales, defaultLocale);
        String fieldName = "text1";
        String indexType = "text";
        ddmForm.addDDMFormField(createDDMFormField(fieldName, indexType));
        String fieldValue = "????";
        DDMFormFieldValue ddmFormFieldValue = createDDMFormFieldValue(fieldName, translationLocale, fieldValue, defaultLocale);
        Document document = createDocument();
        DDMStructure ddmStructure = createDDMStructure(ddmForm);
        DDMFormValues ddmFormValues = createDDMFormValues(ddmForm, ddmFormFieldValue);
        ddmIndexer.addAttributes(document, ddmStructure, ddmFormValues);
        Map<String, String> map = DDMIndexerImplTest._withSortableValues(Collections.singletonMap("ddm__text__NNNNN__text1_ja_JP", fieldValue));
        FieldValuesAssert.assertFieldValues(DDMIndexerImplTest._replaceKeys("NNNNN", String.valueOf(ddmStructure.getStructureId()), map), "ddm__text", document, fieldValue);
    }

    @Test
    public void testFormWithTwoAvailableLocalesAndFieldWithTwoLocales() {
        Locale defaultLocale = LocaleUtil.JAPAN;
        Locale translationLocale = LocaleUtil.US;
        Set<Locale> availableLocales = new HashSet<>(Arrays.asList(defaultLocale, translationLocale));
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm(availableLocales, defaultLocale);
        String fieldName = "text1";
        String indexType = "text";
        DDMFormField ddmFormField = createDDMFormField(fieldName, indexType);
        ddmForm.addDDMFormField(ddmFormField);
        String fieldValueJP = "????";
        String fieldValueUS = "Create New";
        DDMFormFieldValue ddmFormFieldValueJP = createDDMFormFieldValue(fieldName, defaultLocale, fieldValueJP, defaultLocale);
        DDMFormFieldValue ddmFormFieldValueUS = createDDMFormFieldValue(fieldName, translationLocale, fieldValueUS, defaultLocale);
        Document document = createDocument();
        DDMStructure ddmStructure = createDDMStructure(ddmForm);
        DDMFormValues ddmFormValues = createDDMFormValues(ddmForm, ddmFormFieldValueJP, ddmFormFieldValueUS);
        ddmIndexer.addAttributes(document, ddmStructure, ddmFormValues);
        Map<String, String> map = DDMIndexerImplTest._withSortableValues(new HashMap<String, String>() {
            {
                put("ddm__text__NNNNN__text1_ja_JP", fieldValueJP);
                put("ddm__text__NNNNN__text1_en_US", fieldValueUS);
            }
        });
        FieldValuesAssert.assertFieldValues(DDMIndexerImplTest._replaceKeys("NNNNN", String.valueOf(ddmStructure.getStructureId()), map), "ddm__text", document, fieldValueJP);
    }

    protected final DDMFixture ddmFixture = new DDMFixture();

    protected final DDMFormJSONSerializer ddmFormJSONSerializer = createDDMFormJSONSerializer();

    protected final DDMIndexer ddmIndexer = createDDMIndexer();

    protected final DocumentFixture documentFixture = new DocumentFixture();
}

