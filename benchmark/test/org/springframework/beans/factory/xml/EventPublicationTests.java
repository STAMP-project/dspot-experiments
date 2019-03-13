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
package org.springframework.beans.factory.xml;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.parsing.AliasDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.ComponentDefinition;
import org.springframework.beans.factory.parsing.ImportDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.tests.beans.CollectingReaderEventListener;
import org.w3c.dom.Element;


/**
 *
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 */
@SuppressWarnings("rawtypes")
public class EventPublicationTests {
    private final DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

    private final CollectingReaderEventListener eventListener = new CollectingReaderEventListener();

    @Test
    public void defaultsEventReceived() throws Exception {
        List defaultsList = this.eventListener.getDefaults();
        Assert.assertTrue((!(defaultsList.isEmpty())));
        Assert.assertTrue(((defaultsList.get(0)) instanceof DocumentDefaultsDefinition));
        DocumentDefaultsDefinition defaults = ((DocumentDefaultsDefinition) (defaultsList.get(0)));
        Assert.assertEquals("true", defaults.getLazyInit());
        Assert.assertEquals("constructor", defaults.getAutowire());
        Assert.assertEquals("myInit", defaults.getInitMethod());
        Assert.assertEquals("myDestroy", defaults.getDestroyMethod());
        Assert.assertEquals("true", defaults.getMerge());
        Assert.assertTrue(((defaults.getSource()) instanceof Element));
    }

    @Test
    public void beanEventReceived() throws Exception {
        ComponentDefinition componentDefinition1 = this.eventListener.getComponentDefinition("testBean");
        Assert.assertTrue((componentDefinition1 instanceof BeanComponentDefinition));
        Assert.assertEquals(1, componentDefinition1.getBeanDefinitions().length);
        BeanDefinition beanDefinition1 = componentDefinition1.getBeanDefinitions()[0];
        Assert.assertEquals(new TypedStringValue("Rob Harrop"), beanDefinition1.getConstructorArgumentValues().getGenericArgumentValue(String.class).getValue());
        Assert.assertEquals(1, componentDefinition1.getBeanReferences().length);
        Assert.assertEquals("testBean2", componentDefinition1.getBeanReferences()[0].getBeanName());
        Assert.assertEquals(1, componentDefinition1.getInnerBeanDefinitions().length);
        BeanDefinition innerBd1 = componentDefinition1.getInnerBeanDefinitions()[0];
        Assert.assertEquals(new TypedStringValue("ACME"), innerBd1.getConstructorArgumentValues().getGenericArgumentValue(String.class).getValue());
        Assert.assertTrue(((componentDefinition1.getSource()) instanceof Element));
        ComponentDefinition componentDefinition2 = this.eventListener.getComponentDefinition("testBean2");
        Assert.assertTrue((componentDefinition2 instanceof BeanComponentDefinition));
        Assert.assertEquals(1, componentDefinition1.getBeanDefinitions().length);
        BeanDefinition beanDefinition2 = componentDefinition2.getBeanDefinitions()[0];
        Assert.assertEquals(new TypedStringValue("Juergen Hoeller"), beanDefinition2.getPropertyValues().getPropertyValue("name").getValue());
        Assert.assertEquals(0, componentDefinition2.getBeanReferences().length);
        Assert.assertEquals(1, componentDefinition2.getInnerBeanDefinitions().length);
        BeanDefinition innerBd2 = componentDefinition2.getInnerBeanDefinitions()[0];
        Assert.assertEquals(new TypedStringValue("Eva Schallmeiner"), innerBd2.getPropertyValues().getPropertyValue("name").getValue());
        Assert.assertTrue(((componentDefinition2.getSource()) instanceof Element));
    }

    @Test
    public void aliasEventReceived() throws Exception {
        List aliases = this.eventListener.getAliases("testBean");
        Assert.assertEquals(2, aliases.size());
        AliasDefinition aliasDefinition1 = ((AliasDefinition) (aliases.get(0)));
        Assert.assertEquals("testBeanAlias1", aliasDefinition1.getAlias());
        Assert.assertTrue(((aliasDefinition1.getSource()) instanceof Element));
        AliasDefinition aliasDefinition2 = ((AliasDefinition) (aliases.get(1)));
        Assert.assertEquals("testBeanAlias2", aliasDefinition2.getAlias());
        Assert.assertTrue(((aliasDefinition2.getSource()) instanceof Element));
    }

    @Test
    public void importEventReceived() throws Exception {
        List imports = this.eventListener.getImports();
        Assert.assertEquals(1, imports.size());
        ImportDefinition importDefinition = ((ImportDefinition) (imports.get(0)));
        Assert.assertEquals("beanEventsImported.xml", importDefinition.getImportedResource());
        Assert.assertTrue(((importDefinition.getSource()) instanceof Element));
    }
}

