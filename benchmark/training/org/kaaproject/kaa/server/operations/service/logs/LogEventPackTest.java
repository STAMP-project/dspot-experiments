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
package org.kaaproject.kaa.server.operations.service.logs;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.common.dto.EndpointProfileDataDto;
import org.kaaproject.kaa.common.dto.logs.LogSchemaDto;
import org.kaaproject.kaa.server.common.log.shared.appender.LogEvent;
import org.kaaproject.kaa.server.common.log.shared.appender.LogSchema;
import org.kaaproject.kaa.server.common.log.shared.appender.data.BaseLogEventPack;


public class LogEventPackTest {
    private static final String ENDPOINT_KEY = "endpoint key";

    private static final long DATE_CREATED = System.currentTimeMillis();

    private static final LogSchema LOG_SCHEMA = new LogSchema(new LogSchemaDto(), "");

    private static final int VERSION = 3;

    private static final List<LogEvent> EVENTS = new ArrayList<>();

    private static final String userId = "123454567878";

    @Test
    public void basicLogEventPackTest() {
        EndpointProfileDataDto profileDto = new EndpointProfileDataDto("1", LogEventPackTest.ENDPOINT_KEY, 1, "", 0, "");
        BaseLogEventPack logEventPack1 = new BaseLogEventPack(profileDto, LogEventPackTest.DATE_CREATED, LogEventPackTest.VERSION, LogEventPackTest.EVENTS);
        logEventPack1.setLogSchema(LogEventPackTest.LOG_SCHEMA);
        logEventPack1.setUserId(LogEventPackTest.userId);
        Assert.assertEquals(LogEventPackTest.ENDPOINT_KEY, logEventPack1.getEndpointKey());
        Assert.assertEquals(LogEventPackTest.DATE_CREATED, logEventPack1.getDateCreated());
        Assert.assertEquals(LogEventPackTest.LOG_SCHEMA, logEventPack1.getLogSchema());
        Assert.assertEquals(LogEventPackTest.VERSION, logEventPack1.getLogSchemaVersion());
        Assert.assertEquals(LogEventPackTest.EVENTS, logEventPack1.getEvents());
    }
}

