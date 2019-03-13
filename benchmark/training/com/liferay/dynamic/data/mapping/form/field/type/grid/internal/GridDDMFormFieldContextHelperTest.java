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
package com.liferay.dynamic.data.mapping.form.field.type.grid.internal;


import LocaleUtil.US;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Pedro Queiroz
 */
public class GridDDMFormFieldContextHelperTest {
    @Test
    public void testGetOptions() {
        List<Object> expectedOptions = new ArrayList<>();
        expectedOptions.add(createOption("Label 1", "value 1"));
        expectedOptions.add(createOption("Label 2", "value 2"));
        expectedOptions.add(createOption("Label 3", "value 3"));
        DDMFormFieldOptions ddmFormFieldOptions = createDDMFormFieldOptions();
        List<Object> actualOptions = getActualOptions(ddmFormFieldOptions, US);
        Assert.assertEquals(expectedOptions, actualOptions);
    }
}

