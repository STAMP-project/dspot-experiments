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
package com.liferay.data.engine.internal.rule;


import DEDataDefinitionRuleConstants.INVALID_URL_ERROR;
import com.liferay.data.engine.model.DEDataDefinitionField;
import com.liferay.data.engine.rule.DEDataDefinitionRuleFunctionApplyRequest;
import com.liferay.data.engine.rule.DEDataDefinitionRuleFunctionApplyResponse;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Leonardo Barros
 */
public class DEURLRuleFunctionTest {
    @Test
    public void testInvalidURL() {
        _deDataDefinitionRuleFunctionApplyRequest.setValue("INVALID");
        DEDataDefinitionRuleFunctionApplyResponse deDataDefinitionRuleFunctionApplyResponse = _deURLRuleFunction.apply(_deDataDefinitionRuleFunctionApplyRequest);
        Assert.assertFalse(deDataDefinitionRuleFunctionApplyResponse.isValid());
        Assert.assertEquals(INVALID_URL_ERROR, deDataDefinitionRuleFunctionApplyResponse.getErrorCode());
        Assert.assertEquals(_deDataDefinitionField, deDataDefinitionRuleFunctionApplyResponse.getDEDataDefinitionField());
    }

    @Test
    public void testNullValue() {
        DEDataDefinitionRuleFunctionApplyResponse deDataDefinitionRuleFunctionApplyResponse = _deURLRuleFunction.apply(_deDataDefinitionRuleFunctionApplyRequest);
        Assert.assertFalse(deDataDefinitionRuleFunctionApplyResponse.isValid());
        Assert.assertEquals(INVALID_URL_ERROR, deDataDefinitionRuleFunctionApplyResponse.getErrorCode());
        Assert.assertEquals(_deDataDefinitionField, deDataDefinitionRuleFunctionApplyResponse.getDEDataDefinitionField());
    }

    @Test
    public void testValidURL() {
        _deDataDefinitionRuleFunctionApplyRequest.setValue("http://www.liferay.com");
        DEDataDefinitionRuleFunctionApplyResponse deDataDefinitionRuleFunctionApplyResponse = _deURLRuleFunction.apply(_deDataDefinitionRuleFunctionApplyRequest);
        Assert.assertTrue(deDataDefinitionRuleFunctionApplyResponse.isValid());
        Assert.assertNull(deDataDefinitionRuleFunctionApplyResponse.getErrorCode());
        Assert.assertEquals(_deDataDefinitionField, deDataDefinitionRuleFunctionApplyResponse.getDEDataDefinitionField());
    }

    private final DEDataDefinitionField _deDataDefinitionField = new DEDataDefinitionField("url", "text");

    private DEDataDefinitionRuleFunctionApplyRequest _deDataDefinitionRuleFunctionApplyRequest;

    private DEURLRuleFunction _deURLRuleFunction;
}

