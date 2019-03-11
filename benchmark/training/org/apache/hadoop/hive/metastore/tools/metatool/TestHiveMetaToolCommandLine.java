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
package org.apache.hadoop.hive.metastore.tools.metatool;


import org.apache.commons.cli.ParseException;
import org.apache.hadoop.hive.metastore.annotation.MetastoreUnitTest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;


/**
 * Unit tests for HiveMetaToolCommandLine.
 */
@Category(MetastoreUnitTest.class)
public class TestHiveMetaToolCommandLine {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testParseListFSRoot() throws ParseException {
        HiveMetaToolCommandLine cl = new HiveMetaToolCommandLine(new String[]{ "-listFSRoot" });
        Assert.assertTrue(cl.isListFSRoot());
        Assert.assertFalse(cl.isExecuteJDOQL());
        Assert.assertNull(cl.getJDOQLQuery());
        Assert.assertFalse(cl.isUpdateLocation());
        Assert.assertNull(cl.getUpddateLocationParams());
        Assert.assertFalse(cl.isDryRun());
        Assert.assertNull(cl.getSerdePropKey());
        Assert.assertNull(cl.getTablePropKey());
    }

    @Test
    public void testParseExecuteJDOQL() throws ParseException {
        HiveMetaToolCommandLine cl = new HiveMetaToolCommandLine(new String[]{ "-executeJDOQL", "select a from b" });
        Assert.assertFalse(cl.isListFSRoot());
        Assert.assertTrue(cl.isExecuteJDOQL());
        Assert.assertEquals("select a from b", cl.getJDOQLQuery());
        Assert.assertFalse(cl.isUpdateLocation());
        Assert.assertNull(cl.getUpddateLocationParams());
        Assert.assertFalse(cl.isDryRun());
        Assert.assertNull(cl.getSerdePropKey());
        Assert.assertNull(cl.getTablePropKey());
    }

    @Test
    public void testParseUpdateLocation() throws ParseException {
        String[] args = new String[]{ "-updateLocation", "hdfs://new.loc", "hdfs://old.loc", "-dryRun", "-serdePropKey", "abc", "-tablePropKey", "def" };
        HiveMetaToolCommandLine cl = new HiveMetaToolCommandLine(args);
        Assert.assertFalse(cl.isListFSRoot());
        Assert.assertFalse(cl.isExecuteJDOQL());
        Assert.assertNull(cl.getJDOQLQuery());
        Assert.assertTrue(cl.isUpdateLocation());
        Assert.assertEquals("hdfs://new.loc", cl.getUpddateLocationParams()[0]);
        Assert.assertEquals("hdfs://old.loc", cl.getUpddateLocationParams()[1]);
        Assert.assertTrue(cl.isDryRun());
        Assert.assertEquals("abc", cl.getSerdePropKey());
        Assert.assertEquals("def", cl.getTablePropKey());
    }

    @Test
    public void testNoTask() throws ParseException {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("exectly one of -listFSRoot, -executeJDOQL, -updateLocation must be set");
        new HiveMetaToolCommandLine(new String[]{  });
    }

    @Test
    public void testMultipleTask() throws ParseException {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("exectly one of -listFSRoot, -executeJDOQL, -updateLocation must be set");
        new HiveMetaToolCommandLine(new String[]{ "-listFSRoot", "-executeJDOQL", "select a from b" });
    }

    @Test
    public void testUpdateLocationOneArgument() throws ParseException {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("HiveMetaTool:updateLocation takes in 2 arguments but was passed 1 arguments");
        new HiveMetaToolCommandLine(new String[]{ "-updateLocation", "hdfs://abc.de" });
    }

    @Test
    public void testDryRunNotAllowed() throws ParseException {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("-dryRun, -serdePropKey, -tablePropKey may be used only for the -updateLocation command");
        new HiveMetaToolCommandLine(new String[]{ "-listFSRoot", "-dryRun" });
    }

    @Test
    public void testSerdePropKeyNotAllowed() throws ParseException {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("-dryRun, -serdePropKey, -tablePropKey may be used only for the -updateLocation command");
        new HiveMetaToolCommandLine(new String[]{ "-listFSRoot", "-serdePropKey", "abc" });
    }

    @Test
    public void testTablePropKeyNotAllowed() throws ParseException {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("-dryRun, -serdePropKey, -tablePropKey may be used only for the -updateLocation command");
        new HiveMetaToolCommandLine(new String[]{ "-executeJDOQL", "select a from b", "-tablePropKey", "abc" });
    }
}

