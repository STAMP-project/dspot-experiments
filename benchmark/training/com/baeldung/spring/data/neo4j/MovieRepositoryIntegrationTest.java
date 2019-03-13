package com.baeldung.spring.data.neo4j;


import com.baeldung.spring.data.neo4j.config.MovieDatabaseNeo4jTestConfiguration;
import com.baeldung.spring.data.neo4j.domain.Movie;
import com.baeldung.spring.data.neo4j.repository.MovieRepository;
import com.baeldung.spring.data.neo4j.repository.PersonRepository;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MovieDatabaseNeo4jTestConfiguration.class)
@ActiveProfiles(profiles = "test")
public class MovieRepositoryIntegrationTest {
    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private PersonRepository personRepository;

    public MovieRepositoryIntegrationTest() {
    }

    @Test
    @DirtiesContext
    public void testFindByTitle() {
        System.out.println("findByTitle");
        String title = "The Italian Job";
        Movie result = movieRepository.findByTitle(title);
        Assert.assertNotNull(result);
        Assert.assertEquals(1999, result.getReleased());
    }

    @Test
    @DirtiesContext
    public void testCount() {
        System.out.println("count");
        long movieCount = movieRepository.count();
        Assert.assertNotNull(movieCount);
        Assert.assertEquals(1, movieCount);
    }

    @Test
    @DirtiesContext
    public void testFindAll() {
        System.out.println("findAll");
        Collection<Movie> result = ((Collection<Movie>) (movieRepository.findAll()));
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
    }

    @Test
    @DirtiesContext
    public void testFindByTitleContaining() {
        System.out.println("findByTitleContaining");
        String title = "Italian";
        Collection<Movie> result = movieRepository.findByTitleContaining(title);
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
    }

    @Test
    @DirtiesContext
    public void testGraph() {
        System.out.println("graph");
        List<Map<String, Object>> graph = movieRepository.graph(5);
        Assert.assertEquals(1, graph.size());
        Map<String, Object> map = graph.get(0);
        Assert.assertEquals(2, map.size());
        String[] cast = ((String[]) (map.get("cast")));
        String movie = ((String) (map.get("movie")));
        Assert.assertEquals("The Italian Job", movie);
        Assert.assertEquals("Mark Wahlberg", cast[0]);
    }

    @Test
    @DirtiesContext
    public void testDeleteMovie() {
        System.out.println("deleteMovie");
        movieRepository.delete(movieRepository.findByTitle("The Italian Job"));
        TestCase.assertNull(movieRepository.findByTitle("The Italian Job"));
    }

    @Test
    @DirtiesContext
    public void testDeleteAll() {
        System.out.println("deleteAll");
        movieRepository.deleteAll();
        Collection<Movie> result = ((Collection<Movie>) (movieRepository.findAll()));
        Assert.assertEquals(0, result.size());
    }
}

