/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.server.nodemanager.api.protocolrecords.impl.pb;


import java.io.IOException;
import org.apache.hadoop.io.DataInputBuffer;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.yarn.api.records.SerializedException;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.yarn.proto.YarnServerNodemanagerServiceProtos.LocalResourceStatusProto;
import org.apache.hadoop.yarn.proto.YarnServerNodemanagerServiceProtos.LocalizerHeartbeatResponseProto;
import org.apache.hadoop.yarn.proto.YarnServerNodemanagerServiceProtos.LocalizerStatusProto;
import org.apache.hadoop.yarn.server.nodemanager.api.protocolrecords.LocalResourceStatus;
import org.apache.hadoop.yarn.server.nodemanager.api.protocolrecords.LocalizerHeartbeatResponse;
import org.apache.hadoop.yarn.server.nodemanager.api.protocolrecords.LocalizerStatus;
import org.junit.Assert;
import org.junit.Test;


public class TestPBRecordImpl {
    static final RecordFactory recordFactory = TestPBRecordImpl.createPBRecordFactory();

    @Test(timeout = 10000)
    public void testLocalResourceStatusSerDe() throws Exception {
        LocalResourceStatus rsrcS = TestPBRecordImpl.createLocalResourceStatus();
        Assert.assertTrue((rsrcS instanceof LocalResourceStatusPBImpl));
        LocalResourceStatusPBImpl rsrcPb = ((LocalResourceStatusPBImpl) (rsrcS));
        DataOutputBuffer out = new DataOutputBuffer();
        rsrcPb.getProto().writeDelimitedTo(out);
        DataInputBuffer in = new DataInputBuffer();
        in.reset(out.getData(), 0, out.getLength());
        LocalResourceStatusProto rsrcPbD = LocalResourceStatusProto.parseDelimitedFrom(in);
        Assert.assertNotNull(rsrcPbD);
        LocalResourceStatus rsrcD = new LocalResourceStatusPBImpl(rsrcPbD);
        Assert.assertEquals(rsrcS, rsrcD);
        Assert.assertEquals(TestPBRecordImpl.createResource(), rsrcS.getResource());
        Assert.assertEquals(TestPBRecordImpl.createResource(), rsrcD.getResource());
    }

    @Test(timeout = 10000)
    public void testLocalizerStatusSerDe() throws Exception {
        LocalizerStatus rsrcS = TestPBRecordImpl.createLocalizerStatus();
        Assert.assertTrue((rsrcS instanceof LocalizerStatusPBImpl));
        LocalizerStatusPBImpl rsrcPb = ((LocalizerStatusPBImpl) (rsrcS));
        DataOutputBuffer out = new DataOutputBuffer();
        rsrcPb.getProto().writeDelimitedTo(out);
        DataInputBuffer in = new DataInputBuffer();
        in.reset(out.getData(), 0, out.getLength());
        LocalizerStatusProto rsrcPbD = LocalizerStatusProto.parseDelimitedFrom(in);
        Assert.assertNotNull(rsrcPbD);
        LocalizerStatus rsrcD = new LocalizerStatusPBImpl(rsrcPbD);
        Assert.assertEquals(rsrcS, rsrcD);
        Assert.assertEquals("localizer0", rsrcS.getLocalizerId());
        Assert.assertEquals("localizer0", rsrcD.getLocalizerId());
        Assert.assertEquals(TestPBRecordImpl.createLocalResourceStatus(), rsrcS.getResourceStatus(0));
        Assert.assertEquals(TestPBRecordImpl.createLocalResourceStatus(), rsrcD.getResourceStatus(0));
    }

    @Test(timeout = 10000)
    public void testLocalizerHeartbeatResponseSerDe() throws Exception {
        LocalizerHeartbeatResponse rsrcS = TestPBRecordImpl.createLocalizerHeartbeatResponse();
        Assert.assertTrue((rsrcS instanceof LocalizerHeartbeatResponsePBImpl));
        LocalizerHeartbeatResponsePBImpl rsrcPb = ((LocalizerHeartbeatResponsePBImpl) (rsrcS));
        DataOutputBuffer out = new DataOutputBuffer();
        rsrcPb.getProto().writeDelimitedTo(out);
        DataInputBuffer in = new DataInputBuffer();
        in.reset(out.getData(), 0, out.getLength());
        LocalizerHeartbeatResponseProto rsrcPbD = LocalizerHeartbeatResponseProto.parseDelimitedFrom(in);
        Assert.assertNotNull(rsrcPbD);
        LocalizerHeartbeatResponse rsrcD = new LocalizerHeartbeatResponsePBImpl(rsrcPbD);
        Assert.assertEquals(rsrcS, rsrcD);
        Assert.assertEquals(TestPBRecordImpl.createResource(), rsrcS.getResourceSpecs().get(0).getResource());
        Assert.assertEquals(TestPBRecordImpl.createResource(), rsrcD.getResourceSpecs().get(0).getResource());
    }

    @Test(timeout = 10000)
    public void testSerializedExceptionDeSer() throws Exception {
        // without cause
        YarnException yarnEx = new YarnException("Yarn_Exception");
        SerializedException serEx = SerializedException.newInstance(yarnEx);
        Throwable throwable = serEx.deSerialize();
        Assert.assertEquals(yarnEx.getClass(), throwable.getClass());
        Assert.assertEquals(yarnEx.getMessage(), throwable.getMessage());
        // with cause
        IOException ioe = new IOException("Test_IOException");
        RuntimeException runtimeException = new RuntimeException("Test_RuntimeException", ioe);
        YarnException yarnEx2 = new YarnException("Test_YarnException", runtimeException);
        SerializedException serEx2 = SerializedException.newInstance(yarnEx2);
        Throwable throwable2 = serEx2.deSerialize();
        throwable2.printStackTrace();
        Assert.assertEquals(yarnEx2.getClass(), throwable2.getClass());
        Assert.assertEquals(yarnEx2.getMessage(), throwable2.getMessage());
        Assert.assertEquals(runtimeException.getClass(), throwable2.getCause().getClass());
        Assert.assertEquals(runtimeException.getMessage(), throwable2.getCause().getMessage());
        Assert.assertEquals(ioe.getClass(), throwable2.getCause().getCause().getClass());
        Assert.assertEquals(ioe.getMessage(), throwable2.getCause().getCause().getMessage());
    }
}

