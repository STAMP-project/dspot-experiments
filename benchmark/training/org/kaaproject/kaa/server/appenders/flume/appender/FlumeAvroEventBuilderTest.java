/**
 * Copyright 2014-2016 CyberVision, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaaproject.kaa.server.appenders.flume.appender;


import FlumeEventFormat.GENERIC;
import FlumeEventFormat.RECORDS_CONTAINER;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.flume.Event;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.common.dto.EndpointProfileDataDto;
import org.kaaproject.kaa.server.appenders.flume.config.gen.FlumeConfig;
import org.kaaproject.kaa.server.common.log.shared.appender.data.BaseLogEventPack;


public class FlumeAvroEventBuilderTest {
    private static final int EVENTS_COUNT = 5;

    private static final int SCHEMA_VERSION = 10;

    private static final String SERVER_PROFILE_SCHEMA_FILE = "server-profile-schema.avsc";

    private static final String SERVER_PROFILE_CONTENT_FILE = "server-profile-content.json";

    private BaseLogEventPack eventPack;

    private FlumeEventBuilder eventBuilder;

    private String appToken;

    private EndpointProfileDataDto profileDto;

    @Test
    public void generateFromAvroEventWithEmptyLogSchemaTest() {
        eventBuilder = new FlumeAvroEventBuilder();
        FlumeConfig flumeConfig = new FlumeConfig();
        flumeConfig.setFlumeEventFormat(RECORDS_CONTAINER);
        flumeConfig.setIncludeClientProfile(false);
        flumeConfig.setIncludeServerProfile(false);
        eventBuilder.init(flumeConfig);
        eventPack.setLogSchema(null);
        List<Event> events = eventBuilder.generateEvents(eventPack, null, appToken);
        Assert.assertNull(events);
    }

    @Test
    public void generateFromAvroEventWithEmptyLogEventsTest() throws IOException {
        eventBuilder = new FlumeAvroEventBuilder();
        FlumeConfig flumeConfig = new FlumeConfig();
        flumeConfig.setFlumeEventFormat(RECORDS_CONTAINER);
        flumeConfig.setIncludeClientProfile(false);
        flumeConfig.setIncludeServerProfile(false);
        eventBuilder.init(flumeConfig);
        eventPack = generateEventPack(new ArrayList<org.kaaproject.kaa.server.common.log.shared.appender.LogEvent>());
        List<Event> events = eventBuilder.generateEvents(eventPack, null, appToken);
        Assert.assertNull(events);
    }

    @Test
    public void generateFromAvroEventRecordsContainerTest() {
        eventBuilder = new FlumeAvroEventBuilder();
        FlumeConfig flumeConfig = new FlumeConfig();
        flumeConfig.setFlumeEventFormat(RECORDS_CONTAINER);
        flumeConfig.setIncludeClientProfile(false);
        flumeConfig.setIncludeServerProfile(true);
        eventBuilder.init(flumeConfig);
        List<Event> events = eventBuilder.generateEvents(eventPack, null, appToken);
        Assert.assertNotNull(events);
        Assert.assertEquals(1, events.size());
    }

    @Test(expected = RuntimeException.class)
    public void generateFromAvroEventRecordsContainerWithoutClientProfileTest() {
        eventBuilder = new FlumeAvroEventBuilder();
        FlumeConfig flumeConfig = new FlumeConfig();
        flumeConfig.setFlumeEventFormat(RECORDS_CONTAINER);
        flumeConfig.setIncludeClientProfile(true);
        flumeConfig.setIncludeServerProfile(true);
        eventBuilder.init(flumeConfig);
        List<Event> events = eventBuilder.generateEvents(eventPack, null, appToken);
        Assert.assertNull(events);
    }

    @Test
    public void generateFromAvroEventGenericTest() {
        eventBuilder = new FlumeAvroEventBuilder();
        FlumeConfig flumeConfig = new FlumeConfig();
        flumeConfig.setFlumeEventFormat(GENERIC);
        flumeConfig.setIncludeClientProfile(false);
        flumeConfig.setIncludeServerProfile(false);
        eventBuilder.init(flumeConfig);
        List<Event> events = eventBuilder.generateEvents(eventPack, null, appToken);
        Assert.assertNotNull(events);
        Assert.assertEquals(FlumeAvroEventBuilderTest.EVENTS_COUNT, events.size());
    }
}

