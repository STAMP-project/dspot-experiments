/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.client.hadoop.contract;


import PropertyKey.USER_FILE_BUFFER_BYTES;
import alluxio.testutils.LocalAlluxioClusterResource;
import java.net.URL;
import org.apache.hadoop.fs.contract.AbstractFSContractTestBase;
import org.junit.Rule;
import org.junit.Test;


public class FileSystemContractLoadedIntegrationTest extends AbstractFSContractTestBase {
    @Rule
    public LocalAlluxioClusterResource mClusterResource = // Adding this fixed an error in allocating enough space in seekBigFile and PosBulkRead
    new LocalAlluxioClusterResource.Builder().setProperty(USER_FILE_BUFFER_BYTES, "1k").build();

    @Test
    public void testContractWorks() throws Throwable {
        String key = getContract().getConfKey(SUPPORTS_ATOMIC_RENAME);
        assertNotNull(("not set: " + key), getContract().getConf().get(key));
        assertFalse(("true: " + key), getContract().isSupported(SUPPORTS_ATOMIC_RENAME, false));
    }

    @Test
    public void testContractResourceOnClasspath() throws Throwable {
        URL url = this.getClass().getClassLoader().getResource(FileSystemContract.CONTRACT_XML);
        assertNotNull("could not find contract resource", url);
    }
}

