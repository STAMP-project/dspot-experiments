package org.keycloak.testsuite.cli.admin;


import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.client.admin.cli.config.FileConfigHandler;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.testsuite.cli.AbstractCliTest;
import org.keycloak.testsuite.cli.KcAdmExec;
import org.keycloak.testsuite.util.TempFileResource;
import org.keycloak.util.JsonSerialization;


/**
 *
 *
 * @author <a href="mailto:mstrukel@redhat.com">Marko Strukelj</a>
 */
public class KcAdmCreateTest extends AbstractAdmCliTest {
    @Test
    public void testCreateWithRealmOverride() throws IOException {
        FileConfigHandler handler = initCustomConfigFile();
        try (TempFileResource configFile = new TempFileResource(handler.getConfigFile())) {
            // authenticate as a regular user against one realm
            KcAdmExec exe = KcAdmExec.execute((((("config credentials -x --config '" + (configFile.getName())) + "' --server ") + (serverUrl)) + " --realm master --user admin --password admin"));
            assertExitCodeAndStreamSizes(exe, 0, 0, 1);
            exe = KcAdmExec.execute((((("create clients --config '" + (configFile.getName())) + "' --server ") + (serverUrl)) + " -r test -s clientId=my_first_client"));
            assertExitCodeAndStreamSizes(exe, 0, 0, 1);
        }
    }

    @Test
    public void testCreateThoroughly() throws IOException {
        FileConfigHandler handler = initCustomConfigFile();
        try (TempFileResource configFile = new TempFileResource(handler.getConfigFile())) {
            final String realm = "test";
            // authenticate as a regular user against one realm
            KcAdmExec exe = KcAdmExec.execute((((("config credentials -x --config '" + (configFile.getName())) + "' --server ") + (serverUrl)) + " --realm master --user admin --password admin"));
            assertExitCodeAndStreamSizes(exe, 0, 0, 1);
            // create configuration from file using stdin redirect ... output an object
            String content = "{\n" + (((((((((((("        \"clientId\": \"my_client\",\n" + "        \"enabled\": true,\n") + "        \"redirectUris\": [\"http://localhost:8980/myapp/*\"],\n") + "        \"serviceAccountsEnabled\": true,\n") + "        \"name\": \"My Client App\",\n") + "        \"implicitFlowEnabled\": false,\n") + "        \"publicClient\": true,\n") + "        \"webOrigins\": [\"http://localhost:8980/myapp\"],\n") + "        \"consentRequired\": false,\n") + "        \"baseUrl\": \"http://localhost:8980/myapp\",\n") + "        \"bearerOnly\": true,\n") + "        \"standardFlowEnabled\": true\n") + "}");
            try (TempFileResource tmpFile = new TempFileResource(initTempFile(".json", content))) {
                exe = KcAdmExec.execute((((("create clients --config '" + (configFile.getName())) + "' -o -f - < '") + (tmpFile.getName())) + "'"));
                assertExitCodeAndStdErrSize(exe, 0, 0);
                ClientRepresentation client = JsonSerialization.readValue(exe.stdout(), ClientRepresentation.class);
                Assert.assertNotNull("id", client.getId());
                Assert.assertEquals("clientId", "my_client", client.getClientId());
                Assert.assertEquals("enabled", true, client.isEnabled());
                Assert.assertEquals("redirectUris", Arrays.asList("http://localhost:8980/myapp/*"), client.getRedirectUris());
                Assert.assertEquals("serviceAccountsEnabled", true, client.isServiceAccountsEnabled());
                Assert.assertEquals("name", "My Client App", client.getName());
                Assert.assertEquals("implicitFlowEnabled", false, client.isImplicitFlowEnabled());
                Assert.assertEquals("publicClient", true, client.isPublicClient());
                // note there is no server-side check if protocol is supported
                Assert.assertEquals("webOrigins", Arrays.asList("http://localhost:8980/myapp"), client.getWebOrigins());
                Assert.assertEquals("consentRequired", false, client.isConsentRequired());
                Assert.assertEquals("baseUrl", "http://localhost:8980/myapp", client.getBaseUrl());
                Assert.assertEquals("bearerOnly", true, client.isStandardFlowEnabled());
                Assert.assertFalse("mappers not empty", client.getProtocolMappers().isEmpty());
                // create configuration from file as a template and override clientId and other attributes ... output an object
                exe = KcAdmExec.execute((((((("create clients --config '" + (configFile.getName())) + "' -o -f '") + (tmpFile.getName())) + "\' -s clientId=my_client2 -s enabled=false -s \'redirectUris=[\"http://localhost:8980/myapp2/*\"]\'") + " -s \'name=My Client App II\' -s \'webOrigins=[\"http://localhost:8980/myapp2\"]\'") + " -s baseUrl=http://localhost:8980/myapp2 -s rootUrl=http://localhost:8980/myapp2"));
                assertExitCodeAndStdErrSize(exe, 0, 0);
                ClientRepresentation client2 = JsonSerialization.readValue(exe.stdout(), ClientRepresentation.class);
                Assert.assertNotNull("id", client2.getId());
                Assert.assertEquals("clientId", "my_client2", client2.getClientId());
                Assert.assertEquals("enabled", false, client2.isEnabled());
                Assert.assertEquals("redirectUris", Arrays.asList("http://localhost:8980/myapp2/*"), client2.getRedirectUris());
                Assert.assertEquals("serviceAccountsEnabled", true, client2.isServiceAccountsEnabled());
                Assert.assertEquals("name", "My Client App II", client2.getName());
                Assert.assertEquals("implicitFlowEnabled", false, client2.isImplicitFlowEnabled());
                Assert.assertEquals("publicClient", true, client2.isPublicClient());
                Assert.assertEquals("webOrigins", Arrays.asList("http://localhost:8980/myapp2"), client2.getWebOrigins());
                Assert.assertEquals("consentRequired", false, client2.isConsentRequired());
                Assert.assertEquals("baseUrl", "http://localhost:8980/myapp2", client2.getBaseUrl());
                Assert.assertEquals("rootUrl", "http://localhost:8980/myapp2", client2.getRootUrl());
                Assert.assertEquals("bearerOnly", true, client2.isStandardFlowEnabled());
                Assert.assertFalse("mappers not empty", client2.getProtocolMappers().isEmpty());
            }
            // simple create, output an id
            exe = KcAdmExec.execute((("create clients --config '" + (configFile.getName())) + "' -i -s clientId=my_client3"));
            assertExitCodeAndStreamSizes(exe, 0, 1, 0);
            // simple create, default output
            exe = KcAdmExec.execute((("create clients --config '" + (configFile.getName())) + "' -s clientId=my_client4"));
            assertExitCodeAndStreamSizes(exe, 0, 0, 1);
            Assert.assertTrue("only id returned", exe.stderrLines().get(0).startsWith("Created new client with id '"));
        }
    }
}

