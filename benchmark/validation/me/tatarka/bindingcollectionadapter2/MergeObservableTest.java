package me.tatarka.bindingcollectionadapter2;


import ObservableList.OnListChangedCallback;
import androidx.databinding.ObservableList;
import java.util.Arrays;
import me.tatarka.bindingcollectionadapter2.collections.MergeObservableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


@RunWith(JUnit4.class)
@SuppressWarnings("unchecked")
public class MergeObservableTest {
    @Test
    public void emptyListIsEmpty() {
        MergeObservableList<String> list = new MergeObservableList();
        ObservableList.OnListChangedCallback callback = Mockito.mock(OnListChangedCallback.class);
        list.addOnListChangedCallback(callback);
        assertThat(list).isEmpty();
        Mockito.verifyNoMoreInteractions(callback);
    }

    @Test
    public void insertingItemContainsThatItem() {
        MergeObservableList<String> list = new MergeObservableList();
        ObservableList.OnListChangedCallback callback = Mockito.mock(OnListChangedCallback.class);
        list.addOnListChangedCallback(callback);
        list.insertItem("test");
        assertThat(list).hasSize(1).containsExactly("test");
        Mockito.verify(callback).onItemRangeInserted(list, 0, 1);
    }

    @Test
    public void insertingListContainsThatList() {
        MergeObservableList<String> list = new MergeObservableList();
        ObservableList.OnListChangedCallback callback = Mockito.mock(OnListChangedCallback.class);
        list.addOnListChangedCallback(callback);
        ObservableList<String> items = new androidx.databinding.ObservableArrayList();
        items.add("test1");
        items.add("test2");
        list.insertList(items);
        assertThat(list).hasSize(2).containsExactly("test1", "test2");
        Mockito.verify(callback).onItemRangeInserted(list, 0, 2);
    }

    @Test
    public void insertingItemAndListContainsItemThenList() {
        MergeObservableList<String> list = new MergeObservableList();
        ObservableList.OnListChangedCallback callback = Mockito.mock(OnListChangedCallback.class);
        list.addOnListChangedCallback(callback);
        list.insertItem("test1");
        ObservableList<String> items = new androidx.databinding.ObservableArrayList();
        items.add("test2");
        items.add("test3");
        list.insertList(items);
        assertThat(list).hasSize(3).containsExactly("test1", "test2", "test3");
        Mockito.verify(callback).onItemRangeInserted(list, 0, 1);
        Mockito.verify(callback).onItemRangeInserted(list, 1, 2);
    }

    @Test
    public void addingItemToBackingListAddsItemToList() {
        MergeObservableList<String> list = new MergeObservableList();
        ObservableList.OnListChangedCallback callback = Mockito.mock(OnListChangedCallback.class);
        list.addOnListChangedCallback(callback);
        list.insertItem("test1");
        ObservableList<String> items = new androidx.databinding.ObservableArrayList();
        items.add("test2");
        list.insertList(items);
        list.insertItem("test4");
        items.add("test3");
        assertThat(list).hasSize(4).containsExactly("test1", "test2", "test3", "test4");
        Mockito.verify(callback).onItemRangeInserted(list, 0, 1);
        Mockito.verify(callback).onItemRangeInserted(list, 1, 1);
        Mockito.verify(callback, Mockito.times(2)).onItemRangeInserted(list, 2, 1);
    }

    @Test
    public void removingItemFromBackingListRemovesItemFromList() {
        MergeObservableList<String> list = new MergeObservableList();
        ObservableList.OnListChangedCallback callback = Mockito.mock(OnListChangedCallback.class);
        list.addOnListChangedCallback(callback);
        list.insertItem("test1");
        ObservableList<String> items = new androidx.databinding.ObservableArrayList();
        items.add("test2");
        list.insertList(items);
        list.insertItem("test3");
        items.clear();
        assertThat(list).hasSize(2).containsExactly("test1", "test3");
        Mockito.verify(callback).onItemRangeInserted(list, 0, 1);
        Mockito.verify(callback).onItemRangeInserted(list, 1, 1);
        Mockito.verify(callback).onItemRangeInserted(list, 2, 1);
        Mockito.verify(callback).onItemRangeRemoved(list, 1, 1);
    }

    @Test
    public void changingItemFromBackingListChangesItInList() {
        MergeObservableList<String> list = new MergeObservableList();
        ObservableList.OnListChangedCallback callback = Mockito.mock(OnListChangedCallback.class);
        list.addOnListChangedCallback(callback);
        list.insertItem("test1");
        ObservableList<String> items = new androidx.databinding.ObservableArrayList();
        items.add("test2");
        list.insertList(items);
        items.set(0, "test3");
        assertThat(list).hasSize(2).containsExactly("test1", "test3");
        Mockito.verify(callback).onItemRangeInserted(list, 0, 1);
        Mockito.verify(callback).onItemRangeInserted(list, 1, 1);
        Mockito.verify(callback).onItemRangeChanged(list, 1, 1);
    }

    @Test
    public void removingItemRemovesItFromTheList() {
        MergeObservableList<String> list = new MergeObservableList();
        list.insertItem("test1");
        ObservableList.OnListChangedCallback callback = Mockito.mock(OnListChangedCallback.class);
        list.addOnListChangedCallback(callback);
        list.removeItem("test1");
        assertThat(list).isEmpty();
        Mockito.verify(callback).onItemRangeRemoved(list, 0, 1);
    }

    @Test
    public void removingListRemovesItFromTheList() {
        MergeObservableList<String> list = new MergeObservableList();
        ObservableList<String> backingList = new androidx.databinding.ObservableArrayList();
        backingList.addAll(Arrays.asList("test1", "test2"));
        list.insertList(backingList);
        ObservableList.OnListChangedCallback callback = Mockito.mock(OnListChangedCallback.class);
        list.addOnListChangedCallback(callback);
        list.removeList(backingList);
        assertThat(list).isEmpty();
        Mockito.verify(callback).onItemRangeRemoved(list, 0, 2);
    }

    @Test
    public void removingAllRemovesInsertedItemFromTheList() {
        MergeObservableList<String> list = new MergeObservableList();
        list.insertItem("test1");
        ObservableList.OnListChangedCallback callback = Mockito.mock(OnListChangedCallback.class);
        list.addOnListChangedCallback(callback);
        list.removeAll();
        assertThat(list).isEmpty();
        Mockito.verify(callback).onItemRangeRemoved(list, 0, 1);
    }

    @Test
    public void removingAllRemovesInsertedListFromTheList() {
        MergeObservableList<String> list = new MergeObservableList();
        ObservableList<String> backingList = new androidx.databinding.ObservableArrayList();
        backingList.addAll(Arrays.asList("test1", "test2"));
        list.insertList(backingList);
        ObservableList.OnListChangedCallback callback = Mockito.mock(OnListChangedCallback.class);
        list.addOnListChangedCallback(callback);
        list.removeAll();
        assertThat(list).isEmpty();
        Mockito.verify(callback).onItemRangeRemoved(list, 0, 2);
    }
}

