/**
 * * Licensed to the Apache Software Foundation (ASF) under one
 *  * or more contributor license agreements.  See the NOTICE file
 *  * distributed with this work for additional information
 *  * regarding copyright ownership.  The ASF licenses this file
 *  * to you under the Apache License, Version 2.0 (the
 *  * "License"); you may not use this file except in compliance
 *  * with the License.  You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 */
package org.apache.ambari.server.agent.stomp.dto;


import org.apache.ambari.server.state.alert.AlertDefinition;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class AlertClusterTest {
    private static final int DEFAULT_INTERVAL = 1;

    private static final int CHANGED_INTERVAL = 999;

    private AlertCluster alertCluster;

    @Test
    public void testAddingNewAlertDefWithoutChangingExisting() throws Exception {
        // Given
        AlertDefinition existing1 = anAlertDefinition(1L);
        AlertDefinition existing2 = anAlertDefinition(2L);
        alertCluster = newAlertCluster(existing1, existing2);
        // When
        AlertDefinition newDef = anAlertDefinition(3L);
        AlertCluster result = update(alertCluster, newDef);
        // Then
        assertHasAlerts(result, existing1, existing2, newDef);
    }

    @Test
    public void testChangingContentOfExistingAlertDef() throws Exception {
        // Given
        AlertDefinition existing1 = anAlertDefinition(1L);
        AlertDefinition existing2 = anAlertDefinition(2L);
        alertCluster = newAlertCluster(existing1, existing2);
        // When
        AlertDefinition changed = anAlertDefinition(2, AlertClusterTest.CHANGED_INTERVAL);
        AlertCluster result = update(alertCluster, changed);
        // Then
        assertHasAlerts(result, existing1, changed);
    }

    @Test
    public void testAddingNewAlertDefAndChangingExisting() throws Exception {
        // Given
        AlertDefinition existing1 = anAlertDefinition(1L);
        AlertDefinition existing2 = anAlertDefinition(2L);
        alertCluster = newAlertCluster(existing1, existing2);
        // When
        AlertDefinition newDef = anAlertDefinition(3L);
        AlertDefinition changed = anAlertDefinition(2, 999);
        AlertCluster result = update(alertCluster, newDef, changed);
        // Then
        assertHasAlerts(result, existing1, changed, newDef);
    }

    @Test
    public void testNoChange() throws Exception {
        // Given
        AlertDefinition existing = anAlertDefinition(1L);
        alertCluster = newAlertCluster(existing);
        // When
        AlertCluster result = update(alertCluster, existing);
        // Then
        Assert.assertThat(result, Is.is(CoreMatchers.nullValue()));
    }
}

