package org.jabref.model.entry.identifier;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ISBNTest {
    @Test
    public void testIsValidFormat10Correct() {
        Assertions.assertTrue(new ISBN("0-123456-47-9").isValidFormat());
        Assertions.assertTrue(new ISBN("0-9752298-0-X").isValidFormat());
    }

    @Test
    public void testIsValidFormat10Incorrect() {
        Assertions.assertFalse(new ISBN("0-12B456-47-9").isValidFormat());
    }

    @Test
    public void testIsValidChecksum10Correct() {
        Assertions.assertTrue(new ISBN("0-123456-47-9").isValidChecksum());
        Assertions.assertTrue(new ISBN("0-9752298-0-X").isValidChecksum());
        Assertions.assertTrue(new ISBN("0-9752298-0-x").isValidChecksum());
    }

    @Test
    public void testIsValidChecksum10Incorrect() {
        Assertions.assertFalse(new ISBN("0-123456-47-8").isValidChecksum());
    }

    @Test
    public void testIsValidFormat13Correct() {
        Assertions.assertTrue(new ISBN("978-1-56619-909-4").isValidFormat());
    }

    @Test
    public void testIsValidFormat13Incorrect() {
        Assertions.assertFalse(new ISBN("978-1-56619-9O9-4 ").isValidFormat());
    }

    @Test
    public void testIsValidChecksum13Correct() {
        Assertions.assertTrue(new ISBN("978-1-56619-909-4 ").isValidChecksum());
    }

    @Test
    public void testIsValidChecksum13Incorrect() {
        Assertions.assertFalse(new ISBN("978-1-56619-909-5").isValidChecksum());
    }

    @Test
    public void testIsIsbn10Correct() {
        Assertions.assertTrue(new ISBN("0-123456-47-9").isIsbn10());
        Assertions.assertTrue(new ISBN("0-9752298-0-X").isIsbn10());
    }

    @Test
    public void testIsIsbn10Incorrect() {
        Assertions.assertFalse(new ISBN("978-1-56619-909-4").isIsbn10());
    }

    @Test
    public void testIsIsbn13Correct() {
        Assertions.assertTrue(new ISBN("978-1-56619-909-4").isIsbn13());
    }

    @Test
    public void testIsIsbn13Incorrect() {
        Assertions.assertFalse(new ISBN("0-123456-47-9").isIsbn13());
    }
}

