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
package org.ehcache.clustered.common.internal.messages;


import EhcacheEntityResponse.AllInvalidationDone;
import EhcacheEntityResponse.ClientInvalidateAll;
import EhcacheEntityResponse.ClientInvalidateHash;
import EhcacheEntityResponse.HashInvalidationDone;
import EhcacheEntityResponse.IteratorBatch;
import EhcacheEntityResponse.LockFailure;
import EhcacheEntityResponse.LockSuccess;
import EhcacheEntityResponse.MapValue;
import EhcacheEntityResponse.PrepareForDestroy;
import EhcacheEntityResponse.ResolveRequest;
import EhcacheEntityResponse.ServerInvalidateHash;
import EhcacheResponseType.ALL_INVALIDATION_DONE;
import EhcacheResponseType.CLIENT_INVALIDATE_ALL;
import EhcacheResponseType.CLIENT_INVALIDATE_HASH;
import EhcacheResponseType.HASH_INVALIDATION_DONE;
import EhcacheResponseType.ITERATOR_BATCH;
import EhcacheResponseType.LOCK_FAILURE;
import EhcacheResponseType.LOCK_SUCCESS;
import EhcacheResponseType.PREPARE_FOR_DESTROY;
import EhcacheResponseType.RESOLVE_REQUEST;
import EhcacheResponseType.SERVER_INVALIDATE_HASH;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.ehcache.clustered.ChainUtils;
import org.ehcache.clustered.common.internal.exceptions.IllegalMessageException;
import org.ehcache.clustered.common.internal.store.Chain;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import static org.ehcache.clustered.Matchers.hasPayloads;


public class ResponseCodecTest {
    private static final ResponseCodec RESPONSE_CODEC = new ResponseCodec();

    private static final long KEY = 42L;

    private static final int INVALIDATION_ID = 134;

    @Test
    public void testFailureResponseCodec() {
        EhcacheEntityResponse failure = EhcacheEntityResponse.failure(new IllegalMessageException("Test Exception"));
        EhcacheEntityResponse decoded = ResponseCodecTest.RESPONSE_CODEC.decode(ResponseCodecTest.RESPONSE_CODEC.encode(failure));
        Assert.assertThat(getCause().getMessage(), Matchers.is("Test Exception"));
    }

    @Test
    public void testGetResponseCodec() {
        EhcacheEntityResponse getResponse = EhcacheEntityResponse.getResponse(ChainUtils.chainOf(ChainUtils.createPayload(1L), ChainUtils.createPayload(11L), ChainUtils.createPayload(111L)));
        EhcacheEntityResponse decoded = ResponseCodecTest.RESPONSE_CODEC.decode(ResponseCodecTest.RESPONSE_CODEC.encode(getResponse));
        Chain decodedChain = getChain();
        Assert.assertThat(decodedChain, org.ehcache.clustered.Matchers.hasPayloads(1L, 11L, 111L));
    }

    @Test
    public void testMapValueCodec() throws Exception {
        Object subject = new Integer(10);
        EhcacheEntityResponse mapValue = EhcacheEntityResponse.mapValue(subject);
        EhcacheEntityResponse.MapValue decoded = ((EhcacheEntityResponse.MapValue) (ResponseCodecTest.RESPONSE_CODEC.decode(ResponseCodecTest.RESPONSE_CODEC.encode(mapValue))));
        Assert.assertThat(decoded.getValue(), Matchers.equalTo(subject));
    }

    @Test
    public void testSuccess() throws Exception {
        byte[] encoded = ResponseCodecTest.RESPONSE_CODEC.encode(EhcacheEntityResponse.success());
        Assert.assertThat(ResponseCodecTest.RESPONSE_CODEC.decode(encoded), Matchers.<EhcacheEntityResponse>sameInstance(EhcacheEntityResponse.success()));
    }

