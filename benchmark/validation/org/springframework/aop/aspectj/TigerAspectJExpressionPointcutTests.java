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


import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.tests.sample.beans.TestBean;
import test.annotation.EmptySpringAnnotation;
import test.annotation.transaction.Tx;


/**
 * Java 5 specific {@link AspectJExpressionPointcutTests}.
 *
 * @author Rod Johnson
 * @author Chris Beams
 */
public class TigerAspectJExpressionPointcutTests {
    private Method getAge;

    private final Map<String, Method> methodsOnHasGeneric = new HashMap<>();

    @Test
    public void testMatchGenericArgument() {
        String expression = "execution(* set*(java.util.List<org.springframework.tests.sample.beans.TestBean>) )";
        AspectJExpressionPointcut ajexp = new AspectJExpressionPointcut();
        ajexp.setExpression(expression);
        // TODO this will currently map, would be nice for optimization
        // assertTrue(ajexp.matches(HasGeneric.class));
        // assertFalse(ajexp.matches(TestBean.class));
        Method takesGenericList = methodsOnHasGeneric.get("setFriends");
        Assert.assertTrue(ajexp.matches(takesGenericList, TigerAspectJExpressionPointcutTests.HasGeneric.class));
        Assert.assertTrue(ajexp.matches(methodsOnHasGeneric.get("setEnemies"), TigerAspectJExpressionPointcutTests.HasGeneric.class));
        Assert.assertFalse(ajexp.matches(methodsOnHasGeneric.get("setPartners"), TigerAspectJExpressionPointcutTests.HasGeneric.class));
        Assert.assertFalse(ajexp.matches(methodsOnHasGeneric.get("setPhoneNumbers"), TigerAspectJExpressionPointcutTests.HasGeneric.class));
        Assert.assertFalse(ajexp.matches(getAge, TestBean.class));
    }

    @Test
    public void testMatchVarargs() throws Exception {
        @SuppressWarnings("unused")
        class MyTemplate {
            public int queryForInt(String sql, Object... params) {
                return 0;
            }
        }
        String expression = "execution(int *.*(String, Object...))";
        AspectJExpressionPointcut jdbcVarArgs = new AspectJExpressionPointcut();
        jdbcVarArgs.setExpression(expression);
        Assert.assertTrue(jdbcVarArgs.matches(MyTemplate.class.getMethod("queryForInt", String.class, Object[].class), MyTemplate.class));
        Method takesGenericList = methodsOnHasGeneric.get("setFriends");
        Assert.assertFalse(jdbcVarArgs.matches(takesGenericList, TigerAspectJExpressionPointcutTests.HasGeneric.class));
        Assert.assertFalse(jdbcVarArgs.matches(methodsOnHasGeneric.get("setEnemies"), TigerAspectJExpressionPointcutTests.HasGeneric.class));
        Assert.assertFalse(jdbcVarArgs.matches(methodsOnHasGeneric.get("setPartners"), TigerAspectJExpressionPointcutTests.HasGeneric.class));
        Assert.assertFalse(jdbcVarArgs.matches(methodsOnHasGeneric.get("setPhoneNumbers"), TigerAspectJExpressionPointcutTests.HasGeneric.class));
        Assert.assertFalse(jdbcVarArgs.matches(getAge, TestBean.class));
    }

    @Test
    public void testMatchAnnotationOnClassWithAtWithin() throws Exception {
        String expression = "@within(test.annotation.transaction.Tx)";
        testMatchAnnotationOnClass(expression);
    }

    @Test
    public void testMatchAnnotationOnClassWithoutBinding() throws Exception {
        String expression = "within(@test.annotation.transaction.Tx *)";
        testMatchAnnotationOnClass(expression);
    }

