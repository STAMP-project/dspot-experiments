/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.calcite.linq4j.test;


import java.math.BigInteger;
import java.util.Collections;
import java.util.concurrent.Callable;
import org.apache.calcite.linq4j.function.Deterministic;
import org.apache.calcite.linq4j.function.NonDeterministic;
import org.apache.calcite.linq4j.tree.Blocks;
import org.apache.calcite.linq4j.tree.Expression;
import org.apache.calcite.linq4j.tree.Expressions;
import org.apache.calcite.linq4j.tree.Types;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests factoring out deterministic expressions.
 */
public class DeterministicTest {
    /**
     * Class to test @Deterministic annotation
     */
    public static class TestClass {
        @Deterministic
        public static int deterministic(int a) {
            return a + 1;
        }

        public static int nonDeterministic(int a) {
            return a + 2;
        }
    }

    /**
     * Class to test @NonDeterministic annotation
     */
    @Deterministic
    public static class TestDeterministicClass {
        public static int deterministic(int a) {
            return a + 1;
        }

        @NonDeterministic
        public static int nonDeterministic(int a) {
            return a + 2;
        }
    }

    @Test
    public void testConstantIsConstant() {
        // Small expressions are atomic.
        Assert.assertThat(isAtomic(Expressions.constant(0)), CoreMatchers.is(true));
        Assert.assertThat(isAtomic(Expressions.constant("xxx")), CoreMatchers.is(true));
        Assert.assertThat(isAtomic(Expressions.constant(null)), CoreMatchers.is(true));
        Expression e = Expressions.call(DeterministicTest.getMethod(Integer.class, "valueOf", int.class), Expressions.constant((-100)));
        Assert.assertThat(isAtomic(e), CoreMatchers.is(false));
        Assert.assertThat(isConstant(e), CoreMatchers.is(true));
        e = Expressions.call(Integer.class, "valueOf", Expressions.constant(0));
        Assert.assertThat(isAtomic(e), CoreMatchers.is(false));
        Assert.assertThat(isConstant(e), CoreMatchers.is(true));
        e = Expressions.call(Expressions.constant("xxx"), "length");
        Assert.assertThat(isAtomic(e), CoreMatchers.is(false));
        Assert.assertThat(isConstant(e), CoreMatchers.is(true));
    }

    @Test
    public void testFactorOutBinaryAdd() {
        Assert.assertThat(BlockBuilderBase.optimize(Expressions.new_(Runnable.class, Collections.emptyList(), Expressions.methodDecl(0, int.class, "test", Collections.emptyList(), Blocks.toFunctionBlock(Expressions.add(BlockBuilderBase.ONE, BlockBuilderBase.TWO))))), CoreMatchers.equalTo(("{\n" + ((((((("  return new Runnable(){\n" + "      int test() {\n") + "        return $L4J$C$1_2;\n") + "      }\n") + "\n") + "      static final int $L4J$C$1_2 = 1 + 2;\n") + "    };\n") + "}\n"))));
    }

    @Test
    public void testFactorOutBinaryAddSurvivesMultipleOptimizations() {
        Assert.assertThat(BlockBuilderBase.optimize(BlockBuilderBase.optimizeExpression(Expressions.new_(Runnable.class, Collections.emptyList(), Expressions.methodDecl(0, int.class, "test", Collections.emptyList(), Blocks.toFunctionBlock(Expressions.add(BlockBuilderBase.ONE, BlockBuilderBase.TWO)))))), CoreMatchers.equalTo(("{\n" + ((((((("  return new Runnable(){\n" + "      int test() {\n") + "        return $L4J$C$1_2;\n") + "      }\n") + "\n") + "      static final int $L4J$C$1_2 = 1 + 2;\n") + "    };\n") + "}\n"))));
    }

