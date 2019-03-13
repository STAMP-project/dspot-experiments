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
package org.drools.core.reteoo.builder;


import AfterEvaluatorDefinition.AFTER;
import ValueType.OBJECT_TYPE;
import org.drools.core.base.ClassObjectType;
import org.drools.core.base.evaluators.AfterEvaluatorDefinition;
import org.drools.core.base.extractors.SelfReferenceClassFieldReader;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.GroupElement;
import org.drools.core.rule.GroupElement.Type;
import org.drools.core.rule.Pattern;
import org.drools.core.test.model.StockTick;
import org.drools.core.time.Interval;
import org.drools.core.time.TemporalDependencyMatrix;
import org.junit.Assert;
import org.junit.Test;


public class BuildUtilsTest {
    private BuildUtils utils;

    /**
     * Test method for {@link org.drools.core.reteoo.builder.BuildUtils#calculateTemporalDistance(org.drools.core.rule.GroupElement)}.
     */
    @Test
    public void testCalculateTemporalDistance() {
        // input is here just for "documentation" purposes
        Interval[][] input = new Interval[][]{ new Interval[]{ new Interval(0, 0), new Interval((-2), 2), new Interval((-3), 4), new Interval(Interval.MIN, Interval.MAX), new Interval(Interval.MIN, Interval.MAX) }, new Interval[]{ new Interval((-2), 2), new Interval(0, 0), new Interval(Interval.MIN, Interval.MAX), new Interval(1, 2), new Interval(Interval.MIN, Interval.MAX) }, new Interval[]{ new Interval((-4), 3), new Interval(Interval.MIN, Interval.MAX), new Interval(0, 0), new Interval(2, 3), new Interval(Interval.MIN, Interval.MAX) }, new Interval[]{ new Interval(Interval.MIN, Interval.MAX), new Interval((-2), (-1)), new Interval((-3), (-2)), new Interval(0, 0), new Interval(1, 10) }, new Interval[]{ new Interval(Interval.MIN, Interval.MAX), new Interval(Interval.MIN, Interval.MAX), new Interval(Interval.MIN, Interval.MAX), new Interval((-10), (-1)), new Interval(0, 0) } };
        Interval[][] expected = new Interval[][]{ new Interval[]{ new Interval(0, 0), new Interval((-2), 2), new Interval((-3), 2), new Interval((-1), 4), new Interval(0, 14) }, new Interval[]{ new Interval((-2), 2), new Interval(0, 0), new Interval((-2), 0), new Interval(1, 2), new Interval(2, 12) }, new Interval[]{ new Interval((-2), 3), new Interval(0, 2), new Interval(0, 0), new Interval(2, 3), new Interval(3, 13) }, new Interval[]{ new Interval((-4), 1), new Interval((-2), (-1)), new Interval((-3), (-2)), new Interval(0, 0), new Interval(1, 10) }, new Interval[]{ new Interval((-14), 0), new Interval((-12), (-2)), new Interval((-13), (-3)), new Interval((-10), (-1)), new Interval(0, 0) } };
        AfterEvaluatorDefinition evals = new AfterEvaluatorDefinition();
        ClassObjectType ot = new ClassObjectType(StockTick.class, true);
        Pattern a = new Pattern(0, ot, "$a");
        Pattern b = new Pattern(1, ot, "$b");
        b.addConstraint(new org.drools.core.rule.constraint.EvaluatorConstraint(new Declaration[]{ a.getDeclaration() }, evals.getEvaluator(OBJECT_TYPE, AFTER, "-2,2"), new SelfReferenceClassFieldReader(StockTick.class)));
        Pattern c = new Pattern(2, ot, "$c");
        c.addConstraint(new org.drools.core.rule.constraint.EvaluatorConstraint(new Declaration[]{ a.getDeclaration() }, evals.getEvaluator(OBJECT_TYPE, AFTER, "-3,4"), new SelfReferenceClassFieldReader(StockTick.class)));
        Pattern d = new Pattern(3, ot, "$d");
        d.addConstraint(new org.drools.core.rule.constraint.EvaluatorConstraint(new Declaration[]{ b.getDeclaration() }, evals.getEvaluator(OBJECT_TYPE, AFTER, "1,2"), new SelfReferenceClassFieldReader(StockTick.class)));
        d.addConstraint(new org.drools.core.rule.constraint.EvaluatorConstraint(new Declaration[]{ c.getDeclaration() }, evals.getEvaluator(OBJECT_TYPE, AFTER, "2,3"), new SelfReferenceClassFieldReader(StockTick.class)));
        Pattern e = new Pattern(4, ot, "$e");
        e.addConstraint(new org.drools.core.rule.constraint.EvaluatorConstraint(new Declaration[]{ d.getDeclaration() }, evals.getEvaluator(OBJECT_TYPE, AFTER, "1,10"), new SelfReferenceClassFieldReader(StockTick.class)));
        GroupElement not = new GroupElement(Type.NOT);
        not.addChild(e);
        GroupElement and = new GroupElement(Type.AND);
        and.addChild(a);
        and.addChild(b);
        and.addChild(c);
        and.addChild(d);
        and.addChild(not);
        TemporalDependencyMatrix matrix = utils.calculateTemporalDistance(and);
        // printMatrix( matrix.getMatrix() );
        assertEqualsMatrix(expected, matrix.getMatrix());
        Assert.assertEquals(15, matrix.getExpirationOffset(a));
        Assert.assertEquals(11, matrix.getExpirationOffset(d));
        Assert.assertEquals(1, matrix.getExpirationOffset(e));
    }
}

