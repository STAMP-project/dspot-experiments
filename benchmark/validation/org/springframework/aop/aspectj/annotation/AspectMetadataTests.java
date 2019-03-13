/**
 * Copyright 2002-2012 the original author or authors.
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
package org.springframework.aop.aspectj.annotation;


import PerClauseKind.PERTARGET;
import PerClauseKind.PERTHIS;
import PerClauseKind.SINGLETON;
import Pointcut.TRUE;
import org.junit.Assert;
import org.junit.Test;
import test.aop.PerTargetAspect;


/**
 *
 *
 * @since 2.0
 * @author Rod Johnson
 * @author Chris Beams
 */
public class AspectMetadataTests {
    @Test(expected = IllegalArgumentException.class)
    public void testNotAnAspect() {
        new AspectMetadata(String.class, "someBean");
    }

    @Test
    public void testSingletonAspect() {
        AspectMetadata am = new AspectMetadata(AbstractAspectJAdvisorFactoryTests.ExceptionAspect.class, "someBean");
        Assert.assertFalse(am.isPerThisOrPerTarget());
        Assert.assertSame(TRUE, am.getPerClausePointcut());
        Assert.assertEquals(SINGLETON, am.getAjType().getPerClause().getKind());
    }

    @Test
    public void testPerTargetAspect() {
        AspectMetadata am = new AspectMetadata(PerTargetAspect.class, "someBean");
        Assert.assertTrue(am.isPerThisOrPerTarget());
        Assert.assertNotSame(TRUE, am.getPerClausePointcut());
        Assert.assertEquals(PERTARGET, am.getAjType().getPerClause().getKind());
    }

    @Test
    public void testPerThisAspect() {
        AspectMetadata am = new AspectMetadata(PerThisAspect.class, "someBean");
        Assert.assertTrue(am.isPerThisOrPerTarget());
        Assert.assertNotSame(TRUE, am.getPerClausePointcut());
        Assert.assertEquals(PERTHIS, am.getAjType().getPerClause().getKind());
    }
}

