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
package io.crate.expression.scalar.arithmetic;


import CeilFunction.NAME;
import DataTypes.INTEGER;
import DataTypes.LONG;
import io.crate.expression.scalar.AbstractScalarFunctionsTest;
import io.crate.testing.SymbolMatchers;
import org.junit.Test;


public class CeilFunctionTest extends AbstractScalarFunctionsTest {
    @Test
    public void testEvaluateOnDouble() throws Exception {
        assertNormalize("ceil(29.9)", SymbolMatchers.isLiteral(30L));
        assertNormalize("ceil(null)", SymbolMatchers.isLiteral(null));
    }

    @Test
    public void testEvaluateOnFloat() throws Exception {
        assertNormalize("ceil(cast(29.9 as float))", SymbolMatchers.isLiteral(30));
        assertNormalize("ceil(cast(null as float))", SymbolMatchers.isLiteral(null, INTEGER));
    }

    @Test
    public void testEvaluateOnIntAndLong() throws Exception {
        assertNormalize("ceil(cast(20 as integer))", SymbolMatchers.isLiteral(20));
        assertNormalize("ceil(cast(null as integer))", SymbolMatchers.isLiteral(null, INTEGER));
        assertNormalize("ceil(20)", SymbolMatchers.isLiteral(20L));
        assertNormalize("ceil(cast(null as long))", SymbolMatchers.isLiteral(null, LONG));
    }

    @Test
    public void testNormalizeReference() throws Exception {
        assertNormalize("ceil(age)", SymbolMatchers.isFunction(NAME));
    }
}

