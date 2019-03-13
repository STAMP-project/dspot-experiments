package org.javaee7.jpa.listeners;


import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.hamcrest.CoreMatchers;
import org.javaee7.Parameter;
import org.javaee7.ParameterRule;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class JpaListenerInjectionTest {
    public static final List<ImmutablePair<String, Integer>> movies = Arrays.asList(new ImmutablePair<>("The Matrix", 60), new ImmutablePair<>("The Lord of The Rings", 70), new ImmutablePair<>("Inception", 80), new ImmutablePair<>("The Shining", 90));

    @Rule
    public ParameterRule<ImmutablePair<String, Integer>> rule = new ParameterRule(JpaListenerInjectionTest.movies);

    @Parameter
    private ImmutablePair<String, Integer> expectedMovie;

    @Inject
    private MovieBean bean;

    @Test
    public void should_provide_movie_rating_via_jpa_listener_injection() {
        // Given
        Movie movie = bean.getMovieByName(expectedMovie.getLeft());
        Assert.assertThat(movie.getRating(), CoreMatchers.is(CoreMatchers.equalTo(expectedMovie.getRight())));
    }
}

