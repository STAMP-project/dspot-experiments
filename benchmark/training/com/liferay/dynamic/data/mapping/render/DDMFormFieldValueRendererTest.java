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
package com.liferay.dynamic.data.mapping.render;


import LocaleUtil.BRAZIL;
import LocaleUtil.SPAIN;
import LocaleUtil.US;
import StringPool.BLANK;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.dynamic.data.mapping.BaseDDMTestCase;
import com.liferay.dynamic.data.mapping.internal.render.CheckboxDDMFormFieldValueRenderer;
import com.liferay.dynamic.data.mapping.internal.render.DateDDMFormFieldValueRenderer;
import com.liferay.dynamic.data.mapping.internal.render.DecimalDDMFormFieldValueRenderer;
import com.liferay.dynamic.data.mapping.internal.render.DocumentLibraryDDMFormFieldValueRenderer;
import com.liferay.dynamic.data.mapping.internal.render.GeolocationDDMFormFieldValueRenderer;
import com.liferay.dynamic.data.mapping.internal.render.IntegerDDMFormFieldValueRenderer;
import com.liferay.dynamic.data.mapping.internal.render.LinkToPageDDMFormFieldValueRenderer;
import com.liferay.dynamic.data.mapping.internal.render.SelectDDMFormFieldValueRenderer;
import com.liferay.dynamic.data.mapping.internal.render.TextDDMFormFieldValueRenderer;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.service.LayoutServiceUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.JavaDetector;
import org.junit.Assert;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;


/**
 *
 *
 * @author Marcellus Tavares
 */
@PrepareForTest({ DLAppLocalServiceUtil.class, LayoutServiceUtil.class })
public class DDMFormFieldValueRendererTest extends BaseDDMTestCase {
    @Test
    public void testCheckboxFieldValueRendererWithoutRepeatableValues() {
        DDMFormFieldValue ddmFormFieldValue = createDDMFormFieldValue("Checkbox", createLocalizedValue("false", "true", US));
        DDMFormFieldValueRenderer ddmFormFieldValueRenderer = new CheckboxDDMFormFieldValueRenderer();
        String renderedValue = ddmFormFieldValueRenderer.render(ddmFormFieldValue, US);
        Assert.assertEquals("No", renderedValue);
        renderedValue = ddmFormFieldValueRenderer.render(ddmFormFieldValue, BRAZIL);
        Assert.assertEquals("Sim", renderedValue);
    }

    @Test
    public void testCheckboxFieldValueRendererWithRepeatableValues() {
        DDMFormValues ddmFormValues = new DDMFormValues(null);
        ddmFormValues.addDDMFormFieldValue(createDDMFormFieldValue("Checkbox", createLocalizedValue("false", "true", US)));
        ddmFormValues.addDDMFormFieldValue(createDDMFormFieldValue("Checkbox", createLocalizedValue("true", "true", US)));
        DDMFormFieldValueRenderer ddmFormFieldValueRenderer = new CheckboxDDMFormFieldValueRenderer();
        String renderedValue = ddmFormFieldValueRenderer.render(ddmFormValues.getDDMFormFieldValues(), US);
        Assert.assertEquals("No, Yes", renderedValue);
        renderedValue = ddmFormFieldValueRenderer.render(ddmFormValues.getDDMFormFieldValues(), BRAZIL);
        Assert.assertEquals("Sim, Sim", renderedValue);
    }

    @Test
    public void testDateFieldValueRenderer() {
        String valueString = "2014-10-22";
        DDMFormFieldValue ddmFormFieldValue = createDDMFormFieldValue("Date", new UnlocalizedValue(valueString));
        DDMFormFieldValueRenderer ddmFormFieldValueRenderer = new DateDDMFormFieldValueRenderer();
        String renderedValue = ddmFormFieldValueRenderer.render(ddmFormFieldValue, US);
        Assert.assertEquals("10/22/14", renderedValue);
        renderedValue = ddmFormFieldValueRenderer.render(ddmFormFieldValue, BRAZIL);
        Assert.assertEquals("22/10/14", renderedValue);
    }

    @Test
    public void testDateFieldValueRendererWithEmptyValue() {
        DDMFormFieldValue ddmFormFieldValue = createDDMFormFieldValue("Date", new UnlocalizedValue(StringPool.BLANK));
        DDMFormFieldValueRenderer ddmFormFieldValueRenderer = new DateDDMFormFieldValueRenderer();
        String renderedValue = ddmFormFieldValueRenderer.render(ddmFormFieldValue, US);
        Assert.assertEquals(BLANK, renderedValue);
    }

