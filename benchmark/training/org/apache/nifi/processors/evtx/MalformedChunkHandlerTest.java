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
package org.apache.nifi.processors.evtx;


import CoreAttributes.FILENAME;
import CoreAttributes.MIME_TYPE;
import com.google.common.net.MediaType;
import java.io.ByteArrayOutputStream;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.io.OutputStreamCallback;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class MalformedChunkHandlerTest {
    Relationship badChunkRelationship;

    MalformedChunkHandler malformedChunkHandler;

    @Test
    public void testHandle() {
        String name = "name";
        byte[] badChunk = new byte[]{ 8 };
        FlowFile original = Mockito.mock(FlowFile.class);
        FlowFile updated1 = Mockito.mock(FlowFile.class);
        FlowFile updated2 = Mockito.mock(FlowFile.class);
        FlowFile updated3 = Mockito.mock(FlowFile.class);
        FlowFile updated4 = Mockito.mock(FlowFile.class);
        ProcessSession session = Mockito.mock(ProcessSession.class);
        Mockito.when(session.create(original)).thenReturn(updated1);
        Mockito.when(session.putAttribute(updated1, FILENAME.key(), name)).thenReturn(updated2);
        Mockito.when(session.putAttribute(updated2, MIME_TYPE.key(), MediaType.APPLICATION_BINARY.toString())).thenReturn(updated3);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Mockito.when(session.write(ArgumentMatchers.eq(updated3), ArgumentMatchers.any(OutputStreamCallback.class))).thenAnswer(( invocation) -> {
            ((OutputStreamCallback) (invocation.getArguments()[1])).process(out);
            return updated4;
        });
        malformedChunkHandler.handle(original, session, name, badChunk);
        Mockito.verify(session).transfer(updated4, badChunkRelationship);
        Assert.assertArrayEquals(badChunk, out.toByteArray());
    }
}

