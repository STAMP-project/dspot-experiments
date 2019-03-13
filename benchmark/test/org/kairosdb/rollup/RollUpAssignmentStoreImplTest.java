package org.kairosdb.rollup;


import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.kairosdb.testing.FakeServiceKeyStore;


public class RollUpAssignmentStoreImplTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private FakeServiceKeyStore fakeKeyStore = new FakeServiceKeyStore();

    private RollUpAssignmentStore store = new RollUpAssignmentStoreImpl(fakeKeyStore);

    @Test
    public void test_constructor_nullKeystore_invalid() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("serviceKeyStore cannot be null");
        new RollUpAssignmentStoreImpl(null);
    }

    @Test
    public void test_assigedIds() throws RollUpException {
        Collection<String> ids = store.getAssignedIds("host1");
        Assert.assertThat(ids.size(), CoreMatchers.equalTo(1));
        Assert.assertThat(ids, CoreMatchers.hasItems("id1"));
        ids = store.getAssignedIds("host2");
        Assert.assertThat(ids.size(), CoreMatchers.equalTo(1));
        Assert.assertThat(ids, CoreMatchers.hasItems("id2"));
        ids = store.getAssignedIds("host3");
        Assert.assertThat(ids.size(), CoreMatchers.equalTo(3));
        Assert.assertThat(ids, CoreMatchers.hasItems("id3", "id4", "id5"));
    }

    @Test
    public void test_assignmentIds() throws RollUpException {
        Set<String> assignmentIds = store.getAssignmentIds();
        Assert.assertThat(assignmentIds.size(), CoreMatchers.equalTo(5));
        Assert.assertThat(assignmentIds, CoreMatchers.hasItems("id1", "id2", "id3", "id4", "id5"));
    }

    @Test
    public void test_assignments() throws RollUpException {
        Map<String, String> assignments = store.getAssignments();
        Assert.assertThat(assignments.size(), CoreMatchers.equalTo(5));
        Assert.assertThat(assignments.get("id1"), CoreMatchers.equalTo("host1"));
        Assert.assertThat(assignments.get("id2"), CoreMatchers.equalTo("host2"));
        Assert.assertThat(assignments.get("id3"), CoreMatchers.equalTo("host3"));
        Assert.assertThat(assignments.get("id4"), CoreMatchers.equalTo("host3"));
        Assert.assertThat(assignments.get("id5"), CoreMatchers.equalTo("host3"));
    }

    @Test
    public void test_removeAssignments() throws RollUpException {
        store.removeAssignments(ImmutableSet.of("id2", "id4"));
        Map<String, String> assignments = store.getAssignments();
        Assert.assertThat(assignments.size(), CoreMatchers.equalTo(3));
        Assert.assertThat(assignments.get("id1"), CoreMatchers.equalTo("host1"));
        Assert.assertThat(assignments.get("id3"), CoreMatchers.equalTo("host3"));
        Assert.assertThat(assignments.get("id5"), CoreMatchers.equalTo("host3"));
    }
}

