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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.hive.hcatalog.data;


import java.util.Map;
import java.util.Properties;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.serde2.SerDeUtils;
import org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe;
import org.apache.hadoop.io.Writable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestHCatRecordSerDe extends TestCase {
    private static final Logger LOG = LoggerFactory.getLogger(TestHCatRecordSerDe.class);

    public void testRW() throws Exception {
        Configuration conf = new Configuration();
        for (Map.Entry<Properties, HCatRecord> e : getData().entrySet()) {
            Properties tblProps = e.getKey();
            HCatRecord r = e.getValue();
            HCatRecordSerDe hrsd = new HCatRecordSerDe();
            SerDeUtils.initializeSerDe(hrsd, conf, tblProps, null);
            TestHCatRecordSerDe.LOG.info("ORIG: {}", r);
            Writable s = hrsd.serialize(r, hrsd.getObjectInspector());
            TestHCatRecordSerDe.LOG.info("ONE: {}", s);
            HCatRecord r2 = ((HCatRecord) (hrsd.deserialize(s)));
            Assert.assertTrue(HCatDataCheckUtil.recordsEqual(r, r2));
            // If it went through correctly, then s is also a HCatRecord,
            // and also equal to the above, and a deepcopy, and this holds
            // through for multiple levels more of serialization as well.
            Writable s2 = hrsd.serialize(s, hrsd.getObjectInspector());
            TestHCatRecordSerDe.LOG.info("TWO: {}", s2);
            Assert.assertTrue(HCatDataCheckUtil.recordsEqual(r, ((HCatRecord) (s))));
            Assert.assertTrue(HCatDataCheckUtil.recordsEqual(r, ((HCatRecord) (s2))));
            // serialize using another serde, and read out that object repr.
            LazySimpleSerDe testSD = new LazySimpleSerDe();
            SerDeUtils.initializeSerDe(testSD, conf, tblProps, null);
            Writable s3 = testSD.serialize(s, hrsd.getObjectInspector());
            TestHCatRecordSerDe.LOG.info("THREE: {}", s3);
            Object o3 = testSD.deserialize(s3);
            Assert.assertFalse(r.getClass().equals(o3.getClass()));
            // then serialize again using hrsd, and compare results
            HCatRecord s4 = ((HCatRecord) (hrsd.serialize(o3, testSD.getObjectInspector())));
            TestHCatRecordSerDe.LOG.info("FOUR: {}", s4);
            // Test LazyHCatRecord init and read
            LazyHCatRecord s5 = new LazyHCatRecord(o3, testSD.getObjectInspector());
            TestHCatRecordSerDe.LOG.info("FIVE: {}", s5);
            LazyHCatRecord s6 = new LazyHCatRecord(s4, hrsd.getObjectInspector());
            TestHCatRecordSerDe.LOG.info("SIX: {}", s6);
        }
    }
}

