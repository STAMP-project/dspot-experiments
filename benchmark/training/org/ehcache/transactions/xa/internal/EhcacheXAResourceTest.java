/**
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehcache.transactions.xa.internal;


import XATransactionContext.TransactionTimeoutException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import org.ehcache.core.spi.store.Store;
import org.ehcache.transactions.xa.internal.journal.Journal;
import org.ehcache.transactions.xa.utils.TestXid;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 *
 *
 * @author Ludovic Orban
 */
public class EhcacheXAResourceTest {
    @Mock
    private Store<Long, SoftLock<String>> underlyingStore;

    @Mock
    private Journal<Long> journal;

    @Mock
    private XATransactionContextFactory<Long, String> xaTransactionContextFactory;

    @Mock
    private XATransactionContext<Long, String> xaTransactionContext;

    @Test
    public void testStartEndWorks() throws Exception {
        EhcacheXAResource<Long, String> xaResource = new EhcacheXAResource(underlyingStore, journal, xaTransactionContextFactory);
        Mockito.when(xaTransactionContextFactory.createTransactionContext(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))), ArgumentMatchers.refEq(underlyingStore), ArgumentMatchers.refEq(journal), ArgumentMatchers.anyInt())).thenReturn(xaTransactionContext);
        xaResource.start(new TestXid(0, 0), XAResource.TMNOFLAGS);
        Mockito.when(xaTransactionContextFactory.get(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))))).thenReturn(xaTransactionContext);
        xaResource.end(new TestXid(0, 0), XAResource.TMSUCCESS);
        Mockito.when(xaTransactionContextFactory.createTransactionContext(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 1))), ArgumentMatchers.refEq(underlyingStore), ArgumentMatchers.refEq(journal), ArgumentMatchers.anyInt())).thenReturn(xaTransactionContext);
        xaResource.start(new TestXid(0, 1), XAResource.TMNOFLAGS);
        Mockito.when(xaTransactionContextFactory.get(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 1))))).thenReturn(xaTransactionContext);
        xaResource.end(new TestXid(0, 1), XAResource.TMSUCCESS);
    }

    @Test
    public void testTwoNonEndedStartsFails() throws Exception {
        EhcacheXAResource<Long, String> xaResource = new EhcacheXAResource(underlyingStore, journal, xaTransactionContextFactory);
        Mockito.when(xaTransactionContextFactory.createTransactionContext(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))), ArgumentMatchers.refEq(underlyingStore), ArgumentMatchers.refEq(journal), ArgumentMatchers.anyInt())).thenReturn(xaTransactionContext);
        xaResource.start(new TestXid(0, 0), XAResource.TMNOFLAGS);
        Mockito.when(xaTransactionContextFactory.createTransactionContext(ArgumentMatchers.eq(new TransactionId(new TestXid(1, 0))), ArgumentMatchers.refEq(underlyingStore), ArgumentMatchers.refEq(journal), ArgumentMatchers.anyInt())).thenReturn(xaTransactionContext);
        try {
            xaResource.start(new TestXid(1, 0), XAResource.TMNOFLAGS);
            Assert.fail("expected XAException");
        } catch (XAException xae) {
            Assert.assertThat(xae.errorCode, Matchers.is(XAException.XAER_PROTO));
        }
    }

    @Test
    public void testEndWithoutStartFails() throws Exception {
        EhcacheXAResource<Long, String> xaResource = new EhcacheXAResource(underlyingStore, journal, xaTransactionContextFactory);
        try {
            xaResource.end(new TestXid(0, 0), XAResource.TMSUCCESS);
            Assert.fail("expected XAException");
        } catch (XAException xae) {
            Assert.assertThat(xae.errorCode, Matchers.is(XAException.XAER_PROTO));
        }
    }

    @Test
    public void testJoinWorks() throws Exception {
        EhcacheXAResource<Long, String> xaResource = new EhcacheXAResource(underlyingStore, journal, xaTransactionContextFactory);
        Mockito.when(xaTransactionContextFactory.createTransactionContext(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))), ArgumentMatchers.refEq(underlyingStore), ArgumentMatchers.refEq(journal), ArgumentMatchers.anyInt())).thenReturn(xaTransactionContext);
        xaResource.start(new TestXid(0, 0), XAResource.TMNOFLAGS);
        Mockito.when(xaTransactionContextFactory.get(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))))).thenReturn(xaTransactionContext);
        xaResource.end(new TestXid(0, 0), XAResource.TMSUCCESS);
        xaResource.start(new TestXid(0, 0), XAResource.TMJOIN);
        xaResource.end(new TestXid(0, 0), XAResource.TMSUCCESS);
    }

    @Test
    public void testRecoverReportsAbortedTx() throws Exception {
        EhcacheXAResource<Long, String> xaResource = new EhcacheXAResource(underlyingStore, journal, xaTransactionContextFactory);
        Mockito.when(journal.recover()).thenReturn(Collections.singletonMap(new TransactionId(new TestXid(0, 0)), ((Collection<Long>) (Arrays.asList(1L, 2L, 3L)))));
        Xid[] recovered = xaResource.recover(((XAResource.TMSTARTRSCAN) | (XAResource.TMENDRSCAN)));
        Assert.assertThat(recovered.length, Matchers.is(1));
        Assert.assertThat(new SerializableXid(recovered[0]), Matchers.<Xid>equalTo(new SerializableXid(new TestXid(0, 0))));
    }

    @Test
    public void testRecoverIgnoresInFlightTx() throws Exception {
        EhcacheXAResource<Long, String> xaResource = new EhcacheXAResource(underlyingStore, journal, xaTransactionContextFactory);
        Mockito.when(journal.recover()).thenReturn(Collections.singletonMap(new TransactionId(new TestXid(0, 0)), ((Collection<Long>) (Arrays.asList(1L, 2L, 3L)))));
        Mockito.when(xaTransactionContextFactory.contains(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))))).thenReturn(true);
        Xid[] recovered = xaResource.recover(((XAResource.TMSTARTRSCAN) | (XAResource.TMENDRSCAN)));
        Assert.assertThat(recovered.length, Matchers.is(0));
    }

    @Test
    public void testCannotPrepareUnknownXid() throws Exception {
        EhcacheXAResource<Long, String> xaResource = new EhcacheXAResource(underlyingStore, journal, xaTransactionContextFactory);
        try {
            xaResource.prepare(new TestXid(0, 0));
            Assert.fail("expected XAException");
        } catch (XAException xae) {
            Assert.assertThat(xae.errorCode, Matchers.is(XAException.XAER_NOTA));
        }
    }

    @Test
    public void testCannotPrepareNonEndedXid() throws Exception {
        EhcacheXAResource<Long, String> xaResource = new EhcacheXAResource(underlyingStore, journal, xaTransactionContextFactory);
        Mockito.when(xaTransactionContextFactory.createTransactionContext(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))), ArgumentMatchers.refEq(underlyingStore), ArgumentMatchers.refEq(journal), ArgumentMatchers.anyInt())).thenReturn(xaTransactionContext);
        xaResource.start(new TestXid(0, 0), XAResource.TMNOFLAGS);
        try {
            xaResource.prepare(new TestXid(0, 0));
            Assert.fail("expected XAException");
        } catch (XAException xae) {
            Assert.assertThat(xae.errorCode, Matchers.is(XAException.XAER_PROTO));
        }
    }

    @Test
    public void testPrepareOk() throws Exception {
        EhcacheXAResource<Long, String> xaResource = new EhcacheXAResource(underlyingStore, journal, xaTransactionContextFactory);
        Mockito.when(xaTransactionContextFactory.get(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))))).thenReturn(xaTransactionContext);
        Mockito.when(xaTransactionContext.prepare()).thenReturn(1);
        int prepareRc = xaResource.prepare(new TestXid(0, 0));
        Assert.assertThat(prepareRc, Matchers.is(XAResource.XA_OK));
        Mockito.verify(xaTransactionContextFactory, Mockito.times(0)).destroy(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))));
    }

    @Test
    public void testPrepareReadOnly() throws Exception {
        EhcacheXAResource<Long, String> xaResource = new EhcacheXAResource(underlyingStore, journal, xaTransactionContextFactory);
        Mockito.when(xaTransactionContextFactory.get(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))))).thenReturn(xaTransactionContext);
        Mockito.when(xaTransactionContext.prepare()).thenReturn(0);
        int prepareRc = xaResource.prepare(new TestXid(0, 0));
        Assert.assertThat(prepareRc, Matchers.is(XAResource.XA_RDONLY));
        Mockito.verify(xaTransactionContextFactory, Mockito.times(1)).destroy(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))));
    }

    @Test
    public void testCannotCommitUnknownXidInFlight() throws Exception {
        EhcacheXAResource<Long, String> xaResource = new EhcacheXAResource(underlyingStore, journal, xaTransactionContextFactory);
        Mockito.when(journal.isInDoubt(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))))).thenReturn(false);
        Mockito.when(xaTransactionContextFactory.get(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))))).thenReturn(xaTransactionContext);
        Mockito.doThrow(IllegalArgumentException.class).when(xaTransactionContext).commit(ArgumentMatchers.eq(false));
        try {
            xaResource.commit(new TestXid(0, 0), false);
            Assert.fail("expected XAException");
        } catch (XAException xae) {
            Assert.assertThat(xae.errorCode, Matchers.is(XAException.XAER_NOTA));
        }
    }

    @Test
    public void testCannotCommitUnknownXidRecovered() throws Exception {
        EhcacheXAResource<Long, String> xaResource = new EhcacheXAResource(underlyingStore, journal, xaTransactionContextFactory);
        Mockito.when(journal.isInDoubt(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))))).thenReturn(false);
        try {
            xaResource.commit(new TestXid(0, 0), false);
            Assert.fail("expected XAException");
        } catch (XAException xae) {
            Assert.assertThat(xae.errorCode, Matchers.is(XAException.XAER_PROTO));
        }
    }

    @Test
    public void testCannotCommit1PcUnknownXid() throws Exception {
        EhcacheXAResource<Long, String> xaResource = new EhcacheXAResource(underlyingStore, journal, xaTransactionContextFactory);
        try {
            xaResource.commit(new TestXid(0, 0), true);
            Assert.fail("expected XAException");
        } catch (XAException xae) {
            Assert.assertThat(xae.errorCode, Matchers.is(XAException.XAER_NOTA));
        }
    }

    @Test
    public void testCannotCommit1PcNonEndedXid() throws Exception {
        EhcacheXAResource<Long, String> xaResource = new EhcacheXAResource(underlyingStore, journal, xaTransactionContextFactory);
        Mockito.when(xaTransactionContextFactory.createTransactionContext(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))), ArgumentMatchers.refEq(underlyingStore), ArgumentMatchers.refEq(journal), ArgumentMatchers.anyInt())).thenReturn(xaTransactionContext);
        xaResource.start(new TestXid(0, 0), XAResource.TMNOFLAGS);
        try {
            xaResource.commit(new TestXid(0, 0), true);
            Assert.fail("expected XAException");
        } catch (XAException xae) {
            Assert.assertThat(xae.errorCode, Matchers.is(XAException.XAER_PROTO));
        }
    }

    @Test
    public void testCannotCommitNonPreparedXid() throws Exception {
        EhcacheXAResource<Long, String> xaResource = new EhcacheXAResource(underlyingStore, journal, xaTransactionContextFactory);
        Mockito.when(xaTransactionContextFactory.get(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))))).thenReturn(xaTransactionContext);
        Mockito.doThrow(IllegalStateException.class).when(xaTransactionContext).commit(ArgumentMatchers.anyBoolean());
        try {
            xaResource.commit(new TestXid(0, 0), false);
            Assert.fail("expected XAException");
        } catch (XAException xae) {
            Assert.assertThat(xae.errorCode, Matchers.is(XAException.XAER_PROTO));
        }
    }

    @Test
    public void testCannotCommit1PcPreparedXid() throws Exception {
        EhcacheXAResource<Long, String> xaResource = new EhcacheXAResource(underlyingStore, journal, xaTransactionContextFactory);
        Mockito.when(xaTransactionContextFactory.get(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))))).thenReturn(xaTransactionContext);
        Mockito.doThrow(IllegalStateException.class).when(xaTransactionContext).commitInOnePhase();
        try {
            xaResource.commit(new TestXid(0, 0), true);
            Assert.fail("expected XAException");
        } catch (XAException xae) {
            Assert.assertThat(xae.errorCode, Matchers.is(XAException.XAER_PROTO));
        }
    }

    @Test
    public void testCommit() throws Exception {
        EhcacheXAResource<Long, String> xaResource = new EhcacheXAResource(underlyingStore, journal, xaTransactionContextFactory);
        Mockito.when(xaTransactionContextFactory.get(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))))).thenReturn(xaTransactionContext);
        xaResource.commit(new TestXid(0, 0), false);
        Mockito.verify(xaTransactionContextFactory, Mockito.times(1)).destroy(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))));
    }

    @Test
    public void testCommit1Pc() throws Exception {
        EhcacheXAResource<Long, String> xaResource = new EhcacheXAResource(underlyingStore, journal, xaTransactionContextFactory);
        Mockito.when(xaTransactionContextFactory.get(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))))).thenReturn(xaTransactionContext);
        xaResource.commit(new TestXid(0, 0), true);
        Mockito.verify(xaTransactionContextFactory, Mockito.times(1)).destroy(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))));
    }

    @Test
    public void testCannotRollbackUnknownXidInFlight() throws Exception {
        EhcacheXAResource<Long, String> xaResource = new EhcacheXAResource(underlyingStore, journal, xaTransactionContextFactory);
        Mockito.when(xaTransactionContextFactory.get(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))))).thenReturn(xaTransactionContext);
        Mockito.doThrow(IllegalStateException.class).when(xaTransactionContext).rollback(ArgumentMatchers.eq(false));
        try {
            xaResource.rollback(new TestXid(0, 0));
            Assert.fail("expected XAException");
        } catch (XAException xae) {
            Assert.assertThat(xae.errorCode, Matchers.is(XAException.XAER_NOTA));
        }
    }

    @Test
    public void testCannotRollbackUnknownXidRecovered() throws Exception {
        EhcacheXAResource<Long, String> xaResource = new EhcacheXAResource(underlyingStore, journal, xaTransactionContextFactory);
        Mockito.when(journal.isInDoubt(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))))).thenReturn(false);
        try {
            xaResource.rollback(new TestXid(0, 0));
            Assert.fail("expected XAException");
        } catch (XAException xae) {
            Assert.assertThat(xae.errorCode, Matchers.is(XAException.XAER_NOTA));
        }
    }

    @Test
    public void testCannotRollbackNonEndedXid() throws Exception {
        EhcacheXAResource<Long, String> xaResource = new EhcacheXAResource(underlyingStore, journal, xaTransactionContextFactory);
        Mockito.when(xaTransactionContextFactory.createTransactionContext(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))), ArgumentMatchers.refEq(underlyingStore), ArgumentMatchers.refEq(journal), ArgumentMatchers.anyInt())).thenReturn(xaTransactionContext);
        xaResource.start(new TestXid(0, 0), XAResource.TMNOFLAGS);
        try {
            xaResource.rollback(new TestXid(0, 0));
            Assert.fail("expected XAException");
        } catch (XAException xae) {
            Assert.assertThat(xae.errorCode, Matchers.is(XAException.XAER_PROTO));
        }
    }

    @Test
    public void testRollback() throws Exception {
        EhcacheXAResource<Long, String> xaResource = new EhcacheXAResource(underlyingStore, journal, xaTransactionContextFactory);
        Mockito.when(xaTransactionContextFactory.get(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))))).thenReturn(xaTransactionContext);
        xaResource.rollback(new TestXid(0, 0));
        Mockito.verify(xaTransactionContextFactory, Mockito.times(1)).destroy(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))));
    }

    @Test
    public void testForgetUnknownXid() throws Exception {
        EhcacheXAResource<Long, String> xaResource = new EhcacheXAResource(underlyingStore, journal, xaTransactionContextFactory);
        Mockito.when(journal.isInDoubt(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))))).thenReturn(false);
        try {
            xaResource.forget(new TestXid(0, 0));
            Assert.fail("expected XAException");
        } catch (XAException xae) {
            Assert.assertThat(xae.errorCode, Matchers.is(XAException.XAER_NOTA));
        }
    }

    @Test
    public void testForgetInDoubtXid() throws Exception {
        EhcacheXAResource<Long, String> xaResource = new EhcacheXAResource(underlyingStore, journal, xaTransactionContextFactory);
        Mockito.when(journal.isInDoubt(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))))).thenReturn(true);
        try {
            xaResource.forget(new TestXid(0, 0));
            Assert.fail("expected XAException");
        } catch (XAException xae) {
            Assert.assertThat(xae.errorCode, Matchers.is(XAException.XAER_PROTO));
        }
    }

    @Test
    public void testForget() throws Exception {
        EhcacheXAResource<Long, String> xaResource = new EhcacheXAResource(underlyingStore, journal, xaTransactionContextFactory);
        Mockito.when(journal.isHeuristicallyTerminated(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))))).thenReturn(true);
        xaResource.forget(new TestXid(0, 0));
        Mockito.verify(journal, Mockito.times(1)).forget(new TransactionId(new TestXid(0, 0)));
    }

    @Test
    public void testTimeoutStart() throws Exception {
        EhcacheXAResource<Long, String> xaResource = new EhcacheXAResource(underlyingStore, journal, xaTransactionContextFactory);
        Mockito.when(xaTransactionContextFactory.createTransactionContext(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))), ArgumentMatchers.refEq(underlyingStore), ArgumentMatchers.refEq(journal), ArgumentMatchers.anyInt())).thenReturn(xaTransactionContext);
        Mockito.when(xaTransactionContext.hasTimedOut()).thenReturn(true);
        try {
            xaResource.start(new TestXid(0, 0), XAResource.TMNOFLAGS);
            Assert.fail("expected XAException");
        } catch (XAException xae) {
            Assert.assertThat(xae.errorCode, Matchers.is(XAException.XA_RBTIMEOUT));
        }
        Mockito.verify(xaTransactionContextFactory, Mockito.times(1)).destroy(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))));
    }

    @Test
    public void testTimeoutEndSuccess() throws Exception {
        EhcacheXAResource<Long, String> xaResource = new EhcacheXAResource(underlyingStore, journal, xaTransactionContextFactory);
        Mockito.when(xaTransactionContextFactory.createTransactionContext(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))), ArgumentMatchers.refEq(underlyingStore), ArgumentMatchers.refEq(journal), ArgumentMatchers.anyInt())).thenReturn(xaTransactionContext);
        xaResource.start(new TestXid(0, 0), XAResource.TMNOFLAGS);
        Mockito.when(xaTransactionContext.hasTimedOut()).thenReturn(true);
        Mockito.when(xaTransactionContextFactory.get(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))))).thenReturn(xaTransactionContext);
        try {
            xaResource.end(new TestXid(0, 0), XAResource.TMSUCCESS);
            Assert.fail("expected XAException");
        } catch (XAException xae) {
            Assert.assertThat(xae.errorCode, Matchers.is(XAException.XA_RBTIMEOUT));
        }
        Mockito.verify(xaTransactionContextFactory, Mockito.times(1)).destroy(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))));
    }

    @Test
    public void testTimeoutEndFail() throws Exception {
        EhcacheXAResource<Long, String> xaResource = new EhcacheXAResource(underlyingStore, journal, xaTransactionContextFactory);
        Mockito.when(xaTransactionContextFactory.createTransactionContext(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))), ArgumentMatchers.refEq(underlyingStore), ArgumentMatchers.refEq(journal), ArgumentMatchers.anyInt())).thenReturn(xaTransactionContext);
        xaResource.start(new TestXid(0, 0), XAResource.TMNOFLAGS);
        Mockito.when(xaTransactionContext.hasTimedOut()).thenReturn(true);
        Mockito.when(xaTransactionContextFactory.get(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))))).thenReturn(xaTransactionContext);
        try {
            xaResource.end(new TestXid(0, 0), XAResource.TMFAIL);
            Assert.fail("expected XAException");
        } catch (XAException xae) {
            Assert.assertThat(xae.errorCode, Matchers.is(XAException.XA_RBTIMEOUT));
        }
        Mockito.verify(xaTransactionContextFactory, Mockito.times(1)).destroy(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPrepareTimeout() throws Exception {
        EhcacheXAResource<Long, String> xaResource = new EhcacheXAResource(underlyingStore, journal, xaTransactionContextFactory);
        Mockito.when(xaTransactionContextFactory.get(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))))).thenReturn(xaTransactionContext);
        Mockito.when(xaTransactionContext.prepare()).thenThrow(TransactionTimeoutException.class);
        try {
            xaResource.prepare(new TestXid(0, 0));
            Assert.fail("expected XAException");
        } catch (XAException xae) {
            Assert.assertThat(xae.errorCode, Matchers.is(XAException.XA_RBTIMEOUT));
        }
        Mockito.verify(xaTransactionContextFactory, Mockito.times(1)).destroy(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))));
    }

    @Test
    public void testCommit1PcTimeout() throws Exception {
        EhcacheXAResource<Long, String> xaResource = new EhcacheXAResource(underlyingStore, journal, xaTransactionContextFactory);
        Mockito.when(xaTransactionContextFactory.get(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))))).thenReturn(xaTransactionContext);
        Mockito.doThrow(TransactionTimeoutException.class).when(xaTransactionContext).commitInOnePhase();
        try {
            xaResource.commit(new TestXid(0, 0), true);
            Assert.fail("expected XAException");
        } catch (XAException xae) {
            Assert.assertThat(xae.errorCode, Matchers.is(XAException.XA_RBTIMEOUT));
        }
        Mockito.verify(xaTransactionContextFactory, Mockito.times(1)).destroy(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))));
    }

    @Test
    public void testRecoveryCommitOnePhaseFails() throws Exception {
        EhcacheXAResource<Long, String> xaResource = new EhcacheXAResource(underlyingStore, journal, xaTransactionContextFactory);
        Mockito.when(journal.recover()).thenReturn(Collections.singletonMap(new TransactionId(new TestXid(0, 0)), ((Collection<Long>) (Arrays.asList(1L, 2L, 3L)))));
        Mockito.when(journal.getInDoubtKeys(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))))).thenReturn(Arrays.asList(1L, 2L, 3L));
        Xid[] recoveredXids = xaResource.recover(((XAResource.TMSTARTRSCAN) | (XAResource.TMENDRSCAN)));
        Assert.assertThat(recoveredXids.length, Matchers.is(1));
        try {
            xaResource.commit(recoveredXids[0], true);
            Assert.fail("expected XAException");
        } catch (XAException xae) {
            Assert.assertThat(xae.errorCode, Matchers.is(XAException.XAER_NOTA));
        }
        Mockito.verify(xaTransactionContextFactory, Mockito.times(0)).destroy(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))));
    }

    @Test
    public void testRecoveryCommit() throws Exception {
        EhcacheXAResource<Long, String> xaResource = new EhcacheXAResource(underlyingStore, journal, xaTransactionContextFactory);
        Mockito.when(journal.recover()).thenReturn(Collections.singletonMap(new TransactionId(new TestXid(0, 0)), ((Collection<Long>) (Arrays.asList(1L, 2L, 3L)))));
        Mockito.when(journal.getInDoubtKeys(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))))).thenReturn(Arrays.asList(1L, 2L, 3L));
        Mockito.when(journal.isInDoubt(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))))).thenReturn(true);
        Xid[] recoveredXids = xaResource.recover(((XAResource.TMSTARTRSCAN) | (XAResource.TMENDRSCAN)));
        Assert.assertThat(recoveredXids.length, Matchers.is(1));
        xaResource.commit(recoveredXids[0], false);
        Mockito.verify(xaTransactionContextFactory, Mockito.times(0)).destroy(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))));
        Mockito.verify(underlyingStore, Mockito.times(1)).get(ArgumentMatchers.eq(1L));
        Mockito.verify(underlyingStore, Mockito.times(1)).get(ArgumentMatchers.eq(2L));
        Mockito.verify(underlyingStore, Mockito.times(1)).get(ArgumentMatchers.eq(3L));
    }

    @Test
    public void testRecoveryRollback() throws Exception {
        EhcacheXAResource<Long, String> xaResource = new EhcacheXAResource(underlyingStore, journal, xaTransactionContextFactory);
        Mockito.when(journal.isInDoubt(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))))).thenReturn(true);
        Mockito.when(journal.recover()).thenReturn(Collections.singletonMap(new TransactionId(new TestXid(0, 0)), ((Collection<Long>) (Arrays.asList(1L, 2L, 3L)))));
        Mockito.when(journal.getInDoubtKeys(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))))).thenReturn(Arrays.asList(1L, 2L, 3L));
        Xid[] recoveredXids = xaResource.recover(((XAResource.TMSTARTRSCAN) | (XAResource.TMENDRSCAN)));
        Assert.assertThat(recoveredXids.length, Matchers.is(1));
        xaResource.rollback(recoveredXids[0]);
        Mockito.verify(xaTransactionContextFactory, Mockito.times(0)).destroy(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))));
        Mockito.verify(underlyingStore, Mockito.times(1)).get(ArgumentMatchers.eq(1L));
        Mockito.verify(underlyingStore, Mockito.times(1)).get(ArgumentMatchers.eq(2L));
        Mockito.verify(underlyingStore, Mockito.times(1)).get(ArgumentMatchers.eq(3L));
    }
}

