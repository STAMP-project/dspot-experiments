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
package io.crate.expression.symbol;


import DataTypes.BOOLEAN;
import FunctionInfo.Type;
import com.carrotsearch.randomizedtesting.RandomizedTest;
import com.google.common.collect.ImmutableList;
import io.crate.test.integration.CrateUnitTest;
import io.crate.testing.TestingHelpers;
import java.util.Collections;
import org.elasticsearch.common.io.stream.BytesStreamOutput;
import org.elasticsearch.common.io.stream.StreamInput;
import org.hamcrest.Matchers;
import org.junit.Test;


public class FunctionTest extends CrateUnitTest {
    @Test
    public void testSerialization() throws Exception {
        Function fn = new Function(new io.crate.metadata.FunctionInfo(new io.crate.metadata.FunctionIdent(RandomizedTest.randomAsciiLettersOfLength(10), ImmutableList.of(BOOLEAN)), TestingHelpers.randomPrimitiveType(), Type.SCALAR, randomFeatures()), Collections.singletonList(TestingHelpers.createReference(RandomizedTest.randomAsciiLettersOfLength(2), BOOLEAN)));
        BytesStreamOutput output = new BytesStreamOutput();
        Symbols.toStream(fn, output);
        StreamInput input = output.bytes().streamInput();
        Function fn2 = ((Function) (Symbols.fromStream(input)));
        assertThat(fn, Matchers.is(Matchers.equalTo(fn2)));
        assertThat(fn.hashCode(), Matchers.is(fn2.hashCode()));
    }
}

