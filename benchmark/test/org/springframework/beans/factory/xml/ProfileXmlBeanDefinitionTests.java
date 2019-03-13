/**
 * Copyright 2002-2018 the original author or authors.
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


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;


/**
 * Tests various combinations of profile declarations against various profile
 * activation and profile default scenarios.
 *
 * @author Chris Beams
 * @author Sam Brannen
 * @since 3.1
 */
public class ProfileXmlBeanDefinitionTests {
    private static final String PROD_ELIGIBLE_XML = "ProfileXmlBeanDefinitionTests-prodProfile.xml";

    private static final String DEV_ELIGIBLE_XML = "ProfileXmlBeanDefinitionTests-devProfile.xml";

    private static final String NOT_DEV_ELIGIBLE_XML = "ProfileXmlBeanDefinitionTests-notDevProfile.xml";

    private static final String ALL_ELIGIBLE_XML = "ProfileXmlBeanDefinitionTests-noProfile.xml";

    private static final String MULTI_ELIGIBLE_XML = "ProfileXmlBeanDefinitionTests-multiProfile.xml";

    private static final String MULTI_NEGATED_XML = "ProfileXmlBeanDefinitionTests-multiProfileNegated.xml";

    private static final String MULTI_NOT_DEV_ELIGIBLE_XML = "ProfileXmlBeanDefinitionTests-multiProfileNotDev.xml";

    private static final String MULTI_ELIGIBLE_SPACE_DELIMITED_XML = "ProfileXmlBeanDefinitionTests-spaceDelimitedProfile.xml";

    private static final String UNKNOWN_ELIGIBLE_XML = "ProfileXmlBeanDefinitionTests-unknownProfile.xml";

    private static final String DEFAULT_ELIGIBLE_XML = "ProfileXmlBeanDefinitionTests-defaultProfile.xml";

    private static final String CUSTOM_DEFAULT_ELIGIBLE_XML = "ProfileXmlBeanDefinitionTests-customDefaultProfile.xml";

    private static final String DEFAULT_AND_DEV_ELIGIBLE_XML = "ProfileXmlBeanDefinitionTests-defaultAndDevProfile.xml";

    private static final String PROD_ACTIVE = "prod";

    private static final String DEV_ACTIVE = "dev";

    private static final String NULL_ACTIVE = null;

    private static final String UNKNOWN_ACTIVE = "unknown";

    private static final String[] NONE_ACTIVE = new String[0];

    private static final String[] MULTI_ACTIVE = new String[]{ ProfileXmlBeanDefinitionTests.PROD_ACTIVE, ProfileXmlBeanDefinitionTests.DEV_ACTIVE };

    private static final String TARGET_BEAN = "foo";

    @Test(expected = IllegalArgumentException.class)
    public void testProfileValidation() {
        beanFactoryFor(ProfileXmlBeanDefinitionTests.PROD_ELIGIBLE_XML, ProfileXmlBeanDefinitionTests.NULL_ACTIVE);
    }

