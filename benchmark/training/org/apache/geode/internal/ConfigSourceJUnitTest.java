/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal;


import org.junit.Assert;
import org.junit.Test;


public class ConfigSourceJUnitTest {
    @Test
    public void testDescriptions() {
        ConfigSource cs = ConfigSource.api();
        Assert.assertEquals(cs.getDescription(), "api");
        cs = ConfigSource.file("test", true);
        Assert.assertEquals(cs.getDescription(), "test");
        cs = ConfigSource.file("test2", false);
        Assert.assertEquals(cs.getDescription(), "test2");
        cs = ConfigSource.file(null, true);
        Assert.assertEquals(cs.getDescription(), "secure file");
        cs = ConfigSource.file(null, false);
        Assert.assertEquals(cs.getDescription(), "file");
        cs = ConfigSource.file("", true);
        Assert.assertEquals(cs.getDescription(), "");
        cs = ConfigSource.launcher();
        Assert.assertEquals(cs.getDescription(), "launcher");
        cs = ConfigSource.sysprop();
        Assert.assertEquals(cs.getDescription(), "system property");
        cs = ConfigSource.runtime();
        Assert.assertEquals(cs.getDescription(), "runtime modification");
        cs = ConfigSource.xml();
        Assert.assertEquals(cs.getDescription(), "cache.xml");
    }

    @Test
    public void testEquals() {
        ConfigSource cs1 = ConfigSource.file("name", true);
        ConfigSource cs2 = ConfigSource.file("name", false);
        Assert.assertFalse(cs1.equals(cs2));
        cs1 = ConfigSource.file("name", true);
        cs2 = ConfigSource.file("name", true);
        Assert.assertTrue(cs1.equals(cs2));
        cs1 = ConfigSource.file(null, true);
        cs2 = ConfigSource.file(null, false);
        Assert.assertFalse(cs1.equals(cs2));
        cs1 = ConfigSource.file(null, true);
        cs2 = ConfigSource.file(null, true);
        Assert.assertTrue(cs1.equals(cs2));
        cs1 = ConfigSource.file(null, true);
        cs2 = ConfigSource.file("", true);
        Assert.assertFalse(cs1.equals(cs2));
        cs1 = ConfigSource.xml();
        cs2 = ConfigSource.xml();
        Assert.assertTrue(cs1.equals(cs2));
    }
}

