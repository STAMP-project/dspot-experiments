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
package com.liferay.dynamic.data.mapping.form.field.type.checkbox.multiple.internal;


import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactory;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Marcela Cunha
 */
public class CheckboxMultipleDDMFormFieldValueRequestParameterRetrieverTest {
    @Test
    public void testCompletedSubmission() {
        String expectedResult = createJSONArrayString("Option 2");
        String defaultDDMFormFieldParameterValue = createJSONArrayString("Option 1");
        String actualResult = _checkboxMultipleDDMFormFieldValueRequestParameterRetriever.get(createHttpServletRequest("Option 2"), CheckboxMultipleDDMFormFieldValueRequestParameterRetrieverTest._CHECKBOX_MULTIPLE_SUBMISSION, defaultDDMFormFieldParameterValue);
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testEmptySubmission() {
        String expectedResult = "[]";
        String defaultDDMFormFieldParameterValue = createJSONArrayString("Option 1");
        String actualResult = _checkboxMultipleDDMFormFieldValueRequestParameterRetriever.get(createHttpServletRequest(), CheckboxMultipleDDMFormFieldValueRequestParameterRetrieverTest._CHECKBOX_MULTIPLE_SUBMISSION, defaultDDMFormFieldParameterValue);
        Assert.assertEquals(expectedResult, actualResult);
    }

    private static final String _CHECKBOX_MULTIPLE_SUBMISSION = "checkBoxSubmissionResult";

    private CheckboxMultipleDDMFormFieldValueRequestParameterRetriever _checkboxMultipleDDMFormFieldValueRequestParameterRetriever;

    private final JSONFactory _jsonFactory = new JSONFactoryImpl();
}

