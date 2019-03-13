package com.baeldung.server;


import Response.Status.CREATED;
import Response.Status.OK;
import com.baeldung.client.ServicesInterface;
import com.baeldung.model.Movie;
import java.util.List;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;
import org.junit.Test;


public class RestEasyClientLiveTest {
    public static final UriBuilder FULL_PATH = UriBuilder.fromPath("http://127.0.0.1:8082/RestEasyTutorial/rest");

    Movie transformerMovie = null;

    Movie batmanMovie = null;

    ObjectMapper jsonMapper = null;

    @Test
    public void testListAllMovies() {
        final ResteasyClient client = new ResteasyClientBuilder().build();
        final ResteasyWebTarget target = client.target(RestEasyClientLiveTest.FULL_PATH);
        final ServicesInterface proxy = target.proxy(ServicesInterface.class);
        Response moviesResponse = proxy.addMovie(transformerMovie);
        moviesResponse.close();
        moviesResponse = proxy.addMovie(batmanMovie);
        moviesResponse.close();
        final List<Movie> movies = proxy.listMovies();
        System.out.println(movies);
    }

    @Test
    public void testMovieByImdbId() {
        final String transformerImdbId = "tt0418279";
        final ResteasyClient client = new ResteasyClientBuilder().build();
        final ResteasyWebTarget target = client.target(RestEasyClientLiveTest.FULL_PATH);
        final ServicesInterface proxy = target.proxy(ServicesInterface.class);
        final Response moviesResponse = proxy.addMovie(transformerMovie);
        moviesResponse.close();
        final Movie movies = proxy.movieByImdbId(transformerImdbId);
        System.out.println(movies);
    }

    @Test
    public void testAddMovie() {
        final ResteasyClient client = new ResteasyClientBuilder().build();
        final ResteasyWebTarget target = client.target(RestEasyClientLiveTest.FULL_PATH);
        final ServicesInterface proxy = target.proxy(ServicesInterface.class);
        Response moviesResponse = proxy.addMovie(batmanMovie);
        moviesResponse.close();
        moviesResponse = proxy.addMovie(transformerMovie);
        if ((moviesResponse.getStatus()) != (CREATED.getStatusCode())) {
            System.out.println(("Failed : HTTP error code : " + (moviesResponse.getStatus())));
        }
        moviesResponse.close();
        System.out.println(("Response Code: " + (moviesResponse.getStatus())));
    }

    @Test
    public void testAddMovieMultiConnection() {
        final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        final CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();
        final ApacheHttpClient4Engine engine = new ApacheHttpClient4Engine(httpClient);
        final ResteasyClient client = new ResteasyClientBuilder().httpEngine(engine).build();
        final ResteasyWebTarget target = client.target(RestEasyClientLiveTest.FULL_PATH);
        final ServicesInterface proxy = target.proxy(ServicesInterface.class);
        final Response batmanResponse = proxy.addMovie(batmanMovie);
        final Response transformerResponse = proxy.addMovie(transformerMovie);
        if ((batmanResponse.getStatus()) != (CREATED.getStatusCode())) {
            System.out.println(("Batman Movie creation Failed : HTTP error code : " + (batmanResponse.getStatus())));
        }
        if ((batmanResponse.getStatus()) != (CREATED.getStatusCode())) {
            System.out.println(("Batman Movie creation Failed : HTTP error code : " + (batmanResponse.getStatus())));
        }
        batmanResponse.close();
        transformerResponse.close();
        cm.close();
    }

    @Test
    public void testDeleteMovie() {
        final ResteasyClient client = new ResteasyClientBuilder().build();
        final ResteasyWebTarget target = client.target(RestEasyClientLiveTest.FULL_PATH);
        final ServicesInterface proxy = target.proxy(ServicesInterface.class);
        Response moviesResponse = proxy.addMovie(batmanMovie);
        moviesResponse.close();
        moviesResponse = proxy.deleteMovie(batmanMovie.getImdbId());
        if ((moviesResponse.getStatus()) != (OK.getStatusCode())) {
            System.out.println(moviesResponse.readEntity(String.class));
            throw new RuntimeException(("Failed : HTTP error code : " + (moviesResponse.getStatus())));
        }
        moviesResponse.close();
        System.out.println(("Response Code: " + (moviesResponse.getStatus())));
    }

    @Test
    public void testUpdateMovie() {
        final ResteasyClient client = new ResteasyClientBuilder().build();
        final ResteasyWebTarget target = client.target(RestEasyClientLiveTest.FULL_PATH);
        final ServicesInterface proxy = target.proxy(ServicesInterface.class);
        Response moviesResponse = proxy.addMovie(batmanMovie);
        moviesResponse.close();
        batmanMovie.setTitle("Batman Begins");
        moviesResponse = proxy.updateMovie(batmanMovie);
        if ((moviesResponse.getStatus()) != (OK.getStatusCode())) {
            System.out.println(("Failed : HTTP error code : " + (moviesResponse.getStatus())));
        }
        moviesResponse.close();
        System.out.println(("Response Code: " + (moviesResponse.getStatus())));
    }
}

