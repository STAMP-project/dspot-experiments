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
package org.springframework.aop.aspectj.annotation;


import ReflectiveAspectJAdvisorFactory.SyntheticInstantiationAdvisor;
import java.io.FileNotFoundException;
import java.lang.reflect.UndeclaredThrowableException;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.DeclarePrecedence;
import org.aspectj.lang.annotation.Pointcut;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.AopConfigException;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.tests.sample.beans.ITestBean;
import org.springframework.tests.sample.beans.TestBean;
import test.aop.Lockable;
import test.aop.PerTargetAspect;
import test.aop.TwoAdviceAspect;


/**
 * Abstract tests for AspectJAdvisorFactory.
 * See subclasses for tests of concrete factories.
 *
 * @author Rod Johnson
 * @author Chris Beams
 * @author Phillip Webb
 */
public abstract class AbstractAspectJAdvisorFactoryTests {
    @Test
    public void testRejectsPerCflowAspect() {
        try {
            getFixture().getAdvisors(new SingletonMetadataAwareAspectInstanceFactory(new AbstractAspectJAdvisorFactoryTests.PerCflowAspect(), "someBean"));
            Assert.fail("Cannot accept cflow");
        } catch (AopConfigException ex) {
            Assert.assertTrue(ex.getMessage().contains("PERCFLOW"));
        }
    }

    @Test
    public void testRejectsPerCflowBelowAspect() {
        try {
            getFixture().getAdvisors(new SingletonMetadataAwareAspectInstanceFactory(new AbstractAspectJAdvisorFactoryTests.PerCflowBelowAspect(), "someBean"));
            Assert.fail("Cannot accept cflowbelow");
        } catch (AopConfigException ex) {
            Assert.assertTrue(ex.getMessage().contains("PERCFLOWBELOW"));
        }
    }

    @Test
    public void testPerTargetAspect() throws NoSuchMethodException, SecurityException {
        TestBean target = new TestBean();
        int realAge = 65;
        target.setAge(realAge);
        TestBean itb = ((TestBean) (createProxy(target, getFixture().getAdvisors(new SingletonMetadataAwareAspectInstanceFactory(new PerTargetAspect(), "someBean")), TestBean.class)));
        Assert.assertEquals("Around advice must NOT apply", realAge, itb.getAge());
        Advised advised = ((Advised) (itb));
        ReflectiveAspectJAdvisorFactory.SyntheticInstantiationAdvisor sia = ((ReflectiveAspectJAdvisorFactory.SyntheticInstantiationAdvisor) (advised.getAdvisors()[1]));
        Assert.assertTrue(sia.getPointcut().getMethodMatcher().matches(TestBean.class.getMethod("getSpouse"), null));
        InstantiationModelAwarePointcutAdvisorImpl imapa = ((InstantiationModelAwarePointcutAdvisorImpl) (advised.getAdvisors()[3]));
        LazySingletonAspectInstanceFactoryDecorator maaif = ((LazySingletonAspectInstanceFactoryDecorator) (imapa.getAspectInstanceFactory()));
        Assert.assertFalse(maaif.isMaterialized());
        // Check that the perclause pointcut is valid
        Assert.assertTrue(maaif.getAspectMetadata().getPerClausePointcut().getMethodMatcher().matches(TestBean.class.getMethod("getSpouse"), null));
        Assert.assertNotSame(imapa.getDeclaredPointcut(), imapa.getPointcut());
        // Hit the method in the per clause to instantiate the aspect
        itb.getSpouse();
        Assert.assertTrue(maaif.isMaterialized());
        Assert.assertEquals("Around advice must apply", 0, itb.getAge());
        Assert.assertEquals("Around advice must apply", 1, itb.getAge());
    }

