/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hive.ql;


import Entity.Type.DATABASE;
import Entity.Type.DFS_DIR;
import Entity.Type.FUNCTION;
import Entity.Type.LOCAL_DIR;
import org.apache.hadoop.hive.ql.hooks.WriteEntity;
import org.junit.Assert;
import org.junit.Test;


public class TestCreateUdfEntities {
    private IDriver driver;

    private String funcName = "print_test";

    @Test
    public void testUdfWithLocalResource() throws Exception {
        int rc = driver.compile((((("CREATE FUNCTION " + (funcName)) + " AS 'org.apache.hadoop.hive.ql.udf.generic.GenericUDFPrintf' ") + " using file '") + "file:///tmp/udf1.jar'"));
        Assert.assertEquals(0, rc);
        WriteEntity[] outputEntities = driver.getPlan().getOutputs().toArray(new WriteEntity[]{  });
        Assert.assertEquals(outputEntities.length, 3);
        Assert.assertEquals(DATABASE, outputEntities[0].getType());
        Assert.assertEquals("default", outputEntities[0].getDatabase().getName());
        Assert.assertEquals(FUNCTION, outputEntities[1].getType());
        Assert.assertEquals(funcName, outputEntities[1].getFunctionName());
        Assert.assertEquals(LOCAL_DIR, outputEntities[2].getType());
        Assert.assertEquals("file:///tmp/udf1.jar", outputEntities[2].getLocation().toString());
    }

    @Test
    public void testUdfWithDfsResource() throws Exception {
        int rc = driver.compile((((("CREATE FUNCTION default." + (funcName)) + " AS 'org.apache.hadoop.hive.ql.udf.generic.GenericUDFPrintf' ") + " using file '") + "hdfs:///tmp/udf1.jar'"));
        Assert.assertEquals(0, rc);
        WriteEntity[] outputEntities = driver.getPlan().getOutputs().toArray(new WriteEntity[]{  });
        Assert.assertEquals(outputEntities.length, 3);
        Assert.assertEquals(DATABASE, outputEntities[0].getType());
        Assert.assertEquals("default", outputEntities[0].getDatabase().getName());
        Assert.assertEquals(FUNCTION, outputEntities[1].getType());
        Assert.assertEquals(funcName, outputEntities[1].getFunctionName());
        Assert.assertEquals(DFS_DIR, outputEntities[2].getType());
        Assert.assertEquals("hdfs:///tmp/udf1.jar", outputEntities[2].getLocation().toString());
    }
}

