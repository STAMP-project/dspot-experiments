/**
 * Copyright 2016 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  *
 * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wildfly.test.integration.vdx.standalone;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.wildfly.test.integration.vdx.TestBase;
import org.wildfly.test.integration.vdx.category.StandaloneTests;
import org.wildfly.test.integration.vdx.utils.server.Server;
import org.wildfly.test.integration.vdx.utils.server.ServerConfig;


/**
 * Tests to check behavior when $JBOSS_HOME/docs/schema is not available
 *
 * Created by rsvoboda on 11/30/16.
 */
@RunAsClient
@RunWith(Arquillian.class)
@Category(StandaloneTests.class)
public class NoSchemaTestCase extends TestBase {
    private static final Path docs = Paths.get(Server.JBOSS_HOME, "docs");

    private static final Path docsx = Paths.get(Server.JBOSS_HOME, "docsx");

    /* remove $JBOSS_HOME/docs and use <modify-wsdl-address /> instead of <modify-wsdl-address>true</modify-wsdl-address>
    only stacktrace based error messages are available
     */
    @Test
    @ServerConfig(configuration = "standalone.xml", xmlTransformationGroovy = "webservices/AddModifyWsdlAddressElementWithNoValue.groovy", subtreeName = "webservices", subsystemName = "webservices")
    public void addWsdlAddressElementWithNoValueNoSchemaAvailable() throws Exception {
        container().tryStartAndWaitForFail();
        String errorLog = container().getErrorMessageFromServerStart();
        TestBase.assertDoesNotContain(errorLog, "OPVDX001: Validation error in standalone.xml");
        TestBase.assertDoesNotContain(errorLog, "<modify-wsdl-address/>");
        TestBase.assertDoesNotContain(errorLog, " ^^^^ Wrong type for 'modify-wsdl-address'. Expected [BOOLEAN] but was");
        TestBase.assertDoesNotContain(errorLog, "|                  STRING");
        TestBase.assertContains(errorLog, "WFLYCTL0097");
        TestBase.assertContains(errorLog, "Wrong type for 'modify-wsdl-address'. Expected [BOOLEAN] but was STRING");
    }

    /* ensure something like '22:35:00,342 INFO  [org.jboss.as.controller] (Controller Boot Thread) OPVDX003: No schemas available
      from /path/to/server/docs/schema - disabling validation error pretty printing' is available in the log
     */
    @Test
    @ServerConfig(configuration = "standalone.xml", xmlTransformationGroovy = "webservices/AddModifyWsdlAddressElementWithNoValue.groovy", subtreeName = "webservices", subsystemName = "webservices")
    public void ensureNoSchemasAvailableMessage() throws Exception {
        container().tryStartAndWaitForFail();
        String serverLog = String.join("\n", Files.readAllLines(container().getServerLogPath()));
        TestBase.assertContains(serverLog, "OPVDX003: No schemas available");
        TestBase.assertContains(serverLog, "disabling validation error pretty printing");
    }
}

