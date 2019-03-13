package org.schabi.newpipe.settings.tabs;


import Tab.Type;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class TabTest {
    @Test
    public void checkIdDuplication() {
        final Set<Integer> usedIds = new HashSet<>();
        for (Tab.Type type : Type.values()) {
            final boolean added = usedIds.add(type.getTabId());
            Assert.assertTrue(("Id was already used: " + (type.getTabId())), added);
        }
    }
}

