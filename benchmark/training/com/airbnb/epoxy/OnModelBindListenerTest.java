package com.airbnb.epoxy;


import android.view.View;
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver;
import com.airbnb.epoxy.integrationtest.BuildConfig;
import com.airbnb.epoxy.integrationtest.ModelWithClickListener_;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class OnModelBindListenerTest {
    private ControllerLifecycleHelper lifecycleHelper = new ControllerLifecycleHelper();

    static class TestController extends EpoxyController {
        private EpoxyModel model;

        @Override
        protected void buildModels() {
            add(model);
        }

        void setModel(EpoxyModel model) {
            this.model = model.id(1);
        }

        void buildWithModel(EpoxyModel model) {
            setModel(model);
            requestModelBuild();
        }
    }

    static class BindListener implements OnModelBoundListener<ModelWithClickListener_, View> {
        boolean called;

        @Override
        public void onModelBound(ModelWithClickListener_ model, View view, int position) {
            called = true;
        }
    }

    static class UnbindListener implements OnModelUnboundListener<ModelWithClickListener_, View> {
        boolean called;

        @Override
        public void onModelUnbound(ModelWithClickListener_ model, View view) {
            called = true;
        }
    }

    @Test
    public void onBindListenerGetsCalled() {
        OnModelBindListenerTest.TestController controller = new OnModelBindListenerTest.TestController();
        OnModelBindListenerTest.BindListener bindListener = new OnModelBindListenerTest.BindListener();
        ModelWithClickListener_ model = new ModelWithClickListener_().onBind(bindListener);
        controller.setModel(model);
        Assert.assertFalse(bindListener.called);
        lifecycleHelper.buildModelsAndBind(controller);
        Assert.assertTrue(bindListener.called);
    }

    @Test
    public void onUnbindListenerGetsCalled() {
        OnModelBindListenerTest.TestController controller = new OnModelBindListenerTest.TestController();
        ModelWithClickListener_ model = new ModelWithClickListener_();
        controller.setModel(model);
        OnModelBindListenerTest.UnbindListener unbindlistener = new OnModelBindListenerTest.UnbindListener();
        model.onUnbind(unbindlistener);
        Assert.assertFalse(unbindlistener.called);
        lifecycleHelper.buildModelsAndBind(controller);
        Assert.assertFalse(unbindlistener.called);
        lifecycleHelper.recycleLastBoundModel(controller);
        Assert.assertTrue(unbindlistener.called);
    }

    @Test
    public void bindListenerChangesHashCode() {
        OnModelBindListenerTest.TestController controller = new OnModelBindListenerTest.TestController();
        AdapterDataObserver observerMock = Mockito.mock(AdapterDataObserver.class);
        getAdapter().registerAdapterDataObserver(observerMock);
        ModelWithClickListener_ model = new ModelWithClickListener_();
        controller.buildWithModel(model);
        Mockito.verify(observerMock).onItemRangeInserted(ArgumentMatchers.eq(0), ArgumentMatchers.eq(1));
        // shouldn't change
        model = new ModelWithClickListener_();
        model.onBind(null);
        controller.buildWithModel(model);
        Mockito.verify(observerMock, Mockito.never()).onItemRangeChanged(ArgumentMatchers.eq(0), ArgumentMatchers.eq(1), ArgumentMatchers.any());
        model = new ModelWithClickListener_();
        OnModelBindListenerTest.BindListener listener1 = new OnModelBindListenerTest.BindListener();
        model.onBind(listener1);
        controller.buildWithModel(model);
        Mockito.verify(observerMock, Mockito.times(1)).onItemRangeChanged(ArgumentMatchers.eq(0), ArgumentMatchers.eq(1), ArgumentMatchers.any());
        model = new ModelWithClickListener_();
        model.onBind(listener1);
        controller.buildWithModel(model);
        Mockito.verify(observerMock, Mockito.times(1)).onItemRangeChanged(ArgumentMatchers.eq(0), ArgumentMatchers.eq(1), ArgumentMatchers.any());
    }

    @Test
    public void nullBindListenerChangesHashCode() {
        OnModelBindListenerTest.TestController controller = new OnModelBindListenerTest.TestController();
        AdapterDataObserver observerMock = Mockito.mock(AdapterDataObserver.class);
        getAdapter().registerAdapterDataObserver(observerMock);
        ModelWithClickListener_ model = new ModelWithClickListener_();
        controller.buildWithModel(model);
        Mockito.verify(observerMock).onItemRangeInserted(ArgumentMatchers.eq(0), ArgumentMatchers.eq(1));
        model = new ModelWithClickListener_();
        model.onBind(new OnModelBindListenerTest.BindListener());
        controller.buildWithModel(model);
        model = new ModelWithClickListener_();
        model.onBind(null);
        controller.buildWithModel(model);
        Mockito.verify(observerMock, Mockito.times(2)).onItemRangeChanged(ArgumentMatchers.eq(0), ArgumentMatchers.eq(1), ArgumentMatchers.any());
    }

    @Test
    public void newBindListenerDoesNotChangeHashCode() {
        OnModelBindListenerTest.TestController controller = new OnModelBindListenerTest.TestController();
        AdapterDataObserver observerMock = Mockito.mock(AdapterDataObserver.class);
        getAdapter().registerAdapterDataObserver(observerMock);
        ModelWithClickListener_ model = new ModelWithClickListener_();
        controller.buildWithModel(model);
        Mockito.verify(observerMock).onItemRangeInserted(ArgumentMatchers.eq(0), ArgumentMatchers.eq(1));
        model = new ModelWithClickListener_();
        model.onBind(new OnModelBindListenerTest.BindListener());
        controller.buildWithModel(model);
        model = new ModelWithClickListener_();
        model.onBind(new OnModelBindListenerTest.BindListener());
        controller.buildWithModel(model);
        Mockito.verify(observerMock).onItemRangeChanged(ArgumentMatchers.eq(0), ArgumentMatchers.eq(1), ArgumentMatchers.any());
    }

    @Test
    public void unbindListenerChangesHashCode() {
        OnModelBindListenerTest.TestController controller = new OnModelBindListenerTest.TestController();
        AdapterDataObserver observerMock = Mockito.mock(AdapterDataObserver.class);
        getAdapter().registerAdapterDataObserver(observerMock);
        ModelWithClickListener_ model = new ModelWithClickListener_();
        controller.buildWithModel(model);
        Mockito.verify(observerMock).onItemRangeInserted(ArgumentMatchers.eq(0), ArgumentMatchers.eq(1));
        // shouldn't change
        model = new ModelWithClickListener_();
        model.onUnbind(null);
        controller.buildWithModel(model);
        Mockito.verify(observerMock, Mockito.never()).onItemRangeChanged(ArgumentMatchers.eq(0), ArgumentMatchers.eq(1), ArgumentMatchers.any());
        model = new ModelWithClickListener_();
        OnModelBindListenerTest.UnbindListener listener1 = new OnModelBindListenerTest.UnbindListener();
        model.onUnbind(listener1);
        controller.buildWithModel(model);
        Mockito.verify(observerMock, Mockito.times(1)).onItemRangeChanged(ArgumentMatchers.eq(0), ArgumentMatchers.eq(1), ArgumentMatchers.any());
        model = new ModelWithClickListener_();
        model.onUnbind(listener1);
        controller.buildWithModel(model);
        Mockito.verify(observerMock, Mockito.times(1)).onItemRangeChanged(ArgumentMatchers.eq(0), ArgumentMatchers.eq(1), ArgumentMatchers.any());
    }

    @Test
    public void nullUnbindListenerChangesHashCode() {
        OnModelBindListenerTest.TestController controller = new OnModelBindListenerTest.TestController();
        AdapterDataObserver observerMock = Mockito.mock(AdapterDataObserver.class);
        getAdapter().registerAdapterDataObserver(observerMock);
        ModelWithClickListener_ model = new ModelWithClickListener_();
        controller.buildWithModel(model);
        Mockito.verify(observerMock).onItemRangeInserted(ArgumentMatchers.eq(0), ArgumentMatchers.eq(1));
        model = new ModelWithClickListener_();
        model.onUnbind(new OnModelBindListenerTest.UnbindListener());
        controller.buildWithModel(model);
        model = new ModelWithClickListener_();
        model.onUnbind(null);
        controller.buildWithModel(model);
        Mockito.verify(observerMock, Mockito.times(2)).onItemRangeChanged(ArgumentMatchers.eq(0), ArgumentMatchers.eq(1), ArgumentMatchers.any());
    }

    @Test
    public void newUnbindListenerDoesNotChangHashCode() {
        OnModelBindListenerTest.TestController controller = new OnModelBindListenerTest.TestController();
        AdapterDataObserver observerMock = Mockito.mock(AdapterDataObserver.class);
        getAdapter().registerAdapterDataObserver(observerMock);
        ModelWithClickListener_ model = new ModelWithClickListener_();
        controller.buildWithModel(model);
        Mockito.verify(observerMock).onItemRangeInserted(ArgumentMatchers.eq(0), ArgumentMatchers.eq(1));
        model = new ModelWithClickListener_();
        model.onUnbind(new OnModelBindListenerTest.UnbindListener());
        controller.buildWithModel(model);
        model = new ModelWithClickListener_();
        model.onUnbind(new OnModelBindListenerTest.UnbindListener());
        controller.buildWithModel(model);
        Mockito.verify(observerMock).onItemRangeChanged(ArgumentMatchers.eq(0), ArgumentMatchers.eq(1), ArgumentMatchers.any());
    }
}

