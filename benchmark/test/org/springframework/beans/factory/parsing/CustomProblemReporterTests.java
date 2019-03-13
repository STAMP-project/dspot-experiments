/**
 * Copyright 2002-2019 the original author or authors.
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
package org.springframework.beans.factory.parsing;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.tests.sample.beans.TestBean;


/**
 *
 *
 * @author Rob Harrop
 * @author Chris Beams
 * @since 2.0
 */
public class CustomProblemReporterTests {
    private CustomProblemReporterTests.CollatingProblemReporter problemReporter;

    private DefaultListableBeanFactory beanFactory;

    private XmlBeanDefinitionReader reader;

    @Test
    public void testErrorsAreCollated() {
        this.reader.loadBeanDefinitions(qualifiedResource(CustomProblemReporterTests.class, "context.xml"));
        Assert.assertEquals("Incorrect number of errors collated", 4, this.problemReporter.getErrors().length);
        TestBean bean = ((TestBean) (this.beanFactory.getBean("validBean")));
        Assert.assertNotNull(bean);
    }

    private static class CollatingProblemReporter implements ProblemReporter {
        private final List<Problem> errors = new ArrayList<>();

        private final List<Problem> warnings = new ArrayList<>();

        @Override
        public void fatal(Problem problem) {
            throw new BeanDefinitionParsingException(problem);
        }

        @Override
        public void error(Problem problem) {
            this.errors.add(problem);
        }

        public Problem[] getErrors() {
            return this.errors.toArray(new Problem[this.errors.size()]);
        }

        @Override
        public void warning(Problem problem) {
            this.warnings.add(problem);
        }
    }
}

