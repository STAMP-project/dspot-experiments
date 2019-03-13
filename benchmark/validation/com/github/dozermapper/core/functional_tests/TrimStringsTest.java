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


import com.github.dozermapper.core.vo.AnotherTestObject;
import com.github.dozermapper.core.vo.AnotherTestObjectPrime;
import com.github.dozermapper.core.vo.SimpleObj;
import com.github.dozermapper.core.vo.SimpleObjPrime;
import com.github.dozermapper.core.vo.TestObject;
import com.github.dozermapper.core.vo.TestObjectPrime;
import org.junit.Assert;
import org.junit.Test;


public class TrimStringsTest extends AbstractFunctionalTest {
    @Test
    public void testTrimStrings_Global() {
        AnotherTestObject src = newInstance(AnotherTestObject.class);
        src.setField3("      valueNeedingTrimmed       ");
        src.setField4("      anotherValueNeedingTrimmed       ");
        src.setField5("  127 ");
        AnotherTestObjectPrime dest = mapper.map(src, AnotherTestObjectPrime.class);
        Assert.assertEquals("valueNeedingTrimmed", dest.getField3());
        Assert.assertEquals("anotherValueNeedingTrimmed", dest.getTo().getOne());
        Assert.assertEquals("field 5 not trimmed", Integer.valueOf("127"), dest.getField5());
    }

    @Test
    public void testTrimStrings_ClassMapLevel() {
        TestObject src = newInstance(TestObject.class);
        String value = "    shouldNotBeNeedingTrimmed     ";
        src.setOne(value);
        TestObjectPrime dest = mapper.map(src, TestObjectPrime.class);
        Assert.assertEquals(value, dest.getOnePrime());
    }

    @Test
    public void testTrimStrings_ImplicitMapping() {
        SimpleObj src = newInstance(SimpleObj.class);
        src.setField1("      valueNeedingTrimmed       ");
        SimpleObjPrime dest = mapper.map(src, SimpleObjPrime.class);
        Assert.assertEquals("valueNeedingTrimmed", dest.getField1());
    }
}

