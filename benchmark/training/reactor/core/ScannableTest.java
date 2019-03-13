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
package reactor.core;


import Scannable.Attr.ACTUAL;
import Scannable.Attr.BUFFERED;
import Scannable.Attr.CANCELLED;
import Scannable.Attr.CAPACITY;
import Scannable.Attr.DELAY_ERROR;
import Scannable.Attr.ERROR;
import Scannable.Attr.LARGE_BUFFERED;
import Scannable.Attr.NAME;
import Scannable.Attr.NULL_SCAN;
import Scannable.Attr.PARENT;
import Scannable.Attr.PREFETCH;
import Scannable.Attr.REQUESTED_FROM_DOWNSTREAM;
import Scannable.Attr.TAGS;
import Scannable.Attr.TERMINATED;
import Scannable.Attr.UNAVAILABLE_SCAN;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import org.junit.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ParallelFlux;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;


/**
 *
 *
 * @author Stephane Maldini
 */
public class ScannableTest {
    static final Scannable scannable = ( key) -> {
        if (key == Scannable.Attr.BUFFERED)
            return 1;

        if (key == Scannable.Attr.TERMINATED)
            return true;

        if (key == Scannable.Attr.PARENT)
            return null;

        if (key == Scannable.Attr.ACTUAL)
            return ((Scannable) (( k) -> ((Scannable) (( k2) -> null))));

        return null;
    };

    @Test
    public void unavailableScan() {
        assertThat(Scannable.from("nothing")).isEqualTo(UNAVAILABLE_SCAN);
        assertThat(Scannable.from("nothing").isScanAvailable()).isFalse();
        assertThat(Scannable.from("nothing").inners().count()).isEqualTo(0);
        assertThat(Scannable.from("nothing").parents().count()).isEqualTo(0);
        assertThat(Scannable.from("nothing").actuals().count()).isEqualTo(0);
        assertThat(Scannable.from("nothing").scan(TERMINATED)).isFalse();
        assertThat(Scannable.from("nothing").scanOrDefault(BUFFERED, 0)).isEqualTo(0);
        assertThat(Scannable.from("nothing").scan(ACTUAL)).isNull();
    }

    @Test
    public void meaningfulDefaults() {
        Scannable emptyScannable = ( key) -> null;
        assertThat(emptyScannable.scan(BUFFERED)).isEqualTo(0);
        assertThat(emptyScannable.scan(LARGE_BUFFERED)).isNull();
        assertThat(emptyScannable.scan(CAPACITY)).isEqualTo(0);
        assertThat(emptyScannable.scan(PREFETCH)).isEqualTo(0);
        assertThat(emptyScannable.scan(REQUESTED_FROM_DOWNSTREAM)).isEqualTo(0L);
        assertThat(emptyScannable.scan(CANCELLED)).isFalse();
        assertThat(emptyScannable.scan(DELAY_ERROR)).isFalse();
        assertThat(emptyScannable.scan(TERMINATED)).isFalse();
        assertThat(emptyScannable.scan(ERROR)).isNull();
        assertThat(emptyScannable.scan(ACTUAL)).isNull();
        assertThat(emptyScannable.scan(PARENT)).isNull();
        assertThat(emptyScannable.scan(TAGS)).isNull();
        assertThat(emptyScannable.scan(NAME)).isNull();
    }

