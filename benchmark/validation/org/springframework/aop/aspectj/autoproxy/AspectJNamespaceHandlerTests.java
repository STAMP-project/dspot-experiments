/**
 * Copyright 2002-2013 the original author or authors.
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
package org.springframework.aop.aspectj.autoproxy;


import AopConfigUtils.AUTO_PROXY_CREATOR_BEAN_NAME;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.config.AopNamespaceUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.tests.beans.CollectingReaderEventListener;


/**
 *
 *
 * @author Rob Harrop
 * @author Chris Beams
 */
public class AspectJNamespaceHandlerTests {
    private ParserContext parserContext;

    private CollectingReaderEventListener readerEventListener = new CollectingReaderEventListener();

    private BeanDefinitionRegistry registry = new DefaultListableBeanFactory();

    @Test
    public void testRegisterAutoProxyCreator() throws Exception {
        AopNamespaceUtils.registerAutoProxyCreatorIfNecessary(this.parserContext, null);
        Assert.assertEquals("Incorrect number of definitions registered", 1, registry.getBeanDefinitionCount());
        AopNamespaceUtils.registerAspectJAutoProxyCreatorIfNecessary(this.parserContext, null);
        Assert.assertEquals("Incorrect number of definitions registered", 1, registry.getBeanDefinitionCount());
    }

    @Test
    public void testRegisterAspectJAutoProxyCreator() throws Exception {
        AopNamespaceUtils.registerAspectJAutoProxyCreatorIfNecessary(this.parserContext, null);
        Assert.assertEquals("Incorrect number of definitions registered", 1, registry.getBeanDefinitionCount());
        AopNamespaceUtils.registerAspectJAutoProxyCreatorIfNecessary(this.parserContext, null);
        Assert.assertEquals("Incorrect number of definitions registered", 1, registry.getBeanDefinitionCount());
        BeanDefinition definition = registry.getBeanDefinition(AUTO_PROXY_CREATOR_BEAN_NAME);
        Assert.assertEquals("Incorrect APC class", AspectJAwareAdvisorAutoProxyCreator.class.getName(), definition.getBeanClassName());
    }

    @Test
    public void testRegisterAspectJAutoProxyCreatorWithExistingAutoProxyCreator() throws Exception {
        AopNamespaceUtils.registerAutoProxyCreatorIfNecessary(this.parserContext, null);
        Assert.assertEquals(1, registry.getBeanDefinitionCount());
        AopNamespaceUtils.registerAspectJAutoProxyCreatorIfNecessary(this.parserContext, null);
        Assert.assertEquals("Incorrect definition count", 1, registry.getBeanDefinitionCount());
        BeanDefinition definition = registry.getBeanDefinition(AUTO_PROXY_CREATOR_BEAN_NAME);
        Assert.assertEquals("APC class not switched", AspectJAwareAdvisorAutoProxyCreator.class.getName(), definition.getBeanClassName());
    }

    @Test
    public void testRegisterAutoProxyCreatorWhenAspectJAutoProxyCreatorAlreadyExists() throws Exception {
        AopNamespaceUtils.registerAspectJAutoProxyCreatorIfNecessary(this.parserContext, null);
        Assert.assertEquals(1, registry.getBeanDefinitionCount());
        AopNamespaceUtils.registerAutoProxyCreatorIfNecessary(this.parserContext, null);
        Assert.assertEquals("Incorrect definition count", 1, registry.getBeanDefinitionCount());
        BeanDefinition definition = registry.getBeanDefinition(AUTO_PROXY_CREATOR_BEAN_NAME);
        Assert.assertEquals("Incorrect APC class", AspectJAwareAdvisorAutoProxyCreator.class.getName(), definition.getBeanClassName());
    }
}

