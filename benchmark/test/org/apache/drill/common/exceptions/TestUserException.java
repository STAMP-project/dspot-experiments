/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.common.exceptions;


import ErrorType.CONNECTION;
import ErrorType.DATA_WRITE;
import ErrorType.SYSTEM;
import org.apache.drill.exec.proto.UserBitShared.DrillPBError;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test various use cases when creating user exceptions
 */
public class TestUserException {
    private static final Logger logger = LoggerFactory.getLogger("--ignore.as.this.is.for.testing.exceptions--");

    // make sure system exceptions are created properly
    @Test
    public void testBuildSystemException() {
        String message = "This is an exception";
        UserException uex = UserException.systemError(new Exception(new RuntimeException(message))).build(TestUserException.logger);
        Assert.assertTrue(uex.getOriginalMessage().contains(message));
        Assert.assertTrue(uex.getOriginalMessage().contains("RuntimeException"));
        DrillPBError error = uex.getOrCreatePBError(true);
        Assert.assertEquals(SYSTEM, error.getErrorType());
    }

    @Test
    public void testBuildUserExceptionWithMessage() {
        String message = "Test message";
        UserException uex = UserException.dataWriteError().message(message).build(TestUserException.logger);
        DrillPBError error = uex.getOrCreatePBError(false);
        Assert.assertEquals(DATA_WRITE, error.getErrorType());
        Assert.assertEquals(message, uex.getOriginalMessage());
    }

    @Test
    public void testBuildUserExceptionWithCause() {
        String message = "Test message";
        UserException uex = UserException.dataWriteError(new RuntimeException(message)).build(TestUserException.logger);
        DrillPBError error = uex.getOrCreatePBError(false);
        // cause message should be used
        Assert.assertEquals(DATA_WRITE, error.getErrorType());
        Assert.assertEquals(message, uex.getOriginalMessage());
    }

    @Test
    public void testBuildUserExceptionWithCauseAndMessage() {
        String messageA = "Test message A";
        String messageB = "Test message B";
        UserException uex = UserException.dataWriteError(new RuntimeException(messageA)).message(messageB).build(TestUserException.logger);
        DrillPBError error = uex.getOrCreatePBError(false);
        // passed message should override the cause message
        Assert.assertEquals(DATA_WRITE, error.getErrorType());
        Assert.assertFalse(error.getMessage().contains(messageA));// messageA should not be part of the context

        Assert.assertEquals(messageB, uex.getOriginalMessage());
    }

    @Test
    public void testBuildUserExceptionWithUserExceptionCauseAndMessage() {
        String messageA = "Test message A";
        String messageB = "Test message B";
        UserException original = UserException.connectionError().message(messageA).build(TestUserException.logger);
        UserException uex = UserException.dataWriteError(wrap(original, 5)).message(messageB).build(TestUserException.logger);
        // builder should return the unwrapped original user exception and not build a new one
        Assert.assertEquals(original, uex);
        DrillPBError error = uex.getOrCreatePBError(false);
        Assert.assertEquals(messageA, uex.getOriginalMessage());
        Assert.assertFalse(error.getMessage().contains(messageB));// messageB should not be part of the context

    }

    @Test
    public void testBuildUserExceptionWithFormattedMessage() {
        String format = "This is test #%d";
        UserException uex = UserException.connectionError().message(format, 5).build(TestUserException.logger);
        DrillPBError error = uex.getOrCreatePBError(false);
        Assert.assertEquals(CONNECTION, error.getErrorType());
        Assert.assertEquals(String.format(format, 5), uex.getOriginalMessage());
    }

    // make sure wrapped user exceptions are retrieved properly when calling ErrorHelper.wrap()
    @Test
    public void testWrapUserException() {
        UserException uex = UserException.dataReadError().message("this is a data read exception").build(TestUserException.logger);
        Exception wrapped = wrap(uex, 3);
        Assert.assertEquals(uex, UserException.systemError(wrapped).build(TestUserException.logger));
    }
}

