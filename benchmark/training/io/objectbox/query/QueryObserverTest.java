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
package io.objectbox.query;


import io.objectbox.AbstractObjectBoxTest;
import io.objectbox.Box;
import io.objectbox.TestEntity;
import io.objectbox.TestEntity_;
import io.objectbox.reactive.DataObserver;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.Test;


public class QueryObserverTest extends AbstractObjectBoxTest implements DataObserver<List<TestEntity>> {
    private Box<TestEntity> box;

    private List<List<TestEntity>> receivedChanges = new CopyOnWriteArrayList<>();

    private CountDownLatch latch = new CountDownLatch(1);

    @Test
    public void testObserver() {
        int[] valuesInt = new int[]{ 2003, 2007, 2002 };
        Query<TestEntity> query = box.query().in(TestEntity_.simpleInt, valuesInt).build();
        Assert.assertEquals(0, query.count());
        query.subscribe().observer(this);
        assertLatchCountedDown(latch, 5);
        Assert.assertEquals(1, receivedChanges.size());
        Assert.assertEquals(0, receivedChanges.get(0).size());
        receivedChanges.clear();
        latch = new CountDownLatch(1);
        putTestEntitiesScalars();
        assertLatchCountedDown(latch, 5);
        Assert.assertEquals(1, receivedChanges.size());
        Assert.assertEquals(3, receivedChanges.get(0).size());
    }

    @Test
    public void testSingle() throws InterruptedException {
        putTestEntitiesScalars();
        int[] valuesInt = new int[]{ 2003, 2007, 2002 };
        Query<TestEntity> query = box.query().in(TestEntity_.simpleInt, valuesInt).build();
        query.subscribe().single().observer(this);
        assertLatchCountedDown(latch, 5);
        Assert.assertEquals(1, receivedChanges.size());
        Assert.assertEquals(3, receivedChanges.get(0).size());
        receivedChanges.clear();
        putTestEntities(1);
        Thread.sleep(20);
        Assert.assertEquals(0, receivedChanges.size());
    }

    @Test
    public void testTransformer() throws InterruptedException {
        int[] valuesInt = new int[]{ 2003, 2007, 2002 };
        Query<TestEntity> query = box.query().in(TestEntity_.simpleInt, valuesInt).build();
        Assert.assertEquals(0, query.count());
        final List<Integer> receivedSums = new ArrayList<>();
        query.subscribe().transform(new io.objectbox.reactive.DataTransformer<List<TestEntity>, Integer>() {
            @Override
            @SuppressWarnings("NullableProblems")
            public Integer transform(List<TestEntity> source) throws Exception {
                int sum = 0;
                for (TestEntity entity : source) {
                    sum += entity.getSimpleInt();
                }
                return sum;
            }
        }).observer(new DataObserver<Integer>() {
            @Override
            public void onData(Integer data) {
                receivedSums.add(data);
                latch.countDown();
            }
        });
        assertLatchCountedDown(latch, 5);
        latch = new CountDownLatch(1);
        putTestEntitiesScalars();
        assertLatchCountedDown(latch, 5);
        Thread.sleep(20);
        Assert.assertEquals(2, receivedSums.size());
        Assert.assertEquals(0, ((int) (receivedSums.get(0))));
        Assert.assertEquals(((2003 + 2007) + 2002), ((int) (receivedSums.get(1))));
    }
}

