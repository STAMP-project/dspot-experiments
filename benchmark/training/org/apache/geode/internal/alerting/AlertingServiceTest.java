/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.alerting;


import AlertLevel.WARNING;
import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.test.junit.categories.AlertingTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Unit tests for {@link AlertingService}.
 */
@Category(AlertingTest.class)
public class AlertingServiceTest {
    private AlertingProviderRegistry registry;

    private AlertingProvider provider;

    private DistributedMember member;

    private AlertingService alertingService;

    @Test
    public void hasAlertListenerDelegates() {
        assertThat(alertingService.hasAlertListener(member, WARNING)).isTrue();
        Mockito.verify(provider).hasAlertListener(ArgumentMatchers.eq(member), ArgumentMatchers.eq(WARNING));
    }

    @Test
    public void addAlertListenerDelegates() {
        alertingService.addAlertListener(member, WARNING);
        Mockito.verify(provider).addAlertListener(ArgumentMatchers.eq(member), ArgumentMatchers.eq(WARNING));
    }

    @Test
    public void removeAlertListenerDelegates() {
        assertThat(alertingService.removeAlertListener(member)).isTrue();
        Mockito.verify(provider).removeAlertListener(ArgumentMatchers.eq(member));
    }
}

