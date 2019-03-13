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
package com.liferay.dynamic.data.mapping.validator.internal;


import LocaleUtil.BRAZIL;
import LocaleUtil.US;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldType;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldValidation;
import com.liferay.dynamic.data.mapping.model.DDMFormRule;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.validator.DDMFormValidationException.MustNotDuplicateFieldName;
import com.liferay.dynamic.data.mapping.validator.DDMFormValidationException.MustSetAvailableLocales;
import com.liferay.dynamic.data.mapping.validator.DDMFormValidationException.MustSetDefaultLocale;
import com.liferay.dynamic.data.mapping.validator.DDMFormValidationException.MustSetDefaultLocaleAsAvailableLocale;
import com.liferay.dynamic.data.mapping.validator.DDMFormValidationException.MustSetFieldsForForm;
import com.liferay.dynamic.data.mapping.validator.DDMFormValidationException.MustSetOptionsForField;
import com.liferay.dynamic.data.mapping.validator.DDMFormValidationException.MustSetValidAvailableLocalesForProperty;
import com.liferay.dynamic.data.mapping.validator.DDMFormValidationException.MustSetValidCharactersForFieldName;
import com.liferay.dynamic.data.mapping.validator.DDMFormValidationException.MustSetValidCharactersForFieldType;
import com.liferay.dynamic.data.mapping.validator.DDMFormValidationException.MustSetValidDefaultLocaleForProperty;
import com.liferay.dynamic.data.mapping.validator.DDMFormValidationException.MustSetValidFormRuleExpression;
import com.liferay.dynamic.data.mapping.validator.DDMFormValidationException.MustSetValidIndexType;
import com.liferay.dynamic.data.mapping.validator.DDMFormValidationException.MustSetValidValidationExpression;
import com.liferay.dynamic.data.mapping.validator.DDMFormValidationException.MustSetValidVisibilityExpression;
import com.liferay.portal.kernel.util.StringUtil;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Marcellus Tavares
 */
