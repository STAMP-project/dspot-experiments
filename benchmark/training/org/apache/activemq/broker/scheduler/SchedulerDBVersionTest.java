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
package org.apache.activemq.broker.scheduler;


import java.io.File;
import java.io.IOException;
import java.security.ProtectionDomain;
import org.apache.activemq.broker.BrokerService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SchedulerDBVersionTest {
    static String basedir;

    static {
        try {
            ProtectionDomain protectionDomain = SchedulerDBVersionTest.class.getProtectionDomain();
            SchedulerDBVersionTest.basedir = new File(new File(protectionDomain.getCodeSource().getLocation().getPath()), "../..").getCanonicalPath();
        } catch (IOException e) {
            SchedulerDBVersionTest.basedir = ".";
        }
    }

    static final Logger LOG = LoggerFactory.getLogger(SchedulerDBVersionTest.class);

    static final File VERSION_LEGACY_JMS = new File(((SchedulerDBVersionTest.basedir) + "/src/test/resources/org/apache/activemq/store/schedulerDB/legacy"));

    private BrokerService broker = null;

    @Test
    public void testLegacyStoreConversion() throws Exception {
        doTestScheduleRepeated(SchedulerDBVersionTest.VERSION_LEGACY_JMS);
    }
}

