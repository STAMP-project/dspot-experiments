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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.hive.hcatalog.pig;


import java.io.File;
import org.apache.hadoop.hive.ql.IDriver;
import org.apache.hive.hcatalog.HcatTestUtils;
import org.junit.Test;


public class TestE2EScenarios {
    private static final String TEST_DATA_DIR = ((((System.getProperty("java.io.tmpdir")) + (File.separator)) + (TestE2EScenarios.class.getCanonicalName())) + "-") + (System.currentTimeMillis());

    private static final String TEST_WAREHOUSE_DIR = (TestE2EScenarios.TEST_DATA_DIR) + "/warehouse";

    private static final String TEXTFILE_LOCN = (TestE2EScenarios.TEST_DATA_DIR) + "/textfile";

    private static IDriver driver;

    @Test
    public void testReadOrcAndRCFromPig() throws Exception {
        String tableSchema = "ti tinyint, si smallint,i int, bi bigint, f float, d double, b boolean";
        HcatTestUtils.createTestDataFile(TestE2EScenarios.TEXTFILE_LOCN, new String[]{ "-3\u00019001\u000186400\u00014294967297\u000134.532\u00012184239842983489.1231231234\u0001true", "0\u00010\u00010\u00010\u00010\u00010\u0001false" });
        // write this out to a file, and import it into hive
        createTable("inpy", tableSchema, null, "textfile");
        createTable("rc5318", tableSchema, null, "rcfile");
        createTable("orc5318", tableSchema, null, "orc");
        driverRun((("LOAD DATA LOCAL INPATH '" + (TestE2EScenarios.TEXTFILE_LOCN)) + "' OVERWRITE INTO TABLE inpy"));
        // write it out from hive to an rcfile table, and to an orc table
        // driverRun("insert overwrite table rc5318 select * from inpy");
        copyTable("inpy", "rc5318");
        // driverRun("insert overwrite table orc5318 select * from inpy");
        copyTable("inpy", "orc5318");
        pigDump("inpy");
        pigDump("rc5318");
        pigDump("orc5318");
    }
}

