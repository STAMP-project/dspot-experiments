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
package org.ehcache.transactions.xa.internal.journal;


import java.util.Arrays;
import org.ehcache.transactions.xa.internal.TransactionId;
import org.ehcache.transactions.xa.utils.TestXid;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 *
 *
 * @author Ludovic Orban
 */
public class PersistentJournalTest extends AbstractJournalTest {
    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testPersistence() throws Exception {
        journal.saveInDoubt(new TransactionId(new TestXid(0, 0)), Arrays.asList(1L, 2L, 3L));
        journal.saveInDoubt(new TransactionId(new TestXid(1, 0)), Arrays.asList(4L, 5L, 6L));
        journal.saveCommitted(new TransactionId(new TestXid(1, 0)), true);
        journal.close();
        journal = createJournal();
        journal.open();
        Assert.assertThat(journal.recover().keySet(), Matchers.containsInAnyOrder(new TransactionId(new TestXid(0, 0))));
        Assert.assertThat(journal.heuristicDecisions().keySet(), Matchers.containsInAnyOrder(new TransactionId(new TestXid(1, 0))));
        journal.saveRolledBack(new TransactionId(new TestXid(0, 0)), false);
        journal.forget(new TransactionId(new TestXid(1, 0)));
        journal.close();
        journal = createJournal();
        journal.open();
        Assert.assertThat(journal.recover().isEmpty(), Is.is(true));
        Assert.assertThat(journal.heuristicDecisions().isEmpty(), Is.is(true));
    }
}

