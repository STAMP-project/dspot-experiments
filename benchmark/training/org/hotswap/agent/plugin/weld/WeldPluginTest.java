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
package org.hotswap.agent.plugin.weld;


import java.net.URL;
import java.util.Collection;
import org.hotswap.agent.plugin.hotswapper.HotSwapper;
import org.hotswap.agent.plugin.weld.command.BeanClassRefreshAgent;
import org.hotswap.agent.plugin.weld.testBeans.DependentHello1;
import org.hotswap.agent.plugin.weld.testBeans.HelloProducer1;
import org.hotswap.agent.plugin.weld.testBeans.HelloService;
import org.hotswap.agent.plugin.weld.testBeans.HelloServiceDependant;
import org.hotswap.agent.plugin.weld.testBeans.HelloServiceImpl1;
import org.hotswap.agent.plugin.weld.testBeans.ProxyHello1;
import org.hotswap.agent.plugin.weld.testBeans.ProxyHosting;
import org.hotswap.agent.plugin.weld.testBeansHotswap.DependentHello2;
import org.hotswap.agent.plugin.weld.testBeansHotswap.HelloProducer2;
import org.hotswap.agent.plugin.weld.testBeansHotswap.HelloProducer3;
import org.hotswap.agent.plugin.weld.testBeansHotswap.HelloServiceImpl2;
import org.hotswap.agent.plugin.weld.testBeansHotswap.ProxyHello2;
import org.hotswap.agent.util.ReflectionHelper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static WeldPlugin.isTestEnvironment;


/**
 * Test Weld Plugin (test code could be synchronized with OWB test)
 *
 * See maven setup for javaagent and autohotswap settings.
 *
 * @author Vladimir Dvorak
 */
@RunWith(WeldJUnit4Runner.class)
public class WeldPluginTest {
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
     * Add new method - invoke via reflection (not available at compilation
     * time).
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

    /**
     * Plugin is currently unable to reload prototype bean instance.
     */
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

    @Test
    public void newBeanClassIsManagedBeanReRunTestOnlyAfterMvnClean() throws Exception {
        try {
            isTestEnvironment = true;
            Collection<BeanClassRefreshAgent> instances = BeanClassRefreshAgent.getInstances();
            for (BeanClassRefreshAgent instance : instances) {
                // create new class and class file. rerun test only after clean
                Class newClass = HotSwapper.newClass("NewClass", instance.getBdaId(), getClass().getClassLoader());
                URL resource = newClass.getClassLoader().getResource("NewClass.class");
                Thread.sleep(1000);// wait redefine

                Object bean = getBeanInstance(newClass);
                Assert.assertNotNull(bean);
                break;
            }
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
}

