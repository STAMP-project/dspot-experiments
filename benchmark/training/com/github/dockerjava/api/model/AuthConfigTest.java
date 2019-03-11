package com.github.dockerjava.api.model;


import RemoteApiVersion.VERSION_1_22;
import RemoteApiVersion.VERSION_1_23;
import RemoteApiVersion.VERSION_1_25;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.test.serdes.JSONSamples;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class AuthConfigTest {
    @Test
    public void defaultServerAddress() throws Exception {
        Assert.assertEquals(new AuthConfig().getRegistryAddress(), "https://index.docker.io/v1/");
    }

    @Test
    public void serderDocs1() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final JavaType type = mapper.getTypeFactory().constructType(AuthConfig.class);
        final AuthConfig authConfig = JSONSamples.testRoundTrip(VERSION_1_22, "/other/AuthConfig/docs1.json", type);
        MatcherAssert.assertThat(authConfig, Matchers.notNullValue());
        MatcherAssert.assertThat(authConfig.getUsername(), CoreMatchers.is("jdoe"));
        MatcherAssert.assertThat(authConfig.getPassword(), CoreMatchers.is("secret"));
        MatcherAssert.assertThat(authConfig.getEmail(), CoreMatchers.is("jdoe@acme.com"));
        final AuthConfig authConfig1 = new AuthConfig().withUsername("jdoe").withPassword("secret").withEmail("jdoe@acme.com");
        MatcherAssert.assertThat(authConfig1, CoreMatchers.equalTo(authConfig));
    }

    @Test
    public void serderDocs2() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final JavaType type = mapper.getTypeFactory().constructType(AuthConfig.class);
        final AuthConfig authConfig = JSONSamples.testRoundTrip(VERSION_1_22, "/other/AuthConfig/docs2.json", type);
        MatcherAssert.assertThat(authConfig, Matchers.notNullValue());
        MatcherAssert.assertThat(authConfig.getRegistrytoken(), CoreMatchers.is("9cbaf023786cd7..."));
        final AuthConfig authConfig1 = new AuthConfig().withRegistrytoken("9cbaf023786cd7...");
        MatcherAssert.assertThat(authConfig1, CoreMatchers.equalTo(authConfig));
    }

    @Test
    public void compatibleWithIdentitytoken() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final JavaType type = mapper.getTypeFactory().constructType(AuthConfig.class);
        final AuthConfig authConfig = JSONSamples.testRoundTrip(VERSION_1_23, "/other/AuthConfig/docs1.json", type);
        String auth = "YWRtaW46";
        String identitytoken = "1cba468e-8cbe-4c55-9098-2c2ed769e885";
        MatcherAssert.assertThat(authConfig, Matchers.notNullValue());
        MatcherAssert.assertThat(authConfig.getAuth(), CoreMatchers.is(auth));
        MatcherAssert.assertThat(authConfig.getIdentitytoken(), CoreMatchers.is(identitytoken));
        final AuthConfig authConfig1 = new AuthConfig().withAuth(auth).withIdentityToken(identitytoken);
        MatcherAssert.assertThat(authConfig1, CoreMatchers.equalTo(authConfig));
    }

    @Test
    public void shouldNotFailWithStackOrchestratorInConfig() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final JavaType type = mapper.getTypeFactory().constructType(AuthConfig.class);
        final AuthConfig authConfig = JSONSamples.testRoundTrip(VERSION_1_25, "/other/AuthConfig/orchestrators.json", type);
        MatcherAssert.assertThat(authConfig, Matchers.notNullValue());
        MatcherAssert.assertThat(authConfig.getAuth(), CoreMatchers.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(authConfig.getStackOrchestrator(), CoreMatchers.is("kubernetes"));
    }
}

