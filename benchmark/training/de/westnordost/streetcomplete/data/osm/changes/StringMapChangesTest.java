package de.westnordost.streetcomplete.data.osm.changes;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class StringMapChangesTest {
    @Test
    public void empty() {
        List<StringMapEntryChange> list = Collections.emptyList();
        StringMapChanges changes = new StringMapChanges(list);
        Assert.assertEquals("", changes.toString());
        Assert.assertTrue(changes.getChanges().isEmpty());
        // executable without error:
        Map<String, String> someMap = Collections.emptyMap();
        changes.applyTo(someMap);
        Assert.assertFalse(changes.hasConflictsTo(someMap));
    }

    @Test
    public void one() {
        StringMapEntryChange change1 = Mockito.mock(StringMapEntryChange.class);
        Mockito.when(change1.toString()).thenReturn("a");
        StringMapChanges changes = new StringMapChanges(Collections.singletonList(change1));
        Map<String, String> someMap = Collections.emptyMap();
        Assert.assertEquals("a", changes.toString());
        changes.applyTo(someMap);
        Mockito.verify(change1).applyTo(someMap);
        changes.hasConflictsTo(someMap);
        Mockito.verify(change1, Mockito.atLeastOnce()).conflictsWith(someMap);
    }

    @Test
    public void two() {
        List<StringMapEntryChange> list = new ArrayList<>();
        StringMapEntryChange change1 = Mockito.mock(StringMapEntryChange.class);
        Mockito.when(change1.toString()).thenReturn("a");
        list.add(change1);
        StringMapEntryChange change2 = Mockito.mock(StringMapEntryChange.class);
        Mockito.when(change2.toString()).thenReturn("b");
        list.add(change2);
        StringMapChanges changes = new StringMapChanges(list);
        Map<String, String> someMap = Collections.emptyMap();
        Assert.assertEquals("a, b", changes.toString());
        changes.applyTo(someMap);
        Mockito.verify(change1).applyTo(someMap);
        Mockito.verify(change2).applyTo(someMap);
        changes.hasConflictsTo(someMap);
        Mockito.verify(change1, Mockito.atLeastOnce()).conflictsWith(someMap);
        Mockito.verify(change2, Mockito.atLeastOnce()).conflictsWith(someMap);
    }

    @Test(expected = IllegalStateException.class)
    public void applyToConflictFails() {
        Map<String, String> someMap = Collections.emptyMap();
        StringMapEntryChange change1 = Mockito.mock(StringMapEntryChange.class);
        Mockito.when(change1.conflictsWith(someMap)).thenReturn(true);
        StringMapChanges changes = new StringMapChanges(Collections.singletonList(change1));
        changes.applyTo(someMap);
    }

    @Test
    public void getConflicts() {
        List<StringMapEntryChange> list = new ArrayList<>();
        Map<String, String> someMap = Collections.emptyMap();
        StringMapEntryChange conflict = Mockito.mock(StringMapEntryChange.class);
        Mockito.when(conflict.conflictsWith(someMap)).thenReturn(true);
        list.add(Mockito.mock(StringMapEntryChange.class));
        list.add(Mockito.mock(StringMapEntryChange.class));
        list.add(conflict);
        list.add(Mockito.mock(StringMapEntryChange.class));
        list.add(conflict);
        StringMapChanges changes = new StringMapChanges(list);
        changes.getConflictsTo(someMap);
        Iterator<StringMapEntryChange> it = changes.getConflictsTo(someMap).iterator();
        Assert.assertSame(conflict, it.next());
        Assert.assertSame(conflict, it.next());
    }
}

