package org.jabref.gui.keyboard;


import KeyBinding.ABBREVIATE;
import KeyBinding.CLEANUP;
import KeyBinding.CLOSE;
import KeyBinding.COPY;
import KeyBinding.IMPORT_INTO_NEW_DATABASE;
import KeyBinding.SAVE_DATABASE;
import OS.OS_X;
import java.awt.event.InputEvent;
import java.util.Optional;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javax.swing.JFrame;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static KeyBindingCategory.FILE;
import static java.awt.event.KeyEvent.CHAR_UNDEFINED;
import static java.awt.event.KeyEvent.VK_S;


/**
 * Test class for the keybindings dialog view model
 */
public class KeyBindingsDialogViewModelTest {
    private KeyBindingsDialogViewModel model;

    private KeyBindingRepository keyBindingRepository;

    @Test
    public void testInvalidKeyBindingIsNotSaved() {
        setKeyBindingViewModel(COPY);
        KeyEvent shortcutKeyEvent = new KeyEvent(KeyEvent.KEY_RELEASED, "Q", "Q", KeyCode.Q, false, false, false, false);
        Assertions.assertFalse(keyBindingRepository.checkKeyCombinationEquality(COPY, shortcutKeyEvent));
        model.setNewBindingForCurrent(shortcutKeyEvent);
        KeyCombination combination = KeyCombination.keyCombination(keyBindingRepository.get(COPY).get());
        Assertions.assertFalse(KeyBindingRepository.checkKeyCombinationEquality(combination, shortcutKeyEvent));
        model.saveKeyBindings();
        Assertions.assertFalse(keyBindingRepository.checkKeyCombinationEquality(COPY, shortcutKeyEvent));
    }

    @Test
    public void testSpecialKeysValidKeyBindingIsSaved() {
        setKeyBindingViewModel(IMPORT_INTO_NEW_DATABASE);
        KeyEvent shortcutKeyEvent = new KeyEvent(KeyEvent.KEY_RELEASED, "F1", "F1", KeyCode.F1, false, false, false, false);
        Assertions.assertFalse(keyBindingRepository.checkKeyCombinationEquality(IMPORT_INTO_NEW_DATABASE, shortcutKeyEvent));
        model.setNewBindingForCurrent(shortcutKeyEvent);
        KeyCombination combination = KeyCombination.keyCombination(keyBindingRepository.get(IMPORT_INTO_NEW_DATABASE).get());
        Assertions.assertTrue(KeyBindingRepository.checkKeyCombinationEquality(combination, shortcutKeyEvent));
        model.saveKeyBindings();
        Assertions.assertTrue(keyBindingRepository.checkKeyCombinationEquality(IMPORT_INTO_NEW_DATABASE, shortcutKeyEvent));
    }

