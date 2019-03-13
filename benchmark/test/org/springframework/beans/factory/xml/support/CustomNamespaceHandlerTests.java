/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.beans.factory.xml.support;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.Advisor;
import org.springframework.aop.interceptor.DebugInterceptor;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.PluggableSchemaResolver;
import org.springframework.context.ApplicationListener;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.tests.aop.interceptor.NopInterceptor;
import org.springframework.tests.sample.beans.ITestBean;
import org.springframework.tests.sample.beans.TestBean;
import org.xml.sax.InputSource;


/**
 * Unit tests for custom XML namespace handler implementations.
 *
 * @author Rob Harrop
 * @author Rick Evans
 * @author Chris Beams
 * @author Juergen Hoeller
 */
public class CustomNamespaceHandlerTests {
    private static final Class<?> CLASS = CustomNamespaceHandlerTests.class;

    private static final String CLASSNAME = CustomNamespaceHandlerTests.CLASS.getSimpleName();

    private static final String FQ_PATH = "org/springframework/beans/factory/xml/support";

    private static final String NS_PROPS = String.format("%s/%s.properties", CustomNamespaceHandlerTests.FQ_PATH, CustomNamespaceHandlerTests.CLASSNAME);

    private static final String NS_XML = String.format("%s/%s-context.xml", CustomNamespaceHandlerTests.FQ_PATH, CustomNamespaceHandlerTests.CLASSNAME);

    private static final String TEST_XSD = String.format("%s/%s.xsd", CustomNamespaceHandlerTests.FQ_PATH, CustomNamespaceHandlerTests.CLASSNAME);

    private GenericApplicationContext beanFactory;

    @Test
    public void testSimpleParser() throws Exception {
        TestBean bean = ((TestBean) (this.beanFactory.getBean("testBean")));
        assertTestBean(bean);
    }

    @Test
    public void testSimpleDecorator() throws Exception {
        TestBean bean = ((TestBean) (this.beanFactory.getBean("customisedTestBean")));
        assertTestBean(bean);
    }

    @Test
    public void testProxyingDecorator() throws Exception {
        ITestBean bean = ((ITestBean) (this.beanFactory.getBean("debuggingTestBean")));
        assertTestBean(bean);
        Assert.assertTrue(AopUtils.isAopProxy(bean));
        Advisor[] advisors = getAdvisors();
        Assert.assertEquals("Incorrect number of advisors", 1, advisors.length);
        Assert.assertEquals("Incorrect advice class", DebugInterceptor.class, advisors[0].getAdvice().getClass());
    }

    @Test
    public void testProxyingDecoratorNoInstance() throws Exception {
        String[] beanNames = this.beanFactory.getBeanNamesForType(ApplicationListener.class);
        Assert.assertTrue(Arrays.asList(beanNames).contains("debuggingTestBeanNoInstance"));
        Assert.assertEquals(ApplicationListener.class, this.beanFactory.getType("debuggingTestBeanNoInstance"));
        try {
            this.beanFactory.getBean("debuggingTestBeanNoInstance");
            Assert.fail("Should have thrown BeanCreationException");
        } catch (BeanCreationException ex) {
            Assert.assertTrue(((ex.getRootCause()) instanceof BeanInstantiationException));
        }
    }

    @Test
    public void testChainedDecorators() throws Exception {
        ITestBean bean = ((ITestBean) (this.beanFactory.getBean("chainedTestBean")));
        assertTestBean(bean);
        Assert.assertTrue(AopUtils.isAopProxy(bean));
        Advisor[] advisors = getAdvisors();
        Assert.assertEquals("Incorrect number of advisors", 2, advisors.length);
        Assert.assertEquals("Incorrect advice class", DebugInterceptor.class, advisors[0].getAdvice().getClass());
        Assert.assertEquals("Incorrect advice class", NopInterceptor.class, advisors[1].getAdvice().getClass());
    }

    @Test
    public void testDecorationViaAttribute() throws Exception {
        BeanDefinition beanDefinition = this.beanFactory.getBeanDefinition("decorateWithAttribute");
        Assert.assertEquals("foo", beanDefinition.getAttribute("objectName"));
    }

    // SPR-2728
    @Test
    public void testCustomElementNestedWithinUtilList() throws Exception {
        List<?> things = ((List<?>) (this.beanFactory.getBean("list.of.things")));
        Assert.assertNotNull(things);
        Assert.assertEquals(2, things.size());
    }

    // SPR-2728
    @Test
    public void testCustomElementNestedWithinUtilSet() throws Exception {
        Set<?> things = ((Set<?>) (this.beanFactory.getBean("set.of.things")));
        Assert.assertNotNull(things);
        Assert.assertEquals(2, things.size());
    }

    // SPR-2728
    @Test
    public void testCustomElementNestedWithinUtilMap() throws Exception {
        Map<?, ?> things = ((Map<?, ?>) (this.beanFactory.getBean("map.of.things")));
        Assert.assertNotNull(things);
        Assert.assertEquals(2, things.size());
    }

    private final class DummySchemaResolver extends PluggableSchemaResolver {
        public DummySchemaResolver() {
            super(CustomNamespaceHandlerTests.CLASS.getClassLoader());
        }

        @Override
        public InputSource resolveEntity(String publicId, String systemId) throws IOException {
            InputSource source = super.resolveEntity(publicId, systemId);
            if (source == null) {
                Resource resource = new ClassPathResource(CustomNamespaceHandlerTests.TEST_XSD);
                source = new InputSource(resource.getInputStream());
                source.setPublicId(publicId);
                source.setSystemId(systemId);
            }
            return source;
        }
    }
}

