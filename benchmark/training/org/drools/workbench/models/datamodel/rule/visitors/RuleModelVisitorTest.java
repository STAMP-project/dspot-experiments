/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.models.datamodel.rule.visitors;


import BaseSingleFieldConstraint.TYPE_TEMPLATE;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.drools.workbench.models.datamodel.rule.IAction;
import org.drools.workbench.models.datamodel.rule.InterpolationVariable;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.TemplateAware;
import org.junit.Assert;
import org.junit.Test;
import org.kie.soup.project.datamodel.oracle.DataType;


public class RuleModelVisitorTest {
    @Test
    public void visitTemplate() {
        Map<InterpolationVariable, Integer> variableMap = new HashMap<>();
        RuleModelVisitor visitor = new RuleModelVisitor(variableMap);
        visitor.visit(new RuleModelVisitorTest.TemplateAwareIAction());
        Assert.assertTrue(variableMap.containsKey(new InterpolationVariable("test", DataType.TYPE_OBJECT)));
    }

    @Test
    public void visitSingleFieldConstraint() {
        Map<InterpolationVariable, Integer> variableMap = new HashMap<>();
        RuleModelVisitor visitor = new RuleModelVisitor(variableMap);
        SingleFieldConstraint singleFieldConstraint = new SingleFieldConstraint();
        singleFieldConstraint.setConstraintValueType(TYPE_TEMPLATE);
        singleFieldConstraint.setFieldType("fieldType");
        singleFieldConstraint.setFieldName("fieldName");
        singleFieldConstraint.setValue("value");
        singleFieldConstraint.setOperator("==");
        visitor.visit(singleFieldConstraint);
        Assert.assertEquals(1, variableMap.keySet().size());
        InterpolationVariable interpolationVariable = variableMap.keySet().iterator().next();
        Assert.assertEquals("value", interpolationVariable.getVarName());
        Assert.assertEquals("fieldName", interpolationVariable.getFactField());
        Assert.assertEquals("fieldType", interpolationVariable.getDataType());
        Assert.assertEquals("==", interpolationVariable.getOperator());
    }

    private static class TemplateAwareIAction implements IAction , TemplateAware {
        @Override
        public Collection<InterpolationVariable> extractInterpolationVariables() {
            return Arrays.asList(new InterpolationVariable("test", DataType.TYPE_OBJECT));
        }

        @Override
        public void substituteTemplateVariables(Function<String, String> keyToValueFunction) {
        }

        @Override
        public TemplateAware cloneTemplateAware() {
            return new RuleModelVisitorTest.TemplateAwareIAction();
        }
    }
}

