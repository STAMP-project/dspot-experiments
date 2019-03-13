/**
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.hadoop.util.unit;


import org.hamcrest.MatcherAssert;
import org.junit.Test;


/**
 *
 */
public class ByteSizeUnitTests {
    @Test
    public void testBytes() {
        MatcherAssert.assertThat(BYTES.toBytes(1), equalTo(1L));
        MatcherAssert.assertThat(BYTES.toKB(1024), equalTo(1L));
        MatcherAssert.assertThat(BYTES.toMB((1024 * 1024)), equalTo(1L));
        MatcherAssert.assertThat(BYTES.toGB(((1024 * 1024) * 1024)), equalTo(1L));
    }

    @Test
    public void testKB() {
        MatcherAssert.assertThat(KB.toBytes(1), equalTo(1024L));
        MatcherAssert.assertThat(KB.toKB(1), equalTo(1L));
        MatcherAssert.assertThat(KB.toMB(1024), equalTo(1L));
        MatcherAssert.assertThat(KB.toGB((1024 * 1024)), equalTo(1L));
    }

    @Test
    public void testMB() {
        MatcherAssert.assertThat(MB.toBytes(1), equalTo((1024L * 1024)));
        MatcherAssert.assertThat(MB.toKB(1), equalTo(1024L));
        MatcherAssert.assertThat(MB.toMB(1), equalTo(1L));
        MatcherAssert.assertThat(MB.toGB(1024), equalTo(1L));
    }

    @Test
    public void testGB() {
        MatcherAssert.assertThat(GB.toBytes(1), equalTo(((1024L * 1024) * 1024)));
        MatcherAssert.assertThat(GB.toKB(1), equalTo((1024L * 1024)));
        MatcherAssert.assertThat(GB.toMB(1), equalTo(1024L));
        MatcherAssert.assertThat(GB.toGB(1), equalTo(1L));
    }
}

