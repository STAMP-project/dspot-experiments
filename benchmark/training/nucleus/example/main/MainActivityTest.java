package nucleus.example.main;


import MainPresenter.NAME_1;
import MainPresenter.NAME_2;
import R.id.check1;
import R.id.check2;
import R.layout.item;
import ServerAPI.Item;
import View.OnClickListener;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import java.util.concurrent.atomic.AtomicReference;
import nucleus.example.base.ServerAPI;
import nucleus.view.NucleusActivity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ MainActivity.class, NucleusActivity.class })
public class MainActivityTest {
    public static final String TEXT = "test string";

    private static final Class BASE_VIEW_CLASS = NucleusActivity.class;

    @Mock
    MainPresenter mainPresenter;

    @Mock
    ArrayAdapter arrayAdapter;

    @Mock
    CheckedTextView check1;

    @Mock
    CheckedTextView check2;

    @Mock
    ListView listView;

    MainActivity activity;

    @Test
    public void testOnCreate() throws Exception {
        activity.onCreate(null);
        Mockito.verify(check1).setText(NAME_1);
        Mockito.verify(check2).setText(NAME_2);
        Mockito.verify(mainPresenter).request(NAME_1);
    }

    @Test
    public void testClicks() throws Exception {
        AtomicReference<View.OnClickListener> click1 = requireOnClick(activity, R.id.check1, check1);
        AtomicReference<View.OnClickListener> click2 = requireOnClick(activity, R.id.check2, check2);
        activity.onCreate(null);
        click1.get().onClick(check1);
        Mockito.verify(mainPresenter, Mockito.atLeastOnce()).request(NAME_1);
        click2.get().onClick(check1);
        Mockito.verify(mainPresenter, Mockito.atLeastOnce()).request(NAME_1);
    }

    @Test
    public void testOnItems() throws Exception {
        MainActivity activity = Mockito.spy(MainActivity.class);
        PowerMockito.whenNew(ArrayAdapter.class).withArguments(activity, item).thenReturn(arrayAdapter);
        activity.onCreate(null);
        ServerAPI[] items = new Item[]{ new ServerAPI.Item(MainActivityTest.TEXT) };
        activity.onItems(items, "");
        InOrder inOrder = Mockito.inOrder(arrayAdapter);
        inOrder.verify(arrayAdapter, Mockito.times(1)).clear();
        inOrder.verify(arrayAdapter, Mockito.times(1)).addAll(items);
    }
}

