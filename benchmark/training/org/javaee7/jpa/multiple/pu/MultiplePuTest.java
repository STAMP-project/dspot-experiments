package org.javaee7.jpa.multiple.pu;


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
public class MultiplePuTest {
    @Inject
    private MultiplePuBean bean;

    @Test
    public void testMultiplePu() throws Exception {
        List<Movie> movies = bean.listMovies();
        Assert.assertFalse(movies.isEmpty());
        List<ProductCode> productCodes = bean.listProductCode();
        Assert.assertTrue(productCodes.isEmpty());
    }
}