    @Test
    public void testMatchAnnotationOnClassWithSubpackageWildcard() throws Exception {
        String expression = "within(@(test.annotation..*) *)";
        AspectJExpressionPointcut springAnnotatedPc = testMatchAnnotationOnClass(expression);
        Assert.assertFalse(springAnnotatedPc.matches(TestBean.class.getMethod("setName", String.class), TestBean.class));
        Assert.assertTrue(springAnnotatedPc.matches(TigerAspectJExpressionPointcutTests.SpringAnnotated.class.getMethod("foo"), TigerAspectJExpressionPointcutTests.SpringAnnotated.class));
        expression = "within(@(test.annotation.transaction..*) *)";
        AspectJExpressionPointcut springTxAnnotatedPc = testMatchAnnotationOnClass(expression);
        Assert.assertFalse(springTxAnnotatedPc.matches(TigerAspectJExpressionPointcutTests.SpringAnnotated.class.getMethod("foo"), TigerAspectJExpressionPointcutTests.SpringAnnotated.class));
    }

    @Test
    public void testMatchAnnotationOnClassWithExactPackageWildcard() throws Exception {
        String expression = "within(@(test.annotation.transaction.*) *)";
        testMatchAnnotationOnClass(expression);
    }

    @Test
    public void testAnnotationOnMethodWithFQN() throws Exception {
        String expression = "@annotation(test.annotation.transaction.Tx)";
        AspectJExpressionPointcut ajexp = new AspectJExpressionPointcut();
        ajexp.setExpression(expression);
        Assert.assertFalse(ajexp.matches(getAge, TestBean.class));
        Assert.assertFalse(ajexp.matches(TigerAspectJExpressionPointcutTests.HasTransactionalAnnotation.class.getMethod("foo"), TigerAspectJExpressionPointcutTests.HasTransactionalAnnotation.class));
        Assert.assertFalse(ajexp.matches(TigerAspectJExpressionPointcutTests.HasTransactionalAnnotation.class.getMethod("bar", String.class), TigerAspectJExpressionPointcutTests.HasTransactionalAnnotation.class));
        Assert.assertFalse(ajexp.matches(TigerAspectJExpressionPointcutTests.BeanA.class.getMethod("setName", String.class), TigerAspectJExpressionPointcutTests.BeanA.class));
        Assert.assertTrue(ajexp.matches(TigerAspectJExpressionPointcutTests.BeanA.class.getMethod("getAge"), TigerAspectJExpressionPointcutTests.BeanA.class));
        Assert.assertFalse(ajexp.matches(TigerAspectJExpressionPointcutTests.BeanA.class.getMethod("setName", String.class), TigerAspectJExpressionPointcutTests.BeanA.class));
    }

    @Test
    public void testAnnotationOnCglibProxyMethod() throws Exception {
        String expression = "@annotation(test.annotation.transaction.Tx)";
        AspectJExpressionPointcut ajexp = new AspectJExpressionPointcut();
        ajexp.setExpression(expression);
        ProxyFactory factory = new ProxyFactory(new TigerAspectJExpressionPointcutTests.BeanA());
        factory.setProxyTargetClass(true);
        TigerAspectJExpressionPointcutTests.BeanA proxy = ((TigerAspectJExpressionPointcutTests.BeanA) (factory.getProxy()));
        Assert.assertTrue(ajexp.matches(TigerAspectJExpressionPointcutTests.BeanA.class.getMethod("getAge"), proxy.getClass()));
    }

    @Test
    public void testAnnotationOnDynamicProxyMethod() throws Exception {
        String expression = "@annotation(test.annotation.transaction.Tx)";
        AspectJExpressionPointcut ajexp = new AspectJExpressionPointcut();
        ajexp.setExpression(expression);
        ProxyFactory factory = new ProxyFactory(new TigerAspectJExpressionPointcutTests.BeanA());
        factory.setProxyTargetClass(false);
        TigerAspectJExpressionPointcutTests.IBeanA proxy = ((TigerAspectJExpressionPointcutTests.IBeanA) (factory.getProxy()));
        Assert.assertTrue(ajexp.matches(TigerAspectJExpressionPointcutTests.IBeanA.class.getMethod("getAge"), proxy.getClass()));
    }

