package org.jabref.migrations;


import java.util.Collections;
import java.util.Optional;
import org.jabref.logic.importer.ParserResult;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.groups.AllEntriesGroup;
import org.jabref.model.groups.ExplicitGroup;
import org.jabref.model.groups.GroupHierarchyType;
import org.jabref.model.groups.GroupTreeNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ConvertLegacyExplicitGroupsTest {
    private PostOpenMigration action;

    private BibEntry entry;

    private ExplicitGroup group;

    @Test
    public void performActionWritesGroupMembershipInEntry() throws Exception {
        ParserResult parserResult = generateParserResult(GroupTreeNode.fromGroup(group));
        action.performMigration(parserResult);
        Assertions.assertEquals(Optional.of("TestGroup"), entry.getField("groups"));
    }

    @Test
    public void performActionClearsLegacyKeys() throws Exception {
        ParserResult parserResult = generateParserResult(GroupTreeNode.fromGroup(group));
        action.performMigration(parserResult);
        Assertions.assertEquals(Collections.emptyList(), group.getLegacyEntryKeys());
    }

    @Test
    public void performActionWritesGroupMembershipInEntryForComplexGroupTree() throws Exception {
        GroupTreeNode root = GroupTreeNode.fromGroup(new AllEntriesGroup(""));
        root.addSubgroup(new ExplicitGroup("TestGroup2", GroupHierarchyType.INCLUDING, ','));
        root.addSubgroup(group);
        ParserResult parserResult = generateParserResult(root);
        action.performMigration(parserResult);
        Assertions.assertEquals(Optional.of("TestGroup"), entry.getField("groups"));
    }
}

