/**
 * Copyright 2002-2015 the original author or authors.
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
package org.springframework.context;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.xml.AbstractListableBeanFactoryTests;
import org.springframework.tests.sample.beans.LifecycleBean;
import org.springframework.tests.sample.beans.TestBean;


/**
 *
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Sam Brannen
 */
public abstract class AbstractApplicationContextTests extends AbstractListableBeanFactoryTests {
    /**
     * Must be supplied as XML
     */
    public static final String TEST_NAMESPACE = "testNamespace";

    protected ConfigurableApplicationContext applicationContext;

    /**
     * Subclass must register this
     */
    protected TestListener listener = new TestListener();

    protected TestListener parentListener = new TestListener();

    @Test
    public void contextAwareSingletonWasCalledBack() throws Exception {
        ACATester aca = ((ACATester) (applicationContext.getBean("aca")));
        Assert.assertTrue("has had context set", ((aca.getApplicationContext()) == (applicationContext)));
        Object aca2 = applicationContext.getBean("aca");
        Assert.assertTrue("Same instance", (aca == aca2));
        Assert.assertTrue("Says is singleton", applicationContext.isSingleton("aca"));
    }

    @Test
    public void contextAwarePrototypeWasCalledBack() throws Exception {
        ACATester aca = ((ACATester) (applicationContext.getBean("aca-prototype")));
        Assert.assertTrue("has had context set", ((aca.getApplicationContext()) == (applicationContext)));
        Object aca2 = applicationContext.getBean("aca-prototype");
        Assert.assertTrue("NOT Same instance", (aca != aca2));
        Assert.assertTrue("Says is prototype", (!(applicationContext.isSingleton("aca-prototype"))));
    }

    @Test
    public void parentNonNull() {
        Assert.assertTrue("parent isn't null", ((applicationContext.getParent()) != null));
    }

    @Test
    public void grandparentNull() {
        Assert.assertTrue("grandparent is null", ((applicationContext.getParent().getParent()) == null));
    }

    @Test
    public void overrideWorked() throws Exception {
        TestBean rod = ((TestBean) (applicationContext.getParent().getBean("rod")));
        Assert.assertTrue("Parent's name differs", rod.getName().equals("Roderick"));
    }

    @Test
    public void grandparentDefinitionFound() throws Exception {
        TestBean dad = ((TestBean) (applicationContext.getBean("father")));
        Assert.assertTrue("Dad has correct name", dad.getName().equals("Albert"));
    }

    @Test
    public void grandparentTypedDefinitionFound() throws Exception {
        TestBean dad = applicationContext.getBean("father", TestBean.class);
        Assert.assertTrue("Dad has correct name", dad.getName().equals("Albert"));
    }

    @Test
    public void closeTriggersDestroy() {
        LifecycleBean lb = ((LifecycleBean) (applicationContext.getBean("lifecycle")));
        Assert.assertTrue("Not destroyed", (!(lb.isDestroyed())));
        applicationContext.close();
        if ((applicationContext.getParent()) != null) {
            close();
        }
        Assert.assertTrue("Destroyed", lb.isDestroyed());
        applicationContext.close();
        if ((applicationContext.getParent()) != null) {
            close();
        }
        Assert.assertTrue("Destroyed", lb.isDestroyed());
    }

    @Test(expected = NoSuchMessageException.class)
    public void messageSource() throws NoSuchMessageException {
        Assert.assertEquals("message1", applicationContext.getMessage("code1", null, Locale.getDefault()));
        Assert.assertEquals("message2", applicationContext.getMessage("code2", null, Locale.getDefault()));
        applicationContext.getMessage("code0", null, Locale.getDefault());
    }

    @Test
    public void events() throws Exception {
        doTestEvents(this.listener, this.parentListener, new AbstractApplicationContextTests.MyEvent(this));
    }

    @Test
    public void eventsWithNoSource() throws Exception {
        // See SPR-10945 Serialized events result in a null source
        AbstractApplicationContextTests.MyEvent event = new AbstractApplicationContextTests.MyEvent(this);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(event);
        oos.close();
        event = ((AbstractApplicationContextTests.MyEvent) (new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray())).readObject()));
        doTestEvents(this.listener, this.parentListener, event);
    }

    @Test
    public void beanAutomaticallyHearsEvents() throws Exception {
        // String[] listenerNames = ((ListableBeanFactory) applicationContext).getBeanDefinitionNames(ApplicationListener.class);
        // assertTrue("listeners include beanThatListens", Arrays.asList(listenerNames).contains("beanThatListens"));
        BeanThatListens b = ((BeanThatListens) (applicationContext.getBean("beanThatListens")));
        b.zero();
        Assert.assertTrue("0 events before publication", ((b.getEventCount()) == 0));
        this.applicationContext.publishEvent(new AbstractApplicationContextTests.MyEvent(this));
        Assert.assertTrue(("1 events after publication, not " + (b.getEventCount())), ((b.getEventCount()) == 1));
    }

    @SuppressWarnings("serial")
    public static class MyEvent extends ApplicationEvent {
        public MyEvent(Object source) {
            super(source);
        }
    }
}

