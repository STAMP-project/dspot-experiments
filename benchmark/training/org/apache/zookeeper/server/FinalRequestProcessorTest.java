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
package org.apache.zookeeper.server;


import KeeperException.Code;
import KeeperException.Code.NOAUTH;
import ZooDefs.OpCode;
import ZooDefs.Perms;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.jute.Record;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.proto.GetACLResponse;
import org.apache.zookeeper.proto.ReplyHeader;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class FinalRequestProcessorTest {
    private List<ACL> testACLs = new ArrayList<ACL>();

    private final Record[] responseRecord = new Record[1];

    private final ReplyHeader[] replyHeaders = new ReplyHeader[1];

    private ServerCnxn cnxn;

    private ByteBuffer bb;

    private FinalRequestProcessor processor;

    @Test
    public void testACLDigestHashHiding_NoAuth_WorldCanRead() {
        // Arrange
        // Act
        Request r = new Request(cnxn, 0, 0, OpCode.getACL, bb, new ArrayList<Id>());
        processor.processRequest(r);
        // Assert
        assertMasked(true);
    }

    @Test
    public void testACLDigestHashHiding_NoAuth_NoWorld() {
        // Arrange
        testACLs.remove(2);
        // Act
        Request r = new Request(cnxn, 0, 0, OpCode.getACL, bb, new ArrayList<Id>());
        processor.processRequest(r);
        // Assert
        Assert.assertThat(Code.get(replyHeaders[0].getErr()), Matchers.equalTo(NOAUTH));
    }

    @Test
    public void testACLDigestHashHiding_UserCanRead() {
        // Arrange
        List<Id> authInfo = new ArrayList<Id>();
        authInfo.add(new Id("digest", "otheruser:somesecrethash"));
        // Act
        Request r = new Request(cnxn, 0, 0, OpCode.getACL, bb, authInfo);
        processor.processRequest(r);
        // Assert
        assertMasked(true);
    }

    @Test
    public void testACLDigestHashHiding_UserCanAll() {
        // Arrange
        List<Id> authInfo = new ArrayList<Id>();
        authInfo.add(new Id("digest", "user:secrethash"));
        // Act
        Request r = new Request(cnxn, 0, 0, OpCode.getACL, bb, authInfo);
        processor.processRequest(r);
        // Assert
        assertMasked(false);
    }

    @Test
    public void testACLDigestHashHiding_AdminUser() {
        // Arrange
        List<Id> authInfo = new ArrayList<Id>();
        authInfo.add(new Id("digest", "adminuser:adminsecret"));
        // Act
        Request r = new Request(cnxn, 0, 0, OpCode.getACL, bb, authInfo);
        processor.processRequest(r);
        // Assert
        assertMasked(false);
    }

    @Test
    public void testACLDigestHashHiding_OnlyAdmin() {
        // Arrange
        testACLs.clear();
        testACLs.addAll(Arrays.asList(new ACL(Perms.READ, new Id("digest", "user:secrethash")), new ACL(Perms.ADMIN, new Id("digest", "adminuser:adminsecret"))));
        List<Id> authInfo = new ArrayList<Id>();
        authInfo.add(new Id("digest", "adminuser:adminsecret"));
        // Act
        Request r = new Request(cnxn, 0, 0, OpCode.getACL, bb, authInfo);
        processor.processRequest(r);
        // Assert
        Assert.assertTrue("Not a GetACL response. Auth failed?", ((responseRecord[0]) instanceof GetACLResponse));
        GetACLResponse rsp = ((GetACLResponse) (responseRecord[0]));
        Assert.assertThat("Number of ACLs in the response are different", rsp.getAcl().size(), Matchers.equalTo(2));
        // Verify ACLs in the response
        Assert.assertThat("Password hash mismatch in the response", rsp.getAcl().get(0).getId().getId(), Matchers.equalTo("user:secrethash"));
        Assert.assertThat("Password hash mismatch in the response", rsp.getAcl().get(1).getId().getId(), Matchers.equalTo("adminuser:adminsecret"));
    }
}

