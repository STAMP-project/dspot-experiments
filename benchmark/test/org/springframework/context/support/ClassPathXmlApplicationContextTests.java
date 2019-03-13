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
package org.springframework.context.support;


import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.CannotLoadBeanClassException;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.tests.sample.beans.ResourceTestBean;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ObjectUtils;


/**
 *
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 */
public class ClassPathXmlApplicationContextTests {
    private static final String PATH = "/org/springframework/context/support/";

    private static final String RESOURCE_CONTEXT = (ClassPathXmlApplicationContextTests.PATH) + "ClassPathXmlApplicationContextTests-resource.xml";

    private static final String CONTEXT_WILDCARD = (ClassPathXmlApplicationContextTests.PATH) + "test/context*.xml";

    private static final String CONTEXT_A = "test/contextA.xml";

    private static final String CONTEXT_B = "test/contextB.xml";

    private static final String CONTEXT_C = "test/contextC.xml";

    private static final String FQ_CONTEXT_A = (ClassPathXmlApplicationContextTests.PATH) + (ClassPathXmlApplicationContextTests.CONTEXT_A);

    private static final String FQ_CONTEXT_B = (ClassPathXmlApplicationContextTests.PATH) + (ClassPathXmlApplicationContextTests.CONTEXT_B);

    private static final String FQ_CONTEXT_C = (ClassPathXmlApplicationContextTests.PATH) + (ClassPathXmlApplicationContextTests.CONTEXT_C);

    private static final String SIMPLE_CONTEXT = "simpleContext.xml";

    private static final String FQ_SIMPLE_CONTEXT = (ClassPathXmlApplicationContextTests.PATH) + "simpleContext.xml";

    private static final String FQ_ALIASED_CONTEXT_C = (ClassPathXmlApplicationContextTests.PATH) + "test/aliased-contextC.xml";

    private static final String INVALID_VALUE_TYPE_CONTEXT = (ClassPathXmlApplicationContextTests.PATH) + "invalidValueType.xml";

    private static final String CHILD_WITH_PROXY_CONTEXT = (ClassPathXmlApplicationContextTests.PATH) + "childWithProxy.xml";

    private static final String INVALID_CLASS_CONTEXT = "invalidClass.xml";

    private static final String CLASS_WITH_PLACEHOLDER_CONTEXT = "classWithPlaceholder.xml";

    private static final String ALIAS_THAT_OVERRIDES_PARENT_CONTEXT = (ClassPathXmlApplicationContextTests.PATH) + "aliasThatOverridesParent.xml";

    private static final String ALIAS_FOR_PARENT_CONTEXT = (ClassPathXmlApplicationContextTests.PATH) + "aliasForParent.xml";

    private static final String TEST_PROPERTIES = "test.properties";

