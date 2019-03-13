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
package org.apache.ambari.server.state.alerts;


import SourceType.AGGREGATE;
import SourceType.PORT;
import category.AlertTest;
import junit.framework.TestCase;
import org.apache.ambari.server.state.alert.AlertDefinition;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Tests equality of {@link AlertDefinition} for hashing and merging purposes.
 */
@Category({ AlertTest.class })
public class AlertDefinitionEqualityTest extends TestCase {
    @Test
    public void testAlertDefinitionEquality() {
        AlertDefinition ad1 = getAlertDefinition(PORT);
        AlertDefinition ad2 = getAlertDefinition(PORT);
        TestCase.assertTrue(ad1.equals(ad2));
        TestCase.assertTrue(ad1.deeplyEquals(ad2));
        // change 1 property and check that name equality still works
        ad2.setInterval(2);
        TestCase.assertTrue(ad1.equals(ad2));
        TestCase.assertFalse(ad1.deeplyEquals(ad2));
        // change the name and verify name equality is broken
        ad2.setName(((getName()) + " foo"));
        TestCase.assertFalse(ad1.equals(ad2));
        TestCase.assertFalse(ad1.deeplyEquals(ad2));
        ad2 = getAlertDefinition(AGGREGATE);
        TestCase.assertFalse(ad1.deeplyEquals(ad2));
        ad2 = getAlertDefinition(PORT);
        TestCase.assertTrue(ad1.deeplyEquals(ad2));
        ad2.getSource().getReporting().getOk().setText("foo");
        TestCase.assertFalse(ad1.deeplyEquals(ad2));
    }
}

