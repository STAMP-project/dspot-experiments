/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processor;


import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.nifi.components.PropertyValue;
import org.apache.nifi.controller.ControllerService;
import org.apache.nifi.controller.ControllerServiceLookup;
import org.apache.nifi.controller.repository.StandardFlowFileRecord;
import org.apache.nifi.flowfile.FlowFile;
import org.junit.Assert;
import org.junit.Test;


public class TestStandardPropertyValue {
    private final ControllerServiceLookup lookup = new TestStandardPropertyValue.TestControllerServiceLookup();

    @Test
    public void testSubstituteAttributesWithOneMatchingArg() {
        final PropertyValue value = new org.apache.nifi.attribute.expression.language.StandardPropertyValue("Hello, ${audience}!", lookup);
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("audience", "World");
        Assert.assertEquals("Hello, World!", value.evaluateAttributeExpressions(createFlowFile(attributes)).getValue());
    }

    @Test
    public void testMissingEndBraceEvaluatesToStringLiteral() {
        final PropertyValue value = new org.apache.nifi.attribute.expression.language.StandardPropertyValue("Hello, ${audience!", lookup);
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("audience", "World");
        Assert.assertEquals("Hello, ${audience!", value.evaluateAttributeExpressions(createFlowFile(attributes)).getValue());
    }

    @Test
    public void testEscaped() {
        final PropertyValue value = new org.apache.nifi.attribute.expression.language.StandardPropertyValue("Hello, $${audience}!", lookup);
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("audience", "World");
        Assert.assertEquals("Hello, ${audience}!", value.evaluateAttributeExpressions(createFlowFile(attributes)).getValue());
    }

    @Test
    public void testSubstituteAttributesWithMultipleMatchingArgs() {
        final PropertyValue value = new org.apache.nifi.attribute.expression.language.StandardPropertyValue("Hello, ${audience}${comma}${question}!", lookup);
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("audience", "World");
        attributes.put("comma", ",");
        attributes.put("question", " how are you?");
        Assert.assertEquals("Hello, World, how are you?!", value.evaluateAttributeExpressions(createFlowFile(attributes)).getValue());
    }

    @Test
    public void testSubstituteAttributesWithNoMatch() {
        final PropertyValue value = new org.apache.nifi.attribute.expression.language.StandardPropertyValue("Hello, ${audience}${comma}${question:replaceNull('')}!", lookup);
        final Map<String, String> attributes = new HashMap<>();
        Assert.assertEquals("Hello, !", value.evaluateAttributeExpressions(createFlowFile(attributes)).getValue());
    }

    @Test
    public void testSubstituteAttributesRecursively() {
        final PropertyValue value = new org.apache.nifi.attribute.expression.language.StandardPropertyValue("Hello, ${'${a}${b}'}!", lookup);
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("a", "b");
        attributes.put("b", "World");
        attributes.put("bWorld", "World");
        Assert.assertEquals("Hello, World!", value.evaluateAttributeExpressions(createFlowFile(attributes)).getValue());
    }

    @Test
    public void testGetValueAsIntegerAfterSubstitute() {
        final PropertyValue value = new org.apache.nifi.attribute.expression.language.StandardPropertyValue("1${value}", lookup);
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("value", "39");
        Assert.assertEquals(139, value.evaluateAttributeExpressions(createFlowFile(attributes)).asInteger().intValue());
    }

    @Test(expected = NumberFormatException.class)
    public void testGetValueAsIntegerAfterSubstitutingWithNonInteger() {
        final PropertyValue value = new org.apache.nifi.attribute.expression.language.StandardPropertyValue("1${value}", lookup);
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("value", "Yes");
        final PropertyValue substituted = value.evaluateAttributeExpressions(createFlowFile(attributes));
        substituted.asInteger();
    }

    @Test
    public void testFileSize() {
        final PropertyValue value = new org.apache.nifi.attribute.expression.language.StandardPropertyValue("${fileSize}", lookup);
        final FlowFile flowFile = new StandardFlowFileRecord.Builder().size((1024 * 1024L)).build();
        final long val = value.evaluateAttributeExpressions(flowFile).asLong().longValue();
        Assert.assertEquals((1024 * 1024L), val);
    }

    @Test
    public void testFlowFileEntryYear() {
        final Calendar now = Calendar.getInstance();
        final int year = now.get(Calendar.YEAR);
        final PropertyValue value = new org.apache.nifi.attribute.expression.language.StandardPropertyValue("${entryDate:toNumber():toDate():format('yyyy')}", lookup);
        final FlowFile flowFile = new StandardFlowFileRecord.Builder().entryDate(now.getTimeInMillis()).build();
        final int val = value.evaluateAttributeExpressions(flowFile).asInteger().intValue();
        Assert.assertEquals(year, val);
    }

    private static class TestControllerServiceLookup implements ControllerServiceLookup {
        private final Map<String, ControllerService> map = new HashMap<>();

        @Override
        public ControllerService getControllerService(final String serviceIdentifier) {
            return map.get(serviceIdentifier);
        }

        @Override
        public Set<String> getControllerServiceIdentifiers(final Class<? extends ControllerService> serviceType) {
            return null;
        }

        @Override
        public boolean isControllerServiceEnabled(final String serviceIdentifier) {
            return true;
        }

        @Override
        public boolean isControllerServiceEnabled(final ControllerService service) {
            return true;
        }

        @Override
        public String getControllerServiceName(final String serviceIdentifier) {
            return null;
        }

        @Override
        public boolean isControllerServiceEnabling(final String serviceIdentifier) {
            return false;
        }
    }
}

