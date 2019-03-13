package fj.test;


import fj.Ord;
import fj.data.List;
import org.junit.Test;


public final class GenTest {
    private static final List<Character> AS = List.list('A', 'B', 'C');

    @Test
    public void testCombinationOf_none() {
        Gen<List<Character>> instance = Gen.combinationOf(0, GenTest.AS);
        GenTest.testPick(100, instance, ( actual) -> assertTrue(actual.isEmpty()));
    }

    @Test
    public void testCombinationOf_one() {
        Gen<List<Character>> instance = Gen.combinationOf(1, GenTest.AS);
        GenTest.testPick(100, instance, ( actual) -> {
            assertEquals(1, actual.length());
            assertTrue(AS.exists(( a) -> a.equals(actual.head())));
        });
    }

    @Test
    public void testCombinationOf_two() {
        Gen<List<Character>> instance = Gen.combinationOf(2, GenTest.AS);
        GenTest.testPick(100, instance, ( actual) -> {
            assertEquals(2, actual.length());
            assertTrue(actual.forall(( actualA) -> AS.exists(( a) -> a.equals(actualA))));
            assertTrue(actual.tails().forall(( l) -> (l.isEmpty()) || (l.tail().forall(( a) -> Ord.charOrd.isGreaterThan(a, l.head())))));
        });
    }

    @Test
    public void testCombinationOf_three() {
        Gen<List<Character>> instance = Gen.combinationOf(3, GenTest.AS);
        GenTest.testPick(100, instance, ( actual) -> {
            assertEquals(3, actual.length());
            assertTrue(actual.forall(( actualA) -> AS.exists(( a) -> a.equals(actualA))));
            assertTrue(actual.tails().forall(( l) -> (l.isEmpty()) || (l.tail().forall(( a) -> Ord.charOrd.isGreaterThan(a, l.head())))));
        });
    }

    @Test
    public void testSelectionOf_none() {
        Gen<List<Character>> instance = Gen.selectionOf(0, GenTest.AS);
        GenTest.testPick(100, instance, ( actual) -> assertTrue(actual.isEmpty()));
    }

    @Test
    public void testSelectionOf_one() {
        Gen<List<Character>> instance = Gen.selectionOf(1, GenTest.AS);
        GenTest.testPick(100, instance, ( actual) -> {
            assertEquals(1, actual.length());
            assertTrue(AS.exists(( a) -> a.equals(actual.head())));
        });
    }

    @Test
    public void testSelectionOf_two() {
        Gen<List<Character>> instance = Gen.selectionOf(2, GenTest.AS);
        GenTest.testPick(100, instance, ( actual) -> {
            assertEquals(2, actual.length());
            assertTrue(actual.forall(( actualA) -> AS.exists(( a) -> a.equals(actualA))));
            assertTrue(actual.tails().forall(( l) -> (l.isEmpty()) || (l.tail().forall(( a) -> !(Ord.charOrd.isLessThan(a, l.head()))))));
        });
    }

    @Test
    public void testSelectionOf_three() {
        Gen<List<Character>> instance = Gen.selectionOf(3, GenTest.AS);
        GenTest.testPick(100, instance, ( actual) -> {
            assertEquals(3, actual.length());
            assertTrue(actual.forall(( actualA) -> AS.exists(( a) -> a.equals(actualA))));
            assertTrue(actual.tails().forall(( l) -> (l.isEmpty()) || (l.tail().forall(( a) -> !(Ord.charOrd.isLessThan(a, l.head()))))));
        });
    }

    @Test
    public void testSelectionOf_four() {
        Gen<List<Character>> instance = Gen.selectionOf(4, GenTest.AS);
        GenTest.testPick(100, instance, ( actual) -> {
            assertEquals(4, actual.length());
            assertTrue(actual.forall(( actualA) -> AS.exists(( a) -> a.equals(actualA))));
            assertTrue(actual.tails().forall(( l) -> (l.isEmpty()) || (l.tail().forall(( a) -> !(Ord.charOrd.isLessThan(a, l.head()))))));
        });
    }

    @Test
    public void testPermutationOf_none() {
        Gen<List<Character>> instance = Gen.permutationOf(0, GenTest.AS);
        GenTest.testPick(100, instance, ( actual) -> assertTrue(actual.isEmpty()));
    }

    @Test
    public void testPermutationOf_one() {
        Gen<List<Character>> instance = Gen.permutationOf(1, GenTest.AS);
        GenTest.testPick(100, instance, ( actual) -> {
            assertEquals(1, actual.length());
            assertTrue(AS.exists(( a) -> a.equals(actual.head())));
        });
    }

    @Test
    public void testPermutationOf_two() {
        Gen<List<Character>> instance = Gen.combinationOf(2, GenTest.AS);
        GenTest.testPick(100, instance, ( actual) -> {
            assertEquals(2, actual.length());
            assertTrue(actual.tails().forall(( l) -> (l.isEmpty()) || (l.tail().forall(( a) -> !(a.equals(l.head()))))));
        });
    }

    @Test
    public void testPermutationOf_three() {
        Gen<List<Character>> instance = Gen.permutationOf(3, GenTest.AS);
        GenTest.testPick(100, instance, ( actual) -> {
            assertEquals(3, actual.length());
            assertTrue(actual.forall(( actualA) -> AS.exists(( a) -> a.equals(actualA))));
            assertTrue(actual.tails().forall(( l) -> (l.isEmpty()) || (l.tail().forall(( a) -> !(a.equals(l.head()))))));
        });
    }

    @Test
    public void testWordOf_none() {
        Gen<List<Character>> instance = Gen.wordOf(0, GenTest.AS);
        GenTest.testPick(100, instance, ( actual) -> assertTrue(actual.isEmpty()));
    }

    @Test
    public void testWordOf_one() {
        Gen<List<Character>> instance = Gen.wordOf(1, GenTest.AS);
        GenTest.testPick(100, instance, ( actual) -> {
            assertEquals(1, actual.length());
            assertTrue(AS.exists(( a) -> a.equals(actual.head())));
        });
    }

    @Test
    public void testWordOf_two() {
        Gen<List<Character>> instance = Gen.wordOf(2, GenTest.AS);
        GenTest.testPick(100, instance, ( actual) -> {
            assertEquals(2, actual.length());
            assertTrue(actual.forall(( actualA) -> AS.exists(( a) -> a.equals(actualA))));
        });
    }

    @Test
    public void testWordOf_three() {
        Gen<List<Character>> instance = Gen.wordOf(3, GenTest.AS);
        GenTest.testPick(100, instance, ( actual) -> {
            assertEquals(3, actual.length());
            assertTrue(actual.forall(( actualA) -> AS.exists(( a) -> a.equals(actualA))));
        });
    }

    @Test
    public void testWordOf_four() {
        Gen<List<Character>> instance = Gen.wordOf(4, GenTest.AS);
        GenTest.testPick(100, instance, ( actual) -> {
            assertEquals(4, actual.length());
            assertTrue(actual.forall(( actualA) -> AS.exists(( a) -> a.equals(actualA))));
        });
    }
}

