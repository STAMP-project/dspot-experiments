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
package org.apache.hadoop.hbase.procedure2;


import ProcedureProtos.Procedure;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.shaded.protobuf.generated.ProcedureProtos;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MasterTests.class, SmallTests.class })
public class TestProcedureUtil {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestProcedureUtil.class);

    @Test
    public void testValidation() throws Exception {
        ProcedureUtil.validateClass(new ProcedureTestingUtility.TestProcedure(10));
    }

    @Test(expected = BadProcedureException.class)
    public void testNoDefaultConstructorValidation() throws Exception {
        ProcedureUtil.validateClass(new TestProcedureUtil.TestProcedureNoDefaultConstructor(1));
    }

    @Test
    public void testConvert() throws Exception {
        // check Procedure to protobuf conversion
        final ProcedureTestingUtility.TestProcedure proc1 = new ProcedureTestingUtility.TestProcedure(10, 1, new byte[]{ 65 });
        final ProcedureProtos.Procedure proto1 = ProcedureUtil.convertToProtoProcedure(proc1);
        final ProcedureTestingUtility.TestProcedure proc2 = ((ProcedureTestingUtility.TestProcedure) (ProcedureUtil.convertToProcedure(proto1)));
        final ProcedureProtos.Procedure proto2 = ProcedureUtil.convertToProtoProcedure(proc2);
        Assert.assertEquals(false, proto2.hasResult());
        Assert.assertEquals("Procedure protobuf does not match", proto1, proto2);
    }

    @Test
    public void testGetBackoffTimeMs() {
        for (int i = 30; i < 1000; i++) {
            Assert.assertEquals(TimeUnit.MINUTES.toMillis(10), ProcedureUtil.getBackoffTimeMs(30));
        }
        long backoffTimeMs = ProcedureUtil.getBackoffTimeMs(0);
        Assert.assertTrue((backoffTimeMs >= 1000));
        Assert.assertTrue((backoffTimeMs <= (1000 * 1.01F)));
        backoffTimeMs = ProcedureUtil.getBackoffTimeMs(1);
        Assert.assertTrue((backoffTimeMs >= 2000));
        Assert.assertTrue((backoffTimeMs <= (2000 * 1.01F)));
        backoffTimeMs = ProcedureUtil.getBackoffTimeMs(5);
        Assert.assertTrue((backoffTimeMs >= 32000));
        Assert.assertTrue((backoffTimeMs <= (32000 * 1.01F)));
    }

    public static class TestProcedureNoDefaultConstructor extends ProcedureTestingUtility.TestProcedure {
        public TestProcedureNoDefaultConstructor(int x) {
        }
    }
}

