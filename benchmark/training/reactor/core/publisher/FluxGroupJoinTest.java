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


import FluxGroupJoin.GroupJoinSubscription;
import FluxGroupJoin.LeftRightEndSubscriber;
import FluxGroupJoin.LeftRightSubscriber;
import Scannable.Attr.ACTUAL;
import Scannable.Attr.BUFFERED;
import Scannable.Attr.CANCELLED;
import Scannable.Attr.ERROR;
import Scannable.Attr.PARENT;
import Scannable.Attr.REQUESTED_FROM_DOWNSTREAM;
import Scannable.Attr.TERMINATED;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.junit.Test;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.test.subscriber.AssertSubscriber;
import reactor.util.concurrent.Queues;


public class FluxGroupJoinTest {
    final BiFunction<Integer, Flux<Integer>, Flux<Integer>> add2 = ( t1, t2s) -> t2s.map(( t2) -> t1 + t2);

    @Test
    public void behaveAsJoin() {
        AssertSubscriber<Object> ts = AssertSubscriber.create();
        DirectProcessor<Integer> source1 = DirectProcessor.create();
        DirectProcessor<Integer> source2 = DirectProcessor.create();
        Flux<Integer> m = source1.groupJoin(source2, just(Flux.never()), just(Flux.never()), add2).flatMap(( t) -> t);
        m.subscribe(ts);
        source1.onNext(1);
        source1.onNext(2);
        source1.onNext(4);
        source2.onNext(16);
        source2.onNext(32);
        source2.onNext(64);
        source1.onComplete();
        source2.onComplete();
        ts.assertValues(17, 18, 20, 33, 34, 36, 65, 66, 68).assertComplete().assertNoError();
    }

    class Person {
        final int id;

        final String name;

