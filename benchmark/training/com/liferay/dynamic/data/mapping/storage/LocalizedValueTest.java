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
package com.liferay.dynamic.data.mapping.storage;


import LocaleUtil.BRAZIL;
import LocaleUtil.US;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.portal.kernel.util.LocaleUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Marcellus Tavares
 */
public class LocalizedValueTest {
    @Test
    public void testEqualsWithDifferentDefaultLocaleAndSameValuesMap() {
        Value value1 = new com.liferay.dynamic.data.mapping.model.LocalizedValue(LocaleUtil.US);
        Value value2 = new com.liferay.dynamic.data.mapping.model.LocalizedValue(LocaleUtil.BRAZIL);
        Assert.assertNotEquals(value1, value2);
    }

    @Test
    public void testEqualsWithSameDefaultLocaleAndDifferentValuesMap() {
        Value value1 = new com.liferay.dynamic.data.mapping.model.LocalizedValue(LocaleUtil.US);
        value1.addString(US, "Test");
        value1.addString(BRAZIL, "Teste");
        Value value2 = new com.liferay.dynamic.data.mapping.model.LocalizedValue(LocaleUtil.US);
        value2.addString(US, "Different Test");
        value2.addString(BRAZIL, "Teste");
        Assert.assertNotEquals(value1, value2);
    }

    @Test
    public void testEqualsWithSameDefaultLocaleAndSameValuesMap() {
        Value value1 = new com.liferay.dynamic.data.mapping.model.LocalizedValue(LocaleUtil.US);
        value1.addString(US, "Test");
        value1.addString(BRAZIL, "Teste");
        Value value2 = new com.liferay.dynamic.data.mapping.model.LocalizedValue(LocaleUtil.US);
        value2.addString(US, "Test");
        value2.addString(BRAZIL, "Teste");
        Assert.assertEquals(value1, value2);
    }
}

