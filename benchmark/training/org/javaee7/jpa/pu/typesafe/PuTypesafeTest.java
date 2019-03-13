package org.javaee7.jpa.pu.typesafe;


import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
public class PuTypesafeTest {
    @Inject
    private MySessionBean bean;

    @Inject
    @DefaultDatabase
    private EntityManager defaultEM;

    @PersistenceContext(name = "defaultPU")
    private EntityManager persistenceContextEM;

    @Test
    public void testPuTypesafe() throws Exception {
        List<Movie> movies = bean.listMovies();
        Assert.assertFalse(movies.isEmpty());
        Assert.assertNotNull(defaultEM);
        List<Movie> defaultFindAll = defaultEM.createNamedQuery("Movie.findAll", Movie.class).getResultList();
        Assert.assertFalse(defaultFindAll.isEmpty());
        Assert.assertArrayEquals(movies.toArray(), defaultFindAll.toArray());
        List<Movie> persistenceContextFindAll = persistenceContextEM.createNamedQuery("Movie.findAll", Movie.class).getResultList();
        Assert.assertArrayEquals(movies.toArray(), persistenceContextFindAll.toArray());
    }
}

