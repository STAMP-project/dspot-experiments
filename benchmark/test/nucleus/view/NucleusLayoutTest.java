package nucleus.view;


import android.app.Activity;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.widget.FrameLayout;
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
@PrepareForTest({ NucleusLayoutTest.TestView.class, PresenterLifecycleDelegate.class, ReflectionPresenterFactory.class })
public class NucleusLayoutTest {
    public static final Class<?> BASE_VIEW_CLASS = FrameLayout.class;

    public static final Class<NucleusLayoutTest.TestView> VIEW_CLASS = NucleusLayoutTest.TestView.class;

    public static class TestPresenter extends Presenter {}

    @RequiresPresenter(NucleusLayoutTest.TestPresenter.class)
    public static class TestView extends NucleusLayout {
        public TestView() {
            super(null);
        }

        @Override
        public boolean isInEditMode() {
            return false;
        }
    }

    private NucleusLayoutTest.TestPresenter mockPresenter;

    private PresenterLifecycleDelegate mockDelegate;

    private ReflectionPresenterFactory mockFactory;

    private NucleusLayoutTest.TestView tested;

    @Test
    public void testCreation() throws Exception {
        Assert.assertEquals(mockPresenter, getPresenter());
        PowerMockito.verifyStatic(Mockito.times(1));
        ReflectionPresenterFactory.fromViewClass(ArgumentMatchers.argThat(new ArgumentMatcher<Class<?>>() {
            @Override
            public boolean matches(Object argument) {
                return NucleusLayoutTest.TestView.class.isAssignableFrom(((Class) (argument)));
            }
        }));
        getPresenter();
        Mockito.verifyNoMoreInteractions(mockPresenter, mockDelegate, mockFactory);
    }

    @Test
    public void testLifecycle() throws Exception {
        onAttachedToWindow();
        Mockito.verify(mockDelegate, Mockito.times(1)).onResume(tested);
        onDetachedFromWindow();
        Mockito.verify(mockDelegate, Mockito.times(1)).onDropView();
        Mockito.verify(mockDelegate, Mockito.times(1)).onDestroy(false);
        onSaveInstanceState();
        onSaveInstanceState();
        Mockito.verifyNoMoreInteractions(mockPresenter, mockDelegate, mockFactory);
    }

    @Test
    public void testSaveRestore() throws Exception {
        Bundle presenterBundle = BundleMock.mock();
        when(mockDelegate.onSaveInstanceState()).thenReturn(presenterBundle);
        Bundle state = ((Bundle) (onSaveInstanceState()));
        tested = Mockito.spy(NucleusLayoutTest.TestView.class);
        tested.onRestoreInstanceState(state);
        Mockito.verify(mockDelegate).onRestoreInstanceState(presenterBundle);
    }

    @Test
    public void testDestroy() throws Exception {
        setUpIsFinishing(true);
        onDetachedFromWindow();
        Mockito.verify(mockDelegate, Mockito.times(1)).onDestroy(true);
    }

    @Test
    public void getActivityFromContext() throws Exception {
        Activity activity = Mockito.mock(Activity.class);
        stub(method(NucleusLayoutTest.BASE_VIEW_CLASS, "getContext")).toReturn(activity);
        Assert.assertEquals(activity, getActivity());
    }

    @Test
    public void getActivityFromWrappedContext() throws Exception {
        Activity activity = Mockito.mock(Activity.class);
        ContextWrapper wrapper = Mockito.mock(ContextWrapper.class);
        when(wrapper.getBaseContext()).thenReturn(activity);
        ContextWrapper wrapper2 = Mockito.mock(ContextWrapper.class);
        when(wrapper2.getBaseContext()).thenReturn(wrapper);
        stub(method(NucleusLayoutTest.BASE_VIEW_CLASS, "getContext")).toReturn(wrapper2);
        Assert.assertEquals(activity, getActivity());
    }
}

