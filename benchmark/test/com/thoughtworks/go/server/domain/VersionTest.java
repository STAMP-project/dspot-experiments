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
package com.thoughtworks.go.server.domain;


import org.junit.Assert;
import org.junit.Test;


public class VersionTest {
    @Test
    public void shouldCompareVersions() throws Exception {
        Assert.assertTrue(new Version("13.1=20").isAtHigherVersionComparedTo(new Version("13.1=19")));
        Assert.assertTrue(new Version("13.999=20").isAtHigherVersionComparedTo(new Version("13.1=19")));
        Assert.assertTrue(new Version("14.1=20").isAtHigherVersionComparedTo(new Version("13.1=19")));
        Assert.assertTrue(new Version("14.1=   20    ").isAtHigherVersionComparedTo(new Version("13.1=19")));
    }

    @Test
    public void shouldFailDuringParsingOfIllegalVersionContent() throws Exception {
        assertIllegal("13.1=19;");
        assertIllegal("13.1=abc;");
        assertIllegal("13.1=");
        assertIllegal("13.1= ");
        assertIllegal("13.1");
        assertIllegal("abc=abc=123");
    }
}

