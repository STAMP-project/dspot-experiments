/**
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.integration.samples.filesplit;


import DirtiesContext.ClassMode;
import java.io.InputStream;
import org.apache.commons.net.ftp.FTPFile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.integration.endpoint.SourcePollingChannelAdapter;
import org.springframework.integration.file.remote.session.Session;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.test.context.SpringIntegrationTest;
import org.springframework.integration.test.mail.TestMailServer;
import org.springframework.integration.test.mail.TestMailServer.SmtpServer;
import org.springframework.integration.test.util.TestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.main.allow-bean-definition-overriding=true")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@SpringIntegrationTest(noAutoStartup = "fileInboundChannelAdapter")
public class ApplicationTests {
    private static final SmtpServer smtpServer = TestMailServer.smtp(0);

    @Autowired
    private Session<FTPFile> session;

    @Autowired
    private SourcePollingChannelAdapter fileInboundChannelAdapter;

    @SuppressWarnings("unchecked")
    @Test
    public void testSuccess() throws Exception {
        String message = runTest(false);
        assertThat(message).contains("File successfully split and transferred");
        assertThat(message).contains(TestUtils.applySystemFileSeparator("/tmp/in/foo.txt"));
    }

    @Test
    public void testFailure() throws Exception {
        BDDMockito.willThrow(new RuntimeException("fail test exception")).given(this.session).write(ArgumentMatchers.any(InputStream.class), ArgumentMatchers.eq("foo/002.txt.writing"));
        String message = runTest(true);
        assertThat(message).contains("File split and transfer failed");
        assertThat(message).contains("fail test exception");
        assertThat(message).contains(TestUtils.applySystemFileSeparator("/tmp/out/002.txt"));
    }

    /**
     * Overrides the ftp session factories with mocks.
     */
    @Configuration
    @Import(Application.class)
    public static class Config {
        @Bean
        public SessionFactory<FTPFile> ftp1() {
            return mockSf();
        }

        @Bean
        public SessionFactory<FTPFile> ftp2() {
            return mockSf();
        }

        @Bean
        public SessionFactory<FTPFile> ftp3() {
            return mockSf();
        }

        private SessionFactory<FTPFile> mockSf() {
            @SuppressWarnings("unchecked")
            SessionFactory<FTPFile> mocksf = Mockito.mock(SessionFactory.class);
            BDDMockito.given(mocksf.getSession()).willReturn(mockSession());
            return mocksf;
        }

        @Bean
        @SuppressWarnings("unchecked")
        public Session<FTPFile> mockSession() {
            return Mockito.mock(Session.class);
        }
    }
}