    @Test
    public void scanOrDefaultOverridesGlobalDefault() {
        Scannable emptyScannable = ( key) -> null;
        assertThat(emptyScannable.scanOrDefault(BUFFERED, 123)).isEqualTo(123);// global 0

        assertThat(emptyScannable.scanOrDefault(CAPACITY, 123)).isEqualTo(123);// global 0

        assertThat(emptyScannable.scanOrDefault(PREFETCH, 123)).isEqualTo(123);// global 0

        assertThat(emptyScannable.scanOrDefault(LARGE_BUFFERED, 123L)).isEqualTo(123L);// global null

        assertThat(emptyScannable.scanOrDefault(REQUESTED_FROM_DOWNSTREAM, 123L)).isEqualTo(123L);// global 0

        assertThat(emptyScannable.scanOrDefault(CANCELLED, true)).isTrue();// global false

        assertThat(emptyScannable.scanOrDefault(DELAY_ERROR, true)).isTrue();// global false

        assertThat(emptyScannable.scanOrDefault(TERMINATED, true)).isTrue();// global false

        assertThat(emptyScannable.scanOrDefault(ERROR, new IllegalStateException())).isInstanceOf(IllegalStateException.class);// global null

        assertThat(emptyScannable.scanOrDefault(ACTUAL, NULL_SCAN)).isSameAs(NULL_SCAN);// global null

        assertThat(emptyScannable.scanOrDefault(PARENT, NULL_SCAN)).isSameAs(NULL_SCAN);// global null

        List<Tuple2<String, String>> tags = Collections.singletonList(Tuples.of("some", "key"));
        assertThat(emptyScannable.scanOrDefault(TAGS, tags.stream())).containsExactlyElementsOf(tags);// global null

        assertThat(emptyScannable.scanOrDefault(NAME, "SomeName")).isEqualTo("SomeName");// global null

    }

    @Test
    public void availableScan() {
        assertThat(Scannable.from(ScannableTest.scannable)).isEqualTo(ScannableTest.scannable);
        assertThat(Scannable.from(ScannableTest.scannable).isScanAvailable()).isTrue();
        assertThat(Scannable.from(ScannableTest.scannable).inners().count()).isEqualTo(0);
        assertThat(Scannable.from(ScannableTest.scannable).parents().count()).isEqualTo(0);
        assertThat(Scannable.from(ScannableTest.scannable).actuals().count()).isEqualTo(2);
        assertThat(Scannable.from(ScannableTest.scannable).scan(TERMINATED)).isTrue();
        assertThat(Scannable.from(ScannableTest.scannable).scanOrDefault(BUFFERED, 0)).isEqualTo(1);
        assertThat(Scannable.from(ScannableTest.scannable).scan(ACTUAL)).isEqualTo(ScannableTest.scannable.actuals().findFirst().get());
    }

    @Test
    public void nullScan() {
        assertThat(Scannable.from(null)).isNotNull().isSameAs(NULL_SCAN);
    }

    @Test
    public void namedFluxTest() {
        Flux<Integer> named1 = Flux.range(1, 10).name("100s");
        Flux<Integer> named2 = named1.filter(( i) -> (i % 3) == 0).name("multiple of 3 100s").hide();
        assertThat(Scannable.from(named1).name()).isEqualTo("100s");
        assertThat(Scannable.from(named2).name()).isEqualTo("multiple of 3 100s");
    }

    @Test
    public void namedHideFluxTest() {
        Flux<Integer> named1 = Flux.range(1, 10).hide().name("100s");
        Flux<Integer> named2 = named1.filter(( i) -> (i % 3) == 0).name("multiple of 3 100s").hide();
        assertThat(Scannable.from(named1).name()).isEqualTo("100s");
        assertThat(Scannable.from(named2).name()).isEqualTo("multiple of 3 100s");
    }

    @Test
    public void namedOverridenFluxTest() {
        Flux<Integer> named1 = Flux.range(1, 10).name("1s").name("100s");
        Flux<Integer> named2 = named1.filter(( i) -> (i % 3) == 0).name("multiple of 3 100s").hide();
        assertThat(Scannable.from(named1).name()).isEqualTo("100s");
        assertThat(Scannable.from(named2).name()).isEqualTo("multiple of 3 100s");
    }

    @Test
    public void namedOverridenHideFluxTest() {
        Flux<Integer> named1 = Flux.range(1, 10).hide().name("1s").name("100s");
        Flux<Integer> named2 = named1.filter(( i) -> (i % 3) == 0).name("multiple of 3 100s").hide();
        assertThat(Scannable.from(named1).name()).isEqualTo("100s");
        assertThat(Scannable.from(named2).name()).isEqualTo("multiple of 3 100s");
    }

    @Test
    public void scannableNameDefaultsToToString() {
        final Flux<Integer> flux = Flux.range(1, 10).map(( i) -> i + 10);
        assertThat(Scannable.from(flux).name()).isEqualTo(Scannable.from(flux).stepName()).isEqualTo("map");
    }

