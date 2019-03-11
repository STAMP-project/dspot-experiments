/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.messaging.interceptors;


import java.lang.reflect.Field;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.axonframework.messaging.InterceptorChain;
import org.axonframework.messaging.Message;
import org.axonframework.messaging.unitofwork.UnitOfWork;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.impl.Log4jLoggerAdapter;


/**
 *
 *
 * @author Allard Buijze
 * @author Nakul Mishra
 */
@SuppressWarnings({ "ThrowableResultOfMethodCallIgnored" })
public class LoggingInterceptorTest {
    private LoggingInterceptor<Message<?>> testSubject;

    private Logger mockLogger;

    private InterceptorChain interceptorChain;

    private UnitOfWork<Message<?>> unitOfWork;

    @Test
    public void testIncomingLogging_NullReturnValue() throws Exception {
        Mockito.when(mockLogger.isInfoEnabled()).thenReturn(true);
        Mockito.when(interceptorChain.proceed()).thenReturn(null);
        testSubject.handle(unitOfWork, interceptorChain);
        Mockito.verify(mockLogger, Mockito.atLeast(1)).isInfoEnabled();
        Mockito.verify(mockLogger, Mockito.times(2)).log(ArgumentMatchers.any(String.class), ArgumentMatchers.any(Priority.class), ArgumentMatchers.contains("[StubMessage]"), ArgumentMatchers.any());
        Mockito.verify(mockLogger).log(ArgumentMatchers.any(String.class), ArgumentMatchers.any(Priority.class), AdditionalMatchers.and(ArgumentMatchers.contains("[StubMessage]"), ArgumentMatchers.contains("[null]")), ArgumentMatchers.any());
        Mockito.verifyNoMoreInteractions(mockLogger);
    }

    @Test
    public void testSuccessfulExecution_VoidReturnValue() throws Exception {
        Mockito.when(mockLogger.isInfoEnabled()).thenReturn(true);
        Mockito.when(interceptorChain.proceed()).thenReturn(null);
        testSubject.handle(unitOfWork, interceptorChain);
        Mockito.verify(mockLogger, Mockito.atLeast(1)).isInfoEnabled();
        Mockito.verify(mockLogger, Mockito.times(2)).log(ArgumentMatchers.any(String.class), ArgumentMatchers.any(Priority.class), ArgumentMatchers.contains("[StubMessage]"), ArgumentMatchers.any());
        Mockito.verify(mockLogger).log(ArgumentMatchers.any(String.class), ArgumentMatchers.any(Priority.class), AdditionalMatchers.and(ArgumentMatchers.contains("[StubMessage]"), ArgumentMatchers.contains("[null]")), ArgumentMatchers.any());
        Mockito.verifyNoMoreInteractions(mockLogger);
    }

    @Test
    public void testSuccessfulExecution_CustomReturnValue() throws Exception {
        Mockito.when(interceptorChain.proceed()).thenReturn(new LoggingInterceptorTest.StubResponse());
        Mockito.when(mockLogger.isInfoEnabled()).thenReturn(true);
        testSubject.handle(unitOfWork, interceptorChain);
        Mockito.verify(mockLogger, Mockito.atLeast(1)).isInfoEnabled();
        Mockito.verify(mockLogger).log(ArgumentMatchers.anyString(), ArgumentMatchers.eq(Level.INFO), AdditionalMatchers.and(ArgumentMatchers.contains("[StubMessage]"), ArgumentMatchers.contains("[StubResponse]")), ArgumentMatchers.any());
    }

    @SuppressWarnings({ "ThrowableInstanceNeverThrown" })
    @Test
    public void testFailedExecution() throws Exception {
        RuntimeException exception = new RuntimeException();
        Mockito.when(interceptorChain.proceed()).thenThrow(exception);
        Mockito.when(mockLogger.isInfoEnabled()).thenReturn(true);
        try {
            testSubject.handle(unitOfWork, interceptorChain);
            Assert.fail("Expected exception to be propagated");
        } catch (RuntimeException e) {
            // expected
        }
        Mockito.verify(mockLogger).log(ArgumentMatchers.any(String.class), ArgumentMatchers.eq(Level.WARN), AdditionalMatchers.and(ArgumentMatchers.contains("[StubMessage]"), ArgumentMatchers.contains("failed")), ArgumentMatchers.eq(exception));
    }

    @Test
    public void testConstructorWithCustomLogger() throws Exception {
        testSubject = new LoggingInterceptor("my.custom.logger");
        Field field = testSubject.getClass().getDeclaredField("logger");
        field.setAccessible(true);
        Log4jLoggerAdapter logger = ((Log4jLoggerAdapter) (field.get(testSubject)));
        Assert.assertEquals("my.custom.logger", logger.getName());
    }

    private static class StubMessage {}

    private static class StubResponse {}
}

