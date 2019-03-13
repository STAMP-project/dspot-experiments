/**
 * Copyright 2002-2013 the original author or authors.
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
package org.springframework.jca.cci;


import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.Interaction;
import javax.resource.cci.InteractionSpec;
import javax.resource.cci.Record;
import javax.resource.cci.RecordFactory;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.jca.cci.core.RecordCreator;
import org.springframework.jca.cci.object.MappingRecordOperation;
import org.springframework.jca.cci.object.SimpleRecordOperation;


/**
 *
 *
 * @author Thierry Templier
 * @author Chris Beams
 */
public class EisOperationTests {
    @Test
    public void testSimpleRecordOperation() throws ResourceException {
        ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        Connection connection = Mockito.mock(Connection.class);
        Interaction interaction = Mockito.mock(Interaction.class);
        Record inputRecord = Mockito.mock(Record.class);
        Record outputRecord = Mockito.mock(Record.class);
        InteractionSpec interactionSpec = Mockito.mock(InteractionSpec.class);
        SimpleRecordOperation query = new SimpleRecordOperation(connectionFactory, interactionSpec);
        BDDMockito.given(connectionFactory.getConnection()).willReturn(connection);
        BDDMockito.given(connection.createInteraction()).willReturn(interaction);
        BDDMockito.given(interaction.execute(interactionSpec, inputRecord)).willReturn(outputRecord);
        query.execute(inputRecord);
        Mockito.verify(interaction).execute(interactionSpec, inputRecord);
        Mockito.verify(interaction).close();
        Mockito.verify(connection).close();
    }

    @Test
    public void testSimpleRecordOperationWithExplicitOutputRecord() throws ResourceException {
        ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        Connection connection = Mockito.mock(Connection.class);
        Interaction interaction = Mockito.mock(Interaction.class);
        Record inputRecord = Mockito.mock(Record.class);
        Record outputRecord = Mockito.mock(Record.class);
        InteractionSpec interactionSpec = Mockito.mock(InteractionSpec.class);
        SimpleRecordOperation operation = new SimpleRecordOperation(connectionFactory, interactionSpec);
        BDDMockito.given(connectionFactory.getConnection()).willReturn(connection);
        BDDMockito.given(connection.createInteraction()).willReturn(interaction);
        BDDMockito.given(interaction.execute(interactionSpec, inputRecord, outputRecord)).willReturn(true);
        operation.execute(inputRecord, outputRecord);
        Mockito.verify(interaction).execute(interactionSpec, inputRecord, outputRecord);
        Mockito.verify(interaction).close();
        Mockito.verify(connection).close();
    }

    @Test
    public void testSimpleRecordOperationWithInputOutputRecord() throws ResourceException {
        ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        Connection connection = Mockito.mock(Connection.class);
        Interaction interaction = Mockito.mock(Interaction.class);
        Record inputOutputRecord = Mockito.mock(Record.class);
        InteractionSpec interactionSpec = Mockito.mock(InteractionSpec.class);
        SimpleRecordOperation query = new SimpleRecordOperation(connectionFactory, interactionSpec);
        BDDMockito.given(connectionFactory.getConnection()).willReturn(connection);
        BDDMockito.given(connection.createInteraction()).willReturn(interaction);
        BDDMockito.given(interaction.execute(interactionSpec, inputOutputRecord, inputOutputRecord)).willReturn(true);
        query.execute(inputOutputRecord, inputOutputRecord);
        Mockito.verify(interaction).execute(interactionSpec, inputOutputRecord, inputOutputRecord);
        Mockito.verify(interaction).close();
        Mockito.verify(connection).close();
    }

