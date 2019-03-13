package nucleus.view;


import android.app.Fragment;
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
@PrepareForTest({ NucleusFragmentTest.TestView.class, PresenterLifecycleDelegate.class, ReflectionPresenterFactory.class })
public class NucleusFragmentTest {
    public static final Class<?> BASE_VIEW_CLASS = Fragment.class;

    public static final Class<NucleusFragmentTest.TestView> VIEW_CLASS = NucleusFragmentTest.TestView.class;

    public static class TestPresenter extends Presenter {}

    @RequiresPresenter(NucleusFragmentTest.TestPresenter.class)
    public static class TestView extends NucleusFragment {}

    private NucleusFragmentTest.TestPresenter mockPresenter;

    private PresenterLifecycleDelegate mockDelegate;

    private ReflectionPresenterFactory mockFactory;

    private NucleusFragmentTest.TestView tested;

    @Test
    public void testCreation() throws Exception {
        onCreate(null);
        Assert.assertEquals(mockPresenter, getPresenter());
        PowerMockito.verifyStatic(Mockito.times(1));
        ReflectionPresenterFactory.fromViewClass(ArgumentMatchers.argThat(new ArgumentMatcher<Class<?>>() {
            @Override
            public boolean matches(Object argument) {
                return NucleusFragmentTest.TestView.class.isAssignableFrom(((Class) (argument)));
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
        onDestroyView();
        Mockito.verify(mockDelegate, Mockito.times(1)).onDropView();
        tested.onSaveInstanceState(BundleMock.mock());
        Mockito.verify(mockDelegate, Mockito.times(1)).onSaveInstanceState();
        onDestroy();
        Mockito.verify(mockDelegate, Mockito.times(1)).onDestroy(false);
        Mockito.verifyNoMoreInteractions(mockPresenter, mockDelegate, mockFactory);
    }

    @Test
    public void testSaveRestore() throws Exception {
        Bundle presenterBundle = BundleMock.mock();
        when(mockDelegate.onSaveInstanceState()).thenReturn(presenterBundle);
        onCreate(null);
        Bundle state = BundleMock.mock();
        tested.onSaveInstanceState(state);
        tested = Mockito.spy(NucleusFragmentTest.TestView.class);
        tested.onCreate(state);
        Mockito.verify(mockDelegate).onRestoreInstanceState(presenterBundle);
    }

    @Test
    public void testDestroy() throws Exception {
        onCreate(null);
        setUpIsFinishing(true);
        onPause();
        onDestroy();
        Mockito.verify(mockDelegate, Mockito.times(1)).onDestroy(true);
    }
}

