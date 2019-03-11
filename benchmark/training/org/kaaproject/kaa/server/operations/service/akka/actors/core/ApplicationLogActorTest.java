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
package org.kaaproject.kaa.server.operations.service.akka.actors.core;


import Operation.ADD_LOG_APPENDER;
import Operation.REMOVE_LOG_APPENDER;
import Operation.UPDATE_LOG_APPENDER;
import akka.actor.ActorContext;
import java.util.List;
import org.junit.Test;
import org.kaaproject.kaa.common.dto.ApplicationDto;
import org.kaaproject.kaa.server.common.dao.ApplicationService;
import org.kaaproject.kaa.server.common.dao.CtlService;
import org.kaaproject.kaa.server.common.log.shared.appender.LogAppender;
import org.kaaproject.kaa.server.common.log.shared.appender.LogDeliveryCallback;
import org.kaaproject.kaa.server.common.log.shared.appender.LogEventPack;
import org.kaaproject.kaa.server.common.log.shared.appender.LogSchema;
import org.kaaproject.kaa.server.common.log.shared.appender.data.BaseLogEventPack;
import org.kaaproject.kaa.server.common.thrift.gen.operations.Notification;
import org.kaaproject.kaa.server.operations.service.akka.AkkaContext;
import org.kaaproject.kaa.server.operations.service.akka.messages.core.logs.LogEventPackMessage;
import org.kaaproject.kaa.server.operations.service.cache.AppVersionKey;
import org.kaaproject.kaa.server.operations.service.cache.CacheService;
import org.kaaproject.kaa.server.operations.service.logs.LogAppenderService;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ApplicationLogActorTest {
    private static final String CLIENT_PROFILE_CTL_SCHEMA_ID = "144";

    private static final String SERVER_PROFILE_CTL_SCHEMA_ID = "173";

    private static final int CLIENT_SCHEMA_VERSION = 42;

    private static final int SERVER_SCHEMA_VERSION = 33;

    private static final int REQUEST_ID = 42;

    private static final int TEST_SCHEMA_VERSION = 1;

    private static final String APPLICATION_ID = "application_id";

    private static final String APPENDER_ID = "appender_id";

    private static final String APP_TOKEN = "app_token";

    private static final int LOG_SCHEMA_VERSION = 1;

    private ApplicationLogActorMessageProcessor applicationLogActorMessageProcessor;

    private AkkaContext context;

    private LogAppenderService logAppenderService;

    private ApplicationService applicationService;

    private CacheService cacheService;

    private CtlService ctlService;

    private ApplicationDto applicationDto;

    private List<LogAppender> logAppenders;

    private LogSchema logSchema;

    private LogAppender logAppender;

    @Test
    public void proccessLogSchemaVersionMessageHaveLogSchemaTest() throws Exception {
        applicationLogActorMessageProcessor = new ApplicationLogActorMessageProcessor(context, ApplicationLogActorTest.APP_TOKEN);
        LogEventPackMessage logEventPackMessage = buildTestMessage(ApplicationLogActorTest.LOG_SCHEMA_VERSION);
        applicationLogActorMessageProcessor.processLogEventPack(Mockito.mock(ActorContext.class), logEventPackMessage);
        Mockito.verify(logAppender).doAppend(Mockito.any(LogEventPack.class), Mockito.any(LogDeliveryCallback.class));
    }

    @Test
    public void proccessLogSchemaVersionNotSupported() throws Exception {
        applicationLogActorMessageProcessor = new ApplicationLogActorMessageProcessor(context, ApplicationLogActorTest.APP_TOKEN);
        LogEventPackMessage logEventPackMessage = buildTestMessage(ApplicationLogActorTest.LOG_SCHEMA_VERSION, logSchema);
        Mockito.when(logAppender.isSchemaVersionSupported(1)).thenReturn(Boolean.FALSE);
        applicationLogActorMessageProcessor.processLogEventPack(Mockito.mock(ActorContext.class), logEventPackMessage);
        Mockito.verify(logAppender, Mockito.never()).doAppend(Mockito.any(LogEventPack.class), Mockito.any(LogDeliveryCallback.class));
    }

    @Test
    public void proccessLogSchemaVersionLogShemasNoSchemasTest() throws Exception {
        applicationLogActorMessageProcessor = new ApplicationLogActorMessageProcessor(context, ApplicationLogActorTest.APP_TOKEN);
        LogEventPackMessage logEventPackMessage = buildTestMessage(ApplicationLogActorTest.LOG_SCHEMA_VERSION);
        applicationLogActorMessageProcessor.processLogEventPack(Mockito.mock(ActorContext.class), logEventPackMessage);
        Mockito.verify(cacheService).getProfileSchemaByAppAndVersion(new AppVersionKey(ApplicationLogActorTest.APP_TOKEN, ApplicationLogActorTest.CLIENT_SCHEMA_VERSION));
        Mockito.verify(logAppenderService).getLogSchema(ApplicationLogActorTest.APPLICATION_ID, ApplicationLogActorTest.LOG_SCHEMA_VERSION);
        Mockito.verify(logAppender).doAppend(Mockito.any(LogEventPack.class), Mockito.any(LogDeliveryCallback.class));
    }

    @Test
    public void processAddLogAppenderNotificationTest() {
        LogAppender mockAppender = Mockito.mock(LogAppender.class);
        Mockito.when(mockAppender.getName()).thenReturn("flume");
        Mockito.when(mockAppender.getAppenderId()).thenReturn(ApplicationLogActorTest.APPENDER_ID);
        Mockito.when(mockAppender.isSchemaVersionSupported(Mockito.anyInt())).thenReturn(Boolean.TRUE);
        Notification notification = new Notification();
        notification.setAppenderId(ApplicationLogActorTest.APPENDER_ID);
        notification.setAppId(ApplicationLogActorTest.APPLICATION_ID);
        notification.setOp(ADD_LOG_APPENDER);
        logAppenders.clear();
        applicationLogActorMessageProcessor = new ApplicationLogActorMessageProcessor(context, ApplicationLogActorTest.APP_TOKEN);
        Mockito.when(logAppenderService.getApplicationAppender(ApplicationLogActorTest.APPENDER_ID)).thenReturn(mockAppender);
        applicationLogActorMessageProcessor.processLogAppenderNotification(notification);
        LogEventPackMessage logEventPackMessage = buildTestMessage(ApplicationLogActorTest.LOG_SCHEMA_VERSION, logSchema);
        applicationLogActorMessageProcessor.processLogEventPack(Mockito.mock(ActorContext.class), logEventPackMessage);
        Mockito.verify(mockAppender).doAppend(Mockito.any(LogEventPack.class), Mockito.any(LogDeliveryCallback.class));
    }

    @Test
    public void processUpdateLogAppenderNotificationTest() {
        LogEventPackMessage logEventPackMessage = buildTestMessage(ApplicationLogActorTest.LOG_SCHEMA_VERSION, logSchema);
        LogAppender mockAppender = Mockito.mock(LogAppender.class);
        Mockito.when(mockAppender.getName()).thenReturn("flume");
        Mockito.when(mockAppender.getAppenderId()).thenReturn(ApplicationLogActorTest.APPENDER_ID);
        // new appender supports current log schema
        Mockito.when(mockAppender.isSchemaVersionSupported(Mockito.anyInt())).thenReturn(Boolean.TRUE);
        // old appender does not support current log schema
        Mockito.when(logAppender.isSchemaVersionSupported(Mockito.anyInt())).thenReturn(Boolean.FALSE);
        applicationLogActorMessageProcessor = new ApplicationLogActorMessageProcessor(context, ApplicationLogActorTest.APP_TOKEN);
        applicationLogActorMessageProcessor.processLogEventPack(Mockito.mock(ActorContext.class), logEventPackMessage);
        // check that log pack is not processed
        Mockito.verify(logAppender, Mockito.never()).doAppend(Mockito.any(BaseLogEventPack.class), Mockito.any(LogDeliveryCallback.class));
        Notification notification = new Notification();
        notification.setAppenderId(ApplicationLogActorTest.APPENDER_ID);
        notification.setAppId(ApplicationLogActorTest.APPLICATION_ID);
        notification.setOp(UPDATE_LOG_APPENDER);
        Mockito.when(logAppenderService.getApplicationAppender(ApplicationLogActorTest.APPENDER_ID)).thenReturn(mockAppender);
        applicationLogActorMessageProcessor.processLogAppenderNotification(notification);
        applicationLogActorMessageProcessor.processLogEventPack(Mockito.mock(ActorContext.class), logEventPackMessage);
        // check that log pack is processed
        Mockito.verify(mockAppender).doAppend(Mockito.any(LogEventPack.class), Mockito.any(LogDeliveryCallback.class));
    }

    @Test
    public void processRemoveLogAppenderNotificationTest() {
        LogEventPackMessage logEventPackMessage = buildTestMessage(ApplicationLogActorTest.LOG_SCHEMA_VERSION, logSchema);
        LogAppender mockAppender = Mockito.mock(LogAppender.class);
        Mockito.when(mockAppender.getName()).thenReturn("flume");
        Mockito.when(mockAppender.getAppenderId()).thenReturn(ApplicationLogActorTest.APPENDER_ID);
        // new appender supports current log schema
        Mockito.when(logAppender.isSchemaVersionSupported(Mockito.anyInt())).thenReturn(Boolean.TRUE);
        applicationLogActorMessageProcessor = new ApplicationLogActorMessageProcessor(context, ApplicationLogActorTest.APP_TOKEN);
        applicationLogActorMessageProcessor.processLogEventPack(Mockito.mock(ActorContext.class), logEventPackMessage);
        // check that log pack is not processed
        Mockito.verify(logAppender).doAppend(Mockito.any(LogEventPack.class), Mockito.any(LogDeliveryCallback.class));
        Notification notification = new Notification();
        notification.setAppenderId(ApplicationLogActorTest.APPENDER_ID);
        notification.setAppId(ApplicationLogActorTest.APPLICATION_ID);
        notification.setOp(REMOVE_LOG_APPENDER);
        Mockito.when(logAppenderService.getApplicationAppender(ApplicationLogActorTest.APPENDER_ID)).thenReturn(mockAppender);
        applicationLogActorMessageProcessor.processLogAppenderNotification(notification);
        applicationLogActorMessageProcessor.processLogEventPack(Mockito.mock(ActorContext.class), logEventPackMessage);
        // check that log pack is processed
        Mockito.verify(mockAppender, Mockito.never()).doAppend(Mockito.any(LogEventPack.class), Mockito.any(LogDeliveryCallback.class));
    }
}

