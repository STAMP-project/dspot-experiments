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


import Scannable.Attr.ACTUAL;
import Scannable.Attr.BUFFERED;
import Scannable.Attr.CANCELLED;
import Scannable.Attr.DELAY_ERROR;
import Scannable.Attr.PARENT;
import Scannable.Attr.PREFETCH;
import Scannable.Attr.REQUESTED_FROM_DOWNSTREAM;
import Scannable.Attr.TERMINATED;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.test.StepVerifier;


public class FluxOnBackpressureBufferStrategyTest implements BiFunction<Throwable, Object, Throwable> , Consumer<String> {
    private String droppedValue;

    private Object hookCapturedValue;

    private Throwable hookCapturedError;

    @Test
    public void drop() {
        DirectProcessor<String> processor = DirectProcessor.create();
        FluxOnBackpressureBufferStrategy<String> flux = new FluxOnBackpressureBufferStrategy(processor, 2, this, DROP_LATEST);
        StepVerifier.create(flux, 0).thenRequest(1).then(() -> {
            processor.onNext("normal");
            processor.onNext("over1");
            processor.onNext("over2");
            processor.onNext("over3");
            processor.onComplete();
        }).expectNext("normal").thenAwait().thenRequest(3).expectNext("over1", "over2").expectComplete().verify();
        Assert.assertEquals("over3", droppedValue);
        Assert.assertNull("unexpected hookCapturedValue", hookCapturedValue);
        Assert.assertNull("unexpected hookCapturedError", hookCapturedError);
    }

    @Test
    public void dropOldest() {
        DirectProcessor<String> processor = DirectProcessor.create();
        FluxOnBackpressureBufferStrategy<String> flux = new FluxOnBackpressureBufferStrategy(processor, 2, this, DROP_OLDEST);
        StepVerifier.create(flux, 0).thenRequest(1).then(() -> {
            processor.onNext("normal");
            processor.onNext("over1");
            processor.onNext("over2");
            processor.onNext("over3");
            processor.onComplete();
        }).expectNext("normal").thenAwait().thenRequest(3).expectNext("over2", "over3").expectComplete().verify();
        Assert.assertEquals("over1", droppedValue);
        Assert.assertNull("unexpected hookCapturedValue", hookCapturedValue);
        Assert.assertNull("unexpected hookCapturedError", hookCapturedError);
    }

    @Test
    public void error() {
        DirectProcessor<String> processor = DirectProcessor.create();
        FluxOnBackpressureBufferStrategy<String> flux = new FluxOnBackpressureBufferStrategy(processor, 2, this, ERROR);
        StepVerifier.create(flux, 0).thenRequest(1).then(() -> {
            processor.onNext("normal");
            processor.onNext("over1");
            processor.onNext("over2");
            processor.onNext("over3");
            processor.onComplete();
        }).expectNext("normal").thenAwait().thenRequest(3).expectNext("over1", "over2").expectErrorMessage("The receiver is overrun by more signals than expected (bounded queue...)").verify();
        Assert.assertEquals("over3", droppedValue);
        Assert.assertEquals("over3", hookCapturedValue);
        Assert.assertTrue(("unexpected hookCapturedError: " + (hookCapturedError)), ((hookCapturedError) instanceof IllegalStateException));
    }

    @Test
    public void dropCallbackError() {
        DirectProcessor<String> processor = DirectProcessor.create();
        FluxOnBackpressureBufferStrategy<String> flux = new FluxOnBackpressureBufferStrategy(processor, 2, ( v) -> {
            throw new IllegalArgumentException("boom");
        }, DROP_LATEST);
        StepVerifier.create(flux, 0).thenRequest(1).then(() -> {
            processor.onNext("normal");
            processor.onNext("over1");
            processor.onNext("over2");
            processor.onNext("over3");
            processor.onComplete();
        }).expectNext("normal").thenAwait().thenRequest(3).expectNext("over1", "over2").expectErrorMessage("boom").verify();
        Assert.assertNull("unexpected droppedValue", droppedValue);
        Assert.assertEquals("over3", hookCapturedValue);
        Assert.assertTrue(("unexpected hookCapturedError: " + (hookCapturedError)), ((hookCapturedError) instanceof IllegalArgumentException));
        Assert.assertEquals("boom", hookCapturedError.getMessage());
    }

