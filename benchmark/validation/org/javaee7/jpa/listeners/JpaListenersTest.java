package org.javaee7.jpa.listeners;


import MovieListener.entityListenersCountDownLatch;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * In this sample we're going to query a simple +JPA Entity+, using the +JPA EntityManager+ and perform a findAll,
 * persist, merge, and remove operations. By calling these operations we want to demonstrate the behaviour of +Entity
 * Listener Methods+: +@PostLoad+, +@PrePersist+, +@PostPersist+, +@PreUpdate+, +@PostUpdate+, +@PreRemove+,
 * +@PostRemove+ defined on +MovieListener+:
 *
 * include::MovieListener[]
 *
 * The following +JPA Entity+, represents a Movie which has a name and a comma separated list of actors:
 *
 * include::Movie[]
 *
 * @author Roberto Cortez
 */
@RunWith(Arquillian.class)
public class JpaListenersTest {
    @Inject
    private MovieBean movieBean;

    /**
     * In the test, we're just going to invoke the different operations in sequence keeping in mind that each
     * invocation might be dependent of the previous invoked operation.
     */
    @Test
    public void testListeners() throws Exception {
        List<Movie> movies = movieBean.listMovies();// <1> 4 movies in the database, so +@PostLoad+ method called 4x

        Assert.assertEquals(4, movies.size());
        Assert.assertFalse(prePersistInvoked);
        Assert.assertFalse(postPersistInvoked);
        movieBean.createMovie();// <2> On persist both +@PrePersist+ and +@PostPersist+ are called

        Assert.assertTrue(prePersistInvoked);
        Assert.assertTrue(postPersistInvoked);
        movies = movieBean.listMovies();// <3> 5 movies now, so +@PostLoad+ method called 5x

        Assert.assertEquals(5, movies.size());
        Assert.assertTrue(movies.contains(new Movie(5)));
        Assert.assertFalse(preUpdateInvoked);
        Assert.assertFalse(postUpdateInvoked);
        movieBean.updateMovie();// <4> On merge both +@PreUpdate+ and +@PostUpdate+ are called

        Assert.assertTrue(preUpdateInvoked);
        Assert.assertTrue(postUpdateInvoked);
        movies = movieBean.listMovies();// <5> Still 5 mpvies, so +@PostLoad+ method called again 5x

        Assert.assertEquals(5, movies.size());
        Assert.assertEquals("Inception2", movies.get(2).getName());
        Assert.assertFalse(preRemoveInvoked);
        Assert.assertFalse(postRemoveInvoked);
        movieBean.deleteMovie();// <6> On remove both +@PreRemove+ and +@PostRemove+ are called

        Assert.assertTrue(preRemoveInvoked);
        Assert.assertTrue(postRemoveInvoked);
        movies = movieBean.listMovies();// <7> 4 movies now, so +@PostLoad+ method called 4x

        Assert.assertFalse(movies.isEmpty());
        Assert.assertEquals(4, movies.size());
        Assert.assertFalse(movies.contains(new Movie(3)));
        Assert.assertTrue(entityListenersCountDownLatch.await(0, TimeUnit.SECONDS));
    }
}

