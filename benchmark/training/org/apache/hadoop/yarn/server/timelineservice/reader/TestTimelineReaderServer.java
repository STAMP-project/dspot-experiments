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
package org.apache.hadoop.yarn.server.timelineservice.reader;


import STATE.INITED;
import STATE.STARTED;
import STATE.STOPPED;
import YarnConfiguration.TIMELINE_SERVICE_ENABLED;
import YarnConfiguration.TIMELINE_SERVICE_READER_CLASS;
import YarnConfiguration.TIMELINE_SERVICE_READER_WEBAPP_ADDRESS;
import YarnConfiguration.TIMELINE_SERVICE_VERSION;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.apache.hadoop.yarn.server.timelineservice.storage.FileSystemTimelineReaderImpl;
import org.apache.hadoop.yarn.server.timelineservice.storage.TimelineReader;
import org.junit.Assert;
import org.junit.Test;


public class TestTimelineReaderServer {
    @Test(timeout = 60000)
    public void testStartStopServer() throws Exception {
        @SuppressWarnings("resource")
        TimelineReaderServer server = new TimelineReaderServer();
        Configuration config = new YarnConfiguration();
        config.setBoolean(TIMELINE_SERVICE_ENABLED, true);
        config.setFloat(TIMELINE_SERVICE_VERSION, 2.0F);
        config.set(TIMELINE_SERVICE_READER_WEBAPP_ADDRESS, "localhost:0");
        config.setClass(TIMELINE_SERVICE_READER_CLASS, FileSystemTimelineReaderImpl.class, TimelineReader.class);
        try {
            server.init(config);
            Assert.assertEquals(INITED, server.getServiceState());
            Assert.assertEquals(2, server.getServices().size());
            server.start();
            Assert.assertEquals(STARTED, server.getServiceState());
            server.stop();
            Assert.assertEquals(STOPPED, server.getServiceState());
        } finally {
            server.stop();
        }
    }

    @Test(timeout = 60000, expected = YarnRuntimeException.class)
    public void testTimelineReaderServerWithInvalidTimelineReader() {
        Configuration conf = new YarnConfiguration();
        conf.setBoolean(TIMELINE_SERVICE_ENABLED, true);
        conf.setFloat(TIMELINE_SERVICE_VERSION, 2.0F);
        conf.set(TIMELINE_SERVICE_READER_WEBAPP_ADDRESS, "localhost:0");
        conf.set(TIMELINE_SERVICE_READER_CLASS, Object.class.getName());
        TestTimelineReaderServer.runTimelineReaderServerWithConfig(conf);
    }

    @Test(timeout = 60000, expected = YarnRuntimeException.class)
    public void testTimelineReaderServerWithNonexistentTimelineReader() {
        String nonexistentTimelineReaderClass = "org.apache.org.yarn.server." + "timelineservice.storage.XXXXXXXX";
        Configuration conf = new YarnConfiguration();
        conf.setBoolean(TIMELINE_SERVICE_ENABLED, true);
        conf.setFloat(TIMELINE_SERVICE_VERSION, 2.0F);
        conf.set(TIMELINE_SERVICE_READER_WEBAPP_ADDRESS, "localhost:0");
        conf.set(TIMELINE_SERVICE_READER_CLASS, nonexistentTimelineReaderClass);
        TestTimelineReaderServer.runTimelineReaderServerWithConfig(conf);
    }
}

