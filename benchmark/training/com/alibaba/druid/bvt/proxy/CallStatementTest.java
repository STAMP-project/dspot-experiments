/**
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.proxy;


import junit.framework.TestCase;


public class CallStatementTest extends TestCase {
    /**
     * Procedures that should be created before the tests are run and dropped when the tests have finished. First
     * element in each row is the name of the procedure, second element is SQL which creates it.
     */
    private static final String[] PROCEDURES = new String[]{ ((("CREATE PROCEDURE RETRIEVE_DYNAMIC_RESULTS(number INT) " + "LANGUAGE JAVA PARAMETER STYLE JAVA EXTERNAL NAME '") + (CallStatementTest.class.getName())) + ".retrieveDynamicResults' ") + "DYNAMIC RESULT SETS 4", ((("CREATE PROCEDURE RETRIEVE_CLOSED_RESULT() LANGUAGE JAVA " + "PARAMETER STYLE JAVA EXTERNAL NAME '") + (CallStatementTest.class.getName())) + ".retrieveClosedResult' ") + "DYNAMIC RESULT SETS 1", ((("CREATE PROCEDURE RETRIEVE_EXTERNAL_RESULT(" + ("DBNAME VARCHAR(128), DBUSER VARCHAR(128), DBPWD VARCHAR(128)) LANGUAGE JAVA " + "PARAMETER STYLE JAVA EXTERNAL NAME '")) + (CallStatementTest.class.getName())) + ".retrieveExternalResult' ") + "DYNAMIC RESULT SETS 1", ((("CREATE PROCEDURE PROC_WITH_SIDE_EFFECTS(ret INT) LANGUAGE JAVA " + "PARAMETER STYLE JAVA EXTERNAL NAME '") + (CallStatementTest.class.getName())) + ".procWithSideEffects' ") + "DYNAMIC RESULT SETS 2", ((("CREATE PROCEDURE NESTED_RESULT_SETS(proctext VARCHAR(128)) LANGUAGE JAVA " + "PARAMETER STYLE JAVA EXTERNAL NAME '") + (CallStatementTest.class.getName())) + ".nestedDynamicResultSets' ") + "DYNAMIC RESULT SETS 6" };

    private static String create_url = "jdbc:wrap-jdbc:filters=default,commonLogging,log4j:name=demo:jdbc:derby:memory:callableStatementDB;create=true";

    public void test_precall() throws Exception {
        f_testExecuteQueryWithNoDynamicResultSets();
        f_testExecuteQueryWithNoDynamicResultSets_callable();
    }
}

