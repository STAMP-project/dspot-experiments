package com.baeldung.cglib.proxy;


import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.FixedValue;
import net.sf.cglib.proxy.MethodInterceptor;
import org.junit.Assert;
import org.junit.Test;


public class ProxyIntegrationTest {
    @Test
    public void givenPersonService_whenSayHello_thenReturnResult() {
        // given
        PersonService personService = new PersonService();
        // when
        String res = personService.sayHello("Tom");
        // then
        Assert.assertEquals(res, "Hello Tom");
    }

    @Test
    public void givenEnhancerProxy_whenExtendPersonService_thenInterceptMethod() throws Exception {
        // given
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(PersonService.class);
        enhancer.setCallback(((FixedValue) (() -> "Hello Tom!")));
        PersonService proxy = ((PersonService) (enhancer.create()));
        // when
        String res = proxy.sayHello(null);
        // then
        Assert.assertEquals("Hello Tom!", res);
    }

    @Test
    public void givenEnhancer_whenExecuteMethodOnProxy_thenInterceptOnlyStringReturnTypeMethod() throws Exception {
        // given
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(PersonService.class);
        enhancer.setCallback(((MethodInterceptor) (( obj, method, args, proxy) -> {
            if (((method.getDeclaringClass()) != (.class)) && ((method.getReturnType()) == (.class))) {
                return "Hello Tom!";
            } else {
                return proxy.invokeSuper(obj, args);
            }
        })));
        // when
        PersonService proxy = ((PersonService) (enhancer.create()));
        // then
        Assert.assertEquals("Hello Tom!", proxy.sayHello(null));
        int lengthOfName = proxy.lengthOfName("Mary");
        Assert.assertEquals(4, lengthOfName);
    }
}

