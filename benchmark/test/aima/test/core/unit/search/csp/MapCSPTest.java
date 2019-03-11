package aima.test.core.unit.search.csp;


import MapCSP.BLUE;
import MapCSP.GREEN;
import MapCSP.NSW;
import MapCSP.NT;
import MapCSP.Q;
import MapCSP.RED;
import MapCSP.SA;
import MapCSP.T;
import MapCSP.V;
import MapCSP.WA;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Ravi Mohan
 * @author Ruediger Lunde
 */
public class MapCSPTest {
    private CSP<Variable, String> csp;

    @Test
    public void testBackTrackingSearch() {
        Optional<Assignment<Variable, String>> results = new FlexibleBacktrackingSolver<Variable, String>().solve(csp);
        Assert.assertTrue(results.isPresent());
        Assert.assertEquals(GREEN, results.get().getValue(WA));
        Assert.assertEquals(RED, results.get().getValue(NT));
        Assert.assertEquals(BLUE, results.get().getValue(SA));
        Assert.assertEquals(GREEN, results.get().getValue(Q));
        Assert.assertEquals(RED, results.get().getValue(NSW));
        Assert.assertEquals(GREEN, results.get().getValue(V));
        Assert.assertEquals(RED, results.get().getValue(T));
    }

    @Test
    public void testMCSearch() {
        new MinConflictsSolver<Variable, String>(100).solve(csp);
    }
}