    @Test
    public void testDecimalFieldValueRenderer() {
        DDMFormFieldValue ddmFormFieldValue = createDDMFormFieldValue("Decimal", createLocalizedValue("1.2", "1.2", US));
        DDMFormFieldValueRenderer ddmFormFieldValueRenderer = new DecimalDDMFormFieldValueRenderer();
        String renderedValue = ddmFormFieldValueRenderer.render(ddmFormFieldValue, US);
        Assert.assertEquals("1.2", renderedValue);
        renderedValue = ddmFormFieldValueRenderer.render(ddmFormFieldValue, BRAZIL);
        Assert.assertEquals("1,2", renderedValue);
    }

    @Test
    public void testDecimalFieldValueRendererWithoutDefaultValue() {
        DDMFormFieldValue ddmFormFieldValue = createDDMFormFieldValue("Decimal", createLocalizedValue(BLANK, "1,2", US));
        DDMFormFieldValueRenderer ddmFormFieldValueRenderer = new DecimalDDMFormFieldValueRenderer();
        String renderedValue = ddmFormFieldValueRenderer.render(ddmFormFieldValue, US);
        Assert.assertEquals("0", renderedValue);
        renderedValue = ddmFormFieldValueRenderer.render(ddmFormFieldValue, BRAZIL);
        Assert.assertEquals("1,2", renderedValue);
    }

    @Test
    public void testDocumentLibraryFieldValueRenderer() {
        JSONObject jsonObject = JSONFactoryUtil.createJSONObject();
        jsonObject.put("groupId", RandomTestUtil.randomLong());
        jsonObject.put("uuid", RandomTestUtil.randomString());
        DDMFormFieldValue ddmFormFieldValue = createDDMFormFieldValue("DocumentLibrary", new UnlocalizedValue(jsonObject.toString()));
        DDMFormFieldValueRenderer ddmFormFieldValueRenderer = new DocumentLibraryDDMFormFieldValueRenderer();
        String renderedValue = ddmFormFieldValueRenderer.render(ddmFormFieldValue, US);
        Assert.assertEquals("File Entry Title", renderedValue);
    }

    @Test
    public void testGeolocationFieldValueRenderer() {
        JSONObject jsonObject = JSONFactoryUtil.createJSONObject();
        jsonObject.put("latitude", 9.8765);
        jsonObject.put("longitude", 1.2345);
        DDMFormFieldValue ddmFormFieldValue = createDDMFormFieldValue("Geolocation", new UnlocalizedValue(jsonObject.toString()));
        DDMFormFieldValueRenderer ddmFormFieldValueRenderer = new GeolocationDDMFormFieldValueRenderer();
        String renderedValue = ddmFormFieldValueRenderer.render(ddmFormFieldValue, SPAIN);
        if (JavaDetector.isJDK8()) {
            Assert.assertEquals("Latitud: 9,877, Longitud: 1,234", renderedValue);
        } else {
            Assert.assertEquals("Latitud: 9,876, Longitud: 1,234", renderedValue);
        }
        renderedValue = ddmFormFieldValueRenderer.render(ddmFormFieldValue, US);
        if (JavaDetector.isJDK8()) {
            Assert.assertEquals("Latitude: 9.877, Longitude: 1.234", renderedValue);
        } else {
            Assert.assertEquals("Latitude: 9.876, Longitude: 1.234", renderedValue);
        }
    }

    @Test
    public void testIntegerFieldValueRenderer() {
        DDMFormFieldValue ddmFormFieldValue = createDDMFormFieldValue("Integer", createLocalizedValue("1", "2", US));
        DDMFormFieldValueRenderer ddmFormFieldValueRenderer = new IntegerDDMFormFieldValueRenderer();
        String renderedValue = ddmFormFieldValueRenderer.render(ddmFormFieldValue, US);
        Assert.assertEquals("1", renderedValue);
        renderedValue = ddmFormFieldValueRenderer.render(ddmFormFieldValue, BRAZIL);
        Assert.assertEquals("2", renderedValue);
    }

    @Test
    public void testLinkToPageFieldValueRenderer() {
        JSONObject jsonObject = JSONFactoryUtil.createJSONObject();
        jsonObject.put("groupId", RandomTestUtil.randomLong());
        jsonObject.put("layoutId", RandomTestUtil.randomLong());
        jsonObject.put("privateLayout", RandomTestUtil.randomBoolean());
        DDMFormFieldValue ddmFormFieldValue = createDDMFormFieldValue("LinkToPage", new UnlocalizedValue(jsonObject.toString()));
        DDMFormFieldValueRenderer ddmFormFieldValueRenderer = new LinkToPageDDMFormFieldValueRenderer();
        String renderedValue = ddmFormFieldValueRenderer.render(ddmFormFieldValue, US);
        Assert.assertEquals("Layout Name", renderedValue);
        renderedValue = ddmFormFieldValueRenderer.render(ddmFormFieldValue, BRAZIL);
        Assert.assertEquals("Nome da Pagina", renderedValue);
    }