    @Test
    public void testMultiplePerTargetAspects() throws NoSuchMethodException, SecurityException {
        TestBean target = new TestBean();
        int realAge = 65;
        target.setAge(realAge);
        List<Advisor> advisors = new LinkedList<>();
        PerTargetAspect aspect1 = new PerTargetAspect();
        aspect1.count = 100;
        aspect1.setOrder(10);
        advisors.addAll(getFixture().getAdvisors(new SingletonMetadataAwareAspectInstanceFactory(aspect1, "someBean1")));
        PerTargetAspect aspect2 = new PerTargetAspect();
        aspect2.setOrder(5);
        advisors.addAll(getFixture().getAdvisors(new SingletonMetadataAwareAspectInstanceFactory(aspect2, "someBean2")));
        Collections.sort(advisors, new OrderComparator());
        TestBean itb = ((TestBean) (createProxy(target, advisors, TestBean.class)));
        Assert.assertEquals("Around advice must NOT apply", realAge, itb.getAge());
        // Hit the method in the per clause to instantiate the aspect
        itb.getSpouse();
        Assert.assertEquals("Around advice must apply", 0, itb.getAge());
        Assert.assertEquals("Around advice must apply", 1, itb.getAge());
    }

    @Test
    public void testMultiplePerTargetAspectsWithOrderAnnotation() throws NoSuchMethodException, SecurityException {
        TestBean target = new TestBean();
        int realAge = 65;
        target.setAge(realAge);
        List<Advisor> advisors = new LinkedList<>();
        AbstractAspectJAdvisorFactoryTests.PerTargetAspectWithOrderAnnotation10 aspect1 = new AbstractAspectJAdvisorFactoryTests.PerTargetAspectWithOrderAnnotation10();
        aspect1.count = 100;
        advisors.addAll(getFixture().getAdvisors(new SingletonMetadataAwareAspectInstanceFactory(aspect1, "someBean1")));
        AbstractAspectJAdvisorFactoryTests.PerTargetAspectWithOrderAnnotation5 aspect2 = new AbstractAspectJAdvisorFactoryTests.PerTargetAspectWithOrderAnnotation5();
        advisors.addAll(getFixture().getAdvisors(new SingletonMetadataAwareAspectInstanceFactory(aspect2, "someBean2")));
        Collections.sort(advisors, new OrderComparator());
        TestBean itb = ((TestBean) (createProxy(target, advisors, TestBean.class)));
        Assert.assertEquals("Around advice must NOT apply", realAge, itb.getAge());
        // Hit the method in the per clause to instantiate the aspect
        itb.getSpouse();
        Assert.assertEquals("Around advice must apply", 0, itb.getAge());
        Assert.assertEquals("Around advice must apply", 1, itb.getAge());
    }

    @Test
    public void testPerThisAspect() throws NoSuchMethodException, SecurityException {
        TestBean target = new TestBean();
        int realAge = 65;
        target.setAge(realAge);
        TestBean itb = ((TestBean) (createProxy(target, getFixture().getAdvisors(new SingletonMetadataAwareAspectInstanceFactory(new PerThisAspect(), "someBean")), TestBean.class)));
        Assert.assertEquals("Around advice must NOT apply", realAge, itb.getAge());
        Advised advised = ((Advised) (itb));
        // Will be ExposeInvocationInterceptor, synthetic instantiation advisor, 2 method advisors
        Assert.assertEquals(4, advised.getAdvisors().length);
        ReflectiveAspectJAdvisorFactory.SyntheticInstantiationAdvisor sia = ((ReflectiveAspectJAdvisorFactory.SyntheticInstantiationAdvisor) (advised.getAdvisors()[1]));
        Assert.assertTrue(sia.getPointcut().getMethodMatcher().matches(TestBean.class.getMethod("getSpouse"), null));
        InstantiationModelAwarePointcutAdvisorImpl imapa = ((InstantiationModelAwarePointcutAdvisorImpl) (advised.getAdvisors()[2]));
        LazySingletonAspectInstanceFactoryDecorator maaif = ((LazySingletonAspectInstanceFactoryDecorator) (imapa.getAspectInstanceFactory()));
        Assert.assertFalse(maaif.isMaterialized());
        // Check that the perclause pointcut is valid
        Assert.assertTrue(maaif.getAspectMetadata().getPerClausePointcut().getMethodMatcher().matches(TestBean.class.getMethod("getSpouse"), null));
        Assert.assertNotSame(imapa.getDeclaredPointcut(), imapa.getPointcut());
        // Hit the method in the per clause to instantiate the aspect
        itb.getSpouse();
        Assert.assertTrue(maaif.isMaterialized());
        Assert.assertTrue(imapa.getDeclaredPointcut().getMethodMatcher().matches(TestBean.class.getMethod("getAge"), null));
        Assert.assertEquals("Around advice must apply", 0, itb.getAge());
        Assert.assertEquals("Around advice must apply", 1, itb.getAge());
    }

