/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.devtools.restart.server;


import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.devtools.restart.classloader.ClassLoaderFile.Kind;
import org.springframework.boot.devtools.restart.classloader.ClassLoaderFiles;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * Tests for {@link HttpRestartServer}.
 *
 * @author Phillip Webb
 */
public class HttpRestartServerTests {
    @Mock
    private RestartServer delegate;

    private HttpRestartServer server;

    @Captor
    private ArgumentCaptor<ClassLoaderFiles> filesCaptor;

    @Test
    public void sourceFolderUrlFilterMustNotBeNull() {
        assertThatIllegalArgumentException().isThrownBy(() -> new HttpRestartServer(((SourceFolderUrlFilter) (null)))).withMessageContaining("SourceFolderUrlFilter must not be null");
    }

    @Test
    public void restartServerMustNotBeNull() {
        assertThatIllegalArgumentException().isThrownBy(() -> new HttpRestartServer(((RestartServer) (null)))).withMessageContaining("RestartServer must not be null");
    }

    @Test
    public void sendClassLoaderFiles() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        ClassLoaderFiles files = new ClassLoaderFiles();
        files.addFile("name", new org.springframework.boot.devtools.restart.classloader.ClassLoaderFile(Kind.ADDED, new byte[0]));
        byte[] bytes = serialize(files);
        request.setContent(bytes);
        this.server.handle(new org.springframework.http.server.ServletServerHttpRequest(request), new org.springframework.http.server.ServletServerHttpResponse(response));
        Mockito.verify(this.delegate).updateAndRestart(this.filesCaptor.capture());
        assertThat(this.filesCaptor.getValue().getFile("name")).isNotNull();
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    public void sendNoContent() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        this.server.handle(new org.springframework.http.server.ServletServerHttpRequest(request), new org.springframework.http.server.ServletServerHttpResponse(response));
        Mockito.verifyZeroInteractions(this.delegate);
        assertThat(response.getStatus()).isEqualTo(500);
    }

    @Test
    public void sendBadData() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setContent(new byte[]{ 0, 0, 0 });
        this.server.handle(new org.springframework.http.server.ServletServerHttpRequest(request), new org.springframework.http.server.ServletServerHttpResponse(response));
        Mockito.verifyZeroInteractions(this.delegate);
        assertThat(response.getStatus()).isEqualTo(500);
    }
}

