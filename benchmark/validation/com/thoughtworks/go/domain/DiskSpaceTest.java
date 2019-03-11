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


public class DiskSpaceTest {
    @Test
    public void shouldCompareDiskSpace() {
        Assert.assertThat(new DiskSpace(10L).compareTo(new DiskSpace(12L)), Matchers.is(org.hamcrest.Matchers.lessThan(0)));
        Assert.assertThat(DiskSpace.unknownDiskSpace().compareTo(new DiskSpace(12L)), Matchers.is(org.hamcrest.Matchers.lessThan(0)));
        Assert.assertThat(new DiskSpace(10L).compareTo(DiskSpace.unknownDiskSpace()), Matchers.is(Matchers.greaterThan(0)));
        Assert.assertThat(DiskSpace.unknownDiskSpace().compareTo(DiskSpace.unknownDiskSpace()), Matchers.is(0));
    }

    @Test
    public void shouldProduceHumanReadableStringRepresentation() {
        Assert.assertThat(new DiskSpace((((3 * 512) * 1024) * 1024L)).toString(), Matchers.is("1.5 GB"));
        Assert.assertThat(new DiskSpace((((10 * 1024) * 1024) * 1024L)).toString(), Matchers.is("10.0 GB"));
        Assert.assertThat(DiskSpace.unknownDiskSpace().toString(), Matchers.is("Unknown"));
    }
}

