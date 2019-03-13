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


import java.sql.SQLException;
import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.ConnectionSpec;
import javax.resource.cci.IndexedRecord;
import javax.resource.cci.Interaction;
import javax.resource.cci.InteractionSpec;
import javax.resource.cci.MappedRecord;
import javax.resource.cci.Record;
import javax.resource.cci.RecordFactory;
import javax.resource.cci.ResultSet;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.jca.cci.connection.ConnectionSpecConnectionFactoryAdapter;
import org.springframework.jca.cci.connection.NotSupportedRecordFactory;
import org.springframework.jca.cci.core.CciTemplate;
import org.springframework.jca.cci.core.ConnectionCallback;
import org.springframework.jca.cci.core.InteractionCallback;
import org.springframework.jca.cci.core.RecordCreator;
import org.springframework.jca.cci.core.RecordExtractor;


/**
 *
 *
 * @author Thierry Templier
 * @author Juergen Hoeller
 * @author Chris Beams
 */
public class CciTemplateTests {
    @Test
    public void testCreateIndexedRecord() throws ResourceException {
        ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        RecordFactory recordFactory = Mockito.mock(RecordFactory.class);
        IndexedRecord indexedRecord = Mockito.mock(IndexedRecord.class);
        BDDMockito.given(connectionFactory.getRecordFactory()).willReturn(recordFactory);
        BDDMockito.given(recordFactory.createIndexedRecord("name")).willReturn(indexedRecord);
        CciTemplate ct = new CciTemplate(connectionFactory);
        ct.createIndexedRecord("name");
        Mockito.verify(recordFactory).createIndexedRecord("name");
    }

    @Test
    public void testCreateMappedRecord() throws ResourceException {
        ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        RecordFactory recordFactory = Mockito.mock(RecordFactory.class);
        MappedRecord mappedRecord = Mockito.mock(MappedRecord.class);
        BDDMockito.given(connectionFactory.getRecordFactory()).willReturn(recordFactory);
        BDDMockito.given(recordFactory.createMappedRecord("name")).willReturn(mappedRecord);
        CciTemplate ct = new CciTemplate(connectionFactory);
        ct.createMappedRecord("name");
        Mockito.verify(recordFactory).createMappedRecord("name");
    }

    @Test
    public void testTemplateExecuteInputOutput() throws ResourceException {
        ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        Connection connection = Mockito.mock(Connection.class);
        Interaction interaction = Mockito.mock(Interaction.class);
        Record inputRecord = Mockito.mock(Record.class);
        Record outputRecord = Mockito.mock(Record.class);
        InteractionSpec interactionSpec = Mockito.mock(InteractionSpec.class);
        BDDMockito.given(connectionFactory.getConnection()).willReturn(connection);
        BDDMockito.given(connection.createInteraction()).willReturn(interaction);
        BDDMockito.given(interaction.execute(interactionSpec, inputRecord, outputRecord)).willReturn(true);
        CciTemplate ct = new CciTemplate(connectionFactory);
        ct.execute(interactionSpec, inputRecord, outputRecord);
        Mockito.verify(interaction).execute(interactionSpec, inputRecord, outputRecord);
        Mockito.verify(interaction).close();
        Mockito.verify(connection).close();
    }

    @Test
    public void testTemplateExecuteWithCreatorAndRecordFactoryNotSupported() throws ResourceException {
        ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        Connection connection = Mockito.mock(Connection.class);
        Interaction interaction = Mockito.mock(Interaction.class);
        Record inputRecord = Mockito.mock(Record.class);
        final Record outputRecord = Mockito.mock(Record.class);
        InteractionSpec interactionSpec = Mockito.mock(InteractionSpec.class);
        BDDMockito.given(connectionFactory.getConnection()).willReturn(connection);
        BDDMockito.given(connectionFactory.getRecordFactory()).willThrow(new NotSupportedException("not supported"));
        BDDMockito.given(connection.createInteraction()).willReturn(interaction);
        BDDMockito.given(interaction.execute(interactionSpec, inputRecord, outputRecord)).willReturn(true);
        CciTemplate ct = new CciTemplate(connectionFactory);
        ct.setOutputRecordCreator(new RecordCreator() {
            @Override
            public Record createRecord(RecordFactory recordFactory) {
                Assert.assertTrue((recordFactory instanceof NotSupportedRecordFactory));
                return outputRecord;
            }
        });
        ct.execute(interactionSpec, inputRecord);
        Mockito.verify(interaction).execute(interactionSpec, inputRecord, outputRecord);
        Mockito.verify(interaction).close();
        Mockito.verify(connection).close();
    }

