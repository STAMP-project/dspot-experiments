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
package com.liferay.dynamic.data.mapping.form.web.internal.portlet.action;


import LocaleUtil.US;
import com.liferay.dynamic.data.mapping.form.evaluator.DDMFormEvaluator;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import java.util.HashMap;
import java.util.Map;
import javax.portlet.ActionRequest;
import javax.servlet.http.HttpServletRequest;
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
 * @author Leonardo Barros
 */
@PrepareForTest(ResourceBundleUtil.class)
@RunWith(PowerMockRunner.class)
public class AddFormInstanceRecordMVCCommandHelperTest extends PowerMockito {
    @Test
    public void testNotRequiredAndInvisibleField() throws Exception {
        Map<String, Object> changedProperties = new HashMap<>();
        changedProperties.put("visible", false);
        mockDDMFormEvaluator(changedProperties);
        _ddmFormField.setRequired(false);
        _addRecordMVCCommandHelper.updateRequiredFieldsAccordingToVisibility(_actionRequest, _ddmForm, _ddmFormValues, US);
        Assert.assertFalse(_ddmFormField.isRequired());
    }

    @Test
    public void testNotRequiredAndVisibleField() throws Exception {
        Map<String, Object> changedProperties = new HashMap<>();
        changedProperties.put("visible", true);
        mockDDMFormEvaluator(changedProperties);
        _ddmFormField.setRequired(false);
        _addRecordMVCCommandHelper.updateRequiredFieldsAccordingToVisibility(_actionRequest, _ddmForm, _ddmFormValues, US);
        Assert.assertFalse(_ddmFormField.isRequired());
    }

    @Test
    public void testRequiredAndInvisibleField() throws Exception {
        Map<String, Object> changedProperties = new HashMap<>();
        changedProperties.put("visible", false);
        mockDDMFormEvaluator(changedProperties);
        mockGetDDMFormLayout();
        _addRecordMVCCommandHelper.updateRequiredFieldsAccordingToVisibility(_actionRequest, _ddmForm, _ddmFormValues, US);
        Assert.assertFalse(_ddmFormField.isRequired());
    }

    @Test
    public void testRequiredAndVisibleField() throws Exception {
        Map<String, Object> changedProperties = new HashMap<>();
        changedProperties.put("visible", true);
        mockDDMFormEvaluator(changedProperties);
        mockGetDDMFormLayout();
        _addRecordMVCCommandHelper.updateRequiredFieldsAccordingToVisibility(_actionRequest, _ddmForm, _ddmFormValues, US);
        Assert.assertTrue(_ddmFormField.isRequired());
    }

    @Mock
    private ActionRequest _actionRequest;

    private AddFormInstanceRecordMVCCommandHelper _addRecordMVCCommandHelper;

    private DDMForm _ddmForm;

    @Mock
    private DDMFormEvaluator _ddmFormEvaluator;

    private DDMFormField _ddmFormField;

    @Mock
    private DDMFormInstanceService _ddmFormInstanceService;

    private DDMFormValues _ddmFormValues;

    @Mock
    private DDMStructureLocalService _ddmStructureLocalService;

    @Mock
    private Portal _portal;

    @Mock
    private HttpServletRequest _request;
}

