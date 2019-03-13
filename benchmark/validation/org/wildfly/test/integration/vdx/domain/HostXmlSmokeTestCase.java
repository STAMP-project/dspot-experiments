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
package org.wildfly.test.integration.vdx.domain;


import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.wildfly.extras.creaper.commands.foundation.offline.xml.GroovyXmlTransform;
import org.wildfly.extras.creaper.commands.foundation.offline.xml.Subtree;
import org.wildfly.extras.creaper.core.offline.OfflineCommand;
import org.wildfly.test.integration.vdx.TestBase;
import org.wildfly.test.integration.vdx.category.DomainTests;
import org.wildfly.test.integration.vdx.utils.server.ServerConfig;


/**
 * Created by rsvoboda on 12/15/16.
 */
@RunAsClient
@RunWith(Arquillian.class)
@Category(DomainTests.class)
public class HostXmlSmokeTestCase extends TestBase {
    private final String simpleXmlElement = "unexpectedElement";

    private final String simpleXmlValue = "valueXYZ";

    private final String simpleXml = ((((("<" + (simpleXmlElement)) + ">") + (simpleXmlValue)) + "</") + (simpleXmlElement)) + ">\n";

    @Test
    @ServerConfig(configuration = "host.xml", xmlTransformationGroovy = "host/ManagementAuditLogElement.groovy")
    public void addManagementAuditLogElement() throws Exception {
        container().tryStartAndWaitForFail();
        String errorLog = container().getErrorMessageFromServerStart();
        TestBase.assertContains(errorLog, "^^^^ 'foo' isn't an allowed element here");
        TestBase.assertContains(errorLog, "Elements allowed here are: formatters, handlers, logger, server-logger");
    }

    @Test
    @ServerConfig(configuration = "host.xml", xmlTransformationGroovy = "host/EmptyManagementInterfaces.groovy")
    public void emptyManagementInterfaces() throws Exception {
        container().tryStartAndWaitForFail();
        String errorLog = container().getErrorMessageFromServerStart();
        TestBase.assertContains(errorLog, "^^^^ 'management-interfaces' is missing a required child element");
        TestBase.assertContains(errorLog, "One of the following is required: http-interface, native-interface");
    }

    @Test
    @ServerConfig(configuration = "host.xml")
    public void noManagementElement() throws Exception {
        container().tryStartAndWaitForFail(((OfflineCommand) (( ctx) -> ctx.client.apply(GroovyXmlTransform.of(.class, "RemoveElement.groovy").subtree("path", Subtree.management()).build()))));
        String errorLog = container().getErrorMessageFromServerStart();
        TestBase.assertContains(errorLog, "^^^^ 'domain-controller' is missing one or more required child elements");
        TestBase.assertContains(errorLog, "All of the following are required: management");
        TestBase.assertContains(errorLog, "WFLYCTL0134: Missing required element(s): MANAGEMENT");
    }

    @Test
    @ServerConfig(configuration = "host.xml")
    public void appendElementInJvms() throws Exception {
        String errorLog = addElementAndStart(Subtree.jvms(), simpleXml);
        TestBase.assertContains(errorLog, "Elements allowed here are: jvm");
    }

    @Test
    @ServerConfig(configuration = "host.xml")
    public void appendElementInServers() throws Exception {
        String errorLog = addElementAndStart(Subtree.servers(), simpleXml);
        TestBase.assertContains(errorLog, "Elements allowed here are: server");
    }

    @Test
    @ServerConfig(configuration = "host.xml")
    public void appendElementInDC() throws Exception {
        String errorLog = addElementAndStart(Subtree.domainController(), simpleXml);
        TestBase.assertContains(errorLog, "Elements allowed here are: local, remote");
    }

    @Test
    @ServerConfig(configuration = "host.xml")
    public void appendElementInInterfaces() throws Exception {
        String errorLog = addElementAndStart(Subtree.interfaces(), simpleXml);
        TestBase.assertContains(errorLog, "Elements allowed here are: interface");
    }

    @Test
    @ServerConfig(configuration = "host.xml")
    public void appendElementInManagement() throws Exception {
        String errorLog = addElementAndStart(Subtree.management(), simpleXml);
        TestBase.assertContains(errorLog, "Elements allowed here are:");
        TestBase.assertContains(errorLog, "audit-log");
        TestBase.assertContains(errorLog, "management-interfaces");
        TestBase.assertContains(errorLog, "configuration-changes");
        TestBase.assertContains(errorLog, "outbound-connections");
        TestBase.assertContains(errorLog, "identity");
        TestBase.assertContains(errorLog, "security-realms");
    }

    @Test
    @ServerConfig(configuration = "host.xml")
    public void appendElementInExtensions() throws Exception {
        String errorLog = addElementAndStart(Subtree.extensions(), simpleXml);
        TestBase.assertContains(errorLog, "Elements allowed here are: extension");
    }

    @Test
    @ServerConfig(configuration = "host.xml")
    public void appendElementInProperties() throws Exception {
        String errorLog = addElementAndStart(Subtree.systemProperties(), simpleXml);
        TestBase.assertContains(errorLog, "Elements allowed here are: property");
    }
}

