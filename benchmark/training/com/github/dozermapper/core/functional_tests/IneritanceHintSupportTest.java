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


import com.github.dozermapper.core.vo.inheritance.hints.Base;
import com.github.dozermapper.core.vo.inheritance.hints.Base2;
import com.github.dozermapper.core.vo.inheritance.hints.BaseA;
import com.github.dozermapper.core.vo.inheritance.hints.BaseB;
import com.github.dozermapper.core.vo.inheritance.hints.Source;
import com.github.dozermapper.core.vo.inheritance.hints.Target;
import java.util.HashSet;
import org.junit.Assert;
import org.junit.Test;


public class IneritanceHintSupportTest extends AbstractFunctionalTest {
    @Test
    public void test_simple() {
        Source source = new Source();
        HashSet<Base> set = new HashSet<>();
        set.add(new BaseA());
        set.add(new BaseB());
        source.setSet(set);
        Target result = mapper.map(source, Target.class);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getSet());
        Assert.assertEquals(2, result.getSet().size());
        Object[] objects = result.getSet().toArray();
        Assert.assertTrue(((objects[0]) instanceof Base2));
        Assert.assertTrue(((objects[1]) instanceof Base2));
        Assert.assertNotSame(objects[0].getClass(), objects[1].getClass());
    }
}

