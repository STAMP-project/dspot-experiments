/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tomcat.jdbc.test;


import java.sql.SQLException;
import java.util.concurrent.Callable;
import javax.sql.PooledConnection;
import org.apache.tomcat.jdbc.test.driver.Connection;
import org.junit.Assert;
import org.junit.Test;


public class AlternateUsernameTest extends DefaultTestCase {
    private static final int iterations = 500000;// (new Random(System.currentTimeMillis())).nextInt(1000000)+100000;


    @Test
    public void testUsernameCompare() throws Exception {
        testUsername(true);
    }

    @Test
    public void testUsernameCompareAgain() throws Exception {
        testUsernameCompare();
    }

    @Test
    public void testUsernameCompareNotAllowed() throws Exception {
        testUsername(false);
    }

    public static class TestResult {
        public int iterations;

        public int failures;

        public String lastMessage;
    }

    public class TestRunner implements Callable<AlternateUsernameTest.TestResult> {
        String username;

        String password;

        volatile boolean done = false;

        AlternateUsernameTest.TestResult result = null;

        boolean useuser = true;

        public TestRunner(String user, String pass, String guser, String gpass) {
            username = (user == null) ? guser : user;
            password = (pass == null) ? gpass : pass;
            useuser = user != null;
        }

        @Override
        public AlternateUsernameTest.TestResult call() {
            AlternateUsernameTest.TestResult test = new AlternateUsernameTest.TestResult();
            PooledConnection pcon = null;
            for (int i = 0; (!(done)) && (i < (AlternateUsernameTest.iterations)); i++) {
                test.iterations = i + 1;
                try {
                    pcon = (useuser) ? ((PooledConnection) (AlternateUsernameTest.this.datasource.getConnection(username, password))) : ((PooledConnection) (AlternateUsernameTest.this.datasource.getConnection()));
                    Connection con = ((Connection) (pcon.getConnection()));
                    Assert.assertTrue(((("Username mismatch: Requested User:" + (username)) + " Actual user:") + (con.getUsername())), con.getUsername().equals(username));
                    Assert.assertTrue(((("Password mismatch: Requested Password:" + (password)) + " Actual password:") + (con.getPassword())), con.getPassword().equals(password));
                } catch (SQLException x) {
                    (test.failures)++;
                    test.lastMessage = x.getMessage();
                    done = true;
                    x.printStackTrace();
                } catch (Exception x) {
                    (test.failures)++;
                    test.lastMessage = x.getMessage();
                    x.printStackTrace();
                } finally {
                    if (pcon != null) {
                        try {
                            pcon.close();
                        } catch (Exception ignore) {
                            // Ignore
                        }
                        pcon = null;
                    }
                }
            }
            done = true;
            result = test;
            return result;
        }
    }
}

