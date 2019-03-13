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


import java.io.Serializable;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.BeanNameAware;


/**
 *
 *
 * @author Adrian Colyer
 * @author Chris Beams
 */
public class DeclarationOrderIndependenceTests {
    private TopsyTurvyAspect aspect;

    private TopsyTurvyTarget target;

    @Test
    public void testTargetIsSerializable() {
        Assert.assertTrue("target bean is serializable", ((this.target) instanceof Serializable));
    }

    @Test
    public void testTargetIsBeanNameAware() {
        Assert.assertTrue("target bean is bean name aware", ((this.target) instanceof BeanNameAware));
    }

    @Test
    public void testBeforeAdviceFiringOk() {
        AspectCollaborator collab = new AspectCollaborator();
        this.aspect.setCollaborator(collab);
        this.target.doSomething();
        Assert.assertTrue("before advice fired", collab.beforeFired);
    }

    @Test
    public void testAroundAdviceFiringOk() {
        AspectCollaborator collab = new AspectCollaborator();
        this.aspect.setCollaborator(collab);
        this.target.getX();
        Assert.assertTrue("around advice fired", collab.aroundFired);
    }

    @Test
    public void testAfterReturningFiringOk() {
        AspectCollaborator collab = new AspectCollaborator();
        this.aspect.setCollaborator(collab);
        this.target.getX();
        Assert.assertTrue("after returning advice fired", collab.afterReturningFired);
    }

    /**
     * public visibility is required
     */
    public static class BeanNameAwareMixin implements BeanNameAware {
        @SuppressWarnings("unused")
        private String beanName;

        @Override
        public void setBeanName(String name) {
            this.beanName = name;
        }
    }

    /**
     * public visibility is required
     */
    @SuppressWarnings("serial")
    public static class SerializableMixin implements Serializable {}
}

