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
package org.apache.camel.component.file;


import Exchange.FILE_NAME;
import java.io.File;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;

import static FileComponent.DEFAULT_LOCK_FILE_POSTFIX;


public class FileRenameReadLockMustUseMarkerFileTest extends ContextTestSupport {
    @Test
    public void testCamelLockFile() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        mock.expectedBodiesReceived("Bye World");
        mock.message(0).header(FILE_NAME).isEqualTo("bye.txt");
        template.sendBodyAndHeader("file:target/data/rename", "Bye World", FILE_NAME, "bye.txt");
        // start the route
        context.getRouteController().startRoute("foo");
        assertMockEndpointsSatisfied();
        Assert.assertTrue(oneExchangeDone.matchesMockWaitTime());
        // and lock file should be deleted
        File lock = new File(("target/data/rename/bye.txt" + (DEFAULT_LOCK_FILE_POSTFIX)));
        Assert.assertFalse(("Lock file should not exist: " + lock), lock.exists());
    }
}

