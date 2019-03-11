package org.jabref.logic.protectedterms;


import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ProtectedTermsListTest {
    private ProtectedTermsList internalList;

    private ProtectedTermsList externalList;

    @Test
    public void testProtectedTermsListStringListOfStringStringBoolean() {
        Assertions.assertTrue(internalList.isInternalList());
    }

    @Test
    public void testProtectedTermsListStringListOfStringString() {
        Assertions.assertFalse(externalList.isInternalList());
    }

    @Test
    public void testGetDescription() {
        Assertions.assertEquals("Name", internalList.getDescription());
    }

    @Test
    public void testGetTermList() {
        Assertions.assertEquals(Arrays.asList("AAA", "BBB"), internalList.getTermList());
    }

    @Test
    public void testGetLocation() {
        Assertions.assertEquals("location", internalList.getLocation());
    }

    @Test
    public void testGetTermListing() {
        Assertions.assertEquals("AAA\nBBB", internalList.getTermListing());
    }

    @Test
    public void testCompareTo() {
        Assertions.assertEquals((-2), internalList.compareTo(externalList));
    }

    @Test
    public void testSetEnabledIsEnabled() {
        Assertions.assertFalse(internalList.isEnabled());
        internalList.setEnabled(true);
        Assertions.assertTrue(internalList.isEnabled());
    }

    @Test
    public void testNotEnabledByDefault() {
        Assertions.assertFalse(internalList.isEnabled());
    }

    @Test
    public void testCanNotAddTermToInternalList() {
        Assertions.assertFalse(internalList.addProtectedTerm("CCC"));
    }

    @Test
    public void testTermNotAddedToInternalList() {
        internalList.addProtectedTerm("CCC");
        Assertions.assertFalse(internalList.getTermList().contains("CCC"));
    }

    @Test
    public void testCanAddTermToExternalList() {
        Assertions.assertTrue(externalList.addProtectedTerm("CCC"));
    }

    @Test
    public void testTermAddedToExternalList() {
        externalList.addProtectedTerm("CCC");
        Assertions.assertTrue(externalList.getTermList().contains("CCC"));
    }
}

