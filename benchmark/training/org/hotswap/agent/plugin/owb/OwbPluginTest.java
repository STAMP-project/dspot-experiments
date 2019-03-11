/**
 * Copyright 2013-2019 the HotswapAgent authors.
 *
 * This file is part of HotswapAgent.
 *
 * HotswapAgent is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 2 of the License, or (at your
 * option) any later version.
 *
 * HotswapAgent is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with HotswapAgent. If not, see http://www.gnu.org/licenses/.
 */
package org.hotswap.agent.plugin.owb;


import OwbPlugin.archivePath;
import org.hotswap.agent.plugin.hotswapper.HotSwapper;
import org.hotswap.agent.plugin.owb.testBeans.DependentHello1;
import org.hotswap.agent.plugin.owb.testBeans.HelloProducer1;
import org.hotswap.agent.plugin.owb.testBeans.HelloService;
import org.hotswap.agent.plugin.owb.testBeans.HelloServiceDependant;
import org.hotswap.agent.plugin.owb.testBeans.HelloServiceImpl1;
import org.hotswap.agent.plugin.owb.testBeans.ProxyHello1;
import org.hotswap.agent.plugin.owb.testBeans.ProxyHosting;
import org.hotswap.agent.plugin.owb.testBeans.SessionBean1;
import org.hotswap.agent.plugin.owb.testBeansHotswap.DependentHello2;
import org.hotswap.agent.plugin.owb.testBeansHotswap.HelloProducer2;
import org.hotswap.agent.plugin.owb.testBeansHotswap.HelloProducer3;
import org.hotswap.agent.plugin.owb.testBeansHotswap.HelloServiceImpl2;
import org.hotswap.agent.plugin.owb.testBeansHotswap.ProxyHello2;
import org.hotswap.agent.plugin.owb.testBeansHotswap.SessionBean2;
import org.hotswap.agent.util.ReflectionHelper;
import org.junit.Assert;
import org.junit.Test;

import static OwbPlugin.isTestEnvironment;


/**
 * Test OWB Plugin (test code could be synchronized with Weld test)
 *
 * See maven setup for javaagent and autohotswap settings.
 *
 * @author Vladimir Dvorak
 */
public class OwbPluginTest extends HAAbstractUnitTest {
    /**
     * Check correct setup.
     */
    @Test
    public void basicTest() {
        Assert.assertEquals("HelloServiceImpl1.hello():HelloProducer1.hello()", getBeanInstance(HelloService.class).hello());
        Assert.assertEquals("DependentHello1.hello():HelloServiceImpl1.hello():HelloProducer1.hello()", getBeanInstance(DependentHello1.class).hello());
    }

    /**
     * Switch method implementation (using bean definition or interface).
     */
    @Test
    public void hotswapServiceTest() throws Exception {
        HelloServiceImpl1 bean = getBeanInstance(HelloServiceImpl1.class);
        Assert.assertEquals("HelloServiceImpl1.hello():HelloProducer1.hello()", bean.hello());
        swapClasses(HelloServiceImpl1.class, HelloServiceImpl2.class.getName());
        Assert.assertEquals("null:HelloProducer2.hello()", bean.hello());
        // Test set name="Service2" by reflection call
        HelloServiceImpl1.class.getMethod("initName", new Class[0]).invoke(bean, new Object[0]);
        Assert.assertEquals("HelloServiceImpl2.hello(initialized):HelloProducer2.hello()", getBeanInstance(HelloServiceImpl1.class).hello());
        // ensure that using interface is Ok as well
        Assert.assertEquals("HelloServiceImpl2.hello(initialized):HelloProducer2.hello()", getBeanInstance(HelloService.class).hello());
        // return configuration
        swapClasses(HelloServiceImpl1.class, HelloServiceImpl1.class.getName());
        Assert.assertEquals("HelloServiceImpl1.hello():HelloProducer1.hello()", bean.hello());
    }

