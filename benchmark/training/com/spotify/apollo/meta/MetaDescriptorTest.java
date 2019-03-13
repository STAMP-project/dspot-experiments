/**
 * -\-\-
 * Spotify Apollo API Implementations
 * --
 * Copyright (C) 2013 - 2015 Spotify AB
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
package com.spotify.apollo.meta;


import org.junit.Assert;
import org.junit.Test;


/* Copyright (c) 2015 Spotify AB */
public class MetaDescriptorTest {
    @Test
    public void testLoadApolloVersion() throws Exception {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        String version = MetaDescriptor.loadApolloVersion(classLoader);
        Assert.assertTrue(version.startsWith("1."));
    }
}

