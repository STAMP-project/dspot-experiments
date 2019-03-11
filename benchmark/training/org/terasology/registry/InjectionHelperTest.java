/**
 * Copyright 2017 MovingBlocks
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
package org.terasology.registry;


import java.util.NoSuchElementException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.terasology.context.Context;
import org.terasology.context.internal.ContextImpl;


public class InjectionHelperTest {
    private InjectionHelperTest.ServiceA serviceA;

    private InjectionHelperTest.ServiceB serviceB;

    @Test
    public void testSharePopulatesCoreRegistry() {
        Assert.assertThat(CoreRegistry.get(InjectionHelperTest.ServiceA.class), CoreMatchers.is(CoreMatchers.nullValue()));
        InjectionHelper.share(serviceA);
        Assert.assertThat(CoreRegistry.get(InjectionHelperTest.ServiceA.class), CoreMatchers.is(serviceA));
    }

    @Test
    public void testShareRequiresShareAnnotation() {
        InjectionHelper.share(new InjectionHelperTest.ServiceAImplNoAnnotation());
        Assert.assertThat(CoreRegistry.get(InjectionHelperTest.ServiceA.class), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testDefaultFieldInjection() {
        InjectionHelper.share(serviceA);
        InjectionHelper.share(serviceB);
        // no default constructor required
        InjectionHelperTest.FieldInjectionAB fieldInjectionAB = new InjectionHelperTest.FieldInjectionAB();
        InjectionHelper.inject(fieldInjectionAB);
        Assert.assertThat(fieldInjectionAB.getServiceA(), CoreMatchers.is(serviceA));
        Assert.assertThat(fieldInjectionAB.getServiceB(), CoreMatchers.is(serviceB));
    }

    @Test
    public void testInjectUnavailableObject() {
        InjectionHelper.share(serviceA);
        // InjectionHelper.share(serviceB);
        InjectionHelperTest.FieldInjectionAB fieldInjectionAB = new InjectionHelperTest.FieldInjectionAB();
        InjectionHelper.inject(fieldInjectionAB);
        Assert.assertThat(fieldInjectionAB.getServiceA(), CoreMatchers.is(serviceA));
        Assert.assertThat(fieldInjectionAB.getServiceB(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testDefaultConstructorInjection() {
        Context context = new ContextImpl();
        context.put(InjectionHelperTest.ServiceA.class, serviceA);
        context.put(InjectionHelperTest.ServiceB.class, serviceB);
        InjectionHelperTest.ConstructorAB constructorAB = InjectionHelper.createWithConstructorInjection(InjectionHelperTest.ConstructorAB.class, context);
        // the two-arg constructor should be used as it has the most parameters and all can be populated
        Assert.assertThat(constructorAB.getServiceA(), CoreMatchers.is(serviceA));
        Assert.assertThat(constructorAB.getServiceB(), CoreMatchers.is(serviceB));
    }

    @Test
    public void testConstructorInjectionNotAllParametersPopulated() {
        Context context = new ContextImpl();
        context.put(InjectionHelperTest.ServiceA.class, serviceA);
        // context.put(ServiceB.class, serviceB);
        InjectionHelperTest.ConstructorAB constructorAB = InjectionHelper.createWithConstructorInjection(InjectionHelperTest.ConstructorAB.class, context);
        // the two-arg constructor can't be populated because serviceB is not available
        // there is no fallback for a constructor with only serviceA, so the default constructor is called
        Assert.assertThat(constructorAB.getServiceA(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(constructorAB.getServiceB(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testConstructorInjectionNotAllParametersPopulatedFallback() {
        Context context = new ContextImpl();
        context.put(InjectionHelperTest.ServiceA.class, serviceA);
        // context.put(ServiceB.class, serviceB);
        InjectionHelperTest.ConstructorA_AB constructorA_AB = InjectionHelper.createWithConstructorInjection(InjectionHelperTest.ConstructorA_AB.class, context);
        // the one-arg constructor is used as it can be populated  with serviceA which is available
        Assert.assertThat(constructorA_AB.getServiceA(), CoreMatchers.is(serviceA));
        Assert.assertThat(constructorA_AB.getServiceB(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test(expected = NoSuchElementException.class)
    public void testConstructorInjectionNoDefaultConstructorForFallback() {
        Context context = new ContextImpl();
        context.put(InjectionHelperTest.ServiceA.class, serviceA);
        // context.put(ServiceB.class, serviceB);
        // there is only one constructor for serviceB which is not present on the context.
        // a default constructor is not available, so the injection fails.
        InjectionHelper.createWithConstructorInjection(InjectionHelperTest.ConstructorB.class, context);
    }

    // test classes and interfaces for injection
    private interface ServiceA {}

    private interface ServiceB {}

    @Share(InjectionHelperTest.ServiceA.class)
    private static class ServiceAImpl implements InjectionHelperTest.ServiceA {}

    private static class ServiceAImplNoAnnotation implements InjectionHelperTest.ServiceA {}

    @Share(InjectionHelperTest.ServiceB.class)
    private static class ServiceBImpl implements InjectionHelperTest.ServiceB {}

    private static class FieldInjectionAB {
        @In
        private InjectionHelperTest.ServiceA serviceA;

        @In
        private InjectionHelperTest.ServiceB serviceB;

        public InjectionHelperTest.ServiceA getServiceA() {
            return serviceA;
        }

        public InjectionHelperTest.ServiceB getServiceB() {
            return serviceB;
        }
    }

    public static class ConstructorAB {
        private InjectionHelperTest.ServiceA serviceA;

        private InjectionHelperTest.ServiceB serviceB;

        public ConstructorAB() {
        }

        public ConstructorAB(InjectionHelperTest.ServiceA serviceA, InjectionHelperTest.ServiceB serviceB) {
            this.serviceA = serviceA;
            this.serviceB = serviceB;
        }

        public InjectionHelperTest.ServiceA getServiceA() {
            return serviceA;
        }

        public InjectionHelperTest.ServiceB getServiceB() {
            return serviceB;
        }
    }

    public static class ConstructorA_AB {
        private InjectionHelperTest.ServiceA serviceA;

        private InjectionHelperTest.ServiceB serviceB;

        public ConstructorA_AB() {
        }

        public ConstructorA_AB(InjectionHelperTest.ServiceA serviceA) {
            this.serviceA = serviceA;
        }

        public ConstructorA_AB(InjectionHelperTest.ServiceA serviceA, InjectionHelperTest.ServiceB serviceB) {
            this.serviceA = serviceA;
            this.serviceB = serviceB;
        }

        public InjectionHelperTest.ServiceA getServiceA() {
            return serviceA;
        }

        public InjectionHelperTest.ServiceB getServiceB() {
            return serviceB;
        }
    }

    public static class ConstructorB {
        private InjectionHelperTest.ServiceB serviceB;

        public ConstructorB(InjectionHelperTest.ServiceB serviceB) {
            this.serviceB = serviceB;
        }

        public InjectionHelperTest.ServiceB getServiceB() {
            return serviceB;
        }
    }
}