    @Test
    public void testMappingRecordOperation() throws ResourceException {
        ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        Connection connection = Mockito.mock(Connection.class);
        Interaction interaction = Mockito.mock(Interaction.class);
        RecordFactory recordFactory = Mockito.mock(RecordFactory.class);
        Record inputRecord = Mockito.mock(Record.class);
        Record outputRecord = Mockito.mock(Record.class);
        InteractionSpec interactionSpec = Mockito.mock(InteractionSpec.class);
        EisOperationTests.QueryCallDetector callDetector = Mockito.mock(EisOperationTests.QueryCallDetector.class);
        EisOperationTests.MappingRecordOperationImpl query = new EisOperationTests.MappingRecordOperationImpl(connectionFactory, interactionSpec);
        query.setCallDetector(callDetector);
        Object inObj = new Object();
        Object outObj = new Object();
        BDDMockito.given(connectionFactory.getRecordFactory()).willReturn(recordFactory);
        BDDMockito.given(callDetector.callCreateInputRecord(recordFactory, inObj)).willReturn(inputRecord);
        BDDMockito.given(connectionFactory.getConnection()).willReturn(connection);
        BDDMockito.given(connection.createInteraction()).willReturn(interaction);
        BDDMockito.given(interaction.execute(interactionSpec, inputRecord)).willReturn(outputRecord);
        BDDMockito.given(callDetector.callExtractOutputData(outputRecord)).willReturn(outObj);
        Assert.assertSame(outObj, execute(inObj));
        Mockito.verify(interaction).close();
        Mockito.verify(connection).close();
    }

    @Test
    public void testMappingRecordOperationWithOutputRecordCreator() throws ResourceException {
        ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        Connection connection = Mockito.mock(Connection.class);
        Interaction interaction = Mockito.mock(Interaction.class);
        RecordFactory recordFactory = Mockito.mock(RecordFactory.class);
        Record inputRecord = Mockito.mock(Record.class);
        Record outputRecord = Mockito.mock(Record.class);
        RecordCreator outputCreator = Mockito.mock(RecordCreator.class);
        InteractionSpec interactionSpec = Mockito.mock(InteractionSpec.class);
        EisOperationTests.QueryCallDetector callDetector = Mockito.mock(EisOperationTests.QueryCallDetector.class);
        EisOperationTests.MappingRecordOperationImpl query = new EisOperationTests.MappingRecordOperationImpl(connectionFactory, interactionSpec);
        query.setOutputRecordCreator(outputCreator);
        query.setCallDetector(callDetector);
        Object inObj = new Object();
        Object outObj = new Object();
        BDDMockito.given(connectionFactory.getRecordFactory()).willReturn(recordFactory);
        BDDMockito.given(callDetector.callCreateInputRecord(recordFactory, inObj)).willReturn(inputRecord);
        BDDMockito.given(connectionFactory.getConnection()).willReturn(connection);
        BDDMockito.given(connection.createInteraction()).willReturn(interaction);
        BDDMockito.given(connectionFactory.getRecordFactory()).willReturn(recordFactory);
        BDDMockito.given(outputCreator.createRecord(recordFactory)).willReturn(outputRecord);
        BDDMockito.given(interaction.execute(interactionSpec, inputRecord, outputRecord)).willReturn(true);
        BDDMockito.given(callDetector.callExtractOutputData(outputRecord)).willReturn(outObj);
        Assert.assertSame(outObj, execute(inObj));
        Mockito.verify(interaction).close();
        Mockito.verify(connection).close();
    }

    private class MappingRecordOperationImpl extends MappingRecordOperation {
        private EisOperationTests.QueryCallDetector callDetector;

        public MappingRecordOperationImpl(ConnectionFactory connectionFactory, InteractionSpec interactionSpec) {
            super(connectionFactory, interactionSpec);
        }

        public void setCallDetector(EisOperationTests.QueryCallDetector callDetector) {
            this.callDetector = callDetector;
        }

        @Override
        protected Record createInputRecord(RecordFactory recordFactory, Object inputObject) {
            return this.callDetector.callCreateInputRecord(recordFactory, inputObject);
        }

        @Override
        protected Object extractOutputData(Record outputRecord) throws ResourceException {
            return this.callDetector.callExtractOutputData(outputRecord);
        }
    }

    private interface QueryCallDetector {
        Record callCreateInputRecord(RecordFactory recordFactory, Object inputObject);

        Object callExtractOutputData(Record outputRecord);
    }
}

