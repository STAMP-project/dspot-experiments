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


import DEDataDefinitionSerializerApplyRequest.Builder;
import com.liferay.data.engine.exception.DEDataDefinitionSerializerException;
import com.liferay.data.engine.io.DEDataDefinitionSerializerApplyRequest;
import com.liferay.data.engine.io.DEDataDefinitionSerializerApplyResponse;
import com.liferay.data.engine.model.DEDataDefinition;
import com.liferay.data.engine.model.DEDataDefinitionField;
import com.liferay.data.engine.model.DEDataDefinitionRule;
import com.liferay.data.engine.model.DEDataDefinitionRuleFactory;
import com.liferay.portal.json.JSONFactoryImpl;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;


/**
 *
 *
 * @author Leonardo Barros
 */
public class DEDataDefinitionJSONSerializerTest extends BaseTestCase {
    @Test
    public void testApply() throws Exception {
        Map<String, String> nameLabels = new HashMap() {
            {
                put("pt_BR", "Nome");
                put("en_US", "Name");
            }
        };
        DEDataDefinitionField deDataDefinitionField1 = new DEDataDefinitionField("name", "text");
        deDataDefinitionField1.addLabels(nameLabels);
        Map<String, String> emailLabels = new HashMap() {
            {
                put("pt_BR", "Endere?o de Email");
                put("en_US", "Email Address");
            }
        };
        Map<String, String> emailTips = new HashMap() {
            {
                put("en_US", "Enter an email address");
                put("pt_BR", "Informe um endere?o de email");
            }
        };
        DEDataDefinitionField deDataDefinitionField2 = new DEDataDefinitionField("email", "text");
        deDataDefinitionField2.setDefaultValue("test@liferay.com");
        deDataDefinitionField2.setIndexable(false);
        deDataDefinitionField2.addLabels(emailLabels);
        deDataDefinitionField2.addTips(emailTips);
        Map<String, String> salaryLabels = new HashMap() {
            {
                put("pt_BR", "Sal?rio");
                put("en_US", "Salary");
            }
        };
        DEDataDefinitionField deDataDefinitionField3 = new DEDataDefinitionField("salary", "numeric");
        deDataDefinitionField3.setIndexable(false);
        deDataDefinitionField3.addLabels(salaryLabels);
        DEDataDefinitionRule deDataDefinitionRule1 = DEDataDefinitionRuleFactory.emailAddress(Arrays.asList("email"));
        DEDataDefinitionRule deDataDefinitionRule2 = DEDataDefinitionRuleFactory.required(Arrays.asList("name"));
        DEDataDefinitionRule deDataDefinitionRule3 = DEDataDefinitionRuleFactory.matchExpression("^[0-9]+(\\.[0-9]{1,2})?", Arrays.asList("salary"));
        DEDataDefinition deDataDefinition = new DEDataDefinition();
        deDataDefinition.setDEDataDefinitionFields(Arrays.asList(deDataDefinitionField1, deDataDefinitionField2, deDataDefinitionField3));
        deDataDefinition.setDEDataDefinitionRules(Arrays.asList(deDataDefinitionRule1, deDataDefinitionRule2, deDataDefinitionRule3));
        DEDataDefinitionSerializerApplyRequest.Builder builder = Builder.newBuilder(deDataDefinition);
        DEDataDefinitionJSONSerializer deDataDefinitionFieldsJSONSerializer = new DEDataDefinitionJSONSerializer();
        deDataDefinitionFieldsJSONSerializer.jsonFactory = new JSONFactoryImpl();
        DEDataDefinitionSerializerApplyResponse deDataDefinitionFieldsSerializerApplyResponse = deDataDefinitionFieldsJSONSerializer.apply(builder.build());
        String json = read("data-definition-serializer.json");
        JSONAssert.assertEquals(json, deDataDefinitionFieldsSerializerApplyResponse.getContent(), false);
    }

    @Test(expected = DEDataDefinitionSerializerException.class)
    public void testRequiredName() throws DEDataDefinitionSerializerException {
        DEDataDefinitionField deDataDefinitionField1 = new DEDataDefinitionField(null, "boolean");
        DEDataDefinition deDataDefinition = new DEDataDefinition();
        deDataDefinition.setDEDataDefinitionFields(Arrays.asList(deDataDefinitionField1));
        DEDataDefinitionSerializerApplyRequest.Builder builder = Builder.newBuilder(deDataDefinition);
        DEDataDefinitionJSONSerializer deDataDefinitionFieldsJSONSerializer = new DEDataDefinitionJSONSerializer();
        deDataDefinitionFieldsJSONSerializer.jsonFactory = new JSONFactoryImpl();
        deDataDefinitionFieldsJSONSerializer.apply(builder.build());
    }

    @Test(expected = DEDataDefinitionSerializerException.class)
    public void testRequiredType() throws DEDataDefinitionSerializerException {
        DEDataDefinitionField deDataDefinitionField1 = new DEDataDefinitionField("name", null);
        DEDataDefinition deDataDefinition = new DEDataDefinition();
        deDataDefinition.setDEDataDefinitionFields(Arrays.asList(deDataDefinitionField1));
        DEDataDefinitionSerializerApplyRequest.Builder builder = Builder.newBuilder(deDataDefinition);
        DEDataDefinitionJSONSerializer deDataDefinitionFieldsJSONSerializer = new DEDataDefinitionJSONSerializer();
        deDataDefinitionFieldsJSONSerializer.jsonFactory = new JSONFactoryImpl();
        deDataDefinitionFieldsJSONSerializer.apply(builder.build());
    }
}

