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
package io.crate.expression.scalar.arithmetic;


import DataTypes.DOUBLE;
import DataTypes.SHORT;
import io.crate.expression.scalar.AbstractScalarFunctionsTest;
import io.crate.expression.symbol.Literal;
import io.crate.testing.SymbolMatchers;
import org.hamcrest.Matchers;
import org.junit.Test;


public class TrigonometricFunctionsTest extends AbstractScalarFunctionsTest {
    @Test
    public void testEvaluate() throws Exception {
        assertEvaluate("sin(double_val)", 0.8414709848078965, Literal.of(1.0));
        assertEvaluate("sin(float_val)", 0.9092974268256817, Literal.of(2.0F));
        assertEvaluate("sin(x)", 0.1411200080598672, Literal.of(3L));
        assertEvaluate("sin(id)", (-0.7568024953079282), Literal.of(4));
        assertEvaluate("sin(short_val)", (-0.9589242746631385), Literal.of(SHORT, ((short) (5))));
        assertEvaluate("asin(double_val)", 0.12371534584255098, Literal.of(0.1234));
        assertEvaluate("asin(float_val)", 0.44682008883801516, Literal.of(0.4321F));
        assertEvaluate("asin(x)", 0.0, Literal.of(0L));
        assertEvaluate("asin(id)", 1.5707963267948966, Literal.of(1));
        assertEvaluate("asin(short_val)", (-1.5707963267948966), Literal.of(SHORT, ((short) (-1))));
        assertEvaluate("cos(double_val)", 0.5403023058681398, Literal.of(1.0));
        assertEvaluate("cos(float_val)", (-0.4161468365471424), Literal.of(2.0F));
        assertEvaluate("cos(x)", (-0.9899924966004454), Literal.of(3L));
        assertEvaluate("cos(id)", (-0.6536436208636119), Literal.of(4));
        assertEvaluate("cos(short_val)", 0.28366218546322625, Literal.of(SHORT, ((short) (5))));
        assertEvaluate("acos(double_val)", 1.4470809809523457, Literal.of(0.1234));
        assertEvaluate("acos(float_val)", 1.1239762379568814, Literal.of(0.4321F));
        assertEvaluate("acos(x)", 1.5707963267948966, Literal.of(0L));
        assertEvaluate("acos(id)", 0.0, Literal.of(1));
        assertEvaluate("acos(short_val)", 3.141592653589793, Literal.of(SHORT, ((short) (-1))));
        assertEvaluate("tan(double_val)", 1.5574077246549023, Literal.of(1.0));
        assertEvaluate("tan(float_val)", (-2.185039863261519), Literal.of(2.0F));
        assertEvaluate("tan(x)", (-0.1425465430742778), Literal.of(3L));
        assertEvaluate("tan(id)", Matchers.anyOf(Matchers.is(1.1578212823495777), Matchers.is(1.1578212823495775)), Literal.of(4));
        assertEvaluate("tan(short_val)", (-3.380515006246586), Literal.of(SHORT, ((short) (5))));
        assertEvaluate("atan(double_val)", 0.12277930094473836, Literal.of(0.1234));
        assertEvaluate("atan(float_val)", 0.4078690066146179, Literal.of(0.4321F));
        assertEvaluate("atan(x)", 0.0, Literal.of(0L));
        assertEvaluate("atan(id)", 0.7853981633974483, Literal.of(1));
        assertEvaluate("atan(short_val)", (-0.7853981633974483), Literal.of(SHORT, ((short) (-1))));
    }

