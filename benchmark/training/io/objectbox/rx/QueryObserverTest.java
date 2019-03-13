/**
 * Copyright 2017 ObjectBox Ltd. All rights reserved.
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
package io.objectbox.rx;


import io.objectbox.query.FakeQueryPublisher;
import io.objectbox.query.MockQuery;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.functions.Consumer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class QueryObserverTest implements Observer<List<String>> , SingleObserver<List<String>> , Consumer<String> {
    private List<List<String>> receivedChanges = new CopyOnWriteArrayList<>();

    private CountDownLatch latch = new CountDownLatch(1);

    private MockQuery<String> mockQuery = new MockQuery<>(false);

    private FakeQueryPublisher<String> publisher = mockQuery.getFakeQueryPublisher();

    private List<String> listResult = new ArrayList<>();

    private Throwable error;

    private AtomicInteger completedCount = new AtomicInteger();

    @Test
    public void testObservable() {
        Observable observable = RxQuery.observable(mockQuery.getQuery());
        observable.subscribe(((Observer) (this)));
        assertLatchCountedDown(latch, 2);
        Assert.assertEquals(1, receivedChanges.size());
        Assert.assertEquals(0, receivedChanges.get(0).size());
        Assert.assertNull(error);
        latch = new CountDownLatch(1);
        receivedChanges.clear();
        publisher.setQueryResult(listResult);
        publisher.publish();
        assertLatchCountedDown(latch, 5);
        Assert.assertEquals(1, receivedChanges.size());
        Assert.assertEquals(2, receivedChanges.get(0).size());
        Assert.assertEquals(0, completedCount.get());
        // Unsubscribe?
        // receivedChanges.clear();
        // latch = new CountDownLatch(1);
        // assertLatchCountedDown(latch, 5);
        // 
        // assertEquals(1, receivedChanges.size());
        // assertEquals(3, receivedChanges.get(0).size());
    }

    @Test
    public void testFlowableOneByOne() {
        publisher.setQueryResult(listResult);
        latch = new CountDownLatch(2);
        Flowable flowable = RxQuery.flowableOneByOne(mockQuery.getQuery());
        flowable.subscribe(this);
        assertLatchCountedDown(latch, 2);
        Assert.assertEquals(2, receivedChanges.size());
        Assert.assertEquals(1, receivedChanges.get(0).size());
        Assert.assertEquals(1, receivedChanges.get(1).size());
        Assert.assertNull(error);
        receivedChanges.clear();
        publisher.publish();
        assertNoMoreResults();
    }

    @Test
    public void testSingle() {
        publisher.setQueryResult(listResult);
        Single single = RxQuery.single(mockQuery.getQuery());
        single.subscribe(((SingleObserver) (this)));
        assertLatchCountedDown(latch, 2);
        Assert.assertEquals(1, receivedChanges.size());
        Assert.assertEquals(2, receivedChanges.get(0).size());
        receivedChanges.clear();
        publisher.publish();
        assertNoMoreResults();
    }
}

