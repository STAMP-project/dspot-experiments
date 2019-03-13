/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.aop.aspectj;


import org.junit.Assert;
import org.junit.Test;


/**
 * Test for SPR-3522. Arguments changed on a call to proceed should be
 * visible to advice further down the invocation chain.
 *
 * @author Adrian Colyer
 * @author Chris Beams
 */
public class ProceedTests {
    private SimpleBean testBean;

    private ProceedTestingAspect firstTestAspect;

    private ProceedTestingAspect secondTestAspect;

    @Test
    public void testSimpleProceedWithChangedArgs() {
        this.testBean.setName("abc");
        Assert.assertEquals("Name changed in around advice", "ABC", this.testBean.getName());
    }

    @Test
    public void testGetArgsIsDefensive() {
        this.testBean.setAge(5);
        Assert.assertEquals("getArgs is defensive", 5, this.testBean.getAge());
    }

    @Test
    public void testProceedWithArgsInSameAspect() {
        this.testBean.setMyFloat(1.0F);
        Assert.assertTrue("value changed in around advice", ((this.testBean.getMyFloat()) > 1.9F));
        Assert.assertTrue("changed value visible to next advice in chain", ((this.firstTestAspect.getLastBeforeFloatValue()) > 1.9F));
    }

    @Test
    public void testProceedWithArgsAcrossAspects() {
        this.testBean.setSex("male");
        Assert.assertEquals("value changed in around advice", "MALE", this.testBean.getSex());
        Assert.assertEquals("changed value visible to next before advice in chain", "MALE", this.secondTestAspect.getLastBeforeStringValue());
        Assert.assertEquals("changed value visible to next around advice in chain", "MALE", this.secondTestAspect.getLastAroundStringValue());
    }
}

