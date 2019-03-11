package aima.test.core.unit.logic.planning;


import aima.core.logic.planning.Graph;
import aima.core.logic.planning.Level;
import aima.core.logic.planning.Problem;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author samagra
 */
public class GraphTest {
    Problem problem;

    Level firstLevel;

    Level secondLevel;

    Level thirdLevel;

    @Test
    public void addLevelTest() {
        Graph graph = new Graph(problem, firstLevel);
        graph.addLevel();
        Assert.assertEquals(2, graph.getLevels().size());
        graph.addLevel();
        Assert.assertEquals(3, graph.getLevels().size());
    }
}