    @Test
    public void dropOldestCallbackError() {
        DirectProcessor<String> processor = DirectProcessor.create();
        FluxOnBackpressureBufferStrategy<String> flux = new FluxOnBackpressureBufferStrategy(processor, 2, ( v) -> {
            throw new IllegalArgumentException("boom");
        }, DROP_OLDEST);
        StepVerifier.create(flux, 0).thenRequest(1).then(() -> {
            processor.onNext("normal");
            processor.onNext("over1");
            processor.onNext("over2");
            processor.onNext("over3");
            processor.onComplete();
        }).expectNext("normal").thenAwait().thenRequest(3).expectNext("over2", "over3").expectErrorMessage("boom").verify();
        Assert.assertNull("unexpected droppedValue", droppedValue);
        Assert.assertEquals("over1", hookCapturedValue);
        Assert.assertTrue(("unexpected hookCapturedError: " + (hookCapturedError)), ((hookCapturedError) instanceof IllegalArgumentException));
        Assert.assertEquals("boom", hookCapturedError.getMessage());
    }

    @Test
    public void errorCallbackError() {
        DirectProcessor<String> processor = DirectProcessor.create();
        FluxOnBackpressureBufferStrategy<String> flux = new FluxOnBackpressureBufferStrategy(processor, 2, ( v) -> {
            throw new IllegalArgumentException("boom");
        }, ERROR);
        StepVerifier.create(flux, 0).thenRequest(1).then(() -> {
            processor.onNext("normal");
            processor.onNext("over1");
            processor.onNext("over2");
            processor.onNext("over3");
            processor.onComplete();
        }).expectNext("normal").thenAwait().thenRequest(3).expectNext("over1", "over2").expectErrorMessage("boom").verify();
        Assert.assertNull("unexpected droppedValue", droppedValue);
        Assert.assertEquals("over3", hookCapturedValue);
        Assert.assertTrue(("unexpected hookCapturedError: " + (hookCapturedError)), ((hookCapturedError) instanceof IllegalArgumentException));
        Assert.assertEquals("boom", hookCapturedError.getMessage());
    }

    @Test
    public void noCallbackWithErrorStrategyOnErrorImmediately() {
        DirectProcessor<String> processor = DirectProcessor.create();
        FluxOnBackpressureBufferStrategy<String> flux = new FluxOnBackpressureBufferStrategy(processor, 2, null, ERROR);
        StepVerifier.create(flux, 0).thenRequest(1).then(() -> {
            processor.onNext("normal");
            processor.onNext("over1");
            processor.onNext("over2");
            processor.onNext("over3");
            processor.onComplete();
        }).expectNext("normal").thenAwait().thenRequest(1).expectErrorMessage("The receiver is overrun by more signals than expected (bounded queue...)").verify();
        Assert.assertNull("unexpected droppedValue", droppedValue);
        Assert.assertEquals("over3", hookCapturedValue);
        Assert.assertTrue(("unexpected hookCapturedError: " + (hookCapturedError)), ((hookCapturedError) instanceof IllegalStateException));
        Assert.assertEquals("The receiver is overrun by more signals than expected (bounded queue...)", hookCapturedError.getMessage());
    }

    @Test
    public void noCallbackWithDropStrategyNoError() {
        DirectProcessor<String> processor = DirectProcessor.create();
        FluxOnBackpressureBufferStrategy<String> flux = new FluxOnBackpressureBufferStrategy(processor, 2, null, DROP_LATEST);
        StepVerifier.create(flux, 0).thenRequest(1).then(() -> {
            processor.onNext("normal");
            processor.onNext("over1");
            processor.onNext("over2");
            processor.onNext("over3");
            processor.onComplete();
        }).expectNext("normal").thenAwait().thenRequest(3).expectNext("over1", "over2").expectComplete().verify();
        Assert.assertNull("unexpected droppedValue", droppedValue);
        Assert.assertNull("unexpected hookCapturedValue", hookCapturedValue);
        Assert.assertNull("unexpected hookCapturedError", hookCapturedError);
    }

