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
package org.springframework.beans.factory.xml;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;


/**
 *
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 */
public class BeanNameGenerationTests {
    private DefaultListableBeanFactory beanFactory;

    @Test
    public void naming() {
        String className = GeneratedNameBean.class.getName();
        String targetName = (className + (BeanDefinitionReaderUtils.GENERATED_BEAN_NAME_SEPARATOR)) + "0";
        GeneratedNameBean topLevel1 = ((GeneratedNameBean) (beanFactory.getBean(targetName)));
        Assert.assertNotNull(topLevel1);
        targetName = (className + (BeanDefinitionReaderUtils.GENERATED_BEAN_NAME_SEPARATOR)) + "1";
        GeneratedNameBean topLevel2 = ((GeneratedNameBean) (beanFactory.getBean(targetName)));
        Assert.assertNotNull(topLevel2);
        GeneratedNameBean child1 = topLevel1.getChild();
        Assert.assertNotNull(child1.getBeanName());
        Assert.assertTrue(child1.getBeanName().startsWith(className));
        GeneratedNameBean child2 = topLevel2.getChild();
        Assert.assertNotNull(child2.getBeanName());
        Assert.assertTrue(child2.getBeanName().startsWith(className));
        Assert.assertFalse(child1.getBeanName().equals(child2.getBeanName()));
    }
}

