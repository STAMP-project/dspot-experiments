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


import OptimizeShuttle.BOXED_FALSE_EXPR;
import java.util.function.Function;
import org.apache.calcite.linq4j.tree.BinaryExpression;
import org.apache.calcite.linq4j.tree.BlockBuilder;
import org.apache.calcite.linq4j.tree.Expression;
import org.apache.calcite.linq4j.tree.ExpressionType;
import org.apache.calcite.linq4j.tree.Expressions;
import org.apache.calcite.linq4j.tree.OptimizeShuttle;
import org.apache.calcite.linq4j.tree.Shuttle;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests BlockBuilder.
 */
public class BlockBuilderTest {
    BlockBuilder b;

    @Test
    public void testReuseExpressionsFromUpperLevel() {
        Expression x = b.append("x", Expressions.add(BlockBuilderBase.ONE, BlockBuilderBase.TWO));
        BlockBuilder nested = new BlockBuilder(true, b);
        Expression y = nested.append("y", Expressions.add(BlockBuilderBase.ONE, BlockBuilderBase.TWO));
        nested.add(Expressions.return_(null, Expressions.add(y, y)));
        b.add(nested.toBlock());
        Assert.assertEquals(("{\n" + (((("  final int x = 1 + 2;\n" + "  {\n") + "    return x + x;\n") + "  }\n") + "}\n")), b.toBlock().toString());
    }

    @Test
    public void testTestCustomOptimizer() {
        BlockBuilder b = new BlockBuilder() {
            @Override
            protected Shuttle createOptimizeShuttle() {
                return new OptimizeShuttle() {
                    @Override
                    public Expression visit(BinaryExpression binary, Expression expression0, Expression expression1) {
                        if ((((binary.getNodeType()) == (ExpressionType.Add)) && (BlockBuilderBase.ONE.equals(expression0))) && (BlockBuilderBase.TWO.equals(expression1))) {
                            return BlockBuilderBase.FOUR;
                        }
                        return super.visit(binary, expression0, expression1);
                    }
                };
            }
        };
        b.add(Expressions.return_(null, Expressions.add(BlockBuilderBase.ONE, BlockBuilderBase.TWO)));
        Assert.assertEquals("{\n  return 4;\n}\n", b.toBlock().toString());
    }

    @Test
    public void testRenameVariablesWithEmptyInitializer() {
        BlockBuilder outer = appendBlockWithSameVariable(null, null);
        Assert.assertEquals("x in the second block should be renamed to avoid name clash", ("{\n" + (((("  int x;\n" + "  x = 1;\n") + "  int x0;\n") + "  x0 = 42;\n") + "}\n")), Expressions.toString(outer.toBlock()));
    }

    @Test
    public void testRenameVariablesWithInitializer() {
        BlockBuilder outer = appendBlockWithSameVariable(Expressions.constant(7), Expressions.constant(8));
        Assert.assertEquals("x in the second block should be renamed to avoid name clash", ("{\n" + (((("  int x = 7;\n" + "  x = 1;\n") + "  int x0 = 8;\n") + "  x0 = 42;\n") + "}\n")), Expressions.toString(outer.toBlock()));
    }

    /**
     * CALCITE-2413: RexToLixTranslator does not generate correct declaration of Methods with
     * generic return types
     */
    @Test
    public void genericMethodCall() throws NoSuchMethodException {
        BlockBuilder bb = new BlockBuilder();
        bb.append("_i", Expressions.call(Expressions.new_(BlockBuilderTest.Identity.class), BlockBuilderTest.Identity.class.getMethod("apply", Object.class), Expressions.constant("test")));
        Assert.assertEquals(("{\n" + (("  final Object _i = new org.apache.calcite.linq4j.test.BlockBuilderTest.Identity()" + ".apply(\"test\");\n") + "}\n")), Expressions.toString(bb.toBlock()));
    }

    /**
     * CALCITE-2611: unknown on one side of an or may lead to uncompilable code
     */
    @Test
    public void testOptimizeBoxedFalseEqNull() {
        BlockBuilder outer = new BlockBuilder();
        outer.append(Expressions.equal(BOXED_FALSE_EXPR, Expressions.constant(null)));
        Assert.assertEquals("Expected to optimize Boolean.FALSE = null to false", ("{\n" + ("  return false;\n" + "}\n")), Expressions.toString(outer.toBlock()));
    }

    /**
     * Class with generics to validate if {@link Expressions#call(Method, Expression...)} works.
     *
     * @param <I>
     * 		result type
     */
    static class Identity<I> implements Function<I, I> {
        @Override
        public I apply(I i) {
            return i;
        }
    }
}

/**
 * End BlockBuilderTest.java
 */
