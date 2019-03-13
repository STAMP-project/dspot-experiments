/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.repository.kdr;


import KettleDatabaseRepository.FIELD_CLUSTER_SLAVE_ID_CLUSTER;
import KettleDatabaseRepository.FIELD_CLUSTER_SLAVE_ID_CLUSTER_SLAVE;
import KettleDatabaseRepository.FIELD_CLUSTER_SLAVE_ID_SLAVE;
import KettleDatabaseRepository.FIELD_JOBENTRY_DATABASE_ID_DATABASE;
import KettleDatabaseRepository.FIELD_JOBENTRY_DATABASE_ID_JOB;
import KettleDatabaseRepository.FIELD_JOBENTRY_DATABASE_ID_JOBENTRY;
import KettleDatabaseRepository.FIELD_JOB_NOTE_ID_JOB;
import KettleDatabaseRepository.FIELD_JOB_NOTE_ID_NOTE;
import KettleDatabaseRepository.FIELD_REPOSITORY_LOG_ID_REPOSITORY_LOG;
import KettleDatabaseRepository.FIELD_REPOSITORY_LOG_LOG_DATE;
import KettleDatabaseRepository.FIELD_REPOSITORY_LOG_LOG_USER;
import KettleDatabaseRepository.FIELD_REPOSITORY_LOG_OPERATION_DESC;
import KettleDatabaseRepository.FIELD_REPOSITORY_LOG_REP_VERSION;
import KettleDatabaseRepository.FIELD_STEP_DATABASE_ID_DATABASE;
import KettleDatabaseRepository.FIELD_STEP_DATABASE_ID_STEP;
import KettleDatabaseRepository.FIELD_STEP_DATABASE_ID_TRANSFORMATION;
import KettleDatabaseRepository.FIELD_TRANS_CLUSTER_ID_CLUSTER;
import KettleDatabaseRepository.FIELD_TRANS_CLUSTER_ID_TRANSFORMATION;
import KettleDatabaseRepository.FIELD_TRANS_CLUSTER_ID_TRANS_CLUSTER;
import KettleDatabaseRepository.FIELD_TRANS_NOTE_ID_NOTE;
import KettleDatabaseRepository.FIELD_TRANS_NOTE_ID_TRANSFORMATION;
import KettleDatabaseRepository.FIELD_TRANS_PARTITION_SCHEMA_ID_PARTITION_SCHEMA;
import KettleDatabaseRepository.FIELD_TRANS_PARTITION_SCHEMA_ID_TRANSFORMATION;
import KettleDatabaseRepository.FIELD_TRANS_PARTITION_SCHEMA_ID_TRANS_PARTITION_SCHEMA;
import KettleDatabaseRepository.FIELD_TRANS_SLAVE_ID_SLAVE;
import KettleDatabaseRepository.FIELD_TRANS_SLAVE_ID_TRANSFORMATION;
import KettleDatabaseRepository.FIELD_TRANS_SLAVE_ID_TRANS_SLAVE;
import KettleDatabaseRepository.FIELD_TRANS_STEP_CONDITION_ID_CONDITION;
import KettleDatabaseRepository.FIELD_TRANS_STEP_CONDITION_ID_STEP;
import KettleDatabaseRepository.FIELD_TRANS_STEP_CONDITION_ID_TRANSFORMATION;
import KettleDatabaseRepository.TABLE_R_CLUSTER_SLAVE;
import KettleDatabaseRepository.TABLE_R_JOBENTRY_DATABASE;
import KettleDatabaseRepository.TABLE_R_JOB_NOTE;
import KettleDatabaseRepository.TABLE_R_REPOSITORY_LOG;
import KettleDatabaseRepository.TABLE_R_STEP_DATABASE;
import KettleDatabaseRepository.TABLE_R_TRANS_CLUSTER;
import KettleDatabaseRepository.TABLE_R_TRANS_NOTE;
import KettleDatabaseRepository.TABLE_R_TRANS_PARTITION_SCHEMA;
import KettleDatabaseRepository.TABLE_R_TRANS_SLAVE;
import KettleDatabaseRepository.TABLE_R_TRANS_STEP_CONDITION;
import ValueMetaInterface.TYPE_DATE;
import ValueMetaInterface.TYPE_INTEGER;
import ValueMetaInterface.TYPE_STRING;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.cluster.ClusterSchema;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.repository.LongObjectId;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.UserInfo;


public class KettleDatabaseRepositoryTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    KettleDatabaseRepository repo;

    @Test
    public void testInsertLogEntry() throws KettleException {
        Mockito.doReturn(new LongObjectId(123)).when(repo.connectionDelegate).getNextLogID();
        Mockito.doReturn("2.4").when(repo.connectionDelegate).getVersion();
        Mockito.doReturn(new UserInfo("John Doe")).when(repo).getUserInfo();
        ArgumentCaptor<String> argumentTableName = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<RowMetaAndData> argumentTableData = ArgumentCaptor.forClass(RowMetaAndData.class);
        Mockito.doNothing().when(repo.connectionDelegate).insertTableRow(argumentTableName.capture(), argumentTableData.capture());
        Date beforeLogEntryDate = Calendar.getInstance().getTime();
        repo.insertLogEntry("testDescription");
        Date afterLogEntryDate = Calendar.getInstance().getTime();
        RowMetaAndData insertRecord = argumentTableData.getValue();
        Assert.assertEquals(TABLE_R_REPOSITORY_LOG, argumentTableName.getValue());
        Assert.assertEquals(5, insertRecord.size());
        Assert.assertEquals(TYPE_INTEGER, insertRecord.getValueMeta(0).getType());
        Assert.assertEquals(FIELD_REPOSITORY_LOG_ID_REPOSITORY_LOG, insertRecord.getValueMeta(0).getName());
        Assert.assertEquals(Long.valueOf(123), insertRecord.getInteger(0));
        Assert.assertEquals(TYPE_STRING, insertRecord.getValueMeta(1).getType());
        Assert.assertEquals(FIELD_REPOSITORY_LOG_REP_VERSION, insertRecord.getValueMeta(1).getName());
        Assert.assertEquals("2.4", insertRecord.getString(1, null));
        Assert.assertEquals(TYPE_DATE, insertRecord.getValueMeta(2).getType());
        Assert.assertEquals(FIELD_REPOSITORY_LOG_LOG_DATE, insertRecord.getValueMeta(2).getName());
        Assert.assertTrue((((beforeLogEntryDate.compareTo(insertRecord.getDate(2, new Date(Long.MIN_VALUE)))) <= 0) && ((afterLogEntryDate.compareTo(insertRecord.getDate(2, new Date(Long.MIN_VALUE)))) >= 0)));
        Assert.assertEquals(TYPE_STRING, insertRecord.getValueMeta(3).getType());
        Assert.assertEquals(FIELD_REPOSITORY_LOG_LOG_USER, insertRecord.getValueMeta(3).getName());
        Assert.assertEquals("John Doe", insertRecord.getString(3, null));
        Assert.assertEquals(TYPE_STRING, insertRecord.getValueMeta(4).getType());
        Assert.assertEquals(FIELD_REPOSITORY_LOG_OPERATION_DESC, insertRecord.getValueMeta(4).getName());
        Assert.assertEquals("testDescription", insertRecord.getString(4, null));
    }

    @Test
    public void testInsertTransNote() throws KettleException {
        ArgumentCaptor<String> argumentTableName = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<RowMetaAndData> argumentTableData = ArgumentCaptor.forClass(RowMetaAndData.class);
        Mockito.doNothing().when(repo.connectionDelegate).insertTableRow(argumentTableName.capture(), argumentTableData.capture());
        repo.insertTransNote(new LongObjectId(456), new LongObjectId(789));
        RowMetaAndData insertRecord = argumentTableData.getValue();
        Assert.assertEquals(TABLE_R_TRANS_NOTE, argumentTableName.getValue());
        Assert.assertEquals(2, insertRecord.size());
        Assert.assertEquals(TYPE_INTEGER, insertRecord.getValueMeta(0).getType());
        Assert.assertEquals(FIELD_TRANS_NOTE_ID_TRANSFORMATION, insertRecord.getValueMeta(0).getName());
        Assert.assertEquals(Long.valueOf(456), insertRecord.getInteger(0));
        Assert.assertEquals(TYPE_INTEGER, insertRecord.getValueMeta(1).getType());
        Assert.assertEquals(FIELD_TRANS_NOTE_ID_NOTE, insertRecord.getValueMeta(1).getName());
        Assert.assertEquals(Long.valueOf(789), insertRecord.getInteger(1));
    }

    @Test
    public void testInsertJobNote() throws KettleException {
        ArgumentCaptor<String> argumentTableName = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<RowMetaAndData> argumentTableData = ArgumentCaptor.forClass(RowMetaAndData.class);
        Mockito.doNothing().when(repo.connectionDelegate).insertTableRow(argumentTableName.capture(), argumentTableData.capture());
        repo.insertJobNote(new LongObjectId(234), new LongObjectId(567));
        RowMetaAndData insertRecord = argumentTableData.getValue();
        Assert.assertEquals(TABLE_R_JOB_NOTE, argumentTableName.getValue());
        Assert.assertEquals(2, insertRecord.size());
        Assert.assertEquals(TYPE_INTEGER, insertRecord.getValueMeta(0).getType());
        Assert.assertEquals(FIELD_JOB_NOTE_ID_JOB, insertRecord.getValueMeta(0).getName());
        Assert.assertEquals(Long.valueOf(234), insertRecord.getInteger(0));
        Assert.assertEquals(TYPE_INTEGER, insertRecord.getValueMeta(1).getType());
        Assert.assertEquals(FIELD_JOB_NOTE_ID_NOTE, insertRecord.getValueMeta(1).getName());
        Assert.assertEquals(Long.valueOf(567), insertRecord.getInteger(1));
    }

    @Test
    public void testInsertStepDatabase() throws KettleException {
        Mockito.doReturn(getNullIntegerRow()).when(repo.connectionDelegate).getOneRow(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(ObjectId.class));
        ArgumentCaptor<String> argumentTableName = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<RowMetaAndData> argumentTableData = ArgumentCaptor.forClass(RowMetaAndData.class);
        Mockito.doNothing().when(repo.connectionDelegate).insertTableRow(argumentTableName.capture(), argumentTableData.capture());
        repo.insertStepDatabase(new LongObjectId(654), new LongObjectId(765), new LongObjectId(876));
        RowMetaAndData insertRecord = argumentTableData.getValue();
        Assert.assertEquals(TABLE_R_STEP_DATABASE, argumentTableName.getValue());
        Assert.assertEquals(3, insertRecord.size());
        Assert.assertEquals(TYPE_INTEGER, insertRecord.getValueMeta(0).getType());
        Assert.assertEquals(FIELD_STEP_DATABASE_ID_TRANSFORMATION, insertRecord.getValueMeta(0).getName());
        Assert.assertEquals(Long.valueOf(654), insertRecord.getInteger(0));
        Assert.assertEquals(TYPE_INTEGER, insertRecord.getValueMeta(1).getType());
        Assert.assertEquals(FIELD_STEP_DATABASE_ID_STEP, insertRecord.getValueMeta(1).getName());
        Assert.assertEquals(Long.valueOf(765), insertRecord.getInteger(1));
        Assert.assertEquals(TYPE_INTEGER, insertRecord.getValueMeta(2).getType());
        Assert.assertEquals(FIELD_STEP_DATABASE_ID_DATABASE, insertRecord.getValueMeta(2).getName());
        Assert.assertEquals(Long.valueOf(876), insertRecord.getInteger(2));
    }

    @Test
    public void testInsertJobEntryDatabase() throws KettleException {
        Mockito.doReturn(getNullIntegerRow()).when(repo.connectionDelegate).getOneRow(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(ObjectId.class));
        ArgumentCaptor<String> argumentTableName = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<RowMetaAndData> argumentTableData = ArgumentCaptor.forClass(RowMetaAndData.class);
        Mockito.doNothing().when(repo.connectionDelegate).insertTableRow(argumentTableName.capture(), argumentTableData.capture());
        repo.insertJobEntryDatabase(new LongObjectId(234), new LongObjectId(345), new LongObjectId(456));
        RowMetaAndData insertRecord = argumentTableData.getValue();
        Assert.assertEquals(TABLE_R_JOBENTRY_DATABASE, argumentTableName.getValue());
        Assert.assertEquals(3, insertRecord.size());
        Assert.assertEquals(TYPE_INTEGER, insertRecord.getValueMeta(0).getType());
        Assert.assertEquals(FIELD_JOBENTRY_DATABASE_ID_JOB, insertRecord.getValueMeta(0).getName());
        Assert.assertEquals(Long.valueOf(234), insertRecord.getInteger(0));
        Assert.assertEquals(TYPE_INTEGER, insertRecord.getValueMeta(1).getType());
        Assert.assertEquals(FIELD_JOBENTRY_DATABASE_ID_JOBENTRY, insertRecord.getValueMeta(1).getName());
        Assert.assertEquals(Long.valueOf(345), insertRecord.getInteger(1));
        Assert.assertEquals(TYPE_INTEGER, insertRecord.getValueMeta(2).getType());
        Assert.assertEquals(FIELD_JOBENTRY_DATABASE_ID_DATABASE, insertRecord.getValueMeta(2).getName());
        Assert.assertEquals(Long.valueOf(456), insertRecord.getInteger(2));
    }

    @Test
    public void testInsertTransformationPartitionSchema() throws KettleException {
        ArgumentCaptor<String> argumentTableName = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<RowMetaAndData> argumentTableData = ArgumentCaptor.forClass(RowMetaAndData.class);
        Mockito.doNothing().when(repo.connectionDelegate).insertTableRow(argumentTableName.capture(), argumentTableData.capture());
        Mockito.doReturn(new LongObjectId(456)).when(repo.connectionDelegate).getNextTransformationPartitionSchemaID();
        ObjectId result = repo.insertTransformationPartitionSchema(new LongObjectId(147), new LongObjectId(258));
        RowMetaAndData insertRecord = argumentTableData.getValue();
        Assert.assertEquals(TABLE_R_TRANS_PARTITION_SCHEMA, argumentTableName.getValue());
        Assert.assertEquals(3, insertRecord.size());
        Assert.assertEquals(TYPE_INTEGER, insertRecord.getValueMeta(0).getType());
        Assert.assertEquals(FIELD_TRANS_PARTITION_SCHEMA_ID_TRANS_PARTITION_SCHEMA, insertRecord.getValueMeta(0).getName());
        Assert.assertEquals(Long.valueOf(456), insertRecord.getInteger(0));
        Assert.assertEquals(TYPE_INTEGER, insertRecord.getValueMeta(1).getType());
        Assert.assertEquals(FIELD_TRANS_PARTITION_SCHEMA_ID_TRANSFORMATION, insertRecord.getValueMeta(1).getName());
        Assert.assertEquals(Long.valueOf(147), insertRecord.getInteger(1));
        Assert.assertEquals(TYPE_INTEGER, insertRecord.getValueMeta(2).getType());
        Assert.assertEquals(FIELD_TRANS_PARTITION_SCHEMA_ID_PARTITION_SCHEMA, insertRecord.getValueMeta(2).getName());
        Assert.assertEquals(Long.valueOf(258), insertRecord.getInteger(2));
        Assert.assertEquals(new LongObjectId(456), result);
    }

    @Test
    public void testInsertClusterSlave() throws KettleException {
        ArgumentCaptor<String> argumentTableName = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<RowMetaAndData> argumentTableData = ArgumentCaptor.forClass(RowMetaAndData.class);
        Mockito.doNothing().when(repo.connectionDelegate).insertTableRow(argumentTableName.capture(), argumentTableData.capture());
        Mockito.doReturn(new LongObjectId(357)).when(repo.connectionDelegate).getNextClusterSlaveID();
        SlaveServer testSlave = new SlaveServer("slave1", "fakelocal", "9081", "fakeuser", "fakepass");
        testSlave.setObjectId(new LongObjectId(864));
        ClusterSchema testSchema = new ClusterSchema("schema1", Arrays.asList(testSlave));
        testSchema.setObjectId(new LongObjectId(159));
        ObjectId result = repo.insertClusterSlave(testSchema, testSlave);
        RowMetaAndData insertRecord = argumentTableData.getValue();
        Assert.assertEquals(TABLE_R_CLUSTER_SLAVE, argumentTableName.getValue());
        Assert.assertEquals(3, insertRecord.size());
        Assert.assertEquals(TYPE_INTEGER, insertRecord.getValueMeta(0).getType());
        Assert.assertEquals(FIELD_CLUSTER_SLAVE_ID_CLUSTER_SLAVE, insertRecord.getValueMeta(0).getName());
        Assert.assertEquals(Long.valueOf(357), insertRecord.getInteger(0));
        Assert.assertEquals(TYPE_INTEGER, insertRecord.getValueMeta(1).getType());
        Assert.assertEquals(FIELD_CLUSTER_SLAVE_ID_CLUSTER, insertRecord.getValueMeta(1).getName());
        Assert.assertEquals(Long.valueOf(159), insertRecord.getInteger(1));
        Assert.assertEquals(TYPE_INTEGER, insertRecord.getValueMeta(2).getType());
        Assert.assertEquals(FIELD_CLUSTER_SLAVE_ID_SLAVE, insertRecord.getValueMeta(2).getName());
        Assert.assertEquals(Long.valueOf(864), insertRecord.getInteger(2));
        Assert.assertEquals(new LongObjectId(357), result);
    }

    @Test
    public void testInsertTransformationCluster() throws KettleException {
        ArgumentCaptor<String> argumentTableName = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<RowMetaAndData> argumentTableData = ArgumentCaptor.forClass(RowMetaAndData.class);
        Mockito.doNothing().when(repo.connectionDelegate).insertTableRow(argumentTableName.capture(), argumentTableData.capture());
        Mockito.doReturn(new LongObjectId(123)).when(repo.connectionDelegate).getNextTransformationClusterID();
        ObjectId result = repo.insertTransformationCluster(new LongObjectId(456), new LongObjectId(789));
        RowMetaAndData insertRecord = argumentTableData.getValue();
        Assert.assertEquals(TABLE_R_TRANS_CLUSTER, argumentTableName.getValue());
        Assert.assertEquals(3, insertRecord.size());
        Assert.assertEquals(TYPE_INTEGER, insertRecord.getValueMeta(0).getType());
        Assert.assertEquals(FIELD_TRANS_CLUSTER_ID_TRANS_CLUSTER, insertRecord.getValueMeta(0).getName());
        Assert.assertEquals(Long.valueOf(123), insertRecord.getInteger(0));
        Assert.assertEquals(TYPE_INTEGER, insertRecord.getValueMeta(1).getType());
        Assert.assertEquals(FIELD_TRANS_CLUSTER_ID_TRANSFORMATION, insertRecord.getValueMeta(1).getName());
        Assert.assertEquals(Long.valueOf(456), insertRecord.getInteger(1));
        Assert.assertEquals(TYPE_INTEGER, insertRecord.getValueMeta(2).getType());
        Assert.assertEquals(FIELD_TRANS_CLUSTER_ID_CLUSTER, insertRecord.getValueMeta(2).getName());
        Assert.assertEquals(Long.valueOf(789), insertRecord.getInteger(2));
        Assert.assertEquals(new LongObjectId(123), result);
    }

    @Test
    public void testInsertTransformationSlave() throws KettleException {
        ArgumentCaptor<String> argumentTableName = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<RowMetaAndData> argumentTableData = ArgumentCaptor.forClass(RowMetaAndData.class);
        Mockito.doNothing().when(repo.connectionDelegate).insertTableRow(argumentTableName.capture(), argumentTableData.capture());
        Mockito.doReturn(new LongObjectId(789)).when(repo.connectionDelegate).getNextTransformationSlaveID();
        ObjectId result = repo.insertTransformationSlave(new LongObjectId(456), new LongObjectId(123));
        RowMetaAndData insertRecord = argumentTableData.getValue();
        Assert.assertEquals(TABLE_R_TRANS_SLAVE, argumentTableName.getValue());
        Assert.assertEquals(3, insertRecord.size());
        Assert.assertEquals(TYPE_INTEGER, insertRecord.getValueMeta(0).getType());
        Assert.assertEquals(FIELD_TRANS_SLAVE_ID_TRANS_SLAVE, insertRecord.getValueMeta(0).getName());
        Assert.assertEquals(Long.valueOf(789), insertRecord.getInteger(0));
        Assert.assertEquals(TYPE_INTEGER, insertRecord.getValueMeta(1).getType());
        Assert.assertEquals(FIELD_TRANS_SLAVE_ID_TRANSFORMATION, insertRecord.getValueMeta(1).getName());
        Assert.assertEquals(Long.valueOf(456), insertRecord.getInteger(1));
        Assert.assertEquals(TYPE_INTEGER, insertRecord.getValueMeta(2).getType());
        Assert.assertEquals(FIELD_TRANS_SLAVE_ID_SLAVE, insertRecord.getValueMeta(2).getName());
        Assert.assertEquals(Long.valueOf(123), insertRecord.getInteger(2));
        Assert.assertEquals(new LongObjectId(789), result);
    }

    @Test
    public void testInsertTransStepCondition() throws KettleException {
        ArgumentCaptor<String> argumentTableName = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<RowMetaAndData> argumentTableData = ArgumentCaptor.forClass(RowMetaAndData.class);
        Mockito.doNothing().when(repo.connectionDelegate).insertTableRow(argumentTableName.capture(), argumentTableData.capture());
        repo.insertTransStepCondition(new LongObjectId(234), new LongObjectId(567), new LongObjectId(468));
        RowMetaAndData insertRecord = argumentTableData.getValue();
        Assert.assertEquals(TABLE_R_TRANS_STEP_CONDITION, argumentTableName.getValue());
        Assert.assertEquals(3, insertRecord.size());
        Assert.assertEquals(TYPE_INTEGER, insertRecord.getValueMeta(0).getType());
        Assert.assertEquals(FIELD_TRANS_STEP_CONDITION_ID_TRANSFORMATION, insertRecord.getValueMeta(0).getName());
        Assert.assertEquals(Long.valueOf(234), insertRecord.getInteger(0));
        Assert.assertEquals(TYPE_INTEGER, insertRecord.getValueMeta(1).getType());
        Assert.assertEquals(FIELD_TRANS_STEP_CONDITION_ID_STEP, insertRecord.getValueMeta(1).getName());
        Assert.assertEquals(Long.valueOf(567), insertRecord.getInteger(1));
        Assert.assertEquals(TYPE_INTEGER, insertRecord.getValueMeta(2).getType());
        Assert.assertEquals(FIELD_TRANS_STEP_CONDITION_ID_CONDITION, insertRecord.getValueMeta(2).getName());
        Assert.assertEquals(Long.valueOf(468), insertRecord.getInteger(2));
    }
}

