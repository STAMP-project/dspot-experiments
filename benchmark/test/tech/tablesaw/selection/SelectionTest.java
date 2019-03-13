package tech.tablesaw.selection;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class SelectionTest {
    @Test
    public void with() {
        Selection selection = Selection.with(42, 53, 111);
        Assertions.assertTrue(selection.contains(42));
        Assertions.assertTrue(selection.contains(53));
        Assertions.assertTrue(selection.contains(111));
        Assertions.assertFalse(selection.contains(43));
        Assertions.assertFalse(selection.contains(0));
        Assertions.assertFalse(selection.contains(122));
    }

    @Test
    public void withoutRange() {
        Selection selection = Selection.withoutRange(0, 130, 42, 53);
        Assertions.assertFalse(selection.contains(42));
        Assertions.assertFalse(selection.contains(43));
        Assertions.assertFalse(selection.contains(52));
        Assertions.assertTrue(selection.contains(53));
        Assertions.assertTrue(selection.contains(111));
        Assertions.assertTrue(selection.contains(0));
        Assertions.assertTrue(selection.contains(122));
    }

    @Test
    public void withRange() {
        Selection selection = Selection.withRange(42, 53);
        Assertions.assertTrue(selection.contains(42));
        Assertions.assertTrue(selection.contains(43));
        Assertions.assertTrue(selection.contains(52));
        Assertions.assertFalse(selection.contains(53));
        Assertions.assertFalse(selection.contains(111));
        Assertions.assertFalse(selection.contains(0));
        Assertions.assertFalse(selection.contains(122));
    }

    @Test
    public void toArray() {
        Selection selection = Selection.with(42, 53, 111);
        int[] expected = new int[]{ 42, 53, 111 };
        Assertions.assertArrayEquals(expected, selection.toArray());
    }

    @Test
    public void add() {
        Selection selection = Selection.with(42, 53, 111);
        Assertions.assertTrue(selection.contains(42));
        Assertions.assertFalse(selection.contains(43));
        Assertions.assertTrue(selection.add(43).contains(43));
    }

    @Test
    public void addRange() {
        Selection selection = Selection.with(42, 53, 111);
        Assertions.assertTrue(selection.contains(42));
        Assertions.assertFalse(selection.contains(43));
        Assertions.assertTrue(selection.addRange(70, 80).contains(73));
        Assertions.assertTrue(selection.addRange(70, 80).contains(70));
        Assertions.assertTrue(selection.addRange(70, 80).contains(79));
        Assertions.assertFalse(selection.addRange(70, 80).contains(80));
    }

    @Test
    public void size() {
        Selection selection = Selection.with(42, 53, 111);
        Assertions.assertEquals(3, selection.size());
    }

    @Test
    public void and() {
        Selection selection = Selection.with(42, 53, 111);
        Selection selection2 = Selection.with(11, 133, 53, 112);
        Selection selection3 = selection.and(selection2);
        Assertions.assertEquals(1, selection3.size());
        Assertions.assertEquals(53, selection3.get(0));
    }

    @Test
    public void or() {
        Selection selection = Selection.with(42, 53, 111);
        Selection selection2 = Selection.with(11, 133, 53, 112);
        Selection selection3 = selection.or(selection2);
        Assertions.assertEquals(6, selection3.size());
        Assertions.assertEquals(11, selection3.get(0));
        Assertions.assertEquals(42, selection3.get(1));
        Assertions.assertTrue(selection3.contains(53));
    }

    @Test
    public void andNot() {
        Selection selection = Selection.with(42, 53, 111);
        Selection selection2 = Selection.with(11, 133, 53, 112);
        Selection selection3 = selection.andNot(selection2);
        Assertions.assertEquals(2, selection3.size());
        Assertions.assertEquals(111, selection3.get(1));
        Assertions.assertEquals(42, selection3.get(0));
        Assertions.assertFalse(selection3.contains(53));
    }

    @Test
    public void isEmpty() {
        Selection selection = Selection.with();
        Assertions.assertTrue(selection.isEmpty());
        Selection selection1 = Selection.with(42, 53, 111);
        Assertions.assertFalse(selection1.isEmpty());
    }

    @Test
    public void clear() {
        Selection selection1 = Selection.with(42, 53, 111);
        Assertions.assertFalse(selection1.isEmpty());
        selection1.clear();
        Assertions.assertTrue(selection1.isEmpty());
    }

    @Test
    public void get() {
        Selection selection = Selection.with(42, 53, 111);
        Assertions.assertEquals(42, selection.get(0));
        Assertions.assertEquals(53, selection.get(1));
    }

    @Test
    public void remove() {
        Selection selection = Selection.with(42, 53, 111);
        Assertions.assertTrue(selection.contains(53));
        selection = selection.removeRange(50, 69);
        Assertions.assertFalse(selection.contains(53));
        Assertions.assertTrue(selection.contains(111));
    }

    @Test
    public void flip() {
        Selection selection = Selection.with(42, 53, 111);
        Assertions.assertTrue(selection.contains(53));
        Assertions.assertTrue(selection.contains(42));
        Assertions.assertTrue(selection.contains(111));
        selection = selection.flip(0, 124);
        Assertions.assertFalse(selection.contains(53));
        Assertions.assertFalse(selection.contains(42));
        Assertions.assertFalse(selection.contains(111));
        Assertions.assertTrue(selection.contains(0));
        Assertions.assertTrue(selection.contains(110));
        Assertions.assertTrue(selection.contains(112));
    }
}