    @Test
    public void testPerTypeWithinAspect() throws NoSuchMethodException, SecurityException {
        TestBean target = new TestBean();
        int realAge = 65;
        target.setAge(realAge);
        AbstractAspectJAdvisorFactoryTests.PerTypeWithinAspectInstanceFactory aif = new AbstractAspectJAdvisorFactoryTests.PerTypeWithinAspectInstanceFactory();
        TestBean itb = ((TestBean) (createProxy(target, getFixture().getAdvisors(aif), TestBean.class)));
        Assert.assertEquals("No method calls", 0, aif.getInstantiationCount());
        Assert.assertEquals("Around advice must now apply", 0, itb.getAge());
        Advised advised = ((Advised) (itb));
        // Will be ExposeInvocationInterceptor, synthetic instantiation advisor, 2 method advisors
        Assert.assertEquals(4, advised.getAdvisors().length);
        ReflectiveAspectJAdvisorFactory.SyntheticInstantiationAdvisor sia = ((ReflectiveAspectJAdvisorFactory.SyntheticInstantiationAdvisor) (advised.getAdvisors()[1]));
        Assert.assertTrue(sia.getPointcut().getMethodMatcher().matches(TestBean.class.getMethod("getSpouse"), null));
        InstantiationModelAwarePointcutAdvisorImpl imapa = ((InstantiationModelAwarePointcutAdvisorImpl) (advised.getAdvisors()[2]));
        LazySingletonAspectInstanceFactoryDecorator maaif = ((LazySingletonAspectInstanceFactoryDecorator) (imapa.getAspectInstanceFactory()));
        Assert.assertTrue(maaif.isMaterialized());
        // Check that the perclause pointcut is valid
        Assert.assertTrue(maaif.getAspectMetadata().getPerClausePointcut().getMethodMatcher().matches(TestBean.class.getMethod("getSpouse"), null));
        Assert.assertNotSame(imapa.getDeclaredPointcut(), imapa.getPointcut());
        // Hit the method in the per clause to instantiate the aspect
        itb.getSpouse();
        Assert.assertTrue(maaif.isMaterialized());
        Assert.assertTrue(imapa.getDeclaredPointcut().getMethodMatcher().matches(TestBean.class.getMethod("getAge"), null));
        Assert.assertEquals("Around advice must still apply", 1, itb.getAge());
        Assert.assertEquals("Around advice must still apply", 2, itb.getAge());
        TestBean itb2 = ((TestBean) (createProxy(target, getFixture().getAdvisors(aif), TestBean.class)));
        Assert.assertEquals(1, aif.getInstantiationCount());
        Assert.assertEquals("Around advice be independent for second instance", 0, itb2.getAge());
        Assert.assertEquals(2, aif.getInstantiationCount());
    }

    @Test
    public void testNamedPointcutAspectWithFQN() {
        testNamedPointcuts(new AbstractAspectJAdvisorFactoryTests.NamedPointcutAspectWithFQN());
    }

    @Test
    public void testNamedPointcutAspectWithoutFQN() {
        testNamedPointcuts(new AbstractAspectJAdvisorFactoryTests.NamedPointcutAspectWithoutFQN());
    }

    @Test
    public void testNamedPointcutFromAspectLibrary() {
        testNamedPointcuts(new AbstractAspectJAdvisorFactoryTests.NamedPointcutAspectFromLibrary());
    }

    @Test
    public void testNamedPointcutFromAspectLibraryWithBinding() {
        TestBean target = new TestBean();
        ITestBean itb = ((ITestBean) (createProxy(target, getFixture().getAdvisors(new SingletonMetadataAwareAspectInstanceFactory(new AbstractAspectJAdvisorFactoryTests.NamedPointcutAspectFromLibraryWithBinding(), "someBean")), ITestBean.class)));
        itb.setAge(10);
        Assert.assertEquals("Around advice must apply", 20, itb.getAge());
        Assert.assertEquals(20, target.getAge());
    }

    @Test
    public void testBindingWithSingleArg() {
        TestBean target = new TestBean();
        ITestBean itb = ((ITestBean) (createProxy(target, getFixture().getAdvisors(new SingletonMetadataAwareAspectInstanceFactory(new AbstractAspectJAdvisorFactoryTests.BindingAspectWithSingleArg(), "someBean")), ITestBean.class)));
        itb.setAge(10);
        Assert.assertEquals("Around advice must apply", 20, itb.getAge());
        Assert.assertEquals(20, target.getAge());
    }

