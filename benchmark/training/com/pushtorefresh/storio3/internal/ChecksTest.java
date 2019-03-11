package com.pushtorefresh.storio3.internal;


import com.pushtorefresh.private_constructor_checker.PrivateConstructorChecker;
import org.junit.Test;


public class ChecksTest {
    @Test
    public void constructorShouldBePrivateAndThrowException() {
        PrivateConstructorChecker.forClass(Checks.class).expectedTypeOfException(IllegalStateException.class).expectedExceptionMessage("No instances please.").check();
    }

    @Test
    public void checkNotNullPositive() {
        Checks.checkNotNull(new Object(), "No exceptions please");
    }

    @Test(expected = NullPointerException.class)
    public void checkNotNullNegative() {
        Checks.checkNotNull(null, "Throw me!");
    }

    @Test
    public void checkNotNullExceptionMessage() {
        try {
            Checks.checkNotNull(null, "expected message");
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException e) {
            assertThat(e).hasMessage("expected message");
        }
    }

    @Test
    public void checkNotEmptyPositive() {
        Checks.checkNotEmpty("Not empty string", "No exceptions please");
    }

    @Test(expected = NullPointerException.class)
    public void checkNotEmptyNullNegative() {
        Checks.checkNotEmpty(null, "Throw me!");
    }

    @Test(expected = IllegalStateException.class)
    public void checkNotEmptyEmptyNegative() {
        Checks.checkNotEmpty("", "Throw me!");
    }

    @Test
    public void checkNotEmptyExceptionMessage() {
        try {
            Checks.checkNotEmpty(null, "expected message");
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException e) {
            assertThat(e).hasMessage("expected message");
        }
    }

    @Test
    public void checkNotEmptyEmptyExceptionMessage() {
        try {
            Checks.checkNotEmpty("", "expected message");
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage("expected message");
        }
    }
}

