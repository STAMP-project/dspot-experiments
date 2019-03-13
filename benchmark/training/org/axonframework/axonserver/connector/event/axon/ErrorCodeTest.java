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
package org.axonframework.axonserver.connector.event.axon;


import io.axoniq.axonserver.grpc.ErrorMessage;
import junit.framework.TestCase;
import org.axonframework.axonserver.connector.AxonServerException;
import org.axonframework.axonserver.connector.ErrorCode;
import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.common.AxonException;
import org.junit.Test;


/**
 * Author: marc
 */
public class ErrorCodeTest {
    @Test
    public void testConvert4002FromCodeAndMessage() {
        ErrorCode errorCode = ErrorCode.getFromCode("AXONIQ-4002");
        AxonException exception = errorCode.convert(ErrorMessage.newBuilder().setMessage("myMessage").build());
        TestCase.assertTrue((exception instanceof CommandExecutionException));
        TestCase.assertEquals("myMessage", exception.getMessage());
    }

    @Test
    public void testConvertUnknownFromCodeAndMessage() {
        ErrorCode errorCode = ErrorCode.getFromCode("????????");
        AxonException exception = errorCode.convert(ErrorMessage.newBuilder().setMessage("myMessage").build());
        TestCase.assertTrue((exception instanceof AxonServerException));
        TestCase.assertEquals("myMessage", exception.getMessage());
    }

    @Test
    public void testConvertWithoutSource() {
        RuntimeException exception = new RuntimeException("oops");
        AxonException axonException = ErrorCode.getFromCode("AXONIQ-4002").convert(exception);
        TestCase.assertEquals(exception.getMessage(), axonException.getMessage());
    }
}

