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
package com.liferay.dynamic.data.mapping.form.field.type;


import LocaleUtil.US;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.portal.kernel.util.Html;
import com.liferay.portal.kernel.util.StringUtil;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 *
 *
 * @author Marcellus Tavares
 */
public class DefaultDDMFormFieldValueRendererTest {
    @Test
    public void testRender() {
        DDMFormFieldValue ddmFormFieldValue = new DDMFormFieldValue();
        ddmFormFieldValue.setName("Text");
        ddmFormFieldValue.setValue(new com.liferay.dynamic.data.mapping.model.UnlocalizedValue(StringUtil.randomString()));
        _defaultDDMFormFieldValueRenderer.render(ddmFormFieldValue, US);
        Mockito.verify(_html).escape(Matchers.anyString());
    }

    private final DefaultDDMFormFieldValueRenderer _defaultDDMFormFieldValueRenderer = new DefaultDDMFormFieldValueRenderer();

    @Mock
    private Html _html;
}

