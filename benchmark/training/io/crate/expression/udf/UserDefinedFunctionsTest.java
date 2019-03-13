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
package io.crate.expression.udf;


import DataTypes.INTEGER;
import com.google.common.collect.ImmutableList;
import io.crate.metadata.FunctionIdent;
import io.crate.metadata.FunctionImplementation;
import io.crate.metadata.Functions;
import io.crate.testing.SqlExpressions;
import io.crate.testing.SymbolMatchers;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

import static io.crate.expression.udf.UdfUnitTest.DummyFunction.RESULT;


public class UserDefinedFunctionsTest extends UdfUnitTest {
    private Functions functions;

    private SqlExpressions sqlExpressions;

    private Map<FunctionIdent, FunctionImplementation> functionImplementations = new HashMap<>();

    @Test
    public void testOverloadingBuiltinFunctions() throws Exception {
        registerUserDefinedFunction(UdfUnitTest.DUMMY_LANG.name(), "test", "subtract", INTEGER, ImmutableList.of(INTEGER, INTEGER), "function subtract(a, b) { return a + b; }");
        String expr = "test.subtract(2::integer, 1::integer)";
        assertThat(sqlExpressions.asSymbol(expr), SymbolMatchers.isLiteral(RESULT));
    }
}

