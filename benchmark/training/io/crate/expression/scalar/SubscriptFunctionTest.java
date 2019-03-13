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
package io.crate.expression.scalar;


import DataTypes.INTEGER;
import com.google.common.collect.ImmutableSet;
import io.crate.exceptions.ConversionException;
import io.crate.expression.symbol.Literal;
import io.crate.testing.SymbolMatchers;
import io.crate.types.DataTypes;
import org.junit.Test;


public class SubscriptFunctionTest extends AbstractScalarFunctionsTest {
    @Test
    public void testEvaluate() throws Exception {
        assertNormalize("subscript(['Youri', 'Ruben'], cast(1 as integer))", SymbolMatchers.isLiteral("Youri"));
    }

    @Test
    public void testNormalizeSymbol() throws Exception {
        assertNormalize("subscript(tags, cast(1 as integer))", SymbolMatchers.isFunction("subscript"));
    }

    @Test
    public void testIndexOutOfRange() throws Exception {
        assertNormalize("subscript(['Youri', 'Ruben'], cast(3 as integer))", SymbolMatchers.isLiteral(null));
    }

    @Test
    public void testNotRegisteredForSets() throws Exception {
        expectedException.expect(ConversionException.class);
        expectedException.expectMessage("Cannot cast long_set to type undefined_array");
        assertEvaluate("subscript(long_set, a)", 3L, Literal.of(ImmutableSet.of(3L, 7L), new io.crate.types.SetType(DataTypes.LONG)), Literal.of(INTEGER, 1));
    }

    @Test
    public void testIndexExpressionIsNotInteger() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Array literals can only be accessed via numeric index");
        assertNormalize("subscript(['Youri', 'Ruben'], '1')", SymbolMatchers.isLiteral("Ruben"));
    }
}

