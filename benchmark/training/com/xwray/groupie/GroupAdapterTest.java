package com.xwray.groupie;


import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class GroupAdapterTest {
    GroupAdapter groupAdapter;

    @Test(expected = RuntimeException.class)
    public void addItemMustBeNonNull() {
        groupAdapter.add(null);
    }

    @Test(expected = RuntimeException.class)
    public void addAllItemsMustBeNonNull() {
        List<Group> groups = new ArrayList<>();
        groups.add(null);
        groupAdapter.addAll(groups);
    }

    @Test(expected = RuntimeException.class)
    public void removeGroupMustBeNonNull() {
        groupAdapter.remove(null);
    }

    @Test(expected = RuntimeException.class)
    public void putGroupMustBeNonNull() {
        groupAdapter.add(0, null);
    }
}

