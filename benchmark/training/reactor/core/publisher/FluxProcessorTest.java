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
package reactor.core.publisher;


import Scannable.Attr.CAPACITY;
import Scannable.Attr.ERROR;
import Scannable.Attr.TERMINATED;
import Scheduler.Worker;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Subscriber;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;


public class FluxProcessorTest {
    @Test(expected = NullPointerException.class)
    @SuppressWarnings("unchecked")
    public void failNullSubscriber() {
        FluxProcessor.wrap(UnicastProcessor.create(), UnicastProcessor.create()).subscribe(((Subscriber) (null)));
    }

    @Test(expected = NullPointerException.class)
    public void failNullUpstream() {
        FluxProcessor.wrap(null, UnicastProcessor.create());
    }

    @Test(expected = NullPointerException.class)
    public void failNullDownstream() {
        FluxProcessor.wrap(UnicastProcessor.create(), null);
    }

    @Test
    public void testCapacity() {
        assertThat(FluxProcessor.wrap(UnicastProcessor.create(), UnicastProcessor.create()).getBufferSize()).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void normalBlackboxProcessor() {
        UnicastProcessor<Integer> upstream = UnicastProcessor.create();
        FluxProcessor<Integer, Integer> processor = FluxProcessor.wrap(upstream, upstream.map(( i) -> i + 1).filter(( i) -> (i % 2) == 0));
        DelegateProcessor<Integer, Integer> delegateProcessor = ((DelegateProcessor<Integer, Integer>) (processor));
        delegateProcessor.parents().findFirst().ifPresent(( s) -> assertThat(s).isInstanceOf(.class));
        StepVerifier.create(processor).then(() -> Flux.just(1, 2, 3).subscribe(processor)).expectNext(2, 4).verifyComplete();
    }

    @Test
    public void disconnectedBlackboxProcessor() {
        UnicastProcessor<Integer> upstream = UnicastProcessor.create();
        FluxProcessor<Integer, Integer> processor = FluxProcessor.wrap(upstream, Flux.just(1));
        StepVerifier.create(processor).expectNext(1).verifyComplete();
    }

    @Test
    public void symmetricBlackboxProcessor() {
        UnicastProcessor<Integer> upstream = UnicastProcessor.create();
        FluxProcessor<Integer, Integer> processor = FluxProcessor.wrap(upstream, upstream);
        StepVerifier.create(processor).then(() -> Flux.just(1).subscribe(processor)).expectNext(1).verifyComplete();
    }

    @Test
    public void errorSymmetricBlackboxProcessor() {
        UnicastProcessor<Integer> upstream = UnicastProcessor.create();
        FluxProcessor<Integer, Integer> processor = FluxProcessor.wrap(upstream, upstream);
        StepVerifier.create(processor).then(() -> Flux.<Integer>error(new Exception("test")).subscribe(processor)).verifyErrorMessage("test");
    }

    @Test
    public void testSubmitSession() throws Exception {
        FluxProcessor<Integer, Integer> processor = EmitterProcessor.create();
        AtomicInteger count = new AtomicInteger();
        CountDownLatch latch = new CountDownLatch(1);
        Scheduler scheduler = Schedulers.parallel();
        processor.publishOn(scheduler).delaySubscription(Duration.ofMillis(1000)).limitRate(1).subscribe(( d) -> {
            count.incrementAndGet();
            latch.countDown();
        });
        FluxSink<Integer> session = processor.sink();
        session.next(1);
        // System.out.println(emission);
        session.complete();
        latch.await(5, TimeUnit.SECONDS);
        Assert.assertTrue(("latch : " + count), ((count.get()) == 1));
        scheduler.dispose();
    }

    @Test
    public void testEmitter() throws Throwable {
        FluxProcessor<Integer, Integer> processor = EmitterProcessor.create();
        int n = 100000;
        int subs = 4;
        final CountDownLatch latch = new CountDownLatch(((n + 1) * subs));
        Scheduler c = Schedulers.single();
        for (int i = 0; i < subs; i++) {
            processor.publishOn(c).limitRate(1).subscribe(( d) -> latch.countDown(), null, latch::countDown);
        }
        FluxSink<Integer> session = processor.sink();
        for (int i = 0; i < n; i++) {
            while ((session.requestedFromDownstream()) == 0) {
            } 
            session.next(i);
        }
        session.complete();
        boolean waited = latch.await(5, TimeUnit.SECONDS);
        Assert.assertTrue(("latch : " + (latch.getCount())), waited);
        c.dispose();
    }

    @Test
    public void testEmitter2() throws Throwable {
        FluxProcessor<Integer, Integer> processor = EmitterProcessor.create();
        int n = 100000;
        int subs = 4;
        final CountDownLatch latch = new CountDownLatch(((n + 1) * subs));
        Scheduler c = Schedulers.single();
        for (int i = 0; i < subs; i++) {
            processor.publishOn(c).doOnComplete(latch::countDown).doOnNext(( d) -> latch.countDown()).subscribe();
        }
        FluxSink<Integer> session = processor.sink();
        for (int i = 0; i < n; i++) {
            while ((session.requestedFromDownstream()) == 0) {
            } 
            session.next(i);
        }
        session.complete();
        boolean waited = latch.await(5, TimeUnit.SECONDS);
        Assert.assertTrue(("latch : " + (latch.getCount())), waited);
        c.dispose();
    }

    @Test
    public void serializedConcurrent() {
        Scheduler.Worker w1 = Schedulers.elastic().createWorker();
        Scheduler.Worker w2 = Schedulers.elastic().createWorker();
        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch latch2 = new CountDownLatch(1);
        AtomicReference<Thread> ref = new AtomicReference<>();
        ref.set(Thread.currentThread());
        DirectProcessor<String> rp = DirectProcessor.create();
        FluxProcessor<String, String> serialized = rp.serialize();
        try {
            StepVerifier.create(serialized).then(() -> {
                w1.schedule(() -> serialized.onNext("test1"));
                try {
                    latch2.await();
                } catch ( e) {
                    Assert.fail();
                }
                w2.schedule(() -> {
                    serialized.onNext("test2");
                    serialized.onNext("test3");
                    serialized.onComplete();
                    latch.countDown();
                });
            }).assertNext(( s) -> {
                AssertionsForClassTypes.assertThat(s).isEqualTo("test1");
                AssertionsForClassTypes.assertThat(ref.get()).isNotEqualTo(Thread.currentThread());
                ref.set(Thread.currentThread());
                latch2.countDown();
                try {
                    latch.await();
                } catch ( e) {
                    Assert.fail();
                }
            }).assertNext(( s) -> {
                AssertionsForClassTypes.assertThat(ref.get()).isEqualTo(Thread.currentThread());
                AssertionsForClassTypes.assertThat(s).isEqualTo("test2");
            }).assertNext(( s) -> {
                AssertionsForClassTypes.assertThat(ref.get()).isEqualTo(Thread.currentThread());
                AssertionsForClassTypes.assertThat(s).isEqualTo("test3");
            }).verifyComplete();
        } finally {
            w1.dispose();
            w2.dispose();
        }
    }

    @Test
    public void scanProcessor() {
        FluxProcessor<String, String> test = DirectProcessor.<String>create().serialize();
        assertThat(test.scan(CAPACITY)).isEqualTo(16);
        assertThat(test.scan(TERMINATED)).isFalse();
        assertThat(test.scan(ERROR)).isNull();
        test.onError(new IllegalStateException("boom"));
        assertThat(test.scan(ERROR)).hasMessage("boom");
        assertThat(test.scan(TERMINATED)).isTrue();
    }
}

