package com.baeldung.spliteratorAPI;


import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.junit.Test;


public class ExecutorUnitTest {
    Article article;

    Stream<Author> stream;

    Spliterator<Author> spliterator;

    Spliterator<Article> split1;

    Spliterator<Article> split2;

    @Test
    public void givenAstreamOfAuthors_whenProcessedInParallelWithCustomSpliterator_coubtProducessRightOutput() {
        Stream<Author> stream2 = StreamSupport.stream(spliterator, true);
        assertThat(Executor.countAutors(stream2.parallel())).isEqualTo(9);
    }

    @Test
    public void givenSpliterator_whenAppliedToAListOfArticle_thenSplittedInHalf() {
        assertThat(call()).containsSequence((((Executor.generateElements().size()) / 2) + ""));
        assertThat(call()).containsSequence((((Executor.generateElements().size()) / 2) + ""));
    }
}

