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
package org.apache.camel.component.validator;


import java.util.HashSet;
import java.util.Set;
import org.apache.camel.CamelContext;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.impl.JndiRegistry;
import org.junit.Test;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;


public class ValidatorResourceResolverFactoryTest extends ContextTestSupport {
    private JndiRegistry registry;

    @Test
    public void testConfigurationOnEndpoint() throws Exception {
        // ensure that validator from test method "testConfigurationOnComponent"
        // is unbind
        registry.getContext().unbind("validator");
        String directStart = "direct:start";
        String endpointUri = "validator:org/apache/camel/component/validator/xsds/person.xsd?resourceResolverFactory=#resourceResolverFactory";
        execute(directStart, endpointUri);
    }

    @Test
    public void testConfigurationOnComponent() throws Exception {
        // set resource resolver factory on component
        ValidatorComponent validatorComponent = new ValidatorComponent();
        validatorComponent.setResourceResolverFactory(new ValidatorResourceResolverFactoryTest.ResourceResolverFactoryImpl());
        registry.bind("validator", validatorComponent);
        String directStart = "direct:startComponent";
        String endpointUri = "validator:org/apache/camel/component/validator/xsds/person.xsd";
        execute(directStart, endpointUri);
    }

    static class ResourceResolverFactoryImpl implements ValidatorResourceResolverFactory {
        @Override
        public LSResourceResolver createResourceResolver(CamelContext camelContext, String rootResourceUri) {
            return new ValidatorResourceResolverFactoryTest.CustomResourceResolver(camelContext, rootResourceUri);
        }
    }

    /**
     * Custom resource resolver which collects all resolved resource URIs.
     */
    static class CustomResourceResolver extends DefaultLSResourceResolver {
        private final Set<String> resolvedRsourceUris = new HashSet<>();

        CustomResourceResolver(CamelContext camelContext, String resourceUri) {
            super(camelContext, resourceUri);
        }

        public Set<String> getResolvedResourceUris() {
            return resolvedRsourceUris;
        }

        @Override
        public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
            LSInput result = super.resolveResource(type, namespaceURI, publicId, systemId, baseURI);
            resolvedRsourceUris.add(systemId);
            return result;
        }
    }
}

