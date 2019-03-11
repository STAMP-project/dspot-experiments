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


import akka.actor.ActorContext;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kaaproject.kaa.common.dto.ApplicationDto;
import org.kaaproject.kaa.server.common.dao.ApplicationService;
import org.kaaproject.kaa.server.common.dao.CtlService;
import org.kaaproject.kaa.server.common.log.shared.appender.LogAppender;
import org.kaaproject.kaa.server.common.log.shared.appender.LogEventPack;
import org.kaaproject.kaa.server.common.log.shared.appender.LogSchema;
import org.kaaproject.kaa.server.operations.service.akka.AkkaContext;
import org.kaaproject.kaa.server.operations.service.akka.actors.core.ApplicationLogActorMessageProcessor.VoidCallback;
import org.kaaproject.kaa.server.operations.service.akka.messages.core.logs.AbstractActorCallback;
import org.kaaproject.kaa.server.operations.service.akka.messages.core.logs.LogEventPackMessage;
import org.kaaproject.kaa.server.operations.service.akka.messages.core.logs.MultiLogDeliveryCallback;
import org.kaaproject.kaa.server.operations.service.akka.messages.core.logs.SingleLogDeliveryCallback;
import org.kaaproject.kaa.server.operations.service.cache.CacheService;
import org.kaaproject.kaa.server.operations.service.logs.LogAppenderService;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 *
 *
 * @author Bohdan Khablenko
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ AbstractActorCallback.class, SingleLogDeliveryCallback.class, MultiLogDeliveryCallback.class })
public class ApplicationLogActorMessageProcessorTest {
    public static final String DEFAULT_FQN = "org.kaaproject.kaa.ctl.TestSchema";

    private static final String APPLICATION_ID = "application_id";

    private static final String APPLICATION_TOKEN = "application_token";

    private static final String ENDPOINT_ID = "endpoint_id";

    private static final String ENDPOINT_KEY = "endpoint_key";

    private static final int LOG_SCHEMA_VERSION = 1;

    private static final int CLIENT_PROFILE_SCHEMA_ID = 100;

    private static final String CLIENT_PROFILE_SCHEMA_CTL_SCHEMA_ID = "1000";

    private static final int SERVER_PROFILE_SCHEMA_ID = 200;

    private static final String SERVER_PROFILE_SCHEMA_CTL_SCHEMA_ID = "2000";

    private static final int REQUEST_ID = 100;

    private static final int REQUIRED_APPENDERS_COUNT = 2;

    private AkkaContext context;

    private ApplicationDto application;

    private ApplicationService applicationService;

    private LogSchema logSchema;

    private LogAppender[] required;

    private LogAppender optional;

    private List<LogAppender> logAppenders;

    private LogAppenderService logAppenderService;

    private CacheService cacheService;

    private CtlService ctlService;

    private LogEventPackMessage message;

    /**
     * A test to ensure that the endpoint receives a response when there is
     * exactly one log appender that requires delivery confirmation. In such
     * case, the actor uses {@link SingleLogDeliveryCallback}
     */
    @Test
    public void withSingleRequiredDeliveryConfirmationTest() throws Exception {
        logAppenders.add(required[0]);
        logAppenders.add(optional);
        SingleLogDeliveryCallback callback = Mockito.spy(new SingleLogDeliveryCallback(message.getOriginator(), message.getRequestId()));
        PowerMockito.whenNew(SingleLogDeliveryCallback.class).withArguments(Mockito.any(), Mockito.anyInt()).thenReturn(callback);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                callback.onSuccess();
                return null;
            }
        }).when(required[0]).doAppend(Mockito.any(LogEventPack.class), Mockito.any(SingleLogDeliveryCallback.class));
        ApplicationLogActorMessageProcessor messageProcessor = new ApplicationLogActorMessageProcessor(context, ApplicationLogActorMessageProcessorTest.APPLICATION_TOKEN);
        messageProcessor.processLogEventPack(Mockito.mock(ActorContext.class), message);
        PowerMockito.verifyPrivate(callback).invoke("sendSuccessToEndpoint");
    }

    /**
     * A test to ensure that the endpoint receives a response when there are
     * multiple log appenders that require delivery confirmation. In such case,
     * the actor uses an instance of {@link MultiLogDeliveryCallback}
     */
    @Test
    public void withMultipleRequiredDeliveryConfirmationsTest() throws Exception {
        Arrays.stream(required).forEach(logAppenders::add);
        MultiLogDeliveryCallback object = new MultiLogDeliveryCallback(message.getOriginator(), message.getRequestId(), logAppenders.size());
        MultiLogDeliveryCallback callback = Mockito.spy(object);
        PowerMockito.whenNew(MultiLogDeliveryCallback.class).withArguments(Mockito.any(), Mockito.anyInt(), Mockito.anyInt()).thenReturn(callback);
        Arrays.stream(required).forEach(( appender) -> {
            Mockito.doAnswer(new Answer<Void>() {
                @Override
                public Void answer(InvocationOnMock invocation) throws Throwable {
                    callback.onSuccess();
                    return null;
                }
            }).when(appender).doAppend(Mockito.any(.class), Mockito.any(.class));
        });
        ApplicationLogActorMessageProcessor messageProcessor = new ApplicationLogActorMessageProcessor(context, ApplicationLogActorMessageProcessorTest.APPLICATION_TOKEN);
        messageProcessor.processLogEventPack(Mockito.mock(ActorContext.class), message);
        PowerMockito.verifyPrivate(callback).invoke("sendSuccessToEndpoint");
    }

    /**
     * A test to ensure that the endpoint receives a response even when there
     * are no log appenders that require delivery confirmation.
     */
    @Test
    public void withoutRequiredDeliveryConfirmationsTest() throws Exception {
        logAppenders.add(optional);
        ApplicationLogActorMessageProcessor messageProcessor = Mockito.spy(new ApplicationLogActorMessageProcessor(context, ApplicationLogActorMessageProcessorTest.APPLICATION_TOKEN));
        messageProcessor.processLogEventPack(Mockito.mock(ActorContext.class), message);
        Mockito.verify(optional).doAppend(Mockito.eq(message.getLogEventPack()), Mockito.any(VoidCallback.class));
        Mockito.verify(messageProcessor).sendSuccessMessageToEndpoint(message);
    }
}