    @Test
    public void testProfilePermutations() {
        Assert.assertThat(beanFactoryFor(ProfileXmlBeanDefinitionTests.PROD_ELIGIBLE_XML, ProfileXmlBeanDefinitionTests.NONE_ACTIVE), CoreMatchers.not(ProfileXmlBeanDefinitionTests.containsTargetBean()));
        Assert.assertThat(beanFactoryFor(ProfileXmlBeanDefinitionTests.PROD_ELIGIBLE_XML, ProfileXmlBeanDefinitionTests.DEV_ACTIVE), CoreMatchers.not(ProfileXmlBeanDefinitionTests.containsTargetBean()));
        Assert.assertThat(beanFactoryFor(ProfileXmlBeanDefinitionTests.PROD_ELIGIBLE_XML, ProfileXmlBeanDefinitionTests.PROD_ACTIVE), ProfileXmlBeanDefinitionTests.containsTargetBean());
        Assert.assertThat(beanFactoryFor(ProfileXmlBeanDefinitionTests.PROD_ELIGIBLE_XML, ProfileXmlBeanDefinitionTests.MULTI_ACTIVE), ProfileXmlBeanDefinitionTests.containsTargetBean());
        Assert.assertThat(beanFactoryFor(ProfileXmlBeanDefinitionTests.DEV_ELIGIBLE_XML, ProfileXmlBeanDefinitionTests.NONE_ACTIVE), CoreMatchers.not(ProfileXmlBeanDefinitionTests.containsTargetBean()));
        Assert.assertThat(beanFactoryFor(ProfileXmlBeanDefinitionTests.DEV_ELIGIBLE_XML, ProfileXmlBeanDefinitionTests.DEV_ACTIVE), ProfileXmlBeanDefinitionTests.containsTargetBean());
        Assert.assertThat(beanFactoryFor(ProfileXmlBeanDefinitionTests.DEV_ELIGIBLE_XML, ProfileXmlBeanDefinitionTests.PROD_ACTIVE), CoreMatchers.not(ProfileXmlBeanDefinitionTests.containsTargetBean()));
        Assert.assertThat(beanFactoryFor(ProfileXmlBeanDefinitionTests.DEV_ELIGIBLE_XML, ProfileXmlBeanDefinitionTests.MULTI_ACTIVE), ProfileXmlBeanDefinitionTests.containsTargetBean());
        Assert.assertThat(beanFactoryFor(ProfileXmlBeanDefinitionTests.NOT_DEV_ELIGIBLE_XML, ProfileXmlBeanDefinitionTests.NONE_ACTIVE), ProfileXmlBeanDefinitionTests.containsTargetBean());
        Assert.assertThat(beanFactoryFor(ProfileXmlBeanDefinitionTests.NOT_DEV_ELIGIBLE_XML, ProfileXmlBeanDefinitionTests.DEV_ACTIVE), CoreMatchers.not(ProfileXmlBeanDefinitionTests.containsTargetBean()));
        Assert.assertThat(beanFactoryFor(ProfileXmlBeanDefinitionTests.NOT_DEV_ELIGIBLE_XML, ProfileXmlBeanDefinitionTests.PROD_ACTIVE), ProfileXmlBeanDefinitionTests.containsTargetBean());
        Assert.assertThat(beanFactoryFor(ProfileXmlBeanDefinitionTests.NOT_DEV_ELIGIBLE_XML, ProfileXmlBeanDefinitionTests.MULTI_ACTIVE), CoreMatchers.not(ProfileXmlBeanDefinitionTests.containsTargetBean()));
        Assert.assertThat(beanFactoryFor(ProfileXmlBeanDefinitionTests.ALL_ELIGIBLE_XML, ProfileXmlBeanDefinitionTests.NONE_ACTIVE), ProfileXmlBeanDefinitionTests.containsTargetBean());
        Assert.assertThat(beanFactoryFor(ProfileXmlBeanDefinitionTests.ALL_ELIGIBLE_XML, ProfileXmlBeanDefinitionTests.DEV_ACTIVE), ProfileXmlBeanDefinitionTests.containsTargetBean());
        Assert.assertThat(beanFactoryFor(ProfileXmlBeanDefinitionTests.ALL_ELIGIBLE_XML, ProfileXmlBeanDefinitionTests.PROD_ACTIVE), ProfileXmlBeanDefinitionTests.containsTargetBean());
        Assert.assertThat(beanFactoryFor(ProfileXmlBeanDefinitionTests.ALL_ELIGIBLE_XML, ProfileXmlBeanDefinitionTests.MULTI_ACTIVE), ProfileXmlBeanDefinitionTests.containsTargetBean());
        Assert.assertThat(beanFactoryFor(ProfileXmlBeanDefinitionTests.MULTI_ELIGIBLE_XML, ProfileXmlBeanDefinitionTests.NONE_ACTIVE), CoreMatchers.not(ProfileXmlBeanDefinitionTests.containsTargetBean()));
        Assert.assertThat(beanFactoryFor(ProfileXmlBeanDefinitionTests.MULTI_ELIGIBLE_XML, ProfileXmlBeanDefinitionTests.UNKNOWN_ACTIVE), CoreMatchers.not(ProfileXmlBeanDefinitionTests.containsTargetBean()));
        Assert.assertThat(beanFactoryFor(ProfileXmlBeanDefinitionTests.MULTI_ELIGIBLE_XML, ProfileXmlBeanDefinitionTests.DEV_ACTIVE), ProfileXmlBeanDefinitionTests.containsTargetBean());
        Assert.assertThat(beanFactoryFor(ProfileXmlBeanDefinitionTests.MULTI_ELIGIBLE_XML, ProfileXmlBeanDefinitionTests.PROD_ACTIVE), ProfileXmlBeanDefinitionTests.containsTargetBean());
        Assert.assertThat(beanFactoryFor(ProfileXmlBeanDefinitionTests.MULTI_ELIGIBLE_XML, ProfileXmlBeanDefinitionTests.MULTI_ACTIVE), ProfileXmlBeanDefinitionTests.containsTargetBean());
        Assert.assertThat(beanFactoryFor(ProfileXmlBeanDefinitionTests.MULTI_NEGATED_XML, ProfileXmlBeanDefinitionTests.NONE_ACTIVE), ProfileXmlBeanDefinitionTests.containsTargetBean());
        Assert.assertThat(beanFactoryFor(ProfileXmlBeanDefinitionTests.MULTI_NEGATED_XML, ProfileXmlBeanDefinitionTests.UNKNOWN_ACTIVE), ProfileXmlBeanDefinitionTests.containsTargetBean());
        Assert.assertThat(beanFactoryFor(ProfileXmlBeanDefinitionTests.MULTI_NEGATED_XML, ProfileXmlBeanDefinitionTests.DEV_ACTIVE), ProfileXmlBeanDefinitionTests.containsTargetBean());
        Assert.assertThat(beanFactoryFor(ProfileXmlBeanDefinitionTests.MULTI_NEGATED_XML, ProfileXmlBeanDefinitionTests.PROD_ACTIVE), ProfileXmlBeanDefinitionTests.containsTargetBean());
        Assert.assertThat(beanFactoryFor(ProfileXmlBeanDefinitionTests.MULTI_NEGATED_XML, ProfileXmlBeanDefinitionTests.MULTI_ACTIVE), CoreMatchers.not(ProfileXmlBeanDefinitionTests.containsTargetBean()));
        Assert.assertThat(beanFactoryFor(ProfileXmlBeanDefinitionTests.MULTI_NOT_DEV_ELIGIBLE_XML, ProfileXmlBeanDefinitionTests.NONE_ACTIVE), ProfileXmlBeanDefinitionTests.containsTargetBean());
        Assert.assertThat(beanFactoryFor(ProfileXmlBeanDefinitionTests.MULTI_NOT_DEV_ELIGIBLE_XML, ProfileXmlBeanDefinitionTests.UNKNOWN_ACTIVE), ProfileXmlBeanDefinitionTests.containsTargetBean());
        Assert.assertThat(beanFactoryFor(ProfileXmlBeanDefinitionTests.MULTI_NOT_DEV_ELIGIBLE_XML, ProfileXmlBeanDefinitionTests.DEV_ACTIVE), CoreMatchers.not(ProfileXmlBeanDefinitionTests.containsTargetBean()));
        Assert.assertThat(beanFactoryFor(ProfileXmlBeanDefinitionTests.MULTI_NOT_DEV_ELIGIBLE_XML, ProfileXmlBeanDefinitionTests.PROD_ACTIVE), ProfileXmlBeanDefinitionTests.containsTargetBean());
        Assert.assertThat(beanFactoryFor(ProfileXmlBeanDefinitionTests.MULTI_NOT_DEV_ELIGIBLE_XML, ProfileXmlBeanDefinitionTests.MULTI_ACTIVE), ProfileXmlBeanDefinitionTests.containsTargetBean());
        Assert.assertThat(beanFactoryFor(ProfileXmlBeanDefinitionTests.MULTI_ELIGIBLE_SPACE_DELIMITED_XML, ProfileXmlBeanDefinitionTests.NONE_ACTIVE), CoreMatchers.not(ProfileXmlBeanDefinitionTests.containsTargetBean()));
        Assert.assertThat(beanFactoryFor(ProfileXmlBeanDefinitionTests.MULTI_ELIGIBLE_SPACE_DELIMITED_XML, ProfileXmlBeanDefinitionTests.UNKNOWN_ACTIVE), CoreMatchers.not(ProfileXmlBeanDefinitionTests.containsTargetBean()));
        Assert.assertThat(beanFactoryFor(ProfileXmlBeanDefinitionTests.MULTI_ELIGIBLE_SPACE_DELIMITED_XML, ProfileXmlBeanDefinitionTests.DEV_ACTIVE), ProfileXmlBeanDefinitionTests.containsTargetBean());
        Assert.assertThat(beanFactoryFor(ProfileXmlBeanDefinitionTests.MULTI_ELIGIBLE_SPACE_DELIMITED_XML, ProfileXmlBeanDefinitionTests.PROD_ACTIVE), ProfileXmlBeanDefinitionTests.containsTargetBean());
        Assert.assertThat(beanFactoryFor(ProfileXmlBeanDefinitionTests.MULTI_ELIGIBLE_SPACE_DELIMITED_XML, ProfileXmlBeanDefinitionTests.MULTI_ACTIVE), ProfileXmlBeanDefinitionTests.containsTargetBean());
        Assert.assertThat(beanFactoryFor(ProfileXmlBeanDefinitionTests.UNKNOWN_ELIGIBLE_XML, ProfileXmlBeanDefinitionTests.MULTI_ACTIVE), CoreMatchers.not(ProfileXmlBeanDefinitionTests.containsTargetBean()));
    }

