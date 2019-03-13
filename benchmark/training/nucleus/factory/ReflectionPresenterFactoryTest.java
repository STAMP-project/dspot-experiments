package nucleus.factory;


import android.os.Bundle;
import nucleus.presenter.Presenter;
import org.junit.Assert;
import org.junit.Test;


public class ReflectionPresenterFactoryTest {
    public static class ViewNoPresenter {}

    public static class TestPresenter extends Presenter {
        public int value;

        @Override
        public void onCreate(Bundle savedState) {
            super.onCreate(savedState);
            if (savedState != null)
                value = savedState.getInt("1");

        }

        @Override
        public void onSave(Bundle state) {
            super.onSave(state);
            state.putInt("1", 1);
        }
    }

    @RequiresPresenter(ReflectionPresenterFactoryTest.TestPresenter.class)
    public static class ViewWithPresenter extends Presenter {}

    @Test
    public void testNoPresenter() throws Exception {
        Assert.assertNull(ReflectionPresenterFactory.fromViewClass(ReflectionPresenterFactoryTest.ViewNoPresenter.class));
    }

    @Test
    public void testProvidePresenter() throws Exception {
        Assert.assertNotNull(new ReflectionPresenterFactory(ReflectionPresenterFactoryTest.TestPresenter.class).createPresenter());
        PresenterFactory<Presenter> factory = ReflectionPresenterFactory.fromViewClass(ReflectionPresenterFactoryTest.ViewWithPresenter.class);
        Assert.assertNotNull(factory);
        Assert.assertTrue(((factory.createPresenter()) instanceof ReflectionPresenterFactoryTest.TestPresenter));
    }
}

