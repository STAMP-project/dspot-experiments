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
package com.liferay.dynamic.data.mapping.internal.notification;


import StringPool.BLANK;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeServicesTracker;
import com.liferay.dynamic.data.mapping.form.field.type.DefaultDDMFormFieldValueRenderer;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.portal.template.soy.util.SoyRawData;
import java.util.Locale;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 *
 *
 * @author Rafael Praxedes
 */
@RunWith(PowerMockRunner.class)
public class DDMFormEmailNotificationSenderTest {
    @Test
    public void testGetField() {
        DDMFormValues ddmFormValues = createDDMFormValues(new UnlocalizedValue("test"));
        Map<String, Object> fieldLabelValueMap = _ddmFormEmailNotificationSender.getField(ddmFormValues.getDDMFormFieldValues(), Locale.US);
        Assert.assertEquals(fieldLabelValueMap.toString(), 2, fieldLabelValueMap.size());
        Assert.assertTrue(fieldLabelValueMap.containsKey("label"));
        Assert.assertTrue(fieldLabelValueMap.containsKey("value"));
        Assert.assertNull(fieldLabelValueMap.get("label"));
        SoyRawData soyRawData = ((SoyRawData) (fieldLabelValueMap.get("value")));
        Assert.assertEquals("test", String.valueOf(soyRawData.getValue()));
    }

    @Test
    public void testGetFieldWithNullValue() {
        DDMFormValues ddmFormValues = createDDMFormValues(null);
        Map<String, Object> fieldLabelValueMap = _ddmFormEmailNotificationSender.getField(ddmFormValues.getDDMFormFieldValues(), Locale.US);
        Assert.assertEquals(fieldLabelValueMap.toString(), 2, fieldLabelValueMap.size());
        Assert.assertTrue(fieldLabelValueMap.containsKey("label"));
        Assert.assertTrue(fieldLabelValueMap.containsKey("value"));
        Assert.assertNull(fieldLabelValueMap.get("label"));
        SoyRawData soyRawData = ((SoyRawData) (fieldLabelValueMap.get("value")));
        Assert.assertEquals(BLANK, String.valueOf(soyRawData.getValue()));
    }

    private DDMFormEmailNotificationSender _ddmFormEmailNotificationSender;

    @Mock
    private DDMFormFieldTypeServicesTracker _ddmFormFieldTypeServicesTracker;

    private final DefaultDDMFormFieldValueRenderer _defaultDDMFormFieldValueRenderer = new DefaultDDMFormFieldValueRenderer();
}

