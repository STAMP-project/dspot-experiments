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


import ByteSizeUnit.BYTES;
import ByteSizeUnit.GB;
import ByteSizeUnit.KB;
import ByteSizeUnit.MB;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static ByteSizeUnit.BYTES;
import static ByteSizeUnit.GB;
import static ByteSizeUnit.KB;
import static ByteSizeUnit.MB;


public class ByteSizeValueTests {
    @Test
    public void testActual() {
        MatcherAssert.assertThat(bytes(), equalTo((((((long) (4)) * 1024) * 1024) * 1024)));
    }

    @Test
    public void testSimple() {
        MatcherAssert.assertThat(BYTES.toBytes(10), is(bytes()));
        MatcherAssert.assertThat(KB.toKB(10), is(kb()));
        MatcherAssert.assertThat(MB.toMB(10), is(mb()));
        MatcherAssert.assertThat(GB.toGB(10), is(gb()));
    }

    @Test
    public void testToString() {
        MatcherAssert.assertThat("10b", is(new ByteSizeValue(10, BYTES).toString()));
        MatcherAssert.assertThat("1.5kb", is(new ByteSizeValue(((long) (1024 * 1.5)), BYTES).toString()));
        MatcherAssert.assertThat("1.5mb", is(new ByteSizeValue(((long) (1024 * 1.5)), KB).toString()));
        MatcherAssert.assertThat("1.5gb", is(new ByteSizeValue(((long) (1024 * 1.5)), MB).toString()));
        MatcherAssert.assertThat("1536gb", is(new ByteSizeValue(((long) (1024 * 1.5)), GB).toString()));
    }
}

