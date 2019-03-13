/**
 * Copyright 2019 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.agent.launcher;


import DownloadableFile.AGENT;
import com.thoughtworks.go.agent.common.ssl.GoAgentServerHttpClientBuilder;
import com.thoughtworks.go.agent.testhelper.FakeGoServer;
import com.thoughtworks.go.mothers.ServerUrlGeneratorMother;
import com.thoughtworks.go.util.SslVerificationMode;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.UnknownHostException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.HashMap;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.NullOutputStream;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ServerBinaryDownloaderTest {
    @Rule
    public FakeGoServer server = new FakeGoServer();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldSetMd5AndSSLPortHeaders() throws Exception {
        ServerBinaryDownloader downloader = new ServerBinaryDownloader(ServerUrlGeneratorMother.generatorFor("localhost", server.getPort()), null, SslVerificationMode.NONE);
        downloader.downloadIfNecessary(AGENT);
        MessageDigest digester = MessageDigest.getInstance("MD5");
        try (BufferedInputStream stream = new BufferedInputStream(new FileInputStream(AGENT.getLocalFile()))) {
            try (DigestInputStream digest = new DigestInputStream(stream, digester)) {
                IOUtils.copy(digest, new NullOutputStream());
            }
            Assert.assertThat(downloader.getMd5(), Matchers.is(Hex.encodeHexString(digester.digest()).toLowerCase()));
        }
        Assert.assertThat(downloader.getSslPort(), Matchers.is(String.valueOf(server.getSecurePort())));
    }

    @Test
    public void shouldGetExtraPropertiesFromHeader() {
        assertExtraProperties("", new HashMap<>());
        assertExtraProperties("Key1=Value1 key2=value2", new HashMap<String, String>() {
            {
                put("Key1", "Value1");
                put("key2", "value2");
            }
        });
        assertExtraProperties("Key1=Value1 key2=value2 key2=value3", new HashMap<String, String>() {
            {
                put("Key1", "Value1");
                put("key2", "value2");
            }
        });
        assertExtraProperties("Key1%20WithSpace=Value1%20WithSpace key2=value2", new HashMap<String, String>() {
            {
                put("Key1 WithSpace", "Value1 WithSpace");
                put("key2", "value2");
            }
        });
    }

    @Test
    public void shouldNotFailIfExtraPropertiesAreNotFormattedProperly() {
        assertExtraProperties("abc", new HashMap<>());
    }

    @Test
    public void shouldDownloadAgentJarFile() {
        ServerBinaryDownloader downloader = new ServerBinaryDownloader(ServerUrlGeneratorMother.generatorFor("localhost", server.getPort()), null, SslVerificationMode.NONE);
        Assert.assertThat(AGENT.doesNotExist(), Matchers.is(true));
        downloader.downloadIfNecessary(AGENT);
        Assert.assertThat(AGENT.getLocalFile().exists(), Matchers.is(true));
    }

    @Test
    public void shouldReturnTrueIfTheFileIsDownloaded() {
        ServerBinaryDownloader downloader = new ServerBinaryDownloader(ServerUrlGeneratorMother.generatorFor("localhost", server.getPort()), null, SslVerificationMode.NONE);
        Assert.assertThat(downloader.downloadIfNecessary(AGENT), Matchers.is(true));
    }

    @Test(expected = Exception.class)
    public void shouldThrowExceptionIfTheServerIsDown() throws Exception {
        ServerBinaryDownloader downloader = new ServerBinaryDownloader(ServerUrlGeneratorMother.generatorFor("locahost", server.getPort()), null, SslVerificationMode.NONE);
        downloader.download(AGENT);
    }

    @Test
    public void shouldConnectToAnSSLServerWithSelfSignedCertWhenInsecureModeIsNoVerifyHost() throws Exception {
        ServerBinaryDownloader downloader = new ServerBinaryDownloader(ServerUrlGeneratorMother.generatorFor("localhost", server.getPort()), new File("testdata/test_cert.pem"), SslVerificationMode.NO_VERIFY_HOST);
        downloader.download(AGENT);
        Assert.assertThat(AGENT.getLocalFile().exists(), Matchers.is(true));
    }

    @Test
    public void shouldRaiseExceptionWhenSelfSignedCertDoesNotMatchTheHostName() throws Exception {
        exception.expect(Exception.class);
        exception.expectMessage("Certificate for <localhost> doesn't match any of the subject alternative names: []");
        ServerBinaryDownloader downloader = new ServerBinaryDownloader(ServerUrlGeneratorMother.generatorFor((("https://localhost:" + (server.getSecurePort())) + "/go/hello")), new File("testdata/test_cert.pem"), SslVerificationMode.FULL);
        downloader.download(AGENT);
    }

    @Test
    public void shouldFailIfMD5HeadersAreMissing() throws Exception {
        exception.expect(Exception.class);
        exception.expectMessage("Missing required headers 'Content-MD5' and 'Cruise-Server-Ssl-Port' in response.");
        ServerBinaryDownloader downloader = new ServerBinaryDownloader(ServerUrlGeneratorMother.generatorWithoutSubPathFor((("https://localhost:" + (server.getSecurePort())) + "/go/hello")), null, SslVerificationMode.NONE);
        downloader.fetchUpdateCheckHeaders(AGENT);
    }

    @Test
    public void shouldFailIfServerIsNotAvailable() throws Exception {
        exception.expect(UnknownHostException.class);
        exception.expectMessage("invalidserver");
        ServerBinaryDownloader downloader = new ServerBinaryDownloader(ServerUrlGeneratorMother.generatorWithoutSubPathFor((("https://invalidserver:" + (server.getSecurePort())) + "/go/hello")), null, SslVerificationMode.NONE);
        downloader.fetchUpdateCheckHeaders(AGENT);
    }

    @Test
    public void shouldThrowExceptionInCaseOf404() throws Exception {
        exception.expect(Exception.class);
        exception.expectMessage(("This agent might be incompatible with your GoCD Server." + " Please fix the version mismatch between GoCD Server and GoCD Agent."));
        ServerBinaryDownloader downloader = new ServerBinaryDownloader(ServerUrlGeneratorMother.generatorWithoutSubPathFor((("https://localhost:" + (server.getSecurePort())) + "/go/not-found")), null, SslVerificationMode.NONE);
        downloader.download(AGENT);
    }

    @Test
    public void shouldReturnFalseIfTheServerDoesNotRespondWithEntity() throws Exception {
        GoAgentServerHttpClientBuilder builder = Mockito.mock(GoAgentServerHttpClientBuilder.class);
        CloseableHttpClient closeableHttpClient = Mockito.mock(CloseableHttpClient.class);
        Mockito.when(builder.build()).thenReturn(closeableHttpClient);
        CloseableHttpResponse httpResponse = Mockito.mock(CloseableHttpResponse.class);
        Mockito.when(closeableHttpClient.execute(ArgumentMatchers.any(HttpRequestBase.class))).thenReturn(httpResponse);
        ServerBinaryDownloader downloader = new ServerBinaryDownloader(builder, ServerUrlGeneratorMother.generatorFor("localhost", server.getPort()));
        Assert.assertThat(downloader.download(AGENT), Matchers.is(false));
    }
}

