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
package org.apache.camel.component.xslt;


import java.util.HashSet;
import java.util.Set;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import org.apache.camel.CamelContext;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.impl.JndiRegistry;
import org.junit.Test;


/**
 *
 */
public class XsltUriResolverFactoryTest extends ContextTestSupport {
    private JndiRegistry registry;

    @Test
    public void testConfigurationOnEndpoint() throws Exception {
        String endpointUri = "xslt:xslt/staff/staff.xsl?uriResolverFactory=#uriResolverFactory";
        String directStart = "direct:start";
        // ensure that the URI resolver factory is not set on the component by
        // the method "testConfigurationOnComponent"
        registry.getContext().unbind("xslt");
        execute(endpointUri, directStart);
    }

    @Test
    public void testConfigurationOnComponent() throws Exception {
        XsltComponent xsltComponent = new XsltComponent();
        xsltComponent.setUriResolverFactory(new XsltUriResolverFactoryTest.CustomXsltUriResolverFactory());
        registry.bind("xslt", xsltComponent);
        String endpointUri = "xslt:xslt/staff/staff.xsl";
        String directStart = "direct:startComponent";
        execute(endpointUri, directStart);
    }

    static class CustomXsltUriResolverFactory implements XsltUriResolverFactory {
        @Override
        public URIResolver createUriResolver(CamelContext camelContext, String resourceUri) {
            return new XsltUriResolverFactoryTest.CustomXsltUriResolver(camelContext, resourceUri);
        }
    }

    static class CustomXsltUriResolver extends XsltUriResolver {
        private final Set<String> resolvedResourceUris = new HashSet<>();

        CustomXsltUriResolver(CamelContext context, String location) {
            super(context, location);
        }

        public Source resolve(String href, String base) throws TransformerException {
            Source result = super.resolve(href, base);
            resolvedResourceUris.add(href);
            return result;
        }
    }
}

