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
package io.crate.expression.operator.any;


import io.crate.expression.scalar.AbstractScalarFunctionsTest;
import io.crate.testing.SymbolMatchers;
import org.junit.Test;


public class AnyNotLikeOperatorTest extends AbstractScalarFunctionsTest {
    @Test
    public void testNormalizeSingleSymbolEqual() {
        assertNormalize("'foo' not like any (['foo'])", SymbolMatchers.isLiteral(false));
        assertNormalize("'notFoo' not like any (['foo'])", SymbolMatchers.isLiteral(true));
    }

    @Test
    public void testNormalizeSymbolLikeZeroOrMore() {
        // Following tests: wildcard: '%' ... zero or more characters (0...N)
        assertNormalize("'%bar' not like any (['foobar', 'bar'])", SymbolMatchers.isLiteral(false));
        assertNormalize("'%bar' not like any (['bar'])", SymbolMatchers.isLiteral(false));
        assertNormalize("'%bar' not like any (['ar', 'car'])", SymbolMatchers.isLiteral(true));
        assertNormalize("'foo%' not like any (['foobar', 'kuhbar'])", SymbolMatchers.isLiteral(true));
        assertNormalize("'foo%' not like any (['foo', 'kuh'])", SymbolMatchers.isLiteral(true));
        assertNormalize("'foo%' not like any (['fo', 'kuh'])", SymbolMatchers.isLiteral(true));
        assertNormalize("'%oob%' not like any (['foobar'])", SymbolMatchers.isLiteral(false));
    }

    @Test
    public void testNormalizeSymbolLikeExactlyOne() {
        // Following tests: wildcard: '_' ... any single character (exactly one)
        assertNormalize("'_ar' not like any (['bar'])", SymbolMatchers.isLiteral(false));
        assertNormalize("'_bar' not like any (['bar'])", SymbolMatchers.isLiteral(true));
        assertNormalize("'fo_' not like any (['foo', 'for'])", SymbolMatchers.isLiteral(false));
        assertNormalize("'foo_' not like any (['foo', 'foot'])", SymbolMatchers.isLiteral(true));
        assertNormalize("'foo_' not like any (['foo'])", SymbolMatchers.isLiteral(true));
        assertNormalize("'_o_' not like any (['foo'])", SymbolMatchers.isLiteral(false));
        assertNormalize("'_foobar_' not like any (['foobar'])", SymbolMatchers.isLiteral(true));
    }

    // Following tests: mixed wildcards:
    @Test
    public void testNormalizeSymbolLikeMixed() {
        assertNormalize("'%o_ar' not like any (['foobar', 'foobaz'])", SymbolMatchers.isLiteral(true));
        assertNormalize("'%a_' not like any (['foobar'])", SymbolMatchers.isLiteral(false));
        assertNormalize("'%o_a%' not like any (['foobar'])", SymbolMatchers.isLiteral(false));
        assertNormalize("'%i%m%' not like any (['Lorem ipsum dolor...'])", SymbolMatchers.isLiteral(false));
        assertNormalize("'%%%sum%%' not like any (['Lorem ipsum dolor...'])", SymbolMatchers.isLiteral(false));
        assertNormalize("'%i%m' not like any (['Lorem ipsum dolor...'])", SymbolMatchers.isLiteral(true));
    }

    @Test
    public void testEvaluateStraight() throws Exception {
        assertEvaluate("'foo' not like any (['foo', 'koo', 'doo'])", true);
        assertEvaluate("'foo' not like any (['foo'])", false);
        assertEvaluate("'foo' not like any ([])", false);
        assertEvaluate("'foo' not like any (['koo', 'doo'])", true);
    }

    @Test
    public void testEvaluateLikeMixed() {
        assertEvaluate("'%o_ar' not like any (['foobar', 'foobaz'])", true);
        assertEvaluate("'%a_' not like any (['foobar'])", false);
        assertEvaluate("'%o_a%' not like any (['foobar'])", false);
        assertEvaluate("'%i%m%' not like any (['Lorem ipsum dolor..'])", false);
        assertEvaluate("'%%%sum%%' not like any (['Lorem ipsum dolor...'])", false);
        assertEvaluate("'%i%m' not like any (['Lorem ipsum dolor...'])", true);
    }

    @Test
    public void testEvaluateNull() throws Exception {
        assertEvaluate("null not like any ([null])", null);
        assertEvaluate("'foo' not like any ([null])", null);
        assertEvaluate("null not like any (['bar'])", null);
    }

    @Test
    public void testNormalizeSymbolNull() throws Exception {
        assertNormalize("null not like any ([null])", SymbolMatchers.isLiteral(null));
        assertNormalize("'foo' not like any ([null])", SymbolMatchers.isLiteral(null));
        assertNormalize("null not like any (['bar'])", SymbolMatchers.isLiteral(null));
    }

    @Test
    public void testNegateNotLike() throws Exception {
        assertEvaluate("not 'A' not like any (['A', 'B'])", false);
    }
}

