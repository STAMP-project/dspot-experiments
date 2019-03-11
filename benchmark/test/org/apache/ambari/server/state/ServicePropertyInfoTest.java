/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.state;


import Warning.NONFINAL_FIELDS;
import junit.framework.Assert;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;


public class ServicePropertyInfoTest {
    private static final String XML = "<property>\n" + (("  <name>prop_name</name>\n" + "  <value>prop_value</value>\n") + "</property>");

    @Test
    public void testName() throws Exception {
        // Given
        ServicePropertyInfo p = ServicePropertyInfoTest.getServiceProperty(ServicePropertyInfoTest.XML);
        // When
        String name = p.getName();
        // Then
        Assert.assertEquals("prop_name", name);
    }

    @Test
    public void testValue() throws Exception {
        // Given
        ServicePropertyInfo p = ServicePropertyInfoTest.getServiceProperty(ServicePropertyInfoTest.XML);
        // When
        String value = p.getValue();
        // Then
        Assert.assertEquals("prop_value", value);
    }

    @Test
    public void testEquals() throws Exception {
        EqualsVerifier.forClass(ServicePropertyInfo.class).suppress(NONFINAL_FIELDS).verify();
    }
}

