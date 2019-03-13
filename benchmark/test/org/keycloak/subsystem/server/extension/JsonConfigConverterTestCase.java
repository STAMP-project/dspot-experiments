/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.subsystem.server.extension;


import KeycloakExtension.PATH_SUBSYSTEM;
import java.util.List;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.PathElement;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2016 Red Hat Inc.
 */
public class JsonConfigConverterTestCase {
    private final PathElement domainRoot = PathElement.pathElement("profile", "auth-server-clustered");

    private final PathAddress domainAddress = PathAddress.pathAddress(domainRoot).append(PATH_SUBSYSTEM);

    private final PathAddress standaloneAddress = PathAddress.pathAddress(PATH_SUBSYSTEM);

    @Test
    public void testConvertJsonStandaloneWithModules() throws Exception {
        String json = basicJsonConfig(true);
        List<ModelNode> expResult = expectedOperations(true, false);
        List<ModelNode> result = JsonConfigConverter.convertJsonConfig(json, standaloneAddress);
        Assert.assertEquals(expResult, result);
    }

    @Test
    public void testConvertJsonStandaloneWithoutModules() throws Exception {
        String json = basicJsonConfig(false);
        List<ModelNode> expResult = expectedOperations(false, false);
        List<ModelNode> result = JsonConfigConverter.convertJsonConfig(json, standaloneAddress);
        Assert.assertEquals(expResult, result);
    }

    @Test
    public void testConvertJsonDomainWithModules() throws Exception {
        String json = basicJsonConfig(true);
        List<ModelNode> expResult = expectedOperations(true, true);
        List<ModelNode> result = JsonConfigConverter.convertJsonConfig(json, domainAddress);
        Assert.assertEquals(expResult, result);
    }

    @Test
    public void testConvertJsonDomainWithoutModules() throws Exception {
        String json = basicJsonConfig(false);
        List<ModelNode> expResult = expectedOperations(false, true);
        List<ModelNode> result = JsonConfigConverter.convertJsonConfig(json, domainAddress);
        Assert.assertEquals(expResult, result);
    }
}

