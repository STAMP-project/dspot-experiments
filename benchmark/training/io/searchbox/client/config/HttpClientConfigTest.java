package io.searchbox.client.config;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author cihat keser
 */
public class HttpClientConfigTest {
    @Test
    public void defaultInstances() {
        HttpClientConfig httpClientConfig = new HttpClientConfig.Builder("localhost").build();
        Assert.assertNotNull(httpClientConfig.getMaxTotalConnectionPerRoute());
        Assert.assertNotNull(httpClientConfig.getSslSocketFactory());
        Assert.assertNotNull(httpClientConfig.getPlainSocketFactory());
        Assert.assertNotNull(httpClientConfig.getHttpRoutePlanner());
    }

    @Test
    public void defaultCredentials() {
        String user = "ceo";
        String password = "12345";
        HttpClientConfig httpClientConfig = new HttpClientConfig.Builder("localhost").defaultCredentials(user, password).build();
        CredentialsProvider credentialsProvider = httpClientConfig.getCredentialsProvider();
        Credentials credentials = credentialsProvider.getCredentials(new AuthScope("localhost", 80));
        Assert.assertEquals(user, credentials.getUserPrincipal().getName());
        Assert.assertEquals(password, credentials.getPassword());
    }

    @Test
    public void customCredentialProvider() {
        CredentialsProvider customCredentialsProvider = new BasicCredentialsProvider();
        HttpClientConfig httpClientConfig = new HttpClientConfig.Builder("localhost").credentialsProvider(customCredentialsProvider).build();
        Assert.assertEquals(customCredentialsProvider, httpClientConfig.getCredentialsProvider());
    }

    @Test
    public void preemptiveAuth() {
        String hostName = "targetHost";
        int port = 80;
        HttpHost targetHost = new HttpHost(hostName, port, "http");
        HttpClientConfig httpClientConfig = new HttpClientConfig.Builder("localhost").defaultCredentials("someUser", "somePassword").setPreemptiveAuth(targetHost).build();
        Assert.assertThat(httpClientConfig.getPreemptiveAuthTargetHosts(), CoreMatchers.hasItem(targetHost));
    }

    @Test
    public void preemptiveAuthWithMultipleTargetHosts() {
        final Set<HttpHost> targetHosts = new HashSet<HttpHost>(Arrays.asList(new HttpHost("host1", 80, "http"), new HttpHost("host2", 81, "https")));
        HttpClientConfig httpClientConfig = new HttpClientConfig.Builder("localhost").defaultCredentials("someUser", "somePassword").preemptiveAuthTargetHosts(new HashSet<HttpHost>(targetHosts)).build();
        Assert.assertThat(httpClientConfig.getPreemptiveAuthTargetHosts(), CoreMatchers.is(targetHosts));
    }

    @Test(expected = IllegalArgumentException.class)
    public void preemptiveAuthWithoutCredentials() {
        new HttpClientConfig.Builder("localhost").setPreemptiveAuth(new HttpHost("localhost", 80, "http")).build();
        Assert.fail("Builder should have thrown an exception if preemptive authentication is set without setting credentials");
    }
}

