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
import ResultProcessor.UNABLE_TO_PROCESS_DUE_TO;
import com.google.common.net.MediaType;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.Relationship;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ResultProcessorTest {
    Relationship successRelationship;

    Relationship failureRelationship;

    ResultProcessor resultProcessor;

    @Test
    public void testProcessResultFileSuccess() {
        ProcessSession processSession = Mockito.mock(ProcessSession.class);
        ComponentLog componentLog = Mockito.mock(ComponentLog.class);
        FlowFile flowFile = Mockito.mock(FlowFile.class);
        Exception exception = null;
        String name = "basename";
        Mockito.when(processSession.putAttribute(ArgumentMatchers.eq(flowFile), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(flowFile);
        resultProcessor.process(processSession, componentLog, flowFile, exception, name);
        Mockito.verify(processSession).putAttribute(flowFile, FILENAME.key(), name);
        Mockito.verify(processSession).putAttribute(flowFile, MIME_TYPE.key(), MediaType.APPLICATION_XML_UTF_8.toString());
        Mockito.verify(processSession).transfer(flowFile, successRelationship);
        Mockito.verifyNoMoreInteractions(componentLog);
    }

    @Test
    public void testProcessResultFileFalure() {
        ProcessSession processSession = Mockito.mock(ProcessSession.class);
        ComponentLog componentLog = Mockito.mock(ComponentLog.class);
        FlowFile flowFile = Mockito.mock(FlowFile.class);
        Exception exception = new Exception();
        String name = "name";
        Mockito.when(processSession.putAttribute(ArgumentMatchers.eq(flowFile), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(flowFile);
        resultProcessor.process(processSession, componentLog, flowFile, exception, name);
        Mockito.verify(processSession).putAttribute(flowFile, FILENAME.key(), name);
        Mockito.verify(processSession).putAttribute(flowFile, MIME_TYPE.key(), MediaType.APPLICATION_XML_UTF_8.toString());
        Mockito.verify(processSession).transfer(flowFile, failureRelationship);
        Mockito.verify(componentLog).error(ArgumentMatchers.eq(UNABLE_TO_PROCESS_DUE_TO), ArgumentMatchers.any(Object[].class), ArgumentMatchers.eq(exception));
    }
}

