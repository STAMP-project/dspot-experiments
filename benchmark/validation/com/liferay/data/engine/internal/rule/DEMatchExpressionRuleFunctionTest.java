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


import DEDataDefinitionRuleConstants.EXPRESSION_PARAMETER;
import DEDataDefinitionRuleConstants.VALUE_MUST_MATCH_EXPRESSION_ERROR;
import com.liferay.data.engine.model.DEDataDefinitionField;
import com.liferay.data.engine.rule.DEDataDefinitionRuleFunctionApplyRequest;
import com.liferay.data.engine.rule.DEDataDefinitionRuleFunctionApplyResponse;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Leonardo Barros
 */
public class DEMatchExpressionRuleFunctionTest {
    @Test
    public void testInvalidRegex() {
        _deDataDefinitionRuleFunctionApplyRequest.setValue("test@liferay");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(EXPRESSION_PARAMETER, "\\\\S+[@\\S+\\.\\S+");
        _deDataDefinitionRuleFunctionApplyRequest.setParameters(parameters);
        DEDataDefinitionRuleFunctionApplyResponse deDataDefinitionRuleFunctionApplyResponse = _deMatchExpressionRuleFunction.apply(_deDataDefinitionRuleFunctionApplyRequest);
        Assert.assertFalse(deDataDefinitionRuleFunctionApplyResponse.isValid());
        Assert.assertEquals(VALUE_MUST_MATCH_EXPRESSION_ERROR, deDataDefinitionRuleFunctionApplyResponse.getErrorCode());
        Assert.assertEquals(_deDataDefinitionField, deDataDefinitionRuleFunctionApplyResponse.getDEDataDefinitionField());
    }

    @Test
    public void testNotMatch() {
        _deDataDefinitionRuleFunctionApplyRequest.setValue("test@liferay");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(EXPRESSION_PARAMETER, "\\S+@\\S+\\.\\S+");
        _deDataDefinitionRuleFunctionApplyRequest.setParameters(parameters);
        DEDataDefinitionRuleFunctionApplyResponse deDataDefinitionRuleFunctionApplyResponse = _deMatchExpressionRuleFunction.apply(_deDataDefinitionRuleFunctionApplyRequest);
        Assert.assertFalse(deDataDefinitionRuleFunctionApplyResponse.isValid());
        Assert.assertEquals(VALUE_MUST_MATCH_EXPRESSION_ERROR, deDataDefinitionRuleFunctionApplyResponse.getErrorCode());
        Assert.assertEquals(_deDataDefinitionField, deDataDefinitionRuleFunctionApplyResponse.getDEDataDefinitionField());
    }

    @Test
    public void testValidMatch() {
        _deDataDefinitionRuleFunctionApplyRequest.setValue("test@liferay.com");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(EXPRESSION_PARAMETER, "\\S+@\\S+\\.\\S+");
        _deDataDefinitionRuleFunctionApplyRequest.setParameters(parameters);
        DEDataDefinitionRuleFunctionApplyResponse deDataDefinitionRuleFunctionApplyResponse = _deMatchExpressionRuleFunction.apply(_deDataDefinitionRuleFunctionApplyRequest);
        Assert.assertTrue(deDataDefinitionRuleFunctionApplyResponse.isValid());
        Assert.assertNull(deDataDefinitionRuleFunctionApplyResponse.getErrorCode());
        Assert.assertEquals(_deDataDefinitionField, deDataDefinitionRuleFunctionApplyResponse.getDEDataDefinitionField());
    }

    private final DEDataDefinitionField _deDataDefinitionField = new DEDataDefinitionField("field", "text");

    private DEDataDefinitionRuleFunctionApplyRequest _deDataDefinitionRuleFunctionApplyRequest;

    private DEMatchExpressionRuleFunction _deMatchExpressionRuleFunction;
}

