package org.apache.geode.internal.protocol.protobuf.v1.operations;


import ProtocolVersion.MajorVersions.CURRENT_MAJOR_VERSION_VALUE;
import ProtocolVersion.MajorVersions.INVALID_MAJOR_VERSION_VALUE;
import ProtocolVersion.MinorVersions.CURRENT_MINOR_VERSION_VALUE;
import ProtocolVersion.MinorVersions.INVALID_MINOR_VERSION_VALUE;
import ProtocolVersion.NewConnectionClientVersion;
import ProtocolVersion.VersionAcknowledgement;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.apache.geode.internal.protocol.protobuf.ProtocolVersion;
import org.apache.geode.internal.protocol.protobuf.statistics.ClientStatistics;
import org.apache.geode.internal.protocol.protobuf.v1.MessageUtil;
import org.apache.geode.test.junit.categories.ClientServerTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;


/* Licensed to the Apache Software Foundation (ASF) under one or more contributor license
agreements. See the NOTICE file distributed with this work for additional information regarding
copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance with the License. You may obtain a
copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License
is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
or implied. See the License for the specific language governing permissions and limitations under
the License.
 */
@Category({ ClientServerTest.class })
public class ProtocolVersionHandlerJUnitTest {
    private static final int INVALID_MAJOR_VERSION = 67;

    private static final int INVALID_MINOR_VERSION = 92347;

    private ProtocolVersionHandler protocolVersionHandler = new ProtocolVersionHandler();

    @Test
    public void testCurrentVersionVersionMessageSucceeds() throws Exception {
        ProtocolVersion.NewConnectionClientVersion versionRequest = generateVersionMessageRequest(CURRENT_MAJOR_VERSION_VALUE, CURRENT_MINOR_VERSION_VALUE);
        ByteArrayInputStream inputStream = MessageUtil.writeMessageDelimitedToInputStream(versionRequest);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Assert.assertTrue(protocolVersionHandler.handleVersionMessage(inputStream, outputStream, Mockito.mock(ClientStatistics.class)));
        ProtocolVersion.VersionAcknowledgement versionResponse = VersionAcknowledgement.parseDelimitedFrom(new ByteArrayInputStream(outputStream.toByteArray()));
        Assert.assertTrue(versionResponse.getVersionAccepted());
        Assert.assertEquals(CURRENT_MAJOR_VERSION_VALUE, versionResponse.getServerMajorVersion());
        Assert.assertEquals(CURRENT_MINOR_VERSION_VALUE, versionResponse.getServerMinorVersion());
    }

    @Test
    public void testInvalidMajorVersionFails() throws Exception {
        Assert.assertNotEquals(ProtocolVersionHandlerJUnitTest.INVALID_MAJOR_VERSION, CURRENT_MAJOR_VERSION_VALUE);
        ProtocolVersion.NewConnectionClientVersion versionRequest = generateVersionMessageRequest(ProtocolVersionHandlerJUnitTest.INVALID_MAJOR_VERSION, CURRENT_MINOR_VERSION_VALUE);
        verifyVersionMessageFails(versionRequest);
        // Also validate the protobuf INVALID_MAJOR_VERSION_VALUE constant fails
        versionRequest = generateVersionMessageRequest(INVALID_MAJOR_VERSION_VALUE, CURRENT_MINOR_VERSION_VALUE);
        verifyVersionMessageFails(versionRequest);
    }

    @Test
    public void testInvalidMinorVersionFails() throws Exception {
        Assert.assertNotEquals(ProtocolVersionHandlerJUnitTest.INVALID_MINOR_VERSION, CURRENT_MINOR_VERSION_VALUE);
        ProtocolVersion.NewConnectionClientVersion versionRequest = generateVersionMessageRequest(CURRENT_MAJOR_VERSION_VALUE, ProtocolVersionHandlerJUnitTest.INVALID_MINOR_VERSION);
        verifyVersionMessageFails(versionRequest);
        // Also validate the protobuf INVALID_MINOR_VERSION_VALUE constant fails
        versionRequest = generateVersionMessageRequest(CURRENT_MAJOR_VERSION_VALUE, INVALID_MINOR_VERSION_VALUE);
        verifyVersionMessageFails(versionRequest);
    }
}

