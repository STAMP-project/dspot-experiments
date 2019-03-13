/**
 * Copyright 2012-2019 the original author or authors.
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
package org.springframework.boot.devtools.remote.client;


import HttpStatus.OK;
import java.io.File;
import java.net.SocketException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.boot.devtools.classpath.ClassPathChangedEvent;
import org.springframework.boot.devtools.test.MockClientHttpRequestFactory;
import org.springframework.mock.http.client.MockClientHttpRequest;


/**
 * Tests for {@link ClassPathChangeUploader}.
 *
 * @author Phillip Webb
 * @author Andy Wilkinson
 */
public class ClassPathChangeUploaderTests {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private MockClientHttpRequestFactory requestFactory;

    private ClassPathChangeUploader uploader;

    @Test
    public void urlMustNotBeNull() {
        assertThatIllegalArgumentException().isThrownBy(() -> new ClassPathChangeUploader(null, this.requestFactory)).withMessageContaining("URL must not be empty");
    }

    @Test
    public void urlMustNotBeEmpty() {
        assertThatIllegalArgumentException().isThrownBy(() -> new ClassPathChangeUploader("", this.requestFactory)).withMessageContaining("URL must not be empty");
    }

    @Test
    public void requestFactoryMustNotBeNull() {
        assertThatIllegalArgumentException().isThrownBy(() -> new ClassPathChangeUploader("http://localhost:8080", null)).withMessageContaining("RequestFactory must not be null");
    }

    @Test
    public void urlMustNotBeMalformed() {
        assertThatIllegalArgumentException().isThrownBy(() -> new ClassPathChangeUploader("htttttp:///ttest", this.requestFactory)).withMessageContaining("Malformed URL 'htttttp:///ttest'");
    }

    @Test
    public void sendsClassLoaderFiles() throws Exception {
        File sourceFolder = this.temp.newFolder();
        ClassPathChangedEvent event = createClassPathChangedEvent(sourceFolder);
        this.requestFactory.willRespond(OK);
        this.uploader.onApplicationEvent(event);
        assertThat(this.requestFactory.getExecutedRequests()).hasSize(1);
        MockClientHttpRequest request = this.requestFactory.getExecutedRequests().get(0);
        verifyUploadRequest(sourceFolder, request);
    }

    @Test
    public void retriesOnSocketException() throws Exception {
        File sourceFolder = this.temp.newFolder();
        ClassPathChangedEvent event = createClassPathChangedEvent(sourceFolder);
        this.requestFactory.willRespond(new SocketException());
        this.requestFactory.willRespond(OK);
        this.uploader.onApplicationEvent(event);
        assertThat(this.requestFactory.getExecutedRequests()).hasSize(2);
        verifyUploadRequest(sourceFolder, this.requestFactory.getExecutedRequests().get(1));
    }
}