    @Test
    public void taggedFluxTest() {
        Flux<Integer> tagged1 = Flux.range(1, 10).tag("1", "One");
        Flux<Integer> tagged2 = tagged1.filter(( i) -> (i % 3) == 0).tag("2", "Two").hide();
        final Stream<Tuple2<String, String>> scannedTags1 = Scannable.from(tagged1).tags();
        assertThat(scannedTags1.iterator()).containsExactlyInAnyOrder(Tuples.of("1", "One"));
        final Stream<Tuple2<String, String>> scannedTags2 = Scannable.from(tagged2).tags();
        assertThat(scannedTags2.iterator()).containsExactlyInAnyOrder(Tuples.of("1", "One"), Tuples.of("2", "Two"));
    }

    @Test
    public void taggedHideFluxTest() {
        Flux<Integer> tagged1 = Flux.range(1, 10).hide().tag("1", "One");
        Flux<Integer> tagged2 = tagged1.filter(( i) -> (i % 3) == 0).tag("2", "Two").hide();
        final Stream<Tuple2<String, String>> scannedTags1 = Scannable.from(tagged1).tags();
        assertThat(scannedTags1.iterator()).containsExactlyInAnyOrder(Tuples.of("1", "One"));
        final Stream<Tuple2<String, String>> scannedTags2 = Scannable.from(tagged2).tags();
        assertThat(scannedTags2.iterator()).containsExactlyInAnyOrder(Tuples.of("1", "One"), Tuples.of("2", "Two"));
    }

    @Test
    public void taggedAppendedFluxTest() {
        Flux<Integer> tagged1 = Flux.range(1, 10).tag("1", "One").tag("2", "Two");
        final Stream<Tuple2<String, String>> scannedTags = Scannable.from(tagged1).tags();
        assertThat(scannedTags.iterator()).containsExactlyInAnyOrder(Tuples.of("1", "One"), Tuples.of("2", "Two"));
    }

    @Test
    public void taggedAppendedHideFluxTest() {
        Flux<Integer> tagged1 = Flux.range(1, 10).hide().tag("1", "One").tag("2", "Two");
        final Stream<Tuple2<String, String>> scannedTags = Scannable.from(tagged1).tags();
        assertThat(scannedTags.iterator()).containsExactlyInAnyOrder(Tuples.of("1", "One"), Tuples.of("2", "Two"));
    }

    @Test
    public void namedMonoTest() {
        Mono<Integer> named1 = Mono.just(1).name("100s");
        Mono<Integer> named2 = named1.filter(( i) -> (i % 3) == 0).name("multiple of 3 100s").hide();
        assertThat(Scannable.from(named1).name()).isEqualTo("100s");
        assertThat(Scannable.from(named2).name()).isEqualTo("multiple of 3 100s");
    }

    @Test
    public void namedHideMonoTest() {
        Mono<Integer> named1 = Mono.just(1).hide().name("100s");
        Mono<Integer> named2 = named1.filter(( i) -> (i % 3) == 0).name("multiple of 3 100s").hide();
        assertThat(Scannable.from(named1).name()).isEqualTo("100s");
        assertThat(Scannable.from(named2).name()).isEqualTo("multiple of 3 100s");
    }

    @Test
    public void namedOverridenMonoTest() {
        Mono<Integer> named1 = Mono.just(1).name("1s").name("100s");
        Mono<Integer> named2 = named1.filter(( i) -> (i % 3) == 0).name("multiple of 3 100s").hide();
        assertThat(Scannable.from(named1).name()).isEqualTo("100s");
        assertThat(Scannable.from(named2).name()).isEqualTo("multiple of 3 100s");
    }

    @Test
    public void namedOverridenHideMonoTest() {
        Mono<Integer> named1 = Mono.just(1).hide().name("1s").name("100s");
        Mono<Integer> named2 = named1.filter(( i) -> (i % 3) == 0).name("multiple of 3 100s").hide();
        assertThat(Scannable.from(named1).name()).isEqualTo("100s");
        assertThat(Scannable.from(named2).name()).isEqualTo("multiple of 3 100s");
    }

