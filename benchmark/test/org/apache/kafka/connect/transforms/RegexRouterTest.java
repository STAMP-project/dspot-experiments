/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.connect.transforms;


import org.junit.Assert;
import org.junit.Test;


public class RegexRouterTest {
    @Test
    public void staticReplacement() {
        Assert.assertEquals("bar", RegexRouterTest.apply("foo", "bar", "foo"));
    }

    @Test
    public void doesntMatch() {
        Assert.assertEquals("orig", RegexRouterTest.apply("foo", "bar", "orig"));
    }

    @Test
    public void identity() {
        Assert.assertEquals("orig", RegexRouterTest.apply("(.*)", "$1", "orig"));
    }

    @Test
    public void addPrefix() {
        Assert.assertEquals("prefix-orig", RegexRouterTest.apply("(.*)", "prefix-$1", "orig"));
    }

    @Test
    public void addSuffix() {
        Assert.assertEquals("orig-suffix", RegexRouterTest.apply("(.*)", "$1-suffix", "orig"));
    }

    @Test
    public void slice() {
        Assert.assertEquals("index", RegexRouterTest.apply("(.*)-(\\d\\d\\d\\d\\d\\d\\d\\d)", "$1", "index-20160117"));
    }
}

