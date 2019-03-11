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
package org.apache.hadoop.hive.ql.security;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import junit.framework.TestCase;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.SerDeInfo;
import org.apache.hadoop.hive.metastore.api.StorageDescriptor;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.ql.IDriver;
import org.apache.hadoop.hive.ql.io.HiveInputFormat;
import org.apache.hadoop.hive.ql.io.HiveOutputFormat;
import org.apache.hadoop.hive.ql.processors.CommandProcessorResponse;
import org.apache.hadoop.hive.serde.serdeConstants;
import org.apache.hadoop.hive.serde.serdeConstants.SERIALIZATION_FORMAT;
import org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * TestHiveMetastoreAuthorizationProvider. Test case for
 * HiveMetastoreAuthorizationProvider, and by default,
 * for DefaultHiveMetaStoreAuthorizationProvider
 * using {@link org.apache.hadoop.hive.metastore.AuthorizationPreEventListener}
 * and {@link org.apache.hadoop.hive.}
 *
 * Note that while we do use the hive driver to test, that is mostly for test
 * writing ease, and it has the same effect as using a metastore client directly
 * because we disable hive client-side authorization for this test, and only
 * turn on server-side auth.
 *
 * This test is also intended to be extended to provide tests for other
 * authorization providers like StorageBasedAuthorizationProvider
 */
public class TestMetastoreAuthorizationProvider extends TestCase {
    private static final Logger LOG = LoggerFactory.getLogger(TestMetastoreAuthorizationProvider.class);

    protected HiveConf clientHiveConf;

    protected HiveMetaStoreClient msc;

    protected IDriver driver;

    protected UserGroupInformation ugi;

