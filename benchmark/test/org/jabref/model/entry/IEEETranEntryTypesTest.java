package org.jabref.model.entry;


import IEEETranEntryTypes.IEEETRANBSTCTL;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class IEEETranEntryTypesTest {
    @Test
    public void ctlTypeContainsYesNoFields() {
        Collection<String> ctlFields = IEEETRANBSTCTL.getAllFields();
        List<String> ynFields = InternalBibtexFields.getIEEETranBSTctlYesNoFields();
        Assertions.assertTrue(ctlFields.containsAll(ynFields));
    }
}

