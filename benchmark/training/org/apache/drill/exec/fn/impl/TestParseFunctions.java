/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.fn.impl;


import org.apache.drill.categories.SqlFunctionTest;
import org.apache.drill.test.ClusterTest;
import org.apache.drill.test.TestBuilder;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(SqlFunctionTest.class)
public class TestParseFunctions extends ClusterTest {
    @Test
    public void testParseQueryFunction() throws Exception {
        testBuilder().sqlQuery("select parse_query(url) parameters from dfs.`nullable_urls.json`").unOrdered().baselineColumns("parameters").baselineValues(TestBuilder.mapOf("a", "12", "b", "someValue")).baselineValues(TestBuilder.mapOf()).baselineValues(TestBuilder.mapOf("p1", "v1", "p2", "v=2")).go();
    }

    @Test
    public void testParseUrlFunction() throws Exception {
        testBuilder().sqlQuery("select parse_url(url) data from dfs.`nullable_urls.json`").unOrdered().baselineColumns("data").baselineValues(TestBuilder.mapOf("protocol", "ftp", "authority", "somewhere.com:3190", "host", "somewhere.com", "path", "/someFile", "query", "a=12&b=someValue", "filename", "/someFile?a=12&b=someValue", "port", 3190)).baselineValues(TestBuilder.mapOf()).baselineValues(TestBuilder.mapOf("protocol", "http", "authority", "someUrl", "host", "someUrl", "path", "", "query", "p1=v1&p2=v=2&", "filename", "?p1=v1&p2=v=2&")).go();
    }
}

