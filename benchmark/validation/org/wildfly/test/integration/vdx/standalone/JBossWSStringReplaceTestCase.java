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
 * Created by rsvoboda on 12/14/16.
 */
@RunAsClient
@RunWith(Arquillian.class)
@Category(StandaloneTests.class)
public class JBossWSStringReplaceTestCase extends TestBase {
    private static final Path standaloneXml = Server.CONFIGURATION_PATH.resolve("standalone.xml");

    private static final Path patchedStandaloneXml = Server.CONFIGURATION_PATH.resolve("standalone-ws-broken.xml");

    private static final String brokenWSSubsystemDefinition = "        <subsystem xmlns=\"urn:jboss:domain:webservices:2.0\">\n" + ((((((((("            <wsdl-host>${jboss.bind.address:127.0.0.1}</wsdl-host>\n" + "            <mmodify-wsdl-address>true</modify-wsdl-address>\n") + "            <endpoint-config name=\"Standard-Endpoint-Config\"/>\n") + "            <endpoint-config name=\"Recording-Endpoint-Config\">\n") + "                <pre-handler-chain name=\"recording-handlers\" protocol-bindings=\"##SOAP11_HTTP ##SOAP11_HTTP_MTOM ##SOAP12_HTTP ##SOAP12_HTTP_MTOM\">\n") + "                    <handler name=\"RecordingHandler\" class=\"org.jboss.ws.common.invocation.RecordingServerHandler\"/>\n") + "                </pre-handler-chain>\n") + "            </endpoint-config>\n") + "            <client-config name=\"Standard-Client-Config\"/>\n") + "        </subsystem>");

    /* <mmodify-wsdl-address>true</modify-wsdl-address> instead of <modify-wsdl-address>true</modify-wsdl-address> */
    @Test
    @ServerConfig(configuration = "standalone-ws-broken.xml")
    public void incorrectValueOfModifyWsdlAddressOpeningElement() throws Exception {
        container().tryStartAndWaitForFail();
        String errorLog = container().getErrorMessageFromServerStart();
        TestBase.assertContains(errorLog, "OPVDX001: Validation error in standalone-ws-broken.xml");
        TestBase.assertContains(errorLog, "<mmodify-wsdl-address>true</modify-wsdl-address>");
        TestBase.assertContains(errorLog, "^^^^ 'mmodify-wsdl-address' isn't an allowed element here");
        TestBase.assertContains(errorLog, "matching end-tag \"</mmodify-wsdl-address>");
    }
}

