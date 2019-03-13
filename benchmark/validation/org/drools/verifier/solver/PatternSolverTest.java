/**
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.verifier.solver;


import OperatorDescrType.AND;
import OperatorDescrType.OR;
import java.util.List;
import java.util.Set;
import org.drools.verifier.VerifierComponentMockFactory;
import org.drools.verifier.components.LiteralRestriction;
import org.drools.verifier.components.Pattern;
import org.drools.verifier.data.VerifierComponent;
import org.junit.Assert;
import org.junit.Test;


public class PatternSolverTest {
    /**
     * <pre>
     *      and
     *     /   \
     *  descr  descr2
     * </pre>
     *
     * result:<br>
     * descr && descr2
     */
    @Test
    public void testAddBasicAnd() {
        Pattern pattern = VerifierComponentMockFactory.createPattern1();
        LiteralRestriction literalRestriction = LiteralRestriction.createRestriction(pattern, "");
        LiteralRestriction literalRestriction2 = LiteralRestriction.createRestriction(pattern, "");
        PatternSolver solver = new PatternSolver(pattern);
        solver.addOperator(AND);
        solver.add(literalRestriction);
        solver.add(literalRestriction2);
        solver.end();
        List<Set<VerifierComponent>> list = solver.getPossibilityLists();
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(2, list.get(0).size());
    }

    /**
     * <pre>
     *       or
     *      /  \
     *  descr descr2
     * </pre>
     *
     * result:<br>
     * descr<br>
     * or<br>
     * descr2
     */
    @Test
    public void testAddBasicOr() {
        Pattern pattern = VerifierComponentMockFactory.createPattern1();
        LiteralRestriction literalRestriction = LiteralRestriction.createRestriction(pattern, "");
        LiteralRestriction literalRestriction2 = LiteralRestriction.createRestriction(pattern, "");
        PatternSolver solver = new PatternSolver(pattern);
        solver.addOperator(OR);
        solver.add(literalRestriction);
        solver.add(literalRestriction2);
        solver.end();
        List<Set<VerifierComponent>> list = solver.getPossibilityLists();
        Assert.assertEquals(2, list.size());
        Assert.assertEquals(1, list.get(0).size());
        Assert.assertEquals(1, list.get(1).size());
    }

    /**
     * <pre>
     *       or
     *      /  \
     *  descr  and
     *         / \
     *    descr2 descr3
     * </pre>
     *
     * result:<br>
     * descr <br>
     * or<br>
     * descr2 && descr3
     */
    @Test
    public void testAddOrAnd() {
        Pattern pattern = VerifierComponentMockFactory.createPattern1();
        LiteralRestriction literalRestriction = LiteralRestriction.createRestriction(pattern, "");
        LiteralRestriction literalRestriction2 = LiteralRestriction.createRestriction(pattern, "");
        LiteralRestriction literalRestriction3 = LiteralRestriction.createRestriction(pattern, "");
        PatternSolver solver = new PatternSolver(pattern);
        solver.addOperator(OR);
        solver.add(literalRestriction);
        solver.addOperator(AND);
        solver.add(literalRestriction2);
        solver.add(literalRestriction3);
        solver.end();
        solver.end();
        List<Set<VerifierComponent>> list = solver.getPossibilityLists();
        Assert.assertEquals(2, list.size());
        Assert.assertEquals(1, list.get(0).size());
        Assert.assertTrue(list.get(0).contains(literalRestriction));
        Assert.assertEquals(2, list.get(1).size());
        Assert.assertTrue(list.get(1).contains(literalRestriction2));
        Assert.assertTrue(list.get(1).contains(literalRestriction3));
    }

    /**
     * <pre>
     *       and
     *      /  \
     *  descr   or
     *         / \
     *    descr2 descr3
     * </pre>
     *
     * result:<br>
     * descr && descr2 <br>
     * or<br>
     * descr && descr3
     */
    @Test
    public void testAddAndOr() {
        Pattern pattern = VerifierComponentMockFactory.createPattern1();
        LiteralRestriction literalRestriction = LiteralRestriction.createRestriction(pattern, "");
        LiteralRestriction literalRestriction2 = LiteralRestriction.createRestriction(pattern, "");
        LiteralRestriction literalRestriction3 = LiteralRestriction.createRestriction(pattern, "");
        PatternSolver solver = new PatternSolver(pattern);
        solver.addOperator(AND);
        solver.add(literalRestriction);
        solver.addOperator(OR);
        solver.add(literalRestriction2);
        solver.add(literalRestriction3);
        solver.end();
        solver.end();
        List<Set<VerifierComponent>> list = solver.getPossibilityLists();
        Assert.assertEquals(2, list.size());
        Assert.assertEquals(2, list.get(0).size());
        Assert.assertTrue(list.get(0).contains(literalRestriction));
        Assert.assertTrue(list.get(0).contains(literalRestriction2));
        Assert.assertEquals(2, list.get(1).size());
        Assert.assertTrue(list.get(1).contains(literalRestriction));
        Assert.assertTrue(list.get(1).contains(literalRestriction3));
    }

    /**
     * <pre>
     *            and
     *         /        \
     *       or          or
     *      /  \         / \
     * descr descr2 descr3 descr4
     * </pre>
     *
     * result:<br>
     * descr && descr3<br>
     * or<br>
     * descr && descr4<br>
     * or<br>
     * descr2 && descr3<br>
     * or<br>
     * descr2 && descr4
     */
    @Test
    public void testAddAndOrOr() {
        Pattern pattern = VerifierComponentMockFactory.createPattern1();
        LiteralRestriction literalRestriction = LiteralRestriction.createRestriction(pattern, "");
        LiteralRestriction literalRestriction2 = LiteralRestriction.createRestriction(pattern, "");
        LiteralRestriction literalRestriction3 = LiteralRestriction.createRestriction(pattern, "");
        LiteralRestriction literalRestriction4 = LiteralRestriction.createRestriction(pattern, "");
        PatternSolver solver = new PatternSolver(pattern);
        solver.addOperator(AND);
        solver.addOperator(OR);
        solver.add(literalRestriction);
        solver.add(literalRestriction2);
        solver.end();
        solver.addOperator(OR);
        solver.add(literalRestriction3);
        solver.add(literalRestriction4);
        solver.end();
        solver.end();
        List<Set<VerifierComponent>> list = solver.getPossibilityLists();
        Assert.assertEquals(4, list.size());
        Assert.assertEquals(2, list.get(0).size());
        Assert.assertTrue(list.get(0).contains(literalRestriction));
        Assert.assertTrue(list.get(0).contains(literalRestriction3));
        Assert.assertEquals(2, list.get(1).size());
        Assert.assertTrue(list.get(1).contains(literalRestriction));
        Assert.assertTrue(list.get(1).contains(literalRestriction4));
        Assert.assertEquals(2, list.get(2).size());
        Assert.assertTrue(list.get(2).contains(literalRestriction2));
        Assert.assertTrue(list.get(2).contains(literalRestriction3));
        Assert.assertEquals(2, list.get(3).size());
        Assert.assertTrue(list.get(3).contains(literalRestriction2));
        Assert.assertTrue(list.get(3).contains(literalRestriction4));
    }

    /**
     * <pre>
     *             or
     *         /        \
     *       and         and
     *      /  \         / \
     * descr descr2 descr3 descr4
     * </pre>
     *
     * result:<br>
     * descr && descr2<br>
     * or<br>
     * descr3 && descr4
     */
    @Test
    public void testAddOrAndAnd() {
        Pattern pattern = VerifierComponentMockFactory.createPattern1();
        LiteralRestriction literalRestriction = LiteralRestriction.createRestriction(pattern, "");
        LiteralRestriction literalRestriction2 = LiteralRestriction.createRestriction(pattern, "");
        LiteralRestriction literalRestriction3 = LiteralRestriction.createRestriction(pattern, "");
        LiteralRestriction literalRestriction4 = LiteralRestriction.createRestriction(pattern, "");
        PatternSolver solver = new PatternSolver(pattern);
        solver.addOperator(OR);
        solver.addOperator(AND);
        solver.add(literalRestriction);
        solver.add(literalRestriction2);
        solver.end();
        solver.addOperator(AND);
        solver.add(literalRestriction3);
        solver.add(literalRestriction4);
        solver.end();
        solver.end();
        List<Set<VerifierComponent>> list = solver.getPossibilityLists();
        Assert.assertEquals(2, list.size());
        Assert.assertEquals(2, list.get(0).size());
        Assert.assertTrue(list.get(0).contains(literalRestriction));
        Assert.assertTrue(list.get(0).contains(literalRestriction2));
        Assert.assertEquals(2, list.get(1).size());
        Assert.assertTrue(list.get(1).contains(literalRestriction3));
        Assert.assertTrue(list.get(1).contains(literalRestriction4));
    }

    /**
     * <pre>
     *             or
     *         /        \
     *       and         or
     *      /  \         / \
     * descr descr2 descr3 descr4
     * </pre>
     *
     * result:<br>
     * descr && descr2<br>
     * or<br>
     * descr3<br>
     * or<br>
     * descr4
     */
    @Test
    public void testAddOrAndOr() {
        Pattern pattern = VerifierComponentMockFactory.createPattern1();
        LiteralRestriction literalRestriction = LiteralRestriction.createRestriction(pattern, "");
        LiteralRestriction literalRestriction2 = LiteralRestriction.createRestriction(pattern, "");
        LiteralRestriction literalRestriction3 = LiteralRestriction.createRestriction(pattern, "");
        LiteralRestriction literalRestriction4 = LiteralRestriction.createRestriction(pattern, "");
        PatternSolver solver = new PatternSolver(pattern);
        solver.addOperator(OR);
        solver.addOperator(AND);
        solver.add(literalRestriction);
        solver.add(literalRestriction2);
        solver.end();
        solver.addOperator(OR);
        solver.add(literalRestriction3);
        solver.add(literalRestriction4);
        solver.end();
        solver.end();
        List<Set<VerifierComponent>> list = solver.getPossibilityLists();
        Assert.assertEquals(3, list.size());
        Assert.assertEquals(2, list.get(0).size());
        Assert.assertTrue(list.get(0).contains(literalRestriction));
        Assert.assertTrue(list.get(0).contains(literalRestriction2));
        Assert.assertEquals(1, list.get(1).size());
        Assert.assertTrue(list.get(1).contains(literalRestriction3));
        Assert.assertEquals(1, list.get(2).size());
        Assert.assertTrue(list.get(2).contains(literalRestriction4));
    }

    /**
     * <pre>
     *                   and
     *          /         |      \
     *       and         or       descr5
     *      /  \         / \
     * descr descr2 descr3 descr4
     * </pre>
     *
     * result:<br>
     * descr && descr2 && descr3 && descr5<br>
     * or<br>
     * descr && descr2 && descr4 && descr5<br>
     */
    @Test
    public void testAddOrAndOrDescr() {
        Pattern pattern = VerifierComponentMockFactory.createPattern1();
        LiteralRestriction literalRestriction = LiteralRestriction.createRestriction(pattern, "");
        LiteralRestriction literalRestriction2 = LiteralRestriction.createRestriction(pattern, "");
        LiteralRestriction literalRestriction3 = LiteralRestriction.createRestriction(pattern, "");
        LiteralRestriction literalRestriction4 = LiteralRestriction.createRestriction(pattern, "");
        LiteralRestriction literalRestriction5 = LiteralRestriction.createRestriction(pattern, "");
        PatternSolver solver = new PatternSolver(pattern);
        solver.addOperator(AND);
        solver.addOperator(AND);
        solver.add(literalRestriction);
        solver.add(literalRestriction2);
        solver.end();
        solver.addOperator(OR);
        solver.add(literalRestriction3);
        solver.add(literalRestriction4);
        solver.end();
        solver.add(literalRestriction5);
        solver.end();
        List<Set<VerifierComponent>> list = solver.getPossibilityLists();
        Assert.assertEquals(2, list.size());
        Assert.assertEquals(4, list.get(0).size());
        Assert.assertTrue(list.get(0).contains(literalRestriction));
        Assert.assertTrue(list.get(0).contains(literalRestriction2));
        Assert.assertTrue(list.get(0).contains(literalRestriction3));
        Assert.assertTrue(list.get(0).contains(literalRestriction5));
        Assert.assertEquals(4, list.get(1).size());
        Assert.assertTrue(list.get(1).contains(literalRestriction));
        Assert.assertTrue(list.get(1).contains(literalRestriction2));
        Assert.assertTrue(list.get(1).contains(literalRestriction4));
        Assert.assertTrue(list.get(1).contains(literalRestriction4));
    }
}

