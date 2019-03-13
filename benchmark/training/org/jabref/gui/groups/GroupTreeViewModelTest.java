package org.jabref.gui.groups;


import FieldName.GROUPS;
import FieldName.KEYWORDS;
import java.util.Optional;
import org.jabref.gui.StateManager;
import org.jabref.gui.util.CustomLocalDragboard;
import org.jabref.gui.util.TaskExecutor;
import org.jabref.model.database.BibDatabaseContext;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.groups.AllEntriesGroup;
import org.jabref.model.groups.ExplicitGroup;
import org.jabref.model.groups.GroupHierarchyType;
import org.jabref.model.groups.WordKeywordGroup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class GroupTreeViewModelTest {
    StateManager stateManager;

    GroupTreeViewModel groupTree;

    BibDatabaseContext databaseContext;

    private TaskExecutor taskExecutor;

    @Test
    public void rootGroupIsAllEntriesByDefault() throws Exception {
        AllEntriesGroup allEntriesGroup = new AllEntriesGroup("All entries");
        Assertions.assertEquals(new GroupNodeViewModel(databaseContext, stateManager, taskExecutor, allEntriesGroup, new CustomLocalDragboard()), groupTree.rootGroupProperty().getValue());
    }

    @Test
    public void explicitGroupsAreRemovedFromEntriesOnDelete() {
        ExplicitGroup group = new ExplicitGroup("group", GroupHierarchyType.INDEPENDENT, ',');
        BibEntry entry = new BibEntry();
        databaseContext.getDatabase().insertEntry(entry);
        GroupNodeViewModel model = new GroupNodeViewModel(databaseContext, stateManager, taskExecutor, group, new CustomLocalDragboard());
        model.addEntriesToGroup(databaseContext.getEntries());
        groupTree.removeGroupsAndSubGroupsFromEntries(model);
        Assertions.assertEquals(Optional.empty(), entry.getField(GROUPS));
    }

    @Test
    public void keywordGroupsAreNotRemovedFromEntriesOnDelete() {
        String groupName = "A";
        WordKeywordGroup group = new WordKeywordGroup(groupName, GroupHierarchyType.INCLUDING, "keywords", groupName, true, ',', true);
        BibEntry entry = new BibEntry();
        databaseContext.getDatabase().insertEntry(entry);
        GroupNodeViewModel model = new GroupNodeViewModel(databaseContext, stateManager, taskExecutor, group, new CustomLocalDragboard());
        model.addEntriesToGroup(databaseContext.getEntries());
        groupTree.removeGroupsAndSubGroupsFromEntries(model);
        Assertions.assertEquals(groupName, entry.getField(KEYWORDS).get());
    }
}

