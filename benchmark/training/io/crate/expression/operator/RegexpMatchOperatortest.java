/**
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial agreement.
 */
package io.crate.expression.operator;


import io.crate.metadata.CoordinatorTxnCtx;
import io.crate.metadata.TransactionContext;
import io.crate.test.integration.CrateUnitTest;
import org.hamcrest.Matchers;
import org.junit.Test;


public class RegexpMatchOperatortest extends CrateUnitTest {
    private TransactionContext txnCtx = CoordinatorTxnCtx.systemTransactionContext();

    @Test
    public void testNormalize() throws Exception {
        assertThat(regexpNormalize("", ""), Matchers.is(true));
        assertThat(regexpNormalize("abc", "a.c"), Matchers.is(true));
        assertThat(regexpNormalize("AbC", "a.c"), Matchers.is(false));
        assertThat(regexpNormalize("abbbbc", "a(b{1,4})c"), Matchers.is(true));
        assertThat(regexpNormalize("abc", "a~bc"), Matchers.is(false));
        assertThat(regexpNormalize("100 ?", "<10-101> ?|$"), Matchers.is(true));
    }

    @Test
    public void testNormalizeNull() throws Exception {
        assertThat(regexpNormalize(null, "foo"), Matchers.is(Matchers.nullValue()));
        assertThat(regexpNormalize("foo", null), Matchers.is(Matchers.nullValue()));
        assertThat(regexpNormalize(null, null), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void testEvaluate() throws Exception {
        assertThat(regexpEvaluate("foo bar", "([A-Z][^ ]+ ?){2}"), Matchers.is(false));// case-insensitive matching should fail

        assertThat(regexpEvaluate("Foo Bar", "([A-Z][^ ]+ ?){2}"), Matchers.is(true));
        assertThat(regexpEvaluate("1000 $", "(<1-9999>) $|?"), Matchers.is(true));
        assertThat(regexpEvaluate("10000 $", "(<1-9999>) $|?"), Matchers.is(false));
        assertThat(regexpEvaluate("", ""), Matchers.is(true));
    }

    @Test
    public void testEvaluateNull() throws Exception {
        assertThat(regexpEvaluate(null, "foo"), Matchers.is(Matchers.nullValue()));
        assertThat(regexpEvaluate("foo", null), Matchers.is(Matchers.nullValue()));
        assertThat(regexpEvaluate(null, null), Matchers.is(Matchers.nullValue()));
    }
}

