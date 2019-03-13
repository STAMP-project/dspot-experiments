/**
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.hadoop.fs.contract.localfs;


import java.net.URL;
import org.apache.hadoop.fs.contract.AbstractFSContractTestBase;
import org.apache.hadoop.fs.contract.ContractOptions;
import org.junit.Assert;
import org.junit.Test;


/**
 * just here to make sure that the local.xml resource is actually loading
 */
public class TestLocalFSContractLoaded extends AbstractFSContractTestBase {
    @Test
    public void testContractWorks() throws Throwable {
        String key = getContract().getConfKey(ContractOptions.SUPPORTS_ATOMIC_RENAME);
        Assert.assertNotNull(("not set: " + key), getConf().get(key));
        Assert.assertTrue(("not true: " + key), getContract().isSupported(ContractOptions.SUPPORTS_ATOMIC_RENAME, false));
    }

    @Test
    public void testContractResourceOnClasspath() throws Throwable {
        URL url = this.getClass().getClassLoader().getResource(LocalFSContract.CONTRACT_XML);
        Assert.assertNotNull("could not find contract resource", url);
    }
}

