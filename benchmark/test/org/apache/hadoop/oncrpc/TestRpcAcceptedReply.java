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
package org.apache.hadoop.oncrpc;


import AcceptState.GARBAGE_ARGS;
import AcceptState.PROC_UNAVAIL;
import AcceptState.PROG_MISMATCH;
import AcceptState.PROG_UNAVAIL;
import AcceptState.SUCCESS;
import AcceptState.SYSTEM_ERR;
import ReplyState.MSG_ACCEPTED;
import RpcMessage.Type.RPC_REPLY;
import org.apache.hadoop.oncrpc.RpcAcceptedReply.AcceptState;
import org.apache.hadoop.oncrpc.RpcReply.ReplyState;
import org.apache.hadoop.oncrpc.security.Verifier;
import org.apache.hadoop.oncrpc.security.VerifierNone;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for {@link RpcAcceptedReply}
 */
public class TestRpcAcceptedReply {
    @Test
    public void testAcceptState() {
        Assert.assertEquals(SUCCESS, AcceptState.fromValue(0));
        Assert.assertEquals(PROG_UNAVAIL, AcceptState.fromValue(1));
        Assert.assertEquals(PROG_MISMATCH, AcceptState.fromValue(2));
        Assert.assertEquals(PROC_UNAVAIL, AcceptState.fromValue(3));
        Assert.assertEquals(GARBAGE_ARGS, AcceptState.fromValue(4));
        Assert.assertEquals(SYSTEM_ERR, AcceptState.fromValue(5));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testAcceptStateFromInvalidValue() {
        AcceptState.fromValue(6);
    }

    @Test
    public void testConstructor() {
        Verifier verifier = new VerifierNone();
        RpcAcceptedReply reply = new RpcAcceptedReply(0, ReplyState.MSG_ACCEPTED, verifier, AcceptState.SUCCESS);
        Assert.assertEquals(0, reply.getXid());
        Assert.assertEquals(RPC_REPLY, reply.getMessageType());
        Assert.assertEquals(MSG_ACCEPTED, reply.getState());
        Assert.assertEquals(verifier, reply.getVerifier());
        Assert.assertEquals(SUCCESS, reply.getAcceptState());
    }
}