    @Test
    public void testBindingWithMultipleArgsDifferentlyOrdered() {
        AbstractAspectJAdvisorFactoryTests.ManyValuedArgs target = new AbstractAspectJAdvisorFactoryTests.ManyValuedArgs();
        AbstractAspectJAdvisorFactoryTests.ManyValuedArgs mva = ((AbstractAspectJAdvisorFactoryTests.ManyValuedArgs) (createProxy(target, getFixture().getAdvisors(new SingletonMetadataAwareAspectInstanceFactory(new AbstractAspectJAdvisorFactoryTests.ManyValuedArgs(), "someBean")), AbstractAspectJAdvisorFactoryTests.ManyValuedArgs.class)));
        String a = "a";
        int b = 12;
        int c = 25;
        String d = "d";
        StringBuffer e = new StringBuffer("stringbuf");
        String expectedResult = (((a + b) + c) + d) + e;
        Assert.assertEquals(expectedResult, mva.mungeArgs(a, b, c, d, e));
    }

    /**
     * In this case the introduction will be made.
     */
    @Test
    public void testIntroductionOnTargetNotImplementingInterface() {
        NotLockable notLockableTarget = new NotLockable();
        Assert.assertFalse((notLockableTarget instanceof Lockable));
        NotLockable notLockable1 = ((NotLockable) (createProxy(notLockableTarget, getFixture().getAdvisors(new SingletonMetadataAwareAspectInstanceFactory(new MakeLockable(), "someBean")), NotLockable.class)));
        Assert.assertTrue((notLockable1 instanceof Lockable));
        Lockable lockable = ((Lockable) (notLockable1));
        Assert.assertFalse(lockable.locked());
        lockable.lock();
        Assert.assertTrue(lockable.locked());
        NotLockable notLockable2Target = new NotLockable();
        NotLockable notLockable2 = ((NotLockable) (createProxy(notLockable2Target, getFixture().getAdvisors(new SingletonMetadataAwareAspectInstanceFactory(new MakeLockable(), "someBean")), NotLockable.class)));
        Assert.assertTrue((notLockable2 instanceof Lockable));
        Lockable lockable2 = ((Lockable) (notLockable2));
        Assert.assertFalse(lockable2.locked());
        notLockable2.setIntValue(1);
        lockable2.lock();
        try {
            notLockable2.setIntValue(32);
            Assert.fail();
        } catch (IllegalStateException ex) {
        }
        Assert.assertTrue(lockable2.locked());
    }

    @Test
    public void testIntroductionAdvisorExcludedFromTargetImplementingInterface() {
        Assert.assertTrue(AopUtils.findAdvisorsThatCanApply(getFixture().getAdvisors(new SingletonMetadataAwareAspectInstanceFactory(new MakeLockable(), "someBean")), CannotBeUnlocked.class).isEmpty());
        Assert.assertEquals(2, AopUtils.findAdvisorsThatCanApply(getFixture().getAdvisors(new SingletonMetadataAwareAspectInstanceFactory(new MakeLockable(), "someBean")), NotLockable.class).size());
    }

    @Test
    public void testIntroductionOnTargetImplementingInterface() {
        CannotBeUnlocked target = new CannotBeUnlocked();
        Lockable proxy = // Ensure that we exclude
        ((Lockable) (createProxy(target, AopUtils.findAdvisorsThatCanApply(getFixture().getAdvisors(new SingletonMetadataAwareAspectInstanceFactory(new MakeLockable(), "someBean")), CannotBeUnlocked.class), CannotBeUnlocked.class)));
        Assert.assertThat(proxy, instanceOf(Lockable.class));
        Lockable lockable = proxy;
        Assert.assertTrue("Already locked", lockable.locked());
        lockable.lock();
        Assert.assertTrue("Real target ignores locking", lockable.locked());
        try {
            lockable.unlock();
            Assert.fail();
        } catch (UnsupportedOperationException ex) {
            // Ok
        }
    }

