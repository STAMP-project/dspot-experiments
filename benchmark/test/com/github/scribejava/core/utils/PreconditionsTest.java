package com.github.scribejava.core.utils;


import org.junit.Test;


public class PreconditionsTest {
    private static final String ERROR_MSG = "";

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForNullObjects() {
        Preconditions.checkNotNull(null, PreconditionsTest.ERROR_MSG);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForNullStrings() {
        Preconditions.checkEmptyString(null, PreconditionsTest.ERROR_MSG);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForEmptyStrings() {
        Preconditions.checkEmptyString("", PreconditionsTest.ERROR_MSG);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForSpacesOnlyStrings() {
        Preconditions.checkEmptyString("               ", PreconditionsTest.ERROR_MSG);
    }
}

