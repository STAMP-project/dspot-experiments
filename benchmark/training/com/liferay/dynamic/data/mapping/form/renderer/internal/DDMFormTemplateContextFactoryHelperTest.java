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
package com.liferay.dynamic.data.mapping.form.renderer.internal;


import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldValidation;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.portal.kernel.util.SetUtil;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 *
 *
 * @author Rafael Praxedes
 */
@RunWith(PowerMockRunner.class)
public class DDMFormTemplateContextFactoryHelperTest extends PowerMockito {
    @Test
    public void testGetEvaluableFieldNames() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm();
        ddmForm.addDDMFormField(DDMFormTestUtil.createTextDDMFormField("Field0", false, false, false));
        ddmForm.addDDMFormField(DDMFormTestUtil.createTextDDMFormField("Field1", false, false, false));
        ddmForm.addDDMFormField(DDMFormTestUtil.createTextDDMFormField("Field2", false, false, true));
        DDMFormField ddmFormField3 = DDMFormTestUtil.createTextDDMFormField("Field3", false, false, false);
        ddmFormField3.setVisibilityExpression("equals(Field0, 'Joe')");
        ddmForm.addDDMFormField(ddmFormField3);
        DDMFormField ddmFormField4 = DDMFormTestUtil.createTextDDMFormField("Field4", false, false, false);
        DDMFormFieldValidation ddmFormFieldValidation = new DDMFormFieldValidation();
        ddmFormFieldValidation.setExpression("isEmailAddress(Field4)");
        ddmFormField4.setDDMFormFieldValidation(ddmFormFieldValidation);
        ddmForm.addDDMFormField(ddmFormField4);
        Set<String> expectedEvaluableFieldNames = SetUtil.fromArray(new String[]{ "Field0", "Field2", "Field4" });
        Set<String> actualEvaluableFieldNames = DDMFormTemplateContextFactoryHelperTest._ddmFormTemplateContextFactoryHelper.getEvaluableDDMFormFieldNames(ddmForm);
        Assert.assertEquals(expectedEvaluableFieldNames, actualEvaluableFieldNames);
    }

    private static final String _DATA_PROVIDER_INSTANCE_UUID = "ea3464d6-71e2-5202-964a-f53d6cc0ee39";

    private static DDMFormTemplateContextFactoryHelper _ddmFormTemplateContextFactoryHelper;
}

