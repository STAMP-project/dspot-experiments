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
package io.crate.metadata.functions.params;


import DataTypes.DOUBLE;
import DataTypes.INTEGER;
import DataTypes.LONG;
import Param.ANY;
import Param.ANY_ARRAY;
import Param.NUMERIC;
import Param.STRING;
import io.crate.analyze.relations.AnalyzedRelation;
import io.crate.exceptions.ConversionException;
import io.crate.expression.symbol.Field;
import io.crate.expression.symbol.FuncArg;
import io.crate.expression.symbol.Literal;
import io.crate.metadata.ColumnIdent;
import io.crate.metadata.Path;
import io.crate.test.integration.CrateUnitTest;
import io.crate.types.ArrayType;
import io.crate.types.DataType;
import io.crate.types.DataTypes;
import io.crate.types.ObjectType;
import java.util.Collections;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import static FuncParams.NONE;


public class FuncParamsTest extends CrateUnitTest {
    @Test
    public void testNoParams() {
        FuncParams none = NONE;
        assertThat(none.match(Collections.emptyList()), Matchers.is(Collections.emptyList()));
    }

    @Test
    public void testOneParam() {
        FuncParams oneArg = FuncParams.builder(ANY).build();
        Literal<Integer> symbol = Literal.of(1);
        List<DataType> match = oneArg.match(Collections.singletonList(symbol));
        assertThat(match, Matchers.is(Collections.singletonList(symbol.valueType())));
    }

    @Test
    public void testTwoConnectedParams() {
        FuncParams twoArgs = FuncParams.builder(ANY, ANY).build();
        Literal<Integer> symbol1 = Literal.of(1);
        Literal<Long> symbol2 = Literal.of(1L);
        List<DataType> match = twoArgs.match(FuncParamsTest.list(symbol1, symbol2));
        assertThat(match, Matchers.is(FuncParamsTest.list(symbol2.valueType(), symbol2.valueType())));
    }

    @Test
    public void testTooManyArgs() {
        FuncParams params = FuncParams.builder().build();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("The number of arguments is incorrect");
        params.match(FuncParamsTest.list(Literal.of(1)));
    }

    @Test
    public void testVarArgs() {
        FuncParams onlyVarArgs = FuncParams.builder(ANY).withVarArgs(ANY).build();
        List<DataType> signature = onlyVarArgs.match(FuncParamsTest.list(Literal.of(1), Literal.of(1L), Literal.of(1.0)));
        assertThat(signature, Matchers.is(FuncParamsTest.list(DOUBLE, DOUBLE, DOUBLE)));
    }

    @Test
    public void testVarArgLimit() {
        FuncParams maxOneVarArg = FuncParams.builder(ANY).withVarArgs(ANY).limitVarArgOccurrences(1).build();
        maxOneVarArg.match(FuncParamsTest.list(Literal.of("foo")));
        maxOneVarArg.match(FuncParamsTest.list(Literal.of("bla")));
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Too many variable arguments provided");
        List<DataType> signature = maxOneVarArg.match(FuncParamsTest.list(Literal.of(1), Literal.of(1L), Literal.of(1.0)));
        assertThat(signature, Matchers.is(FuncParamsTest.list(DOUBLE, DOUBLE, DOUBLE)));
    }

    @Test
    public void testIndependentVarArgs() {
        FuncParams independentVarArgs = FuncParams.builder(ANY).withIndependentVarArgs(ANY).build();
        List<DataType> signature = independentVarArgs.match(FuncParamsTest.list(Literal.of(1), Literal.of(1L), Literal.of(1.0)));
        assertThat(signature, Matchers.is(FuncParamsTest.list(INTEGER, LONG, DOUBLE)));
    }

    @Test
    public void testCompositeTypes() {
        FuncParams params = FuncParams.builder(ANY_ARRAY, ANY_ARRAY).build();
        ArrayType longArray = new ArrayType(DataTypes.LONG);
        List<DataType> signature = params.match(FuncParamsTest.list(Literal.of(new Object[]{ 1L, 2L, 3L }, longArray), Literal.of(new Object[]{ 4L, 5L, 6L }, longArray)));
        assertThat(signature, Matchers.is(FuncParamsTest.list(longArray, longArray)));
        ArrayType integerArray = new ArrayType(DataTypes.INTEGER);
        signature = params.match(FuncParamsTest.list(Literal.of(new Object[]{ 1L, 2L, 3L }, longArray), Literal.of(new Object[]{ 4, 5, 6 }, integerArray)));
        assertThat(signature, Matchers.is(FuncParamsTest.list(longArray, longArray)));
    }

