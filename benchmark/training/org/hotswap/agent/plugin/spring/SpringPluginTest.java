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
package org.hotswap.agent.plugin.spring;


import java.io.IOException;
import org.hotswap.agent.plugin.spring.scanner.XmlBeanDefinationScannerAgent;
import org.hotswap.agent.plugin.spring.testBeans.BeanPrototype;
import org.hotswap.agent.plugin.spring.testBeans.BeanRepository;
import org.hotswap.agent.plugin.spring.testBeans.BeanService;
import org.hotswap.agent.plugin.spring.testBeans.BeanServiceImpl;
import org.hotswap.agent.plugin.spring.testBeans.Pojo;
import org.hotswap.agent.plugin.spring.testBeansHotswap.BeanPrototype2;
import org.hotswap.agent.plugin.spring.testBeansHotswap.BeanRepository2;
import org.hotswap.agent.plugin.spring.testBeansHotswap.BeanServiceImpl2;
import org.hotswap.agent.plugin.spring.testBeansHotswap.Pojo2;
import org.hotswap.agent.util.ReflectionHelper;
import org.hotswap.agent.util.spring.io.resource.ClassPathResource;
import org.hotswap.agent.util.spring.io.resource.Resource;
import org.hotswap.agent.util.test.WaitHelper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Hotswap class files of spring beans.
 *
 * See maven setup for javaagent and autohotswap settings.
 *
 * @author Jiri Bubnik
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class SpringPluginTest {
    @Autowired
    ApplicationContext applicationContext;

    /**
     * Check correct setup.
     */
    @Test
    public void basicTest() {
        Assert.assertEquals("Hello from Repository ServiceWithAspect", applicationContext.getBean(BeanService.class).hello());
        Assert.assertEquals("Hello from Repository ServiceWithAspect Prototype", applicationContext.getBean(BeanPrototype.class).hello());
    }

    /**
     * Switch method implementation (using bean definition or interface).
     */
    @Test
    public void hotswapSeviceTest() throws Exception {
        BeanServiceImpl bean = applicationContext.getBean(BeanServiceImpl.class);
        Assert.assertEquals("Hello from Repository ServiceWithAspect", bean.hello());
        swapClasses(BeanServiceImpl.class, BeanServiceImpl2.class.getName());
        Assert.assertEquals("Hello from ChangedRepository Service2WithAspect", bean.hello());
        // ensure that using interface is Ok as well
        Assert.assertEquals("Hello from ChangedRepository Service2WithAspect", applicationContext.getBean(BeanService.class).hello());
        // return configuration
        swapClasses(BeanServiceImpl.class, BeanServiceImpl.class.getName());
        Assert.assertEquals("Hello from Repository ServiceWithAspect", bean.hello());
    }

    /**
     * Add new method - invoke via reflection (not available at compilation time).
     */
    @Test
    public void hotswapSeviceAddMethodTest() throws Exception {
        swapClasses(BeanServiceImpl.class, BeanServiceImpl2.class.getName());
        String helloNewMethodIfaceVal = ((String) (ReflectionHelper.invoke(applicationContext.getBean(BeanService.class), BeanServiceImpl.class, "helloNewMethod", new Class[]{  })));
        Assert.assertEquals("Hello from helloNewMethod Service2", helloNewMethodIfaceVal);
        String helloNewMethodImplVal = ((String) (ReflectionHelper.invoke(applicationContext.getBean(BeanServiceImpl.class), BeanServiceImpl.class, "helloNewMethod", new Class[]{  })));
        Assert.assertEquals("Hello from helloNewMethod Service2", helloNewMethodImplVal);
        // return configuration
        swapClasses(BeanServiceImpl.class, BeanServiceImpl.class.getName());
        Assert.assertEquals("Hello from Repository ServiceWithAspect", applicationContext.getBean(BeanServiceImpl.class).hello());
    }

    @Test
    public void hotswapRepositoryTest() throws Exception {
        BeanServiceImpl bean = applicationContext.getBean(BeanServiceImpl.class);
        Assert.assertEquals("Hello from Repository ServiceWithAspect", bean.hello());
        swapClasses(BeanRepository.class, BeanRepository2.class.getName());
        Assert.assertEquals("Hello from ChangedRepository2 ServiceWithAspect", bean.hello());
        // return configuration
        swapClasses(BeanRepository.class, BeanRepository.class.getName());
        Assert.assertEquals("Hello from Repository ServiceWithAspect", bean.hello());
    }

    @Test
    public void hotswapRepositoryNewMethodTest() throws Exception {
        Assert.assertEquals("Hello from Repository ServiceWithAspect", applicationContext.getBean(BeanServiceImpl.class).hello());
        swapClasses(BeanRepository.class, BeanRepository2.class.getName());
        String helloNewMethodImplVal = ((String) (ReflectionHelper.invoke(applicationContext.getBean("beanRepository", BeanRepository.class), BeanRepository.class, "helloNewMethod", new Class[]{  })));
        Assert.assertEquals("Repository new method", helloNewMethodImplVal);
        // return configuration
        swapClasses(BeanRepository.class, BeanRepository.class.getName());
        Assert.assertEquals("Hello from Repository ServiceWithAspect", applicationContext.getBean(BeanServiceImpl.class).hello());
    }

    @Test
    public void hotswapPrototypeTestNewInstance() throws Exception {
        Assert.assertEquals("Hello from Repository ServiceWithAspect Prototype", applicationContext.getBean(BeanPrototype.class).hello());
        // swap service this prototype is dependent to
        swapClasses(BeanServiceImpl.class, BeanServiceImpl2.class.getName());
        Assert.assertEquals("Hello from ChangedRepository Service2WithAspect Prototype", applicationContext.getBean(BeanPrototype.class).hello());
        // swap autowired field
        swapClasses(BeanPrototype.class, BeanPrototype2.class.getName());
        Assert.assertEquals("Hello from Repository Prototype2", applicationContext.getBean(BeanPrototype.class).hello());
        // return configuration
        swapClasses(BeanServiceImpl.class, BeanServiceImpl.class.getName());
        swapClasses(BeanPrototype.class, BeanPrototype.class.getName());
        Assert.assertEquals("Hello from Repository ServiceWithAspect Prototype", applicationContext.getBean(BeanPrototype.class).hello());
    }

    @Test
    public void hotswapPrototypeTestExistingInstance() throws Exception {
        BeanPrototype beanPrototypeInstance = applicationContext.getBean(BeanPrototype.class);
        Assert.assertEquals("Hello from Repository ServiceWithAspect Prototype", beanPrototypeInstance.hello());
        swapClasses(BeanServiceImpl.class, BeanServiceImpl2.class.getName());
        Assert.assertEquals("Hello from ChangedRepository Service2WithAspect Prototype", beanPrototypeInstance.hello());
        // return configuration
        swapClasses(BeanServiceImpl.class, BeanServiceImpl.class.getName());
        Assert.assertEquals("Hello from Repository ServiceWithAspect Prototype", applicationContext.getBean(BeanPrototype.class).hello());
    }

    @Test
    public void pojoTest() throws Exception {
        // Pojo pojo = applicationContext.getAutowireCapableBeanFactory().createBean(Pojo.class);
        Assert.assertEquals(0, applicationContext.getBeanNamesForType(Pojo.class).length);
        swapClasses(Pojo.class, Pojo2.class.getName());
        Assert.assertEquals(0, applicationContext.getBeanNamesForType(Pojo.class).length);
    }

    private static ApplicationContext xmlApplicationContext;

    private static Resource xmlContext = new ClassPathResource("xmlContext.xml");

    private static Resource xmlContextWithRepo = new ClassPathResource("xmlContextWithRepository.xml");

    private static Resource xmlContextWithChangedRepo = new ClassPathResource("xmlContextWithChangedRepository.xml");

    @Test
    public void swapXmlTest() throws IOException {
        BeanService beanService = SpringPluginTest.xmlApplicationContext.getBean("beanService", BeanService.class);
        Assert.assertEquals(beanService.hello(), "Hello from Repository ServiceWithAspect");
        XmlBeanDefinationScannerAgent.reloadFlag = true;
        writeChangedRepositoryToXml();
        Assert.assertTrue(WaitHelper.waitForCommand(new WaitHelper.Command() {
            @Override
            public boolean result() throws Exception {
                return !(XmlBeanDefinationScannerAgent.reloadFlag);
            }
        }, 5000));
        Assert.assertEquals(beanService.hello(), "Hello from ChangedRepository ServiceWithAspect");
        XmlBeanDefinationScannerAgent.reloadFlag = true;
        writeRepositoryToXml();
        Assert.assertTrue(WaitHelper.waitForCommand(new WaitHelper.Command() {
            @Override
            public boolean result() throws Exception {
                return !(XmlBeanDefinationScannerAgent.reloadFlag);
            }
        }, 5000));
        Assert.assertEquals(beanService.hello(), "Hello from Repository ServiceWithAspect");
    }
}

