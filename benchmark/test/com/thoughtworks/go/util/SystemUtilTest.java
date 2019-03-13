/**
 * ***********************GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
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
 * ************************GO-LICENSE-END**********************************
 */
package com.thoughtworks.go.util;


import java.net.InetAddress;
import java.net.UnknownHostException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class SystemUtilTest {
    @Test
    public void shouldReturnFalseIfAddressIsNotLocalFilteredByHostname() throws UnknownHostException {
        Assert.assertThat("Localhost (google.com with ip 127.0.0.1) should not be a local address.", SystemUtil.isLocalhost("google.com", "127.0.0.1"), Matchers.is(false));
    }

    @Test
    public void shouldDetermineIfAddressIsLocal() throws UnknownHostException {
        InetAddress local;
        try {
            local = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            local = InetAddress.getByName("localhost");
        }
        Assert.assertThat((("Localhost (" + (local.getHostName())) + ") should be a local address."), SystemUtil.isLocalhost(local.getHostAddress()), Matchers.is(true));
    }

    @Test
    public void shouldReturnFalseIfAddressIsNotLocal() throws UnknownHostException {
        String hostName = "hostThatNeverExists";
        Assert.assertThat((("Localhost (" + hostName) + ") should not be a local address."), SystemUtil.isLocalhost("8.8.8.8"), Matchers.is(false));
    }

    @Test
    public void shouldReturnFalseIfPortIsNotReachableOnLocalhost() throws Exception {
        Assert.assertThat(SystemUtil.isLocalhostReachable(9876), Matchers.is(false));
    }
}

