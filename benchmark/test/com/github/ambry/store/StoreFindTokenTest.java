/**
 * Copyright 2016 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package com.github.ambry.store;


import StoreFindToken.VERSION_0;
import StoreFindToken.VERSION_1;
import StoreFindToken.VERSION_2;
import com.github.ambry.utils.Pair;
import com.github.ambry.utils.Utils;
import com.github.ambry.utils.UtilsTest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests for {@link StoreFindToken}. Tests both segmented and non segmented log use cases.
 */
@RunWith(Parameterized.class)
public class StoreFindTokenTest {
    private static final StoreKeyFactory STORE_KEY_FACTORY;

    static {
        try {
            STORE_KEY_FACTORY = Utils.getObj("com.github.ambry.store.MockIdFactory");
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private final boolean isLogSegmented;

    public StoreFindTokenTest(boolean isLogSegmented) {
        this.isLogSegmented = isLogSegmented;
    }

    /**
     * Tests the correctness of {@link StoreFindToken#equals(Object)}.
     */
    @Test
    public void equalityTest() {
        UUID sessionId = UUID.randomUUID();
        UUID incarnationId = UUID.randomUUID();
        String logSegmentName = LogSegmentNameHelper.generateFirstSegmentName(isLogSegmented);
        Offset offset = new Offset(logSegmentName, 0);
        Offset otherOffset = new Offset(logSegmentName, 1);
        MockId key = new MockId(UtilsTest.getRandomString(10));
        MockId otherKey = new MockId(UtilsTest.getRandomString(10));
        StoreFindToken initToken = new StoreFindToken();
        StoreFindToken otherInitToken = new StoreFindToken();
        StoreFindToken indexToken = new StoreFindToken(key, offset, sessionId, incarnationId);
        StoreFindToken otherIndexToken = new StoreFindToken(key, offset, sessionId, incarnationId);
        StoreFindToken journalToken = new StoreFindToken(offset, sessionId, incarnationId, false);
        StoreFindToken otherJournalToken = new StoreFindToken(offset, sessionId, incarnationId, false);
        StoreFindToken inclusiveJournalToken = new StoreFindToken(offset, sessionId, incarnationId, true);
        StoreFindToken otherInclusiveJournalToken = new StoreFindToken(offset, sessionId, incarnationId, true);
        // equality
        compareTokens(initToken, initToken);
        compareTokens(initToken, otherInitToken);
        compareTokens(indexToken, indexToken);
        compareTokens(indexToken, otherIndexToken);
        compareTokens(journalToken, journalToken);
        compareTokens(journalToken, otherJournalToken);
        compareTokens(inclusiveJournalToken, otherInclusiveJournalToken);
        UUID newSessionId = getRandomUUID(sessionId);
        UUID newIncarnationId = getRandomUUID(incarnationId);
        // equality even if session IDs are different
        compareTokens(indexToken, new StoreFindToken(key, offset, newSessionId, incarnationId));
        compareTokens(journalToken, new StoreFindToken(offset, newSessionId, incarnationId, false));
        // equality even if incarnation IDs are different
        compareTokens(indexToken, new StoreFindToken(key, offset, sessionId, newIncarnationId));
        compareTokens(journalToken, new StoreFindToken(offset, sessionId, newIncarnationId, false));
        // inequality if some fields differ
        List<Pair<StoreFindToken, StoreFindToken>> unequalPairs = new ArrayList<>();
        unequalPairs.add(new Pair(initToken, indexToken));
        unequalPairs.add(new Pair(initToken, journalToken));
        unequalPairs.add(new Pair(initToken, inclusiveJournalToken));
        unequalPairs.add(new Pair(indexToken, journalToken));
        unequalPairs.add(new Pair(indexToken, inclusiveJournalToken));
        unequalPairs.add(new Pair(indexToken, new StoreFindToken(key, otherOffset, sessionId, incarnationId)));
        unequalPairs.add(new Pair(indexToken, new StoreFindToken(otherKey, offset, sessionId, incarnationId)));
        unequalPairs.add(new Pair(journalToken, new StoreFindToken(otherOffset, sessionId, incarnationId, false)));
        unequalPairs.add(new Pair(inclusiveJournalToken, journalToken));
        for (Pair<StoreFindToken, StoreFindToken> unequalPair : unequalPairs) {
            StoreFindToken first = unequalPair.getFirst();
            StoreFindToken second = unequalPair.getSecond();
            Assert.assertFalse((((("StoreFindTokens [" + first) + "] and [") + second) + "] should not be equal"), unequalPair.getFirst().equals(unequalPair.getSecond()));
        }
    }

    /**
     * Tests {@link StoreFindToken} serialization/deserialization.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void serDeTest() throws IOException {
        UUID sessionId = UUID.randomUUID();
        UUID incarnationId = UUID.randomUUID();
        String logSegmentName = LogSegmentNameHelper.generateFirstSegmentName(isLogSegmented);
        Offset offset = new Offset(logSegmentName, 0);
        MockId key = new MockId(UtilsTest.getRandomString(10));
        if (!(isLogSegmented)) {
            // UnInitialized
            doSerDeTest(new StoreFindToken(), VERSION_0, VERSION_1, VERSION_2);
            // Journal based token
            doSerDeTest(new StoreFindToken(offset, sessionId, incarnationId, false), VERSION_0, VERSION_1, VERSION_2);
            // inclusiveness is present only in VERSION_2
            doSerDeTest(new StoreFindToken(offset, sessionId, incarnationId, true), VERSION_2);
            // Index based
            doSerDeTest(new StoreFindToken(key, offset, sessionId, incarnationId), VERSION_0, VERSION_1, VERSION_2);
        } else {
            // UnInitialized
            doSerDeTest(new StoreFindToken(), VERSION_1, VERSION_2);
            // Journal based token
            doSerDeTest(new StoreFindToken(offset, sessionId, incarnationId, false), VERSION_1, VERSION_2);
            // inclusiveness is present only in VERSION_2
            doSerDeTest(new StoreFindToken(offset, sessionId, incarnationId, true), VERSION_2);
            // Index based
            doSerDeTest(new StoreFindToken(key, offset, sessionId, incarnationId), VERSION_1, VERSION_2);
        }
    }

    /**
     * Tests {@link StoreFindToken} for construction error cases.
     */
    @Test
    public void constructionErrorCasesTest() {
        UUID sessionId = UUID.randomUUID();
        UUID incarnationId = UUID.randomUUID();
        String logSegmentName = LogSegmentNameHelper.generateFirstSegmentName(isLogSegmented);
        Offset offset = new Offset(logSegmentName, 0);
        MockId key = new MockId(UtilsTest.getRandomString(10));
        // no offset
        testConstructionFailure(key, sessionId, incarnationId, null);
        // no session id
        testConstructionFailure(key, null, incarnationId, offset);
        // no incarnation Id
        testConstructionFailure(key, sessionId, null, offset);
        // no key in IndexBased
        try {
            new StoreFindToken(null, offset, sessionId, null);
            Assert.fail("Construction of StoreFindToken should have failed");
        } catch (IllegalArgumentException e) {
            // expected. Nothing to do.
        }
    }
}

