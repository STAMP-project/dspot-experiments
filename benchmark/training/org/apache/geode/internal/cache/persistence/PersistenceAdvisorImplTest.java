/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.cache.persistence;


import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import org.apache.geode.internal.cache.CacheDistributionAdvisor;
import org.junit.Test;


public class PersistenceAdvisorImplTest {
    private static final long TIME_STAMP_1 = 1530300988488L;

    private static final long TIME_STAMP_2 = 1530301340401L;

    private static final long TIME_STAMP_3 = 1530301598541L;

    private static final long TIME_STAMP_4 = 1530301598616L;

    private static final String SET_POSITION_1 = "a";

    private static final String SET_POSITION_2 = "b";

    private static final String SET_POSITION_3 = "c";

    private static final String SET_POSITION_4 = "d";

    private static final String SET_POSITION_5 = "e";

    private static final String SET_POSITION_6 = "f";

    private static final String SET_POSITION_7 = "g";

    private CacheDistributionAdvisor cacheDistributionAdvisor;

    private PersistentMemberView persistentMemberView;

    private PersistentStateQueryResults persistentStateQueryResults;

    private PersistenceAdvisorImpl persistenceAdvisorImpl;

    private int diskStoreIDIndex = 92837487;// some random number


    /**
     * GEODE-5402: This test creates a scenario where a member has two versions (based on timeStamp)
     * of another member. The call to getMembersToWaitFor should return that we wait for neither of
     * them.
     */
    @Test
    public void getMembersToWaitForRemovesAllMembersWhenDiskStoreListedTwice() {
        DiskStoreID diskStoreID = getNewDiskStoreID();
        Set<PersistentMemberID> previouslyOnlineMembers = new HashSet<>();
        previouslyOnlineMembers.add(createPersistentMemberID(diskStoreID, PersistenceAdvisorImplTest.TIME_STAMP_1));
        previouslyOnlineMembers.add(createPersistentMemberID(diskStoreID, PersistenceAdvisorImplTest.TIME_STAMP_3));
        getMembersToWaitForRemovesAllMembers(diskStoreID, previouslyOnlineMembers);
    }

    @Test
    public void getMembersToWaitForDoesNotWaitForMemberWhoIsNotInitialized() {
        DiskStoreID diskStoreID = getNewDiskStoreID();
        Set<PersistentMemberID> previouslyOnlineMembers = new HashSet<>();
        previouslyOnlineMembers.add(createPersistentMemberID(diskStoreID, PersistenceAdvisorImplTest.TIME_STAMP_1));
        getMembersToWaitForRemovesAllMembers(diskStoreID, previouslyOnlineMembers);
    }

    @Test
    public void removeOlderMembersHandlesEmptySet() {
        Set<PersistentMemberID> aSet = new HashSet<>();
        persistenceAdvisorImpl.removeOlderMembers(aSet);
        assertThat(aSet).isEmpty();
    }

    @Test
    public void removeOlderMembersWithEqualTimeStampsInDifferentDiskStores() {
        long timeStamp = 239874;// anything

        // all diskStoreIDs in set are different so nothing to remove
        Set<PersistentMemberID> aSet = new HashSet<>();
        aSet.add(createPersistentMemberID(getNewDiskStoreID(), timeStamp));
        aSet.add(createPersistentMemberID(getNewDiskStoreID(), timeStamp));
        aSet.add(createPersistentMemberID(getNewDiskStoreID(), timeStamp));
        aSet.add(createPersistentMemberID(getNewDiskStoreID(), timeStamp));
        persistenceAdvisorImpl.removeOlderMembers(aSet);
        assertThat(aSet).hasSize(4);
    }

    @Test
    public void removeOlderMembersWhenFirstInSetIsOlder() {
        DiskStoreID diskStoreID = getNewDiskStoreID();
        Set<PersistentMemberID> aSet = new java.util.TreeSet(new PersistenceAdvisorImplTest.PersistentMemberIDComparator());
        aSet.add(createPersistentMemberID(diskStoreID, 1, PersistenceAdvisorImplTest.SET_POSITION_1));
        PersistentMemberID newest = createPersistentMemberID(diskStoreID, 2, PersistenceAdvisorImplTest.SET_POSITION_2);
        aSet.add(newest);
        persistenceAdvisorImpl.removeOlderMembers(aSet);
        assertThat(aSet).containsExactly(newest);
    }

    @Test
    public void removeOlderMembersWhenFirstInSetIsNewer() {
        DiskStoreID diskStoreID = getNewDiskStoreID();
        Set<PersistentMemberID> aSet = new java.util.TreeSet(new PersistenceAdvisorImplTest.PersistentMemberIDComparator());
        PersistentMemberID newest = createPersistentMemberID(diskStoreID, 2, PersistenceAdvisorImplTest.SET_POSITION_1);
        aSet.add(newest);
        aSet.add(createPersistentMemberID(diskStoreID, 1, PersistenceAdvisorImplTest.SET_POSITION_2));
        persistenceAdvisorImpl.removeOlderMembers(aSet);
        assertThat(aSet).containsExactly(newest);
    }

    @Test
    public void removeOlderMembersWithMultipleRemovals() {
        DiskStoreID diskStoreID1 = getNewDiskStoreID();
        DiskStoreID diskStoreID2 = getNewDiskStoreID();
        DiskStoreID diskStoreID3 = getNewDiskStoreID();
        Set<PersistentMemberID> aSet = new java.util.TreeSet(new PersistenceAdvisorImplTest.PersistentMemberIDComparator());
        PersistentMemberID id_1 = createPersistentMemberID(diskStoreID1, 10, PersistenceAdvisorImplTest.SET_POSITION_1);
        PersistentMemberID id_2 = createPersistentMemberID(diskStoreID2, 10, PersistenceAdvisorImplTest.SET_POSITION_2);
        PersistentMemberID id_3 = createPersistentMemberID(diskStoreID3, 40, PersistenceAdvisorImplTest.SET_POSITION_3);
        PersistentMemberID id_4 = createPersistentMemberID(diskStoreID3, 10, PersistenceAdvisorImplTest.SET_POSITION_4);
        PersistentMemberID id_5 = createPersistentMemberID(diskStoreID2, 50, PersistenceAdvisorImplTest.SET_POSITION_5);
        PersistentMemberID id_6 = createPersistentMemberID(diskStoreID2, 70, PersistenceAdvisorImplTest.SET_POSITION_6);
        PersistentMemberID id_7 = createPersistentMemberID(diskStoreID2, 60, PersistenceAdvisorImplTest.SET_POSITION_7);
        aSet.add(id_1);
        aSet.add(id_2);
        aSet.add(id_3);
        aSet.add(id_4);
        aSet.add(id_5);
        aSet.add(id_6);
        aSet.add(id_7);
        persistenceAdvisorImpl.removeOlderMembers(aSet);
        assertThat(aSet).containsExactly(id_1, id_3, id_6);
    }

    private class PersistentMemberIDComparator implements Comparator<PersistentMemberID> {
        @Override
        public int compare(PersistentMemberID id1, PersistentMemberID id2) {
            return id1.getName().compareTo(id2.getName());
        }
    }
}

