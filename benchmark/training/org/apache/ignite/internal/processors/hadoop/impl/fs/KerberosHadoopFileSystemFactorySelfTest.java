/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.internal.processors.hadoop.impl.fs;


import org.apache.ignite.hadoop.fs.KerberosHadoopFileSystemFactory;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Tests KerberosHadoopFileSystemFactory.
 */
public class KerberosHadoopFileSystemFactorySelfTest extends GridCommonAbstractTest {
    /**
     * Test parameters validation.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testParameters() throws Exception {
        checkParameters(null, null, (-1));
        checkParameters(null, null, 100);
        checkParameters(null, "b", (-1));
        checkParameters("a", null, (-1));
        checkParameters(null, "b", 100);
        checkParameters("a", null, 100);
        checkParameters("a", "b", (-1));
    }

    /**
     * Checks serializatuion and deserialization of the secure factory.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSerialization() throws Exception {
        KerberosHadoopFileSystemFactory fac = new KerberosHadoopFileSystemFactory();
        checkSerialization(fac);
        fac = new KerberosHadoopFileSystemFactory();
        fac.setUri("igfs://igfs@localhost:10500/");
        fac.setConfigPaths("/a/core-sute.xml", "/b/mapred-site.xml");
        fac.setKeyTabPrincipal("foo");
        fac.setKeyTab("/etc/krb5.keytab");
        fac.setReloginInterval(((30 * 60) * 1000L));
        checkSerialization(fac);
    }
}