    @Test
    public void testHashInvalidationDone() throws Exception {
        EhcacheEntityResponse.HashInvalidationDone response = EhcacheEntityResponse.hashInvalidationDone(ResponseCodecTest.KEY);
        byte[] encoded = ResponseCodecTest.RESPONSE_CODEC.encode(response);
        EhcacheEntityResponse.HashInvalidationDone decodedResponse = ((EhcacheEntityResponse.HashInvalidationDone) (ResponseCodecTest.RESPONSE_CODEC.decode(encoded)));
        Assert.assertThat(decodedResponse.getResponseType(), Matchers.is(HASH_INVALIDATION_DONE));
        Assert.assertThat(decodedResponse.getKey(), Matchers.is(ResponseCodecTest.KEY));
    }

    @Test
    public void testAllInvalidationDone() throws Exception {
        EhcacheEntityResponse.AllInvalidationDone response = EhcacheEntityResponse.allInvalidationDone();
        byte[] encoded = ResponseCodecTest.RESPONSE_CODEC.encode(response);
        EhcacheEntityResponse.AllInvalidationDone decodedResponse = ((EhcacheEntityResponse.AllInvalidationDone) (ResponseCodecTest.RESPONSE_CODEC.decode(encoded)));
        Assert.assertThat(decodedResponse.getResponseType(), Matchers.is(ALL_INVALIDATION_DONE));
    }

    @Test
    public void testClientInvalidateHash() throws Exception {
        EhcacheEntityResponse.ClientInvalidateHash response = EhcacheEntityResponse.clientInvalidateHash(ResponseCodecTest.KEY, ResponseCodecTest.INVALIDATION_ID);
        byte[] encoded = ResponseCodecTest.RESPONSE_CODEC.encode(response);
        EhcacheEntityResponse.ClientInvalidateHash decodedResponse = ((EhcacheEntityResponse.ClientInvalidateHash) (ResponseCodecTest.RESPONSE_CODEC.decode(encoded)));
        Assert.assertThat(decodedResponse.getResponseType(), Matchers.is(CLIENT_INVALIDATE_HASH));
        Assert.assertThat(decodedResponse.getKey(), Matchers.is(ResponseCodecTest.KEY));
        Assert.assertThat(decodedResponse.getInvalidationId(), Matchers.is(ResponseCodecTest.INVALIDATION_ID));
    }

    @Test
    public void testClientInvalidateAll() throws Exception {
        EhcacheEntityResponse.ClientInvalidateAll response = EhcacheEntityResponse.clientInvalidateAll(ResponseCodecTest.INVALIDATION_ID);
        byte[] encoded = ResponseCodecTest.RESPONSE_CODEC.encode(response);
        EhcacheEntityResponse.ClientInvalidateAll decodedResponse = ((EhcacheEntityResponse.ClientInvalidateAll) (ResponseCodecTest.RESPONSE_CODEC.decode(encoded)));
        Assert.assertThat(decodedResponse.getResponseType(), Matchers.is(CLIENT_INVALIDATE_ALL));
        Assert.assertThat(decodedResponse.getInvalidationId(), Matchers.is(ResponseCodecTest.INVALIDATION_ID));
    }

    @Test
    public void testServerInvalidateHash() throws Exception {
        EhcacheEntityResponse.ServerInvalidateHash response = EhcacheEntityResponse.serverInvalidateHash(ResponseCodecTest.KEY);
        byte[] encoded = ResponseCodecTest.RESPONSE_CODEC.encode(response);
        EhcacheEntityResponse.ServerInvalidateHash decodedResponse = ((EhcacheEntityResponse.ServerInvalidateHash) (ResponseCodecTest.RESPONSE_CODEC.decode(encoded)));
        Assert.assertThat(decodedResponse.getResponseType(), Matchers.is(SERVER_INVALIDATE_HASH));
        Assert.assertThat(decodedResponse.getKey(), Matchers.is(ResponseCodecTest.KEY));
    }

