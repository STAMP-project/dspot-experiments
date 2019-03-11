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
package com.thoughtworks.go.domain;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class IpAddressTest {
    @Test
    public void shouldParseValidIpAddressString() {
        Assert.assertThat(IpAddress.create("10.12.16.18").toString(), Matchers.is("10.12.16.18"));
    }

    @Test
    public void invalidIpAddress() {
        Assert.assertThat(IpAddress.create("").toString(), Matchers.is(""));
    }

    @Test
    public void shouldAcceptLegalValuesForIpAddresses() {
        Assert.assertThat(IpAddress.create("255.255.255.255").toString(), Matchers.is("255.255.255.255"));
    }

    @Test
    public void ipAddressComparator() {
        Assert.assertThat(IpAddress.create("10.12.34.20").compareTo(IpAddress.create("10.12.34.3")), Matchers.is(Matchers.greaterThan(0)));
        Assert.assertThat(IpAddress.create("10.12.34.20").compareTo(IpAddress.create("10.12.34.20")), Matchers.is(0));
        Assert.assertThat(IpAddress.create("112.12.34.20").compareTo(IpAddress.create("10.12.34.20")), Matchers.is(Matchers.greaterThan(0)));
        Assert.assertThat(IpAddress.create("10.12.34.20").compareTo(IpAddress.create("")), Matchers.is(Matchers.greaterThan(0)));
        Assert.assertThat(IpAddress.create("").compareTo(IpAddress.create("10.12.34.3")), Matchers.is(org.hamcrest.Matchers.lessThan(0)));
        Assert.assertThat(IpAddress.create("").compareTo(IpAddress.create("")), Matchers.is(org.hamcrest.Matchers.lessThan(0)));
        Assert.assertThat(IpAddress.create("8:8:8:8:8:8:8:8").compareTo(IpAddress.create("10.12.34.20")), Matchers.is(Matchers.greaterThan(0)));
    }
}

