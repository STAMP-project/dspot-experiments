package org.jabref.model.groups;


import java.util.HashSet;
import java.util.Set;
import org.jabref.model.entry.BibEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static GroupHierarchyType.INCLUDING;
import static GroupHierarchyType.INDEPENDENT;


public class AutomaticKeywordGroupTest {
    @Test
    public void createSubgroupsForTwoKeywords() throws Exception {
        AutomaticKeywordGroup keywordsGroup = new AutomaticKeywordGroup("Keywords", INDEPENDENT, "keywords", ',', '>');
        BibEntry entry = new BibEntry().withField("keywords", "A, B");
        Set<GroupTreeNode> expected = new HashSet<>();
        expected.add(GroupTreeNode.fromGroup(new WordKeywordGroup("A", INCLUDING, "keywords", "A", true, ',', true)));
        expected.add(GroupTreeNode.fromGroup(new WordKeywordGroup("B", INCLUDING, "keywords", "B", true, ',', true)));
        Assertions.assertEquals(expected, keywordsGroup.createSubgroups(entry));
    }
}

