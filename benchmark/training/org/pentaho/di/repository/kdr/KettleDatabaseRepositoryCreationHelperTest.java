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


import Const.KETTLE_PASSWORD_ENCODER_PLUGIN;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.database.OracleDatabaseMeta;
import org.pentaho.di.core.encryption.Encr;
import org.pentaho.di.core.encryption.TwoWayPasswordEncoderPluginType;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannel;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.util.EnvUtil;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;

import static KettleDatabaseRepository.REP_ORACLE_STRING_LENGTH;


/**
 *
 *
 * @author Tatsiana_Kasiankova
 */
public class KettleDatabaseRepositoryCreationHelperTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private static final int EXPECTED_ORACLE_DB_REPO_STRING = 1999;

    private static final int EXPECTED_DEFAULT_DB_REPO_STRING = REP_ORACLE_STRING_LENGTH;

    private KettleDatabaseRepositoryMeta repositoryMeta;

    private KettleDatabaseRepository repository;

    LogChannelInterface log = LogChannel.GENERAL;

    KettleDatabaseRepositoryCreationHelper helper;

    static String INDEX = "INDEX ";

    private KettleDatabaseRepositoryCreationHelperTest.AnswerSecondArgument lan = new KettleDatabaseRepositoryCreationHelperTest.AnswerSecondArgument();

    /**
     * PDI-10237 test index name length.
     *
     * @throws KettleException
     * 		
     */
    @Test
    public void testCreateIndexLenghts() throws KettleException {
        DatabaseMeta meta = Mockito.mock(DatabaseMeta.class);
        Mockito.when(meta.getStartQuote()).thenReturn("");
        Mockito.when(meta.getEndQuote()).thenReturn("");
        Mockito.when(meta.getQuotedSchemaTableCombination(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArguments()[1].toString();
            }
        });
        Mockito.when(meta.getDatabaseInterface()).thenReturn(new OracleDatabaseMeta());
        Database db = Mockito.mock(Database.class);
        Mockito.when(db.getDatabaseMeta()).thenReturn(meta);
        // always return some create sql.
        Mockito.when(db.getDDL(ArgumentMatchers.anyString(), ArgumentMatchers.any(RowMetaInterface.class), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean())).thenReturn("### CREATE TABLE;");
        Mockito.when(repository.getDatabase()).thenReturn(db);
        Mockito.when(repository.getDatabaseMeta()).thenReturn(meta);
        Mockito.when(db.getCreateIndexStatement(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(String[].class), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean())).thenAnswer(lan);
        KettleDatabaseRepositoryCreationHelper helper = new KettleDatabaseRepositoryCreationHelper(repository);
        PluginRegistry.addPluginType(TwoWayPasswordEncoderPluginType.getInstance());
        PluginRegistry.init(false);
        String passwordEncoderPluginID = Const.NVL(EnvUtil.getSystemProperty(KETTLE_PASSWORD_ENCODER_PLUGIN), "Kettle");
        Encr.init(passwordEncoderPluginID);
        List<String> statements = new ArrayList<String>();
        helper.createRepositorySchema(null, false, statements, true);
        for (String st : statements) {
            if ((st == null) || (st.startsWith("#"))) {
                continue;
            }
            Assert.assertTrue(("Index name is not overlenght!: " + st), ((st.length()) <= 30));
        }
    }

    @Test
    public void testOracleDBRepoStringLength() throws Exception {
        KettleEnvironment.init();
        DatabaseMeta databaseMeta = new DatabaseMeta("OraRepo", "ORACLE", "JDBC", null, "test", null, null, null);
        repositoryMeta = new KettleDatabaseRepositoryMeta("KettleDatabaseRepository", "OraRepo", "Ora Repository", databaseMeta);
        repository = new KettleDatabaseRepository();
        repository.init(repositoryMeta);
        KettleDatabaseRepositoryCreationHelper helper = new KettleDatabaseRepositoryCreationHelper(repository);
        int repoStringLength = helper.getRepoStringLength();
        Assert.assertEquals(KettleDatabaseRepositoryCreationHelperTest.EXPECTED_ORACLE_DB_REPO_STRING, repoStringLength);
    }

    @Test
    public void testDefaultDBRepoStringLength() throws Exception {
        KettleEnvironment.init();
        DatabaseMeta databaseMeta = new DatabaseMeta();
        databaseMeta.setDatabaseInterface(new KettleDatabaseRepositoryCreationHelperTest.TestDatabaseMeta());
        repositoryMeta = new KettleDatabaseRepositoryMeta("KettleDatabaseRepository", "TestRepo", "Test Repository", databaseMeta);
        repository = new KettleDatabaseRepository();
        repository.init(repositoryMeta);
        KettleDatabaseRepositoryCreationHelper helper = new KettleDatabaseRepositoryCreationHelper(repository);
        int repoStringLength = helper.getRepoStringLength();
        Assert.assertEquals(KettleDatabaseRepositoryCreationHelperTest.EXPECTED_DEFAULT_DB_REPO_STRING, repoStringLength);
    }

    class TestDatabaseMeta extends OracleDatabaseMeta {
        @Override
        public int getMaxVARCHARLength() {
            return 1;
        }
    }

    static class AnswerSecondArgument implements Answer<String> {
        @Override
        public String answer(InvocationOnMock invocation) throws Throwable {
            if ((invocation.getArguments().length) < 2) {
                throw new RuntimeException("no cookies!");
            }
            return String.valueOf(invocation.getArguments()[1]);
        }
    }
}

