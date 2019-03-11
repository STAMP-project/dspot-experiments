/**
 * Copyright 2015 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud;


import com.google.api.client.http.HttpResponse;
import com.google.api.core.ApiClock;
import com.google.api.core.CurrentMillisClock;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.spi.ServiceRpcFactory;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.Files;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.regex.Pattern;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class ServiceOptionsTest {
    private static final String JSON_KEY = "{\n" + (((((((((((((((((((((((("  \"private_key_id\": \"somekeyid\",\n" + "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggS") + "kAgEAAoIBAQC+K2hSuFpAdrJI\\nnCgcDz2M7t7bjdlsadsasad+fvRSW6TjNQZ3p5LLQY1kSZRqBqylRkzteMOyHg") + "aR\\n0Pmxh3ILCND5men43j3h4eDbrhQBuxfEMalkG92sL+PNQSETY2tnvXryOvmBRwa/\\nQP/9dJfIkIDJ9Fw9N4") + "Bhhhp6mCcRpdQjV38H7JsyJ7lih/oNjECgYAt\\nknddadwkwewcVxHFhcZJO+XWf6ofLUXpRwiTZakGMn8EE1uVa2") + "LgczOjwWHGi99MFjxSer5m9\\n1tCa3/KEGKiS/YL71JvjwX3mb+cewlkcmweBKZHM2JPTk0ZednFSpVZMtycjkbLa") + "\\ndYOS8V85AgMBewECggEBAKksaldajfDZDV6nGqbFjMiizAKJolr/M3OQw16K6o3/\\n0S31xIe3sSlgW0+UbYlF") + "4U8KifhManD1apVSC3csafaspP4RZUHFhtBywLO9pR5c\\nr6S5aLp+gPWFyIp1pfXbWGvc5VY/v9x7ya1VEa6rXvL") + "sKupSeWAW4tMj3eo/64ge\\nsdaceaLYw52KeBYiT6+vpsnYrEkAHO1fF/LavbLLOFJmFTMxmsNaG0tuiJHgjshB\\") + "n82DpMCbXG9YcCgI/DbzuIjsdj2JC1cascSP//3PmefWysucBQe7Jryb6NQtASmnv\\nCdDw/0jmZTEjpe4S1lxfHp") + "lAhHFtdgYTvyYtaLZiVVkCgYEA8eVpof2rceecw/I6\\n5ng1q3Hl2usdWV/4mZMvR0fOemacLLfocX6IYxT1zA1FF") + "JlbXSRsJMf/Qq39mOR2\\nSpW+hr4jCoHeRVYLgsbggtrevGmILAlNoqCMpGZ6vDmJpq6ECV9olliDvpPgWOP+\\nm") + "YPDreFBGxWvQrADNbRt2dmGsrsCgYEAyUHqB2wvJHFqdmeBsaacewzV8x9WgmeX\\ngUIi9REwXlGDW0Mz50dxpxcK") + "CAYn65+7TCnY5O/jmL0VRxU1J2mSWyWTo1C+17L0\\n3fUqjxL1pkefwecxwecvC+gFFYdJ4CQ/MHHXU81Lwl1iWdF") + "Cd2UoGddYaOF+KNeM\\nHC7cmqra+JsCgYEAlUNywzq8nUg7282E+uICfCB0LfwejuymR93CtsFgb7cRd6ak\\nECR") + "8FGfCpH8ruWJINllbQfcHVCX47ndLZwqv3oVFKh6pAS/vVI4dpOepP8++7y1u\\ncoOvtreXCX6XqfrWDtKIvv0vjl") + "HBhhhp6mCcRpdQjV38H7JsyJ7lih/oNjECgYAt\\nkndj5uNl5SiuVxHFhcZJO+XWf6ofLUregtevZakGMn8EE1uVa") + "2AY7eafmoU/nZPT\\n00YB0TBATdCbn/nBSuKDESkhSg9s2GEKQZG5hBmL5uCMfo09z3SfxZIhJdlerreP\\nJ7gSi") + "dI12N+EZxYd4xIJh/HFDgp7RRO87f+WJkofMQKBgGTnClK1VMaCRbJZPriw\\nEfeFCoOX75MxKwXs6xgrw4W//AYG") + "GUjDt83lD6AZP6tws7gJ2IwY/qP7+lyhjEqN\\nHtfPZRGFkGZsdaksdlaksd323423d+15/UvrlRSFPNj1tWQmNKk") + "XyRDW4IG1Oa2p\\nrALStNBx5Y9t0/LQnFI4w3aG\\n-----END PRIVATE KEY-----\\n\",\n") + "  \"client_email\": \"someclientid@developer.gserviceaccount.com\",\n") + "  \"client_id\": \"someclientid.apps.googleusercontent.com\",\n") + "  \"type\": \"service_account\"\n") + "}");

    private static GoogleCredentials credentials;

    static {
        try {
            InputStream keyStream = new ByteArrayInputStream(ServiceOptionsTest.JSON_KEY.getBytes());
            ServiceOptionsTest.credentials = GoogleCredentials.fromStream(keyStream);
        } catch (IOException e) {
            Assert.fail("Couldn't create fake JSON credentials.");
        }
    }

    private static final String JSON_KEY_PROJECT_ID = "{\n" + ((((((((((((((((((((((((("  \"private_key_id\": \"somekeyid\",\n" + "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggS") + "kAgEAAoIBAQC+K2hSuFpAdrJI\\nnCgcDz2M7t7bjdlsadsasad+fvRSW6TjNQZ3p5LLQY1kSZRqBqylRkzteMOyHg") + "aR\\n0Pmxh3ILCND5men43j3h4eDbrhQBuxfEMalkG92sL+PNQSETY2tnvXryOvmBRwa/\\nQP/9dJfIkIDJ9Fw9N4") + "Bhhhp6mCcRpdQjV38H7JsyJ7lih/oNjECgYAt\\nknddadwkwewcVxHFhcZJO+XWf6ofLUXpRwiTZakGMn8EE1uVa2") + "LgczOjwWHGi99MFjxSer5m9\\n1tCa3/KEGKiS/YL71JvjwX3mb+cewlkcmweBKZHM2JPTk0ZednFSpVZMtycjkbLa") + "\\ndYOS8V85AgMBewECggEBAKksaldajfDZDV6nGqbFjMiizAKJolr/M3OQw16K6o3/\\n0S31xIe3sSlgW0+UbYlF") + "4U8KifhManD1apVSC3csafaspP4RZUHFhtBywLO9pR5c\\nr6S5aLp+gPWFyIp1pfXbWGvc5VY/v9x7ya1VEa6rXvL") + "sKupSeWAW4tMj3eo/64ge\\nsdaceaLYw52KeBYiT6+vpsnYrEkAHO1fF/LavbLLOFJmFTMxmsNaG0tuiJHgjshB\\") + "n82DpMCbXG9YcCgI/DbzuIjsdj2JC1cascSP//3PmefWysucBQe7Jryb6NQtASmnv\\nCdDw/0jmZTEjpe4S1lxfHp") + "lAhHFtdgYTvyYtaLZiVVkCgYEA8eVpof2rceecw/I6\\n5ng1q3Hl2usdWV/4mZMvR0fOemacLLfocX6IYxT1zA1FF") + "JlbXSRsJMf/Qq39mOR2\\nSpW+hr4jCoHeRVYLgsbggtrevGmILAlNoqCMpGZ6vDmJpq6ECV9olliDvpPgWOP+\\nm") + "YPDreFBGxWvQrADNbRt2dmGsrsCgYEAyUHqB2wvJHFqdmeBsaacewzV8x9WgmeX\\ngUIi9REwXlGDW0Mz50dxpxcK") + "CAYn65+7TCnY5O/jmL0VRxU1J2mSWyWTo1C+17L0\\n3fUqjxL1pkefwecxwecvC+gFFYdJ4CQ/MHHXU81Lwl1iWdF") + "Cd2UoGddYaOF+KNeM\\nHC7cmqra+JsCgYEAlUNywzq8nUg7282E+uICfCB0LfwejuymR93CtsFgb7cRd6ak\\nECR") + "8FGfCpH8ruWJINllbQfcHVCX47ndLZwqv3oVFKh6pAS/vVI4dpOepP8++7y1u\\ncoOvtreXCX6XqfrWDtKIvv0vjl") + "HBhhhp6mCcRpdQjV38H7JsyJ7lih/oNjECgYAt\\nkndj5uNl5SiuVxHFhcZJO+XWf6ofLUregtevZakGMn8EE1uVa") + "2AY7eafmoU/nZPT\\n00YB0TBATdCbn/nBSuKDESkhSg9s2GEKQZG5hBmL5uCMfo09z3SfxZIhJdlerreP\\nJ7gSi") + "dI12N+EZxYd4xIJh/HFDgp7RRO87f+WJkofMQKBgGTnClK1VMaCRbJZPriw\\nEfeFCoOX75MxKwXs6xgrw4W//AYG") + "GUjDt83lD6AZP6tws7gJ2IwY/qP7+lyhjEqN\\nHtfPZRGFkGZsdaksdlaksd323423d+15/UvrlRSFPNj1tWQmNKk") + "XyRDW4IG1Oa2p\\nrALStNBx5Y9t0/LQnFI4w3aG\\n-----END PRIVATE KEY-----\\n\",\n") + "  \"project_id\": \"someprojectid\",\n") + "  \"client_email\": \"someclientid@developer.gserviceaccount.com\",\n") + "  \"client_id\": \"someclientid.apps.googleusercontent.com\",\n") + "  \"type\": \"service_account\"\n") + "}");

    private static GoogleCredentials credentialsWithProjectId;

    static {
        try {
            InputStream keyStream = new ByteArrayInputStream(ServiceOptionsTest.JSON_KEY_PROJECT_ID.getBytes());
            ServiceOptionsTest.credentialsWithProjectId = GoogleCredentials.fromStream(keyStream);
        } catch (IOException e) {
            Assert.fail("Couldn't create fake JSON credentials.");
        }
    }

    private static final ApiClock TEST_CLOCK = new ServiceOptionsTest.TestClock();

    private static final ServiceOptionsTest.TestServiceOptions OPTIONS = setProjectId("project-id").setRetrySettings(ServiceOptions.getNoRetrySettings()).build();

    private static final ServiceOptionsTest.TestServiceOptions OPTIONS_NO_CREDENTIALS = setProjectId("project-id").setRetrySettings(ServiceOptions.getNoRetrySettings()).build();

    private static final ServiceOptionsTest.TestServiceOptions DEFAULT_OPTIONS = setProjectId("project-id").build();

    private static final ServiceOptionsTest.TestServiceOptions OPTIONS_COPY = ServiceOptionsTest.OPTIONS.toBuilder().build();

    private static final String LIBRARY_NAME = "gcloud-java";

    private static final Pattern APPLICATION_NAME_PATTERN = Pattern.compile(((ServiceOptionsTest.LIBRARY_NAME) + "/.*"));

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static class TestClock implements ApiClock {
        @Override
        public long nanoTime() {
            return 123456789000000L;
        }

        @Override
        public long millisTime() {
            return 123456789L;
        }
    }

    interface TestService extends Service<ServiceOptionsTest.TestServiceOptions> {}

    private static class TestServiceImpl extends BaseService<ServiceOptionsTest.TestServiceOptions> implements ServiceOptionsTest.TestService {
        private TestServiceImpl(ServiceOptionsTest.TestServiceOptions options) {
            super(options);
        }
    }

    public interface TestServiceFactory extends ServiceFactory<ServiceOptionsTest.TestService, ServiceOptionsTest.TestServiceOptions> {}

    private static class DefaultTestServiceFactory implements ServiceOptionsTest.TestServiceFactory {
        private static final ServiceOptionsTest.TestServiceFactory INSTANCE = new ServiceOptionsTest.DefaultTestServiceFactory();

        @Override
        public ServiceOptionsTest.TestService create(ServiceOptionsTest.TestServiceOptions options) {
            return new ServiceOptionsTest.TestServiceImpl(options);
        }
    }

    public interface TestServiceRpcFactory extends ServiceRpcFactory<ServiceOptionsTest.TestServiceOptions> {}

    private static class DefaultTestServiceRpcFactory implements ServiceOptionsTest.TestServiceRpcFactory {
        private static final ServiceOptionsTest.TestServiceRpcFactory INSTANCE = new ServiceOptionsTest.DefaultTestServiceRpcFactory();

        @Override
        public ServiceOptionsTest.TestServiceRpc create(ServiceOptionsTest.TestServiceOptions options) {
            return new ServiceOptionsTest.DefaultTestServiceRpc(options);
        }
    }

    private interface TestServiceRpc extends ServiceRpc {}

    private static class DefaultTestServiceRpc implements ServiceOptionsTest.TestServiceRpc {
        DefaultTestServiceRpc(ServiceOptionsTest.TestServiceOptions options) {
        }
    }

    static class TestServiceOptions extends ServiceOptions<ServiceOptionsTest.TestService, ServiceOptionsTest.TestServiceOptions> {
        private static class Builder extends ServiceOptions.Builder<ServiceOptionsTest.TestService, ServiceOptionsTest.TestServiceOptions, ServiceOptionsTest.TestServiceOptions.Builder> {
            private Builder() {
            }

            private Builder(ServiceOptionsTest.TestServiceOptions options) {
                super(options);
            }

            @Override
            protected ServiceOptionsTest.TestServiceOptions build() {
                return new ServiceOptionsTest.TestServiceOptions(this);
            }
        }

        private TestServiceOptions(ServiceOptionsTest.TestServiceOptions.Builder builder) {
            super(ServiceOptionsTest.TestServiceFactory.class, ServiceOptionsTest.TestServiceRpcFactory.class, builder, new ServiceOptionsTest.TestServiceOptions.TestServiceDefaults());
        }

        private static class TestServiceDefaults implements ServiceDefaults<ServiceOptionsTest.TestService, ServiceOptionsTest.TestServiceOptions> {
            @Override
            public ServiceOptionsTest.TestServiceFactory getDefaultServiceFactory() {
                return ServiceOptionsTest.DefaultTestServiceFactory.INSTANCE;
            }

            @Override
            public ServiceOptionsTest.TestServiceRpcFactory getDefaultRpcFactory() {
                return ServiceOptionsTest.DefaultTestServiceRpcFactory.INSTANCE;
            }

            @Override
            public TransportOptions getDefaultTransportOptions() {
                return new TransportOptions() {};
            }
        }

        @Override
        protected Set<String> getScopes() {
            return null;
        }

        @Override
        public ServiceOptionsTest.TestServiceOptions.Builder toBuilder() {
            return new ServiceOptionsTest.TestServiceOptions.Builder(this);
        }

        private static ServiceOptionsTest.TestServiceOptions.Builder newBuilder() {
            return new ServiceOptionsTest.TestServiceOptions.Builder();
        }

        @Override
        public boolean equals(Object obj) {
            return (obj instanceof ServiceOptionsTest.TestServiceOptions) && (baseEquals(((ServiceOptionsTest.TestServiceOptions) (obj))));
        }

        @Override
        public int hashCode() {
            return baseHashCode();
        }
    }

    @Test
    public void testBuilder() {
        Assert.assertSame(ServiceOptionsTest.credentials, getCredentials());
        Assert.assertSame(ServiceOptionsTest.TEST_CLOCK, getClock());
        Assert.assertEquals("host", getHost());
        Assert.assertEquals("project-id", getProjectId());
        Assert.assertSame(ServiceOptions.getNoRetrySettings(), getRetrySettings());
        Assert.assertSame(CurrentMillisClock.getDefaultClock(), getClock());
        Assert.assertEquals("https://www.googleapis.com", getHost());
        Assert.assertSame(ServiceOptions.getDefaultRetrySettings(), getRetrySettings());
    }

    @Test
    public void testBuilderNoCredentials() {
        Assert.assertEquals(NoCredentials.getInstance(), getCredentials());
        Assert.assertTrue(NoCredentials.getInstance().equals(getCredentials()));
        TestCase.assertFalse(NoCredentials.getInstance().equals(getCredentials()));
        TestCase.assertFalse(NoCredentials.getInstance().equals(null));
        Assert.assertSame(ServiceOptionsTest.TEST_CLOCK, getClock());
        Assert.assertEquals("host", getHost());
        Assert.assertEquals("project-id", getProjectId());
        Assert.assertSame(ServiceOptions.getNoRetrySettings(), getRetrySettings());
    }

    @Test
    public void testBuilderNullCredentials() {
        thrown.expect(NullPointerException.class);
        setCredentials(null).build();
    }

    @Test
    public void testBuilderServiceAccount_setsProjectId() {
        ServiceOptionsTest.TestServiceOptions options = ServiceOptionsTest.TestServiceOptions.newBuilder().setCredentials(ServiceOptionsTest.credentialsWithProjectId).build();
        Assert.assertEquals("someprojectid", getProjectId());
    }

    @Test
    public void testBuilderServiceAccount_explicitSetProjectIdBefore() {
        ServiceOptionsTest.TestServiceOptions options = setProjectId("override-project-id").setCredentials(ServiceOptionsTest.credentialsWithProjectId).build();
        Assert.assertEquals("override-project-id", getProjectId());
    }

    @Test
    public void testBuilderServiceAccount_explicitSetProjectIdAfter() {
        ServiceOptionsTest.TestServiceOptions options = setProjectId("override-project-id").build();
        Assert.assertEquals("override-project-id", getProjectId());
    }

    @Test
    public void testGetProjectIdRequired() {
        Assert.assertTrue(projectIdRequired());
    }

    @Test
    public void testService() {
        Assert.assertTrue(((getService()) instanceof ServiceOptionsTest.TestServiceImpl));
    }

    @Test
    public void testRpc() {
        Assert.assertTrue(((getRpc()) instanceof ServiceOptionsTest.DefaultTestServiceRpc));
    }

    @Test
    public void testBaseEquals() {
        Assert.assertEquals(ServiceOptionsTest.OPTIONS, ServiceOptionsTest.OPTIONS_COPY);
        Assert.assertNotEquals(ServiceOptionsTest.DEFAULT_OPTIONS, ServiceOptionsTest.OPTIONS);
    }

    @Test
    public void testLibraryName() {
        Assert.assertEquals(ServiceOptionsTest.LIBRARY_NAME, ServiceOptions.getLibraryName());
    }

    @Test
    public void testApplicationName() {
        Assert.assertTrue(ServiceOptionsTest.APPLICATION_NAME_PATTERN.matcher(getApplicationName()).matches());
    }

    @Test
    public void testBaseHashCode() {
        Assert.assertEquals(ServiceOptionsTest.OPTIONS.hashCode(), ServiceOptionsTest.OPTIONS_COPY.hashCode());
        Assert.assertNotEquals(ServiceOptionsTest.DEFAULT_OPTIONS.hashCode(), ServiceOptionsTest.OPTIONS.hashCode());
    }

    @Test
    public void testGetServiceAccountProjectId() throws Exception {
        File credentialsFile = File.createTempFile("credentials", ".json");
        credentialsFile.deleteOnExit();
        Files.write("{\"project_id\":\"my-project-id\"}".getBytes(), credentialsFile);
        Assert.assertEquals("my-project-id", ServiceOptions.getServiceAccountProjectId(credentialsFile.getPath()));
    }

    @Test
    public void testGetServiceAccountProjectId_badJson() throws Exception {
        File credentialsFile = File.createTempFile("credentials", ".json");
        credentialsFile.deleteOnExit();
        Files.write("asdfghj".getBytes(), credentialsFile);
        Assert.assertNull(ServiceOptions.getServiceAccountProjectId(credentialsFile.getPath()));
    }

    @Test
    public void testGetServiceAccountProjectId_nonExistentFile() throws Exception {
        File credentialsFile = new File("/doesnotexist");
        Assert.assertNull(ServiceOptions.getServiceAccountProjectId(credentialsFile.getPath()));
    }

    @Test
    public void testResponseHeaderContainsMetaDataFlavor() throws Exception {
        Multimap<String, String> headers = ArrayListMultimap.create();
        headers.put("Metadata-Flavor", "Google");
        HttpResponse httpResponse = createHttpResponseWithHeader(headers);
        assertThat(ServiceOptions.headerContainsMetadataFlavor(httpResponse)).isTrue();
    }

    @Test
    public void testResponseHeaderDoesNotContainMetaDataFlavor() throws Exception {
        Multimap<String, String> headers = ArrayListMultimap.create();
        HttpResponse httpResponse = createHttpResponseWithHeader(headers);
        assertThat(ServiceOptions.headerContainsMetadataFlavor(httpResponse)).isFalse();
    }
}

