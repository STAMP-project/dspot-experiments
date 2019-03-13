/**
 * Copyright 2005-2019 Dozer Project
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
package com.github.dozermapper.core.functional_tests;


import com.github.dozermapper.core.vo.inheritance.twolevel.A;
import com.github.dozermapper.core.vo.inheritance.twolevel.B;
import com.github.dozermapper.core.vo.inheritance.twolevel.C;
import org.junit.Assert;
import org.junit.Test;


public class InheritanceTwoLevelTest extends AbstractFunctionalTest {
    @Test
    public void testMapping_TwoLevels() {
        C source = newInstance(C.class);
        source.setA("A");
        source.setB("B");
        B destination = mapper.map(source, B.class);
        Assert.assertNotNull(destination);
        Assert.assertEquals("A", destination.getA());
        Assert.assertNotNull("B", destination.getB());
    }

    @Test
    public void testMapping_TwoLevelsReverse() {
        B source = newInstance(B.class);
        source.setA("A");
        source.setB("B");
        C destination = mapper.map(source, C.class);
        Assert.assertNotNull(destination);
        Assert.assertEquals("A", destination.getA());
        Assert.assertNotNull("B", destination.getB());
    }

    @Test
    public void testMapping_OneLevel() {
        C source = newInstance(C.class);
        source.setA("A");
        source.setB("B");
        A destination = mapper.map(source, A.class);
        Assert.assertNotNull(destination);
        Assert.assertEquals("A", destination.getA());
    }
}

