package org.keycloak.procotol.docker.installation;


import Response.Status.OK;
import java.security.cert.Certificate;
import javax.ws.rs.core.Response;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.Test;
import org.keycloak.protocol.docker.installation.DockerComposeYamlInstallationProvider;


public class DockerComposeYamlInstallationProviderTest {
    DockerComposeYamlInstallationProvider installationProvider;

    static Certificate certificate;

    @Test
    public void testAllTheZipThings() throws Exception {
        final Response response = fireInstallationProvider();
        MatcherAssert.assertThat("compose YAML returned non-ok response", response.getStatus(), IsEqual.equalTo(OK.getStatusCode()));
        shouldIncludeDockerComposeYamlInZip(getZipResponseFromInstallProvider(response));
        shouldIncludeReadmeInZip(getZipResponseFromInstallProvider(response));
        shouldWriteBlankDataDirectoryInZip(getZipResponseFromInstallProvider(response));
        shouldWriteCertDirectoryInZip(getZipResponseFromInstallProvider(response));
        shouldWriteSslCertificateInZip(getZipResponseFromInstallProvider(response));
        shouldWritePrivateKeyInZip(getZipResponseFromInstallProvider(response));
    }
}