    @Test
    public void scannableNameMonoDefaultsToToString() {
        final Mono<Integer> flux = Mono.just(1).map(( i) -> i + 10);
        assertThat(Scannable.from(flux).name()).isEqualTo(Scannable.from(flux).stepName()).isEqualTo("map");
    }

    @Test
    public void taggedMonoTest() {
        Mono<Integer> tagged1 = Mono.just(1).tag("1", "One");
        Mono<Integer> tagged2 = tagged1.filter(( i) -> (i % 3) == 0).tag("2", "Two").hide();
        final Stream<Tuple2<String, String>> scannedTags1 = Scannable.from(tagged1).tags();
        assertThat(scannedTags1.iterator()).containsExactlyInAnyOrder(Tuples.of("1", "One"));
        final Stream<Tuple2<String, String>> scannedTags2 = Scannable.from(tagged2).tags();
        assertThat(scannedTags2.iterator()).containsExactlyInAnyOrder(Tuples.of("1", "One"), Tuples.of("2", "Two"));
    }

    @Test
    public void taggedHideMonoTest() {
        Mono<Integer> tagged1 = Mono.just(1).hide().tag("1", "One");
        Mono<Integer> tagged2 = tagged1.filter(( i) -> (i % 3) == 0).tag("2", "Two").hide();
        final Stream<Tuple2<String, String>> scannedTags1 = Scannable.from(tagged1).tags();
        assertThat(scannedTags1.iterator()).containsExactlyInAnyOrder(Tuples.of("1", "One"));
        final Stream<Tuple2<String, String>> scannedTags2 = Scannable.from(tagged2).tags();
        assertThat(scannedTags2.iterator()).containsExactlyInAnyOrder(Tuples.of("1", "One"), Tuples.of("2", "Two"));
    }

    @Test
    public void taggedAppendedMonoTest() {
        Mono<Integer> tagged1 = Mono.just(1).tag("1", "One").tag("2", "Two");
        final Stream<Tuple2<String, String>> scannedTags = Scannable.from(tagged1).tags();
        assertThat(scannedTags.iterator()).containsExactlyInAnyOrder(Tuples.of("1", "One"), Tuples.of("2", "Two"));
    }

    @Test
    public void taggedAppendedHideMonoTest() {
        Mono<Integer> tagged1 = Mono.just(1).hide().tag("1", "One").tag("2", "Two");
        final Stream<Tuple2<String, String>> scannedTags = Scannable.from(tagged1).tags();
        assertThat(scannedTags.iterator()).containsExactlyInAnyOrder(Tuples.of("1", "One"), Tuples.of("2", "Two"));
    }

    @Test
    public void namedParallelFluxTest() {
        ParallelFlux<Integer> named1 = ParallelFlux.from(Mono.just(1)).name("100s");
        ParallelFlux<Integer> named2 = named1.filter(( i) -> (i % 3) == 0).name("multiple of 3 100s").hide();
        assertThat(Scannable.from(named1).name()).isEqualTo("100s");
        assertThat(Scannable.from(named2).name()).isEqualTo("multiple of 3 100s");
    }

    @Test
    public void namedOverridenParallelFluxTest() {
        ParallelFlux<Integer> named1 = ParallelFlux.from(Mono.just(1)).name("1s").name("100s");
        ParallelFlux<Integer> named2 = named1.filter(( i) -> (i % 3) == 0).name("multiple of 3 100s").hide();
        assertThat(Scannable.from(named1).name()).isEqualTo("100s");
        assertThat(Scannable.from(named2).name()).isEqualTo("multiple of 3 100s");
    }

    @Test
    public void scannableNameParallelFluxDefaultsToToString() {
        final ParallelFlux<Integer> flux = ParallelFlux.from(Mono.just(1)).map(( i) -> i + 10);
        assertThat(Scannable.from(flux).name()).isEqualTo(Scannable.from(flux).stepName()).isEqualTo("map");
    }

