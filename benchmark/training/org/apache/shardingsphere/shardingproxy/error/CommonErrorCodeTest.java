/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.shardingsphere.shardingproxy.error;


import CommonErrorCode.CIRCUIT_BREAK_MODE;
import CommonErrorCode.UNKNOWN_EXCEPTION;
import CommonErrorCode.UNSUPPORTED_COMMAND;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class CommonErrorCodeTest {
    @Test
    public void assertCircuitBreakMode() {
        Assert.assertThat(CIRCUIT_BREAK_MODE.getErrorCode(), CoreMatchers.is(10000));
        Assert.assertThat(CIRCUIT_BREAK_MODE.getSqlState(), CoreMatchers.is("C10000"));
        Assert.assertThat(CIRCUIT_BREAK_MODE.getErrorMessage(), CoreMatchers.is("Circuit break mode is ON."));
    }

    @Test
    public void assertUnsupportedCommand() {
        Assert.assertThat(UNSUPPORTED_COMMAND.getErrorCode(), CoreMatchers.is(10001));
        Assert.assertThat(UNSUPPORTED_COMMAND.getSqlState(), CoreMatchers.is("C10001"));
        Assert.assertThat(UNSUPPORTED_COMMAND.getErrorMessage(), CoreMatchers.is("Unsupported command: [%s]"));
    }

    @Test
    public void assertUnknownException() {
        Assert.assertThat(UNKNOWN_EXCEPTION.getErrorCode(), CoreMatchers.is(10002));
        Assert.assertThat(UNKNOWN_EXCEPTION.getSqlState(), CoreMatchers.is("C10002"));
        Assert.assertThat(UNKNOWN_EXCEPTION.getErrorMessage(), CoreMatchers.is("Unknown exception: [%s]"));
    }
}