    @Test
    public void testNumberFieldValueRenderer() {
        DDMFormFieldValue ddmFormFieldValue = createDDMFormFieldValue("Number", createLocalizedValue("1", "2.1", US));
        DDMFormFieldValueRenderer ddmFormFieldValueRenderer = new IntegerDDMFormFieldValueRenderer();
        String renderedValue = ddmFormFieldValueRenderer.render(ddmFormFieldValue, US);
        Assert.assertEquals("1", renderedValue);
        renderedValue = ddmFormFieldValueRenderer.render(ddmFormFieldValue, BRAZIL);
        Assert.assertEquals("2,1", renderedValue);
    }

    @Test(expected = ValueAccessorException.class)
    public void testSelectFieldValueRendererWithInvalidValue() throws Exception {
        DDMForm ddmForm = createDDMFormWithSelectField();
        DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);
        ddmFormValues.addDDMFormFieldValue(createDDMFormFieldValue("Select", new UnlocalizedValue("Invalid JSON")));
        DDMFormFieldValueRenderer ddmFormFieldValueRenderer = new SelectDDMFormFieldValueRenderer();
        ddmFormFieldValueRenderer.render(ddmFormValues.getDDMFormFieldValues(), US);
    }

    @Test
    public void testSelectFieldValueRendererWithoutRepeatableValues() {
        DDMForm ddmForm = createDDMFormWithSelectField();
        DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);
        JSONArray jsonArray = toJSONArray("Option 1", "Option 2");
        ddmFormValues.addDDMFormFieldValue(createDDMFormFieldValue("Select", new UnlocalizedValue(jsonArray.toString())));
        DDMFormFieldValueRenderer ddmFormFieldValueRenderer = new SelectDDMFormFieldValueRenderer();
        String renderedValue = ddmFormFieldValueRenderer.render(ddmFormValues.getDDMFormFieldValues(), US);
        Assert.assertEquals("English Label 1, English Label 2", renderedValue);
        renderedValue = ddmFormFieldValueRenderer.render(ddmFormValues.getDDMFormFieldValues(), BRAZIL);
        Assert.assertEquals("Portuguese Label 1, Portuguese Label 2", renderedValue);
    }

    @Test
    public void testSelectFieldValueRendererWithRepeatableValues() {
        DDMForm ddmForm = createDDMFormWithSelectField();
        DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);
        JSONArray jsonArray = toJSONArray("Option 1", "Option 2");
        ddmFormValues.addDDMFormFieldValue(createDDMFormFieldValue("Select", new UnlocalizedValue(jsonArray.toString())));
        jsonArray = toJSONArray("Option 1");
        ddmFormValues.addDDMFormFieldValue(createDDMFormFieldValue("Select", new UnlocalizedValue(jsonArray.toString())));
        DDMFormFieldValueRenderer ddmFormFieldValueRenderer = new SelectDDMFormFieldValueRenderer();
        String renderedValue = ddmFormFieldValueRenderer.render(ddmFormValues.getDDMFormFieldValues(), US);
        Assert.assertEquals("English Label 1, English Label 2, English Label 1", renderedValue);
        renderedValue = ddmFormFieldValueRenderer.render(ddmFormValues.getDDMFormFieldValues(), BRAZIL);
        Assert.assertEquals("Portuguese Label 1, Portuguese Label 2, Portuguese Label 1", renderedValue);
    }

    @Test
    public void testTextFieldValueRendererWithoutRepeatableValues() {
        DDMFormFieldValue ddmFormFieldValue = createDDMFormFieldValue("Text", new UnlocalizedValue("Scott Joplin"));
        DDMFormFieldValueRenderer ddmFormFieldValueRenderer = new TextDDMFormFieldValueRenderer();
        String renderedValue = ddmFormFieldValueRenderer.render(ddmFormFieldValue, US);
        Assert.assertEquals("Scott Joplin", renderedValue);
    }

    @Test
    public void testTextFieldValueRendererWithRepeatableValues() {
        DDMFormValues ddmFormValues = new DDMFormValues(null);
        ddmFormValues.addDDMFormFieldValue(createDDMFormFieldValue("Text", new UnlocalizedValue("Charlie Parker")));
        ddmFormValues.addDDMFormFieldValue(createDDMFormFieldValue("Text", new UnlocalizedValue("Dave Brubeck")));
        DDMFormFieldValueRenderer ddmFormFieldValueRenderer = new TextDDMFormFieldValueRenderer();
        String renderedValue = ddmFormFieldValueRenderer.render(ddmFormValues.getDDMFormFieldValues(), US);
        Assert.assertEquals("Charlie Parker, Dave Brubeck", renderedValue);
    }
}

