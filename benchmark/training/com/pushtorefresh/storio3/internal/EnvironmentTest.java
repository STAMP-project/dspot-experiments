package com.pushtorefresh.storio3.internal;


import com.pushtorefresh.private_constructor_checker.PrivateConstructorChecker;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.junit.Test;


public class EnvironmentTest {
    @Test
    public void constructorShouldBePrivateAndThrowException() {
        PrivateConstructorChecker.forClass(Environment.class).expectedTypeOfException(IllegalStateException.class).expectedExceptionMessage("No instances please").check();
    }

    @Test
    public void rxJavaShouldBeInClassPath() {
        assertThat(Environment.RX_JAVA_2_IS_IN_THE_CLASS_PATH).isTrue();
    }

    @Test
    public void shouldThrowExceptionIfRxJavaIsNotInTheClassPath() throws IllegalAccessException, NoSuchFieldException {
        Field field = Environment.class.getDeclaredField("RX_JAVA_2_IS_IN_THE_CLASS_PATH");
        field.setAccessible(true);
        // Removing FINAL modifier
        Field modifiersFieldOfTheField = Field.class.getDeclaredField("modifiers");
        modifiersFieldOfTheField.setAccessible(true);
        modifiersFieldOfTheField.setInt(field, ((field.getModifiers()) & (~(Modifier.FINAL))));
        final Object prevValue = field.get(null);
        field.set(null, false);// No Environment will think that RxJava is not in the ClassPath

        try {
            Environment.throwExceptionIfRxJava2IsNotAvailable("yolo");
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException expected) {
            assertThat(expected).hasMessage(("yolo requires RxJava2 in classpath," + " please add it as compile dependency to the application"));
        } finally {
            // Return previous value of the field
            field.set(null, prevValue);
            // Restoring FINAL modifier (for better tests performance)
            modifiersFieldOfTheField.setInt(field, ((field.getModifiers()) & (Modifier.FINAL)));
        }
    }

    @Test
    public void shouldNotThrowExceptionIfRxJavaIsInTheClassPath() {
        // Because RxJava should be in the ClassPath for tests
        Environment.throwExceptionIfRxJava2IsNotAvailable("no exceptions please");
    }
}

