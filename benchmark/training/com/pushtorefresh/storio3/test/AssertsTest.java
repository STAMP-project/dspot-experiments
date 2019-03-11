package com.pushtorefresh.storio3.test;


import com.pushtorefresh.private_constructor_checker.PrivateConstructorChecker;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class AssertsTest {
    @Test
    public void constructorMustBePrivateAndThrowException() {
        PrivateConstructorChecker.forClass(Asserts.class).expectedTypeOfException(IllegalStateException.class).expectedExceptionMessage("No instances please.").check();
    }

    @Test
    public void shouldAssertThatListIsImmutable() {
        // Should not throw exception
        Asserts.assertThatListIsImmutable(Collections.unmodifiableList(new ArrayList<Object>()));
    }

    @Test
    public void shouldAssetThatEmptyListIsImmutable() {
        // Should not throw exception
        Asserts.assertThatListIsImmutable(Collections.EMPTY_LIST);
    }

    @Test(expected = AssertionError.class)
    public void shouldNotAssertThatListIsImmutable() {
        Asserts.assertThatListIsImmutable(new ArrayList<Object>());
    }

    @Test
    public void shouldCheckThatListIsImmutableByCallingAdd() {
        // noinspection unchecked
        List<Object> list = Mockito.mock(List.class);
        try {
            Asserts.assertThatListIsImmutable(list);
            failBecauseExceptionWasNotThrown(AssertionError.class);
        } catch (AssertionError expected) {
            assertThat(expected).hasMessage(("List is not immutable: list = " + list));
        }
        Mockito.verify(list).add(ArgumentMatchers.any());
        Mockito.verifyNoMoreInteractions(list);
    }

    @Test
    public void shouldCheckThatListIsImmutableByCallingRemove() {
        // noinspection unchecked
        List<Object> list = Mockito.mock(List.class);
        Mockito.when(list.add(ArgumentMatchers.any())).thenThrow(new UnsupportedOperationException("add() not supported"));
        try {
            Asserts.assertThatListIsImmutable(list);
            failBecauseExceptionWasNotThrown(AssertionError.class);
        } catch (AssertionError expected) {
            assertThat(expected).hasMessage(("List is not immutable: list = " + list));
        }
        Mockito.verify(list).add(ArgumentMatchers.any());
        Mockito.verify(list).remove(0);
        Mockito.verifyNoMoreInteractions(list);
    }
}

