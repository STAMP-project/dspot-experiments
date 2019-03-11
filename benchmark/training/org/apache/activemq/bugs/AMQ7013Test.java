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
package org.apache.activemq.bugs;


import org.apache.activemq.command.XATransactionId;
import org.junit.Assert;
import org.junit.Test;


public class AMQ7013Test {
    @Test
    public void hashTest() throws Exception {
        byte[] globalId1 = hexStringToByteArray("00000000000000000000ffff0a970616dbbe2c3b5b42f94800002259");
        byte[] branchQualifier1 = hexStringToByteArray("00000000000000000000ffff0a970616dbbe2c3b5b42f94800002259");
        XATransactionId id1 = new XATransactionId();
        id1.setGlobalTransactionId(globalId1);
        id1.setBranchQualifier(branchQualifier1);
        id1.setFormatId(131077);
        byte[] globalId2 = hexStringToByteArray("00000000000000000000ffff0a970616dbbe2c3b5b42f948000021d2");
        byte[] branchQualifier2 = hexStringToByteArray("00000000000000000000ffff0a970616dbbe2c3b5b42f948000021d2");
        XATransactionId id2 = new XATransactionId();
        id2.setGlobalTransactionId(globalId2);
        id2.setBranchQualifier(branchQualifier2);
        id2.setFormatId(131077);
        Assert.assertNotEquals(id1.hashCode(), id2.hashCode());
    }
}

