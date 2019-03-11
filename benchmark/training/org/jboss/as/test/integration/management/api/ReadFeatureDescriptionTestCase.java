/**
 * Copyright 2018 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.test.integration.management.api;


import PathAddress.EMPTY_ADDRESS;
import java.io.IOException;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.controller.operations.common.Util;
import org.jboss.as.test.integration.management.base.ContainerResourceMgmtTestBase;
import org.jboss.as.test.integration.management.util.MgmtOperationException;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test of read-feature-description handling.
 *
 * @author Brian Stansberry
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ReadFeatureDescriptionTestCase extends ContainerResourceMgmtTestBase {
    @Test
    public void testRecursiveReadFeature() throws IOException, MgmtOperationException {
        ModelNode op = Util.createEmptyOperation(READ_FEATURE_DESCRIPTION_OPERATION, EMPTY_ADDRESS);
        op.get(RECURSIVE).set(true);
        ModelNode result = executeForResult(op);
        int maxDepth = ReadFeatureDescriptionTestCase.validateBaseFeature(result, Integer.MAX_VALUE);
        Assert.assertTrue(result.toString(), (maxDepth > 3));// >3 is a good sign we're recursing all the way

    }
}

