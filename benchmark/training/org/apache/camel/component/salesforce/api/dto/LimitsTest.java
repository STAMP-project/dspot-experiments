/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.salesforce.api.dto;


import Limits.Operation;
import Usage.UNKNOWN;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.camel.component.salesforce.api.dto.Limits.Usage;
import org.apache.camel.component.salesforce.api.utils.JsonUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Test;

import static Limits.Operation;


public class LimitsTest {
    @Test
    public void shouldBeKnownIfDefined() {
        Assert.assertFalse("Known usage must not declare itself as unknown", new Usage(1, 2).isUnknown());
    }

    @Test
    public void shouldDeserializeFromSalesforceGeneratedJSON() throws JsonProcessingException, IOException {
        final ObjectMapper mapper = JsonUtils.createObjectMapper();
        final Object read = mapper.readerFor(Limits.class).readValue(LimitsTest.class.getResource("/org/apache/camel/component/salesforce/api/dto/limits.json"));
        Assert.assertThat("Limits should be parsed from JSON", read, IsInstanceOf.instanceOf(Limits.class));
        final Limits limits = ((Limits) (read));
        final Usage dailyApiRequests = limits.getDailyApiRequests();
        Assert.assertFalse("Should have some usage present", dailyApiRequests.isUnknown());
        Assert.assertFalse("Per application usage should be present", dailyApiRequests.getPerApplicationUsage().isEmpty());
        Assert.assertNotNull("'Camel Salesman' application usage should be present", dailyApiRequests.forApplication("Camel Salesman"));
    }

    @Test
    public void shouldDeserializeWithUnsupportedKeys() throws JsonProcessingException, IOException {
        final ObjectMapper mapper = JsonUtils.createObjectMapper();
        final Limits withUnsupported = mapper.readerFor(Limits.class).readValue("{\"Camel-NotSupportedKey\": {\"Max\": 200,\"Remaining\": 200}}");
        Assert.assertNotNull(withUnsupported);
        Assert.assertNotNull(withUnsupported.forOperation("Camel-NotSupportedKey"));
    }

    @Test
    public void shouldSupportGettingAllDefinedUsages() throws IntrospectionException {
        final BeanInfo beanInfo = Introspector.getBeanInfo(Limits.class);
        final PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        final Set<String> found = new HashSet<>();
        for (final PropertyDescriptor descriptor : propertyDescriptors) {
            found.add(descriptor.getName());
        }
        final Set<String> defined = Arrays.stream(Operation.values()).map(Operation::name).map(Introspector::decapitalize).collect(Collectors.toSet());
        defined.removeAll(found);
        Assert.assertThat("All operations declared in Operation enum should have it's corresponding getter", defined, CoreMatchers.is(Collections.emptySet()));
    }

    @Test
    public void usageShouldBeUnknownIfUnknown() {
        Assert.assertTrue("Unknown usage must declare itself as such", UNKNOWN.isUnknown());
    }
}

