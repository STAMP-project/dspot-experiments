package org.keycloak.testsuite.console.clients;


import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.testsuite.console.page.clients.credentials.SAMLClientCredentialsForm;


/**
 *
 *
 * @author <a href="mailto:bruno@abstractj.org">Bruno Oliveira</a>
 */
public class ClientSAMLKeysTest extends AbstractClientTest {
    private ClientRepresentation newClient;

    @Page
    private SAMLClientCredentialsForm samlForm;

    @Test
    public void importSAMLKeyPEM() {
        samlForm.importPemCertificateKey();
        Assert.assertEquals("Expected key upload", "Success! Keystore uploaded successfully.", samlForm.getSuccessMessage());
    }

    @Test
    public void importSAMLKeyJKS() {
        samlForm.importJKSKey();
        Assert.assertEquals("Expected key upload", "Success! Keystore uploaded successfully.", samlForm.getSuccessMessage());
    }

    @Test
    public void importSAMLKeyPKCS12() {
        samlForm.importPKCS12Key();
        Assert.assertEquals("Expected key upload", "Success! Keystore uploaded successfully.", samlForm.getSuccessMessage());
    }
}

