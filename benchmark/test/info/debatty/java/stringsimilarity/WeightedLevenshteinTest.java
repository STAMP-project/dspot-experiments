package info.debatty.java.stringsimilarity;


import info.debatty.java.stringsimilarity.testutil.NullEmptyTests;
import org.junit.Assert;
import org.junit.Test;


public class WeightedLevenshteinTest {
    @Test
    public void testDistance() {
        WeightedLevenshtein instance = new WeightedLevenshtein(new CharacterSubstitutionInterface() {
            public double cost(char c1, char c2) {
                // The cost for substituting 't' and 'r' is considered
                // smaller as these 2 are located next to each other
                // on a keyboard
                if ((c1 == 't') && (c2 == 'r')) {
                    return 0.5;
                }
                // For most cases, the cost of substituting 2 characters
                // is 1.0
                return 1.0;
            }
        });
        Assert.assertEquals(0.0, instance.distance("String1", "String1"), 0.1);
        Assert.assertEquals(0.5, instance.distance("String1", "Srring1"), 0.1);
        Assert.assertEquals(1.5, instance.distance("String1", "Srring2"), 0.1);
        // One insert or delete.
        Assert.assertEquals(1.0, instance.distance("Strng", "String"), 0.1);
        Assert.assertEquals(1.0, instance.distance("String", "Strng"), 0.1);
        // With limits.
        Assert.assertEquals(0.0, instance.distance("String1", "String1", Double.MAX_VALUE), 0.1);
        Assert.assertEquals(0.0, instance.distance("String1", "String1", 2.0), 0.1);
        Assert.assertEquals(1.5, instance.distance("String1", "Srring2", Double.MAX_VALUE), 0.1);
        Assert.assertEquals(1.5, instance.distance("String1", "Srring2", 2.0), 0.1);
        Assert.assertEquals(1.5, instance.distance("String1", "Srring2", 1.5), 0.1);
        Assert.assertEquals(1.0, instance.distance("String1", "Srring2", 1.0), 0.1);
        Assert.assertEquals(4.0, instance.distance("String1", "Potato", 4.0), 0.1);
        NullEmptyTests.testDistance(instance);
    }

    @Test
    public void testDistanceCharacterInsDelInterface() {
        WeightedLevenshtein instance = new WeightedLevenshtein(new CharacterSubstitutionInterface() {
            public double cost(char c1, char c2) {
                if ((c1 == 't') && (c2 == 'r')) {
                    return 0.5;
                }
                return 1.0;
            }
        }, new CharacterInsDelInterface() {
            public double deletionCost(char c) {
                if (c == 'i') {
                    return 0.8;
                }
                return 1.0;
            }

            public double insertionCost(char c) {
                if (c == 'i') {
                    return 0.5;
                }
                return 1.0;
            }
        });
        // Same as testDistance above.
        Assert.assertEquals(0.0, instance.distance("String1", "String1"), 0.1);
        Assert.assertEquals(0.5, instance.distance("String1", "Srring1"), 0.1);
        Assert.assertEquals(1.5, instance.distance("String1", "Srring2"), 0.1);
        // Cost of insert of 'i' is less than normal, so these scores are
        // different than testDistance above.  Note that the cost of delete
        // has been set differently than the cost of insert, so the distance
        // call is not symmetric in its arguments if an 'i' has changed.
        Assert.assertEquals(0.5, instance.distance("Strng", "String"), 0.1);
        Assert.assertEquals(0.8, instance.distance("String", "Strng"), 0.1);
        Assert.assertEquals(1.0, instance.distance("Strig", "String"), 0.1);
        Assert.assertEquals(1.0, instance.distance("String", "Strig"), 0.1);
        // Same as above with limits.
        Assert.assertEquals(0.0, instance.distance("String1", "String1", Double.MAX_VALUE), 0.1);
        Assert.assertEquals(0.0, instance.distance("String1", "String1", 2.0), 0.1);
        Assert.assertEquals(0.5, instance.distance("String1", "Srring1", Double.MAX_VALUE), 0.1);
        Assert.assertEquals(0.5, instance.distance("String1", "Srring1", 2.0), 0.1);
        Assert.assertEquals(1.5, instance.distance("String1", "Srring2", 2.0), 0.1);
        Assert.assertEquals(1.5, instance.distance("String1", "Srring2", 1.5), 0.1);
        Assert.assertEquals(1.0, instance.distance("String1", "Srring2", 1.0), 0.1);
        Assert.assertEquals(4.0, instance.distance("String1", "Potato", 4.0), 0.1);
        NullEmptyTests.testDistance(instance);
    }
}

