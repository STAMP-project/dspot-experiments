package org.baeldung.scopes;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class ScopesIntegrationTest {
    private static final String NAME = "John Smith";

    private static final String NAME_OTHER = "Anna Jones";

    @Test
    public void testScopeSingleton() {
        final ApplicationContext applicationContext = new ClassPathXmlApplicationContext("scopes.xml");
        final Person personSingletonA = ((Person) (applicationContext.getBean("personSingleton")));
        final Person personSingletonB = ((Person) (applicationContext.getBean("personSingleton")));
        personSingletonA.setName(ScopesIntegrationTest.NAME);
        Assert.assertEquals(ScopesIntegrationTest.NAME, personSingletonB.getName());
        close();
    }

    @Test
    public void testScopePrototype() {
        final ApplicationContext applicationContext = new ClassPathXmlApplicationContext("scopes.xml");
        final Person personPrototypeA = ((Person) (applicationContext.getBean("personPrototype")));
        final Person personPrototypeB = ((Person) (applicationContext.getBean("personPrototype")));
        personPrototypeA.setName(ScopesIntegrationTest.NAME);
        personPrototypeB.setName(ScopesIntegrationTest.NAME_OTHER);
        Assert.assertEquals(ScopesIntegrationTest.NAME, personPrototypeA.getName());
        Assert.assertEquals(ScopesIntegrationTest.NAME_OTHER, personPrototypeB.getName());
        close();
    }
}