        public Person(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    class PersonFruit {
        final int personId;

        final String fruit;

        public PersonFruit(int personId, String fruit) {
            this.personId = personId;
            this.fruit = fruit;
        }
    }

    class PPF {
        final FluxGroupJoinTest.Person person;

        final Flux<FluxGroupJoinTest.PersonFruit> fruits;

        public PPF(FluxGroupJoinTest.Person person, Flux<FluxGroupJoinTest.PersonFruit> fruits) {
            this.person = person;
            this.fruits = fruits;
        }
    }

    @Test
    public void normal1() {
        AssertSubscriber<Object> ts = AssertSubscriber.create();
        Flux<FluxGroupJoinTest.Person> source1 = Flux.fromIterable(Arrays.asList(new FluxGroupJoinTest.Person(1, "Joe"), new FluxGroupJoinTest.Person(2, "Mike"), new FluxGroupJoinTest.Person(3, "Charlie")));
        Flux<FluxGroupJoinTest.PersonFruit> source2 = Flux.fromIterable(Arrays.asList(new FluxGroupJoinTest.PersonFruit(1, "Strawberry"), new FluxGroupJoinTest.PersonFruit(1, "Apple"), new FluxGroupJoinTest.PersonFruit(3, "Peach")));
        Mono<FluxGroupJoinTest.PPF> q = source1.groupJoin(source2, just2(Flux.never()), just2(Flux.never()), FluxGroupJoinTest.PPF::new).doOnNext(( ppf) -> ppf.fruits.filter(( t1) -> ppf.person.id == t1.personId).subscribe(( t1) -> ts.onNext(Arrays.asList(ppf.person.name, t1.fruit)))).ignoreElements();
        q.subscribe(ts);
        ts.assertValues(Arrays.asList("Joe", "Strawberry"), Arrays.asList("Joe", "Apple"), Arrays.asList("Charlie", "Peach")).assertComplete().assertNoError();
    }

    @Test
    public void leftThrows() {
        AssertSubscriber<Object> ts = AssertSubscriber.create();
        DirectProcessor<Integer> source1 = DirectProcessor.create();
        DirectProcessor<Integer> source2 = DirectProcessor.create();
        Flux<Flux<Integer>> m = source1.groupJoin(source2, just(Flux.never()), just(Flux.never()), add2);
        m.subscribe(ts);
        source2.onNext(1);
        source1.onError(new RuntimeException("Forced failure"));
        ts.assertErrorMessage("Forced failure").assertNotComplete().assertNoValues();
    }

    @Test
    public void rightThrows() {
        AssertSubscriber<Object> ts = AssertSubscriber.create();
        DirectProcessor<Integer> source1 = DirectProcessor.create();
        DirectProcessor<Integer> source2 = DirectProcessor.create();
        Flux<Flux<Integer>> m = source1.groupJoin(source2, just(Flux.never()), just(Flux.never()), add2);
        m.subscribe(ts);
        source1.onNext(1);
        source2.onError(new RuntimeException("Forced failure"));
        ts.assertErrorMessage("Forced failure").assertNotComplete().assertValueCount(1);
    }

    @Test
    public void leftDurationThrows() {
        AssertSubscriber<Object> ts = AssertSubscriber.create();
        DirectProcessor<Integer> source1 = DirectProcessor.create();
        DirectProcessor<Integer> source2 = DirectProcessor.create();
        Flux<Integer> duration1 = Flux.error(new RuntimeException("Forced failure"));
        Flux<Flux<Integer>> m = source1.groupJoin(source2, just(duration1), just(Flux.never()), add2);
        m.subscribe(ts);
        source1.onNext(1);
        ts.assertErrorMessage("Forced failure").assertNotComplete().assertNoValues();
    }

    @Test
    public void rightDurationThrows() {
        AssertSubscriber<Object> ts = AssertSubscriber.create();
        DirectProcessor<Integer> source1 = DirectProcessor.create();
        DirectProcessor<Integer> source2 = DirectProcessor.create();
        Flux<Integer> duration1 = Flux.error(new RuntimeException("Forced failure"));
        Flux<Flux<Integer>> m = source1.groupJoin(source2, just(Flux.never()), just(duration1), add2);
        m.subscribe(ts);
        source2.onNext(1);
        ts.assertErrorMessage("Forced failure").assertNotComplete().assertNoValues();
    }

    @Test
    public void leftDurationSelectorThrows() {
        AssertSubscriber<Object> ts = AssertSubscriber.create();
        DirectProcessor<Integer> source1 = DirectProcessor.create();
        DirectProcessor<Integer> source2 = DirectProcessor.create();
        Function<Integer, Flux<Integer>> fail = ( t1) -> {
            throw new RuntimeException("Forced failure");
        };
        Flux<Flux<Integer>> m = source1.groupJoin(source2, fail, just(Flux.never()), add2);
        m.subscribe(ts);
        source1.onNext(1);
        ts.assertErrorMessage("Forced failure").assertNotComplete().assertNoValues();
    }

    @Test
    public void rightDurationSelectorThrows() {
        AssertSubscriber<Object> ts = AssertSubscriber.create();
        DirectProcessor<Integer> source1 = DirectProcessor.create();
        DirectProcessor<Integer> source2 = DirectProcessor.create();
        Function<Integer, Flux<Integer>> fail = ( t1) -> {
            throw new RuntimeException("Forced failure");
        };
        Flux<Flux<Integer>> m = source1.groupJoin(source2, just(Flux.never()), fail, add2);
        m.subscribe(ts);
        source2.onNext(1);
        ts.assertErrorMessage("Forced failure").assertNotComplete().assertNoValues();
    }

    @Test
    public void resultSelectorThrows() {
        AssertSubscriber<Object> ts = AssertSubscriber.create();
        DirectProcessor<Integer> source1 = DirectProcessor.create();
        DirectProcessor<Integer> source2 = DirectProcessor.create();
        BiFunction<Integer, Flux<Integer>, Integer> fail = ( t1, t2) -> {
            throw new RuntimeException("Forced failure");
        };
        Flux<Integer> m = source1.groupJoin(source2, just(Flux.never()), just(Flux.never()), fail);
        m.subscribe(ts);
        source1.onNext(1);
        source2.onNext(2);
        ts.assertErrorMessage("Forced failure").assertNotComplete().assertNoValues();
    }

    @Test
    public void scanGroupJoinSubscription() {
        CoreSubscriber<String> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, ( sub) -> sub.request(100));
        GroupJoinSubscription<String, String, String, String, String> test = new FluxGroupJoin.GroupJoinSubscription<>(actual, ( s) -> Mono.just(s), ( s) -> Mono.just(s), ( l, r) -> l, Queues.one());
        assertThat(test.scan(ACTUAL)).isSameAs(actual);
        test.request(123);
        assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isEqualTo(123);
        test.queue.add(5);
        test.queue.add(10);
        assertThat(test.scan(BUFFERED)).isEqualTo(1);
        test.error = new IllegalArgumentException("boom");
        assertThat(test.scan(ERROR)).isSameAs(test.error);
        assertThat(test.scan(TERMINATED)).isFalse();
        test.active = 0;
        assertThat(test.scan(TERMINATED)).isTrue();
        assertThat(test.scan(CANCELLED)).isFalse();
        test.cancel();
        assertThat(test.scan(CANCELLED)).isTrue();
    }

    @Test
    public void scanLeftRightSubscriber() {
        CoreSubscriber<String> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, ( sub) -> sub.request(100));
        GroupJoinSubscription<String, String, String, String, String> parent = new FluxGroupJoin.GroupJoinSubscription<>(actual, ( s) -> Mono.just(s), ( s) -> Mono.just(s), ( l, r) -> l, Queues.one());
        FluxGroupJoin.LeftRightSubscriber test = new FluxGroupJoin.LeftRightSubscriber(parent, true);
        Subscription sub = Operators.emptySubscription();
        test.onSubscribe(sub);
        assertThat(test.scan(ACTUAL)).isSameAs(parent);
        assertThat(test.scan(PARENT)).isSameAs(sub);
        assertThat(test.scan(CANCELLED)).isFalse();
        test.dispose();
        assertThat(test.scan(CANCELLED)).isTrue();
    }

    @Test
    public void scanLeftRightEndSubscriber() {
        CoreSubscriber<String> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, ( sub) -> sub.request(100));
        GroupJoinSubscription<String, String, String, String, String> parent = new FluxGroupJoin.GroupJoinSubscription<>(actual, ( s) -> Mono.just(s), ( s) -> Mono.just(s), ( l, r) -> l, Queues.one());
        FluxGroupJoin.LeftRightEndSubscriber test = new FluxGroupJoin.LeftRightEndSubscriber(parent, false, 1);
        Subscription sub = Operators.emptySubscription();
        test.onSubscribe(sub);
        assertThat(test.scan(PARENT)).isSameAs(sub);
        assertThat(test.scan(CANCELLED)).isFalse();
        test.dispose();
        assertThat(test.scan(CANCELLED)).isTrue();
    }
}

