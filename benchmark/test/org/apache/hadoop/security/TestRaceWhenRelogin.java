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
package org.apache.hadoop.security;


import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.minikdc.KerberosSecurityTestcase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Testcase for HADOOP-13433 that confirms that tgt will always be the first
 * ticket after relogin.
 */
public class TestRaceWhenRelogin extends KerberosSecurityTestcase {
    private int numThreads = 10;

    private String clientPrincipal = "client";

    private String serverProtocol = "server";

    private String[] serverProtocols;

    private String host = "localhost";

    private String serverPrincipal = ((serverProtocol) + "/") + (host);

    private String[] serverPrincipals;

    private File keytabFile;

    private Configuration conf = new Configuration();

    private Map<String, String> props;

    private UserGroupInformation ugi;

    @Test
    public void test() throws IOException, InterruptedException {
        AtomicBoolean pass = new AtomicBoolean(true);
        Thread reloginThread = new Thread(() -> relogin(pass), "Relogin");
        AtomicBoolean running = new AtomicBoolean(true);
        Thread[] getServiceTicketThreads = new Thread[numThreads];
        for (int i = 0; i < (numThreads); i++) {
            String serverProtocol = serverProtocols[i];
            getServiceTicketThreads[i] = new Thread(() -> getServiceTicket(running, serverProtocol), ("GetServiceTicket-" + i));
        }
        for (Thread getServiceTicketThread : getServiceTicketThreads) {
            getServiceTicketThread.start();
        }
        reloginThread.start();
        reloginThread.join();
        running.set(false);
        for (Thread getServiceTicketThread : getServiceTicketThreads) {
            getServiceTicketThread.join();
        }
        Assert.assertTrue("tgt is not the first ticket after relogin", pass.get());
    }
}

