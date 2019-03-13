package org.javaee7.jpa.entitygraph;


import Movie_.movieActors;
import Movie_.movieAwards;
import Movie_.movieDirectors;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnitUtil;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * In this sample we're going to query a +JPA Entity+ and control property loading by providing +Hints+ using the new
 * +JPA Entity Graph+ API.
 * <p/>
 * Entity Graphs are used in the specification of fetch plans for query or find operations.
 *
 * <p>
 * See: http://radcortez.com/jpa-entity-graphs
 *
 * @author Roberto Cortez
 */
@RunWith(Arquillian.class)
public class EntityGraphTest {
    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private MovieBean movieBean;

    @Test
    public void testEntityGraphMovieDefault() throws Exception {
        PersistenceUnitUtil persistenceUnitUtil = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
        List<Movie> listMoviesDefaultFetch = movieBean.listMovies();
        for (Movie movie : listMoviesDefaultFetch) {
            Assert.assertFalse(persistenceUnitUtil.isLoaded(movie, "movieActors"));
            Assert.assertTrue(persistenceUnitUtil.isLoaded(movie, "movieDirectors"));
            Assert.assertFalse(persistenceUnitUtil.isLoaded(movie, "movieAwards"));
        }
    }

    @Test
    public void testEntityGraphMovieWithActors() throws Exception {
        PersistenceUnitUtil persistenceUnitUtil = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
        List<Movie> listMoviesWithActorsFetch = movieBean.listMovies("javax.persistence.fetchgraph", "movieWithActors");
        for (Movie movie : listMoviesWithActorsFetch) {
            Assert.assertTrue(persistenceUnitUtil.isLoaded(movie, "movieActors"));
            Assert.assertFalse(movie.getMovieActors().isEmpty());
            for (MovieActor movieActor : movie.getMovieActors()) {
                Assert.assertFalse(persistenceUnitUtil.isLoaded(movieActor, "movieActorAwards"));
            }
            // https://hibernate.atlassian.net/browse/HHH-8776
            // The specification states that by using fetchgraph, attributes should stay unloaded even if defined as
            // EAGER (movieDirectors), but specification also states that the persistence provider is allowed to fetch
            // additional state.
            Assert.assertTrue(((persistenceUnitUtil.isLoaded(movie, "movieDirectors")) || (!(persistenceUnitUtil.isLoaded(movie, "movieDirectors")))));
            Assert.assertFalse(persistenceUnitUtil.isLoaded(movie, "movieAwards"));
        }
        List<Movie> listMoviesWithActorsLoad = movieBean.listMovies("javax.persistence.loadgraph", "movieWithActors");
        for (Movie movie : listMoviesWithActorsLoad) {
            // https://java.net/jira/browse/GLASSFISH-21200
            // Glassfish is not processing "javax.persistence.loadgraph".
            Assert.assertTrue(persistenceUnitUtil.isLoaded(movie, "movieActors"));
            Assert.assertFalse(movie.getMovieActors().isEmpty());
            for (MovieActor movieActor : movie.getMovieActors()) {
                Assert.assertFalse(persistenceUnitUtil.isLoaded(movieActor, "movieActorAwards"));
            }
            Assert.assertTrue(persistenceUnitUtil.isLoaded(movie, "movieDirectors"));
            Assert.assertFalse(persistenceUnitUtil.isLoaded(movie, "movieAwards"));
        }
    }

    @Test
    public void testEntityGraphMovieWithActorsAndAwards() throws Exception {
        PersistenceUnitUtil persistenceUnitUtil = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
        List<Movie> listMoviesWithActorsFetch = movieBean.listMovies("javax.persistence.fetchgraph", "movieWithActorsAndAwards");
        for (Movie movie : listMoviesWithActorsFetch) {
            Assert.assertTrue(persistenceUnitUtil.isLoaded(movie, "movieActors"));
            Assert.assertFalse(movie.getMovieActors().isEmpty());
            for (MovieActor movieActor : movie.getMovieActors()) {
                Assert.assertTrue(((persistenceUnitUtil.isLoaded(movieActor, "movieActorAwards")) || (!(persistenceUnitUtil.isLoaded(movieActor, "movieActorAwards")))));
            }
            // https://hibernate.atlassian.net/browse/HHH-8776
            // The specification states that by using fetchgraph, attributes should stay unloaded even if defined as
            // EAGER (movieDirectors), but specification also states that the persistence provider is allowed to fetch
            // additional state.
            Assert.assertTrue(((persistenceUnitUtil.isLoaded(movie, "movieDirectors")) || (!(persistenceUnitUtil.isLoaded(movie, "movieDirectors")))));
            Assert.assertFalse(persistenceUnitUtil.isLoaded(movie, "movieAwards"));
        }
        List<Movie> listMoviesWithActorsLoad = movieBean.listMovies("javax.persistence.loadgraph", "movieWithActorsAndAwards");
        for (Movie movie : listMoviesWithActorsLoad) {
            // https://java.net/jira/browse/GLASSFISH-21200
            // Glassfish is not processing "javax.persistence.loadgraph".
            Assert.assertTrue(persistenceUnitUtil.isLoaded(movie, "movieActors"));
            Assert.assertFalse(movie.getMovieActors().isEmpty());
            for (MovieActor movieActor : movie.getMovieActors()) {
                Assert.assertTrue(persistenceUnitUtil.isLoaded(movieActor, "movieActorAwards"));
            }
            Assert.assertTrue(persistenceUnitUtil.isLoaded(movie, "movieDirectors"));
            Assert.assertFalse(persistenceUnitUtil.isLoaded(movie, "movieAwards"));
        }
    }

