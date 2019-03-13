/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.hadoop;


import AbstractHadoopProcessor.HdfsResources;
import java.io.File;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.nifi.hadoop.KerberosProperties;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processors.hadoop.util.SequenceFileReader;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


public class GetHDFSSequenceFileTest {
    private HdfsResources hdfsResources;

    private GetHDFSSequenceFile getHDFSSequenceFile;

    private Configuration configuration;

    private FileSystem fileSystem;

    private UserGroupInformation userGroupInformation;

    private boolean reloginTried;

    @Test
    public void getFlowFilesWithUgiAndNewTicketShouldCallDoAsAndNotRelogin() throws Exception {
        SequenceFileReader reader = Mockito.mock(SequenceFileReader.class);
        Path file = Mockito.mock(Path.class);
        getHDFSSequenceFile.getFlowFiles(configuration, fileSystem, reader, file);
        ArgumentCaptor<PrivilegedExceptionAction> privilegedExceptionActionArgumentCaptor = ArgumentCaptor.forClass(PrivilegedExceptionAction.class);
        Mockito.verifyNoMoreInteractions(reader);
        Mockito.verify(userGroupInformation).doAs(privilegedExceptionActionArgumentCaptor.capture());
        privilegedExceptionActionArgumentCaptor.getValue().run();
        Mockito.verify(reader).readSequenceFile(file, configuration, fileSystem);
        Assert.assertFalse(reloginTried);
    }

    @Test
    public void testGetFlowFilesNoUgiShouldntCallDoAs() throws Exception {
        hdfsResources = new AbstractHadoopProcessor.HdfsResources(configuration, fileSystem, null);
        init();
        SequenceFileReader reader = Mockito.mock(SequenceFileReader.class);
        Path file = Mockito.mock(Path.class);
        getHDFSSequenceFile.getFlowFiles(configuration, fileSystem, reader, file);
        Mockito.verify(reader).readSequenceFile(file, configuration, fileSystem);
    }

    public class TestableGetHDFSSequenceFile extends GetHDFSSequenceFile {
        @Override
        HdfsResources resetHDFSResources(String configResources, ProcessContext context) throws IOException {
            return hdfsResources;
        }

        @Override
        public void onScheduled(ProcessContext context) throws IOException {
            abstractOnScheduled(context);
        }

        @Override
        protected KerberosProperties getKerberosProperties(File kerberosConfigFile) {
            return kerberosProperties;
        }
    }
}

