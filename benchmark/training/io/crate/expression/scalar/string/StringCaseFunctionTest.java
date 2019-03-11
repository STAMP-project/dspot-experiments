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
package io.crate.expression.scalar.string;


import DataTypes.STRING;
import io.crate.expression.scalar.AbstractScalarFunctionsTest;
import io.crate.expression.symbol.Literal;
import io.crate.testing.SymbolMatchers;
import org.junit.Test;


public class StringCaseFunctionTest extends AbstractScalarFunctionsTest {
    @Test
    public void testNormalizeLowerFunc() throws Exception {
        assertNormalize("lower('ABCDEFGHIJKLMNOPQRSTUVWXYZ??????')", SymbolMatchers.isLiteral("abcdefghijklmnopqrstuvwxyz??????"));
    }

    @Test
    public void testEvaluateLowerFunc() throws Exception {
        assertEvaluate("lower(name)", "abcdefghijklmnopqrstuvwxyz??????", Literal.of(STRING, "ABCDEFGHIJKLMNOPQRSTUVWXYZ??????"));
    }

    @Test
    public void testEvaluateLowerFuncWhenColIsNull() throws Exception {
        assertEvaluate("lower(name)", null, Literal.of(STRING, null));
    }

    @Test
    public void testNormalizeUpperFunc() throws Exception {
        assertNormalize("upper('abcdefghijklmnopqrstuvwxyz??????')", SymbolMatchers.isLiteral("ABCDEFGHIJKLMNOPQRSTUVWXYZ??????"));
    }

    @Test
    public void testEvaluateUpperFunc() throws Exception {
        assertEvaluate("upper(name)", "ABCDEFGHIJKLMNOPQRSTUVWXYZ??????", Literal.of(STRING, "abcdefghijklmnopqrstuvwxyz??????"));
    }

    @Test
    public void testEvaluateUpperFuncWhenColIsNull() throws Exception {
        assertEvaluate("upper(name)", null, Literal.of(STRING, null));
    }
}