    @Test
    public void testIntroductionOnTargetExcludedByTypePattern() {
        LinkedList<Object> target = new LinkedList<>();
        List<?> proxy = ((List<?>) (createProxy(target, AopUtils.findAdvisorsThatCanApply(getFixture().getAdvisors(new SingletonMetadataAwareAspectInstanceFactory(new MakeLockable(), "someBean")), List.class), List.class)));
        Assert.assertFalse("Type pattern must have excluded mixin", (proxy instanceof Lockable));
    }

    @Test
    public void testIntroductionBasedOnAnnotationMatch_SPR5307() {
        AnnotatedTarget target = new AnnotatedTargetImpl();
        List<Advisor> advisors = getFixture().getAdvisors(new SingletonMetadataAwareAspectInstanceFactory(new MakeAnnotatedTypeModifiable(), "someBean"));
        Object proxy = createProxy(target, advisors, AnnotatedTarget.class);
        System.out.println(advisors.get(1));
        Assert.assertTrue((proxy instanceof Lockable));
        Lockable lockable = ((Lockable) (proxy));
        lockable.locked();
    }

    @Test
    public void testAspectMethodThrowsExceptionLegalOnSignature() {
        TestBean target = new TestBean();
        UnsupportedOperationException expectedException = new UnsupportedOperationException();
        List<Advisor> advisors = getFixture().getAdvisors(new SingletonMetadataAwareAspectInstanceFactory(new AbstractAspectJAdvisorFactoryTests.ExceptionAspect(expectedException), "someBean"));
        Assert.assertEquals("One advice method was found", 1, advisors.size());
        ITestBean itb = ((ITestBean) (createProxy(target, advisors, ITestBean.class)));
        try {
            itb.getAge();
            Assert.fail();
        } catch (UnsupportedOperationException ex) {
            Assert.assertSame(expectedException, ex);
        }
    }

    // TODO document this behaviour.
    // Is it different AspectJ behaviour, at least for checked exceptions?
    @Test
    public void testAspectMethodThrowsExceptionIllegalOnSignature() {
        TestBean target = new TestBean();
        RemoteException expectedException = new RemoteException();
        List<Advisor> advisors = getFixture().getAdvisors(new SingletonMetadataAwareAspectInstanceFactory(new AbstractAspectJAdvisorFactoryTests.ExceptionAspect(expectedException), "someBean"));
        Assert.assertEquals("One advice method was found", 1, advisors.size());
        ITestBean itb = ((ITestBean) (createProxy(target, advisors, ITestBean.class)));
        try {
            itb.getAge();
            Assert.fail();
        } catch (UndeclaredThrowableException ex) {
            Assert.assertSame(expectedException, ex.getCause());
        }
    }

    @Test
    public void testTwoAdvicesOnOneAspect() {
        TestBean target = new TestBean();
        TwoAdviceAspect twoAdviceAspect = new TwoAdviceAspect();
        List<Advisor> advisors = getFixture().getAdvisors(new SingletonMetadataAwareAspectInstanceFactory(twoAdviceAspect, "someBean"));
        Assert.assertEquals("Two advice methods found", 2, advisors.size());
        ITestBean itb = ((ITestBean) (createProxy(target, advisors, ITestBean.class)));
        itb.setName("");
        Assert.assertEquals(0, itb.getAge());
        int newAge = 32;
        itb.setAge(newAge);
        Assert.assertEquals(1, itb.getAge());
    }

    @Test
    public void testAfterAdviceTypes() throws Exception {
        AbstractAspectJAdvisorFactoryTests.Echo target = new AbstractAspectJAdvisorFactoryTests.Echo();
        AbstractAspectJAdvisorFactoryTests.ExceptionHandling afterReturningAspect = new AbstractAspectJAdvisorFactoryTests.ExceptionHandling();
        List<Advisor> advisors = getFixture().getAdvisors(new SingletonMetadataAwareAspectInstanceFactory(afterReturningAspect, "someBean"));
        AbstractAspectJAdvisorFactoryTests.Echo echo = ((AbstractAspectJAdvisorFactoryTests.Echo) (createProxy(target, advisors, AbstractAspectJAdvisorFactoryTests.Echo.class)));
        Assert.assertEquals(0, afterReturningAspect.successCount);
        Assert.assertEquals("", echo.echo(""));
        Assert.assertEquals(1, afterReturningAspect.successCount);
        Assert.assertEquals(0, afterReturningAspect.failureCount);
        try {
            echo.echo(new FileNotFoundException());
            Assert.fail();
        } catch (FileNotFoundException ex) {
            // Ok
        } catch (Exception ex) {
            Assert.fail();
        }
        Assert.assertEquals(1, afterReturningAspect.successCount);
        Assert.assertEquals(1, afterReturningAspect.failureCount);
        Assert.assertEquals(((afterReturningAspect.failureCount) + (afterReturningAspect.successCount)), afterReturningAspect.afterCount);
    }