    @Test
    public void testTemplateExecuteInputTrueWithCreator2() throws ResourceException {
        ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        RecordFactory recordFactory = Mockito.mock(RecordFactory.class);
        Connection connection = Mockito.mock(Connection.class);
        Interaction interaction = Mockito.mock(Interaction.class);
        RecordCreator creator = Mockito.mock(RecordCreator.class);
        Record inputRecord = Mockito.mock(Record.class);
        final Record outputRecord = Mockito.mock(Record.class);
        InteractionSpec interactionSpec = Mockito.mock(InteractionSpec.class);
        BDDMockito.given(connectionFactory.getConnection()).willReturn(connection);
        BDDMockito.given(connectionFactory.getRecordFactory()).willReturn(recordFactory);
        BDDMockito.given(connection.createInteraction()).willReturn(interaction);
        BDDMockito.given(creator.createRecord(recordFactory)).willReturn(outputRecord);
        BDDMockito.given(interaction.execute(interactionSpec, inputRecord, outputRecord)).willReturn(true);
        CciTemplate ct = new CciTemplate(connectionFactory);
        ct.setOutputRecordCreator(creator);
        ct.execute(interactionSpec, inputRecord);
        Mockito.verify(interaction).execute(interactionSpec, inputRecord, outputRecord);
        Mockito.verify(interaction).close();
        Mockito.verify(connection).close();
    }

