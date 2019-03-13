package org.testcontainers.vault;


import GenericContainer.ExecResult;
import io.restassured.RestAssured;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.Wait;


/**
 * This test shows the pattern to use the VaultContainer @ClassRule for a junit test. It also has tests that ensure
 * the secrets were added correctly by reading from Vault with the CLI and over HTTP.
 */
public class VaultContainerTest {
    private static final int VAULT_PORT = 8201;// using non-default port to show other ports can be passed besides 8200


    private static final String VAULT_TOKEN = "my-root-token";

    @ClassRule
    public static VaultContainer vaultContainer = new VaultContainer().withVaultToken(VaultContainerTest.VAULT_TOKEN).withVaultPort(VaultContainerTest.VAULT_PORT).withSecretInVault("secret/testing1", "top_secret=password123").withSecretInVault("secret/testing2", "secret_one=password1", "secret_two=password2", "secret_three=password3", "secret_three=password3", "secret_four=password4").waitingFor(Wait.forHttp("/v1/secret/testing1").forStatusCode(400));

    @Test
    public void readFirstSecretPathWithCli() throws IOException, InterruptedException {
        GenericContainer.ExecResult result = VaultContainerTest.vaultContainer.execInContainer("vault", "read", "-field=top_secret", "secret/testing1");
        Assert.assertThat(result.getStdout(), CoreMatchers.containsString("password123"));
    }

    @Test
    public void readSecondSecretPathWithCli() throws IOException, InterruptedException {
        GenericContainer.ExecResult result = VaultContainerTest.vaultContainer.execInContainer("vault", "read", "secret/testing2");
        String output = result.getStdout();
        Assert.assertThat(output, CoreMatchers.containsString("password1"));
        Assert.assertThat(output, CoreMatchers.containsString("password2"));
        Assert.assertThat(output, CoreMatchers.containsString("password3"));
        Assert.assertThat(output, CoreMatchers.containsString("password4"));
    }

    @Test
    public void readFirstSecretPathOverHttpApi() throws InterruptedException {
        RestAssured.given().header("X-Vault-Token", VaultContainerTest.VAULT_TOKEN).when().get((("http://" + (getHostAndPort())) + "/v1/secret/testing1")).then().assertThat().body("data.top_secret", CoreMatchers.equalTo("password123"));
    }

    @Test
    public void readSecondecretPathOverHttpApi() throws InterruptedException {
        RestAssured.given().header("X-Vault-Token", VaultContainerTest.VAULT_TOKEN).when().get((("http://" + (getHostAndPort())) + "/v1/secret/testing2")).then().assertThat().body("data.secret_one", CoreMatchers.containsString("password1")).assertThat().body("data.secret_two", CoreMatchers.containsString("password2")).assertThat().body("data.secret_three", CoreMatchers.containsString("password3")).assertThat().body("data.secret_four", CoreMatchers.containsString("password4"));
    }
}