    @Test
    public void noCallbackWithDropOldestStrategyNoError() {
        DirectProcessor<String> processor = DirectProcessor.create();
        FluxOnBackpressureBufferStrategy<String> flux = new FluxOnBackpressureBufferStrategy(processor, 2, null, DROP_OLDEST);
        StepVerifier.create(flux, 0).thenRequest(1).then(() -> {
            processor.onNext("normal");
            processor.onNext("over1");
            processor.onNext("over2");
            processor.onNext("over3");
            processor.onComplete();
        }).expectNext("normal").thenAwait().thenRequest(3).expectNext("over2", "over3").expectComplete().verify();
        Assert.assertNull("unexpected droppedValue", droppedValue);
        Assert.assertNull("unexpected hookCapturedValue", hookCapturedValue);
        Assert.assertNull("unexpected hookCapturedError", hookCapturedError);
    }

    @Test
    public void fluxOnBackpressureBufferStrategyNoCallback() {
        DirectProcessor<String> processor = DirectProcessor.create();
        StepVerifier.create(processor.onBackpressureBuffer(2, DROP_OLDEST), 0).thenRequest(1).then(() -> {
            processor.onNext("normal");
            processor.onNext("over1");
            processor.onNext("over2");
            processor.onNext("over3");
            processor.onComplete();
        }).expectNext("normal").thenAwait().thenRequest(3).expectNext("over2", "over3").expectComplete().verify();
        Assert.assertNull("unexpected droppedValue", droppedValue);
        Assert.assertNull("unexpected hookCapturedValue", hookCapturedValue);
        Assert.assertNull("unexpected hookCapturedError", hookCapturedError);
    }

    @Test
    public void fluxOnBackpressureBufferStrategyRequiresCallback() {
        try {
            Flux.just("foo").onBackpressureBuffer(1, null, ERROR);
            Assert.fail("expected NullPointerException");
        } catch (NullPointerException e) {
            Assert.assertEquals("onBufferOverflow", e.getMessage());
        }
    }

    @Test
    public void fluxOnBackpressureBufferStrategyRequiresStrategy() {
        try {
            Flux.just("foo").onBackpressureBuffer(1, ( v) -> {
            }, null);
            Assert.fail("expected NullPointerException");
        } catch (NullPointerException e) {
            Assert.assertEquals("bufferOverflowStrategy", e.getMessage());
        }
    }

    @Test
    public void scanSubscriber() {
        CoreSubscriber<Integer> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        FluxOnBackpressureBufferStrategy.BackpressureBufferDropOldestSubscriber<Integer> test = new FluxOnBackpressureBufferStrategy.BackpressureBufferDropOldestSubscriber<>(actual, 123, true, ( t) -> {
        }, BufferOverflowStrategy.DROP_OLDEST);
        Subscription parent = Operators.emptySubscription();
        test.onSubscribe(parent);
        Assert.assertThat(test.scan(ACTUAL)).isSameAs(actual);
        Assert.assertThat(test.scan(PARENT)).isSameAs(parent);
        Assert.assertThat(test.scan(DELAY_ERROR)).isTrue();
        test.requested = 35;
        Assert.assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isEqualTo(35);
        Assert.assertThat(test.scan(PREFETCH)).isEqualTo(Integer.MAX_VALUE);
        test.offer(9);
        Assert.assertThat(test.scan(BUFFERED)).isEqualTo(1);
        Assert.assertThat(test.scan(Scannable.Attr.ERROR)).isNull();
        Assert.assertThat(test.scan(TERMINATED)).isFalse();
        test.onError(new IllegalStateException("boom"));
        Assert.assertThat(test.scan(Scannable.Attr.ERROR)).isSameAs(test.error);
        Assert.assertThat(test.scan(TERMINATED)).isTrue();
        Assert.assertThat(test.scan(CANCELLED)).isFalse();
        test.cancel();
        Assert.assertThat(test.scan(CANCELLED)).isTrue();
    }
}

