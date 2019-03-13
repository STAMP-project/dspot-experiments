package nucleus.view;


import android.app.Activity;
import android.os.Bundle;
import mocks.BundleMock;
import nucleus.factory.ReflectionPresenterFactory;
import nucleus.factory.RequiresPresenter;
import nucleus.presenter.Presenter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ NucleusActivityTest.TestView.class, PresenterLifecycleDelegate.class, ReflectionPresenterFactory.class })
public class NucleusActivityTest {
    public static final Class<?> BASE_VIEW_CLASS = Activity.class;

    public static final Class<NucleusActivityTest.TestView> VIEW_CLASS = NucleusActivityTest.TestView.class;

    public static class TestPresenter extends Presenter {}

    @RequiresPresenter(NucleusActivityTest.TestPresenter.class)
    public static class TestView extends NucleusActivity {
        @Override
        protected void onDestroy() {
            super.onDestroy();
        }
    }

    private NucleusActivityTest.TestPresenter mockPresenter;

    private PresenterLifecycleDelegate mockDelegate;

    private ReflectionPresenterFactory mockFactory;

    private NucleusActivityTest.TestView tested;

    @Test
    public void testCreation() throws Exception {
        onCreate(null);
        Assert.assertEquals(mockPresenter, getPresenter());
        PowerMockito.verifyStatic(Mockito.times(1));
        ReflectionPresenterFactory.fromViewClass(ArgumentMatchers.argThat(new ArgumentMatcher<Class<?>>() {
            @Override
            public boolean matches(Object argument) {
                return NucleusActivityTest.TestView.class.isAssignableFrom(((Class) (argument)));
            }
        }));
        getPresenter();
        Mockito.verifyNoMoreInteractions(mockPresenter, mockDelegate, mockFactory);
    }

    @Test
    public void testLifecycle() throws Exception {
        onCreate(null);
        onResume();
        Mockito.verify(mockDelegate, Mockito.times(1)).onResume(tested);
        onPause();
        tested.onDestroy();
        Mockito.verify(mockDelegate, Mockito.times(1)).onDropView();
        Mockito.verify(mockDelegate, Mockito.times(1)).onDestroy(false);
        tested.onSaveInstanceState(BundleMock.mock());
        Mockito.verify(mockDelegate, Mockito.times(1)).onSaveInstanceState();
        onPause();
        tested.onDestroy();
        Mockito.verify(mockDelegate, Mockito.times(2)).onDropView();
        Mockito.verify(mockDelegate, Mockito.times(2)).onDestroy(false);
        Mockito.verifyNoMoreInteractions(mockPresenter, mockDelegate, mockFactory);
    }

    @Test
    public void testSaveRestore() throws Exception {
        Bundle presenterBundle = BundleMock.mock();
        when(mockDelegate.onSaveInstanceState()).thenReturn(presenterBundle);
        onCreate(null);
        Bundle state = BundleMock.mock();
        tested.onSaveInstanceState(state);
        tested = Mockito.spy(NucleusActivityTest.TestView.class);
        tested.onCreate(state);
        Mockito.verify(mockDelegate).onRestoreInstanceState(presenterBundle);
    }

    @Test
    public void testDestroy() throws Exception {
        onCreate(null);
        setUpIsFinishing(true);
        onPause();
        tested.onDestroy();
        Mockito.verify(mockDelegate, Mockito.times(1)).onDropView();
        Mockito.verify(mockDelegate, Mockito.times(1)).onDestroy(true);
    }
}

