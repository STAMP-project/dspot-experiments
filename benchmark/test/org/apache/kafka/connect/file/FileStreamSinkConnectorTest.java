/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.connect.file;


import FileStreamSinkConnector.FILE_CONFIG;
import java.util.List;
import java.util.Map;
import org.apache.kafka.common.config.ConfigValue;
import org.apache.kafka.connect.connector.ConnectorContext;
import org.easymock.EasyMockSupport;
import org.junit.Assert;
import org.junit.Test;


public class FileStreamSinkConnectorTest extends EasyMockSupport {
    private static final String MULTIPLE_TOPICS = "test1,test2";

    private static final String FILENAME = "/afilename";

    private FileStreamSinkConnector connector;

    private ConnectorContext ctx;

    private Map<String, String> sinkProperties;

    @Test
    public void testConnectorConfigValidation() {
        replayAll();
        List<ConfigValue> configValues = connector.config().validate(sinkProperties);
        for (ConfigValue val : configValues) {
            Assert.assertEquals(("Config property errors: " + (val.errorMessages())), 0, val.errorMessages().size());
        }
        verifyAll();
    }

    @Test
    public void testSinkTasks() {
        replayAll();
        connector.start(sinkProperties);
        List<Map<String, String>> taskConfigs = connector.taskConfigs(1);
        Assert.assertEquals(1, taskConfigs.size());
        Assert.assertEquals(FileStreamSinkConnectorTest.FILENAME, taskConfigs.get(0).get(FILE_CONFIG));
        taskConfigs = connector.taskConfigs(2);
        Assert.assertEquals(2, taskConfigs.size());
        for (int i = 0; i < 2; i++) {
            Assert.assertEquals(FileStreamSinkConnectorTest.FILENAME, taskConfigs.get(0).get(FILE_CONFIG));
        }
        verifyAll();
    }

    @Test
    public void testSinkTasksStdout() {
        replayAll();
        sinkProperties.remove(FileStreamSourceConnector.FILE_CONFIG);
        connector.start(sinkProperties);
        List<Map<String, String>> taskConfigs = connector.taskConfigs(1);
        Assert.assertEquals(1, taskConfigs.size());
        Assert.assertNull(taskConfigs.get(0).get(FileStreamSourceConnector.FILE_CONFIG));
        verifyAll();
    }

    @Test
    public void testTaskClass() {
        replayAll();
        connector.start(sinkProperties);
        Assert.assertEquals(FileStreamSinkTask.class, connector.taskClass());
        verifyAll();
    }
}