    @Test
    public void testPrepareForDestroy() throws Exception {
        Set<String> storeIdentifiers = new HashSet<>();
        storeIdentifiers.add("store1");
        storeIdentifiers.add("anotherStore");
        EhcacheEntityResponse.PrepareForDestroy response = EhcacheEntityResponse.prepareForDestroy(storeIdentifiers);
        byte[] encoded = ResponseCodecTest.RESPONSE_CODEC.encode(response);
        EhcacheEntityResponse.PrepareForDestroy decodedResponse = ((EhcacheEntityResponse.PrepareForDestroy) (ResponseCodecTest.RESPONSE_CODEC.decode(encoded)));
        Assert.assertThat(decodedResponse.getResponseType(), Matchers.is(PREPARE_FOR_DESTROY));
        Assert.assertThat(decodedResponse.getStores(), Matchers.is(storeIdentifiers));
    }

    @Test
    public void testResolveRequest() throws Exception {
        long hash = 42L;
        EhcacheEntityResponse.ResolveRequest response = new EhcacheEntityResponse.ResolveRequest(hash, ChainUtils.chainOf(ChainUtils.createPayload(1L), ChainUtils.createPayload(11L), ChainUtils.createPayload(111L)));
        byte[] encoded = ResponseCodecTest.RESPONSE_CODEC.encode(response);
        EhcacheEntityResponse.ResolveRequest decodedResponse = ((EhcacheEntityResponse.ResolveRequest) (ResponseCodecTest.RESPONSE_CODEC.decode(encoded)));
        Assert.assertThat(decodedResponse.getResponseType(), Matchers.is(RESOLVE_REQUEST));
        Assert.assertThat(decodedResponse.getKey(), Matchers.is(42L));
        Assert.assertThat(decodedResponse.getChain(), org.ehcache.clustered.Matchers.hasPayloads(1L, 11L, 111L));
    }

    @Test
    public void testLockResponse() {
        EhcacheEntityResponse.LockSuccess lockSuccess = new EhcacheEntityResponse.LockSuccess(ChainUtils.chainOf(ChainUtils.createPayload(1L), ChainUtils.createPayload(10L)));
        byte[] sucessEncoded = ResponseCodecTest.RESPONSE_CODEC.encode(lockSuccess);
        EhcacheEntityResponse.LockSuccess successDecoded = ((EhcacheEntityResponse.LockSuccess) (ResponseCodecTest.RESPONSE_CODEC.decode(sucessEncoded)));
        Assert.assertThat(successDecoded.getResponseType(), Matchers.is(LOCK_SUCCESS));
        Assert.assertThat(successDecoded.getChain(), hasPayloads(1L, 10L));
        EhcacheEntityResponse.LockFailure lockFailure = EhcacheEntityResponse.lockFailure();
        byte[] failureEncoded = ResponseCodecTest.RESPONSE_CODEC.encode(lockFailure);
        EhcacheEntityResponse.LockFailure failureDecoded = ((EhcacheEntityResponse.LockFailure) (ResponseCodecTest.RESPONSE_CODEC.decode(failureEncoded)));
        Assert.assertThat(failureDecoded.getResponseType(), Matchers.is(LOCK_FAILURE));
    }

    @Test
    public void testIteratorBatchResponse() {
        UUID uuid = UUID.randomUUID();
        List<Chain> chains = Arrays.asList(ChainUtils.chainOf(ChainUtils.createPayload(1L), ChainUtils.createPayload(10L)), ChainUtils.chainOf(ChainUtils.createPayload(2L), ChainUtils.createPayload(20L)));
        EhcacheEntityResponse.IteratorBatch iteratorBatch = new EhcacheEntityResponse.IteratorBatch(uuid, chains, true);
        byte[] encoded = ResponseCodecTest.RESPONSE_CODEC.encode(iteratorBatch);
        EhcacheEntityResponse.IteratorBatch batchDecoded = ((EhcacheEntityResponse.IteratorBatch) (ResponseCodecTest.RESPONSE_CODEC.decode(encoded)));
        Assert.assertThat(batchDecoded.getResponseType(), Matchers.is(ITERATOR_BATCH));
        Assert.assertThat(batchDecoded.getIdentity(), Matchers.is(uuid));
        Assert.assertThat(batchDecoded.getChains().get(0), hasPayloads(1L, 10L));
        Assert.assertThat(batchDecoded.getChains().get(1), hasPayloads(2L, 20L));
        Assert.assertThat(batchDecoded.isLast(), Matchers.is(true));
    }
}

