/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.execution.dml;


import io.crate.test.integration.CrateUnitTest;
import java.util.BitSet;
import org.hamcrest.core.Is;
import org.junit.Test;


public class ShardResponseTest extends CrateUnitTest {
    @Test
    public void testMarkResponseItemsAndFailures() {
        ShardResponse shardResponse = new ShardResponse();
        shardResponse.add(0);
        shardResponse.add(1);
        shardResponse.add(2, new ShardResponse.Failure());
        BitSet bitSet = new BitSet();
        ShardResponse.markResponseItemsAndFailures(shardResponse, bitSet);
        assertThat(bitSet.get(0), Is.is(true));
        assertThat(bitSet.get(1), Is.is(true));
        assertThat(bitSet.get(2), Is.is(false));
    }
}

