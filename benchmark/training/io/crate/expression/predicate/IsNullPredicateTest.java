/**
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
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
package io.crate.expression.predicate;


import DataTypes.STRING;
import IsNullPredicate.NAME;
import io.crate.expression.scalar.AbstractScalarFunctionsTest;
import io.crate.expression.symbol.Literal;
import io.crate.testing.SymbolMatchers;
import org.junit.Test;


public class IsNullPredicateTest extends AbstractScalarFunctionsTest {
    @Test
    public void testNormalizeSymbolFalse() throws Exception {
        assertNormalize("'a' is null", SymbolMatchers.isLiteral(false));
    }

    @Test
    public void testNormalizeSymbolTrue() throws Exception {
        assertNormalize("null is null", SymbolMatchers.isLiteral(true));
    }

    @Test
    public void testNormalizeReference() throws Exception {
        assertNormalize("name is null", SymbolMatchers.isFunction(NAME));
    }

    @Test
    public void testEvaluateWithInputThatReturnsNull() throws Exception {
        assertEvaluate("name is null", true, Literal.of(STRING, null));
    }
}