    @Test
    public void testAllowedTypes() {
        FuncParams numericParams = FuncParams.builder(STRING).build();
        List<DataType> foo = numericParams.match(FuncParamsTest.list(Literal.of(1)));
        assertThat(foo, Matchers.is(FuncParamsTest.list(DataTypes.STRING)));
        FuncParams arrayParams = FuncParams.builder(Param.of(ObjectType.untyped())).build();
        expectedException.expect(ConversionException.class);
        expectedException.expectMessage("Cannot cast 1");
        arrayParams.match(FuncParamsTest.list(Literal.of(1)));
    }

    @Test
    public void testFieldsAreNotCastable() {
        Path path = new ColumnIdent("test");
        Field field = new Field(Mockito.mock(AnalyzedRelation.class), path, DataTypes.INTEGER);
        FuncParams params = FuncParams.builder(Param.LONG).build();
        expectedException.expect(ConversionException.class);
        expectedException.expectMessage("Cannot cast test to type long");
        params.match(FuncParamsTest.list(field));
    }

    @Test
    public void testRespectCastableArguments() {
        FuncArg castableArg = new FuncParamsTest.Arg(DataTypes.INTEGER, true);
        FuncParams params = FuncParams.builder(Param.LONG).build();
        assertThat(params.match(FuncParamsTest.list(castableArg)), Matchers.is(FuncParamsTest.list(LONG)));
    }

    @Test
    public void testRespectNonCastableArguments() {
        FuncArg castableArg = new FuncParamsTest.Arg(DataTypes.INTEGER, false);
        FuncParams params = FuncParams.builder(Param.LONG).build();
        expectedException.expect(ConversionException.class);
        expectedException.expectMessage("Cannot cast testarg to type long");
        params.match(FuncParamsTest.list(castableArg));
    }

    @Test
    public void testRespectNonCastableArgumentsWithInnerType() {
        FuncArg nonCastableArg1 = new FuncParamsTest.Arg(DataTypes.LONG, false);
        FuncArg nonCastableArg2 = new FuncParamsTest.Arg(new ArrayType(DataTypes.INTEGER), false);
        FuncParams params = FuncParams.builder(ANY, ANY_ARRAY.withInnerType(ANY)).build();
        List<DataType> signature = params.match(FuncParamsTest.list(nonCastableArg1, nonCastableArg2));
        assertThat(signature, Matchers.is(FuncParamsTest.list(LONG, new ArrayType(DataTypes.LONG))));
    }

    @Test
    public void testRespectCastableArgumentsWithInnerType() {
        FuncArg castableArg = new FuncParamsTest.Arg(DataTypes.LONG, true);
        ArrayType integerArrayType = new ArrayType(DataTypes.INTEGER);
        FuncArg nonCastableArg = new FuncParamsTest.Arg(integerArrayType, false);
        FuncParams params = FuncParams.builder(ANY, ANY_ARRAY.withInnerType(ANY)).build();
        List<DataType> signature = params.match(FuncParamsTest.list(castableArg, nonCastableArg));
        assertThat(signature, Matchers.is(FuncParamsTest.list(INTEGER, integerArrayType)));
    }

    @Test
    public void testLosslessConversionResultingInDowncast() {
        FuncArg highPrecedenceType = Literal.of(LONG, 5L);
        FuncArg lowerPrecedenceType = new FuncParamsTest.Arg(DataTypes.INTEGER, true);
        FuncParams params = FuncParams.builder(NUMERIC, NUMERIC).build();
        List<DataType> signature;
        signature = params.match(FuncParamsTest.list(highPrecedenceType, lowerPrecedenceType));
        assertThat(signature, Matchers.is(FuncParamsTest.list(INTEGER, INTEGER)));
        signature = params.match(FuncParamsTest.list(lowerPrecedenceType, highPrecedenceType));
        assertThat(signature, Matchers.is(FuncParamsTest.list(INTEGER, INTEGER)));
    }

    private static class Arg implements FuncArg {
        private final DataType dataType;

        private final boolean castable;

        Arg(DataType dataType, boolean castable) {
            this.dataType = dataType;
            this.castable = castable;
        }

        @Override
        public DataType valueType() {
            return dataType;
        }

        @Override
        public boolean canBeCasted() {
            return castable;
        }

        @Override
        public String toString() {
            return "testarg";
        }
    }
}

