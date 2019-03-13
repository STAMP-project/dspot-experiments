package org.jabref.model.groups;


import FieldName.GROUPS;
import java.util.Optional;
import org.jabref.model.entry.BibEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static GroupHierarchyType.INCLUDING;


public class ExplicitGroupTest {
    private ExplicitGroup group;

    private ExplicitGroup group2;

    private BibEntry entry;

    @Test
    public void addSingleGroupToEmptyBibEntryChangesGroupsField() {
        group.add(entry);
        Assertions.assertEquals(Optional.of("myExplicitGroup"), entry.getField(GROUPS));
    }

    @Test
    public void addSingleGroupToNonemptyBibEntryAppendsToGroupsField() {
        entry.setField(GROUPS, "some thing");
        group.add(entry);
        Assertions.assertEquals(Optional.of("some thing, myExplicitGroup"), entry.getField(GROUPS));
    }

    @Test
    public void addTwoGroupsToBibEntryChangesGroupsField() {
        group.add(entry);
        group2.add(entry);
        Assertions.assertEquals(Optional.of("myExplicitGroup, myExplicitGroup2"), entry.getField(GROUPS));
    }

    @Test
    public void addDuplicateGroupDoesNotChangeGroupsField() throws Exception {
        entry.setField(GROUPS, "myExplicitGroup");
        group.add(entry);
        Assertions.assertEquals(Optional.of("myExplicitGroup"), entry.getField(GROUPS));
    }

    // For https://github.com/JabRef/jabref/issues/2334
    @Test
    public void removeDoesNotChangeFieldIfContainsNameAsPart() throws Exception {
        entry.setField(GROUPS, "myExplicitGroup_alternative");
        group.remove(entry);
        Assertions.assertEquals(Optional.of("myExplicitGroup_alternative"), entry.getField(GROUPS));
    }

    // For https://github.com/JabRef/jabref/issues/2334
    @Test
    public void removeDoesNotChangeFieldIfContainsNameAsWord() throws Exception {
        entry.setField(GROUPS, "myExplicitGroup alternative");
        group.remove(entry);
        Assertions.assertEquals(Optional.of("myExplicitGroup alternative"), entry.getField(GROUPS));
    }

    // For https://github.com/JabRef/jabref/issues/1873
    @Test
    public void containsOnlyMatchesCompletePhraseWithWhitespace() throws Exception {
        entry.setField(GROUPS, "myExplicitGroup b");
        Assertions.assertFalse(group.contains(entry));
    }

    // For https://github.com/JabRef/jabref/issues/1873
    @Test
    public void containsOnlyMatchesCompletePhraseWithSlash() throws Exception {
        entry.setField(GROUPS, "myExplicitGroup/b");
        Assertions.assertFalse(group.contains(entry));
    }

    // For https://github.com/JabRef/jabref/issues/2394
    @Test
    public void containsMatchesPhraseWithBrackets() throws Exception {
        entry.setField(GROUPS, "[aa] Subgroup1");
        ExplicitGroup explicitGroup = new ExplicitGroup("[aa] Subgroup1", INCLUDING, ',');
        Assertions.assertTrue(explicitGroup.contains(entry));
    }
}

