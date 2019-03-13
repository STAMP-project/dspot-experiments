/**
 * Copyright 2016 Google LLC
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
package com.google.cloud;


import ValueType.BOOL;
import ValueType.INT64;
import ValueType.STRING;
import com.google.cloud.MonitoredResourceDescriptor.LabelDescriptor;
import com.google.cloud.MonitoredResourceDescriptor.LabelDescriptor.ValueType;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class MonitoredResourceDescriptorTest {
    private static final LabelDescriptor BOOLEAN_LABEL = new LabelDescriptor("booleanKey", ValueType.BOOL, "Boolean label");

    private static final LabelDescriptor STRING_LABEL = new LabelDescriptor("stringKey", ValueType.STRING, "String label");

    private static final LabelDescriptor INT_LABEL = new LabelDescriptor("intKey", ValueType.INT64, "Int label");

    private static final LabelDescriptor INT_LABEL_NO_DESCRIPTION = new LabelDescriptor("intKey", ValueType.INT64, null);

    private static final String TYPE = "resource_type";

    private static final String NAME = "resourceName";

    private static final String DISPLAY_NAME = "Display Name";

    private static final String DESCRIPTION = "Resource Descriptor";

    private static final List<LabelDescriptor> LABELS = ImmutableList.of(MonitoredResourceDescriptorTest.BOOLEAN_LABEL, MonitoredResourceDescriptorTest.STRING_LABEL, MonitoredResourceDescriptorTest.INT_LABEL);

    private static final MonitoredResourceDescriptor RESOURCE_DESCRIPTOR = MonitoredResourceDescriptor.newBuilder(MonitoredResourceDescriptorTest.TYPE).setName(MonitoredResourceDescriptorTest.NAME).setDisplayName(MonitoredResourceDescriptorTest.DISPLAY_NAME).setDescription(MonitoredResourceDescriptorTest.DESCRIPTION).setLabels(MonitoredResourceDescriptorTest.LABELS).build();

    @Test
    public void testLabelDescriptor() {
        Assert.assertEquals("booleanKey", MonitoredResourceDescriptorTest.BOOLEAN_LABEL.getKey());
        Assert.assertEquals(BOOL, MonitoredResourceDescriptorTest.BOOLEAN_LABEL.getValueType());
        Assert.assertEquals("Boolean label", MonitoredResourceDescriptorTest.BOOLEAN_LABEL.getDescription());
        Assert.assertEquals("stringKey", MonitoredResourceDescriptorTest.STRING_LABEL.getKey());
        Assert.assertEquals(STRING, MonitoredResourceDescriptorTest.STRING_LABEL.getValueType());
        Assert.assertEquals("String label", MonitoredResourceDescriptorTest.STRING_LABEL.getDescription());
        Assert.assertEquals("intKey", MonitoredResourceDescriptorTest.INT_LABEL.getKey());
        Assert.assertEquals(INT64, MonitoredResourceDescriptorTest.INT_LABEL.getValueType());
        Assert.assertEquals("Int label", MonitoredResourceDescriptorTest.INT_LABEL.getDescription());
        Assert.assertEquals("intKey", MonitoredResourceDescriptorTest.INT_LABEL_NO_DESCRIPTION.getKey());
        Assert.assertEquals(INT64, MonitoredResourceDescriptorTest.INT_LABEL_NO_DESCRIPTION.getValueType());
        Assert.assertNull(MonitoredResourceDescriptorTest.INT_LABEL_NO_DESCRIPTION.getDescription());
    }

    @Test
    public void testBuilder() {
        Assert.assertEquals(MonitoredResourceDescriptorTest.TYPE, MonitoredResourceDescriptorTest.RESOURCE_DESCRIPTOR.getType());
        Assert.assertEquals(MonitoredResourceDescriptorTest.NAME, MonitoredResourceDescriptorTest.RESOURCE_DESCRIPTOR.getName());
        Assert.assertEquals(MonitoredResourceDescriptorTest.DISPLAY_NAME, MonitoredResourceDescriptorTest.RESOURCE_DESCRIPTOR.getDisplayName());
        Assert.assertEquals(MonitoredResourceDescriptorTest.DESCRIPTION, MonitoredResourceDescriptorTest.RESOURCE_DESCRIPTOR.getDescription());
        Assert.assertEquals(MonitoredResourceDescriptorTest.LABELS, MonitoredResourceDescriptorTest.RESOURCE_DESCRIPTOR.getLabels());
        MonitoredResourceDescriptor resourceDescriptor = MonitoredResourceDescriptor.newBuilder(MonitoredResourceDescriptorTest.TYPE).build();
        Assert.assertEquals(MonitoredResourceDescriptorTest.TYPE, resourceDescriptor.getType());
        Assert.assertNull(resourceDescriptor.getName());
        Assert.assertNull(resourceDescriptor.getDisplayName());
        Assert.assertNull(resourceDescriptor.getDescription());
        Assert.assertEquals(ImmutableList.of(), resourceDescriptor.getLabels());
    }

    @Test
    public void testToAndFromPbLabelDescriptor() {
        compareLabelDescriptor(MonitoredResourceDescriptorTest.BOOLEAN_LABEL, LabelDescriptor.fromPb(MonitoredResourceDescriptorTest.BOOLEAN_LABEL.toPb()));
        compareLabelDescriptor(MonitoredResourceDescriptorTest.STRING_LABEL, LabelDescriptor.fromPb(MonitoredResourceDescriptorTest.STRING_LABEL.toPb()));
        compareLabelDescriptor(MonitoredResourceDescriptorTest.INT_LABEL, LabelDescriptor.fromPb(MonitoredResourceDescriptorTest.INT_LABEL.toPb()));
        compareLabelDescriptor(MonitoredResourceDescriptorTest.INT_LABEL_NO_DESCRIPTION, LabelDescriptor.fromPb(MonitoredResourceDescriptorTest.INT_LABEL_NO_DESCRIPTION.toPb()));
    }

    @Test
    public void testToAndFromPb() {
        compareResourceDescriptor(MonitoredResourceDescriptorTest.RESOURCE_DESCRIPTOR, MonitoredResourceDescriptor.fromPb(MonitoredResourceDescriptorTest.RESOURCE_DESCRIPTOR.toPb()));
        MonitoredResourceDescriptor resourceDescriptor = MonitoredResourceDescriptor.newBuilder(MonitoredResourceDescriptorTest.TYPE).build();
        compareResourceDescriptor(resourceDescriptor, MonitoredResourceDescriptor.fromPb(resourceDescriptor.toPb()));
    }
}