    @Test
    public void testTemplateExecuteInputFalse() throws ResourceException {
        ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        Connection connection = Mockito.mock(Connection.class);
        Interaction interaction = Mockito.mock(Interaction.class);
        Record inputRecord = Mockito.mock(Record.class);
        Record outputRecord = Mockito.mock(Record.class);
        InteractionSpec interactionSpec = Mockito.mock(InteractionSpec.class);
        BDDMockito.given(connectionFactory.getConnection()).willReturn(connection);
        BDDMockito.given(connection.createInteraction()).willReturn(interaction);
        BDDMockito.given(interaction.execute(interactionSpec, inputRecord)).willReturn(outputRecord);
        CciTemplate ct = new CciTemplate(connectionFactory);
        ct.execute(interactionSpec, inputRecord);
        Mockito.verify(interaction).execute(interactionSpec, inputRecord);
        Mockito.verify(interaction).close();
        Mockito.verify(connection).close();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testTemplateExecuteInputExtractorTrueWithCreator() throws SQLException, ResourceException {
        ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        RecordFactory recordFactory = Mockito.mock(RecordFactory.class);
        Connection connection = Mockito.mock(Connection.class);
        Interaction interaction = Mockito.mock(Interaction.class);
        RecordExtractor<Object> extractor = Mockito.mock(RecordExtractor.class);
        RecordCreator creator = Mockito.mock(RecordCreator.class);
        Record inputRecord = Mockito.mock(Record.class);
        Record outputRecord = Mockito.mock(Record.class);
        InteractionSpec interactionSpec = Mockito.mock(InteractionSpec.class);
        BDDMockito.given(connectionFactory.getConnection()).willReturn(connection);
        BDDMockito.given(connection.createInteraction()).willReturn(interaction);
        BDDMockito.given(connectionFactory.getRecordFactory()).willReturn(recordFactory);
        BDDMockito.given(creator.createRecord(recordFactory)).willReturn(outputRecord);
        BDDMockito.given(interaction.execute(interactionSpec, inputRecord, outputRecord)).willReturn(true);
        BDDMockito.given(extractor.extractData(outputRecord)).willReturn(new Object());
        CciTemplate ct = new CciTemplate(connectionFactory);
        ct.setOutputRecordCreator(creator);
        ct.execute(interactionSpec, inputRecord, extractor);
        Mockito.verify(extractor).extractData(outputRecord);
        Mockito.verify(interaction).close();
        Mockito.verify(connection).close();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testTemplateExecuteInputExtractorFalse() throws SQLException, ResourceException {
        ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        Connection connection = Mockito.mock(Connection.class);
        Interaction interaction = Mockito.mock(Interaction.class);
        RecordExtractor<Object> extractor = Mockito.mock(RecordExtractor.class);
        Record inputRecord = Mockito.mock(Record.class);
        Record outputRecord = Mockito.mock(Record.class);
        InteractionSpec interactionSpec = Mockito.mock(InteractionSpec.class);
        BDDMockito.given(connectionFactory.getConnection()).willReturn(connection);
        BDDMockito.given(connection.createInteraction()).willReturn(interaction);
        BDDMockito.given(interaction.execute(interactionSpec, inputRecord)).willReturn(outputRecord);
        BDDMockito.given(extractor.extractData(outputRecord)).willReturn(new Object());
        CciTemplate ct = new CciTemplate(connectionFactory);
        ct.execute(interactionSpec, inputRecord, extractor);
        Mockito.verify(extractor).extractData(outputRecord);
        Mockito.verify(interaction).close();
        Mockito.verify(connection).close();
    }

    @Test
    public void testTemplateExecuteInputGeneratorTrueWithCreator() throws ResourceException {
        ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        RecordFactory recordFactory = Mockito.mock(RecordFactory.class);
        Connection connection = Mockito.mock(Connection.class);
        Interaction interaction = Mockito.mock(Interaction.class);
        RecordCreator generator = Mockito.mock(RecordCreator.class);
        RecordCreator creator = Mockito.mock(RecordCreator.class);
        Record inputRecord = Mockito.mock(Record.class);
        Record outputRecord = Mockito.mock(Record.class);
        InteractionSpec interactionSpec = Mockito.mock(InteractionSpec.class);
        BDDMockito.given(connectionFactory.getRecordFactory()).willReturn(recordFactory);
        BDDMockito.given(generator.createRecord(recordFactory)).willReturn(inputRecord);
        BDDMockito.given(connectionFactory.getConnection()).willReturn(connection);
        BDDMockito.given(connection.createInteraction()).willReturn(interaction);
        BDDMockito.given(creator.createRecord(recordFactory)).willReturn(outputRecord);
        BDDMockito.given(connectionFactory.getRecordFactory()).willReturn(recordFactory);
        BDDMockito.given(interaction.execute(interactionSpec, inputRecord, outputRecord)).willReturn(true);
        CciTemplate ct = new CciTemplate(connectionFactory);
        ct.setOutputRecordCreator(creator);
        ct.execute(interactionSpec, generator);
        Mockito.verify(interaction).execute(interactionSpec, inputRecord, outputRecord);
        Mockito.verify(interaction).close();
        Mockito.verify(connection).close();
    }

    @Test
    public void testTemplateExecuteInputGeneratorFalse() throws ResourceException {
        ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        RecordFactory recordFactory = Mockito.mock(RecordFactory.class);
        Connection connection = Mockito.mock(Connection.class);
        Interaction interaction = Mockito.mock(Interaction.class);
        RecordCreator generator = Mockito.mock(RecordCreator.class);
        Record inputRecord = Mockito.mock(Record.class);
        Record outputRecord = Mockito.mock(Record.class);
        InteractionSpec interactionSpec = Mockito.mock(InteractionSpec.class);
        BDDMockito.given(connectionFactory.getRecordFactory()).willReturn(recordFactory);
        BDDMockito.given(connectionFactory.getConnection()).willReturn(connection);
        BDDMockito.given(connection.createInteraction()).willReturn(interaction);
        BDDMockito.given(generator.createRecord(recordFactory)).willReturn(inputRecord);
        BDDMockito.given(interaction.execute(interactionSpec, inputRecord)).willReturn(outputRecord);
        CciTemplate ct = new CciTemplate(connectionFactory);
        ct.execute(interactionSpec, generator);
        Mockito.verify(interaction).execute(interactionSpec, inputRecord);
        Mockito.verify(interaction).close();
        Mockito.verify(connection).close();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testTemplateExecuteInputGeneratorExtractorTrueWithCreator() throws SQLException, ResourceException {
        ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        RecordFactory recordFactory = Mockito.mock(RecordFactory.class);
        Connection connection = Mockito.mock(Connection.class);
        Interaction interaction = Mockito.mock(Interaction.class);
        RecordCreator generator = Mockito.mock(RecordCreator.class);
        RecordExtractor<Object> extractor = Mockito.mock(RecordExtractor.class);
        RecordCreator creator = Mockito.mock(RecordCreator.class);
        Record inputRecord = Mockito.mock(Record.class);
        Record outputRecord = Mockito.mock(Record.class);
        Object obj = new Object();
        InteractionSpec interactionSpec = Mockito.mock(InteractionSpec.class);
        BDDMockito.given(connectionFactory.getRecordFactory()).willReturn(recordFactory);
        BDDMockito.given(connectionFactory.getConnection()).willReturn(connection);
        BDDMockito.given(connection.createInteraction()).willReturn(interaction);
        BDDMockito.given(creator.createRecord(recordFactory)).willReturn(outputRecord);
        BDDMockito.given(connectionFactory.getRecordFactory()).willReturn(recordFactory);
        BDDMockito.given(generator.createRecord(recordFactory)).willReturn(inputRecord);
        BDDMockito.given(interaction.execute(interactionSpec, inputRecord, outputRecord)).willReturn(true);
        BDDMockito.given(extractor.extractData(outputRecord)).willReturn(obj);
        CciTemplate ct = new CciTemplate(connectionFactory);
        ct.setOutputRecordCreator(creator);
        Assert.assertEquals(obj, ct.execute(interactionSpec, generator, extractor));
        Mockito.verify(interaction).close();
        Mockito.verify(connection).close();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testTemplateExecuteInputGeneratorExtractorFalse() throws SQLException, ResourceException {
        ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        RecordFactory recordFactory = Mockito.mock(RecordFactory.class);
        Connection connection = Mockito.mock(Connection.class);
        Interaction interaction = Mockito.mock(Interaction.class);
        RecordCreator generator = Mockito.mock(RecordCreator.class);
        RecordExtractor<Object> extractor = Mockito.mock(RecordExtractor.class);
        Record inputRecord = Mockito.mock(Record.class);
        Record outputRecord = Mockito.mock(Record.class);
        InteractionSpec interactionSpec = Mockito.mock(InteractionSpec.class);
        BDDMockito.given(connectionFactory.getRecordFactory()).willReturn(recordFactory);
        BDDMockito.given(connectionFactory.getConnection()).willReturn(connection);
        BDDMockito.given(connection.createInteraction()).willReturn(interaction);
        BDDMockito.given(generator.createRecord(recordFactory)).willReturn(inputRecord);
        BDDMockito.given(interaction.execute(interactionSpec, inputRecord)).willReturn(outputRecord);
        BDDMockito.given(extractor.extractData(outputRecord)).willReturn(new Object());
        CciTemplate ct = new CciTemplate(connectionFactory);
        ct.execute(interactionSpec, generator, extractor);
        Mockito.verify(extractor).extractData(outputRecord);
        Mockito.verify(interaction).close();
        Mockito.verify(connection).close();
    }

    @Test
    public void testTemplateExecuteInputOutputConnectionSpec() throws ResourceException {
        ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        ConnectionSpec connectionSpec = Mockito.mock(ConnectionSpec.class);
        Connection connection = Mockito.mock(Connection.class);
        Interaction interaction = Mockito.mock(Interaction.class);
        Record inputRecord = Mockito.mock(Record.class);
        Record outputRecord = Mockito.mock(Record.class);
        InteractionSpec interactionSpec = Mockito.mock(InteractionSpec.class);
        BDDMockito.given(connectionFactory.getConnection(connectionSpec)).willReturn(connection);
        BDDMockito.given(connection.createInteraction()).willReturn(interaction);
        BDDMockito.given(interaction.execute(interactionSpec, inputRecord, outputRecord)).willReturn(true);
        ConnectionSpecConnectionFactoryAdapter adapter = new ConnectionSpecConnectionFactoryAdapter();
        adapter.setTargetConnectionFactory(connectionFactory);
        adapter.setConnectionSpec(connectionSpec);
        CciTemplate ct = new CciTemplate(adapter);
        ct.execute(interactionSpec, inputRecord, outputRecord);
        Mockito.verify(interaction).execute(interactionSpec, inputRecord, outputRecord);
        Mockito.verify(interaction).close();
        Mockito.verify(connection).close();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testTemplateExecuteInputOutputResultsSetFalse() throws SQLException, ResourceException {
        ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        RecordFactory recordFactory = Mockito.mock(RecordFactory.class);
        Connection connection = Mockito.mock(Connection.class);
        Interaction interaction = Mockito.mock(Interaction.class);
        Record record = Mockito.mock(Record.class);
        ResultSet resultset = Mockito.mock(ResultSet.class);
        RecordCreator generator = Mockito.mock(RecordCreator.class);
        RecordExtractor<Object> extractor = Mockito.mock(RecordExtractor.class);
        InteractionSpec interactionSpec = Mockito.mock(InteractionSpec.class);
        BDDMockito.given(connectionFactory.getRecordFactory()).willReturn(recordFactory);
        BDDMockito.given(connectionFactory.getConnection()).willReturn(connection);
        BDDMockito.given(connection.createInteraction()).willReturn(interaction);
        BDDMockito.given(generator.createRecord(recordFactory)).willReturn(record);
        BDDMockito.given(interaction.execute(interactionSpec, record)).willReturn(resultset);
        BDDMockito.given(extractor.extractData(resultset)).willReturn(new Object());
        CciTemplate ct = new CciTemplate(connectionFactory);
        ct.execute(interactionSpec, generator, extractor);
        Mockito.verify(extractor).extractData(resultset);
        Mockito.verify(resultset).close();
        Mockito.verify(interaction).close();
        Mockito.verify(connection).close();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testTemplateExecuteConnectionCallback() throws SQLException, ResourceException {
        ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        Connection connection = Mockito.mock(Connection.class);
        ConnectionCallback<Object> connectionCallback = Mockito.mock(ConnectionCallback.class);
        BDDMockito.given(connectionFactory.getConnection()).willReturn(connection);
        BDDMockito.given(connectionCallback.doInConnection(connection, connectionFactory)).willReturn(new Object());
        CciTemplate ct = new CciTemplate(connectionFactory);
        ct.execute(connectionCallback);
        Mockito.verify(connectionCallback).doInConnection(connection, connectionFactory);
        Mockito.verify(connection).close();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testTemplateExecuteInteractionCallback() throws SQLException, ResourceException {
        ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        Connection connection = Mockito.mock(Connection.class);
        Interaction interaction = Mockito.mock(Interaction.class);
        InteractionCallback<Object> interactionCallback = Mockito.mock(InteractionCallback.class);
        BDDMockito.given(connectionFactory.getConnection()).willReturn(connection);
        BDDMockito.given(connection.createInteraction()).willReturn(interaction);
        BDDMockito.given(interactionCallback.doInInteraction(interaction, connectionFactory)).willReturn(new Object());
        CciTemplate ct = new CciTemplate(connectionFactory);
        ct.execute(interactionCallback);
        Mockito.verify(interactionCallback).doInInteraction(interaction, connectionFactory);
        Mockito.verify(interaction).close();
        Mockito.verify(connection).close();
    }

    @Test
    public void testTemplateExecuteInputTrueTrueWithCreator() throws ResourceException {
        ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        Connection connection = Mockito.mock(Connection.class);
        Interaction interaction = Mockito.mock(Interaction.class);
        RecordCreator creator = Mockito.mock(RecordCreator.class);
        Record inputOutputRecord = Mockito.mock(Record.class);
        InteractionSpec interactionSpec = Mockito.mock(InteractionSpec.class);
        BDDMockito.given(connectionFactory.getConnection()).willReturn(connection);
        BDDMockito.given(connection.createInteraction()).willReturn(interaction);
        BDDMockito.given(interaction.execute(interactionSpec, inputOutputRecord, inputOutputRecord)).willReturn(true);
        CciTemplate ct = new CciTemplate(connectionFactory);
        ct.setOutputRecordCreator(creator);
        ct.execute(interactionSpec, inputOutputRecord, inputOutputRecord);
        Mockito.verify(interaction).execute(interactionSpec, inputOutputRecord, inputOutputRecord);
        Mockito.verify(interaction).close();
        Mockito.verify(connection).close();
    }

    @Test
    public void testTemplateExecuteInputTrueTrue() throws ResourceException {
        ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        Connection connection = Mockito.mock(Connection.class);
        Interaction interaction = Mockito.mock(Interaction.class);
        Record inputOutputRecord = Mockito.mock(Record.class);
        InteractionSpec interactionSpec = Mockito.mock(InteractionSpec.class);
        BDDMockito.given(connectionFactory.getConnection()).willReturn(connection);
        BDDMockito.given(connection.createInteraction()).willReturn(interaction);
        BDDMockito.given(interaction.execute(interactionSpec, inputOutputRecord, inputOutputRecord)).willReturn(true);
        CciTemplate ct = new CciTemplate(connectionFactory);
        ct.execute(interactionSpec, inputOutputRecord, inputOutputRecord);
        Mockito.verify(interaction).execute(interactionSpec, inputOutputRecord, inputOutputRecord);
        Mockito.verify(interaction).close();
        Mockito.verify(connection).close();
    }

    @Test
    public void testTemplateExecuteInputFalseTrue() throws ResourceException {
        ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        Connection connection = Mockito.mock(Connection.class);
        Interaction interaction = Mockito.mock(Interaction.class);
        Record inputOutputRecord = Mockito.mock(Record.class);
        InteractionSpec interactionSpec = Mockito.mock(InteractionSpec.class);
        BDDMockito.given(connectionFactory.getConnection()).willReturn(connection);
        BDDMockito.given(connection.createInteraction()).willReturn(interaction);
        BDDMockito.given(interaction.execute(interactionSpec, inputOutputRecord)).willReturn(null);
        CciTemplate ct = new CciTemplate(connectionFactory);
        Record tmpOutputRecord = ct.execute(interactionSpec, inputOutputRecord);
        Assert.assertNull(tmpOutputRecord);
        Mockito.verify(interaction).execute(interactionSpec, inputOutputRecord);
        Mockito.verify(interaction).close();
        Mockito.verify(connection).close();
    }
}