    public void testSimplePrivileges() throws Exception {
        if (!(isTestEnabled())) {
            System.out.println(("Skipping test " + (this.getClass().getName())));
            return;
        }
        String dbName = getTestDbName();
        String tblName = getTestTableName();
        String userName = setupUser();
        allowCreateDatabase(userName);
        CommandProcessorResponse ret = driver.run(("create database " + dbName));
        TestCase.assertEquals(0, ret.getResponseCode());
        Database db = msc.getDatabase(dbName);
        String dbLocn = db.getLocationUri();
        validateCreateDb(db, dbName);
        disallowCreateInDb(dbName, userName, dbLocn);
        disallowCreateDatabase(userName);
        driver.run(("use " + dbName));
        ret = driver.run(String.format("create table %s (a string) partitioned by (b string)", tblName));
        TestCase.assertEquals(1, ret.getResponseCode());
        // Even if table location is specified table creation should fail
        String tblNameLoc = tblName + "_loc";
        String tblLocation = ((new Path(dbLocn).getParent().toUri()) + "/") + tblNameLoc;
        driver.run(("use " + dbName));
        ret = driver.run(String.format((("create table %s (a string) partitioned by (b string) location '" + tblLocation) + "'"), tblNameLoc));
        TestCase.assertEquals(1, ret.getResponseCode());
        // failure from not having permissions to create table
        ArrayList<FieldSchema> fields = new ArrayList<FieldSchema>(2);
        fields.add(new FieldSchema("a", serdeConstants.STRING_TYPE_NAME, ""));
        Table ttbl = new Table();
        ttbl.setDbName(dbName);
        ttbl.setTableName(tblName);
        StorageDescriptor sd = new StorageDescriptor();
        ttbl.setSd(sd);
        sd.setCols(fields);
        sd.setParameters(new HashMap<String, String>());
        sd.getParameters().put("test_param_1", "Use this for comments etc");
        sd.setSerdeInfo(new SerDeInfo());
        sd.getSerdeInfo().setName(ttbl.getTableName());
        sd.getSerdeInfo().setParameters(new HashMap<String, String>());
        sd.getSerdeInfo().getParameters().put(SERIALIZATION_FORMAT, "1");
        sd.getSerdeInfo().setSerializationLib(LazySimpleSerDe.class.getName());
        sd.setInputFormat(HiveInputFormat.class.getName());
        sd.setOutputFormat(HiveOutputFormat.class.getName());
        ttbl.setPartitionKeys(new ArrayList<FieldSchema>());
        MetaException me = null;
        try {
            msc.createTable(ttbl);
        } catch (MetaException e) {
            me = e;
        }
        assertNoPrivileges(me);
        allowCreateInDb(dbName, userName, dbLocn);
        driver.run(("use " + dbName));
        ret = driver.run(String.format("create table %s (a string) partitioned by (b string)", tblName));
        TestCase.assertEquals(0, ret.getResponseCode());// now it succeeds.

        Table tbl = msc.getTable(dbName, tblName);
        Assert.assertTrue(tbl.isSetId());
        tbl.unsetId();
        validateCreateTable(tbl, tblName, dbName);
        // Table creation should succeed even if location is specified
        driver.run(("use " + dbName));
        ret = driver.run(String.format((("create table %s (a string) partitioned by (b string) location '" + tblLocation) + "'"), tblNameLoc));
        TestCase.assertEquals(0, ret.getResponseCode());
        Table tblLoc = msc.getTable(dbName, tblNameLoc);
        validateCreateTable(tblLoc, tblNameLoc, dbName);
        String fakeUser = "mal";
        List<String> fakeGroupNames = new ArrayList<String>();
        fakeGroupNames.add("groupygroup");
        InjectableDummyAuthenticator.injectUserName(fakeUser);
        InjectableDummyAuthenticator.injectGroupNames(fakeGroupNames);
        InjectableDummyAuthenticator.injectMode(true);
        ret = driver.run(String.format("create table %s (a string) partitioned by (b string)", (tblName + "mal")));
        TestCase.assertEquals(1, ret.getResponseCode());
        ttbl.setTableName((tblName + "mal"));
        me = null;
        try {
            msc.createTable(ttbl);
        } catch (MetaException e) {
            me = e;
        }
        assertNoPrivileges(me);
        disallowCreateInTbl(tbl.getTableName(), userName, tbl.getSd().getLocation());
        ret = driver.run((("alter table " + tblName) + " add partition (b='2011')"));
        TestCase.assertEquals(1, ret.getResponseCode());
        List<String> ptnVals = new ArrayList<String>();
        ptnVals.add("b=2011");
        Partition tpart = new Partition();
        tpart.setDbName(dbName);
        tpart.setTableName(tblName);
        tpart.setValues(ptnVals);
        tpart.setParameters(new HashMap<String, String>());
        tpart.setSd(tbl.getSd().deepCopy());
        tpart.getSd().setSerdeInfo(tbl.getSd().getSerdeInfo().deepCopy());
        tpart.getSd().setLocation(((tbl.getSd().getLocation()) + "/tpart"));
        me = null;
        try {
            msc.add_partition(tpart);
        } catch (MetaException e) {
            me = e;
        }
        assertNoPrivileges(me);
        InjectableDummyAuthenticator.injectMode(false);
        allowCreateInTbl(tbl.getTableName(), userName, tbl.getSd().getLocation());
        ret = driver.run((("alter table " + tblName) + " add partition (b='2011')"));
        TestCase.assertEquals(0, ret.getResponseCode());
        allowDropOnTable(tblName, userName, tbl.getSd().getLocation());
        allowDropOnDb(dbName, userName, db.getLocationUri());
        ret = driver.run((("drop database if exists " + (getTestDbName())) + " cascade"));
        TestCase.assertEquals(0, ret.getResponseCode());
        InjectableDummyAuthenticator.injectUserName(userName);
        InjectableDummyAuthenticator.injectGroupNames(Arrays.asList(ugi.getGroupNames()));
        InjectableDummyAuthenticator.injectMode(true);
        allowCreateDatabase(userName);
        driver.run(("create database " + dbName));
        allowCreateInDb(dbName, userName, dbLocn);
        tbl.setTableType("EXTERNAL_TABLE");
        msc.createTable(tbl);
        disallowDropOnTable(tblName, userName, tbl.getSd().getLocation());
        ret = driver.run(("drop table " + (tbl.getTableName())));
        TestCase.assertEquals(1, ret.getResponseCode());
    }
}

