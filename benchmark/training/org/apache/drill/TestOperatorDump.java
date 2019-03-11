/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill;


import ExternalSortBatch.INTERRUPTION_AFTER_SORT;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.drill.common.exceptions.UserRemoteException;
import org.apache.drill.exec.exception.OutOfMemoryException;
import org.apache.drill.exec.physical.impl.ScanBatch;
import org.apache.drill.exec.physical.impl.xsort.managed.ExternalSortBatch;
import org.apache.drill.exec.testing.Controls;
import org.apache.drill.exec.testing.ControlsInjectionUtil;
import org.apache.drill.test.ClusterTest;
import org.apache.drill.test.LogFixture;
import org.junit.Assert;
import org.junit.Test;


public class TestOperatorDump extends ClusterTest {
    private static final String ENTRY_DUMP_COMPLETED = "Batch dump completed";

    private static final String ENTRY_DUMP_STARTED = "Batch dump started";

    private LogFixture logFixture;

    private TestOperatorDump.EventAwareContextAppender appender;

    @Test(expected = UserRemoteException.class)
    public void testScanBatchChecked() throws Exception {
        String exceptionDesc = "next-allocate";
        final String controls = Controls.newBuilder().addException(ScanBatch.class, exceptionDesc, OutOfMemoryException.class, 0, 1).build();
        ControlsInjectionUtil.setControls(ClusterTest.client.client(), controls);
        String query = "select * from dfs.`multilevel/parquet` limit 100";
        try {
            ClusterTest.client.queryBuilder().sql(query).run();
        } catch (UserRemoteException e) {
            Assert.assertTrue(e.getMessage().contains(exceptionDesc));
            String[] expectedEntries = new String[]{ TestOperatorDump.ENTRY_DUMP_STARTED, TestOperatorDump.ENTRY_DUMP_COMPLETED };
            validateContainsEntries(expectedEntries, ScanBatch.class.getName());
            throw e;
        }
    }

    @Test(expected = UserRemoteException.class)
    public void testExternalSortUnchecked() throws Exception {
        Class<?> siteClass = ExternalSortBatch.class;
        final String controls = Controls.newBuilder().addException(siteClass, INTERRUPTION_AFTER_SORT, RuntimeException.class).build();
        ControlsInjectionUtil.setControls(ClusterTest.client.client(), controls);
        String query = "select n_name from cp.`tpch/lineitem.parquet` order by n_name";
        try {
            ClusterTest.client.queryBuilder().sql(query).run();
        } catch (UserRemoteException e) {
            Assert.assertTrue(e.getMessage().contains(INTERRUPTION_AFTER_SORT));
            String[] expectedEntries = new String[]{ TestOperatorDump.ENTRY_DUMP_STARTED, TestOperatorDump.ENTRY_DUMP_COMPLETED };
            validateContainsEntries(expectedEntries, ExternalSortBatch.class.getName());
            throw e;
        }
    }

    // ConsoleAppender which stores logged events
    private static class EventAwareContextAppender extends ConsoleAppender<ILoggingEvent> {
        private List<ILoggingEvent> events = new ArrayList<>();

        @Override
        protected void append(ILoggingEvent e) {
            events.add(e);
        }

        List<String> getMessages() {
            return events.stream().map(ILoggingEvent::getMessage).collect(Collectors.toList());
        }

        Set<String> getLoggerNames() {
            return events.stream().map(ILoggingEvent::getLoggerName).collect(Collectors.toSet());
        }
    }
}

