/**
 * Copyright 2015 Victor Albertos
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
package io.rx_cache2.internal;


import Source.CLOUD;
import io.reactivex.observers.TestObserver;
import io.rx_cache2.Reply;
import io.rx_cache2.internal.cache.EvictExpiredRecordsPersistence;
import io.rx_cache2.internal.cache.GetDeepCopy;
import io.rx_cache2.internal.cache.HasRecordExpired;
import io.rx_cache2.internal.cache.TwoLayersCache;
import io.rx_cache2.internal.common.BaseTest;
import io.rx_cache2.internal.migration.DoMigrations;
import junit.framework.TestCase;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by victor on 28/12/15.
 */
public class ProcessorProvidersTest extends BaseTest {
    private ProcessorProvidersBehaviour processorProvidersUT;

    private TwoLayersCache twoLayersCacheMock;

    private HasRecordExpired hasRecordExpired;

    private EvictExpiredRecordsPersistence evictExpiredRecordsPersistence;

    private GetDeepCopy getDeepCopy;

    private DoMigrations doMigrations;

    @Test
    public void When_First_Retrieve_Then_Source_Retrieved_Is_Cloud() {
        TestObserver observerMock = getSubscriberCompleted(false, false, true, ProcessorProvidersTest.Loader.VALID, false);
        Reply<Mock> reply = ((Reply) (observerMock.values().get(0)));
        Assert.assertThat(reply.getSource(), Is.is(CLOUD));
        TestCase.assertNotNull(reply.getData());
    }

    @Test
    public void When_Evict_Cache_Then_Source_Retrieved_Is_Cloud() {
        TestObserver observerMock = getSubscriberCompleted(true, true, true, ProcessorProvidersTest.Loader.VALID, false);
        Reply<Mock> reply = ((Reply) (observerMock.values().get(0)));
        Assert.assertThat(reply.getSource(), Is.is(CLOUD));
        TestCase.assertNotNull(reply.getData());
    }

    @Test
    public void When_No_Evict_Cache_Then_Source_Retrieved_Is_Not_Cloud() {
        TestObserver observerMock = getSubscriberCompleted(true, false, true, ProcessorProvidersTest.Loader.VALID, false);
        Reply<Mock> reply = ((Reply) (observerMock.values().get(0)));
        Assert.assertThat(reply.getSource(), Is.is(IsNot.not(CLOUD)));
        TestCase.assertNotNull(reply.getData());
    }

    @Test
    public void When_No_Reply_Then_Get_Mock() {
        TestObserver observerMock = getSubscriberCompleted(true, false, false, ProcessorProvidersTest.Loader.VALID, false);
        Mock mock = ((Mock) (observerMock.values().get(0)));
        TestCase.assertNotNull(mock);
    }

    @Test
    public void When_No_Loader_And_Not_Cache_Then_Get_Throw_Exception() {
        TestObserver observerMock = getSubscriberCompleted(false, false, false, ProcessorProvidersTest.Loader.NULL, false);
        Assert.assertThat(observerMock.errorCount(), Is.is(1));
        Assert.assertThat(observerMock.valueCount(), Is.is(0));
    }

    @Test
    public void When_No_Loader_And_Cache_Expired_Then_Get_Throw_Exception() {
        TestObserver observerMock = getSubscriberCompleted(true, true, false, ProcessorProvidersTest.Loader.NULL, false);
        Assert.assertThat(observerMock.errorCount(), Is.is(1));
        Assert.assertThat(observerMock.valueCount(), Is.is(0));
    }

    @Test
    public void When_No_Loader_And_Cache_Expired_But_Use_Expired_Data_If_Loader_Not_Available_Then_Get_Mock() {
        processorProvidersUT = new io.rx_cache2.internal.ProcessorProvidersBehaviour(twoLayersCacheMock, false, evictExpiredRecordsPersistence, getDeepCopy, doMigrations);
        TestObserver observerMock = getSubscriberCompleted(true, true, false, ProcessorProvidersTest.Loader.NULL, true);
        Assert.assertThat(observerMock.errorCount(), Is.is(0));
        Assert.assertThat(observerMock.valueCount(), Is.is(1));
    }

    @Test
    public void When_Loader_Throws_Exception_And_Cache_Expired_Then_Get_Throw_Exception() {
        TestObserver observerMock = getSubscriberCompleted(true, true, false, ProcessorProvidersTest.Loader.EXCEPTION, false);
        Assert.assertThat(observerMock.errorCount(), Is.is(1));
        Assert.assertThat(observerMock.valueCount(), Is.is(0));
    }

    @Test
    public void When_Loader_Throws_Exception_And_Cache_Expired_But_Use_Expired_Data_If_Loader_Not_Available_Then_Get_Mock() {
        processorProvidersUT = new io.rx_cache2.internal.ProcessorProvidersBehaviour(twoLayersCacheMock, false, evictExpiredRecordsPersistence, getDeepCopy, doMigrations);
        TestObserver observerMock = getSubscriberCompleted(true, true, false, ProcessorProvidersTest.Loader.EXCEPTION, true);
        Assert.assertThat(observerMock.errorCount(), Is.is(0));
        Assert.assertThat(observerMock.valueCount(), Is.is(1));
    }

    enum Loader {

        VALID,
        NULL,
        EXCEPTION;}
}