    @Test
    public void testSingleConfigLocation() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(ClassPathXmlApplicationContextTests.FQ_SIMPLE_CONTEXT);
        Assert.assertTrue(ctx.containsBean("someMessageSource"));
        ctx.close();
    }

    @Test
    public void testMultipleConfigLocations() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(ClassPathXmlApplicationContextTests.FQ_CONTEXT_B, ClassPathXmlApplicationContextTests.FQ_CONTEXT_C, ClassPathXmlApplicationContextTests.FQ_CONTEXT_A);
        Assert.assertTrue(ctx.containsBean("service"));
        Assert.assertTrue(ctx.containsBean("logicOne"));
        Assert.assertTrue(ctx.containsBean("logicTwo"));
        // re-refresh (after construction refresh)
        Service service = ((Service) (ctx.getBean("service")));
        ctx.refresh();
        Assert.assertTrue(service.isProperlyDestroyed());
        // regular close call
        service = ((Service) (ctx.getBean("service")));
        ctx.close();
        Assert.assertTrue(service.isProperlyDestroyed());
        // re-activating and re-closing the context (SPR-13425)
        ctx.refresh();
        service = ((Service) (ctx.getBean("service")));
        ctx.close();
        Assert.assertTrue(service.isProperlyDestroyed());
    }

    @Test
    public void testConfigLocationPattern() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(ClassPathXmlApplicationContextTests.CONTEXT_WILDCARD);
        Assert.assertTrue(ctx.containsBean("service"));
        Assert.assertTrue(ctx.containsBean("logicOne"));
        Assert.assertTrue(ctx.containsBean("logicTwo"));
        Service service = ((Service) (ctx.getBean("service")));
        ctx.close();
        Assert.assertTrue(service.isProperlyDestroyed());
    }

    @Test
    public void testSingleConfigLocationWithClass() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(ClassPathXmlApplicationContextTests.SIMPLE_CONTEXT, getClass());
        Assert.assertTrue(ctx.containsBean("someMessageSource"));
        ctx.close();
    }

    @Test
    public void testAliasWithPlaceholder() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(ClassPathXmlApplicationContextTests.FQ_CONTEXT_B, ClassPathXmlApplicationContextTests.FQ_ALIASED_CONTEXT_C, ClassPathXmlApplicationContextTests.FQ_CONTEXT_A);
        Assert.assertTrue(ctx.containsBean("service"));
        Assert.assertTrue(ctx.containsBean("logicOne"));
        Assert.assertTrue(ctx.containsBean("logicTwo"));
        ctx.refresh();
    }

    @Test
    public void testContextWithInvalidValueType() throws IOException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{ ClassPathXmlApplicationContextTests.INVALID_VALUE_TYPE_CONTEXT }, false);
        try {
            context.refresh();
            Assert.fail("Should have thrown BeanCreationException");
        } catch (BeanCreationException ex) {
            Assert.assertTrue(ex.contains(TypeMismatchException.class));
            Assert.assertTrue(ex.toString().contains("someMessageSource"));
            Assert.assertTrue(ex.toString().contains("useCodeAsDefaultMessage"));
            checkExceptionFromInvalidValueType(ex);
            checkExceptionFromInvalidValueType(new ExceptionInInitializerError(ex));
            Assert.assertFalse(context.isActive());
        }
    }

    @Test
    public void testContextWithInvalidLazyClass() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(ClassPathXmlApplicationContextTests.INVALID_CLASS_CONTEXT, getClass());
        Assert.assertTrue(ctx.containsBean("someMessageSource"));
        try {
            ctx.getBean("someMessageSource");
            Assert.fail("Should have thrown CannotLoadBeanClassException");
        } catch (CannotLoadBeanClassException ex) {
            Assert.assertTrue(ex.contains(ClassNotFoundException.class));
        }
        ctx.close();
    }

    @Test
    public void testContextWithClassNameThatContainsPlaceholder() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(ClassPathXmlApplicationContextTests.CLASS_WITH_PLACEHOLDER_CONTEXT, getClass());
        Assert.assertTrue(ctx.containsBean("someMessageSource"));
        Assert.assertTrue(((ctx.getBean("someMessageSource")) instanceof StaticMessageSource));
        ctx.close();
    }

    @Test
    public void testMultipleConfigLocationsWithClass() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{ ClassPathXmlApplicationContextTests.CONTEXT_B, ClassPathXmlApplicationContextTests.CONTEXT_C, ClassPathXmlApplicationContextTests.CONTEXT_A }, getClass());
        Assert.assertTrue(ctx.containsBean("service"));
        Assert.assertTrue(ctx.containsBean("logicOne"));
        Assert.assertTrue(ctx.containsBean("logicTwo"));
        ctx.close();
    }

    @Test
    public void testFactoryBeanAndApplicationListener() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(ClassPathXmlApplicationContextTests.CONTEXT_WILDCARD);
        ctx.getBeanFactory().registerSingleton("manualFBAAL", new FactoryBeanAndApplicationListener());
        Assert.assertEquals(2, ctx.getBeansOfType(ApplicationListener.class).size());
        ctx.close();
    }

    @Test
    public void testMessageSourceAware() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(ClassPathXmlApplicationContextTests.CONTEXT_WILDCARD);
        MessageSource messageSource = ((MessageSource) (ctx.getBean("messageSource")));
        Service service1 = ((Service) (ctx.getBean("service")));
        Assert.assertEquals(ctx, service1.getMessageSource());
        Service service2 = ((Service) (ctx.getBean("service2")));
        Assert.assertEquals(ctx, service2.getMessageSource());
        AutowiredService autowiredService1 = ((AutowiredService) (ctx.getBean("autowiredService")));
        Assert.assertEquals(messageSource, autowiredService1.getMessageSource());
        AutowiredService autowiredService2 = ((AutowiredService) (ctx.getBean("autowiredService2")));
        Assert.assertEquals(messageSource, autowiredService2.getMessageSource());
        ctx.close();
    }

    @Test
    public void testResourceArrayPropertyEditor() throws IOException {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(ClassPathXmlApplicationContextTests.CONTEXT_WILDCARD);
        Service service = ((Service) (ctx.getBean("service")));
        Assert.assertEquals(3, service.getResources().length);
        List<Resource> resources = Arrays.asList(service.getResources());
        Assert.assertTrue(resources.contains(new org.springframework.core.io.FileSystemResource(new ClassPathResource(ClassPathXmlApplicationContextTests.FQ_CONTEXT_A).getFile())));
        Assert.assertTrue(resources.contains(new org.springframework.core.io.FileSystemResource(new ClassPathResource(ClassPathXmlApplicationContextTests.FQ_CONTEXT_B).getFile())));
        Assert.assertTrue(resources.contains(new org.springframework.core.io.FileSystemResource(new ClassPathResource(ClassPathXmlApplicationContextTests.FQ_CONTEXT_C).getFile())));
        ctx.close();
    }

    @Test
    public void testChildWithProxy() throws Exception {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(ClassPathXmlApplicationContextTests.CONTEXT_WILDCARD);
        ClassPathXmlApplicationContext child = new ClassPathXmlApplicationContext(new String[]{ ClassPathXmlApplicationContextTests.CHILD_WITH_PROXY_CONTEXT }, ctx);
        Assert.assertTrue(AopUtils.isAopProxy(child.getBean("assemblerOne")));
        Assert.assertTrue(AopUtils.isAopProxy(child.getBean("assemblerTwo")));
        ctx.close();
    }

    @Test
    public void testAliasForParentContext() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(ClassPathXmlApplicationContextTests.FQ_SIMPLE_CONTEXT);
        Assert.assertTrue(ctx.containsBean("someMessageSource"));
        ClassPathXmlApplicationContext child = new ClassPathXmlApplicationContext(new String[]{ ClassPathXmlApplicationContextTests.ALIAS_FOR_PARENT_CONTEXT }, ctx);
        Assert.assertTrue(child.containsBean("someMessageSource"));
        Assert.assertTrue(child.containsBean("yourMessageSource"));
        Assert.assertTrue(child.containsBean("myMessageSource"));
        Assert.assertTrue(child.isSingleton("someMessageSource"));
        Assert.assertTrue(child.isSingleton("yourMessageSource"));
        Assert.assertTrue(child.isSingleton("myMessageSource"));
        Assert.assertEquals(StaticMessageSource.class, child.getType("someMessageSource"));
        Assert.assertEquals(StaticMessageSource.class, child.getType("yourMessageSource"));
        Assert.assertEquals(StaticMessageSource.class, child.getType("myMessageSource"));
        Object someMs = child.getBean("someMessageSource");
        Object yourMs = child.getBean("yourMessageSource");
        Object myMs = child.getBean("myMessageSource");
        Assert.assertSame(someMs, yourMs);
        Assert.assertSame(someMs, myMs);
        String[] aliases = child.getAliases("someMessageSource");
        Assert.assertEquals(2, aliases.length);
        Assert.assertEquals("myMessageSource", aliases[0]);
        Assert.assertEquals("yourMessageSource", aliases[1]);
        aliases = child.getAliases("myMessageSource");
        Assert.assertEquals(2, aliases.length);
        Assert.assertEquals("someMessageSource", aliases[0]);
        Assert.assertEquals("yourMessageSource", aliases[1]);
        child.close();
        ctx.close();
    }

    @Test
    public void testAliasThatOverridesParent() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(ClassPathXmlApplicationContextTests.FQ_SIMPLE_CONTEXT);
        Object someMs = ctx.getBean("someMessageSource");
        ClassPathXmlApplicationContext child = new ClassPathXmlApplicationContext(new String[]{ ClassPathXmlApplicationContextTests.ALIAS_THAT_OVERRIDES_PARENT_CONTEXT }, ctx);
        Object myMs = child.getBean("myMessageSource");
        Object someMs2 = child.getBean("someMessageSource");
        Assert.assertSame(myMs, someMs2);
        Assert.assertNotSame(someMs, someMs2);
        assertOneMessageSourceOnly(child, myMs);
    }

    @Test
    public void testAliasThatOverridesEarlierBean() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(ClassPathXmlApplicationContextTests.FQ_SIMPLE_CONTEXT, ClassPathXmlApplicationContextTests.ALIAS_THAT_OVERRIDES_PARENT_CONTEXT);
        Object myMs = ctx.getBean("myMessageSource");
        Object someMs2 = ctx.getBean("someMessageSource");
        Assert.assertSame(myMs, someMs2);
        assertOneMessageSourceOnly(ctx, myMs);
    }

    @Test
    public void testResourceAndInputStream() throws IOException {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(ClassPathXmlApplicationContextTests.RESOURCE_CONTEXT) {
            @Override
            public Resource getResource(String location) {
                if (ClassPathXmlApplicationContextTests.TEST_PROPERTIES.equals(location)) {
                    return new ClassPathResource(ClassPathXmlApplicationContextTests.TEST_PROPERTIES, ClassPathXmlApplicationContextTests.class);
                }
                return super.getResource(location);
            }
        };
        ResourceTestBean resource1 = ((ResourceTestBean) (ctx.getBean("resource1")));
        ResourceTestBean resource2 = ((ResourceTestBean) (ctx.getBean("resource2")));
        Assert.assertTrue(((resource1.getResource()) instanceof ClassPathResource));
        StringWriter writer = new StringWriter();
        FileCopyUtils.copy(new InputStreamReader(resource1.getResource().getInputStream()), writer);
        Assert.assertEquals("contexttest", writer.toString());
        writer = new StringWriter();
        FileCopyUtils.copy(new InputStreamReader(resource1.getInputStream()), writer);
        Assert.assertEquals("test", writer.toString());
        writer = new StringWriter();
        FileCopyUtils.copy(new InputStreamReader(resource2.getResource().getInputStream()), writer);
        Assert.assertEquals("contexttest", writer.toString());
        writer = new StringWriter();
        FileCopyUtils.copy(new InputStreamReader(resource2.getInputStream()), writer);
        Assert.assertEquals("test", writer.toString());
        ctx.close();
    }

    @Test
    public void testGenericApplicationContextWithXmlBeanDefinitions() {
        GenericApplicationContext ctx = new GenericApplicationContext();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(ctx);
        reader.loadBeanDefinitions(new ClassPathResource(ClassPathXmlApplicationContextTests.CONTEXT_B, getClass()));
        reader.loadBeanDefinitions(new ClassPathResource(ClassPathXmlApplicationContextTests.CONTEXT_C, getClass()));
        reader.loadBeanDefinitions(new ClassPathResource(ClassPathXmlApplicationContextTests.CONTEXT_A, getClass()));
        ctx.refresh();
        Assert.assertTrue(ctx.containsBean("service"));
        Assert.assertTrue(ctx.containsBean("logicOne"));
        Assert.assertTrue(ctx.containsBean("logicTwo"));
        ctx.close();
    }

    @Test
    public void testGenericApplicationContextWithXmlBeanDefinitionsAndClassLoaderNull() {
        GenericApplicationContext ctx = new GenericApplicationContext();
        ctx.setClassLoader(null);
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(ctx);
        reader.loadBeanDefinitions(new ClassPathResource(ClassPathXmlApplicationContextTests.CONTEXT_B, getClass()));
        reader.loadBeanDefinitions(new ClassPathResource(ClassPathXmlApplicationContextTests.CONTEXT_C, getClass()));
        reader.loadBeanDefinitions(new ClassPathResource(ClassPathXmlApplicationContextTests.CONTEXT_A, getClass()));
        ctx.refresh();
        Assert.assertEquals(ObjectUtils.identityToString(ctx), ctx.getId());
        Assert.assertEquals(ObjectUtils.identityToString(ctx), ctx.getDisplayName());
        Assert.assertTrue(ctx.containsBean("service"));
        Assert.assertTrue(ctx.containsBean("logicOne"));
        Assert.assertTrue(ctx.containsBean("logicTwo"));
        ctx.close();
    }

    @Test
    public void testGenericApplicationContextWithXmlBeanDefinitionsAndSpecifiedId() {
        GenericApplicationContext ctx = new GenericApplicationContext();
        ctx.setId("testContext");
        ctx.setDisplayName("Test Context");
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(ctx);
        reader.loadBeanDefinitions(new ClassPathResource(ClassPathXmlApplicationContextTests.CONTEXT_B, getClass()));
        reader.loadBeanDefinitions(new ClassPathResource(ClassPathXmlApplicationContextTests.CONTEXT_C, getClass()));
        reader.loadBeanDefinitions(new ClassPathResource(ClassPathXmlApplicationContextTests.CONTEXT_A, getClass()));
        ctx.refresh();
        Assert.assertEquals("testContext", ctx.getId());
        Assert.assertEquals("Test Context", ctx.getDisplayName());
        Assert.assertTrue(ctx.containsBean("service"));
        Assert.assertTrue(ctx.containsBean("logicOne"));
        Assert.assertTrue(ctx.containsBean("logicTwo"));
        ctx.close();
    }
}