    @Test
    public void testFactorOutBinaryAddNameCollision() {
        Assert.assertThat(BlockBuilderBase.optimize(Expressions.new_(Runnable.class, Collections.emptyList(), Expressions.methodDecl(0, int.class, "test", Collections.emptyList(), Blocks.toFunctionBlock(Expressions.multiply(Expressions.add(BlockBuilderBase.ONE, BlockBuilderBase.TWO), Expressions.subtract(BlockBuilderBase.ONE, BlockBuilderBase.TWO)))))), CoreMatchers.equalTo(("{\n" + ((((((((("  return new Runnable(){\n" + "      int test() {\n") + "        return $L4J$C$1_2_1_20;\n") + "      }\n") + "\n") + "      static final int $L4J$C$1_2 = 1 + 2;\n") + "      static final int $L4J$C$1_20 = 1 - 2;\n") + "      static final int $L4J$C$1_2_1_20 = $L4J$C$1_2 * $L4J$C$1_20;\n") + "    };\n") + "}\n"))));
    }

    @Test
    public void testFactorOutBinaryAddMul() {
        Assert.assertThat(BlockBuilderBase.optimize(Expressions.new_(Runnable.class, Collections.emptyList(), Expressions.methodDecl(0, int.class, "test", Collections.emptyList(), Blocks.toFunctionBlock(Expressions.multiply(Expressions.add(BlockBuilderBase.ONE, BlockBuilderBase.TWO), BlockBuilderBase.THREE))))), CoreMatchers.equalTo(("{\n" + (((((((("  return new Runnable(){\n" + "      int test() {\n") + "        return $L4J$C$1_2_3;\n") + "      }\n") + "\n") + "      static final int $L4J$C$1_2 = 1 + 2;\n") + "      static final int $L4J$C$1_2_3 = $L4J$C$1_2 * 3;\n") + "    };\n") + "}\n"))));
    }

    @Test
    public void testFactorOutNestedClasses() {
        Assert.assertThat(BlockBuilderBase.optimize(Expressions.new_(Runnable.class, Collections.emptyList(), Expressions.methodDecl(0, int.class, "test", Collections.emptyList(), Blocks.toFunctionBlock(Expressions.add(Expressions.add(BlockBuilderBase.ONE, BlockBuilderBase.FOUR), Expressions.call(Expressions.new_(Callable.class, Collections.emptyList(), Expressions.methodDecl(0, Object.class, "call", Collections.emptyList(), Blocks.toFunctionBlock(Expressions.multiply(Expressions.add(BlockBuilderBase.ONE, BlockBuilderBase.TWO), BlockBuilderBase.THREE)))), "call", Collections.emptyList())))))), CoreMatchers.equalTo(("{\n" + (((((((((((((("  return new Runnable(){\n" + "      int test() {\n") + "        return $L4J$C$1_4 + new java.util.concurrent.Callable(){\n") + "            Object call() {\n") + "              return $L4J$C$1_2_3;\n") + "            }\n") + "\n") + "            static final int $L4J$C$1_2 = 1 + 2;\n") + "            static final int $L4J$C$1_2_3 = $L4J$C$1_2 * 3;\n") + "          }.call();\n") + "      }\n") + "\n") + "      static final int $L4J$C$1_4 = 1 + 4;\n") + "    };\n") + "}\n"))));
    }

    @Test
    public void testNewBigInteger() {
        Assert.assertThat(BlockBuilderBase.optimize(Expressions.new_(Runnable.class, Collections.emptyList(), Expressions.methodDecl(0, int.class, "test", Collections.emptyList(), Blocks.toFunctionBlock(Expressions.new_(BigInteger.class, Expressions.constant("42")))))), CoreMatchers.equalTo(("{\n" + ((((((((("  return new Runnable(){\n" + "      int test() {\n") + "        return $L4J$C$new_java_math_BigInteger_42_;\n") + "      }\n") + "\n") + "      static final java.math.BigInteger ") + "$L4J$C$new_java_math_BigInteger_42_ = new java.math.BigInteger(\n") + "        \"42\");\n") + "    };\n") + "}\n"))));
    }

    @Test
    public void testInstanceofTest() {
        // Single instanceof is not optimized
        Assert.assertThat(BlockBuilderBase.optimize(Expressions.new_(Runnable.class, Collections.emptyList(), Expressions.methodDecl(0, int.class, "test", Collections.emptyList(), Blocks.toFunctionBlock(Expressions.typeIs(BlockBuilderBase.ONE, Boolean.class))))), CoreMatchers.equalTo(("{\n" + (((((("  return new Runnable(){\n" + "      int test() {\n") + "        return 1 instanceof Boolean;\n") + "      }\n") + "\n") + "    };\n") + "}\n"))));
    }

