package nucleus.presenter;


import Presenter.OnDestroyListener;
import android.os.Bundle;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class PresenterTest {
    static class TestPresenter extends Presenter<Object> {
        ArrayList<Bundle> onCreate = new ArrayList<>();

        ArrayList<Bundle> onSave = new ArrayList<>();

        ArrayList<Object> onTakeView = new ArrayList<>();

        int onDestroy;

        int onDropView;

        @Override
        protected void onCreate(@Nullable
        Bundle savedState) {
            onCreate.add(savedState);
        }

        @Override
        protected void onDestroy() {
            (onDestroy)++;
        }

        @Override
        protected void onSave(Bundle state) {
            onSave.add(state);
        }

        @Override
        protected void onTakeView(Object o) {
            onTakeView.add(o);
        }

        @Override
        protected void onDropView() {
            (onDropView)++;
        }
    }

    @Test
    public void testLifecycle() throws Exception {
        PresenterTest.TestPresenter presenter = new PresenterTest.TestPresenter();
        Bundle bundle = Mockito.mock(Bundle.class);
        presenter.create(bundle);
        Assert.assertEquals(bundle, presenter.onCreate.get(0));
        presenter.save(bundle);
        Assert.assertEquals(bundle, presenter.onSave.get(0));
        Object view = 1;
        takeView(view);
        Assert.assertEquals(view, presenter.onTakeView.get(0));
        dropView();
        Assert.assertEquals(1, presenter.onDropView);
        destroy();
        Assert.assertEquals(1, presenter.onDestroy);
        Assert.assertEquals(1, presenter.onCreate.size());
        Assert.assertEquals(1, presenter.onSave.size());
        Assert.assertEquals(1, presenter.onTakeView.size());
        Assert.assertEquals(1, presenter.onDropView);
        Assert.assertEquals(1, presenter.onDestroy);
    }

    @Test
    public void testOnDestroy() throws Exception {
        Presenter.OnDestroyListener listener = Mockito.mock(OnDestroyListener.class);
        Presenter presenter = new Presenter();
        presenter.create(null);
        presenter.addOnDestroyListener(listener);
        presenter.destroy();
        Mockito.verify(listener, Mockito.times(1)).onDestroy();
        Mockito.verifyNoMoreInteractions(listener);
    }

    @Test
    public void testOnNoDestroy() throws Exception {
        Presenter.OnDestroyListener listener = Mockito.mock(OnDestroyListener.class);
        Presenter presenter = new Presenter();
        presenter.create(null);
        presenter.addOnDestroyListener(listener);
        presenter.removeOnDestroyListener(listener);
        presenter.destroy();
        Mockito.verifyNoMoreInteractions(listener);
    }
}

