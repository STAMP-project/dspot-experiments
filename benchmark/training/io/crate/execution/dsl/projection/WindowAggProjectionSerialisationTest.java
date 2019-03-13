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
package io.crate.execution.dsl.projection;


import io.crate.analyze.WindowDefinition;
import io.crate.expression.symbol.Literal;
import io.crate.expression.symbol.Symbol;
import io.crate.expression.symbol.WindowFunction;
import io.crate.metadata.FunctionImplementation;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.elasticsearch.common.io.stream.BytesStreamOutput;
import org.elasticsearch.common.io.stream.StreamInput;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class WindowAggProjectionSerialisationTest {
    @Test
    public void testWindowAggProjectionSerialisation() throws IOException {
        FunctionImplementation sumFunctionImpl = getSumFunction();
        WindowDefinition partitionByOneWindowDef = new WindowDefinition(Collections.singletonList(Literal.of(1L)), null, null);
        WindowDefinition partitionByTwoWindowDef = new WindowDefinition(Collections.singletonList(Literal.of(2L)), null, null);
        WindowFunction firstWindowFunction = new WindowFunction(sumFunctionImpl.info(), Arrays.asList(Literal.of(1L)), partitionByOneWindowDef);
        WindowFunction secondWindowFunction = new WindowFunction(sumFunctionImpl.info(), Arrays.asList(Literal.of(2L)), partitionByTwoWindowDef);
        LinkedHashMap<WindowFunction, List<Symbol>> functionsWithInputs = new LinkedHashMap<>(2, 1.0F);
        functionsWithInputs.put(firstWindowFunction, Arrays.asList(Literal.of(1L)));
        functionsWithInputs.put(secondWindowFunction, Arrays.asList(Literal.of(2L)));
        WindowAggProjection windowAggProjection = new WindowAggProjection(partitionByOneWindowDef, functionsWithInputs, Collections.singletonList(Literal.of(42L)), new int[0]);
        BytesStreamOutput output = new BytesStreamOutput();
        windowAggProjection.writeTo(output);
        StreamInput input = output.bytes().streamInput();
        WindowAggProjection fromInput = new WindowAggProjection(input);
        Map<WindowFunction, List<Symbol>> deserialisedFunctionsByWindow = fromInput.functionsWithInputs();
        Assert.assertThat(deserialisedFunctionsByWindow, Matchers.equalTo(functionsWithInputs));
    }
}

