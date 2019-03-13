package org.jabref.model.entry.specialfields;


import SpecialField.RANKING;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class SpecialFieldTest {
    @Test
    public void getSpecialFieldInstanceFromFieldNameValid() {
        Assertions.assertEquals(Optional.of(RANKING), SpecialField.getSpecialFieldInstanceFromFieldName("ranking"));
    }

    @Test
    public void getSpecialFieldInstanceFromFieldNameEmptyForInvalidField() {
        Assertions.assertEquals(Optional.empty(), SpecialField.getSpecialFieldInstanceFromFieldName("title"));
    }

    @Test
    public void isSpecialFieldTrueForValidField() {
        Assertions.assertTrue(SpecialField.isSpecialField("ranking"));
    }

    @Test
    public void isSpecialFieldFalseForInvalidField() {
        Assertions.assertFalse(SpecialField.isSpecialField("title"));
    }
}

