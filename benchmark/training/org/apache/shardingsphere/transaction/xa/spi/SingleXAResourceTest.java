/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.shardingsphere.transaction.xa.spi;


import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public final class SingleXAResourceTest {
    @Mock
    private XAResource xaResource;

    @Mock
    private Xid xid;

    private SingleXAResource singleXAResource;

    @Test
    public void assertCommit() throws XAException {
        singleXAResource.commit(xid, true);
        Mockito.verify(xaResource).commit(xid, true);
    }

    @Test
    public void assertEnd() throws XAException {
        singleXAResource.end(xid, 1);
        Mockito.verify(xaResource).end(xid, 1);
    }

    @Test
    public void assertForget() throws XAException {
        singleXAResource.forget(xid);
        Mockito.verify(xaResource).forget(xid);
    }

    @Test
    public void assertGetTransactionTimeout() throws XAException {
        singleXAResource.getTransactionTimeout();
        Mockito.verify(xaResource).getTransactionTimeout();
    }

    @Test
    public void assertIsSameRM() {
        Assert.assertTrue(singleXAResource.isSameRM(new SingleXAResource("ds1", xaResource)));
    }

    @Test
    public void assertPrepare() throws XAException {
        singleXAResource.prepare(xid);
        Mockito.verify(xaResource).prepare(xid);
    }

    @Test
    public void assertRecover() throws XAException {
        singleXAResource.recover(1);
        Mockito.verify(xaResource).recover(1);
    }

    @Test
    public void assertRollback() throws XAException {
        singleXAResource.rollback(xid);
        Mockito.verify(xaResource).rollback(xid);
    }

    @Test
    public void assertSetTransactionTimeout() throws XAException {
        singleXAResource.setTransactionTimeout(1);
        Mockito.verify(xaResource).setTransactionTimeout(1);
    }

    @Test
    public void assertStart() throws XAException {
        singleXAResource.start(xid, 1);
        Mockito.verify(xaResource).start(xid, 1);
    }
}

