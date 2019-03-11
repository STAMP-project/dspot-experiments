/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.ozone.audit.parser;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests GenerateOzoneRequiredConfigurations.
 */
public class TestAuditParser {
    private static File outputBaseDir;

    private static AuditParser parserTool;

    private static final Logger LOG = LoggerFactory.getLogger(TestAuditParser.class);

    private static final ByteArrayOutputStream OUT = new ByteArrayOutputStream();

    private final ByteArrayOutputStream err = new ByteArrayOutputStream();

    private static final PrintStream OLD_OUT = System.out;

    private static final PrintStream OLD_ERR = System.err;

    private static String dbName;

    private static final String LOGS = TestAuditParser.class.getClassLoader().getResource("testaudit.log").getPath();

    /**
     * Test to find top 5 commands.
     */
    @Test
    public void testTemplateTop5Cmds() {
        String[] args = new String[]{ TestAuditParser.dbName, "template", "top5cmds" };
        TestAuditParser.execute(args, ("DELETE_KEY\t3\t\n" + ((("ALLOCATE_KEY\t2\t\n" + "COMMIT_KEY\t2\t\n") + "CREATE_BUCKET\t1\t\n") + "CREATE_VOLUME\t1\t\n\n")));
    }

    /**
     * Test to find top 5 users.
     */
    @Test
    public void testTemplateTop5Users() {
        String[] args = new String[]{ TestAuditParser.dbName, "template", "top5users" };
        TestAuditParser.execute(args, "hadoop\t9\t\n");
    }

    /**
     * Test to find top 5 users.
     */
    @Test
    public void testTemplateTop5ActiveTimeBySeconds() {
        String[] args = new String[]{ TestAuditParser.dbName, "template", "top5activetimebyseconds" };
        TestAuditParser.execute(args, ("2018-09-06 01:57:22\t3\t\n" + ((("2018-09-06 01:58:08\t1\t\n" + "2018-09-06 01:58:18\t1\t\n") + "2018-09-06 01:59:36\t1\t\n") + "2018-09-06 01:59:41\t1\t\n")));
    }

    /**
     * Test to execute custom query.
     */
    @Test
    public void testQueryCommand() {
        String[] args = new String[]{ TestAuditParser.dbName, "query", "select count(*) from audit" };
        TestAuditParser.execute(args, "9");
    }

    /**
     * Test to check help message.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testHelp() throws Exception {
        String[] args = new String[]{ "--help" };
        TestAuditParser.execute(args, ("Usage: ozone auditparser [-hV] [--verbose] [-D=<String=String>]... " + ("<database>\n" + "                         [COMMAND]")));
    }
}

