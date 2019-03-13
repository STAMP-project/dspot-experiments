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


import java.io.File;
import org.hotswap.agent.plugin.spring.wildcardstest.beans.hotswap.BeanServiceImpl2;
import org.hotswap.agent.plugin.spring.wildcardstest.beans.hotswap.NewHelloServiceImpl;
import org.hotswap.agent.plugin.spring.wildcardstest.beans.testbeans.BeanService;
import org.hotswap.agent.plugin.spring.wildcardstest.beans.testbeans.BeanServiceImpl;
import org.hotswap.agent.plugin.spring.wildcardstest.beans.testbeans.NewHelloService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Tests when the application context file is using component-scan with wildcards
 *
 * See maven setup for javaagent and autohotswap settings.
 *
 * @author Cedric Chabanois
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:wildcardsApplicationContext.xml" })
public class ComponentScanWithWildcardsTest {
    @Autowired
    AutowireCapableBeanFactory factory;

    @Test
    public void testHotswapService() throws Exception {
        BeanServiceImpl bean = factory.getBean(BeanServiceImpl.class);
        try {
            // Given
            Assert.assertEquals("Hello from Repository ServiceWithAspect", bean.hello());
            // When
            swapClasses(BeanServiceImpl.class, BeanServiceImpl2.class.getName());
            // Then
            Assert.assertEquals("Hello from ChangedRepository Service2WithAspect", bean.hello());
            Assert.assertEquals("Hello from ChangedRepository Service2WithAspect", factory.getBean(BeanService.class).hello());
        } finally {
            swapClasses(BeanServiceImpl.class, BeanServiceImpl.class.getName());
            Assert.assertEquals("Hello from Repository ServiceWithAspect", bean.hello());
        }
    }

    @Test
    public void testNewServiceClassFile() throws Exception {
        File file = null;
        try {
            // Given
            assertNoBean(NewHelloService.class);
            // When
            file = ComponentScanWithWildcardsTest.copyClassFileAndWaitForReload(NewHelloServiceImpl.class, "org.hotswap.agent.plugin.spring.wildcardstest.beans.testbeans.NewHelloServiceImpl");
            // Then
            Assert.assertEquals("Hello from Repository NewHelloServiceWithAspect", factory.getBean(NewHelloService.class).hello());
        } finally {
            if ((file != null) && (file.exists())) {
                // explicit GC on windows to release class file
                System.gc();
                // we don't reload on delete
                if (!(file.delete())) {
                    System.err.println((("Warning - unable to delete class file " + (file.getAbsolutePath())) + ". Run maven clean project or delete the file manually before running subsequent test runs."));
                }
            }
        }
    }
}

