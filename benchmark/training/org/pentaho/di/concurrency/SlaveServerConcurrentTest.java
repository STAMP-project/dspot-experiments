/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.concurrency;


import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.cluster.SlaveServer;


/**
 * We have a {@link SlaveServer} with a bunch of attributes (userName, password, port and others).
 *
 * The goal of this test is to verify that attributes from one SlaveServer doesn't mix with attributes of
 * another SlaveServer in concurrent environment when performing replace and clone methods.
 *
 * We have a {@code sharedSlaveServer} that will be share across {@link Replacer} and {@link Getter}
 *
 * {@link Replacer} generates consistent slaveService's. By consistence the following is meant:
 * Each slaveServer has it's id (random int) and all his fields are named in pattern: FIELD_NAME + slaveServerId.
 * Then replacer replaces {@code sharedSlaveServer} with generated slaveServer.
 *
 * Note: it is expected that {@code sharedSlaveServer} will always be in consistent state (all his fields will end
 * with the same id).
 *
 * And that's exactly what {@link Getter} does:
 *
 * It takes a snapshot of {@code sharedSlaveServer} and verifies it's consistency.
 * We have to take a snapshot here because {@code sharedSlaveServer} can be updated in the middle of consistency
 * verification. It's absolutely fine, cause it will be updated with consistent values.
 */
public class SlaveServerConcurrentTest {
    private static final String ID = "id";

    private static final String NAME = "name";

    private static final String HOST_NAME = "hostName";

    private static final String PORT = "port";

    private static final String WEB_APP_NAME = "webAppName";

    private static final String USERNAME = "userName";

    private static final String PASSWORD = "password";

    private static final String PROXY_HOST_NAME = "proxyHostName";

    private static final String PROXY_PORT = "proxyPort";

    private static final String NON_PROXY_HOSTS = "nonProxyHosts";

    private static final int NUMBER_OF_REPLACES = 200;

    private static final int NUMBER_OF_GETTERS = 100;

    private static final int REPLACE_CIRCLES = 100;

    private SlaveServer sharedSlaveServer;

    @Test
    public void getAndReplaceConcurrently() throws Exception {
        AtomicBoolean condition = new AtomicBoolean(true);
        List<SlaveServerConcurrentTest.Getter> getters = generateGetters(condition);
        List<SlaveServerConcurrentTest.Replacer> replacers = generateReplacers(condition);
        ConcurrencyTestRunner.runAndCheckNoExceptionRaised(replacers, getters, condition);
    }

    private class Replacer extends StopOnErrorCallable<Object> {
        Replacer(AtomicBoolean condition) {
            super(condition);
        }

        @Override
        public Object doCall() {
            for (int i = 0; i < (SlaveServerConcurrentTest.REPLACE_CIRCLES); i++) {
                sharedSlaveServer.replaceMeta(generateSlaveServer());
            }
            return null;
        }
    }

    private class Getter extends StopOnErrorCallable<Object> {
        Getter(AtomicBoolean condition) {
            super(condition);
        }

        @Override
        public Object doCall() {
            while (condition.get()) {
                checkConsistency(((SlaveServer) (sharedSlaveServer.clone())));
            } 
            return null;
        }

        private void checkConsistency(SlaveServer slaveServer) {
            String id = extractId(slaveServer.getName());
            Assert.assertEquals(id, extractId(slaveServer.getHostname()));
            Assert.assertEquals(id, extractId(slaveServer.getPort()));
            Assert.assertEquals(id, extractId(slaveServer.getWebAppName()));
            Assert.assertEquals(id, extractId(slaveServer.getUsername()));
            Assert.assertEquals(id, extractId(slaveServer.getPassword()));
            Assert.assertEquals(id, extractId(slaveServer.getProxyHostname()));
            Assert.assertEquals(id, extractId(slaveServer.getProxyPort()));
            Assert.assertEquals(id, extractId(slaveServer.getNonProxyHosts()));
            Assert.assertEquals(id, extractId(slaveServer.getObjectId().getId()));
        }

        private String extractId(String string) {
            return string.replaceAll("\\D+", "");
        }
    }
}

