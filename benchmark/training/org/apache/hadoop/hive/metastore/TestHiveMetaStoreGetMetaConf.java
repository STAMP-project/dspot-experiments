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
package org.apache.hadoop.hive.metastore;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.metastore.annotation.MetastoreUnitTest;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.conf.MetastoreConf.ConfVars;
import org.apache.thrift.TException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category(MetastoreUnitTest.class)
public class TestHiveMetaStoreGetMetaConf {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final Logger LOG = LoggerFactory.getLogger(TestHiveMetaStoreGetMetaConf.class);

    private static Configuration conf;

    private HiveMetaStoreClient hmsc;

    @Test
    public void testGetMetaConfDefault() throws TException {
        ConfVars metaConfVar = ConfVars.TRY_DIRECT_SQL;
        String expected = metaConfVar.getDefaultVal().toString();
        String actual = hmsc.getMetaConf(metaConfVar.toString());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetMetaConfDefaultEmptyString() throws TException {
        ConfVars metaConfVar = ConfVars.PARTITION_NAME_WHITELIST_PATTERN;
        String expected = "";
        String actual = hmsc.getMetaConf(metaConfVar.toString());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetMetaConfOverridden() throws TException {
        ConfVars metaConfVar = ConfVars.TRY_DIRECT_SQL_DDL;
        String expected = "false";
        String actual = hmsc.getMetaConf(metaConfVar.toString());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetMetaConfUnknownPreperty() throws TException {
        String unknownPropertyName = "hive.meta.foo.bar";
        thrown.expect(MetaException.class);
        thrown.expectMessage(("Invalid configuration key " + unknownPropertyName));
        hmsc.getMetaConf(unknownPropertyName);
    }
}