    @Test
    public void testEvaluateAsinOnIllegalArgument() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("input value 2.0 is out of range. Values must be in range of [-1.0, 1.0]");
        assertEvaluate("asin(2.0)", 0);
    }

    @Test
    public void testEvaluateAcosOnIllegalArgument() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("input value 2.0 is out of range. Values must be in range of [-1.0, 1.0]");
        assertEvaluate("acos(2.0)", 0);
    }

    @Test
    public void testEvaluateAtanOnIllegalArgument() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("input value 2.0 is out of range. Values must be in range of [-1.0, 1.0]");
        assertEvaluate("atan(2.0)", 0);
    }

    @Test
    public void testEvaluateOnNull() throws Exception {
        assertEvaluate("sin(null)", null);
        assertEvaluate("asin(null)", null);
        assertEvaluate("cos(null)", null);
        assertEvaluate("acos(null)", null);
        assertEvaluate("tan(null)", null);
        assertEvaluate("atan(null)", null);
    }

    @Test
    public void testNormalize() throws Exception {
        // SinFunction
        assertNormalize("sin(1.0)", SymbolMatchers.isLiteral(0.8414709848078965, DOUBLE));
        assertNormalize("sin(cast (2.0 as float))", SymbolMatchers.isLiteral(0.9092974268256817, DOUBLE));
        assertNormalize("sin(3)", SymbolMatchers.isLiteral(0.1411200080598672, DOUBLE));
        assertNormalize("sin(cast (4 as integer))", SymbolMatchers.isLiteral((-0.7568024953079282), DOUBLE));
        assertNormalize("sin(cast (5 as short))", SymbolMatchers.isLiteral((-0.9589242746631385), DOUBLE));
        // AsinFunction
        assertNormalize("asin(0.1234)", SymbolMatchers.isLiteral(0.12371534584255098, DOUBLE));
        assertNormalize("asin(cast (0.4321 as float))", SymbolMatchers.isLiteral(0.44682008883801516, DOUBLE));
        assertNormalize("asin(0)", SymbolMatchers.isLiteral(0.0, DOUBLE));
        assertNormalize("asin(cast (1 as integer))", SymbolMatchers.isLiteral(1.5707963267948966, DOUBLE));
        assertNormalize("asin(cast (-1 as short))", SymbolMatchers.isLiteral((-1.5707963267948966), DOUBLE));
        // CosFunction
        assertNormalize("cos(1.0)", SymbolMatchers.isLiteral(0.5403023058681398, DOUBLE));
        assertNormalize("cos(cast (2.0 as float))", SymbolMatchers.isLiteral((-0.4161468365471424), DOUBLE));
        assertNormalize("cos(3)", SymbolMatchers.isLiteral((-0.9899924966004454), DOUBLE));
        assertNormalize("cos(cast (4 as integer))", SymbolMatchers.isLiteral((-0.6536436208636119), DOUBLE));
        assertNormalize("cos(cast (5 as short))", SymbolMatchers.isLiteral(0.28366218546322625, DOUBLE));
        // AcosFunction
        assertNormalize("acos(0.1234)", SymbolMatchers.isLiteral(1.4470809809523457, DOUBLE));
        assertNormalize("acos(cast (0.4321 as float))", SymbolMatchers.isLiteral(1.1239762379568814, DOUBLE));
        assertNormalize("acos(0)", SymbolMatchers.isLiteral(1.5707963267948966, DOUBLE));
        assertNormalize("acos(cast (1 as integer))", SymbolMatchers.isLiteral(0.0, DOUBLE));
        assertNormalize("acos(cast (-1 as short))", SymbolMatchers.isLiteral(3.141592653589793, DOUBLE));
        // TanFunction
        assertNormalize("tan(1.0)", SymbolMatchers.isLiteral(1.5574077246549023, DOUBLE));
        assertNormalize("tan(cast (2.0 as float))", SymbolMatchers.isLiteral((-2.185039863261519), DOUBLE));
        assertNormalize("tan(3)", SymbolMatchers.isLiteral((-0.1425465430742778), DOUBLE));
        assertNormalize("tan(cast (4 as integer))", Matchers.anyOf(SymbolMatchers.isLiteral(1.1578212823495775, DOUBLE), SymbolMatchers.isLiteral(1.1578212823495777, DOUBLE)));
        assertNormalize("tan(cast (5 as short))", SymbolMatchers.isLiteral((-3.380515006246586), DOUBLE));
        // AtanFunction
        assertNormalize("atan(0.1234)", SymbolMatchers.isLiteral(0.12277930094473836, DOUBLE));
        assertNormalize("atan(cast (0.4321 as float))", SymbolMatchers.isLiteral(0.4078690066146179, DOUBLE));
        assertNormalize("atan(0)", SymbolMatchers.isLiteral(0.0, DOUBLE));
        assertNormalize("atan(cast (1 as integer))", SymbolMatchers.isLiteral(0.7853981633974483, DOUBLE));
        assertNormalize("atan(cast (-1 as short))", SymbolMatchers.isLiteral((-0.7853981633974483), DOUBLE));
    }

    @Test
    public void testNormalizeOnNull() throws Exception {
        // SinFunction
        assertNormalize("sin(cast(null as double))", SymbolMatchers.isLiteral(null, DOUBLE));
        assertNormalize("sin(cast(null as float))", SymbolMatchers.isLiteral(null, DOUBLE));
        assertNormalize("sin(cast(null as long))", SymbolMatchers.isLiteral(null, DOUBLE));
        assertNormalize("sin(cast(null as integer))", SymbolMatchers.isLiteral(null, DOUBLE));
        assertNormalize("sin(cast(null as short))", SymbolMatchers.isLiteral(null, DOUBLE));
        // AsinFunction
        assertNormalize("asin(cast(null as double))", SymbolMatchers.isLiteral(null, DOUBLE));
        assertNormalize("asin(cast(null as float))", SymbolMatchers.isLiteral(null, DOUBLE));
        assertNormalize("asin(cast(null as long))", SymbolMatchers.isLiteral(null, DOUBLE));
        assertNormalize("asin(cast(null as integer))", SymbolMatchers.isLiteral(null, DOUBLE));
        assertNormalize("asin(cast(null as short))", SymbolMatchers.isLiteral(null, DOUBLE));
        // CosFunction
        assertNormalize("cos(cast(null as double))", SymbolMatchers.isLiteral(null, DOUBLE));
        assertNormalize("cos(cast(null as float))", SymbolMatchers.isLiteral(null, DOUBLE));
        assertNormalize("cos(cast(null as long))", SymbolMatchers.isLiteral(null, DOUBLE));
        assertNormalize("cos(cast(null as integer))", SymbolMatchers.isLiteral(null, DOUBLE));
        assertNormalize("cos(cast(null as short))", SymbolMatchers.isLiteral(null, DOUBLE));
        // AcosFunction
        assertNormalize("acos(cast(null as double))", SymbolMatchers.isLiteral(null, DOUBLE));
        assertNormalize("acos(cast(null as float))", SymbolMatchers.isLiteral(null, DOUBLE));
        assertNormalize("acos(cast(null as long))", SymbolMatchers.isLiteral(null, DOUBLE));
        assertNormalize("acos(cast(null as integer))", SymbolMatchers.isLiteral(null, DOUBLE));
        assertNormalize("acos(cast(null as short))", SymbolMatchers.isLiteral(null, DOUBLE));
        // TanFunction
        assertNormalize("tan(cast(null as double))", SymbolMatchers.isLiteral(null, DOUBLE));
        assertNormalize("tan(cast(null as float))", SymbolMatchers.isLiteral(null, DOUBLE));
        assertNormalize("tan(cast(null as long))", SymbolMatchers.isLiteral(null, DOUBLE));
        assertNormalize("tan(cast(null as integer))", SymbolMatchers.isLiteral(null, DOUBLE));
        assertNormalize("tan(cast(null as short))", SymbolMatchers.isLiteral(null, DOUBLE));
        // AtanFunction
        assertNormalize("atan(cast(null as double))", SymbolMatchers.isLiteral(null, DOUBLE));
        assertNormalize("atan(cast(null as float))", SymbolMatchers.isLiteral(null, DOUBLE));
        assertNormalize("atan(cast(null as long))", SymbolMatchers.isLiteral(null, DOUBLE));
        assertNormalize("atan(cast(null as integer))", SymbolMatchers.isLiteral(null, DOUBLE));
        assertNormalize("atan(cast(null as short))", SymbolMatchers.isLiteral(null, DOUBLE));
    }

    @Test
    public void testNormalizeReference() throws Exception {
        assertNormalize("sin(age)", SymbolMatchers.isFunction("sin"));
        assertNormalize("asin(age)", SymbolMatchers.isFunction("asin"));
        assertNormalize("cos(age)", SymbolMatchers.isFunction("cos"));
        assertNormalize("acos(age)", SymbolMatchers.isFunction("acos"));
        assertNormalize("tan(age)", SymbolMatchers.isFunction("tan"));
        assertNormalize("atan(age)", SymbolMatchers.isFunction("atan"));
    }
}