    @Test
    public void testInstanceofComplexTest() {
        // instanceof is optimized in complex expressions
        Assert.assertThat(BlockBuilderBase.optimize(Expressions.new_(Runnable.class, Collections.emptyList(), Expressions.methodDecl(0, int.class, "test", Collections.emptyList(), Blocks.toFunctionBlock(Expressions.orElse(Expressions.typeIs(BlockBuilderBase.ONE, Boolean.class), Expressions.typeIs(BlockBuilderBase.TWO, Integer.class)))))), CoreMatchers.equalTo(("{\n" + ((((((((("  return new Runnable(){\n" + "      int test() {\n") + "        return $L4J$C$1_instanceof_Boolean_2_instanceof_Integer;\n") + "      }\n") + "\n") + "      static final boolean ") + "$L4J$C$1_instanceof_Boolean_2_instanceof_Integer = 1 instanceof ") + "Boolean || 2 instanceof Integer;\n") + "    };\n") + "}\n"))));
    }

    @Test
    public void testIntegerValueOfZeroComplexTest() {
        // Integer.valueOf(0) is optimized in complex expressions
        Assert.assertThat(BlockBuilderBase.optimize(Expressions.new_(Runnable.class, Collections.emptyList(), Expressions.methodDecl(0, int.class, "test", Collections.emptyList(), Blocks.toFunctionBlock(Expressions.call(DeterministicTest.getMethod(Integer.class, "valueOf", int.class), Expressions.constant(0)))))), CoreMatchers.equalTo(("{\n" + ((((((("  return new Runnable(){\n" + "      int test() {\n") + "        return $L4J$C$Integer_valueOf_0_;\n") + "      }\n") + "\n") + "      static final Integer $L4J$C$Integer_valueOf_0_ = Integer.valueOf(0);\n") + "    };\n") + "}\n"))));
    }

    @Test
    public void testStaticField() {
        // instanceof is optimized in complex expressions
        Assert.assertThat(BlockBuilderBase.optimize(Expressions.new_(Runnable.class, Collections.emptyList(), Expressions.methodDecl(0, int.class, "test", Collections.emptyList(), Blocks.toFunctionBlock(Expressions.call(Expressions.field(null, BigInteger.class, "ONE"), "add", Expressions.call(null, Types.lookupMethod(BigInteger.class, "valueOf", long.class), Expressions.constant(42L))))))), CoreMatchers.equalTo(("{\n" + (((((((((((("  return new Runnable(){\n" + "      int test() {\n") + "        return ") + "$L4J$C$java_math_BigInteger_ONE_add_java_math_BigInteger_valueOf_42L_;\n") + "      }\n") + "\n") + "      static final java.math.BigInteger ") + "$L4J$C$java_math_BigInteger_valueOf_42L_ = java.math.BigInteger") + ".valueOf(42L);\n") + "      static final java.math.BigInteger ") + "$L4J$C$java_math_BigInteger_ONE_add_java_math_BigInteger_valueOf_42L_ = java.math.BigInteger.ONE.add($L4J$C$java_math_BigInteger_valueOf_42L_);\n") + "    };\n") + "}\n"))));
    }

    @Test
    public void testBigIntegerValueOf() {
        // instanceof is optimized in complex expressions
        Assert.assertThat(BlockBuilderBase.optimize(Expressions.new_(Runnable.class, Collections.emptyList(), Expressions.methodDecl(0, int.class, "test", Collections.emptyList(), Blocks.toFunctionBlock(Expressions.call(Expressions.call(null, Types.lookupMethod(BigInteger.class, "valueOf", long.class), Expressions.constant(42L)), "add", Expressions.call(null, Types.lookupMethod(BigInteger.class, "valueOf", long.class), Expressions.constant(42L))))))), CoreMatchers.equalTo(("{\n" + (((((((((((("  return new Runnable(){\n" + "      int test() {\n") + "        return ") + "$L4J$C$java_math_BigInteger_valueOf_42L_add_java_math_BigInteger_valued8d57d69;\n") + "      }\n") + "\n") + "      static final java.math.BigInteger ") + "$L4J$C$java_math_BigInteger_valueOf_42L_ = java.math.BigInteger") + ".valueOf(42L);\n") + "      static final java.math.BigInteger ") + "$L4J$C$java_math_BigInteger_valueOf_42L_add_java_math_BigInteger_valued8d57d69 = $L4J$C$java_math_BigInteger_valueOf_42L_.add($L4J$C$java_math_BigInteger_valueOf_42L_);\n") + "    };\n") + "}\n"))));
    }

