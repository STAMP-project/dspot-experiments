package com.baeldung.java9.streams.reactive;


import TimeUnit.MILLISECONDS;
import java.util.List;
import java.util.concurrent.SubmissionPublisher;
import org.junit.Test;


public class ReactiveStreamsTest {
    @Test
    public void givenPublisher_whenSubscribeToIt_thenShouldConsumeAllElements() throws InterruptedException {
        // given
        SubmissionPublisher<String> publisher = new SubmissionPublisher();
        EndSubscriber<String> subscriber = new EndSubscriber(6);
        publisher.subscribe(subscriber);
        List<String> items = List.of(List, "1", "x", "2", "x", "3", "x");
        // when
        assertThat(publisher.getNumberOfSubscribers()).isEqualTo(1);
        items.forEach(publisher::submit);
        publisher.close();
        // then
        await().atMost(1000, MILLISECONDS).until(() -> assertThat(subscriber.consumedElements).containsExactlyElementsOf(items));
    }

    @Test
    public void givenPublisher_whenSubscribeAndTransformElements_thenShouldConsumeAllElements() throws InterruptedException {
        // given
        SubmissionPublisher<String> publisher = new SubmissionPublisher();
        TransformProcessor<String, Integer> transformProcessor = new TransformProcessor(Integer::parseInt);
        EndSubscriber<Integer> subscriber = new EndSubscriber(3);
        List<String> items = List.of(List, "1", "2", "3");
        List<Integer> expectedResult = List.of(List, 1, 2, 3);
        // when
        publisher.subscribe(transformProcessor);
        transformProcessor.subscribe(subscriber);
        items.forEach(publisher::submit);
        publisher.close();
        // then
        await().atMost(1000, MILLISECONDS).until(() -> assertThat(subscriber.consumedElements).containsExactlyElementsOf(expectedResult));
    }

    @Test
    public void givenPublisher_whenRequestForOnlyOneElement_thenShouldConsumeOnlyThatOne() throws InterruptedException {
        // given
        SubmissionPublisher<String> publisher = new SubmissionPublisher();
        EndSubscriber<String> subscriber = new EndSubscriber(1);
        publisher.subscribe(subscriber);
        List<String> items = List.of(List, "1", "x", "2", "x", "3", "x");
        List<String> expected = List.of(List, "1");
        // when
        assertThat(publisher.getNumberOfSubscribers()).isEqualTo(1);
        items.forEach(publisher::submit);
        publisher.close();
        // then
        await().atMost(1000, MILLISECONDS).until(() -> assertThat(subscriber.consumedElements).containsExactlyElementsOf(expected));
    }
}