public class DDMFormValidatorTest {
    @Test(expected = MustSetValidCharactersForFieldType.class)
    public void testCaretInFieldType() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm(createAvailableLocales(US), US);
        DDMFormField ddmFormField = new DDMFormField("Name", "html-text_@");
        ddmForm.addDDMFormField(ddmFormField);
        _ddmFormValidatorImpl.validate(ddmForm);
    }

    @Test(expected = MustSetValidCharactersForFieldName.class)
    public void testDashInFieldName() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm(createAvailableLocales(US), US);
        ddmForm.addDDMFormField(new DDMFormField("text-dash", DDMFormFieldType.TEXT));
        _ddmFormValidatorImpl.validate(ddmForm);
    }

    @Test(expected = MustSetDefaultLocaleAsAvailableLocale.class)
    public void testDefaultLocaleMissingAsAvailableLocale() throws Exception {
        DDMForm ddmForm = new DDMForm();
        ddmForm.setAvailableLocales(createAvailableLocales(BRAZIL));
        ddmForm.setDefaultLocale(US);
        _ddmFormValidatorImpl.validate(ddmForm);
    }

    @Test(expected = MustSetValidCharactersForFieldName.class)
    public void testDollarInFieldName() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm(createAvailableLocales(US), US);
        ddmForm.addDDMFormField(new DDMFormField("$text", DDMFormFieldType.TEXT));
        _ddmFormValidatorImpl.validate(ddmForm);
    }

    @Test(expected = MustNotDuplicateFieldName.class)
    public void testDuplicateCaseInsensitiveFieldName() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm(createAvailableLocales(US), US);
        ddmForm.addDDMFormField(new DDMFormField("Name1", DDMFormFieldType.TEXT));
        DDMFormField name2DDMFormField = new DDMFormField("Name2", DDMFormFieldType.TEXT);
        name2DDMFormField.addNestedDDMFormField(new DDMFormField("name1", DDMFormFieldType.TEXT));
        ddmForm.addDDMFormField(name2DDMFormField);
        _ddmFormValidatorImpl.validate(ddmForm);
    }

    @Test(expected = MustNotDuplicateFieldName.class)
    public void testDuplicateFieldName() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm(createAvailableLocales(US), US);
        ddmForm.addDDMFormField(new DDMFormField("Name1", DDMFormFieldType.TEXT));
        DDMFormField name2DDMFormField = new DDMFormField("Name2", DDMFormFieldType.TEXT);
        name2DDMFormField.addNestedDDMFormField(new DDMFormField("Name1", DDMFormFieldType.TEXT));
        ddmForm.addDDMFormField(name2DDMFormField);
        _ddmFormValidatorImpl.validate(ddmForm);
    }

    @Test
    public void testFormRuleEmptyCondition() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm("Name");
        ddmForm.addDDMFormRule(new DDMFormRule("", Arrays.asList("true")));
        _ddmFormValidatorImpl.validate(ddmForm);
    }

    @Test(expected = MustSetValidIndexType.class)
    public void testInvalidFieldIndexType() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm(createAvailableLocales(US), US);
        DDMFormField ddmFormField = new DDMFormField("Text", DDMFormFieldType.TEXT);
        ddmFormField.setIndexType("Invalid");
        ddmForm.addDDMFormField(ddmFormField);
        _ddmFormValidatorImpl.validate(ddmForm);
    }

    @Test(expected = MustSetValidCharactersForFieldName.class)
    public void testInvalidFieldName() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm(createAvailableLocales(US), US);
        DDMFormField ddmFormField = new DDMFormField("*", DDMFormFieldType.TEXT);
        ddmForm.addDDMFormField(ddmFormField);
        _ddmFormValidatorImpl.validate(ddmForm);
    }

    @Test(expected = MustSetValidCharactersForFieldType.class)
    public void testInvalidFieldType() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm(createAvailableLocales(US), US);
        DDMFormField ddmFormField = new DDMFormField("Name", "html-text_*");
        ddmForm.addDDMFormField(ddmFormField);
        _ddmFormValidatorImpl.validate(ddmForm);
    }

    @Test(expected = MustSetValidValidationExpression.class)
    public void testInvalidFieldValidationExpression() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm(createAvailableLocales(US), US);
        DDMFormField ddmFormField = new DDMFormField("Name", DDMFormFieldType.TEXT);
        DDMFormFieldValidation ddmFormFieldValidation = new DDMFormFieldValidation();
        ddmFormFieldValidation.setExpression("*/+");
        ddmFormField.setDDMFormFieldValidation(ddmFormFieldValidation);
        ddmForm.addDDMFormField(ddmFormField);
        _ddmFormValidatorImpl.validate(ddmForm);
    }

    @Test
    public void testInvalidFieldValidationExpressionMessage() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm(createAvailableLocales(US), US);
        DDMFormField ddmFormField = new DDMFormField("Name", DDMFormFieldType.TEXT);
        DDMFormFieldValidation ddmFormFieldValidation = new DDMFormFieldValidation();
        String expression = "*/+";
        ddmFormFieldValidation.setExpression(expression);
        ddmFormField.setDDMFormFieldValidation(ddmFormFieldValidation);
        ddmForm.addDDMFormField(ddmFormField);
        try {
            _ddmFormValidatorImpl.validate(ddmForm);
        } catch (MustSetValidValidationExpression msvve) {
            Assert.assertTrue(StringUtil.equals(expression, msvve.getExpression()));
        }
    }

    @Test(expected = MustSetValidVisibilityExpression.class)
    public void testInvalidFieldVisibilityExpression() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm(createAvailableLocales(US), US);
        DDMFormField ddmFormField = new DDMFormField("Name", DDMFormFieldType.TEXT);
        ddmFormField.setVisibilityExpression("1 -< 2");
        ddmForm.addDDMFormField(ddmFormField);
        _ddmFormValidatorImpl.validate(ddmForm);
    }

    @Test(expected = MustSetValidFormRuleExpression.class)
    public void testInvalidFormRuleAction() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm("Name");
        ddmForm.addDDMFormRule(new DDMFormRule("true", Arrays.asList("*/?")));
        _ddmFormValidatorImpl.validate(ddmForm);
    }

    @Test(expected = MustSetValidFormRuleExpression.class)
    public void testInvalidFormRuleCondition() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm("Name");
        ddmForm.addDDMFormRule(new DDMFormRule("*/?", Arrays.asList("true")));
        _ddmFormValidatorImpl.validate(ddmForm);
    }

    @Test(expected = MustSetFieldsForForm.class)
    public void testNoFieldsSetForForm() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm(createAvailableLocales(US), US);
        _ddmFormValidatorImpl.validate(ddmForm);
    }

    @Test(expected = MustSetOptionsForField.class)
    public void testNoOptionsSetForFieldOptions() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm(createAvailableLocales(US), US);
        DDMFormField ddmFormField = new DDMFormField("Select", DDMFormFieldType.SELECT);
        ddmFormField.setProperty("dataSourceType", "manual");
        ddmForm.addDDMFormField(ddmFormField);
        _ddmFormValidatorImpl.validate(ddmForm);
    }

    @Test(expected = MustSetOptionsForField.class)
    public void testNoOptionsSetForMultipleCheckbox() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm(createAvailableLocales(US), US);
        DDMFormField ddmFormField = new DDMFormField("MultipleCheckbox", DDMFormFieldType.CHECKBOX_MULTIPLE);
        ddmFormField.setProperty("dataSourceType", "manual");
        ddmForm.addDDMFormField(ddmFormField);
        _ddmFormValidatorImpl.validate(ddmForm);
    }

    @Test(expected = MustSetAvailableLocales.class)
    public void testNullAvailableLocales() throws Exception {
        DDMForm ddmForm = new DDMForm();
        ddmForm.setAvailableLocales(null);
        ddmForm.setDefaultLocale(US);
        _ddmFormValidatorImpl.validate(ddmForm);
    }

    @Test(expected = MustSetDefaultLocale.class)
    public void testNullDefaultLocale() throws Exception {
        DDMForm ddmForm = new DDMForm();
        ddmForm.setDefaultLocale(null);
        _ddmFormValidatorImpl.validate(ddmForm);
    }

    @Test
    public void testOptionsSetForMultipleCheckbox() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm(createAvailableLocales(US), US);
        DDMFormField ddmFormField = new DDMFormField("MultipleCheckbox", DDMFormFieldType.CHECKBOX_MULTIPLE);
        ddmFormField.setProperty("dataSourceType", "manual");
        DDMFormFieldOptions ddmFormFieldOptions = ddmFormField.getDDMFormFieldOptions();
        ddmFormFieldOptions.addOptionLabel("1", US, "Option 1");
        ddmFormFieldOptions.addOptionLabel("2", US, "Option 2");
        ddmForm.addDDMFormField(ddmFormField);
        _ddmFormValidatorImpl.validate(ddmForm);
    }

    @Test(expected = MustSetValidCharactersForFieldName.class)
    public void testSpaceInFieldName() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm(createAvailableLocales(US), US);
        ddmForm.addDDMFormField(new DDMFormField("Text with Space", DDMFormFieldType.TEXT));
        _ddmFormValidatorImpl.validate(ddmForm);
    }

    @Test
    public void testSpecialCharactersInFieldName() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm(createAvailableLocales(US), US);
        ddmForm.addDDMFormField(new DDMFormField("??", DDMFormFieldType.TEXT));
        _ddmFormValidatorImpl.validate(ddmForm);
    }

    @Test
    public void testSpecialCharactersInFieldType() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm(createAvailableLocales(US), US);
        DDMFormField ddmFormField = new DDMFormField("Name", "html-????");
        ddmForm.addDDMFormField(ddmFormField);
        _ddmFormValidatorImpl.validate(ddmForm);
    }

    @Test
    public void testValidFieldType() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm(createAvailableLocales(US), US);
        DDMFormField ddmFormField = new DDMFormField("Name", "html-text_1");
        ddmForm.addDDMFormField(ddmFormField);
        _ddmFormValidatorImpl.validate(ddmForm);
    }

    @Test
    public void testValidFieldValidationExpression() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm(createAvailableLocales(US), US);
        DDMFormField ddmFormField = new DDMFormField("Name", DDMFormFieldType.TEXT);
        DDMFormFieldValidation ddmFormFieldValidation = new DDMFormFieldValidation();
        ddmFormFieldValidation.setExpression("false");
        ddmFormField.setDDMFormFieldValidation(ddmFormFieldValidation);
        ddmForm.addDDMFormField(ddmFormField);
        _ddmFormValidatorImpl.validate(ddmForm);
    }

    @Test
    public void testValidFieldVisibilityExpression() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm(createAvailableLocales(US), US);
        DDMFormField ddmFormField = new DDMFormField("Name", DDMFormFieldType.TEXT);
        ddmFormField.setVisibilityExpression("1 < 2");
        ddmForm.addDDMFormField(ddmFormField);
        _ddmFormValidatorImpl.validate(ddmForm);
    }

    @Test(expected = MustSetValidAvailableLocalesForProperty.class)
    public void testWrongAvailableLocalesSetForFieldOptions() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm(createAvailableLocales(US), US);
        DDMFormField ddmFormField = new DDMFormField("Select", DDMFormFieldType.SELECT);
        ddmFormField.setProperty("dataSourceType", "manual");
        DDMFormFieldOptions ddmFormFieldOptions = ddmFormField.getDDMFormFieldOptions();
        ddmFormFieldOptions.addOptionLabel("Value", BRAZIL, "Portuguese Label");
        ddmForm.addDDMFormField(ddmFormField);
        _ddmFormValidatorImpl.validate(ddmForm);
    }

    @Test(expected = MustSetValidAvailableLocalesForProperty.class)
    public void testWrongAvailableLocalesSetForLabel() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm(createAvailableLocales(US), US);
        DDMFormField ddmFormField = new DDMFormField("Text", DDMFormFieldType.TEXT);
        LocalizedValue label = ddmFormField.getLabel();
        label.addString(BRAZIL, "Portuguese Label");
        ddmForm.addDDMFormField(ddmFormField);
        _ddmFormValidatorImpl.validate(ddmForm);
    }

    @Test(expected = MustSetValidDefaultLocaleForProperty.class)
    public void testWrongDefaultLocaleSetForFieldOptions() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm(createAvailableLocales(US), US);
        DDMFormField ddmFormField = new DDMFormField("Select", DDMFormFieldType.SELECT);
        ddmFormField.setProperty("dataSourceType", "manual");
        DDMFormFieldOptions ddmFormFieldOptions = ddmFormField.getDDMFormFieldOptions();
        ddmFormFieldOptions.addOptionLabel("Value", US, "Value Label");
        ddmFormFieldOptions.setDefaultLocale(BRAZIL);
        ddmForm.addDDMFormField(ddmFormField);
        _ddmFormValidatorImpl.validate(ddmForm);
    }

    @Test(expected = MustSetValidDefaultLocaleForProperty.class)
    public void testWrongDefaultLocaleSetForLabel() throws Exception {
        DDMForm ddmForm = DDMFormTestUtil.createDDMForm(createAvailableLocales(US), US);
        DDMFormField ddmFormField = new DDMFormField("Text", DDMFormFieldType.TEXT);
        LocalizedValue label = ddmFormField.getLabel();
        label.addString(US, "Label");
        label.setDefaultLocale(BRAZIL);
        ddmForm.addDDMFormField(ddmFormField);
        _ddmFormValidatorImpl.validate(ddmForm);
    }

    private final DDMFormValidatorImpl _ddmFormValidatorImpl = new DDMFormValidatorImpl();
}

