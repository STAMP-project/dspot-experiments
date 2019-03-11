/**
 * Copyright 2014-2019 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.aeron;


import ChannelUri.SPY_QUALIFIER;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class ChannelUriTest {
    @Test
    public void shouldParseSimpleDefaultUri() {
        assertParseWithMedia("aeron:udp", "udp");
        assertParseWithMedia("aeron:ipc", "ipc");
        assertParseWithMedia("aeron:", "");
        assertParseWithMediaAndPrefix("aeron-spy:aeron:ipc", "aeron-spy", "ipc");
    }

    @Test
    public void shouldRejectUriWithoutAeronPrefix() {
        ChannelUriTest.assertInvalid(":udp");
        ChannelUriTest.assertInvalid("aeron");
        ChannelUriTest.assertInvalid("aron:");
        ChannelUriTest.assertInvalid("eeron:");
    }

    @Test
    public void shouldRejectWithOutOfPlaceColon() {
        ChannelUriTest.assertInvalid("aeron:udp:");
    }

    @Test
    public void shouldParseWithSingleParameter() {
        assertParseWithParams("aeron:udp?endpoint=224.10.9.8", "endpoint", "224.10.9.8");
        assertParseWithParams("aeron:udp?add|ress=224.10.9.8", "add|ress", "224.10.9.8");
        assertParseWithParams("aeron:udp?endpoint=224.1=0.9.8", "endpoint", "224.1=0.9.8");
    }

    @Test
    public void shouldParseWithMultipleParameters() {
        assertParseWithParams("aeron:udp?endpoint=224.10.9.8|port=4567|interface=192.168.0.3|ttl=16", "endpoint", "224.10.9.8", "port", "4567", "interface", "192.168.0.3", "ttl", "16");
    }

    @Test
    public void shouldAllowReturnDefaultIfParamNotSpecified() {
        final ChannelUri uri = ChannelUri.parse("aeron:udp?endpoint=224.10.9.8");
        Assert.assertThat(uri.get("interface"), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(uri.get("interface", "192.168.0.0"), CoreMatchers.is("192.168.0.0"));
    }

    @Test
    public void shouldRoundTripToString() {
        final String uriString = "aeron:udp?endpoint=224.10.9.8:777";
        final ChannelUri uri = ChannelUri.parse(uriString);
        final String result = uri.toString();
        Assert.assertThat(result, CoreMatchers.is(uriString));
    }

    @Test
    public void shouldRoundTripToStringBuilder() {
        final ChannelUriStringBuilder builder = new ChannelUriStringBuilder().media("udp").endpoint("224.10.9.8:777");
        final String uriString = builder.build();
        final ChannelUri uri = ChannelUri.parse(uriString);
        Assert.assertThat(uri.toString(), CoreMatchers.is(uriString));
    }

    @Test
    public void shouldRoundTripToStringBuilderWithPrefix() {
        final ChannelUriStringBuilder builder = new ChannelUriStringBuilder().prefix(SPY_QUALIFIER).media("udp").endpoint("224.10.9.8:777");
        final String uriString = builder.build();
        final ChannelUri uri = ChannelUri.parse(uriString);
        Assert.assertThat(uri.toString(), CoreMatchers.is(uriString));
    }
}