    @Test
    public void testKeyBindingCategory() {
        KeyBindingViewModel bindViewModel = new KeyBindingViewModel(keyBindingRepository, FILE);
        model.selectedKeyBindingProperty().set(bindViewModel);
        KeyEvent shortcutKeyEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "M", "M", KeyCode.M, true, true, true, false);
        Assertions.assertFalse(keyBindingRepository.checkKeyCombinationEquality(CLEANUP, shortcutKeyEvent));
        model.setNewBindingForCurrent(shortcutKeyEvent);
        Assertions.assertNull(model.selectedKeyBindingProperty().get().getKeyBinding());
    }

    @Test
    public void testRandomNewKeyKeyBindingInRepository() {
        setKeyBindingViewModel(CLEANUP);
        KeyEvent shortcutKeyEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "K", "K", KeyCode.K, true, true, true, false);
        Assertions.assertFalse(keyBindingRepository.checkKeyCombinationEquality(CLEANUP, shortcutKeyEvent));
        model.setNewBindingForCurrent(shortcutKeyEvent);
        KeyCombination combination = KeyCombination.keyCombination(keyBindingRepository.get(CLEANUP).get());
        Assertions.assertTrue(KeyBindingRepository.checkKeyCombinationEquality(combination, shortcutKeyEvent));
        Assertions.assertFalse(KeyBindingRepository.checkKeyCombinationEquality(KeyCombination.valueOf(CLEANUP.getDefaultKeyBinding()), shortcutKeyEvent));
    }

    @Test
    public void testSaveNewKeyBindingsToPreferences() {
        Assumptions.assumeFalse(OS_X);
        setKeyBindingViewModel(ABBREVIATE);
        KeyEvent shortcutKeyEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "J", "J", KeyCode.J, true, true, true, false);
        Assertions.assertFalse(keyBindingRepository.checkKeyCombinationEquality(ABBREVIATE, shortcutKeyEvent));
        model.setNewBindingForCurrent(shortcutKeyEvent);
        model.saveKeyBindings();
        Assertions.assertTrue(keyBindingRepository.checkKeyCombinationEquality(ABBREVIATE, shortcutKeyEvent));
    }

    @Test
    public void testSaveNewSpecialKeysKeyBindingsToPreferences() {
        setKeyBindingViewModel(ABBREVIATE);
        KeyEvent shortcutKeyEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "F1", "F1", KeyCode.F1, true, false, false, false);
        Assertions.assertFalse(keyBindingRepository.checkKeyCombinationEquality(ABBREVIATE, shortcutKeyEvent));
        model.setNewBindingForCurrent(shortcutKeyEvent);
        model.saveKeyBindings();
        Assertions.assertTrue(keyBindingRepository.checkKeyCombinationEquality(ABBREVIATE, shortcutKeyEvent));
    }

    @Test
    public void testSetAllKeyBindingsToDefault() {
        Assumptions.assumeFalse(OS_X);
        setKeyBindingViewModel(ABBREVIATE);
        KeyEvent shortcutKeyEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "C", "C", KeyCode.C, true, true, true, false);
        Assertions.assertFalse(keyBindingRepository.checkKeyCombinationEquality(ABBREVIATE, shortcutKeyEvent));
        model.setNewBindingForCurrent(shortcutKeyEvent);
        model.saveKeyBindings();
        Assertions.assertTrue(keyBindingRepository.checkKeyCombinationEquality(ABBREVIATE, shortcutKeyEvent));
        keyBindingRepository.resetToDefault();
        model.saveKeyBindings();
        Assertions.assertFalse(keyBindingRepository.checkKeyCombinationEquality(ABBREVIATE, shortcutKeyEvent));
    }

    @Test
    public void testCloseEntryEditorCloseEntryKeybinding() {
        KeyBindingViewModel viewModel = setKeyBindingViewModel(CLOSE);
        model.selectedKeyBindingProperty().set(viewModel);
        KeyEvent closeEditorEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.ESCAPE, false, false, false, false);
        Assertions.assertEquals(CLOSE.getDefaultKeyBinding(), KeyCode.ESCAPE.getName());
        KeyCombination combi = KeyCombination.valueOf(CLOSE.getDefaultKeyBinding());
        Assertions.assertTrue(combi.match(closeEditorEvent));
        Assertions.assertTrue(keyBindingRepository.checkKeyCombinationEquality(CLOSE, closeEditorEvent));
    }

    @Test
    public void testSetSingleKeyBindingToDefault() {
        Assumptions.assumeFalse(OS_X);
        KeyBindingViewModel viewModel = setKeyBindingViewModel(ABBREVIATE);
        model.selectedKeyBindingProperty().set(viewModel);
        KeyEvent shortcutKeyEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "C", "C", KeyCode.C, true, true, true, false);
        Assertions.assertFalse(keyBindingRepository.checkKeyCombinationEquality(ABBREVIATE, shortcutKeyEvent));
        model.setNewBindingForCurrent(shortcutKeyEvent);
        model.saveKeyBindings();
        Assertions.assertTrue(keyBindingRepository.checkKeyCombinationEquality(ABBREVIATE, shortcutKeyEvent));
        viewModel.resetToDefault();
        model.saveKeyBindings();
        Assertions.assertFalse(keyBindingRepository.checkKeyCombinationEquality(ABBREVIATE, shortcutKeyEvent));
    }

    @Test
    public void testConversionAwtKeyEventJavafxKeyEvent() {
        Assumptions.assumeFalse(OS_X);
        java.awt.event.KeyEvent evt = new java.awt.event.KeyEvent(Mockito.mock(JFrame.class), 0, 0, InputEvent.CTRL_MASK, VK_S, CHAR_UNDEFINED);
        Optional<KeyBinding> keyBinding = keyBindingRepository.mapToKeyBinding(evt);
        Assertions.assertEquals(Optional.of(SAVE_DATABASE), keyBinding);
    }
}

