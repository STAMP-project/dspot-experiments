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
package com.liferay.data.engine.internal.io;


import DEDataDefinitionDeserializerApplyRequest.Builder;
import DEDataDefinitionRuleConstants.EMAIL_ADDRESS_RULE;
import DEDataDefinitionRuleConstants.MATCH_EXPRESSION_RULE;
import DEDataDefinitionRuleConstants.REQUIRED_RULE;
import com.liferay.data.engine.exception.DEDataDefinitionDeserializerException;
import com.liferay.data.engine.io.DEDataDefinitionDeserializerApplyRequest;
import com.liferay.data.engine.io.DEDataDefinitionDeserializerApplyResponse;
import com.liferay.data.engine.model.DEDataDefinition;
import com.liferay.data.engine.model.DEDataDefinitionField;
import com.liferay.data.engine.model.DEDataDefinitionRule;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.util.ArrayUtil;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Leonardo Barros
 */
public class DEDataDefinitionJSONDeserializerTest extends BaseTestCase {
    @Test
    public void testApply() throws Exception {
        String json = read("data-definition-deserializer.json");
        DEDataDefinitionDeserializerApplyRequest deDataDefinitionFieldsDeserializerApplyRequest = Builder.of(json);
        DEDataDefinitionJSONDeserializer deDataDefinitionFieldsJSONDeserializer = new DEDataDefinitionJSONDeserializer();
        deDataDefinitionFieldsJSONDeserializer.jsonFactory = new JSONFactoryImpl();
        DEDataDefinitionDeserializerApplyResponse deDataDefinitionDeserializerApplyResponse = deDataDefinitionFieldsJSONDeserializer.apply(deDataDefinitionFieldsDeserializerApplyRequest);
        DEDataDefinition deDataDefinition = deDataDefinitionDeserializerApplyResponse.getDEDataDefinition();
        List<DEDataDefinitionField> deDataDefinitionFields = deDataDefinition.getDEDataDefinitionFields();
        Assert.assertEquals(deDataDefinitionFields.toString(), 3, deDataDefinitionFields.size());
        DEDataDefinitionField deDataDefinitionField = deDataDefinitionFields.get(0);
        Assert.assertEquals(false, deDataDefinitionField.isIndexable());
        Assert.assertEquals(true, deDataDefinitionField.isLocalizable());
        Assert.assertEquals(false, deDataDefinitionField.isRepeatable());
        Assert.assertEquals("name", deDataDefinitionField.getName());
        Assert.assertEquals("text", deDataDefinitionField.getType());
        Map<String, String> labels = deDataDefinitionField.getLabel();
        Assert.assertEquals("Name", labels.get("en_US"));
        Assert.assertEquals("Nome", labels.get("pt_BR"));
        deDataDefinitionField = deDataDefinitionFields.get(1);
        Assert.assertEquals(true, deDataDefinitionField.isIndexable());
        Assert.assertEquals(false, deDataDefinitionField.isLocalizable());
        Assert.assertEquals(true, deDataDefinitionField.isRepeatable());
        Assert.assertEquals("email", deDataDefinitionField.getName());
        Assert.assertEquals("text", deDataDefinitionField.getType());
        labels = deDataDefinitionField.getLabel();
        Assert.assertEquals("Email Address", labels.get("en_US"));
        Assert.assertEquals("Endere?o de Email", labels.get("pt_BR"));
        Map<String, String> tips = deDataDefinitionField.getTip();
        Assert.assertEquals("Enter an email address", tips.get("en_US"));
        Assert.assertEquals("Informe um endere?o de email", tips.get("pt_BR"));
        deDataDefinitionField = deDataDefinitionFields.get(2);
        Assert.assertEquals(false, deDataDefinitionField.isIndexable());
        Assert.assertEquals(true, deDataDefinitionField.isLocalizable());
        Assert.assertEquals(false, deDataDefinitionField.isRepeatable());
        Assert.assertEquals("salary", deDataDefinitionField.getName());
        Assert.assertEquals("numeric", deDataDefinitionField.getType());
        List<DEDataDefinitionRule> deDataDefinitionRules = deDataDefinition.getDEDataDefinitionRules();
        Assert.assertEquals(deDataDefinitionRules.toString(), 3, deDataDefinitionRules.size());
        DEDataDefinitionRule deDataDefinitionRule = deDataDefinitionRules.get(0);
        Assert.assertEquals(EMAIL_ADDRESS_RULE, deDataDefinitionRule.getName());
        Assert.assertArrayEquals(new String[]{ "email" }, ArrayUtil.toStringArray(deDataDefinitionRule.getDEDataDefinitionFieldNames()));
        deDataDefinitionRule = deDataDefinitionRules.get(1);
        Assert.assertEquals(REQUIRED_RULE, deDataDefinitionRule.getName());
        Assert.assertArrayEquals(new String[]{ "name" }, ArrayUtil.toStringArray(deDataDefinitionRule.getDEDataDefinitionFieldNames()));
        deDataDefinitionRule = deDataDefinitionRules.get(2);
        Assert.assertEquals(MATCH_EXPRESSION_RULE, deDataDefinitionRule.getName());
        Assert.assertArrayEquals(new String[]{ "salary" }, ArrayUtil.toStringArray(deDataDefinitionRule.getDEDataDefinitionFieldNames()));
        Map<String, Object> parameters = deDataDefinitionRule.getParameters();
        Assert.assertEquals("^[0-9]+(\\.[0-9]{1,2})?", parameters.get("expression"));
    }