    @Test
    public void testFailureWithoutExplicitDeclarePrecedence() {
        TestBean target = new TestBean();
        MetadataAwareAspectInstanceFactory aspectInstanceFactory = new SingletonMetadataAwareAspectInstanceFactory(new AbstractAspectJAdvisorFactoryTests.NoDeclarePrecedenceShouldFail(), "someBean");
        ITestBean itb = ((ITestBean) (createProxy(target, getFixture().getAdvisors(aspectInstanceFactory), ITestBean.class)));
        itb.getAge();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeclarePrecedenceNotSupported() {
        TestBean target = new TestBean();
        MetadataAwareAspectInstanceFactory aspectInstanceFactory = new SingletonMetadataAwareAspectInstanceFactory(new AbstractAspectJAdvisorFactoryTests.DeclarePrecedenceShouldSucceed(), "someBean");
        createProxy(target, getFixture().getAdvisors(aspectInstanceFactory), ITestBean.class);
    }

    @Aspect("percflow(execution(* *(..)))")
    public static class PerCflowAspect {}

    @Aspect("percflowbelow(execution(* *(..)))")
    public static class PerCflowBelowAspect {}

    @Aspect("pertarget(execution(* *.getSpouse()))")
    @Order(10)
    public static class PerTargetAspectWithOrderAnnotation10 {
        public int count;

        @Around("execution(int *.getAge())")
        public int returnCountAsAge() {
            return (count)++;
        }

        @Before("execution(void *.set*(int))")
        public void countSetter() {
            ++(count);
        }
    }

    @Aspect("pertarget(execution(* *.getSpouse()))")
    @Order(5)
    public static class PerTargetAspectWithOrderAnnotation5 {
        public int count;

        @Around("execution(int *.getAge())")
        public int returnCountAsAge() {
            return (count)++;
        }

        @Before("execution(void *.set*(int))")
        public void countSetter() {
            ++(count);
        }
    }

    @Aspect("pertypewithin(org.springframework.tests.sample.beans.IOther+)")
    public static class PerTypeWithinAspect {
        public int count;

        @Around("execution(int *.getAge())")
        public int returnCountAsAge() {
            return (count)++;
        }

        @Before("execution(void *.*(..))")
        public void countAnythingVoid() {
            ++(count);
        }
    }

    private class PerTypeWithinAspectInstanceFactory implements MetadataAwareAspectInstanceFactory {
        private int count;

        public int getInstantiationCount() {
            return this.count;
        }

        @Override
        public Object getAspectInstance() {
            ++(this.count);
            return new AbstractAspectJAdvisorFactoryTests.PerTypeWithinAspect();
        }

        @Override
        public ClassLoader getAspectClassLoader() {
            return AbstractAspectJAdvisorFactoryTests.PerTypeWithinAspect.class.getClassLoader();
        }

        @Override
        public AspectMetadata getAspectMetadata() {
            return new AspectMetadata(AbstractAspectJAdvisorFactoryTests.PerTypeWithinAspect.class, "perTypeWithin");
        }

        @Override
        public Object getAspectCreationMutex() {
            return this;
        }

        @Override
        public int getOrder() {
            return Ordered.LOWEST_PRECEDENCE;
        }
    }

    @Aspect
    public static class NamedPointcutAspectWithFQN {
        private ITestBean fieldThatShouldBeIgnoredBySpringAtAspectJProcessing = new TestBean();

        @Pointcut("execution(* getAge())")
        public void getAge() {
        }

        @Around("org.springframework.aop.aspectj.annotation.AbstractAspectJAdvisorFactoryTests.NamedPointcutAspectWithFQN.getAge()")
        public int changeReturnValue(ProceedingJoinPoint pjp) {
            return -1;
        }
    }

    @Aspect
    public static class NamedPointcutAspectWithoutFQN {
        @Pointcut("execution(* getAge())")
        public void getAge() {
        }

        @Around("getAge()")
        public int changeReturnValue(ProceedingJoinPoint pjp) {
            return -1;
        }
    }

    @Aspect
    public static class NamedPointcutAspectFromLibrary {
        @Around("org.springframework.aop.aspectj.annotation.AbstractAspectJAdvisorFactoryTests.Library.propertyAccess()")
        public int changeReturnType(ProceedingJoinPoint pjp) {
            return -1;
        }

        @Around(value = "org.springframework.aop.aspectj.annotation.AbstractAspectJAdvisorFactoryTests.Library.integerArgOperation(x)", argNames = "x")
        public void doubleArg(ProceedingJoinPoint pjp, int x) throws Throwable {
            pjp.proceed(new Object[]{ x * 2 });
        }
    }

    @Aspect
    public static class Library {
        @Pointcut("execution(!void get*())")
        public void propertyAccess() {
        }

        @Pointcut("execution(* *(..)) && args(i)")
        public void integerArgOperation(int i) {
        }
    }

    @Aspect
    public static class NamedPointcutAspectFromLibraryWithBinding {
        @Around(value = "org.springframework.aop.aspectj.annotation.AbstractAspectJAdvisorFactoryTests.Library.integerArgOperation(x)", argNames = "x")
        public void doubleArg(ProceedingJoinPoint pjp, int x) throws Throwable {
            pjp.proceed(new Object[]{ x * 2 });
        }
    }

    @Aspect
    public static class BindingAspectWithSingleArg {
        @Pointcut(value = "args(a)", argNames = "a")
        public void setAge(int a) {
        }

        // @ArgNames({"age"})	// AMC needs more work here? ignoring pjp arg... ok??
        // argNames should be supported in Around as it is in Pointcut
        @Around(value = "setAge(age)", argNames = "age")
        public void changeReturnType(ProceedingJoinPoint pjp, int age) throws Throwable {
            pjp.proceed(new Object[]{ age * 2 });
        }
    }

    @Aspect
    public static class ManyValuedArgs {
        public String mungeArgs(String a, int b, int c, String d, StringBuffer e) {
            return (((a + b) + c) + d) + e;
        }

        @Around(value = "execution(String mungeArgs(..)) && args(a, b, c, d, e)", argNames = "b,c,d,e,a")
        public String reverseAdvice(ProceedingJoinPoint pjp, int b, int c, String d, StringBuffer e, String a) throws Throwable {
            Assert.assertEquals(((((a + b) + c) + d) + e), pjp.proceed());
            return (((a + b) + c) + d) + e;
        }
    }

    @Aspect
    public static class ExceptionAspect {
        private final Exception ex;

        public ExceptionAspect(Exception ex) {
            this.ex = ex;
        }

        @Before("execution(* getAge())")
        public void throwException() throws Exception {
            throw ex;
        }
    }

    public static class Echo {
        public Object echo(Object o) throws Exception {
            if (o instanceof Exception) {
                throw ((Exception) (o));
            }
            return o;
        }
    }

    @Aspect
    public static class ExceptionHandling {
        public int successCount;

        public int failureCount;

        public int afterCount;

        @AfterReturning("execution(* echo(*))")
        public void succeeded() {
            ++(successCount);
        }

        @AfterThrowing("execution(* echo(*))")
        public void failed() {
            ++(failureCount);
        }

        @After("execution(* echo(*))")
        public void invoked() {
            ++(afterCount);
        }
    }

    @Aspect
    public static class NoDeclarePrecedenceShouldFail {
        @Pointcut("execution(int *.getAge())")
        public void getAge() {
        }

        @Before("getAge()")
        public void blowUpButDoesntMatterBecauseAroundAdviceWontLetThisBeInvoked() {
            throw new IllegalStateException();
        }

        @Around("getAge()")
        public int preventExecution(ProceedingJoinPoint pjp) {
            return 666;
        }
    }

    @Aspect
    @DeclarePrecedence("test..*")
    public static class DeclarePrecedenceShouldSucceed {
        @Pointcut("execution(int *.getAge())")
        public void getAge() {
        }

        @Before("getAge()")
        public void blowUpButDoesntMatterBecauseAroundAdviceWontLetThisBeInvoked() {
            throw new IllegalStateException();
        }

        @Around("getAge()")
        public int preventExecution(ProceedingJoinPoint pjp) {
            return 666;
        }
    }
}

