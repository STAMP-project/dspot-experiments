package org.jabref.gui.fieldeditors;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class IdentifierEditorViewModelTest {
    private IdentifierEditorViewModel viewModel;

    @Test
    public void validIdentifierIsNotPresentIsTrueForEmptyText() throws Exception {
        Assertions.assertTrue(viewModel.validIdentifierIsNotPresentProperty().get());
    }
}

