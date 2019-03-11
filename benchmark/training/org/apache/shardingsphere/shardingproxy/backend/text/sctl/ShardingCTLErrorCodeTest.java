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
package org.apache.shardingsphere.shardingproxy.backend.text.sctl;


import ShardingCTLErrorCode.INVALID_FORMAT;
import ShardingCTLErrorCode.UNSUPPORTED_TYPE;
import org.apache.shardingsphere.shardingproxy.backend.text.sctl.exception.InvalidShardingCTLFormatException;
import org.apache.shardingsphere.shardingproxy.backend.text.sctl.exception.ShardingCTLException;
import org.apache.shardingsphere.shardingproxy.backend.text.sctl.exception.UnsupportedShardingCTLTypeException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public final class ShardingCTLErrorCodeTest {
    @Test
    public void assertInvalidFormat() {
        Assert.assertThat(INVALID_FORMAT.getErrorCode(), CoreMatchers.is(11000));
        Assert.assertThat(INVALID_FORMAT.getSqlState(), CoreMatchers.is("S11000"));
        Assert.assertThat(INVALID_FORMAT.getErrorMessage(), CoreMatchers.is("Invalid format for sharding ctl [%s], should be [sctl:set key=value]."));
    }

    @Test
    public void assertUnsupportedType() {
        Assert.assertThat(UNSUPPORTED_TYPE.getErrorCode(), CoreMatchers.is(11001));
        Assert.assertThat(UNSUPPORTED_TYPE.getSqlState(), CoreMatchers.is("S11001"));
        Assert.assertThat(UNSUPPORTED_TYPE.getErrorMessage(), CoreMatchers.is("Could not support sctl type [%s]."));
    }

    @Test
    public void assertValueOfWithInvalidFormat() {
        Assert.assertThat(ShardingCTLErrorCode.valueOf(new InvalidShardingCTLFormatException("test")), CoreMatchers.is(INVALID_FORMAT));
    }

    @Test
    public void assertValueOfWithUnsupportedType() {
        Assert.assertThat(ShardingCTLErrorCode.valueOf(new UnsupportedShardingCTLTypeException("test")), CoreMatchers.is(UNSUPPORTED_TYPE));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void assertValueOfWithUnsupportedUnsupportedOperationException() {
        ShardingCTLErrorCode.valueOf(Mockito.mock(ShardingCTLException.class));
    }
}