    /**
     * Add new method - invoke via reflection (not available at compilation time).
     */
    @Test
    public void hotswapSeviceAddMethodTest() throws Exception {
        swapClasses(HelloServiceImpl1.class, HelloServiceImpl2.class.getName());
        String helloNewMethodIfaceVal = ((String) (ReflectionHelper.invoke(getBeanInstance(HelloService.class), HelloServiceImpl1.class, "helloNewMethod", new Class[]{  })));
        Assert.assertEquals("HelloServiceImpl2.helloNewMethod()", helloNewMethodIfaceVal);
        String helloNewMethodImplVal = ((String) (ReflectionHelper.invoke(getBeanInstance(HelloServiceImpl1.class), HelloServiceImpl1.class, "helloNewMethod", new Class[]{  })));
        Assert.assertEquals("HelloServiceImpl2.helloNewMethod()", helloNewMethodImplVal);
        // return configuration
        swapClasses(HelloServiceImpl1.class, HelloServiceImpl1.class.getName());
        Assert.assertEquals("HelloServiceImpl1.hello():HelloProducer1.hello()", getBeanInstance(HelloServiceImpl1.class).hello());
    }

    @Test
    public void hotswapRepositoryTest() throws Exception {
        HelloServiceDependant bean = getBeanInstance(HelloServiceDependant.class);
        Assert.assertEquals("HelloServiceDependant.hello():HelloProducer1.hello()", bean.hello());
        swapClasses(HelloProducer1.class, HelloProducer2.class.getName());
        Assert.assertEquals("HelloServiceDependant.hello():HelloProducer2.hello()", bean.hello());
        swapClasses(HelloProducer1.class, HelloProducer3.class.getName());
        try {
            Assert.assertEquals("HelloProducer3.hello():HelloProducer2.hello()", bean.hello());
        } catch (NullPointerException npe) {
            System.out.println("INFO: dependant beans are not reinjected now.");
        }
        Assert.assertEquals("HelloServiceDependant.hello():HelloProducer3.hello():HelloProducer2.hello()", getBeanInstance(HelloServiceDependant.class).hello());
        // return configuration
        swapClasses(HelloProducer1.class, HelloProducer1.class.getName());
        Assert.assertEquals("HelloServiceDependant.hello():HelloProducer1.hello()", bean.hello());
    }

    @Test
    public void hotswapRepositoryNewMethodTest() throws Exception {
        Assert.assertEquals("HelloServiceImpl1.hello():HelloProducer1.hello()", getBeanInstance(HelloServiceImpl1.class).hello());
        swapClasses(HelloProducer1.class, HelloProducer3.class.getName());
        String helloNewMethodImplVal = ((String) (ReflectionHelper.invoke(getBeanInstance(HelloProducer1.class), HelloProducer1.class, "helloNewMethod", new Class[]{  })));
        Assert.assertEquals("HelloProducer3.helloNewMethod()", helloNewMethodImplVal);
        // return configuration
        swapClasses(HelloProducer1.class, HelloProducer1.class.getName());
        Assert.assertEquals("HelloServiceImpl1.hello():HelloProducer1.hello()", getBeanInstance(HelloServiceImpl1.class).hello());
    }

    @Test
    public void hotswapPrototypeTest() throws Exception {
        Assert.assertEquals("DependentHello1.hello():HelloServiceImpl1.hello():HelloProducer1.hello()", getBeanInstance(DependentHello1.class).hello());
        // swap service this prototype is dependent to
        swapClasses(HelloServiceImpl1.class, HelloServiceImpl2.class.getName());
        Assert.assertEquals("DependentHello1.hello():null:HelloProducer2.hello()", getBeanInstance(DependentHello1.class).hello());
        HelloServiceImpl1.class.getMethod("initName", new Class[0]).invoke(getBeanInstance(HelloServiceImpl1.class), new Object[0]);
        Assert.assertEquals("DependentHello1.hello():HelloServiceImpl2.hello(initialized):HelloProducer2.hello()", getBeanInstance(DependentHello1.class).hello());
        // swap Inject field
        swapClasses(DependentHello1.class, DependentHello2.class.getName());
        Assert.assertEquals("DependentHello2.hello():HelloProducer1.hello()", getBeanInstance(DependentHello1.class).hello());
        // return configuration
        swapClasses(HelloServiceImpl1.class, HelloServiceImpl1.class.getName());
        swapClasses(DependentHello1.class, DependentHello1.class.getName());
        Assert.assertEquals("DependentHello1.hello():HelloServiceImpl1.hello():HelloProducer1.hello()", getBeanInstance(DependentHello1.class).hello());
    }

