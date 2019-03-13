/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.impl;


import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import org.apache.camel.CamelContext;
import org.apache.camel.Component;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.apache.camel.NoSuchLanguageException;
import org.apache.camel.Predicate;
import org.apache.camel.spi.DataFormat;
import org.apache.camel.spi.Language;
import org.apache.camel.support.DefaultComponent;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests if the default camel context is able to resolve components and data formats using both their real names and/or fallback names.
 * Fallback names have been introduced to avoid name clash in some registries (eg. Spring application context) between components and other camel features.
 */
public class DefaultCamelContextResolverTest {
    private static CamelContext context;

    @Test
    public void testComponentWithFallbackNames() throws Exception {
        Component component = DefaultCamelContextResolverTest.context.getComponent("green");
        Assert.assertNotNull("Component not found in registry", component);
        Assert.assertTrue("Wrong instance type of the Component", (component instanceof DefaultCamelContextResolverTest.SampleComponent));
        Assert.assertTrue("Here we should have the fallback Component", ((DefaultCamelContextResolverTest.SampleComponent) (component)).isFallback());
    }

    @Test
    public void testComponentWithBothNames() throws Exception {
        Component component = DefaultCamelContextResolverTest.context.getComponent("yellow");
        Assert.assertNotNull("Component not found in registry", component);
        Assert.assertTrue("Wrong instance type of the Component", (component instanceof DefaultCamelContextResolverTest.SampleComponent));
        Assert.assertFalse("Here we should NOT have the fallback Component", ((DefaultCamelContextResolverTest.SampleComponent) (component)).isFallback());
    }

    @Test
    public void testDataFormatWithFallbackNames() throws Exception {
        DataFormat dataFormat = DefaultCamelContextResolverTest.context.resolveDataFormat("green");
        Assert.assertNotNull("DataFormat not found in registry", dataFormat);
        Assert.assertTrue("Wrong instance type of the DataFormat", (dataFormat instanceof DefaultCamelContextResolverTest.SampleDataFormat));
        Assert.assertTrue("Here we should have the fallback DataFormat", ((DefaultCamelContextResolverTest.SampleDataFormat) (dataFormat)).isFallback());
    }

    @Test
    public void testDataformatWithBothNames() throws Exception {
        DataFormat dataFormat = DefaultCamelContextResolverTest.context.resolveDataFormat("red");
        Assert.assertNotNull("DataFormat not found in registry", dataFormat);
        Assert.assertTrue("Wrong instance type of the DataFormat", (dataFormat instanceof DefaultCamelContextResolverTest.SampleDataFormat));
        Assert.assertFalse("Here we should NOT have the fallback DataFormat", ((DefaultCamelContextResolverTest.SampleDataFormat) (dataFormat)).isFallback());
    }

    @Test
    public void testLanguageWithFallbackNames() throws Exception {
        Language language = DefaultCamelContextResolverTest.context.resolveLanguage("green");
        Assert.assertNotNull("Language not found in registry", language);
        Assert.assertTrue("Wrong instance type of the Language", (language instanceof DefaultCamelContextResolverTest.SampleLanguage));
        Assert.assertTrue("Here we should have the fallback Language", ((DefaultCamelContextResolverTest.SampleLanguage) (language)).isFallback());
    }

    @Test
    public void testLanguageWithBothNames() throws Exception {
        Language language = DefaultCamelContextResolverTest.context.resolveLanguage("blue");
        Assert.assertNotNull("Language not found in registry", language);
        Assert.assertTrue("Wrong instance type of the Language", (language instanceof DefaultCamelContextResolverTest.SampleLanguage));
        Assert.assertFalse("Here we should NOT have the fallback Language", ((DefaultCamelContextResolverTest.SampleLanguage) (language)).isFallback());
    }

    @Test
    public void testNullLookupComponent() throws Exception {
        Component component = DefaultCamelContextResolverTest.context.getComponent("xxxxxxxxx");
        Assert.assertNull("Non-existent Component should be null", component);
    }

    @Test
    public void testNullLookupDataFormat() throws Exception {
        DataFormat dataFormat = DefaultCamelContextResolverTest.context.resolveDataFormat("xxxxxxxxx");
        Assert.assertNull("Non-existent DataFormat should be null", dataFormat);
    }

    @Test(expected = NoSuchLanguageException.class)
    public void testNullLookupLanguage() throws Exception {
        DefaultCamelContextResolverTest.context.resolveLanguage("xxxxxxxxx");
    }

    public static class SampleComponent extends DefaultComponent {
        private boolean fallback;

        SampleComponent(boolean fallback) {
            this.fallback = fallback;
        }

        @Override
        protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
            throw new UnsupportedOperationException("Should not be called");
        }

        public boolean isFallback() {
            return fallback;
        }

        public void setFallback(boolean fallback) {
            this.fallback = fallback;
        }
    }

    public static class SampleDataFormat implements DataFormat {
        private boolean fallback;

        SampleDataFormat(boolean fallback) {
            this.fallback = fallback;
        }

        @Override
        public void marshal(Exchange exchange, Object graph, OutputStream stream) throws Exception {
            throw new UnsupportedOperationException("Should not be called");
        }

        @Override
        public Object unmarshal(Exchange exchange, InputStream stream) throws Exception {
            throw new UnsupportedOperationException("Should not be called");
        }

        public boolean isFallback() {
            return fallback;
        }

        public void setFallback(boolean fallback) {
            this.fallback = fallback;
        }
    }

    public static class SampleLanguage implements Language {
        private boolean fallback;

        SampleLanguage(boolean fallback) {
            this.fallback = fallback;
        }

        @Override
        public Predicate createPredicate(String expression) {
            throw new UnsupportedOperationException("Should not be called");
        }

        @Override
        public Expression createExpression(String expression) {
            throw new UnsupportedOperationException("Should not be called");
        }

        public boolean isFallback() {
            return fallback;
        }

        public void setFallback(boolean fallback) {
            this.fallback = fallback;
        }
    }
}

