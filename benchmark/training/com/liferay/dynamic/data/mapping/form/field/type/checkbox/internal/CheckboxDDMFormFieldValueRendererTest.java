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
package com.liferay.dynamic.data.mapping.form.field.type.checkbox.internal;


import LocaleUtil.US;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 *
 *
 * @author Renato Rego
 */
@PrepareForTest(LanguageUtil.class)
@RunWith(PowerMockRunner.class)
public class CheckboxDDMFormFieldValueRendererTest extends PowerMockito {
    @Test
    public void testRender() throws Exception {
        DDMFormFieldValue ddmFormFieldValue = DDMFormValuesTestUtil.createDDMFormFieldValue("Checkbox", new UnlocalizedValue("true"));
        CheckboxDDMFormFieldValueRenderer checkboxDDMFormFieldValueRenderer = createCheckboxDDMFormFieldValueRenderer();
        String expectedCheckboxRenderedValue = LanguageUtil.get(US, "yes");
        Assert.assertEquals(expectedCheckboxRenderedValue, checkboxDDMFormFieldValueRenderer.render(ddmFormFieldValue, US));
        ddmFormFieldValue.setValue(new UnlocalizedValue("false"));
        expectedCheckboxRenderedValue = LanguageUtil.get(US, "no");
        Assert.assertEquals(expectedCheckboxRenderedValue, checkboxDDMFormFieldValueRenderer.render(ddmFormFieldValue, US));
    }

    @Mock
    protected Language language;
}