    @Test
    public void taggedParallelFluxTest() {
        ParallelFlux<Integer> tagged1 = ParallelFlux.from(Mono.just(1)).tag("1", "One");
        ParallelFlux<Integer> tagged2 = tagged1.filter(( i) -> (i % 3) == 0).tag("2", "Two").hide();
        Stream<Tuple2<String, String>> scannedTags1 = Scannable.from(tagged1).tags();
        assertThat(scannedTags1.iterator()).containsExactlyInAnyOrder(Tuples.of("1", "One"));
        Stream<Tuple2<String, String>> scannedTags2 = Scannable.from(tagged2).tags();
        assertThat(scannedTags2.iterator()).containsExactlyInAnyOrder(Tuples.of("1", "One"), Tuples.of("2", "Two"));
    }

    @Test
    public void taggedAppendedParallelFluxTest() {
        ParallelFlux<Integer> tagged1 = ParallelFlux.from(Mono.just(1)).tag("1", "One").tag("2", "Two");
        final Stream<Tuple2<String, String>> scannedTags = Scannable.from(tagged1).tags();
        assertThat(scannedTags.iterator()).containsExactlyInAnyOrder(Tuples.of("1", "One"), Tuples.of("2", "Two"));
    }

    @Test
    public void scanForParentIsSafe() {
        Scannable scannable = ( key) -> "String";
        assertThat(scannable.scan(PARENT)).isSameAs(UNAVAILABLE_SCAN);
    }

    @Test
    public void scanForActualIsSafe() {
        Scannable scannable = ( key) -> "String";
        assertThat(scannable.scan(ACTUAL)).isSameAs(UNAVAILABLE_SCAN);
    }

    @Test
    public void scanForRawParentOrActual() {
        Scannable scannable = ( key) -> "String";
        assertThat(scannable.scanUnsafe(ACTUAL)).isInstanceOf(String.class).isEqualTo("String");
        assertThat(scannable.scan(ACTUAL)).isSameAs(UNAVAILABLE_SCAN);
        assertThat(scannable.scanUnsafe(PARENT)).isInstanceOf(String.class).isEqualTo("String");
        assertThat(scannable.scan(PARENT)).isSameAs(UNAVAILABLE_SCAN);
    }

    @Test
    public void attributeIsConversionSafe() {
        assertThat(ACTUAL.isConversionSafe()).as("ACTUAL").isTrue();
        assertThat(PARENT.isConversionSafe()).as("PARENT").isTrue();
        assertThat(BUFFERED.isConversionSafe()).as("BUFFERED").isFalse();
        assertThat(CAPACITY.isConversionSafe()).as("CAPACITY").isFalse();
        assertThat(CANCELLED.isConversionSafe()).as("CANCELLED").isFalse();
        assertThat(DELAY_ERROR.isConversionSafe()).as("DELAY_ERROR").isFalse();
        assertThat(ERROR.isConversionSafe()).as("ERROR").isFalse();
        assertThat(LARGE_BUFFERED.isConversionSafe()).as("LARGE_BUFFERED").isFalse();
        assertThat(NAME.isConversionSafe()).as("NAME").isFalse();
        assertThat(PREFETCH.isConversionSafe()).as("PREFETCH").isFalse();
        assertThat(REQUESTED_FROM_DOWNSTREAM.isConversionSafe()).as("REQUESTED_FROM_DOWNSTREAM").isFalse();
        assertThat(TERMINATED.isConversionSafe()).as("TERMINATED").isFalse();
    }

