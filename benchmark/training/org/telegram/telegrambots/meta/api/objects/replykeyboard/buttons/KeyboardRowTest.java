package org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class KeyboardRowTest {
    private static final List<String> BUTTON_NAMES = Arrays.asList("Carlotta Valdes", "Jimmy Stewart");

    @Test
    public void shouldAddAllButtons() {
        final KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.addAll(KeyboardRowTest.BUTTON_NAMES);
        Assert.assertThat(keyboardRow.size(), Is.is(2));
        Assert.assertThat(keyboardRow.get(0).getText(), Is.is("Carlotta Valdes"));
        Assert.assertThat(keyboardRow.get(1).getText(), Is.is("Jimmy Stewart"));
    }

    @Test
    public void shouldAddNoButtons() {
        final KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.addAll(new ArrayList<String>());
        Assert.assertTrue(keyboardRow.isEmpty());
    }
}

