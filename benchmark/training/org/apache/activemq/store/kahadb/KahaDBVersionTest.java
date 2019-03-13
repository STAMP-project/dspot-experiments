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
package org.apache.activemq.store.kahadb;


import java.io.File;
import java.io.IOException;
import java.security.ProtectionDomain;
import junit.framework.TestCase;
import org.apache.activemq.broker.BrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author chirino
 */
public class KahaDBVersionTest extends TestCase {
    static String basedir;

    static {
        try {
            ProtectionDomain protectionDomain = KahaDBVersionTest.class.getProtectionDomain();
            KahaDBVersionTest.basedir = new File(new File(protectionDomain.getCodeSource().getLocation().getPath()), "../..").getCanonicalPath();
        } catch (IOException e) {
            KahaDBVersionTest.basedir = ".";
        }
    }

    static final Logger LOG = LoggerFactory.getLogger(KahaDBVersionTest.class);

    static final File VERSION_1_DB = new File(((KahaDBVersionTest.basedir) + "/src/test/resources/org/apache/activemq/store/kahadb/KahaDBVersion1"));

    static final File VERSION_2_DB = new File(((KahaDBVersionTest.basedir) + "/src/test/resources/org/apache/activemq/store/kahadb/KahaDBVersion2"));

    static final File VERSION_3_DB = new File(((KahaDBVersionTest.basedir) + "/src/test/resources/org/apache/activemq/store/kahadb/KahaDBVersion3"));

    static final File VERSION_4_DB = new File(((KahaDBVersionTest.basedir) + "/src/test/resources/org/apache/activemq/store/kahadb/KahaDBVersion4"));

    static final File VERSION_5_DB = new File(((KahaDBVersionTest.basedir) + "/src/test/resources/org/apache/activemq/store/kahadb/KahaDBVersion5"));

    static final File VERSION_6_DB = new File(((KahaDBVersionTest.basedir) + "/src/test/resources/org/apache/activemq/store/kahadb/KahaDBVersion6"));

    BrokerService broker = null;

    public void testVersion1Conversion() throws Exception {
        doConvertRestartCycle(KahaDBVersionTest.VERSION_1_DB);
    }

    public void testVersion2Conversion() throws Exception {
        doConvertRestartCycle(KahaDBVersionTest.VERSION_2_DB);
    }

    public void testVersion3Conversion() throws Exception {
        doConvertRestartCycle(KahaDBVersionTest.VERSION_3_DB);
    }

    public void testVersion4Conversion() throws Exception {
        doConvertRestartCycle(KahaDBVersionTest.VERSION_4_DB);
    }

    public void testVersion5Conversion() throws Exception {
        doConvertRestartCycle(KahaDBVersionTest.VERSION_5_DB);
    }

    public void testVersion6Conversion() throws Exception {
        doConvertRestartCycle(KahaDBVersionTest.VERSION_6_DB);
    }
}

