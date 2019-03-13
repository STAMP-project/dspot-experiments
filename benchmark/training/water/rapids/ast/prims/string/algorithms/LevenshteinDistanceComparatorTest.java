package water.rapids.ast.prims.string.algorithms;


import org.junit.Assert;
import org.junit.Test;


public class LevenshteinDistanceComparatorTest {
    private static LevenshteinDistanceComparator levenshteinDistanceComparator = new LevenshteinDistanceComparator();

    @Test
    public void isTokenized() {
        Assert.assertTrue(LevenshteinDistanceComparatorTest.levenshteinDistanceComparator.isTokenized());
    }

    @Test
    public void compare() {
        final double stringDistance = LevenshteinDistanceComparatorTest.levenshteinDistanceComparator.compare("abcd", "abcd");
        Assert.assertEquals(1.0, stringDistance, 0.0);
    }

    @Test
    public void compareEmpty() {
        final double stringDistance = LevenshteinDistanceComparatorTest.levenshteinDistanceComparator.compare("", "");
        Assert.assertEquals(1.0, stringDistance, 0.0);
    }

    @Test
    public void compareLeftEmpty() {
        final double stringDistance = LevenshteinDistanceComparatorTest.levenshteinDistanceComparator.compare("", "abcd");
        Assert.assertEquals(0.0, stringDistance, 0.0);
    }

    @Test
    public void compareRightEmpty() {
        final double stringDistance = LevenshteinDistanceComparatorTest.levenshteinDistanceComparator.compare("abcd", "");
        Assert.assertEquals(0.0, stringDistance, 0.0);
    }

    @Test
    public void compareCaseSensitive() {
        final double stringDistance = LevenshteinDistanceComparatorTest.levenshteinDistanceComparator.compare("ABcd", "abcd");
        Assert.assertEquals(0.5, stringDistance, 0.0);
    }

    @Test
    public void compareLongDistance() {
        final double stringDistance = LevenshteinDistanceComparatorTest.levenshteinDistanceComparator.compare("bAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "baaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        Assert.assertEquals(0.03, stringDistance, 0.001);
    }

    @Test
    public void compareLongDistanceNoCommon() {
        final double stringDistance = LevenshteinDistanceComparatorTest.levenshteinDistanceComparator.compare("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        Assert.assertEquals(0.0, stringDistance, 0.0);
    }

    /**
     * Check no transpositions are taken into account when the distance is computed, e.g. Damerau-Levenshtein behavior.
     */
    @Test
    public void compareTranspositions() {
        final double stringDistance = LevenshteinDistanceComparatorTest.levenshteinDistanceComparator.compare("h2o2", "ho22");
        Assert.assertEquals(0.5, stringDistance, 0.0);
    }
}