    @Test
    public void hotswapPrototypeTestNotFailWhenHoldingInstanceBecauseSingletonInjectionPointWasReinitialize() throws Exception {
        DependentHello1 dependentBeanInstance = getBeanInstance(DependentHello1.class);
        Assert.assertEquals("DependentHello1.hello():HelloServiceImpl1.hello():HelloProducer1.hello()", dependentBeanInstance.hello());
        // swap service this is dependent to
        swapClasses(HelloServiceImpl1.class, HelloServiceImpl2.class.getName());
        ReflectionHelper.invoke(getBeanInstance(HelloService.class), HelloServiceImpl1.class, "initName", new Class[]{  });
        Assert.assertEquals("DependentHello1.hello():HelloServiceImpl2.hello(initialized):HelloProducer2.hello()", dependentBeanInstance.hello());
        // return configuration
        swapClasses(HelloServiceImpl1.class, HelloServiceImpl1.class.getName());
        Assert.assertEquals("DependentHello1.hello():HelloServiceImpl1.hello():HelloProducer1.hello()", getBeanInstance(DependentHello1.class).hello());
    }

    // Create new class and class file. rerun test only after clean
    @Test
    public void newBeanClassIsManagedBeanReRunTestOnlyAfterMvnClean() throws Exception {
        try {
            isTestEnvironment = true;
            Class<?> clazz = getClass();
            String path = clazz.getResource(((clazz.getSimpleName()) + ".class")).getPath().replace(((clazz.getSimpleName()) + ".class"), "");
            // create new class and class file. rerun test only after clean
            Class newClass = HotSwapper.newClass("NewClass", archivePath, getClass().getClassLoader());
            Thread.sleep(1000);// wait redefine

            Object bean = getBeanInstance(newClass);
            Assert.assertNotNull(bean);
        } finally {
            isTestEnvironment = false;
        }
    }

    @Test
    public void proxyTest() throws Exception {
        ProxyHosting proxyHosting = getBeanInstance(ProxyHosting.class);
        Assert.assertEquals("ProxyHello1.hello()", proxyHosting.hello());
        swapClasses(ProxyHello1.class, ProxyHello2.class.getName());
        Assert.assertEquals("ProxyHello2.hello()", proxyHosting.hello());
        Object proxy = proxyHosting.proxy;
        String hello2 = ((String) (ReflectionHelper.invoke(proxy, ProxyHello1.class, "hello2", new Class[]{  }, null)));
        Assert.assertEquals("ProxyHello2.hello2()", hello2);
        // return configuration
        swapClasses(ProxyHello1.class, ProxyHello1.class.getName());
        Assert.assertEquals("ProxyHello1.hello()", proxyHosting.hello());
    }

    @Test
    public void sessionBeanTest() throws Exception {
        SessionBean1 sessionBean = getBeanInstance(SessionBean1.class);
        Assert.assertEquals("SessionBean1.hello():ProxyHello1.hello()", sessionBean.hello());
        swapClasses(SessionBean1.class, SessionBean2.class.getName());
        Assert.assertEquals("SessionBean2.hello():ProxyHello2.hello():ProxyHello1.hello()", sessionBean.hello());
        // return configuration
        swapClasses(SessionBean1.class, SessionBean1.class.getName());
        Assert.assertEquals("SessionBean1.hello():ProxyHello1.hello()", sessionBean.hello());
    }
}

