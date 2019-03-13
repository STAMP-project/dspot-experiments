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


import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.ehcache.core.spi.store.Store;
import org.ehcache.internal.TestTimeSource;
import org.ehcache.transactions.xa.internal.journal.Journal;
import org.ehcache.transactions.xa.utils.TestXid;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.stubbing.Answer;

import static org.ehcache.core.spi.store.Store.RemoveStatus.KEY_MISSING;
import static org.ehcache.core.spi.store.Store.RemoveStatus.REMOVED;
import static org.ehcache.core.spi.store.Store.ReplaceStatus.HIT;
import static org.ehcache.core.spi.store.Store.ReplaceStatus.MISS_NOT_PRESENT;


/**
 *
 *
 * @author Ludovic Orban
 */
public class XATransactionContextTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private Store<Long, SoftLock<String>> underlyingStore;

    @Mock
    private Journal<Long> journal;

    private final TestTimeSource timeSource = new TestTimeSource();

    @Test
    public void testSimpleCommands() {
        XATransactionContext<Long, String> xaTransactionContext = getXaTransactionContext();
        MatcherAssert.assertThat(xaTransactionContext.touched(1L), Is.is(false));
        MatcherAssert.assertThat(xaTransactionContext.removed(1L), Is.is(false));
        MatcherAssert.assertThat(xaTransactionContext.updated(1L), Is.is(false));
        MatcherAssert.assertThat(xaTransactionContext.evicted(1L), Is.is(false));
        MatcherAssert.assertThat(xaTransactionContext.newValueHolderOf(1L), Is.is(Matchers.nullValue()));
        MatcherAssert.assertThat(xaTransactionContext.oldValueOf(1L), Is.is(Matchers.nullValue()));
        MatcherAssert.assertThat(xaTransactionContext.newValueOf(1L), Is.is(Matchers.nullValue()));
        xaTransactionContext.addCommand(1L, new org.ehcache.transactions.xa.internal.commands.StorePutCommand("old", new XAValueHolder("new", timeSource.getTimeMillis())));
        MatcherAssert.assertThat(xaTransactionContext.touched(1L), Is.is(true));
        MatcherAssert.assertThat(xaTransactionContext.removed(1L), Is.is(false));
        MatcherAssert.assertThat(xaTransactionContext.updated(1L), Is.is(true));
        MatcherAssert.assertThat(xaTransactionContext.evicted(1L), Is.is(false));
        MatcherAssert.assertThat(xaTransactionContext.newValueHolderOf(1L).get(), Matchers.equalTo("new"));
        MatcherAssert.assertThat(xaTransactionContext.oldValueOf(1L), Matchers.equalTo("old"));
        MatcherAssert.assertThat(xaTransactionContext.newValueOf(1L), Matchers.equalTo("new"));
        xaTransactionContext.addCommand(1L, new org.ehcache.transactions.xa.internal.commands.StoreRemoveCommand("old"));
        MatcherAssert.assertThat(xaTransactionContext.touched(1L), Is.is(true));
        MatcherAssert.assertThat(xaTransactionContext.removed(1L), Is.is(true));
        MatcherAssert.assertThat(xaTransactionContext.updated(1L), Is.is(false));
        MatcherAssert.assertThat(xaTransactionContext.evicted(1L), Is.is(false));
        MatcherAssert.assertThat(xaTransactionContext.newValueHolderOf(1L), Is.is(Matchers.nullValue()));
        MatcherAssert.assertThat(xaTransactionContext.oldValueOf(1L), Matchers.equalTo("old"));
        MatcherAssert.assertThat(xaTransactionContext.newValueOf(1L), Is.is(Matchers.nullValue()));
        xaTransactionContext.addCommand(1L, new org.ehcache.transactions.xa.internal.commands.StoreEvictCommand("old"));
        MatcherAssert.assertThat(xaTransactionContext.touched(1L), Is.is(true));
        MatcherAssert.assertThat(xaTransactionContext.removed(1L), Is.is(false));
        MatcherAssert.assertThat(xaTransactionContext.updated(1L), Is.is(false));
        MatcherAssert.assertThat(xaTransactionContext.evicted(1L), Is.is(true));
        MatcherAssert.assertThat(xaTransactionContext.newValueHolderOf(1L), Is.is(Matchers.nullValue()));
        MatcherAssert.assertThat(xaTransactionContext.oldValueOf(1L), Matchers.equalTo("old"));
        MatcherAssert.assertThat(xaTransactionContext.newValueOf(1L), Is.is(Matchers.nullValue()));
    }

    @Test
    public void testCommandsOverrideEachOther() {
        XATransactionContext<Long, String> xaTransactionContext = getXaTransactionContext();
        xaTransactionContext.addCommand(1L, new org.ehcache.transactions.xa.internal.commands.StorePutCommand("old", new XAValueHolder("new", timeSource.getTimeMillis())));
        MatcherAssert.assertThat(xaTransactionContext.touched(1L), Is.is(true));
        MatcherAssert.assertThat(xaTransactionContext.removed(1L), Is.is(false));
        MatcherAssert.assertThat(xaTransactionContext.updated(1L), Is.is(true));
        MatcherAssert.assertThat(xaTransactionContext.evicted(1L), Is.is(false));
        MatcherAssert.assertThat(xaTransactionContext.newValueHolderOf(1L).get(), Matchers.equalTo("new"));
        MatcherAssert.assertThat(xaTransactionContext.oldValueOf(1L), Matchers.equalTo("old"));
        MatcherAssert.assertThat(xaTransactionContext.newValueOf(1L), Matchers.equalTo("new"));
        xaTransactionContext.addCommand(1L, new org.ehcache.transactions.xa.internal.commands.StoreRemoveCommand("old"));
        MatcherAssert.assertThat(xaTransactionContext.touched(1L), Is.is(true));
        MatcherAssert.assertThat(xaTransactionContext.removed(1L), Is.is(true));
        MatcherAssert.assertThat(xaTransactionContext.updated(1L), Is.is(false));
        MatcherAssert.assertThat(xaTransactionContext.evicted(1L), Is.is(false));
        MatcherAssert.assertThat(xaTransactionContext.newValueHolderOf(1L), Is.is(Matchers.nullValue()));
        MatcherAssert.assertThat(xaTransactionContext.oldValueOf(1L), Matchers.equalTo("old"));
        MatcherAssert.assertThat(xaTransactionContext.newValueOf(1L), Is.is(Matchers.nullValue()));
        xaTransactionContext.addCommand(1L, new org.ehcache.transactions.xa.internal.commands.StoreRemoveCommand("old2"));
        MatcherAssert.assertThat(xaTransactionContext.touched(1L), Is.is(true));
        MatcherAssert.assertThat(xaTransactionContext.removed(1L), Is.is(true));
        MatcherAssert.assertThat(xaTransactionContext.updated(1L), Is.is(false));
        MatcherAssert.assertThat(xaTransactionContext.evicted(1L), Is.is(false));
        MatcherAssert.assertThat(xaTransactionContext.newValueHolderOf(1L), Is.is(Matchers.nullValue()));
        MatcherAssert.assertThat(xaTransactionContext.oldValueOf(1L), Matchers.equalTo("old2"));
        MatcherAssert.assertThat(xaTransactionContext.newValueOf(1L), Is.is(Matchers.nullValue()));
        xaTransactionContext.addCommand(1L, new org.ehcache.transactions.xa.internal.commands.StorePutCommand("old2", new XAValueHolder("new2", timeSource.getTimeMillis())));
        MatcherAssert.assertThat(xaTransactionContext.touched(1L), Is.is(true));
        MatcherAssert.assertThat(xaTransactionContext.removed(1L), Is.is(false));
        MatcherAssert.assertThat(xaTransactionContext.updated(1L), Is.is(true));
        MatcherAssert.assertThat(xaTransactionContext.evicted(1L), Is.is(false));
        MatcherAssert.assertThat(xaTransactionContext.newValueHolderOf(1L).get(), Matchers.equalTo("new2"));
        MatcherAssert.assertThat(xaTransactionContext.oldValueOf(1L), Matchers.equalTo("old2"));
        MatcherAssert.assertThat(xaTransactionContext.newValueOf(1L), Matchers.equalTo("new2"));
    }

    @Test
    public void testEvictCommandCannotBeOverridden() {
        XATransactionContext<Long, String> xaTransactionContext = getXaTransactionContext();
        xaTransactionContext.addCommand(1L, new org.ehcache.transactions.xa.internal.commands.StorePutCommand("old", new XAValueHolder("new", timeSource.getTimeMillis())));
        MatcherAssert.assertThat(xaTransactionContext.touched(1L), Is.is(true));
        MatcherAssert.assertThat(xaTransactionContext.removed(1L), Is.is(false));
        MatcherAssert.assertThat(xaTransactionContext.updated(1L), Is.is(true));
        MatcherAssert.assertThat(xaTransactionContext.evicted(1L), Is.is(false));
        MatcherAssert.assertThat(xaTransactionContext.newValueHolderOf(1L).get(), Matchers.equalTo("new"));
        MatcherAssert.assertThat(xaTransactionContext.oldValueOf(1L), Matchers.equalTo("old"));
        MatcherAssert.assertThat(xaTransactionContext.newValueOf(1L), Matchers.equalTo("new"));
        xaTransactionContext.addCommand(1L, new org.ehcache.transactions.xa.internal.commands.StoreEvictCommand("old"));
        MatcherAssert.assertThat(xaTransactionContext.touched(1L), Is.is(true));
        MatcherAssert.assertThat(xaTransactionContext.removed(1L), Is.is(false));
        MatcherAssert.assertThat(xaTransactionContext.updated(1L), Is.is(false));
        MatcherAssert.assertThat(xaTransactionContext.evicted(1L), Is.is(true));
        MatcherAssert.assertThat(xaTransactionContext.newValueHolderOf(1L), Is.is(Matchers.nullValue()));
        MatcherAssert.assertThat(xaTransactionContext.oldValueOf(1L), Matchers.equalTo("old"));
        MatcherAssert.assertThat(xaTransactionContext.newValueOf(1L), Is.is(Matchers.nullValue()));
        xaTransactionContext.addCommand(1L, new org.ehcache.transactions.xa.internal.commands.StorePutCommand("old2", new XAValueHolder("new2", timeSource.getTimeMillis())));
        MatcherAssert.assertThat(xaTransactionContext.touched(1L), Is.is(true));
        MatcherAssert.assertThat(xaTransactionContext.removed(1L), Is.is(false));
        MatcherAssert.assertThat(xaTransactionContext.updated(1L), Is.is(false));
        MatcherAssert.assertThat(xaTransactionContext.evicted(1L), Is.is(true));
        MatcherAssert.assertThat(xaTransactionContext.newValueHolderOf(1L), Is.is(Matchers.nullValue()));
        MatcherAssert.assertThat(xaTransactionContext.oldValueOf(1L), Matchers.equalTo("old"));
        MatcherAssert.assertThat(xaTransactionContext.newValueOf(1L), Is.is(Matchers.nullValue()));
    }

    @Test
    public void testHasTimedOut() {
        XATransactionContext<Long, String> xaTransactionContext = new XATransactionContext(new TransactionId(new TestXid(0, 0)), null, null, timeSource, ((timeSource.getTimeMillis()) + 30000));
        MatcherAssert.assertThat(xaTransactionContext.hasTimedOut(), Is.is(false));
        timeSource.advanceTime(30000);
        MatcherAssert.assertThat(xaTransactionContext.hasTimedOut(), Is.is(true));
    }

    @Test
    public void testPrepareReadOnly() throws Exception {
        XATransactionContext<Long, String> xaTransactionContext = getXaTransactionContext();
        MatcherAssert.assertThat(xaTransactionContext.prepare(), Is.is(0));
        Mockito.verify(journal, Mockito.times(1)).saveInDoubt(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))), ArgumentMatchers.eq(Collections.emptySet()));
        Mockito.verify(journal, Mockito.times(0)).saveCommitted(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))), ArgumentMatchers.anyBoolean());
        Mockito.verify(journal, Mockito.times(1)).saveRolledBack(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))), ArgumentMatchers.eq(false));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPrepare() throws Exception {
        XATransactionContext<Long, String> xaTransactionContext = getXaTransactionContext();
        xaTransactionContext.addCommand(1L, new org.ehcache.transactions.xa.internal.commands.StorePutCommand(null, new XAValueHolder("un", timeSource.getTimeMillis())));
        xaTransactionContext.addCommand(2L, new org.ehcache.transactions.xa.internal.commands.StoreRemoveCommand("two"));
        xaTransactionContext.addCommand(3L, new org.ehcache.transactions.xa.internal.commands.StoreEvictCommand("three"));
        Store.ValueHolder<SoftLock<String>> mockValueHolder = Mockito.mock(Store.ValueHolder.class);
        Mockito.when(mockValueHolder.get()).thenReturn(new SoftLock(null, "two", null));
        Mockito.when(underlyingStore.get(ArgumentMatchers.eq(2L))).thenReturn(mockValueHolder);
        Mockito.when(underlyingStore.replace(ArgumentMatchers.eq(2L), ArgumentMatchers.eq(new SoftLock(null, "two", null)), ArgumentMatchers.eq(new SoftLock(new TransactionId(new TestXid(0, 0)), "two", null)))).thenReturn(HIT);
        AtomicReference<Collection<Long>> savedInDoubt = new AtomicReference<>();
        // doAnswer is required to make a copy of the keys collection because xaTransactionContext.prepare() clears it before the verify(journal, times(1)).saveInDoubt(...) assertion can be made.
        // See: http://stackoverflow.com/questions/17027368/mockito-what-if-argument-passed-to-mock-is-modified
        Mockito.doAnswer(( invocation) -> {
            Collection<Long> o = ((Collection<Long>) (invocation.getArguments()[1]));
            savedInDoubt.set(new HashSet<>(o));
            return null;
        }).when(journal).saveInDoubt(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))), ArgumentMatchers.any(Collection.class));
        MatcherAssert.assertThat(xaTransactionContext.prepare(), Is.is(3));
        Assert.assertThat(savedInDoubt.get(), Matchers.containsInAnyOrder(1L, 2L, 3L));
        Mockito.verify(journal, Mockito.times(1)).saveInDoubt(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))), ArgumentMatchers.any(Collection.class));
        Mockito.verify(journal, Mockito.times(0)).saveCommitted(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))), ArgumentMatchers.anyBoolean());
        Mockito.verify(journal, Mockito.times(0)).saveRolledBack(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))), ArgumentMatchers.anyBoolean());
        Mockito.verify(underlyingStore, Mockito.times(0)).get(1L);
        Mockito.verify(underlyingStore, Mockito.times(1)).putIfAbsent(ArgumentMatchers.eq(1L), ArgumentMatchers.eq(new SoftLock(new TransactionId(new TestXid(0, 0)), null, new XAValueHolder("un", timeSource.getTimeMillis()))), ArgumentMatchers.any(Consumer.class));
        Mockito.verify(underlyingStore, Mockito.times(0)).get(2L);
        Mockito.verify(underlyingStore, Mockito.times(1)).replace(ArgumentMatchers.eq(2L), ArgumentMatchers.eq(new SoftLock(null, "two", null)), ArgumentMatchers.eq(new SoftLock(new TransactionId(new TestXid(0, 0)), "two", null)));
        Mockito.verify(underlyingStore, Mockito.times(0)).get(3L);
        Mockito.verify(underlyingStore, Mockito.times(1)).remove(ArgumentMatchers.eq(3L));
    }

    @Test
    public void testCommitNotPreparedInFlightThrows() throws Exception {
        XATransactionContext<Long, String> xaTransactionContext = getXaTransactionContext();
        xaTransactionContext.addCommand(1L, new org.ehcache.transactions.xa.internal.commands.StorePutCommand("one", new XAValueHolder("un", timeSource.getTimeMillis())));
        xaTransactionContext.addCommand(2L, new org.ehcache.transactions.xa.internal.commands.StorePutCommand("two", new XAValueHolder("deux", timeSource.getTimeMillis())));
        @SuppressWarnings("unchecked")
        Store.ValueHolder<SoftLock<String>> mockValueHolder = Mockito.mock(Store.ValueHolder.class);
        Mockito.when(mockValueHolder.get()).thenReturn(new SoftLock(null, "two", null));
        Mockito.when(underlyingStore.get(ArgumentMatchers.eq(2L))).thenReturn(mockValueHolder);
        try {
            xaTransactionContext.commit(false);
            Assert.fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ise) {
            // expected
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCommit() throws Exception {
        XATransactionContext<Long, String> xaTransactionContext = getXaTransactionContext();
        xaTransactionContext.addCommand(1L, new org.ehcache.transactions.xa.internal.commands.StorePutCommand("one", new XAValueHolder("un", timeSource.getTimeMillis())));
        xaTransactionContext.addCommand(2L, new org.ehcache.transactions.xa.internal.commands.StoreRemoveCommand("two"));
        xaTransactionContext.addCommand(3L, new org.ehcache.transactions.xa.internal.commands.StoreEvictCommand("three"));
        Store.ValueHolder<SoftLock<String>> mockValueHolder1 = Mockito.mock(Store.ValueHolder.class);
        Mockito.when(mockValueHolder1.get()).thenReturn(new SoftLock(new TransactionId(new TestXid(0, 0)), "one", new XAValueHolder("un", timeSource.getTimeMillis())));
        Mockito.when(underlyingStore.get(ArgumentMatchers.eq(1L))).thenReturn(mockValueHolder1);
        Store.ValueHolder<SoftLock<String>> mockValueHolder2 = Mockito.mock(Store.ValueHolder.class);
        Mockito.when(mockValueHolder2.get()).thenReturn(new SoftLock(new TransactionId(new TestXid(0, 0)), "two", null));
        Mockito.when(underlyingStore.get(ArgumentMatchers.eq(2L))).thenReturn(mockValueHolder2);
        Store.ValueHolder<SoftLock<String>> mockValueHolder3 = Mockito.mock(Store.ValueHolder.class);
        Mockito.when(mockValueHolder3.get()).thenReturn(new SoftLock(new TransactionId(new TestXid(0, 0)), "three", null));
        Mockito.when(underlyingStore.get(ArgumentMatchers.eq(3L))).thenReturn(mockValueHolder3);
        Mockito.when(journal.isInDoubt(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))))).thenReturn(true);
        Mockito.when(journal.getInDoubtKeys(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))))).thenReturn(Arrays.asList(1L, 2L, 3L));
        Mockito.when(underlyingStore.replace(ArgumentMatchers.any(Long.class), ArgumentMatchers.any(SoftLock.class), ArgumentMatchers.any(SoftLock.class))).thenReturn(MISS_NOT_PRESENT);
        Mockito.when(underlyingStore.remove(ArgumentMatchers.any(Long.class), ArgumentMatchers.any(SoftLock.class))).thenReturn(KEY_MISSING);
        xaTransactionContext.commit(false);
        Mockito.verify(journal, Mockito.times(1)).saveCommitted(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))), ArgumentMatchers.eq(false));
        Mockito.verify(journal, Mockito.times(0)).saveRolledBack(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))), ArgumentMatchers.anyBoolean());
        Mockito.verify(journal, Mockito.times(0)).saveInDoubt(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))), ArgumentMatchers.any(Collection.class));
        Mockito.verify(underlyingStore, Mockito.times(1)).get(1L);
        Mockito.verify(underlyingStore, Mockito.times(1)).replace(ArgumentMatchers.eq(1L), ArgumentMatchers.eq(new SoftLock(new TransactionId(new TestXid(0, 0)), "one", new XAValueHolder("un", timeSource.getTimeMillis()))), ArgumentMatchers.eq(new SoftLock(null, "un", null)));
        Mockito.verify(underlyingStore, Mockito.times(1)).get(2L);
        Mockito.verify(underlyingStore, Mockito.times(1)).remove(ArgumentMatchers.eq(2L), ArgumentMatchers.eq(new SoftLock(new TransactionId(new TestXid(0, 0)), "two", null)));
        Mockito.verify(underlyingStore, Mockito.times(1)).get(3L);
        Mockito.verify(underlyingStore, Mockito.times(1)).remove(ArgumentMatchers.eq(3L));
    }

    @Test
    public void testCommitInOnePhasePreparedThrows() throws Exception {
        XATransactionContext<Long, String> xaTransactionContext = getXaTransactionContext();
        Mockito.when(journal.isInDoubt(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))))).thenReturn(true);
        try {
            xaTransactionContext.commitInOnePhase();
            Assert.fail("expected IllegalStateException");
        } catch (IllegalStateException ise) {
            // expected
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCommitInOnePhase() throws Exception {
        XATransactionContext<Long, String> xaTransactionContext = getXaTransactionContext();
        xaTransactionContext.addCommand(1L, new org.ehcache.transactions.xa.internal.commands.StorePutCommand(null, new XAValueHolder("un", timeSource.getTimeMillis())));
        xaTransactionContext.addCommand(2L, new org.ehcache.transactions.xa.internal.commands.StoreRemoveCommand("two"));
        xaTransactionContext.addCommand(3L, new org.ehcache.transactions.xa.internal.commands.StoreEvictCommand("three"));
        Store.ValueHolder<SoftLock<String>> mockValueHolder = Mockito.mock(Store.ValueHolder.class);
        Mockito.when(mockValueHolder.get()).thenReturn(new SoftLock(null, "two", null));
        Mockito.when(underlyingStore.get(ArgumentMatchers.eq(2L))).thenReturn(mockValueHolder);
        AtomicReference<Collection<Long>> savedInDoubtCollectionRef = new AtomicReference<>();
        Mockito.doAnswer(( invocation) -> {
            savedInDoubtCollectionRef.set(new HashSet<>(((Collection<Long>) (invocation.getArguments()[1]))));
            return null;
        }).when(journal).saveInDoubt(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))), ArgumentMatchers.any(Collection.class));
        Mockito.when(journal.isInDoubt(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))))).then(( invocation) -> (savedInDoubtCollectionRef.get()) != null);
        Mockito.when(journal.getInDoubtKeys(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))))).then(( invocation) -> savedInDoubtCollectionRef.get());
        AtomicReference<SoftLock<Object>> softLock1Ref = new AtomicReference<>();
        Mockito.when(underlyingStore.get(ArgumentMatchers.eq(1L))).then(( invocation) -> (softLock1Ref.get()) == null ? null : new AbstractValueHolder<Object>((-1), (-1)) {
            @Override
            public Object get() {
                return softLock1Ref.get();
            }
        });
        Mockito.when(underlyingStore.putIfAbsent(ArgumentMatchers.eq(1L), ArgumentMatchers.isA(SoftLock.class), ArgumentMatchers.any(Consumer.class))).then(( invocation) -> {
            softLock1Ref.set(((SoftLock) (invocation.getArguments()[1])));
            return null;
        });
        Mockito.when(underlyingStore.replace(ArgumentMatchers.eq(1L), ArgumentMatchers.isA(SoftLock.class), ArgumentMatchers.isA(SoftLock.class))).then(( invocation) -> {
            if ((softLock1Ref.get()) != null) {
                return ReplaceStatus.HIT;
            }
            return ReplaceStatus.MISS_PRESENT;
        });
        AtomicReference<SoftLock<Object>> softLock2Ref = new AtomicReference(new SoftLock(null, "two", null));
        Mockito.when(underlyingStore.get(ArgumentMatchers.eq(2L))).then(( invocation) -> (softLock2Ref.get()) == null ? null : new AbstractValueHolder<Object>((-1), (-1)) {
            @Override
            public Object get() {
                return softLock2Ref.get();
            }
        });
        Mockito.when(underlyingStore.replace(ArgumentMatchers.eq(2L), ArgumentMatchers.isA(SoftLock.class), ArgumentMatchers.isA(SoftLock.class))).then(( invocation) -> {
            softLock2Ref.set(((SoftLock) (invocation.getArguments()[2])));
            return ReplaceStatus.HIT;
        });
        Mockito.when(underlyingStore.remove(ArgumentMatchers.any(Long.class), ArgumentMatchers.any(SoftLock.class))).thenReturn(REMOVED);
        xaTransactionContext.commitInOnePhase();
        Assert.assertThat(savedInDoubtCollectionRef.get(), Matchers.containsInAnyOrder(1L, 2L, 3L));
        Mockito.verify(journal, Mockito.times(1)).saveCommitted(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))), ArgumentMatchers.eq(false));
        Mockito.verify(journal, Mockito.times(0)).saveRolledBack(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))), ArgumentMatchers.anyBoolean());
        Mockito.verify(journal, Mockito.times(1)).saveInDoubt(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))), ArgumentMatchers.any(Collection.class));
        Mockito.verify(underlyingStore, Mockito.times(1)).putIfAbsent(ArgumentMatchers.eq(1L), ArgumentMatchers.eq(new SoftLock(new TransactionId(new TestXid(0, 0)), null, new XAValueHolder("un", timeSource.getTimeMillis()))), ArgumentMatchers.any(Consumer.class));
        Mockito.verify(underlyingStore, Mockito.times(1)).replace(ArgumentMatchers.eq(2L), ArgumentMatchers.eq(new SoftLock(null, "two", null)), ArgumentMatchers.eq(new SoftLock(new TransactionId(new TestXid(0, 0)), "two", null)));
        Mockito.verify(underlyingStore, Mockito.times(1)).remove(ArgumentMatchers.eq(3L));
        Mockito.verify(underlyingStore, Mockito.times(1)).get(1L);
        Mockito.verify(underlyingStore, Mockito.times(1)).replace(ArgumentMatchers.eq(1L), ArgumentMatchers.eq(new SoftLock(new TransactionId(new TestXid(0, 0)), null, new XAValueHolder("un", timeSource.getTimeMillis()))), ArgumentMatchers.eq(new SoftLock(null, "un", null)));
        Mockito.verify(underlyingStore, Mockito.times(1)).get(2L);
        Mockito.verify(underlyingStore, Mockito.times(1)).remove(ArgumentMatchers.eq(2L), ArgumentMatchers.eq(new SoftLock(new TransactionId(new TestXid(0, 0)), "two", null)));
        Mockito.verify(underlyingStore, Mockito.times(1)).get(3L);
        Mockito.verify(underlyingStore, Mockito.times(1)).remove(ArgumentMatchers.eq(3L));
    }

    @Test
    public void testRollbackPhase1() throws Exception {
        XATransactionContext<Long, String> xaTransactionContext = getXaTransactionContext();
        xaTransactionContext.addCommand(1L, new org.ehcache.transactions.xa.internal.commands.StorePutCommand("one", new XAValueHolder("un", timeSource.getTimeMillis())));
        xaTransactionContext.addCommand(2L, new org.ehcache.transactions.xa.internal.commands.StoreRemoveCommand("two"));
        xaTransactionContext.rollback(false);
        Mockito.verifyNoMoreInteractions(underlyingStore);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRollbackPhase2() throws Exception {
        XATransactionContext<Long, String> xaTransactionContext = getXaTransactionContext();
        xaTransactionContext.addCommand(1L, new org.ehcache.transactions.xa.internal.commands.StorePutCommand("one", new XAValueHolder("un", timeSource.getTimeMillis())));
        xaTransactionContext.addCommand(2L, new org.ehcache.transactions.xa.internal.commands.StoreRemoveCommand("two"));
        Mockito.when(journal.isInDoubt(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))))).thenReturn(true);
        Mockito.when(journal.getInDoubtKeys(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))))).thenReturn(Arrays.asList(1L, 2L));
        Mockito.when(underlyingStore.get(1L)).thenReturn(new org.ehcache.core.spi.store.AbstractValueHolder<SoftLock<String>>((-1), (-1)) {
            @Override
            public SoftLock<String> get() {
                return new SoftLock(new TransactionId(new TestXid(0, 0)), "one", new XAValueHolder("un", timeSource.getTimeMillis()));
            }
        });
        Mockito.when(underlyingStore.get(2L)).thenReturn(new org.ehcache.core.spi.store.AbstractValueHolder<SoftLock<String>>((-1), (-1)) {
            @Override
            public SoftLock<String> get() {
                return new SoftLock(new TransactionId(new TestXid(0, 0)), "two", null);
            }
        });
        Mockito.when(underlyingStore.replace(ArgumentMatchers.any(Long.class), ArgumentMatchers.any(SoftLock.class), ArgumentMatchers.any(SoftLock.class))).thenReturn(HIT);
        xaTransactionContext.rollback(false);
        Mockito.verify(underlyingStore, Mockito.times(1)).get(1L);
        Mockito.verify(underlyingStore, Mockito.times(1)).replace(ArgumentMatchers.eq(1L), ArgumentMatchers.eq(new SoftLock(new TransactionId(new TestXid(0, 0)), "one", new XAValueHolder("un", timeSource.getTimeMillis()))), ArgumentMatchers.eq(new SoftLock(null, "one", null)));
        Mockito.verify(underlyingStore, Mockito.times(1)).get(2L);
        Mockito.verify(underlyingStore, Mockito.times(1)).replace(ArgumentMatchers.eq(2L), ArgumentMatchers.eq(new SoftLock(new TransactionId(new TestXid(0, 0)), "two", null)), ArgumentMatchers.eq(new SoftLock(null, "two", null)));
    }

    @Test
    public void testCommitInOnePhaseTimeout() throws Exception {
        XATransactionContext<Long, String> xaTransactionContext = getXaTransactionContext();
        xaTransactionContext.addCommand(1L, new org.ehcache.transactions.xa.internal.commands.StorePutCommand("one", new XAValueHolder("un", timeSource.getTimeMillis())));
        xaTransactionContext.addCommand(2L, new org.ehcache.transactions.xa.internal.commands.StoreRemoveCommand("two"));
        timeSource.advanceTime(30000);
        try {
            xaTransactionContext.commitInOnePhase();
            Assert.fail("expected TransactionTimeoutException");
        } catch (XATransactionContext tte) {
            // expected
        }
    }

    @Test
    public void testPrepareTimeout() throws Exception {
        XATransactionContext<Long, String> xaTransactionContext = getXaTransactionContext();
        xaTransactionContext.addCommand(1L, new org.ehcache.transactions.xa.internal.commands.StorePutCommand("one", new XAValueHolder("un", timeSource.getTimeMillis())));
        xaTransactionContext.addCommand(2L, new org.ehcache.transactions.xa.internal.commands.StoreRemoveCommand("two"));
        timeSource.advanceTime(30000);
        try {
            xaTransactionContext.prepare();
            Assert.fail("expected TransactionTimeoutException");
        } catch (XATransactionContext tte) {
            // expected
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCommitConflictsEvicts() throws Exception {
        XATransactionContext<Long, String> xaTransactionContext = getXaTransactionContext();
        Mockito.when(journal.isInDoubt(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))))).thenReturn(true);
        Mockito.when(journal.getInDoubtKeys(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))))).thenReturn(Arrays.asList(1L, 2L));
        Mockito.when(underlyingStore.get(ArgumentMatchers.eq(1L))).thenReturn(new org.ehcache.core.spi.store.AbstractValueHolder<SoftLock<String>>((-1), (-1)) {
            @Override
            public SoftLock<String> get() {
                return new SoftLock(new TransactionId(new TestXid(0, 0)), "old1", new XAValueHolder("new1", timeSource.getTimeMillis()));
            }
        });
        Mockito.when(underlyingStore.get(ArgumentMatchers.eq(2L))).thenReturn(new org.ehcache.core.spi.store.AbstractValueHolder<SoftLock<String>>((-1), (-1)) {
            @Override
            public SoftLock<String> get() {
                return new SoftLock(new TransactionId(new TestXid(0, 0)), "old2", null);
            }
        });
        Mockito.when(underlyingStore.replace(ArgumentMatchers.any(Long.class), ArgumentMatchers.any(SoftLock.class), ArgumentMatchers.any(SoftLock.class))).thenReturn(MISS_NOT_PRESENT);
        Mockito.when(underlyingStore.remove(ArgumentMatchers.any(Long.class), ArgumentMatchers.any(SoftLock.class))).thenReturn(KEY_MISSING);
        xaTransactionContext.commit(false);
        Mockito.verify(underlyingStore, Mockito.times(1)).replace(ArgumentMatchers.eq(1L), ArgumentMatchers.eq(new SoftLock(new TransactionId(new TestXid(0, 0)), "old1", new XAValueHolder("new1", timeSource.getTimeMillis()))), ArgumentMatchers.eq(new SoftLock(null, "new1", null)));
        Mockito.verify(underlyingStore, Mockito.times(1)).remove(ArgumentMatchers.eq(1L));
        Mockito.verify(underlyingStore, Mockito.times(1)).remove(ArgumentMatchers.eq(2L), ArgumentMatchers.eq(new SoftLock(new TransactionId(new TestXid(0, 0)), "old2", null)));
        Mockito.verify(underlyingStore, Mockito.times(1)).remove(ArgumentMatchers.eq(2L));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPrepareConflictsEvicts() throws Exception {
        XATransactionContext<Long, String> xaTransactionContext = getXaTransactionContext();
        xaTransactionContext.addCommand(1L, new org.ehcache.transactions.xa.internal.commands.StorePutCommand("one", new XAValueHolder("un", timeSource.getTimeMillis())));
        xaTransactionContext.addCommand(2L, new org.ehcache.transactions.xa.internal.commands.StoreRemoveCommand("two"));
        Mockito.when(underlyingStore.replace(ArgumentMatchers.any(Long.class), ArgumentMatchers.any(SoftLock.class), ArgumentMatchers.any(SoftLock.class))).thenReturn(MISS_NOT_PRESENT);
        xaTransactionContext.prepare();
        Mockito.verify(underlyingStore).replace(ArgumentMatchers.eq(1L), ArgumentMatchers.eq(new SoftLock(null, "one", null)), ArgumentMatchers.eq(new SoftLock(new TransactionId(new TestXid(0, 0)), "one", new XAValueHolder("un", timeSource.getTimeMillis()))));
        Mockito.verify(underlyingStore).remove(1L);
        Mockito.verify(underlyingStore).replace(ArgumentMatchers.eq(2L), ArgumentMatchers.eq(new SoftLock(null, "two", null)), ArgumentMatchers.eq(new SoftLock(new TransactionId(new TestXid(0, 0)), "two", null)));
        Mockito.verify(underlyingStore).remove(2L);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRollbackConflictsEvicts() throws Exception {
        XATransactionContext<Long, String> xaTransactionContext = getXaTransactionContext();
        Mockito.when(journal.isInDoubt(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))))).thenReturn(true);
        Mockito.when(journal.getInDoubtKeys(ArgumentMatchers.eq(new TransactionId(new TestXid(0, 0))))).thenReturn(Arrays.asList(1L, 2L));
        Mockito.when(underlyingStore.get(ArgumentMatchers.eq(1L))).thenReturn(new org.ehcache.core.spi.store.AbstractValueHolder<SoftLock<String>>((-1), (-1)) {
            @Override
            public SoftLock<String> get() {
                return new SoftLock(new TransactionId(new TestXid(0, 0)), "old1", new XAValueHolder("new1", timeSource.getTimeMillis()));
            }
        });
        Mockito.when(underlyingStore.get(ArgumentMatchers.eq(2L))).thenReturn(new org.ehcache.core.spi.store.AbstractValueHolder<SoftLock<String>>((-1), (-1)) {
            @Override
            public SoftLock<String> get() {
                return new SoftLock(new TransactionId(new TestXid(0, 0)), "old2", null);
            }
        });
        Mockito.when(underlyingStore.replace(ArgumentMatchers.any(Long.class), ArgumentMatchers.any(SoftLock.class), ArgumentMatchers.any(SoftLock.class))).thenReturn(MISS_NOT_PRESENT);
        Mockito.when(underlyingStore.remove(ArgumentMatchers.any(Long.class), ArgumentMatchers.any(SoftLock.class))).thenReturn(KEY_MISSING);
        xaTransactionContext.rollback(false);
        Mockito.verify(underlyingStore, Mockito.times(1)).replace(ArgumentMatchers.eq(1L), ArgumentMatchers.eq(new SoftLock(new TransactionId(new TestXid(0, 0)), "old1", new XAValueHolder("new1", timeSource.getTimeMillis()))), ArgumentMatchers.eq(new SoftLock(null, "old1", null)));
        Mockito.verify(underlyingStore, Mockito.times(1)).remove(ArgumentMatchers.eq(1L));
        Mockito.verify(underlyingStore, Mockito.times(1)).replace(ArgumentMatchers.eq(2L), ArgumentMatchers.eq(new SoftLock(new TransactionId(new TestXid(0, 0)), "old2", null)), ArgumentMatchers.eq(new SoftLock(null, "old2", null)));
        Mockito.verify(underlyingStore, Mockito.times(1)).remove(ArgumentMatchers.eq(2L));
    }
}

