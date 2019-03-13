/**
 * Copyright 2017 Red Hat, Inc, and individual contributors.
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


import java.nio.file.Path;
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
 * Tests for missing closing tag in configuration files
 *
 * Created by rsvoboda on 1/20/17.
 */
@RunAsClient
@RunWith(Arquillian.class)
@Category(StandaloneTests.class)
public class MissingClosingTagTestCase extends TestBase {
    private static final Path standaloneXml = Server.CONFIGURATION_PATH.resolve("standalone.xml");

    private static final String STANDALONE_MISSING_CLOSING_TAG_XML = "standalone-missingClosingTag.xml";

    private static final String STANDALONE_COMMENT_IS_NOT_CLOSED_XML = "standalone-commentIsNotClosed.xml";

    private static final String STANDALONE_NOT_EXPECTED_CLOSING_TAG_XML = "standalone-notExpectedClosingTag.xml";

    private static final Path missingClosingTagStandaloneXml = Server.CONFIGURATION_PATH.resolve(MissingClosingTagTestCase.STANDALONE_MISSING_CLOSING_TAG_XML);

    private static final Path commentIsNotClosedStandaloneXml = Server.CONFIGURATION_PATH.resolve(MissingClosingTagTestCase.STANDALONE_COMMENT_IS_NOT_CLOSED_XML);

    private static final Path notExpectedClosingTagStandaloneXml = Server.CONFIGURATION_PATH.resolve(MissingClosingTagTestCase.STANDALONE_NOT_EXPECTED_CLOSING_TAG_XML);

    // + "        </subsystem>";  // <-- missing closing tag
    private static final String missingClosingTag = "        <subsystem xmlns=\"urn:jboss:domain:webservices:2.0\">\n" + (((((((("            <wsdl-host>${jboss.bind.address:127.0.0.1}</wsdl-host>\n" + "            <modify-wsdl-address>true</modify-wsdl-address>\n") + "            <endpoint-config name=\"Standard-Endpoint-Config\"/>\n") + "            <endpoint-config name=\"Recording-Endpoint-Config\">\n") + "                <pre-handler-chain name=\"recording-handlers\" protocol-bindings=\"##SOAP11_HTTP ##SOAP11_HTTP_MTOM ##SOAP12_HTTP ##SOAP12_HTTP_MTOM\">\n") + "                    <handler name=\"RecordingHandler\" class=\"org.jboss.ws.common.invocation.RecordingServerHandler\"/>\n") + "                </pre-handler-chain>\n") + "            </endpoint-config>\n") + "            <client-config name=\"Standard-Client-Config\"/>\n");

    private static final String commentIsNotClosed = "        <subsystem xmlns=\"urn:jboss:domain:webservices:2.0\">\n" + ((((((((("            <wsdl-host>${jboss.bind.address:127.0.0.1}</wsdl-host>\n" + "            <modify-wsdl-address>true</modify-wsdl-address>   <!-- some important comment   \n")// <-- not closed comment
     + "            <endpoint-config name=\"Standard-Endpoint-Config\"/>\n") + "            <endpoint-config name=\"Recording-Endpoint-Config\">\n") + "                <pre-handler-chain name=\"recording-handlers\" protocol-bindings=\"##SOAP11_HTTP ##SOAP11_HTTP_MTOM ##SOAP12_HTTP ##SOAP12_HTTP_MTOM\">\n") + "                    <handler name=\"RecordingHandler\" class=\"org.jboss.ws.common.invocation.RecordingServerHandler\"/>\n") + "                </pre-handler-chain>\n") + "            </endpoint-config>\n") + "            <client-config name=\"Standard-Client-Config\"/>\n") + "        </subsystem>");

    private static final String notExpectedClosingTag = "        <subsystem xmlns=\"urn:jboss:domain:webservices:2.0\" />\n"// <-- closing tag / is here
     + ((((((((("            <wsdl-host>${jboss.bind.address:127.0.0.1}</wsdl-host>\n" + "            <modify-wsdl-address>true</modify-wsdl-address>\n") + "            <endpoint-config name=\"Standard-Endpoint-Config\"/>\n") + "            <endpoint-config name=\"Recording-Endpoint-Config\">\n") + "                <pre-handler-chain name=\"recording-handlers\" protocol-bindings=\"##SOAP11_HTTP ##SOAP11_HTTP_MTOM ##SOAP12_HTTP ##SOAP12_HTTP_MTOM\">\n") + "                    <handler name=\"RecordingHandler\" class=\"org.jboss.ws.common.invocation.RecordingServerHandler\"/>\n") + "                </pre-handler-chain>\n") + "            </endpoint-config>\n") + "            <client-config name=\"Standard-Client-Config\"/>\n") + "        </subsystem>");

    /* There is missing closing tag (for example </subsystem> tag is missing) */
    @Test
    @ServerConfig(configuration = MissingClosingTagTestCase.STANDALONE_MISSING_CLOSING_TAG_XML)
    public void missingClosingTag() throws Exception {
        container().tryStartAndWaitForFail();
        String errorLog = container().getErrorMessageFromServerStart();
        TestBase.assertContains(errorLog, "XMLStreamException:");
        TestBase.assertContains(errorLog, "WFLYCTL0198: Unexpected element '{urn:jboss:domain:weld:");
        TestBase.assertContains(errorLog, "WFLYCTL0085: Failed to parse configuration");
    }

    /* Comments - what happens if there is missing closing --> in <!-- comment here --> */
    @Test
    @ServerConfig(configuration = MissingClosingTagTestCase.STANDALONE_COMMENT_IS_NOT_CLOSED_XML)
    public void commentIsNotClosed() throws Exception {
        container().tryStartAndWaitForFail();
        String errorLog = container().getErrorMessageFromServerStart();
        TestBase.assertContains(errorLog, ("OPVDX001: Validation error in " + (MissingClosingTagTestCase.STANDALONE_COMMENT_IS_NOT_CLOSED_XML)));
        TestBase.assertContains(errorLog, "^^^^ Unexpected end of input block in comment");
        TestBase.assertContains(errorLog, "WFLYCTL0085: Failed to parse configuration");
    }

    /* Not expected closing tag - for example <subsystem ... /> ... </subsystem> */
    @Test
    @ServerConfig(configuration = MissingClosingTagTestCase.STANDALONE_NOT_EXPECTED_CLOSING_TAG_XML)
    public void notExpectedClosingTag() throws Exception {
        container().tryStartAndWaitForFail();
        String errorLog = container().getErrorMessageFromServerStart();
        TestBase.assertContains(errorLog, "OPVDX001: Validation error in standalone-notExpectedClosingTag.xml");
        TestBase.assertContains(errorLog, "^^^^ 'wsdl-host' isn't an allowed element here");
        TestBase.assertContains(errorLog, "WFLYCTL0198: Unexpected element");
    }
}

