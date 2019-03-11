/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.ozone.om;


import OzoneConsts.OM_CONTEXT_ATTRIBUTE;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.ozone.MiniOzoneCluster;
import org.apache.hadoop.ozone.OzoneConsts;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mockito;


/**
 * Class used for testing the OM DB Checkpoint provider servlet.
 */
public class TestOMDbCheckpointServlet {
    private MiniOzoneCluster cluster = null;

    private OMMetrics omMetrics;

    private OzoneConfiguration conf;

    private String clusterId;

    private String scmId;

    private String omId;

    @Rule
    public Timeout timeout = new Timeout(60000);

    @Test
    public void testDoGet() throws IOException, ServletException {
        File tempFile = null;
        try {
            OMDBCheckpointServlet omDbCheckpointServletMock = Mockito.mock(OMDBCheckpointServlet.class);
            Mockito.doCallRealMethod().when(omDbCheckpointServletMock).init();
            HttpServletRequest requestMock = Mockito.mock(HttpServletRequest.class);
            HttpServletResponse responseMock = Mockito.mock(HttpServletResponse.class);
            ServletContext servletContextMock = Mockito.mock(ServletContext.class);
            Mockito.when(omDbCheckpointServletMock.getServletContext()).thenReturn(servletContextMock);
            Mockito.when(servletContextMock.getAttribute(OM_CONTEXT_ATTRIBUTE)).thenReturn(cluster.getOzoneManager());
            Mockito.when(requestMock.getParameter(OzoneConsts.OZONE_DB_CHECKPOINT_REQUEST_FLUSH)).thenReturn("true");
            Mockito.doNothing().when(responseMock).setContentType("application/x-tgz");
            Mockito.doNothing().when(responseMock).setHeader(Matchers.anyString(), Matchers.anyString());
            tempFile = File.createTempFile(("testDoGet_" + (System.currentTimeMillis())), ".tar.gz");
            FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
            Mockito.when(responseMock.getOutputStream()).thenReturn(new ServletOutputStream() {
                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setWriteListener(WriteListener writeListener) {
                }

                @Override
                public void write(int b) throws IOException {
                    fileOutputStream.write(b);
                }
            });
            Mockito.doCallRealMethod().when(omDbCheckpointServletMock).doGet(requestMock, responseMock);
            omDbCheckpointServletMock.init();
            Assert.assertTrue(((omMetrics.getLastCheckpointCreationTimeTaken()) == 0));
            Assert.assertTrue(((omMetrics.getLastCheckpointTarOperationTimeTaken()) == 0));
            Assert.assertTrue(((omMetrics.getLastCheckpointStreamingTimeTaken()) == 0));
            omDbCheckpointServletMock.doGet(requestMock, responseMock);
            Assert.assertTrue(((tempFile.length()) > 0));
            Assert.assertTrue(((omMetrics.getLastCheckpointCreationTimeTaken()) > 0));
            Assert.assertTrue(((omMetrics.getLastCheckpointTarOperationTimeTaken()) > 0));
            Assert.assertTrue(((omMetrics.getLastCheckpointStreamingTimeTaken()) > 0));
        } finally {
            FileUtils.deleteQuietly(tempFile);
        }
    }
}

