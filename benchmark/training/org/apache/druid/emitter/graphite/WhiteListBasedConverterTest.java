/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.emitter.graphite;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import junitparams.JUnitParamsRunner;
import org.apache.commons.io.IOUtils;
import org.apache.druid.jackson.DefaultObjectMapper;
import org.apache.druid.java.util.common.DateTimes;
import org.apache.druid.java.util.emitter.service.ServiceMetricEvent;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(JUnitParamsRunner.class)
public class WhiteListBasedConverterTest {
    private final String prefix = "druid";

    private final WhiteListBasedConverter defaultWhiteListBasedConverter = new WhiteListBasedConverter(prefix, false, false, false, null, new DefaultObjectMapper());

    private ServiceMetricEvent event;

    private DateTime createdTime = DateTimes.nowUtc();

    private String hostname = "testHost.yahoo.com:8080";

    private String serviceName = "historical";

    private String defaultNamespace = ((((prefix) + ".") + (serviceName)) + ".") + (GraphiteEmitter.sanitize(hostname));

    @Test
    public void testWhiteListedStringArrayDimension() throws IOException {
        File mapFile = File.createTempFile(("testing-" + (System.nanoTime())), ".json");
        mapFile.deleteOnExit();
        try (OutputStream outputStream = new FileOutputStream(mapFile)) {
            IOUtils.copyLarge(getClass().getResourceAsStream("/testWhiteListedStringArrayDimension.json"), outputStream);
        }
        WhiteListBasedConverter converter = new WhiteListBasedConverter(prefix, false, false, false, mapFile.getAbsolutePath(), new DefaultObjectMapper());
        ServiceMetricEvent event = new ServiceMetricEvent.Builder().setDimension("gcName", new String[]{ "g1" }).build(createdTime, "jvm/gc/cpu", 10).build(serviceName, hostname);
        GraphiteEvent graphiteEvent = converter.druidEventToGraphite(event);
        Assert.assertNotNull(graphiteEvent);
        Assert.assertEquals(((defaultNamespace) + ".g1.jvm/gc/cpu"), graphiteEvent.getEventPath());
    }
}