    @Test
    public void testAnnotationOnMethodWithWildcard() throws Exception {
        String expression = "execution(@(test.annotation..*) * *(..))";
        AspectJExpressionPointcut anySpringMethodAnnotation = new AspectJExpressionPointcut();
        anySpringMethodAnnotation.setExpression(expression);
        Assert.assertFalse(anySpringMethodAnnotation.matches(getAge, TestBean.class));
        Assert.assertFalse(anySpringMethodAnnotation.matches(TigerAspectJExpressionPointcutTests.HasTransactionalAnnotation.class.getMethod("foo"), TigerAspectJExpressionPointcutTests.HasTransactionalAnnotation.class));
        Assert.assertFalse(anySpringMethodAnnotation.matches(TigerAspectJExpressionPointcutTests.HasTransactionalAnnotation.class.getMethod("bar", String.class), TigerAspectJExpressionPointcutTests.HasTransactionalAnnotation.class));
        Assert.assertFalse(anySpringMethodAnnotation.matches(TigerAspectJExpressionPointcutTests.BeanA.class.getMethod("setName", String.class), TigerAspectJExpressionPointcutTests.BeanA.class));
        Assert.assertTrue(anySpringMethodAnnotation.matches(TigerAspectJExpressionPointcutTests.BeanA.class.getMethod("getAge"), TigerAspectJExpressionPointcutTests.BeanA.class));
        Assert.assertFalse(anySpringMethodAnnotation.matches(TigerAspectJExpressionPointcutTests.BeanA.class.getMethod("setName", String.class), TigerAspectJExpressionPointcutTests.BeanA.class));
    }

    @Test
    public void testAnnotationOnMethodArgumentsWithFQN() throws Exception {
        String expression = "@args(*, test.annotation.EmptySpringAnnotation))";
        AspectJExpressionPointcut takesSpringAnnotatedArgument2 = new AspectJExpressionPointcut();
        takesSpringAnnotatedArgument2.setExpression(expression);
        Assert.assertFalse(takesSpringAnnotatedArgument2.matches(getAge, TestBean.class));
        Assert.assertFalse(takesSpringAnnotatedArgument2.matches(TigerAspectJExpressionPointcutTests.HasTransactionalAnnotation.class.getMethod("foo"), TigerAspectJExpressionPointcutTests.HasTransactionalAnnotation.class));
        Assert.assertFalse(takesSpringAnnotatedArgument2.matches(TigerAspectJExpressionPointcutTests.HasTransactionalAnnotation.class.getMethod("bar", String.class), TigerAspectJExpressionPointcutTests.HasTransactionalAnnotation.class));
        Assert.assertFalse(takesSpringAnnotatedArgument2.matches(TigerAspectJExpressionPointcutTests.BeanA.class.getMethod("setName", String.class), TigerAspectJExpressionPointcutTests.BeanA.class));
        Assert.assertFalse(takesSpringAnnotatedArgument2.matches(TigerAspectJExpressionPointcutTests.BeanA.class.getMethod("getAge"), TigerAspectJExpressionPointcutTests.BeanA.class));
        Assert.assertFalse(takesSpringAnnotatedArgument2.matches(TigerAspectJExpressionPointcutTests.BeanA.class.getMethod("setName", String.class), TigerAspectJExpressionPointcutTests.BeanA.class));
        Assert.assertTrue(takesSpringAnnotatedArgument2.matches(TigerAspectJExpressionPointcutTests.ProcessesSpringAnnotatedParameters.class.getMethod("takesAnnotatedParameters", TestBean.class, TigerAspectJExpressionPointcutTests.SpringAnnotated.class), TigerAspectJExpressionPointcutTests.ProcessesSpringAnnotatedParameters.class));
        // True because it maybeMatches with potential argument subtypes
        Assert.assertTrue(takesSpringAnnotatedArgument2.matches(TigerAspectJExpressionPointcutTests.ProcessesSpringAnnotatedParameters.class.getMethod("takesNoAnnotatedParameters", TestBean.class, TigerAspectJExpressionPointcutTests.BeanA.class), TigerAspectJExpressionPointcutTests.ProcessesSpringAnnotatedParameters.class));
        Assert.assertFalse(takesSpringAnnotatedArgument2.matches(TigerAspectJExpressionPointcutTests.ProcessesSpringAnnotatedParameters.class.getMethod("takesNoAnnotatedParameters", TestBean.class, TigerAspectJExpressionPointcutTests.BeanA.class), TigerAspectJExpressionPointcutTests.ProcessesSpringAnnotatedParameters.class, new TestBean(), new TigerAspectJExpressionPointcutTests.BeanA()));
    }

