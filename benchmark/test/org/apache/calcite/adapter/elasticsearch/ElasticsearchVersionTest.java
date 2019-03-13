/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.calcite.adapter.elasticsearch;


import ElasticsearchVersion.ES2;
import ElasticsearchVersion.ES5;
import ElasticsearchVersion.ES6;
import ElasticsearchVersion.ES7;
import ElasticsearchVersion.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


/**
 * Basic tests for parsing ES version in different formats
 */
public class ElasticsearchVersionTest {
    @Test
    public void versions() {
        Assert.assertEquals(ElasticsearchVersion.fromString("2.3.4"), ES2);
        Assert.assertEquals(ElasticsearchVersion.fromString("2.0.0"), ES2);
        Assert.assertEquals(ElasticsearchVersion.fromString("5.6.1"), ES5);
        Assert.assertEquals(ElasticsearchVersion.fromString("6.0.1"), ES6);
        Assert.assertEquals(ElasticsearchVersion.fromString("7.0.1"), ES7);
        Assert.assertEquals(ElasticsearchVersion.fromString("111.0.1"), UNKNOWN);
        Assert.assertEquals(ElasticsearchVersion.fromString("2020.12.12"), UNKNOWN);
        ElasticsearchVersionTest.assertFails("");
        ElasticsearchVersionTest.assertFails(".");
        ElasticsearchVersionTest.assertFails(".1.2");
        ElasticsearchVersionTest.assertFails("1.2");
        ElasticsearchVersionTest.assertFails("0");
        ElasticsearchVersionTest.assertFails("b");
        ElasticsearchVersionTest.assertFails("a.b");
        ElasticsearchVersionTest.assertFails("aa");
        ElasticsearchVersionTest.assertFails("a.b.c");
        ElasticsearchVersionTest.assertFails("2.2");
        ElasticsearchVersionTest.assertFails("a.2");
        ElasticsearchVersionTest.assertFails("2.2.0a");
        ElasticsearchVersionTest.assertFails("2a.2.0");
    }
}

/**
 * End ElasticsearchVersionTest.java
 */
