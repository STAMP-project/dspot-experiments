/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.application.process;


import ClusterHealthStatus.GREEN;
import ClusterHealthStatus.RED;
import ClusterHealthStatus.YELLOW;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import java.util.ArrayList;
import java.util.List;
import org.elasticsearch.client.transport.NoNodeAvailableException;
import org.elasticsearch.discovery.MasterNotDiscoveredException;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.sonar.application.es.EsConnector;
import org.sonar.process.ProcessId;


public class EsProcessMonitorTest {
    @Test
    public void isOperational_should_return_false_if_Elasticsearch_is_RED() {
        EsConnector esConnector = Mockito.mock(EsConnector.class);
        Mockito.when(esConnector.getClusterHealthStatus()).thenReturn(RED);
        EsProcessMonitor underTest = new EsProcessMonitor(Mockito.mock(Process.class), ProcessId.ELASTICSEARCH, esConnector);
        assertThat(underTest.isOperational()).isFalse();
    }

    @Test
    public void isOperational_should_return_true_if_Elasticsearch_is_YELLOW() {
        EsConnector esConnector = Mockito.mock(EsConnector.class);
        Mockito.when(esConnector.getClusterHealthStatus()).thenReturn(YELLOW);
        EsProcessMonitor underTest = new EsProcessMonitor(Mockito.mock(Process.class), ProcessId.ELASTICSEARCH, esConnector);
        assertThat(underTest.isOperational()).isTrue();
    }

    @Test
    public void isOperational_should_return_true_if_Elasticsearch_is_GREEN() {
        EsConnector esConnector = Mockito.mock(EsConnector.class);
        Mockito.when(esConnector.getClusterHealthStatus()).thenReturn(GREEN);
        EsProcessMonitor underTest = new EsProcessMonitor(Mockito.mock(Process.class), ProcessId.ELASTICSEARCH, esConnector);
        assertThat(underTest.isOperational()).isTrue();
    }

    @Test
    public void isOperational_should_return_true_if_Elasticsearch_was_GREEN_once() {
        EsConnector esConnector = Mockito.mock(EsConnector.class);
        Mockito.when(esConnector.getClusterHealthStatus()).thenReturn(GREEN);
        EsProcessMonitor underTest = new EsProcessMonitor(Mockito.mock(Process.class), ProcessId.ELASTICSEARCH, esConnector);
        assertThat(underTest.isOperational()).isTrue();
        Mockito.when(esConnector.getClusterHealthStatus()).thenReturn(RED);
        assertThat(underTest.isOperational()).isTrue();
    }

    @Test
    public void isOperational_should_retry_if_Elasticsearch_is_unreachable() {
        EsConnector esConnector = Mockito.mock(EsConnector.class);
        Mockito.when(esConnector.getClusterHealthStatus()).thenThrow(new NoNodeAvailableException("test")).thenReturn(GREEN);
        EsProcessMonitor underTest = new EsProcessMonitor(Mockito.mock(Process.class), ProcessId.ELASTICSEARCH, esConnector);
        assertThat(underTest.isOperational()).isTrue();
    }

    @Test
    public void isOperational_should_return_false_if_Elasticsearch_status_cannot_be_evaluated() {
        EsConnector esConnector = Mockito.mock(EsConnector.class);
        Mockito.when(esConnector.getClusterHealthStatus()).thenThrow(new RuntimeException("test"));
        EsProcessMonitor underTest = new EsProcessMonitor(Mockito.mock(Process.class), ProcessId.ELASTICSEARCH, esConnector);
        assertThat(underTest.isOperational()).isFalse();
    }

    @Test
    public void isOperational_must_log_once_when_master_is_not_elected() {
        EsProcessMonitorTest.MemoryAppender<ILoggingEvent> memoryAppender = new EsProcessMonitorTest.MemoryAppender<>();
        LoggerContext lc = ((LoggerContext) (LoggerFactory.getILoggerFactory()));
        lc.reset();
        memoryAppender.setContext(lc);
        memoryAppender.start();
        lc.getLogger(EsProcessMonitor.class).addAppender(memoryAppender);
        EsConnector esConnector = Mockito.mock(EsConnector.class);
        Mockito.when(esConnector.getClusterHealthStatus()).thenThrow(new MasterNotDiscoveredException("Master not elected -test-"));
        EsProcessMonitor underTest = new EsProcessMonitor(Mockito.mock(Process.class), ProcessId.ELASTICSEARCH, esConnector);
        assertThat(underTest.isOperational()).isFalse();
        assertThat(memoryAppender.events).isNotEmpty();
        assertThat(memoryAppender.events).extracting(ILoggingEvent::getLevel, ILoggingEvent::getMessage).containsOnlyOnce(tuple(Level.INFO, "Elasticsearch is waiting for a master to be elected. Did you start all the search nodes ?"));
        // Second call must not log another message
        assertThat(underTest.isOperational()).isFalse();
        assertThat(memoryAppender.events).extracting(ILoggingEvent::getLevel, ILoggingEvent::getMessage).containsOnlyOnce(tuple(Level.INFO, "Elasticsearch is waiting for a master to be elected. Did you start all the search nodes ?"));
    }

    private class MemoryAppender<E> extends AppenderBase<E> {
        private final List<E> events = new ArrayList();

        @Override
        protected void append(E eventObject) {
            events.add(eventObject);
        }
    }
}