    @Test
    public void testEntityGraphProgrammatically() throws Exception {
        PersistenceUnitUtil persistenceUnitUtil = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
        EntityGraph<Movie> fetchAll = entityManager.createEntityGraph(Movie.class);
        fetchAll.addSubgraph(movieActors);
        fetchAll.addSubgraph(movieDirectors);
        fetchAll.addSubgraph(movieAwards);
        List<Movie> moviesFetchAll = movieBean.listMovies("javax.persistence.fetchgraph", fetchAll);
        for (Movie movie : moviesFetchAll) {
            Assert.assertTrue(persistenceUnitUtil.isLoaded(movie, "movieActors"));
            Assert.assertTrue(persistenceUnitUtil.isLoaded(movie, "movieDirectors"));
            Assert.assertTrue(persistenceUnitUtil.isLoaded(movie, "movieAwards"));
        }
    }

    @Test
    public void testEntityGraphWithNamedParameters() throws Exception {
        PersistenceUnitUtil persistenceUnitUtil = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
        List<Movie> listMovieById = movieBean.listMoviesById(1, "javax.persistence.fetchgraph", "movieWithActors");
        Assert.assertFalse(listMovieById.isEmpty());
        Assert.assertEquals(1, listMovieById.size());
        for (Movie movie : listMovieById) {
            Assert.assertTrue(("The ID of the movie entity should have been 1 but was " + (movie.getId())), movie.getId().equals(1));
            Assert.assertTrue("Attribute movieActors of entity Movie should have been loaded, but was not", persistenceUnitUtil.isLoaded(movie, "movieActors"));
            Assert.assertFalse(movie.getMovieActors().isEmpty());
            for (MovieActor movieActor : movie.getMovieActors()) {
                Assert.assertFalse(persistenceUnitUtil.isLoaded(movieActor, "movieActorAwards"));
            }
            // https://hibernate.atlassian.net/browse/HHH-8776
            // The specification states that by using fetchgraph, attributes should stay unloaded even if defined as
            // EAGER (movieDirectors), but specification also states that the persistence provider is allowed to fetch
            // additional state.
            Assert.assertTrue(((persistenceUnitUtil.isLoaded(movie, "movieDirectors")) || (!(persistenceUnitUtil.isLoaded(movie, "movieDirectors")))));
            Assert.assertFalse(persistenceUnitUtil.isLoaded(movie, "movieAwards"));
        }
    }

    @Test
    public void testEntityGraphWithNamedParametersList() throws Exception {
        PersistenceUnitUtil persistenceUnitUtil = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
        // Hibernate fails mixing Entity Graphs and Named Parameters with "IN". Throws NPE
        List<Movie> listMoviesByIds = movieBean.listMoviesByIds(Arrays.asList(1, 2), "javax.persistence.fetchgraph", "movieWithActors");
        Assert.assertFalse(listMoviesByIds.isEmpty());
        Assert.assertEquals(2, listMoviesByIds.size());
        for (Movie movie : listMoviesByIds) {
            Assert.assertTrue(((movie.getId().equals(1)) || (movie.getId().equals(2))));
            Assert.assertTrue(persistenceUnitUtil.isLoaded(movie, "movieActors"));
            Assert.assertFalse(movie.getMovieActors().isEmpty());
            for (MovieActor movieActor : movie.getMovieActors()) {
                Assert.assertFalse(persistenceUnitUtil.isLoaded(movieActor, "movieActorAwards"));
            }
            // https://hibernate.atlassian.net/browse/HHH-8776
            // The specification states that by using fetchgraph, attributes should stay unloaded even if defined as
            // EAGER (movieDirectors), but specification also states that the persistence provider is allowed to fetch
            // additional state.
            Assert.assertTrue(((persistenceUnitUtil.isLoaded(movie, "movieDirectors")) || (!(persistenceUnitUtil.isLoaded(movie, "movieDirectors")))));
            Assert.assertFalse(persistenceUnitUtil.isLoaded(movie, "movieAwards"));
        }
    }
}

