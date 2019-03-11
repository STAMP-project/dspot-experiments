package aima.test.core.unit.learning.learners;


import aima.core.learning.framework.DataSet;
import aima.core.learning.framework.DataSetFactory;
import aima.core.learning.inductive.DecisionTree;
import aima.core.learning.learners.DecisionTreeLearner;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Ravi Mohan
 */
public class DecisionTreeTest {
    private static final String YES = "Yes";

    private static final String NO = "No";

    @Test
    public void testActualDecisionTreeClassifiesRestaurantDataSetCorrectly() throws Exception {
        DecisionTreeLearner learner = new DecisionTreeLearner(DecisionTreeTest.createActualRestaurantDecisionTree(), "Unable to clasify");
        int[] results = learner.test(DataSetFactory.getRestaurantDataSet());
        Assert.assertEquals(12, results[0]);
        Assert.assertEquals(0, results[1]);
    }

    @Test
    public void testInducedDecisionTreeClassifiesRestaurantDataSetCorrectly() throws Exception {
        DecisionTreeLearner learner = new DecisionTreeLearner(DecisionTreeTest.createInducedRestaurantDecisionTree(), "Unable to clasify");
        int[] results = learner.test(DataSetFactory.getRestaurantDataSet());
        Assert.assertEquals(12, results[0]);
        Assert.assertEquals(0, results[1]);
    }

    @Test
    public void testStumpCreationForSpecifiedAttributeValuePair() throws Exception {
        DataSet ds = DataSetFactory.getRestaurantDataSet();
        List<String> unmatchedValues = new ArrayList<String>();
        unmatchedValues.add(DecisionTreeTest.NO);
        DecisionTree dt = DecisionTree.getStumpFor(ds, "alternate", DecisionTreeTest.YES, DecisionTreeTest.YES, unmatchedValues, DecisionTreeTest.NO);
        Assert.assertNotNull(dt);
    }

    @Test
    public void testStumpCreationForDataSet() throws Exception {
        DataSet ds = DataSetFactory.getRestaurantDataSet();
        List<DecisionTree> dt = DecisionTree.getStumpsFor(ds, DecisionTreeTest.YES, "Unable to classify");
        Assert.assertEquals(26, dt.size());
    }

    @Test
    public void testStumpPredictionForDataSet() throws Exception {
        DataSet ds = DataSetFactory.getRestaurantDataSet();
        List<String> unmatchedValues = new ArrayList<String>();
        unmatchedValues.add(DecisionTreeTest.NO);
        DecisionTree tree = DecisionTree.getStumpFor(ds, "hungry", DecisionTreeTest.YES, DecisionTreeTest.YES, unmatchedValues, "Unable to Classify");
        DecisionTreeLearner learner = new DecisionTreeLearner(tree, "Unable to Classify");
        int[] result = learner.test(ds);
        Assert.assertEquals(5, result[0]);
        Assert.assertEquals(7, result[1]);
    }
}

