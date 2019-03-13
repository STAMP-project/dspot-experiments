/**
 * Copyright (c) 2011-2017 Pivotal Software Inc, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package reactor.core.publisher.scenarios;


import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import org.junit.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ReplayProcessor;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.subscriber.AssertSubscriber;
import reactor.util.Logger;
import reactor.util.Loggers;
import reactor.util.annotation.Nullable;


/**
 *
 *
 * @author Stephane Maldini
 */
public class CombinationTests {
    private static final Logger LOG = Loggers.getLogger(CombinationTests.class);

    private FluxProcessor<CombinationTests.SensorData, CombinationTests.SensorData> sensorEven;

    private FluxProcessor<CombinationTests.SensorData, CombinationTests.SensorData> sensorOdd;

    @Test
    public void sampleMergeTest() throws Exception {
        int elements = 40;
        CountDownLatch latch = new CountDownLatch((elements + 1));
        Publisher<CombinationTests.SensorData> p = Flux.merge(sensorOdd(), sensorEven()).log("merge");
        generateData(elements);
        awaitLatch(p, latch);
    }

    @Test
    public void sampleAmbTest() throws Exception {
        int elements = 40;
        CountDownLatch latch = new CountDownLatch(((elements / 2) + 1));
        Flux<CombinationTests.SensorData> p = Flux.first(sensorOdd(), sensorEven()).log("first");
        p.subscribe(( d) -> latch.countDown(), null, latch::countDown);
        Thread.sleep(1000);
        generateData(elements);
    }

    /* @Test
    public void sampleConcatTestConsistent() throws Exception {
    for(int i = 0; i < 1000; i++){
    System.out.println("------");
    sampleConcatTest();
    }
    }
     */
    @Test
    public void sampleConcatTest() throws Exception {
        int elements = 40;
        CountDownLatch latch = new CountDownLatch((elements + 1));
        Publisher<CombinationTests.SensorData> p = Flux.concat(sensorEven(), sensorOdd()).log("concat");
        generateData(elements);
        awaitLatch(p, latch);
    }

    @Test
    public void sampleZipTest() throws Exception {
        int elements = 69;
        CountDownLatch latch = new CountDownLatch(((elements / 2) + 1));
        Publisher<CombinationTests.SensorData> p = Flux.zip(sensorEven(), sensorOdd(), this::computeMin).log("zip");
        generateData(elements);
        awaitLatch(p, latch);
    }

    @Test
    public void sampleMergeMonoTest() throws Exception {
        CountDownLatch latch = new CountDownLatch(2);
        Flux<Integer> p = Flux.merge(Flux.<Integer>empty().next(), Mono.just(1)).log("mono");
        awaitLatch(p, latch);
    }

    @Test
    public void sampleZipTest2() throws Exception {
        int elements = 1;
        CountDownLatch latch = new CountDownLatch((elements + 1));
        Publisher<CombinationTests.SensorData> p = Flux.zip(sensorEven(), Flux.just(new CombinationTests.SensorData(1L, 14.0F)), this::computeMin).log("zip2");
        generateData(elements);
        awaitLatch(p, latch);
    }

    @Test
    public void sampleZipTest3() throws Exception {
        int elements = 1;
        CountDownLatch latch = new CountDownLatch((elements + 1));
        EmitterProcessor<CombinationTests.SensorData> sensorDataProcessor = EmitterProcessor.create();
        Scheduler scheduler = Schedulers.single();
        sensorDataProcessor.publishOn(scheduler).subscribe(( d) -> latch.countDown(), null, latch::countDown);
        Flux.zip(Flux.just(new CombinationTests.SensorData(2L, 12.0F)), Flux.just(new CombinationTests.SensorData(1L, 14.0F)), this::computeMin).log("zip3").subscribe(sensorDataProcessor);
        awaitLatch(null, latch);
        scheduler.dispose();
    }

    public class SensorData implements Comparable<CombinationTests.SensorData> {
        private final Long id;

        private final Float value;

        public SensorData(Long id, Float value) {
            this.id = id;
            this.value = value;
        }

        public Long getId() {
            return id;
        }

        public Float getValue() {
            return value;
        }

        @Override
        public int compareTo(@Nullable
        CombinationTests.SensorData other) {
            if (null == other) {
                return 1;
            }
            return value.compareTo(other.getValue());
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CombinationTests.SensorData)) {
                return false;
            }
            CombinationTests.SensorData other = ((CombinationTests.SensorData) (obj));
            return ((Long.compare(other.getId(), id)) == 0) && ((Float.compare(other.getValue(), value)) == 0);
        }

        @Override
        public int hashCode() {
            return (id) != null ? id.hashCode() : 0;
        }

        @Override
        public String toString() {
            return (((("SensorData{" + "id=") + (id)) + ", value=") + (value)) + '}';
        }
    }

    AssertSubscriber<Long> ts;

    ReplayProcessor<Long> emitter1;

    ReplayProcessor<Long> emitter2;

    @Test
    public void mergeWithInterleave() {
        Flux.merge(emitter1, emitter2).subscribe(ts);
        emitValues();
        ts.assertValues(1L, 2L, 3L, 4L).assertComplete();
    }

    @Test
    public void mergeWithNoInterleave() throws Exception {
        Flux.concat(emitter1.log("test1"), emitter2.log("test2")).log().subscribe(ts);
        emitValues();
        ts.assertValues(1L, 3L, 2L, 4L).assertComplete();
    }

    @Test
    public void sampleCombineLatestTest() throws Exception {
        int elements = 40;
        CountDownLatch latch = new CountDownLatch(((elements / 2) - 2));
        Flux.combineLatest(sensorOdd().cache().delayElements(Duration.ofMillis(100)), sensorEven().cache().delayElements(Duration.ofMillis(200)), this::computeMin).log("combineLatest").subscribe(( i) -> latch.countDown(), null, latch::countDown);
        generateData(elements);
        awaitLatch(null, latch);
    }
}

