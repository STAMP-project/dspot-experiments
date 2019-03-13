package me.tatarka.bindingcollectionadapter2;


import ObservableList.OnListChangedCallback;
import androidx.databinding.ObservableList;
import java.util.Arrays;
import me.tatarka.bindingcollectionadapter2.collections.DiffObservableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


@RunWith(JUnit4.class)
@SuppressWarnings("unchecked")
public class DiffObservableListTest {
    @Test
    public void insetOneItem() {
        DiffObservableList<DiffObservableListTest.Item> list = new DiffObservableList(this.DIFF_CALLBACK);
        ObservableList.OnListChangedCallback callback = Mockito.mock(OnListChangedCallback.class);
        list.addOnListChangedCallback(callback);
        list.update(Arrays.asList(new DiffObservableListTest.Item("1", "a")));
        assertThat(list).hasSize(1).containsExactly(new DiffObservableListTest.Item("1", "a"));
        Mockito.verify(callback).onItemRangeInserted(list, 0, 1);
    }

    @Test
    public void removeOneItem() {
        DiffObservableList<DiffObservableListTest.Item> list = new DiffObservableList(this.DIFF_CALLBACK);
        ObservableList.OnListChangedCallback callback = Mockito.mock(OnListChangedCallback.class);
        list.addOnListChangedCallback(callback);
        list.update(Arrays.asList(new DiffObservableListTest.Item("1", "a")));
        list.update(Arrays.<DiffObservableListTest.Item>asList());
        assertThat(list).isEmpty();
        Mockito.verify(callback).onItemRangeRemoved(list, 0, 1);
    }

    @Test
    public void moveOneItem() {
        DiffObservableList<DiffObservableListTest.Item> list = new DiffObservableList(this.DIFF_CALLBACK);
        ObservableList.OnListChangedCallback callback = Mockito.mock(OnListChangedCallback.class);
        list.addOnListChangedCallback(callback);
        list.update(Arrays.asList(new DiffObservableListTest.Item("1", "a"), new DiffObservableListTest.Item("2", "b")));
        list.update(Arrays.asList(new DiffObservableListTest.Item("2", "b"), new DiffObservableListTest.Item("1", "a")));
        assertThat(list).hasSize(2).containsExactly(new DiffObservableListTest.Item("2", "b"), new DiffObservableListTest.Item("1", "a"));
        Mockito.verify(callback).onItemRangeMoved(list, 1, 0, 1);
    }

    @Test
    public void changeItem() {
        DiffObservableList<DiffObservableListTest.Item> list = new DiffObservableList(this.DIFF_CALLBACK);
        ObservableList.OnListChangedCallback callback = Mockito.mock(OnListChangedCallback.class);
        list.addOnListChangedCallback(callback);
        list.update(Arrays.asList(new DiffObservableListTest.Item("1", "a")));
        list.update(Arrays.asList(new DiffObservableListTest.Item("1", "b")));
        assertThat(list).hasSize(1).containsExactly(new DiffObservableListTest.Item("1", "b"));
        Mockito.verify(callback).onItemRangeChanged(list, 0, 1);
    }

    static class Item {
        static final DiffObservableList.Callback<DiffObservableListTest.Item> DIFF_CALLBACK = new DiffObservableList.Callback<DiffObservableListTest.Item>() {
            @Override
            public boolean areItemsTheSame(me.tatarka.bindingcollectionadapter2.Item oldItem, me.tatarka.bindingcollectionadapter2.Item newItem) {
                return oldItem.id.equals(newItem.id);
            }

            @Override
            public boolean areContentsTheSame(me.tatarka.bindingcollectionadapter2.Item oldItem, me.tatarka.bindingcollectionadapter2.Item newItem) {
                return oldItem.value.equals(newItem.value);
            }
        };

        final String id;

        final String value;

        Item(String id, String value) {
            this.id = id;
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            DiffObservableListTest.Item item = ((DiffObservableListTest.Item) (o));
            if (!(id.equals(item.id)))
                return false;

            return value.equals(item.value);
        }

        @Override
        public int hashCode() {
            int result = id.hashCode();
            result = (31 * result) + (value.hashCode());
            return result;
        }

        @Override
        public String toString() {
            return (((((("Test(" + "id='") + (id)) + '\'') + ", value='") + (value)) + '\'') + ')';
        }
    }
}

