/**
 * -
 * -\-\-
 * Helios Client
 * --
 * Copyright (C) 2016 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package com.spotify.helios.common;


import Status.COMPATIBLE;
import Status.EQUAL;
import Status.INCOMPATIBLE;
import Status.MAYBE;
import org.junit.Assert;
import org.junit.Test;


public class VersionCompatibilityTest {
    @Test
    public void test() {
        final PomVersion server = PomVersion.parse("1.3.9");
        Assert.assertEquals(EQUAL, VersionCompatibility.getStatus(server, server));
        Assert.assertEquals(COMPATIBLE, VersionCompatibility.getStatus(server, PomVersion.parse("1.3.10")));
        Assert.assertEquals(COMPATIBLE, VersionCompatibility.getStatus(server, PomVersion.parse("1.2.8")));
        Assert.assertEquals(MAYBE, VersionCompatibility.getStatus(server, PomVersion.parse("1.4.8")));
        Assert.assertEquals(INCOMPATIBLE, VersionCompatibility.getStatus(server, PomVersion.parse("9.0.0")));
    }
}