    @Test(expected = DEDataDefinitionDeserializerException.class)
    public void testInvalidLabel() throws Exception {
        String json = read("data-definition-deserializer-invalid-label.json");
        DEDataDefinitionDeserializerApplyRequest deDataDefinitionFieldsDeserializerApplyRequest = Builder.of(json);
        DEDataDefinitionJSONDeserializer deDataDefinitionFieldsJSONDeserializer = new DEDataDefinitionJSONDeserializer();
        deDataDefinitionFieldsJSONDeserializer.jsonFactory = new JSONFactoryImpl();
        deDataDefinitionFieldsJSONDeserializer.apply(deDataDefinitionFieldsDeserializerApplyRequest);
    }

    @Test(expected = DEDataDefinitionDeserializerException.class)
    public void testInvalidTip() throws Exception {
        String json = read("data-definition-deserializer-invalid-tip.json");
        DEDataDefinitionDeserializerApplyRequest deDataDefinitionFieldsDeserializerApplyRequest = Builder.of(json);
        DEDataDefinitionJSONDeserializer deDataDefinitionFieldsJSONDeserializer = new DEDataDefinitionJSONDeserializer();
        deDataDefinitionFieldsJSONDeserializer.jsonFactory = new JSONFactoryImpl();
        deDataDefinitionFieldsJSONDeserializer.apply(deDataDefinitionFieldsDeserializerApplyRequest);
    }

    @Test(expected = DEDataDefinitionDeserializerException.class)
    public void testRequiredName() throws Exception {
        String json = read("data-definition-deserializer-required-name.json");
        DEDataDefinitionDeserializerApplyRequest deDataDefinitionFieldsDeserializerApplyRequest = Builder.of(json);
        DEDataDefinitionJSONDeserializer deDataDefinitionFieldsJSONDeserializer = new DEDataDefinitionJSONDeserializer();
        deDataDefinitionFieldsJSONDeserializer.jsonFactory = new JSONFactoryImpl();
        deDataDefinitionFieldsJSONDeserializer.apply(deDataDefinitionFieldsDeserializerApplyRequest);
    }

    @Test(expected = DEDataDefinitionDeserializerException.class)
    public void testRequiredType() throws Exception {
        String json = read("data-definition-deserializer-required-type.json");
        DEDataDefinitionDeserializerApplyRequest deDataDefinitionFieldsDeserializerApplyRequest = Builder.of(json);
        DEDataDefinitionJSONDeserializer deDataDefinitionFieldsJSONDeserializer = new DEDataDefinitionJSONDeserializer();
        deDataDefinitionFieldsJSONDeserializer.jsonFactory = new JSONFactoryImpl();
        deDataDefinitionFieldsJSONDeserializer.apply(deDataDefinitionFieldsDeserializerApplyRequest);
    }
}