    @Test
    public void operatorChainWithDebugMode() {
        Hooks.onOperatorDebug();
        List<String> downstream = new ArrayList<>();
        List<String> upstream = new ArrayList<>();
        try {
            Mono<?> m = Flux.from(( s) -> {
                Scannable thisSubscriber = Scannable.from(s);
                assertThat(thisSubscriber.isScanAvailable()).as("thisSubscriber.isScanAvailable").isTrue();
                thisSubscriber.steps().forEach(downstream::add);
            }).map(( a) -> a).delayElements(Duration.ofMillis(10)).filter(( a) -> true).reduce(( a, b) -> b);
            m.subscribe();
            Scannable thisOperator = Scannable.from(m);
            assertThat(thisOperator.isScanAvailable()).as("thisOperator.isScanAvailable").isTrue();
            thisOperator.steps().forEach(upstream::add);
        } finally {
            Hooks.resetOnOperatorDebug();
        }
        assertThat(downstream).containsExactly("Flux.map ? reactor.core.ScannableTest.operatorChainWithDebugMode(ScannableTest.java:542)", "Flux.delayElements ? reactor.core.ScannableTest.operatorChainWithDebugMode(ScannableTest.java:543)", "Flux.filter ? reactor.core.ScannableTest.operatorChainWithDebugMode(ScannableTest.java:544)", "Flux.reduce ? reactor.core.ScannableTest.operatorChainWithDebugMode(ScannableTest.java:545)", "lambda");
        assertThat(upstream).containsExactly("source(FluxSource)", "Flux.map ? reactor.core.ScannableTest.operatorChainWithDebugMode(ScannableTest.java:542)", "Flux.delayElements ? reactor.core.ScannableTest.operatorChainWithDebugMode(ScannableTest.java:543)", "Flux.filter ? reactor.core.ScannableTest.operatorChainWithDebugMode(ScannableTest.java:544)", "Flux.reduce ? reactor.core.ScannableTest.operatorChainWithDebugMode(ScannableTest.java:545)");
    }

    @Test
    public void operatorChainWithLastSubscriber() {
        AtomicReference<Subscription> subRef = new AtomicReference<>(null);
        Mono<String> m = Flux.just("foo").map(( a) -> a).filter(( a) -> true).reduce(( a, b) -> b).doOnSubscribe(subRef::set);
        m.subscribe();
        Scannable lastSubscriber = Scannable.from(subRef.get());
        assertThat(lastSubscriber.isScanAvailable()).as("lastSubscriber.isScanAvailable").isTrue();
        Stream<String> chain = lastSubscriber.steps();
        assertThat(chain).containsExactly("just", "map", "filter", "reduce", "peek", "lambda");
    }

    @Test
    public void operatorChainWithLastOperator() {
        Mono<String> m = Flux.concat(Mono.just("foo"), Mono.just("bar")).map(( a) -> a).filter(( a) -> true).reduce(( a, b) -> b).doOnSubscribe(( sub) -> {
        });
        Scannable operator = Scannable.from(m);
        assertThat(operator.isScanAvailable()).as("operator.isScanAvailable").isTrue();
        assertThat(operator.steps()).containsExactly("source(FluxConcatArray)", "map", "filter", "reduce", "peek");
    }

    @Test
    public void operatorChainWithCheckpoint() {
        Flux<String> flux = Flux.just("foo").checkpoint("checkpointHere", true).map(( a) -> a);
        assertThat(Scannable.from(flux).steps()).containsExactly("source(FluxJust)", "Flux.checkpoint ? reactor.core.ScannableTest.operatorChainWithCheckpoint(ScannableTest.java:614)", "map");
    }

    @Test
    public void operatorChainWithLightCheckpoint() {
        Flux<String> flux = Flux.just("foo").checkpoint("checkpointHere").map(( a) -> a);
        assertThat(Scannable.from(flux).steps()).containsExactly("source(FluxJust)", "checkpoint(\"checkpointHere\")", "map");
    }

    @Test
    public void operatorChainWithoutDebugMode() {
        List<String> downstream = new ArrayList<>();
        Mono<?> m = Flux.from(( s) -> {
            Scannable thisSubscriber = Scannable.from(s);
            assertThat(thisSubscriber.isScanAvailable()).as("thisSubscriber.isScanAvailable").isTrue();
            thisSubscriber.steps().forEach(downstream::add);
        }).map(( a) -> a).filter(( a) -> true).reduce(( a, b) -> b);
        m.subscribe();
        assertThat(downstream).as("from downstream").containsExactly("map", "filter", "reduce", "lambda");
        Scannable thisOperator = Scannable.from(m);
        assertThat(thisOperator.isScanAvailable()).as("thisOperator.isScanAvailable").isTrue();
        assertThat(thisOperator.steps()).as("from upstream").containsExactly("source(FluxSource)", "map", "filter", "reduce");
    }
}

