/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.impl.score.director.drools.testgen.mutation;


import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;


public class TestGenHeadCuttingMutatorTest {
    private ArrayList<Integer> list = new ArrayList<>();

    @Test
    public void mutateUntilListIsEmpty() {
        TestGenHeadCuttingMutator<Integer> m = new TestGenHeadCuttingMutator(list);
        Assert.assertTrue(m.canMutate());
        // 0.8 * 25 = 20 cut
        Assert.assertEquals(5, m.mutate().size());
        Assert.assertTrue(m.canMutate());
        m.revert();
        Assert.assertEquals(25, m.getResult().size());
        Assert.assertTrue(m.canMutate());
        // 0.4 * 25 = 10 cut
        Assert.assertEquals(15, m.mutate().size());
        Assert.assertTrue(m.canMutate());
        // 10 + 0.4 * 15 = 16 cut
        Assert.assertEquals(9, m.mutate().size());
        Assert.assertTrue(m.canMutate());
        // 16 + 0.4 * 9 = 19 cut
        Assert.assertEquals(6, m.mutate().size());
        Assert.assertTrue(m.canMutate());
        m.revert();
        Assert.assertEquals(9, m.getResult().size());
        Assert.assertTrue(m.canMutate());
        // 16 + 0.2 * 9 = 17 cut
        Assert.assertEquals(8, m.mutate().size());
        Assert.assertTrue(m.canMutate());
        Assert.assertEquals(7, m.mutate().size());
        Assert.assertTrue(m.canMutate());
        Assert.assertEquals(6, m.mutate().size());
        Assert.assertTrue(m.canMutate());
        Assert.assertEquals(5, m.mutate().size());
        Assert.assertTrue(m.canMutate());
        Assert.assertEquals(4, m.mutate().size());
        Assert.assertTrue(m.canMutate());
        Assert.assertEquals(3, m.mutate().size());
        Assert.assertTrue(m.canMutate());
        Assert.assertEquals(2, m.mutate().size());
        Assert.assertTrue(m.canMutate());
        Assert.assertEquals(1, m.mutate().size());
        Assert.assertTrue(m.canMutate());
        Assert.assertEquals(0, m.mutate().size());
        Assert.assertFalse(m.canMutate());
        Assert.assertEquals(0, m.getResult().size());
    }

    @Test
    public void testImpossibleMutation() {
        TestGenHeadCuttingMutator<Integer> m = new TestGenHeadCuttingMutator(list);
        Assert.assertTrue(m.canMutate());
        Assert.assertTrue(m.canMutate());
        // 0.8 * 25 = 20 cut
        Assert.assertEquals(5, m.mutate().size());
        m.revert();
        Assert.assertTrue(m.canMutate());
        // 0.4 * 25 = 10 cut
        Assert.assertEquals(15, m.mutate().size());
        m.revert();
        Assert.assertTrue(m.canMutate());
        // 0.2 * 25 = 5 cut
        Assert.assertEquals(20, m.mutate().size());
        m.revert();
        Assert.assertTrue(m.canMutate());
        // 0.1 * 25 = 2 cut
        Assert.assertEquals(23, m.mutate().size());
        m.revert();
        Assert.assertTrue(m.canMutate());
        // 0.05 * 25 = 1 cut
        Assert.assertEquals(24, m.mutate().size());
        m.revert();
        // impossible to mutate to full list
        Assert.assertFalse(m.canMutate());
        Assert.assertEquals(25, m.getResult().size());
    }
}

