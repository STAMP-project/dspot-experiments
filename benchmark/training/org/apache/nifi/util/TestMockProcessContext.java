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
package org.apache.nifi.util;


import StandardValidators.NON_EMPTY_VALIDATOR;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.exception.ProcessException;
import org.junit.Assert;
import org.junit.Test;


public class TestMockProcessContext {
    @Test
    public void testRemoveProperty() {
        final TestMockProcessContext.DummyProcessor proc = new TestMockProcessContext.DummyProcessor();
        final MockProcessContext context = new MockProcessContext(proc);
        context.setProperty(TestMockProcessContext.DummyProcessor.REQUIRED_PROP, "req-value");
        context.setProperty(TestMockProcessContext.DummyProcessor.OPTIONAL_PROP, "opt-value");
        context.setProperty(TestMockProcessContext.DummyProcessor.DEFAULTED_PROP, "custom-value");
        Assert.assertEquals(1, proc.getUpdateCount(TestMockProcessContext.DummyProcessor.REQUIRED_PROP));
        Assert.assertEquals(1, proc.getUpdateCount(TestMockProcessContext.DummyProcessor.OPTIONAL_PROP));
        Assert.assertEquals(1, proc.getUpdateCount(TestMockProcessContext.DummyProcessor.DEFAULTED_PROP));
        Assert.assertTrue(context.removeProperty(TestMockProcessContext.DummyProcessor.OPTIONAL_PROP));
        Assert.assertNull(context.getProperty(TestMockProcessContext.DummyProcessor.OPTIONAL_PROP).getValue());
        Assert.assertFalse(context.removeProperty(TestMockProcessContext.DummyProcessor.OPTIONAL_PROP));
        Assert.assertEquals(2, proc.getUpdateCount(TestMockProcessContext.DummyProcessor.OPTIONAL_PROP));
        Assert.assertTrue(context.removeProperty(TestMockProcessContext.DummyProcessor.REQUIRED_PROP));
        Assert.assertNull(context.getProperty(TestMockProcessContext.DummyProcessor.REQUIRED_PROP).getValue());
        Assert.assertFalse(context.removeProperty(TestMockProcessContext.DummyProcessor.REQUIRED_PROP));
        Assert.assertEquals(2, proc.getUpdateCount(TestMockProcessContext.DummyProcessor.REQUIRED_PROP));
        Assert.assertTrue(context.removeProperty(TestMockProcessContext.DummyProcessor.DEFAULTED_PROP));
        Assert.assertEquals("default-value", context.getProperty(TestMockProcessContext.DummyProcessor.DEFAULTED_PROP).getValue());
        Assert.assertFalse(context.removeProperty(TestMockProcessContext.DummyProcessor.DEFAULTED_PROP));
        Assert.assertEquals(2, proc.getUpdateCount(TestMockProcessContext.DummyProcessor.DEFAULTED_PROP));
        // Since value is already the default, this shouldn't trigger onPropertyModified to be called.
        context.setProperty(TestMockProcessContext.DummyProcessor.DEFAULTED_PROP, TestMockProcessContext.DummyProcessor.DEFAULTED_PROP.getDefaultValue());
        Assert.assertEquals(2, proc.getUpdateCount(TestMockProcessContext.DummyProcessor.DEFAULTED_PROP));
        Assert.assertEquals("default-value", context.getProperty(TestMockProcessContext.DummyProcessor.DEFAULTED_PROP).getValue());
        Assert.assertTrue(context.removeProperty(TestMockProcessContext.DummyProcessor.DEFAULTED_PROP));
        // since we are calling remove on a property that has a default value, this shouldn't
        // trigger the onPropertyModified method to be called.
        Assert.assertEquals(2, proc.getUpdateCount(TestMockProcessContext.DummyProcessor.DEFAULTED_PROP));
    }

    private static class DummyProcessor extends AbstractProcessor {
        static final PropertyDescriptor REQUIRED_PROP = new PropertyDescriptor.Builder().name("required").required(true).addValidator(NON_EMPTY_VALIDATOR).build();

        static final PropertyDescriptor DEFAULTED_PROP = new PropertyDescriptor.Builder().name("defaulted").required(true).addValidator(NON_EMPTY_VALIDATOR).defaultValue("default-value").build();

        static final PropertyDescriptor OPTIONAL_PROP = new PropertyDescriptor.Builder().name("optional").required(false).addValidator(NON_EMPTY_VALIDATOR).build();

        private final Map<PropertyDescriptor, Integer> propertyModifiedCount = new HashMap<>();

        @Override
        protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
            final List<PropertyDescriptor> properties = new ArrayList<>();
            properties.add(TestMockProcessContext.DummyProcessor.REQUIRED_PROP);
            properties.add(TestMockProcessContext.DummyProcessor.DEFAULTED_PROP);
            properties.add(TestMockProcessContext.DummyProcessor.OPTIONAL_PROP);
            return properties;
        }

        @Override
        public void onPropertyModified(final PropertyDescriptor descriptor, final String oldValue, final String newValue) {
            Integer updateCount = propertyModifiedCount.get(descriptor);
            if (updateCount == null) {
                updateCount = 0;
            }
            propertyModifiedCount.put(descriptor, (updateCount + 1));
        }

        public int getUpdateCount(final PropertyDescriptor descriptor) {
            Integer updateCount = propertyModifiedCount.get(descriptor);
            return updateCount == null ? 0 : updateCount;
        }

        @Override
        public void onTrigger(final ProcessContext context, final ProcessSession session) throws ProcessException {
        }
    }
}

