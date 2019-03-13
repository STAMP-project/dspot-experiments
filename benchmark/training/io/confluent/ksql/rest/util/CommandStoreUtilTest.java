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
package io.confluent.ksql.rest.util;


import Code.SERVICE_UNAVAILABLE;
import io.confluent.ksql.rest.entity.KsqlErrorMessageMatchers;
import io.confluent.ksql.rest.entity.KsqlRequest;
import io.confluent.ksql.rest.server.computation.CommandQueue;
import io.confluent.ksql.rest.server.resources.KsqlRestException;
import io.confluent.ksql.rest.server.resources.KsqlRestExceptionMatchers;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class CommandStoreUtilTest {
    private static final Duration TIMEOUT = Duration.ofMillis(5000L);

    private static final long SEQUENCE_NUMBER = 2;

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Mock
    private CommandQueue commandQueue;

    @Mock
    private KsqlRequest request;

    @Test
    public void shouldNotWaitIfNoSequenceNumberSpecified() throws Exception {
        // Given:
        Mockito.when(request.getCommandSequenceNumber()).thenReturn(Optional.empty());
        // When:
        CommandStoreUtil.waitForCommandSequenceNumber(commandQueue, request, CommandStoreUtilTest.TIMEOUT);
        // Then:
        Mockito.verify(commandQueue, Mockito.never()).ensureConsumedPast(ArgumentMatchers.anyLong(), ArgumentMatchers.any());
    }

    @Test
    public void shouldWaitIfSequenceNumberSpecified() throws Exception {
        // Given:
        Mockito.when(request.getCommandSequenceNumber()).thenReturn(Optional.of(CommandStoreUtilTest.SEQUENCE_NUMBER));
        // When:
        CommandStoreUtil.waitForCommandSequenceNumber(commandQueue, request, CommandStoreUtilTest.TIMEOUT);
        // Then:
        Mockito.verify(commandQueue).ensureConsumedPast(CommandStoreUtilTest.SEQUENCE_NUMBER, CommandStoreUtilTest.TIMEOUT);
    }

    @Test
    public void shouldThrowKsqlRestExceptionOnTimeout() throws Exception {
        // Given:
        Mockito.when(request.getCommandSequenceNumber()).thenReturn(Optional.of(CommandStoreUtilTest.SEQUENCE_NUMBER));
        Mockito.doThrow(new TimeoutException("uh oh")).when(commandQueue).ensureConsumedPast(CommandStoreUtilTest.SEQUENCE_NUMBER, CommandStoreUtilTest.TIMEOUT);
        // Expect:
        expectedException.expect(KsqlRestException.class);
        expectedException.expect(KsqlRestExceptionMatchers.exceptionStatusCode(CoreMatchers.is(SERVICE_UNAVAILABLE)));
        expectedException.expect(KsqlRestExceptionMatchers.exceptionErrorMessage(KsqlErrorMessageMatchers.errorMessage(Matchers.containsString("Timed out while waiting for a previous command to execute"))));
        expectedException.expect(KsqlRestExceptionMatchers.exceptionErrorMessage(KsqlErrorMessageMatchers.errorMessage(Matchers.containsString("command sequence number: 2"))));
        // When:
        CommandStoreUtil.httpWaitForCommandSequenceNumber(commandQueue, request, CommandStoreUtilTest.TIMEOUT);
    }
}

