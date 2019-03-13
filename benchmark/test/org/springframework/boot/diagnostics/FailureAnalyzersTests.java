/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.diagnostics;


import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;


/**
 * Tests for {@link FailureAnalyzers}.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
public class FailureAnalyzersTests {
    private static FailureAnalyzersTests.AwareFailureAnalyzer failureAnalyzer;

    @Test
    public void analyzersAreLoadedAndCalled() {
        RuntimeException failure = new RuntimeException();
        analyzeAndReport("basic.factories", failure);
        analyze(failure);
    }

    @Test
    public void beanFactoryIsInjectedIntoBeanFactoryAwareFailureAnalyzers() {
        RuntimeException failure = new RuntimeException();
        analyzeAndReport("basic.factories", failure);
        setBeanFactory(ArgumentMatchers.any(BeanFactory.class));
    }

    @Test
    public void environmentIsInjectedIntoEnvironmentAwareFailureAnalyzers() {
        RuntimeException failure = new RuntimeException();
        analyzeAndReport("basic.factories", failure);
        setEnvironment(ArgumentMatchers.any(Environment.class));
    }

    @Test
    public void analyzerThatFailsDuringInitializationDoesNotPreventOtherAnalyzersFromBeingCalled() {
        RuntimeException failure = new RuntimeException();
        analyzeAndReport("broken-initialization.factories", failure);
        analyze(failure);
    }

    @Test
    public void analyzerThatFailsDuringAnalysisDoesNotPreventOtherAnalyzersFromBeingCalled() {
        RuntimeException failure = new RuntimeException();
        analyzeAndReport("broken-analysis.factories", failure);
        analyze(failure);
    }

    static class BasicFailureAnalyzer implements FailureAnalyzer {
        @Override
        public FailureAnalysis analyze(Throwable failure) {
            return FailureAnalyzersTests.failureAnalyzer.analyze(failure);
        }
    }

    static class BrokenInitializationFailureAnalyzer implements FailureAnalyzer {
        static {
            Object foo = null;
            foo.toString();
        }

        @Override
        public FailureAnalysis analyze(Throwable failure) {
            return null;
        }
    }

    static class BrokenAnalysisFailureAnalyzer implements FailureAnalyzer {
        @Override
        public FailureAnalysis analyze(Throwable failure) {
            throw new NoClassDefFoundError();
        }
    }

    interface AwareFailureAnalyzer extends BeanFactoryAware , FailureAnalyzer , EnvironmentAware {}

    static class StandardAwareFailureAnalyzer extends FailureAnalyzersTests.BasicFailureAnalyzer implements FailureAnalyzersTests.AwareFailureAnalyzer {
        @Override
        public void setEnvironment(Environment environment) {
            FailureAnalyzersTests.failureAnalyzer.setEnvironment(environment);
        }

        @Override
        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            FailureAnalyzersTests.failureAnalyzer.setBeanFactory(beanFactory);
        }
    }

    static class CustomSpringFactoriesClassLoader extends ClassLoader {
        private final String factoriesName;

        CustomSpringFactoriesClassLoader(String factoriesName) {
            super(FailureAnalyzersTests.CustomSpringFactoriesClassLoader.class.getClassLoader());
            this.factoriesName = factoriesName;
        }

        @Override
        public Enumeration<URL> getResources(String name) throws IOException {
            if ("META-INF/spring.factories".equals(name)) {
                return super.getResources(("failure-analyzers-tests/" + (this.factoriesName)));
            }
            return super.getResources(name);
        }
    }
}

