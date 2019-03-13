package org.javaee7.jpa.criteria;


import java.util.List;
import javax.inject.Inject;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * In this sample we're going to query a simple +JPA Entity+, using the +JPA Criteria API+ and perform a select,
 * update and delete operations.
 *
 * The following +JPA Entity+, represents a Movie which has a name and a comma separated list of actors:
 *
 * include::Movie[]
 *
 * The select, update and delete operations are exposed using a simple stateless ejb.
 *
 * Select every movie:
 * include::MovieBean#listMovies[]
 *
 * Update all the name of the movies to "INCEPTION" where the name of the movie is "Inception":
 * include::MovieBean#updateMovie[]
 *
 * Delete all movies where the name of the movie is "Matrix":
 * include::MovieBean#deleteMovie[]
 *
 * @author Roberto Cortez
 */
@RunWith(Arquillian.class)
public class JpaCriteriaTest {
    @Inject
    private MovieBean movieBean;

    /**
     * In the test, we're just going to invoke the different operations in sequence keeping in mind that each
     * invocation might be dependent of the previous invoked operation.
     */
    @Test
    public void testCriteria() {
        List<Movie> movies = movieBean.listMovies();// <1> Get a list of all the movies in the database.

        Assert.assertEquals(4, movies.size());// <2> 4 movies loaded on the db, so the size shoud be 4.

        Assert.assertTrue(movies.contains(new Movie(1)));
        Assert.assertTrue(movies.contains(new Movie(2)));
        Assert.assertTrue(movies.contains(new Movie(3)));
        Assert.assertTrue(movies.contains(new Movie(4)));
        movieBean.updateMovie();// <3> Update name to "INCEPTION" where name is "Inception"

        movies = movieBean.listMovies();
        Assert.assertEquals(4, movies.size());// <4> Size of movies should still be 4.

        Assert.assertEquals("INCEPTION", movies.get(2).getName());// <5> Verify the movie name change.

        movieBean.deleteMovie();// <6> Now delete the movie "Matrix"

        movies = movieBean.listMovies();
        Assert.assertFalse(movies.isEmpty());
        Assert.assertEquals(3, movies.size());// <7> Size of movies should be 3 now.

        Assert.assertFalse(movies.contains(new Movie(1)));// <8> Check if the movie "Matrix" is not on the list.

    }
}

