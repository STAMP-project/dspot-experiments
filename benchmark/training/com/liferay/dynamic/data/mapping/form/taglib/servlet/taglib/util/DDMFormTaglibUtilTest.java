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
package com.liferay.dynamic.data.mapping.form.taglib.servlet.taglib.util;


import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureVersionLocalService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 *
 *
 * @author Pedro Queiroz
 */
@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor({ "com.liferay.dynamic.data.mapping.model.impl.DDMStructureModelImpl", "com.liferay.dynamic.data.mapping.model.impl.DDMStructureVersionModelImpl" })
public class DDMFormTaglibUtilTest {
    @Test
    public void testGetDDMFormFromDDMStructure() {
        DDMForm ddmForm = _ddmStructure.getDDMForm();
        Assert.assertTrue(ddmForm.equals(_ddmFormTaglibUtil.getDDMForm(_ddmStructure.getStructureId(), 0)));
    }

    @Test
    public void testGetDDMFormFromDDMStructureVersion1() {
        DDMForm ddmForm = _ddmStructureVersion.getDDMForm();
        Assert.assertTrue(ddmForm.equals(_ddmFormTaglibUtil.getDDMForm(0, _ddmStructureVersion.getStructureId())));
    }

    @Test
    public void testGetDDMFormFromDDMStructureVersion2() {
        DDMForm ddmForm = _ddmStructureVersion.getDDMForm();
        Assert.assertTrue(ddmForm.equals(_ddmFormTaglibUtil.getDDMForm(_ddmStructure.getStructureId(), _ddmStructureVersion.getStructureId())));
    }

    @Test
    public void testGetEmptyDDMFormTest() {
        Assert.assertEquals(new DDMForm(), _ddmFormTaglibUtil.getDDMForm(0, 0));
    }

    private final DDMFormTaglibUtil _ddmFormTaglibUtil = new DDMFormTaglibUtil();

    private DDMStructure _ddmStructure;

    @Mock
    private DDMStructureLocalService _ddmStructureLocalService;

    private DDMStructureVersion _ddmStructureVersion;

    @Mock
    private DDMStructureVersionLocalService _ddmStructureVersionLocalService;
}

