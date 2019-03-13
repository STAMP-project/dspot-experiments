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
package org.springframework.context.annotation.configuration;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.tests.sample.beans.TestBean;


/**
 * Unit tests cornering the bug exposed in SPR-6779.
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 */
public class ImportedConfigurationClassEnhancementTests {
    @Test
    public void autowiredConfigClassIsEnhancedWhenImported() {
        autowiredConfigClassIsEnhanced(ImportedConfigurationClassEnhancementTests.ConfigThatDoesImport.class);
    }

    @Test
    public void autowiredConfigClassIsEnhancedWhenRegisteredViaConstructor() {
        autowiredConfigClassIsEnhanced(ImportedConfigurationClassEnhancementTests.ConfigThatDoesNotImport.class, ImportedConfigurationClassEnhancementTests.ConfigToBeAutowired.class);
    }

    @Test
    public void autowiredConfigClassBeanMethodsRespectScopingWhenImported() {
        autowiredConfigClassBeanMethodsRespectScoping(ImportedConfigurationClassEnhancementTests.ConfigThatDoesImport.class);
    }

    @Test
    public void autowiredConfigClassBeanMethodsRespectScopingWhenRegisteredViaConstructor() {
        autowiredConfigClassBeanMethodsRespectScoping(ImportedConfigurationClassEnhancementTests.ConfigThatDoesNotImport.class, ImportedConfigurationClassEnhancementTests.ConfigToBeAutowired.class);
    }

    @Test
    public void importingNonConfigurationClassCausesBeanDefinitionParsingException() {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(ImportedConfigurationClassEnhancementTests.ConfigThatImportsNonConfigClass.class);
        ImportedConfigurationClassEnhancementTests.ConfigThatImportsNonConfigClass config = ctx.getBean(ImportedConfigurationClassEnhancementTests.ConfigThatImportsNonConfigClass.class);
        Assert.assertSame(ctx.getBean(TestBean.class), config.testBean);
    }

    @Configuration
    static class ConfigToBeAutowired {
        @Bean
        public TestBean testBean() {
            return new TestBean();
        }
    }

    static class Config {
        @Autowired
        ImportedConfigurationClassEnhancementTests.ConfigToBeAutowired autowiredConfig;
    }

    @Import(ImportedConfigurationClassEnhancementTests.ConfigToBeAutowired.class)
    @Configuration
    static class ConfigThatDoesImport extends ImportedConfigurationClassEnhancementTests.Config {}

    @Configuration
    static class ConfigThatDoesNotImport extends ImportedConfigurationClassEnhancementTests.Config {}

    @Configuration
    @Import(TestBean.class)
    static class ConfigThatImportsNonConfigClass {
        @Autowired
        TestBean testBean;
    }
}