    @Test
    public void testDeterministicMethodCall() {
        Assert.assertThat(BlockBuilderBase.optimize(Expressions.new_(Runnable.class, Collections.emptyList(), Expressions.methodDecl(0, int.class, "test", Collections.emptyList(), Blocks.toFunctionBlock(Expressions.call(null, Types.lookupMethod(DeterministicTest.TestClass.class, "deterministic", int.class), BlockBuilderBase.ONE))))), CoreMatchers.equalTo(("{\n" + ((((((("  return new Runnable(){\n" + "      int test() {\n") + "        return $L4J$C$org_apache_calcite_linq4j_test_DeterministicTest_TestClass_dete33e8af1c;\n") + "      }\n") + "\n") + "      static final int $L4J$C$org_apache_calcite_linq4j_test_DeterministicTest_TestClass_dete33e8af1c = org.apache.calcite.linq4j.test.DeterministicTest.TestClass.deterministic(1);\n") + "    };\n") + "}\n"))));
    }

    @Test
    public void testNonDeterministicMethodCall() {
        Assert.assertThat(BlockBuilderBase.optimize(Expressions.new_(Runnable.class, Collections.emptyList(), Expressions.methodDecl(0, int.class, "test", Collections.emptyList(), Blocks.toFunctionBlock(Expressions.call(null, Types.lookupMethod(DeterministicTest.TestClass.class, "nonDeterministic", int.class), BlockBuilderBase.ONE))))), CoreMatchers.equalTo(("{\n" + (((((("  return new Runnable(){\n" + "      int test() {\n") + "        return org.apache.calcite.linq4j.test.DeterministicTest.TestClass.nonDeterministic(1);\n") + "      }\n") + "\n") + "    };\n") + "}\n"))));
    }

    @Test
    public void testDeterministicClassDefaultMethod() {
        Assert.assertThat(BlockBuilderBase.optimize(Expressions.new_(Runnable.class, Collections.emptyList(), Expressions.methodDecl(0, int.class, "test", Collections.emptyList(), Blocks.toFunctionBlock(Expressions.call(null, Types.lookupMethod(DeterministicTest.TestDeterministicClass.class, "deterministic", int.class), BlockBuilderBase.ONE))))), CoreMatchers.equalTo(("{\n" + ((((((("  return new Runnable(){\n" + "      int test() {\n") + "        return $L4J$C$org_apache_calcite_linq4j_test_DeterministicTest_TestDeterminis9de610da;\n") + "      }\n") + "\n") + "      static final int $L4J$C$org_apache_calcite_linq4j_test_DeterministicTest_TestDeterminis9de610da = org.apache.calcite.linq4j.test.DeterministicTest.TestDeterministicClass.deterministic(1);\n") + "    };\n") + "}\n"))));
    }

    @Test
    public void testDeterministicClassNonDeterministicMethod() {
        Assert.assertThat(BlockBuilderBase.optimize(Expressions.new_(Runnable.class, Collections.emptyList(), Expressions.methodDecl(0, int.class, "test", Collections.emptyList(), Blocks.toFunctionBlock(Expressions.call(null, Types.lookupMethod(DeterministicTest.TestDeterministicClass.class, "nonDeterministic", int.class), BlockBuilderBase.ONE))))), CoreMatchers.equalTo(("{\n" + (((((("  return new Runnable(){\n" + "      int test() {\n") + "        return org.apache.calcite.linq4j.test.DeterministicTest.TestDeterministicClass.nonDeterministic(1);\n") + "      }\n") + "\n") + "    };\n") + "}\n"))));
    }
}

/**
 * End DeterministicTest.java
 */
