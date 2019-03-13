/**
 * Copyright (c) 2011-2018 Pivotal Software Inc, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package reactor.core.publisher;


import Operators.LiftFunction;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.reactivestreams.Publisher;
import reactor.core.Fuseable;


public class LiftFunctionTest {
    @Test
    public void liftMono() {
        Mono<Integer> source = Mono.just(1).hide();
        LiftFunction<Integer, Integer> liftFunction = LiftFunction.liftScannable(null, ( s, actual) -> actual);
        Publisher<Integer> liftOperator = liftFunction.apply(source);
        assertThat(liftOperator).isInstanceOf(Mono.class).isExactlyInstanceOf(MonoLift.class);
        assertThatCode(() -> liftOperator.subscribe(new BaseSubscriber<Integer>() {})).doesNotThrowAnyException();
    }

    @Test
    public void liftFlux() {
        Flux<Integer> source = Flux.just(1).hide();
        LiftFunction<Integer, Integer> liftFunction = LiftFunction.liftScannable(null, ( s, actual) -> actual);
        Publisher<Integer> liftOperator = liftFunction.apply(source);
        assertThat(liftOperator).isInstanceOf(Flux.class).isExactlyInstanceOf(FluxLift.class);
        assertThatCode(() -> liftOperator.subscribe(new BaseSubscriber<Integer>() {})).doesNotThrowAnyException();
    }

    @Test
    public void liftParallelFlux() {
        ParallelFlux<Integer> source = Flux.just(1).parallel(2).hide();
        LiftFunction<Integer, Integer> liftFunction = LiftFunction.liftScannable(null, ( s, actual) -> actual);
        Publisher<Integer> liftOperator = liftFunction.apply(source);
        assertThat(liftOperator).isInstanceOf(ParallelFlux.class).isExactlyInstanceOf(ParallelLift.class);
        assertThatCode(() -> liftOperator.subscribe(new BaseSubscriber<Integer>() {})).doesNotThrowAnyException();
    }

    @Test
    public void liftConnectableFlux() {
        ConnectableFlux<Integer> source = Flux.just(1).publish();// TODO hide if ConnectableFlux gets a hide function

        LiftFunction<Integer, Integer> liftFunction = LiftFunction.liftScannable(null, ( s, actual) -> actual);
        Publisher<Integer> liftOperator = liftFunction.apply(source);
        assertThat(liftOperator).isInstanceOf(ConnectableFlux.class).isExactlyInstanceOf(ConnectableLift.class);
        assertThatCode(() -> liftOperator.subscribe(new BaseSubscriber<Integer>() {})).doesNotThrowAnyException();
    }

    @Test
    public void liftMonoFuseable() {
        Mono<Integer> source = Mono.just(1);
        LiftFunction<Integer, Integer> liftFunction = LiftFunction.liftScannable(null, ( s, actual) -> actual);
        Publisher<Integer> liftOperator = liftFunction.apply(source);
        assertThat(liftOperator).isInstanceOf(Mono.class).isInstanceOf(Fuseable.class).isExactlyInstanceOf(MonoLiftFuseable.class);
        assertThatCode(() -> liftOperator.subscribe(new BaseSubscriber<Integer>() {})).doesNotThrowAnyException();
    }

    @Test
    public void liftFluxFuseable() {
        Flux<Integer> source = Flux.just(1);
        LiftFunction<Integer, Integer> liftFunction = LiftFunction.liftScannable(null, ( s, actual) -> actual);
        Publisher<Integer> liftOperator = liftFunction.apply(source);
        assertThat(liftOperator).isInstanceOf(Flux.class).isInstanceOf(Fuseable.class).isExactlyInstanceOf(FluxLiftFuseable.class);
        assertThatCode(() -> liftOperator.subscribe(new BaseSubscriber<Integer>() {})).doesNotThrowAnyException();
    }

    @Test
    public void liftParallelFluxFuseable() {
        ParallelFlux<List<Integer>> source = Flux.just(1).parallel(2).collect(ArrayList::new, List::add);
        LiftFunction<List<Integer>, List<Integer>> liftFunction = LiftFunction.liftScannable(null, ( s, actual) -> actual);
        Publisher<List<Integer>> liftOperator = liftFunction.apply(source);
        assertThat(liftOperator).isInstanceOf(ParallelFlux.class).isExactlyInstanceOf(ParallelLiftFuseable.class);
        assertThatCode(() -> liftOperator.subscribe(new BaseSubscriber<List<Integer>>() {})).doesNotThrowAnyException();
    }

    @Test
    public void liftConnectableFluxFuseable() {
        ConnectableFlux<Integer> source = Flux.just(1).publish().replay(2);
        LiftFunction<Integer, Integer> liftFunction = LiftFunction.liftScannable(null, ( s, actual) -> actual);
        Publisher<Integer> liftOperator = liftFunction.apply(source);
        assertThat(liftOperator).isInstanceOf(ConnectableFlux.class).isInstanceOf(Fuseable.class).isExactlyInstanceOf(ConnectableLiftFuseable.class);
        assertThatCode(() -> liftOperator.subscribe(new BaseSubscriber<Integer>() {})).doesNotThrowAnyException();
    }

    @Test
    public void liftGroupedFluxFuseable() {
        Flux<GroupedFlux<String, Integer>> sourceGroups = Flux.just(1).groupBy(( i) -> "" + i);
        LiftFunction<Integer, Integer> liftFunction = LiftFunction.liftScannable(null, ( s, actual) -> actual);
        sourceGroups.map(( g) -> liftFunction.apply(g)).doOnNext(( liftOperator) -> assertThat(liftOperator).isInstanceOf(.class).isInstanceOf(.class).isExactlyInstanceOf(.class)).blockLast();
    }
}

