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
package org.springframework.web.context;


import java.util.Locale;
import javax.servlet.ServletException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.AbstractApplicationContextTests;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.NoSuchMessageException;
import org.springframework.mock.web.test.MockServletContext;
import org.springframework.tests.sample.beans.TestBean;
import org.springframework.web.context.support.XmlWebApplicationContext;


/**
 *
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public class XmlWebApplicationContextTests extends AbstractApplicationContextTests {
    private ConfigurableWebApplicationContext root;

    @Test
    @SuppressWarnings("deprecation")
    public void environmentMerge() {
        Assert.assertThat(this.root.getEnvironment().acceptsProfiles("rootProfile1"), CoreMatchers.is(true));
        Assert.assertThat(this.root.getEnvironment().acceptsProfiles("wacProfile1"), CoreMatchers.is(false));
        Assert.assertThat(this.applicationContext.getEnvironment().acceptsProfiles("rootProfile1"), CoreMatchers.is(true));
        Assert.assertThat(this.applicationContext.getEnvironment().acceptsProfiles("wacProfile1"), CoreMatchers.is(true));
    }

    @Test
    @Override
    public void count() {
        Assert.assertTrue(("should have 14 beans, not " + (this.applicationContext.getBeanDefinitionCount())), ((this.applicationContext.getBeanDefinitionCount()) == 14));
    }

    @Test
    @SuppressWarnings("resource")
    public void withoutMessageSource() throws Exception {
        MockServletContext sc = new MockServletContext("");
        XmlWebApplicationContext wac = new XmlWebApplicationContext();
        wac.setParent(root);
        wac.setServletContext(sc);
        wac.setNamespace("testNamespace");
        wac.setConfigLocations(new String[]{ "/org/springframework/web/context/WEB-INF/test-servlet.xml" });
        wac.refresh();
        try {
            wac.getMessage("someMessage", null, Locale.getDefault());
            Assert.fail("Should have thrown NoSuchMessageException");
        } catch (NoSuchMessageException ex) {
            // expected;
        }
        String msg = wac.getMessage("someMessage", null, "default", Locale.getDefault());
        Assert.assertTrue("Default message returned", "default".equals(msg));
    }

    @Test
    public void contextNesting() {
        TestBean father = ((TestBean) (this.applicationContext.getBean("father")));
        Assert.assertTrue("Bean from root context", (father != null));
        Assert.assertTrue("Custom BeanPostProcessor applied", father.getFriends().contains("myFriend"));
        TestBean rod = ((TestBean) (this.applicationContext.getBean("rod")));
        Assert.assertTrue("Bean from child context", "Rod".equals(rod.getName()));
        Assert.assertTrue("Bean has external reference", ((rod.getSpouse()) == father));
        Assert.assertTrue("Custom BeanPostProcessor not applied", (!(rod.getFriends().contains("myFriend"))));
        rod = ((TestBean) (this.root.getBean("rod")));
        Assert.assertTrue("Bean from root context", "Roderick".equals(rod.getName()));
        Assert.assertTrue("Custom BeanPostProcessor applied", rod.getFriends().contains("myFriend"));
    }

    @Test
    public void initializingBeanAndInitMethod() throws Exception {
        Assert.assertFalse(XmlWebApplicationContextTests.InitAndIB.constructed);
        XmlWebApplicationContextTests.InitAndIB iib = ((XmlWebApplicationContextTests.InitAndIB) (this.applicationContext.getBean("init-and-ib")));
        Assert.assertTrue(XmlWebApplicationContextTests.InitAndIB.constructed);
        Assert.assertTrue(((iib.afterPropertiesSetInvoked) && (iib.initMethodInvoked)));
        Assert.assertTrue(((!(iib.destroyed)) && (!(iib.customDestroyed))));
        this.applicationContext.close();
        Assert.assertTrue(((!(iib.destroyed)) && (!(iib.customDestroyed))));
        ConfigurableApplicationContext parent = ((ConfigurableApplicationContext) (this.applicationContext.getParent()));
        parent.close();
        Assert.assertTrue(((iib.destroyed) && (iib.customDestroyed)));
        parent.close();
        Assert.assertTrue(((iib.destroyed) && (iib.customDestroyed)));
    }

    public static class InitAndIB implements DisposableBean , InitializingBean {
        public static boolean constructed;

        public boolean afterPropertiesSetInvoked;

        public boolean initMethodInvoked;

        public boolean destroyed;

        public boolean customDestroyed;

        public InitAndIB() {
            XmlWebApplicationContextTests.InitAndIB.constructed = true;
        }

        @Override
        public void afterPropertiesSet() {
            if (this.initMethodInvoked)
                Assert.fail();

            this.afterPropertiesSetInvoked = true;
        }

        /**
         * Init method
         */
        public void customInit() throws ServletException {
            if (!(this.afterPropertiesSetInvoked))
                Assert.fail();

            this.initMethodInvoked = true;
        }

        @Override
        public void destroy() {
            if (this.customDestroyed)
                Assert.fail();

            if (this.destroyed) {
                throw new IllegalStateException("Already destroyed");
            }
            this.destroyed = true;
        }

        public void customDestroy() {
            if (!(this.destroyed))
                Assert.fail();

            if (this.customDestroyed) {
                throw new IllegalStateException("Already customDestroyed");
            }
            this.customDestroyed = true;
        }
    }
}

