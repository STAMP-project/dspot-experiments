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
package org.apache.geode.internal.logging.log4j;


import AlertLevel.ERROR;
import AlertLevel.NONE;
import AlertLevel.SEVERE;
import AlertLevel.WARNING;
import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.internal.alerting.AlertingProvider;
import org.apache.geode.test.junit.categories.AlertingTest;
import org.apache.geode.test.junit.categories.LoggingTest;
import org.apache.logging.log4j.Level;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.mockito.Mockito;


/**
 * Unit tests for {@link AlertAppender}.
 */
@Category({ AlertingTest.class, LoggingTest.class })
public class AlertAppenderTest {
    private DistributedMember member;

    private AlertAppender alertAppender;

    private AlertingProvider asAlertingProvider;

    @Rule
    public TestName testName = new TestName();

    @Test
    public void alertListenersIsEmptyByDefault() {
        assertThat(alertAppender.getAlertListeners()).isEmpty();
    }

    @Test
    public void hasAlertListenerReturnsFalseByDefault() {
        asAlertingProvider.hasAlertListener(member, WARNING);
    }

    @Test
    public void addAlertListenerAddsListener() {
        asAlertingProvider.addAlertListener(member, WARNING);
        assertThat(alertAppender.getAlertListeners()).contains(new AlertListener(Level.WARN, member));
    }

    @Test
    public void hasAlertListenerReturnsTrueIfListenerExists() {
        asAlertingProvider.addAlertListener(member, WARNING);
        assertThat(asAlertingProvider.hasAlertListener(member, WARNING)).isTrue();
    }

    @Test
    public void removeAlertListenerDoesNothingByDefault() {
        asAlertingProvider.removeAlertListener(member);
        assertThat(alertAppender.getAlertListeners()).isEmpty();
    }

    @Test
    public void removeAlertListenerDoesNothingIfMemberDoesNotMatch() {
        asAlertingProvider.addAlertListener(member, WARNING);
        asAlertingProvider.removeAlertListener(Mockito.mock(DistributedMember.class));
        assertThat(asAlertingProvider.hasAlertListener(member, WARNING)).isTrue();
    }

    @Test
    public void removeAlertListenerRemovesListener() {
        asAlertingProvider.addAlertListener(member, WARNING);
        asAlertingProvider.removeAlertListener(member);
        assertThat(asAlertingProvider.hasAlertListener(member, WARNING)).isFalse();
    }

    @Test
    public void addAlertListenerWithAlertLevelNoneDoesNothing() {
        asAlertingProvider.addAlertListener(member, NONE);
        assertThat(alertAppender.getAlertListeners()).isEmpty();
    }

    @Test
    public void hasAlertListenerReturnsFalseIfAlertLevelIsNone() {
        asAlertingProvider.addAlertListener(member, WARNING);
        assertThat(asAlertingProvider.hasAlertListener(member, NONE)).isFalse();
    }

    @Test
    public void addAlertListenerOrdersByAscendingAlertLevel() {
        DistributedMember member1 = Mockito.mock(DistributedMember.class);
        DistributedMember member2 = Mockito.mock(DistributedMember.class);
        DistributedMember member3 = Mockito.mock(DistributedMember.class);
        asAlertingProvider.addAlertListener(member3, WARNING);
        asAlertingProvider.addAlertListener(member1, SEVERE);
        asAlertingProvider.addAlertListener(member2, ERROR);
        AlertListener listener1 = new AlertListener(Level.WARN, member3);
        AlertListener listener2 = new AlertListener(Level.ERROR, member2);
        AlertListener listener3 = new AlertListener(Level.FATAL, member1);
        assertThat(alertAppender.getAlertListeners()).containsExactly(listener1, listener2, listener3);
    }

    @Test
    public void removeAlertListenerMaintainsExistingOrder() {
        DistributedMember member1 = Mockito.mock(DistributedMember.class);
        DistributedMember member2 = Mockito.mock(DistributedMember.class);
        DistributedMember member3 = Mockito.mock(DistributedMember.class);
        asAlertingProvider.addAlertListener(member3, WARNING);
        asAlertingProvider.addAlertListener(member1, SEVERE);
        asAlertingProvider.addAlertListener(member2, ERROR);
        AlertListener listener1 = new AlertListener(Level.WARN, member3);
        AlertListener listener3 = new AlertListener(Level.FATAL, member1);
        assertThat(alertAppender.removeAlertListener(member2)).isTrue();
        assertThat(alertAppender.getAlertListeners()).containsExactly(listener1, listener3);
    }

    @Test
    public void addAlertListenerOrdersByDescendingAddIfAlertLevelMatches() {
        DistributedMember member1 = Mockito.mock(DistributedMember.class);
        DistributedMember member2 = Mockito.mock(DistributedMember.class);
        DistributedMember member3 = Mockito.mock(DistributedMember.class);
        asAlertingProvider.addAlertListener(member3, WARNING);
        asAlertingProvider.addAlertListener(member1, WARNING);
        asAlertingProvider.addAlertListener(member2, WARNING);
        AlertListener listener1 = new AlertListener(Level.WARN, member2);
        AlertListener listener2 = new AlertListener(Level.WARN, member1);
        AlertListener listener3 = new AlertListener(Level.WARN, member3);
        assertThat(alertAppender.getAlertListeners()).containsExactly(listener1, listener2, listener3);
    }
}