    @Test
    public void testDefaultProfile() {
        {
            DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
            XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
            ConfigurableEnvironment env = new StandardEnvironment();
            env.setDefaultProfiles("custom-default");
            reader.setEnvironment(env);
            reader.loadBeanDefinitions(new ClassPathResource(ProfileXmlBeanDefinitionTests.DEFAULT_ELIGIBLE_XML, getClass()));
            Assert.assertThat(beanFactory, CoreMatchers.not(ProfileXmlBeanDefinitionTests.containsTargetBean()));
        }
        {
            DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
            XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
            ConfigurableEnvironment env = new StandardEnvironment();
            env.setDefaultProfiles("custom-default");
            reader.setEnvironment(env);
            reader.loadBeanDefinitions(new ClassPathResource(ProfileXmlBeanDefinitionTests.CUSTOM_DEFAULT_ELIGIBLE_XML, getClass()));
            Assert.assertThat(beanFactory, ProfileXmlBeanDefinitionTests.containsTargetBean());
        }
    }

    @Test
    public void testDefaultAndNonDefaultProfile() {
        Assert.assertThat(beanFactoryFor(ProfileXmlBeanDefinitionTests.DEFAULT_ELIGIBLE_XML, ProfileXmlBeanDefinitionTests.NONE_ACTIVE), ProfileXmlBeanDefinitionTests.containsTargetBean());
        Assert.assertThat(beanFactoryFor(ProfileXmlBeanDefinitionTests.DEFAULT_ELIGIBLE_XML, "other"), CoreMatchers.not(ProfileXmlBeanDefinitionTests.containsTargetBean()));
        {
            DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
            XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
            ConfigurableEnvironment env = new StandardEnvironment();
            env.setActiveProfiles(ProfileXmlBeanDefinitionTests.DEV_ACTIVE);
            env.setDefaultProfiles("default");
            reader.setEnvironment(env);
            reader.loadBeanDefinitions(new ClassPathResource(ProfileXmlBeanDefinitionTests.DEFAULT_AND_DEV_ELIGIBLE_XML, getClass()));
            Assert.assertThat(beanFactory, ProfileXmlBeanDefinitionTests.containsTargetBean());
        }
        {
            DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
            XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
            ConfigurableEnvironment env = new StandardEnvironment();
            // env.setActiveProfiles(DEV_ACTIVE);
            env.setDefaultProfiles("default");
            reader.setEnvironment(env);
            reader.loadBeanDefinitions(new ClassPathResource(ProfileXmlBeanDefinitionTests.DEFAULT_AND_DEV_ELIGIBLE_XML, getClass()));
            Assert.assertThat(beanFactory, ProfileXmlBeanDefinitionTests.containsTargetBean());
        }
        {
            DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
            XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
            ConfigurableEnvironment env = new StandardEnvironment();
            // env.setActiveProfiles(DEV_ACTIVE);
            // env.setDefaultProfiles("default");
            reader.setEnvironment(env);
            reader.loadBeanDefinitions(new ClassPathResource(ProfileXmlBeanDefinitionTests.DEFAULT_AND_DEV_ELIGIBLE_XML, getClass()));
            Assert.assertThat(beanFactory, ProfileXmlBeanDefinitionTests.containsTargetBean());
        }
    }
}

