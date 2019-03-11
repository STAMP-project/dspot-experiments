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
package org.apache.hadoop.util.hash;


import Hash.INVALID_HASH;
import Hash.JENKINS_HASH;
import Hash.MURMUR_HASH;
import org.apache.hadoop.conf.Configuration;
import org.junit.Assert;
import org.junit.Test;

import static Hash.INVALID_HASH;
import static Hash.JENKINS_HASH;
import static Hash.MURMUR_HASH;


public class TestHash {
    static final String LINE = "34563@45kjkksdf/ljfdb9d8fbusd*89uggjsk<dfgjsdfh@sddc2q3esc";

    @Test
    public void testHash() {
        int iterations = 30;
        Assert.assertTrue("testHash jenkins error !!!", ((JENKINS_HASH) == (Hash.parseHashType("jenkins"))));
        Assert.assertTrue("testHash murmur error !!!", ((MURMUR_HASH) == (Hash.parseHashType("murmur"))));
        Assert.assertTrue("testHash undefined", ((INVALID_HASH) == (Hash.parseHashType("undefined"))));
        Configuration cfg = new Configuration();
        cfg.set("hadoop.util.hash.type", "murmur");
        Assert.assertTrue("testHash", ((MurmurHash.getInstance()) == (Hash.getInstance(cfg))));
        cfg = new Configuration();
        cfg.set("hadoop.util.hash.type", "jenkins");
        Assert.assertTrue("testHash jenkins configuration error !!!", ((JenkinsHash.getInstance()) == (Hash.getInstance(cfg))));
        cfg = new Configuration();
        Assert.assertTrue("testHash undefine configuration error !!!", ((MurmurHash.getInstance()) == (Hash.getInstance(cfg))));
        Assert.assertTrue("testHash error jenkin getInstance !!!", ((JenkinsHash.getInstance()) == (Hash.getInstance(JENKINS_HASH))));
        Assert.assertTrue("testHash error murmur getInstance !!!", ((MurmurHash.getInstance()) == (Hash.getInstance(MURMUR_HASH))));
        Assert.assertNull("testHash error invalid getInstance !!!", Hash.getInstance(INVALID_HASH));
        int murmurHash = Hash.getInstance(MURMUR_HASH).hash(TestHash.LINE.getBytes());
        for (int i = 0; i < iterations; i++) {
            Assert.assertTrue("multiple evaluation murmur hash error !!!", (murmurHash == (Hash.getInstance(MURMUR_HASH).hash(TestHash.LINE.getBytes()))));
        }
        murmurHash = Hash.getInstance(MURMUR_HASH).hash(TestHash.LINE.getBytes(), 67);
        for (int i = 0; i < iterations; i++) {
            Assert.assertTrue("multiple evaluation murmur hash error !!!", (murmurHash == (Hash.getInstance(MURMUR_HASH).hash(TestHash.LINE.getBytes(), 67))));
        }
        int jenkinsHash = Hash.getInstance(JENKINS_HASH).hash(TestHash.LINE.getBytes());
        for (int i = 0; i < iterations; i++) {
            Assert.assertTrue("multiple evaluation jenkins hash error !!!", (jenkinsHash == (Hash.getInstance(JENKINS_HASH).hash(TestHash.LINE.getBytes()))));
        }
        jenkinsHash = Hash.getInstance(JENKINS_HASH).hash(TestHash.LINE.getBytes(), 67);
        for (int i = 0; i < iterations; i++) {
            Assert.assertTrue("multiple evaluation jenkins hash error !!!", (jenkinsHash == (Hash.getInstance(JENKINS_HASH).hash(TestHash.LINE.getBytes(), 67))));
        }
    }
}

