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
package org.drools.core.util.asm;


import org.junit.Assert;
import org.junit.Test;
import org.mvel2.asm.ClassReader;


public class MethodComparerTest {
    @Test
    public void testMethodCompare() throws Exception {
        final MethodComparator comp = new MethodComparator();
        boolean result = comp.equivalent("evaluate", new ClassReader(getClassData(MethodCompareA.class)), "evaluate", new ClassReader(getClassData(MethodCompareB.class)));
        Assert.assertEquals(true, result);
        result = comp.equivalent("evaluate", new ClassReader(getClassData(MethodCompareA.class)), "evaluate2", new ClassReader(getClassData(MethodCompareA.class)));
        Assert.assertEquals(false, result);
        result = comp.equivalent("evaluate", new ClassReader(getClassData(MethodCompareB.class)), "evaluate2", new ClassReader(getClassData(MethodCompareA.class)));
        Assert.assertEquals(false, result);
        result = comp.equivalent("evaluate", new ClassReader(getClassData(MethodCompareB.class)), "evaluate", new ClassReader(getClassData(MethodCompareA.class)));
        Assert.assertEquals(true, result);
        result = comp.equivalent("evaluate", new ClassReader(getClassData(MethodCompareA.class)), "evaluate", new ClassReader(getClassData(MethodCompareA.class)));
        Assert.assertEquals(true, result);
        result = comp.equivalent("evaluate", new ClassReader(getClassData(MethodCompareA.class)), "askew", new ClassReader(getClassData(MethodCompareA.class)));
        Assert.assertEquals(false, result);
    }
}

