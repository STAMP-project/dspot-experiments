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


import com.github.dozermapper.core.vo.excluded.OneA;
import com.github.dozermapper.core.vo.excluded.OneB;
import com.github.dozermapper.core.vo.excluded.TwoA;
import com.github.dozermapper.core.vo.excluded.TwoB;
import com.github.dozermapper.core.vo.excluded.ZeroA;
import com.github.dozermapper.core.vo.excluded.ZeroB;
import org.junit.Assert;
import org.junit.Test;


public class ExcludeFieldTest extends AbstractFunctionalTest {
    @Test
    public void testExcludedField_SimpleLevel() {
        ZeroB zeroB = newInstance(ZeroB.class);
        zeroB.setId(Integer.valueOf("10"));
        ZeroA zeroA = mapper.map(zeroB, ZeroA.class);
        Assert.assertNull(zeroA.getId());
        Assert.assertEquals(Integer.valueOf("10"), zeroB.getId());
    }

    @Test
    public void testExcludedField_SimpleReverse() {
        ZeroA zeroA = newInstance(ZeroA.class);
        zeroA.setId(Integer.valueOf("5"));
        ZeroB zeroB = mapper.map(zeroA, ZeroB.class);
        Assert.assertEquals(Integer.valueOf("5"), zeroA.getId());
        Assert.assertNull(zeroB.getId());
    }

    @Test
    public void testExcludedField_OneLevel() {
        OneB oneB = newInstance(OneB.class);
        oneB.setId(Integer.valueOf("10"));
        OneA oneA = mapper.map(oneB, OneA.class);
        Assert.assertNull(oneA.getId());
        Assert.assertEquals(Integer.valueOf("10"), oneB.getId());
    }

    @Test
    public void testExcludedField_OneLevelReverse() {
        OneA oneA = newInstance(OneA.class);
        oneA.setId(Integer.valueOf("5"));
        OneB oneB = mapper.map(oneA, OneB.class);
        Assert.assertEquals(Integer.valueOf("5"), oneA.getId());
        Assert.assertNull(oneB.getId());
    }

    @Test
    public void testExcludedField_TwoLevel() {
        TwoB twoB = new TwoB();
        twoB.setId(Integer.valueOf("10"));
        TwoA twoA = mapper.map(twoB, TwoA.class);
        Assert.assertNull(twoA.getId());
        Assert.assertEquals(Integer.valueOf("10"), twoB.getId());
    }

    @Test
    public void testExcludedField_TwoLevelReverse() {
        TwoA twoA = newInstance(TwoA.class);
        twoA.setId(Integer.valueOf("5"));
        TwoB twoB = mapper.map(twoA, TwoB.class);
        Assert.assertEquals(Integer.valueOf("5"), twoA.getId());
        Assert.assertNull(twoB.getId());
    }
}