    @Test
    public void testAnnotationOnMethodArgumentsWithWildcards() throws Exception {
        String expression = "execution(* *(*, @(test..*) *))";
        AspectJExpressionPointcut takesSpringAnnotatedArgument2 = new AspectJExpressionPointcut();
        takesSpringAnnotatedArgument2.setExpression(expression);
        Assert.assertFalse(takesSpringAnnotatedArgument2.matches(getAge, TestBean.class));
        Assert.assertFalse(takesSpringAnnotatedArgument2.matches(TigerAspectJExpressionPointcutTests.HasTransactionalAnnotation.class.getMethod("foo"), TigerAspectJExpressionPointcutTests.HasTransactionalAnnotation.class));
        Assert.assertFalse(takesSpringAnnotatedArgument2.matches(TigerAspectJExpressionPointcutTests.HasTransactionalAnnotation.class.getMethod("bar", String.class), TigerAspectJExpressionPointcutTests.HasTransactionalAnnotation.class));
        Assert.assertFalse(takesSpringAnnotatedArgument2.matches(TigerAspectJExpressionPointcutTests.BeanA.class.getMethod("setName", String.class), TigerAspectJExpressionPointcutTests.BeanA.class));
        Assert.assertFalse(takesSpringAnnotatedArgument2.matches(TigerAspectJExpressionPointcutTests.BeanA.class.getMethod("getAge"), TigerAspectJExpressionPointcutTests.BeanA.class));
        Assert.assertFalse(takesSpringAnnotatedArgument2.matches(TigerAspectJExpressionPointcutTests.BeanA.class.getMethod("setName", String.class), TigerAspectJExpressionPointcutTests.BeanA.class));
        Assert.assertTrue(takesSpringAnnotatedArgument2.matches(TigerAspectJExpressionPointcutTests.ProcessesSpringAnnotatedParameters.class.getMethod("takesAnnotatedParameters", TestBean.class, TigerAspectJExpressionPointcutTests.SpringAnnotated.class), TigerAspectJExpressionPointcutTests.ProcessesSpringAnnotatedParameters.class));
        Assert.assertFalse(takesSpringAnnotatedArgument2.matches(TigerAspectJExpressionPointcutTests.ProcessesSpringAnnotatedParameters.class.getMethod("takesNoAnnotatedParameters", TestBean.class, TigerAspectJExpressionPointcutTests.BeanA.class), TigerAspectJExpressionPointcutTests.ProcessesSpringAnnotatedParameters.class));
    }

    public static class HasGeneric {
        public void setFriends(List<TestBean> friends) {
        }

        public void setEnemies(List<TestBean> enemies) {
        }

        public void setPartners(List<?> partners) {
        }

        public void setPhoneNumbers(List<String> numbers) {
        }
    }

    public static class ProcessesSpringAnnotatedParameters {
        public void takesAnnotatedParameters(TestBean tb, TigerAspectJExpressionPointcutTests.SpringAnnotated sa) {
        }

        public void takesNoAnnotatedParameters(TestBean tb, TigerAspectJExpressionPointcutTests.BeanA tb3) {
        }
    }

    @Tx
    public static class HasTransactionalAnnotation {
        public void foo() {
        }

        public Object bar(String foo) {
            throw new UnsupportedOperationException();
        }
    }

    @EmptySpringAnnotation
    public static class SpringAnnotated {
        public void foo() {
        }
    }

    interface IBeanA {
        @Tx
        int getAge();
    }

    static class BeanA implements TigerAspectJExpressionPointcutTests.IBeanA {
        private String name;

        private int age;

        public void setName(String name) {
            this.name = name;
        }

        @Tx
        @Override
        public int getAge() {
            return age;
        }
    }

    @Tx
    static class BeanB {
        private String name;

        public void setName(String name) {
            this.name = name;
        }
    }
}

