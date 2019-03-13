package org.javaee7.jpa.storedprocedure;


import java.util.List;
import javax.inject.Inject;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Roberto Cortez
 */
@RunWith(Arquillian.class)
public class StoredProcedureTest {
    @Inject
    private MovieBean movieBean;

    @Test
    public void testStoredProcedure() throws Exception {
        List<Movie> movies = movieBean.listMovies();
        Assert.assertFalse(movies.isEmpty());
        // movieBean.executeStoredProcedure();
    }
}

