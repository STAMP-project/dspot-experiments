package brave.internal;


import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;


public class ListsTest {
    @Test
    public void ensureMutable_leavesArrayList() {
        List<Object> list = new ArrayList<>();
        assertThat(Lists.ensureMutable(list)).isSameAs(list);
    }

    @Test
    public void ensureMutable_copiesImmutable() {
        List<Object> list = Collections.unmodifiableList(Arrays.asList("foo", "bar"));
        assertThat(Lists.ensureMutable(list)).isInstanceOf(ArrayList.class).containsExactlyElementsOf(list);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void ensureImmutable_returnsImmutableEmptyList() {
        Lists.ensureImmutable(new ArrayList()).add("foo");
    }

    @Test
    public void ensureImmutable_convertsToSingletonList() {
        List<Object> list = new ArrayList<>();
        list.add("foo");
        assertThat(Lists.ensureImmutable(list).getClass().getSimpleName()).isEqualTo("SingletonList");
    }

    @Test
    public void ensureImmutable_returnsEmptyList() {
        List<Object> list = Collections.emptyList();
        assertThat(Lists.ensureImmutable(list)).isSameAs(list);
    }

    @Test
    public void ensureImmutable_doesntCopySingletonList() {
        List<Object> list = Collections.singletonList("foo");
        assertThat(Lists.ensureImmutable(list)).isSameAs(list);
    }

    @Test
    public void ensureImmutable_doesntCopyUnmodifiableList() {
        List<Object> list = Collections.unmodifiableList(Arrays.asList("foo"));
        assertThat(Lists.ensureImmutable(list)).isSameAs(list);
    }

    @Test
    public void ensureImmutable_doesntCopyImmutableList() {
        List<Object> list = ImmutableList.of("foo");
        assertThat(Lists.ensureImmutable(list)).isSameAs(list);
    }

    @Test
    public void concatImmutableLists_choosesNonEmpty() {
        List<Object> list = ImmutableList.of("foo");
        assertThat(Lists.concatImmutableLists(list, Collections.emptyList())).isSameAs(list);
        assertThat(Lists.concatImmutableLists(Collections.emptyList(), list)).isSameAs(list);
    }

    @Test
    public void concatImmutableLists_concatenates() {
        List<Object> list1 = ImmutableList.of("foo");
        List<Object> list2 = ImmutableList.of("bar", "baz");
        assertThat(Lists.concatImmutableLists(list1, list2)).hasSameClassAs(Collections.unmodifiableList(list1)).containsExactly("foo", "bar", "baz");
    }
}

