package org.jabref.logic.util;


import java.util.Optional;
import org.jabref.model.FieldChange;
import org.jabref.model.entry.BibEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class UpdateFieldTest {
    private BibEntry entry;

    @Test
    public void testUpdateFieldWorksEmptyField() {
        Assertions.assertFalse(entry.hasField("year"));
        UpdateField.updateField(entry, "year", "2016");
        Assertions.assertEquals(Optional.of("2016"), entry.getField("year"));
    }

    @Test
    public void testUpdateFieldWorksNonEmptyField() {
        entry.setField("year", "2015");
        UpdateField.updateField(entry, "year", "2016");
        Assertions.assertEquals(Optional.of("2016"), entry.getField("year"));
    }

    @Test
    public void testUpdateFieldHasChanged() {
        Assertions.assertFalse(entry.hasChanged());
        UpdateField.updateField(entry, "year", "2016");
        Assertions.assertTrue(entry.hasChanged());
    }

    @Test
    public void testUpdateFieldValidFieldChange() {
        Optional<FieldChange> change = UpdateField.updateField(entry, "year", "2016");
        Assertions.assertTrue(change.isPresent());
    }

    @Test
    public void testUpdateFieldCorrectFieldChangeContentsEmptyField() {
        Optional<FieldChange> change = UpdateField.updateField(entry, "year", "2016");
        Assertions.assertNull(change.get().getOldValue());
        Assertions.assertEquals("year", change.get().getField());
        Assertions.assertEquals("2016", change.get().getNewValue());
        Assertions.assertEquals(entry, change.get().getEntry());
    }

    @Test
    public void testUpdateFieldCorrectFieldChangeContentsNonEmptyField() {
        entry.setField("year", "2015");
        Optional<FieldChange> change = UpdateField.updateField(entry, "year", "2016");
        Assertions.assertEquals("2015", change.get().getOldValue());
        Assertions.assertEquals("year", change.get().getField());
        Assertions.assertEquals("2016", change.get().getNewValue());
        Assertions.assertEquals(entry, change.get().getEntry());
    }

    @Test
    public void testUpdateFieldSameValueNoChange() {
        entry.setField("year", "2016");
        Optional<FieldChange> change = UpdateField.updateField(entry, "year", "2016");
        Assertions.assertFalse(change.isPresent());
    }

    @Test
    public void testUpdateFieldSameValueNotChange() {
        entry.setField("year", "2016");
        entry.setChanged(false);
        UpdateField.updateField(entry, "year", "2016");
        Assertions.assertFalse(entry.hasChanged());
    }

    @Test
    public void testUpdateFieldSetToNullClears() {
        entry.setField("year", "2016");
        UpdateField.updateField(entry, "year", null);
        Assertions.assertFalse(entry.hasField("year"));
    }

    @Test
    public void testUpdateFieldSetEmptyToNullClears() {
        UpdateField.updateField(entry, "year", null);
        Assertions.assertFalse(entry.hasField("year"));
    }

    @Test
    public void testUpdateFieldSetToNullHasFieldChangeContents() {
        entry.setField("year", "2016");
        Optional<FieldChange> change = UpdateField.updateField(entry, "year", null);
        Assertions.assertTrue(change.isPresent());
    }

    @Test
    public void testUpdateFieldSetRmptyToNullHasNoFieldChangeContents() {
        Optional<FieldChange> change = UpdateField.updateField(entry, "year", null);
        Assertions.assertFalse(change.isPresent());
    }

    @Test
    public void testUpdateFieldSetToNullCorrectFieldChangeContents() {
        entry.setField("year", "2016");
        Optional<FieldChange> change = UpdateField.updateField(entry, "year", null);
        Assertions.assertNull(change.get().getNewValue());
        Assertions.assertEquals("year", change.get().getField());
        Assertions.assertEquals("2016", change.get().getOldValue());
        Assertions.assertEquals(entry, change.get().getEntry());
    }

    @Test
    public void testUpdateFieldSameContentClears() {
        entry.setField("year", "2016");
        UpdateField.updateField(entry, "year", "2016", true);
        Assertions.assertFalse(entry.hasField("year"));
    }

    @Test
    public void testUpdateFieldSameContentHasChanged() {
        entry.setField("year", "2016");
        entry.setChanged(false);
        UpdateField.updateField(entry, "year", "2016", true);
        Assertions.assertTrue(entry.hasChanged());
    }

    @Test
    public void testUpdateFieldSameContentHasFieldChange() {
        entry.setField("year", "2016");
        Optional<FieldChange> change = UpdateField.updateField(entry, "year", "2016", true);
        Assertions.assertTrue(change.isPresent());
    }

    @Test
    public void testUpdateFieldSameContentHasCorrectFieldChange() {
        entry.setField("year", "2016");
        Optional<FieldChange> change = UpdateField.updateField(entry, "year", "2016", true);
        Assertions.assertNull(change.get().getNewValue());
        Assertions.assertEquals("year", change.get().getField());
        Assertions.assertEquals("2016", change.get().getOldValue());
        Assertions.assertEquals(entry, change.get().getEntry());
    }

    @Test
    public void testUpdateNonDisplayableFieldUpdates() {
        Assertions.assertFalse(entry.hasField("year"));
        UpdateField.updateNonDisplayableField(entry, "year", "2016");
        Assertions.assertTrue(entry.hasField("year"));
        Assertions.assertEquals(Optional.of("2016"), entry.getField("year"));
    }

    @Test
    public void testUpdateNonDisplayableFieldHasNotChanged() {
        Assertions.assertFalse(entry.hasChanged());
        UpdateField.updateNonDisplayableField(entry, "year", "2016");
        Assertions.assertFalse(entry.hasChanged());
    }
}

