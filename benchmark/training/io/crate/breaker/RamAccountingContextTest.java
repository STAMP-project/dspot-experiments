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
package io.crate.breaker;


import io.crate.test.integration.CrateUnitTest;
import org.elasticsearch.common.breaker.MemoryCircuitBreaker;
import org.hamcrest.Matchers;
import org.junit.Test;


public class RamAccountingContextTest extends CrateUnitTest {
    private long originalBufferSize;

    private RamAccountingContext ramAccountingContext;

    private MemoryCircuitBreaker breaker;

    @Test
    public void testTotalBytesIsNotResetOnClose() {
        ramAccountingContext.addBytes(20);
        ramAccountingContext.addBytesWithoutBreaking(10);
        ramAccountingContext.close();
        assertThat(ramAccountingContext.totalBytes(), Matchers.is(30L));
        assertThat(breaker.getUsed(), Matchers.is(0L));
    }

    @Test
    public void testTotalBytesIsResetAfterRelease() {
        ramAccountingContext.addBytes(20);
        ramAccountingContext.addBytesWithoutBreaking(10);
        ramAccountingContext.release();
        assertThat(ramAccountingContext.totalBytes(), Matchers.is(0L));
        assertThat(breaker.getUsed(), Matchers.is(0L));
    }

    @Test
    public void testTotalWhenAddingBytesAfterRelease() {
        ramAccountingContext.addBytes(20);
        ramAccountingContext.addBytesWithoutBreaking(10);
        ramAccountingContext.release();
        ramAccountingContext.addBytes(20);
        assertThat(ramAccountingContext.totalBytes(), Matchers.is(20L));
        assertThat(breaker.getUsed(), Matchers.is(20L));
    }

    @Test
    public void testMultipleReleases() {
        ramAccountingContext.addBytes(20);
        ramAccountingContext.release();
        ramAccountingContext.addBytes(10);
        ramAccountingContext.release();
        assertThat(ramAccountingContext.totalBytes(), Matchers.is(0L));
        assertThat(breaker.getUsed(), Matchers.is(0L));
    }
}

