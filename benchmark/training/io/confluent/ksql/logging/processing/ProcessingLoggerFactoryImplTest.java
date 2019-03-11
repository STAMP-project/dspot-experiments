/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.logging.processing;


import com.google.common.collect.ImmutableList;
import io.confluent.common.logging.StructuredLogger;
import io.confluent.common.logging.StructuredLoggerFactory;
import java.util.Collection;
import java.util.function.BiFunction;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ProcessingLoggerFactoryImplTest {
    @Mock
    private StructuredLoggerFactory innerFactory;

    @Mock
    private StructuredLogger innerLogger;

    @Mock
    private ProcessingLogConfig config;

    @Mock
    private BiFunction<ProcessingLogConfig, StructuredLogger, ProcessingLogger> loggerFactory;

    @Mock
    private ProcessingLogger logger;

    private final Collection<String> loggers = ImmutableList.of("logger1", "logger2");

    private ProcessingLoggerFactoryImpl factory;

    @Test
    public void shouldCreateLogger() {
        // When:
        final ProcessingLogger logger = factory.getLogger("foo.bar");
        // Then:
        Assert.assertThat(logger, Matchers.is(this.logger));
        Mockito.verify(innerFactory).getLogger("foo.bar");
        Mockito.verify(loggerFactory).apply(config, innerLogger);
    }

    @Test
    public void shouldGetLoggers() {
        // When:
        final Collection<String> loggers = factory.getLoggers();
        // Then:
        Assert.assertThat(loggers, Matchers.equalTo(this.loggers));
    }
}

