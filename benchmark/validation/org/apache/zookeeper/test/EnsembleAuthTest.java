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
package org.apache.zookeeper.test;


import KeeperException.AuthFailedException;
import KeeperException.ConnectionLossException;
import org.junit.Test;


public class EnsembleAuthTest extends ClientBase {
    @Test
    public void noAuth() throws Exception {
        resetEnsembleAuth(null, false);
        connectToEnsemble(null);
    }

    @Test
    public void emptyAuth() throws Exception {
        resetEnsembleAuth(null, true);
        connectToEnsemble("foo");
    }

    @Test
    public void skipAuth() throws Exception {
        resetEnsembleAuth("woo", true);
        connectToEnsemble(null);
    }

    @Test
    public void passAuth() throws Exception {
        resetEnsembleAuth("woo", true);
        connectToEnsemble("woo");
    }

    @Test
    public void passAuthCSV() throws Exception {
        resetEnsembleAuth(" foo,bar, baz ", true);
        connectToEnsemble("foo");
        connectToEnsemble("bar");
        connectToEnsemble("baz");
    }

    @Test(expected = ConnectionLossException.class)
    public void failAuth() throws Exception {
        resetEnsembleAuth("woo", true);
        connectToEnsemble("goo");
    }

    @Test(expected = AuthFailedException.class)
    public void removeEnsembleAuthProvider() throws Exception {
        resetEnsembleAuth(null, false);
        connectToEnsemble("goo");
    }
}

