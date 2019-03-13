package me.tatarka.bindingcollectionadapter2;


import ObservableList.OnListChangedCallback;
import androidx.databinding.ObservableList;
import java.lang.ref.WeakReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


@RunWith(JUnit4.class)
public class AdapterReferenceCollectorTest {
    @Test
    public void removesCallbackWhenAdapterIsCollected() throws Exception {
        BindingCollectionAdapter adapter = Mockito.mock(BindingCollectionAdapter.class);
        ObservableList items = Mockito.mock(ObservableList.class);
        ObservableList.OnListChangedCallback callback = Mockito.mock(OnListChangedCallback.class);
        WeakReference ref = AdapterReferenceCollector.createRef(adapter, items, callback);
        adapter = null;
        System.gc();
        do {
            Thread.sleep(50);
        } while ((ref.get()) != null );
        Mockito.verify(items).removeOnListChangedCallback(callback);
    }
}

