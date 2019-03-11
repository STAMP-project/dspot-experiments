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
package org.apache.hadoop.yarn.server.timelineservice.collector;


import YarnConfiguration.TIMELINE_SERVICE_WRITER_CLASS;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.apache.hadoop.yarn.server.timelineservice.storage.FileSystemTimelineWriterImpl;
import org.apache.hadoop.yarn.server.timelineservice.storage.TimelineWriter;
import org.junit.Test;


/**
 * Unit tests for TimelineCollectorManager.
 */
public class TestTimelineCollectorManager {
    @Test(timeout = 60000, expected = YarnRuntimeException.class)
    public void testTimelineCollectorManagerWithInvalidTimelineWriter() {
        Configuration conf = new YarnConfiguration();
        conf.set(TIMELINE_SERVICE_WRITER_CLASS, Object.class.getName());
        TestTimelineCollectorManager.runTimelineCollectorManagerWithConfig(conf);
    }

    @Test(timeout = 60000, expected = YarnRuntimeException.class)
    public void testTimelineCollectorManagerWithNonexistentTimelineWriter() {
        String nonexistentTimelineWriterClass = "org.apache.org.yarn.server." + "timelineservice.storage.XXXXXXXX";
        Configuration conf = new YarnConfiguration();
        conf.set(TIMELINE_SERVICE_WRITER_CLASS, nonexistentTimelineWriterClass);
        TestTimelineCollectorManager.runTimelineCollectorManagerWithConfig(conf);
    }

    @Test(timeout = 60000)
    public void testTimelineCollectorManagerWithFileSystemWriter() {
        Configuration conf = new YarnConfiguration();
        conf.setClass(TIMELINE_SERVICE_WRITER_CLASS, FileSystemTimelineWriterImpl.class, TimelineWriter.class);
        TestTimelineCollectorManager.runTimelineCollectorManagerWithConfig(conf);
    }
}

