/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.scripting.bsh;


import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.target.dynamic.Refreshable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.NestedRuntimeException;
import org.springframework.scripting.Calculator;
import org.springframework.scripting.ConfigurableMessenger;
import org.springframework.scripting.Messenger;
import org.springframework.scripting.ScriptCompilationException;
import org.springframework.scripting.ScriptSource;
import org.springframework.scripting.TestBeanAwareMessenger;
import org.springframework.scripting.support.ScriptFactoryPostProcessor;
import org.springframework.tests.sample.beans.TestBean;


/**
 *
 *
 * @author Rob Harrop
 * @author Rick Evans
 * @author Juergen Hoeller
 */
public class BshScriptFactoryTests {
    @Test
    public void staticScript() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("bshContext.xml", getClass());
        Assert.assertTrue(Arrays.asList(ctx.getBeanNamesForType(Calculator.class)).contains("calculator"));
        Assert.assertTrue(Arrays.asList(ctx.getBeanNamesForType(Messenger.class)).contains("messenger"));
        Calculator calc = ((Calculator) (ctx.getBean("calculator")));
        Messenger messenger = ((Messenger) (ctx.getBean("messenger")));
        Assert.assertFalse("Scripted object should not be instance of Refreshable", (calc instanceof Refreshable));
        Assert.assertFalse("Scripted object should not be instance of Refreshable", (messenger instanceof Refreshable));
        Assert.assertEquals(calc, calc);
        Assert.assertEquals(messenger, messenger);
        Assert.assertTrue((!(messenger.equals(calc))));
        Assert.assertTrue(((messenger.hashCode()) != (calc.hashCode())));
        Assert.assertTrue((!(messenger.toString().equals(calc.toString()))));
        Assert.assertEquals(5, calc.add(2, 3));
        String desiredMessage = "Hello World!";
        Assert.assertEquals("Message is incorrect", desiredMessage, messenger.getMessage());
        Assert.assertTrue(ctx.getBeansOfType(Calculator.class).values().contains(calc));
        Assert.assertTrue(ctx.getBeansOfType(Messenger.class).values().contains(messenger));
    }

    @Test
    public void staticScriptWithNullReturnValue() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("bshContext.xml", getClass());
        Assert.assertTrue(Arrays.asList(ctx.getBeanNamesForType(Messenger.class)).contains("messengerWithConfig"));
        ConfigurableMessenger messenger = ((ConfigurableMessenger) (ctx.getBean("messengerWithConfig")));
        messenger.setMessage(null);
        Assert.assertNull(messenger.getMessage());
        Assert.assertTrue(ctx.getBeansOfType(Messenger.class).values().contains(messenger));
    }

    @Test
    public void staticScriptWithTwoInterfacesSpecified() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("bshContext.xml", getClass());
        Assert.assertTrue(Arrays.asList(ctx.getBeanNamesForType(Messenger.class)).contains("messengerWithConfigExtra"));
        ConfigurableMessenger messenger = ((ConfigurableMessenger) (ctx.getBean("messengerWithConfigExtra")));
        messenger.setMessage(null);
        Assert.assertNull(messenger.getMessage());
        Assert.assertTrue(ctx.getBeansOfType(Messenger.class).values().contains(messenger));
        ctx.close();
        Assert.assertNull(messenger.getMessage());
    }

    @Test
    public void staticWithScriptReturningInstance() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("bshContext.xml", getClass());
        Assert.assertTrue(Arrays.asList(ctx.getBeanNamesForType(Messenger.class)).contains("messengerInstance"));
        Messenger messenger = ((Messenger) (ctx.getBean("messengerInstance")));
        String desiredMessage = "Hello World!";
        Assert.assertEquals("Message is incorrect", desiredMessage, messenger.getMessage());
        Assert.assertTrue(ctx.getBeansOfType(Messenger.class).values().contains(messenger));
        ctx.close();
        Assert.assertNull(messenger.getMessage());
    }

    @Test
    public void staticScriptImplementingInterface() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("bshContext.xml", getClass());
        Assert.assertTrue(Arrays.asList(ctx.getBeanNamesForType(Messenger.class)).contains("messengerImpl"));
        Messenger messenger = ((Messenger) (ctx.getBean("messengerImpl")));
        String desiredMessage = "Hello World!";
        Assert.assertEquals("Message is incorrect", desiredMessage, messenger.getMessage());
        Assert.assertTrue(ctx.getBeansOfType(Messenger.class).values().contains(messenger));
        ctx.close();
        Assert.assertNull(messenger.getMessage());
    }

    @Test
    public void staticPrototypeScript() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("bshContext.xml", getClass());
        ConfigurableMessenger messenger = ((ConfigurableMessenger) (ctx.getBean("messengerPrototype")));
        ConfigurableMessenger messenger2 = ((ConfigurableMessenger) (ctx.getBean("messengerPrototype")));
        Assert.assertFalse("Shouldn't get proxy when refresh is disabled", AopUtils.isAopProxy(messenger));
        Assert.assertFalse("Scripted object should not be instance of Refreshable", (messenger instanceof Refreshable));
        Assert.assertNotSame(messenger, messenger2);
        Assert.assertSame(messenger.getClass(), messenger2.getClass());
        Assert.assertEquals("Hello World!", messenger.getMessage());
        Assert.assertEquals("Hello World!", messenger2.getMessage());
        messenger.setMessage("Bye World!");
        messenger2.setMessage("Byebye World!");
        Assert.assertEquals("Bye World!", messenger.getMessage());
        Assert.assertEquals("Byebye World!", messenger2.getMessage());
    }

    @Test
    public void nonStaticScript() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("bshRefreshableContext.xml", getClass());
        Messenger messenger = ((Messenger) (ctx.getBean("messenger")));
        Assert.assertTrue("Should be a proxy for refreshable scripts", AopUtils.isAopProxy(messenger));
        Assert.assertTrue("Should be an instance of Refreshable", (messenger instanceof Refreshable));
        String desiredMessage = "Hello World!";
        Assert.assertEquals("Message is incorrect", desiredMessage, messenger.getMessage());
        Refreshable refreshable = ((Refreshable) (messenger));
        refreshable.refresh();
        Assert.assertEquals("Message is incorrect after refresh", desiredMessage, messenger.getMessage());
        Assert.assertEquals("Incorrect refresh count", 2, refreshable.getRefreshCount());
    }

    @Test
    public void nonStaticPrototypeScript() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("bshRefreshableContext.xml", getClass());
        ConfigurableMessenger messenger = ((ConfigurableMessenger) (ctx.getBean("messengerPrototype")));
        ConfigurableMessenger messenger2 = ((ConfigurableMessenger) (ctx.getBean("messengerPrototype")));
        Assert.assertTrue("Should be a proxy for refreshable scripts", AopUtils.isAopProxy(messenger));
        Assert.assertTrue("Should be an instance of Refreshable", (messenger instanceof Refreshable));
        Assert.assertEquals("Hello World!", messenger.getMessage());
        Assert.assertEquals("Hello World!", messenger2.getMessage());
        messenger.setMessage("Bye World!");
        messenger2.setMessage("Byebye World!");
        Assert.assertEquals("Bye World!", messenger.getMessage());
        Assert.assertEquals("Byebye World!", messenger2.getMessage());
        Refreshable refreshable = ((Refreshable) (messenger));
        refreshable.refresh();
        Assert.assertEquals("Hello World!", messenger.getMessage());
        Assert.assertEquals("Byebye World!", messenger2.getMessage());
        Assert.assertEquals("Incorrect refresh count", 2, refreshable.getRefreshCount());
    }

    @Test
    public void scriptCompilationException() {
        try {
            new ClassPathXmlApplicationContext("org/springframework/scripting/bsh/bshBrokenContext.xml");
            Assert.fail("Must throw exception for broken script file");
        } catch (NestedRuntimeException ex) {
            Assert.assertTrue(ex.contains(ScriptCompilationException.class));
        }
    }

    @Test
    public void scriptThatCompilesButIsJustPlainBad() throws IOException {
        ScriptSource script = Mockito.mock(ScriptSource.class);
        final String badScript = "String getMessage() { throw new IllegalArgumentException(); }";
        BDDMockito.given(script.getScriptAsString()).willReturn(badScript);
        BDDMockito.given(script.isModified()).willReturn(true);
        BshScriptFactory factory = new BshScriptFactory(((ScriptFactoryPostProcessor.INLINE_SCRIPT_PREFIX) + badScript), Messenger.class);
        try {
            Messenger messenger = ((Messenger) (factory.getScriptedObject(script, Messenger.class)));
            messenger.getMessage();
            Assert.fail("Must have thrown a BshScriptUtils.BshExecutionException.");
        } catch (BshScriptUtils expected) {
        }
    }

    @Test
    public void ctorWithNullScriptSourceLocator() {
        try {
            new BshScriptFactory(null, Messenger.class);
            Assert.fail("Must have thrown exception by this point.");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void ctorWithEmptyScriptSourceLocator() {
        try {
            new BshScriptFactory("", Messenger.class);
            Assert.fail("Must have thrown exception by this point.");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void ctorWithWhitespacedScriptSourceLocator() {
        try {
            new BshScriptFactory("\n   ", Messenger.class);
            Assert.fail("Must have thrown exception by this point.");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void resourceScriptFromTag() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("bsh-with-xsd.xml", getClass());
        TestBean testBean = ((TestBean) (ctx.getBean("testBean")));
        Collection<String> beanNames = Arrays.asList(ctx.getBeanNamesForType(Messenger.class));
        Assert.assertTrue(beanNames.contains("messenger"));
        Assert.assertTrue(beanNames.contains("messengerImpl"));
        Assert.assertTrue(beanNames.contains("messengerInstance"));
        Messenger messenger = ((Messenger) (ctx.getBean("messenger")));
        Assert.assertEquals("Hello World!", messenger.getMessage());
        Assert.assertFalse((messenger instanceof Refreshable));
        Messenger messengerImpl = ((Messenger) (ctx.getBean("messengerImpl")));
        Assert.assertEquals("Hello World!", messengerImpl.getMessage());
        Messenger messengerInstance = ((Messenger) (ctx.getBean("messengerInstance")));
        Assert.assertEquals("Hello World!", messengerInstance.getMessage());
        TestBeanAwareMessenger messengerByType = ((TestBeanAwareMessenger) (ctx.getBean("messengerByType")));
        Assert.assertEquals(testBean, messengerByType.getTestBean());
        TestBeanAwareMessenger messengerByName = ((TestBeanAwareMessenger) (ctx.getBean("messengerByName")));
        Assert.assertEquals(testBean, messengerByName.getTestBean());
        Collection<Messenger> beans = ctx.getBeansOfType(Messenger.class).values();
        Assert.assertTrue(beans.contains(messenger));
        Assert.assertTrue(beans.contains(messengerImpl));
        Assert.assertTrue(beans.contains(messengerInstance));
        Assert.assertTrue(beans.contains(messengerByType));
        Assert.assertTrue(beans.contains(messengerByName));
        ctx.close();
        Assert.assertNull(messenger.getMessage());
        Assert.assertNull(messengerImpl.getMessage());
        Assert.assertNull(messengerInstance.getMessage());
    }

    @Test
    public void prototypeScriptFromTag() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("bsh-with-xsd.xml", getClass());
        ConfigurableMessenger messenger = ((ConfigurableMessenger) (ctx.getBean("messengerPrototype")));
        ConfigurableMessenger messenger2 = ((ConfigurableMessenger) (ctx.getBean("messengerPrototype")));
        Assert.assertNotSame(messenger, messenger2);
        Assert.assertSame(messenger.getClass(), messenger2.getClass());
        Assert.assertEquals("Hello World!", messenger.getMessage());
        Assert.assertEquals("Hello World!", messenger2.getMessage());
        messenger.setMessage("Bye World!");
        messenger2.setMessage("Byebye World!");
        Assert.assertEquals("Bye World!", messenger.getMessage());
        Assert.assertEquals("Byebye World!", messenger2.getMessage());
    }

    @Test
    public void inlineScriptFromTag() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("bsh-with-xsd.xml", getClass());
        Calculator calculator = ((Calculator) (ctx.getBean("calculator")));
        Assert.assertNotNull(calculator);
        Assert.assertFalse((calculator instanceof Refreshable));
    }

    @Test
    public void refreshableFromTag() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("bsh-with-xsd.xml", getClass());
        Messenger messenger = ((Messenger) (ctx.getBean("refreshableMessenger")));
        Assert.assertEquals("Hello World!", messenger.getMessage());
        Assert.assertTrue("Messenger should be Refreshable", (messenger instanceof Refreshable));
    }

    @Test
    public void applicationEventListener() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("bsh-with-xsd.xml", getClass());
        Messenger eventListener = ((Messenger) (ctx.getBean("eventListener")));
        ctx.publishEvent(new BshScriptFactoryTests.MyEvent(ctx));
        Assert.assertEquals("count=2", eventListener.getMessage());
    }

    @SuppressWarnings("serial")
    private static class MyEvent extends ApplicationEvent {
        public MyEvent(Object source) {
            super(source);
        }
    }
}

