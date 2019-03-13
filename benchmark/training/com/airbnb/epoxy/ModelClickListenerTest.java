package com.airbnb.epoxy;


import android.view.View;
import android.view.View.OnClickListener;
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver;
import com.airbnb.epoxy.integrationtest.BuildConfig;
import com.airbnb.epoxy.integrationtest.ModelWithClickListener_;
import com.airbnb.epoxy.integrationtest.ModelWithLongClickListener_;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ModelClickListenerTest {
    private ControllerLifecycleHelper lifecycleHelper = new ControllerLifecycleHelper();

    static class TestController extends EpoxyController {
        private EpoxyModel<?> model;

        @Override
        protected void buildModels() {
            add(model.id(1));
        }

        void setModel(EpoxyModel<?> model) {
            this.model = model;
        }
    }

    static class ModelClickListener implements OnModelClickListener<ModelWithClickListener_, View> {
        boolean clicked;

        @Override
        public void onClick(ModelWithClickListener_ model, View view, View v, int position) {
            clicked = true;
        }
    }

    static class ModelLongClickListener implements OnModelLongClickListener<ModelWithLongClickListener_, View> {
        boolean clicked;

        @Override
        public boolean onLongClick(ModelWithLongClickListener_ model, View view, View v, int position) {
            clicked = true;
            return true;
        }
    }

    static class ViewClickListener implements OnClickListener {
        boolean clicked;

        @Override
        public void onClick(View v) {
            clicked = true;
        }
    }

    @Test
    public void basicModelClickListener() {
        final ModelWithClickListener_ model = new ModelWithClickListener_();
        ModelClickListenerTest.ModelClickListener modelClickListener = Mockito.spy(new ModelClickListenerTest.ModelClickListener());
        model.clickListener(modelClickListener);
        ModelClickListenerTest.TestController controller = new ModelClickListenerTest.TestController();
        controller.setModel(model);
        lifecycleHelper.buildModelsAndBind(controller);
        View viewMock = mockModelForClicking(model);
        model.clickListener().onClick(viewMock);
        Assert.assertTrue(modelClickListener.clicked);
        Mockito.verify(modelClickListener).onClick(ArgumentMatchers.eq(model), ArgumentMatchers.any(View.class), ArgumentMatchers.eq(viewMock), ArgumentMatchers.eq(1));
    }

    @Test
    public void basicModelLongClickListener() {
        final ModelWithLongClickListener_ model = new ModelWithLongClickListener_();
        ModelClickListenerTest.ModelLongClickListener modelClickListener = Mockito.spy(new ModelClickListenerTest.ModelLongClickListener());
        model.clickListener(modelClickListener);
        ModelClickListenerTest.TestController controller = new ModelClickListenerTest.TestController();
        controller.setModel(model);
        lifecycleHelper.buildModelsAndBind(controller);
        View viewMock = mockModelForClicking(model);
        model.clickListener().onLongClick(viewMock);
        Assert.assertTrue(modelClickListener.clicked);
        Mockito.verify(modelClickListener).onLongClick(ArgumentMatchers.eq(model), ArgumentMatchers.any(View.class), ArgumentMatchers.eq(viewMock), ArgumentMatchers.eq(1));
    }

    @Test
    public void modelClickListenerOverridesViewClickListener() {
        final ModelWithClickListener_ model = new ModelWithClickListener_();
        ModelClickListenerTest.TestController controller = new ModelClickListenerTest.TestController();
        controller.setModel(model);
        ModelClickListenerTest.ViewClickListener viewClickListener = new ModelClickListenerTest.ViewClickListener();
        model.clickListener(viewClickListener);
        Assert.assertNotNull(model.clickListener());
        ModelClickListenerTest.ModelClickListener modelClickListener = new ModelClickListenerTest.ModelClickListener();
        model.clickListener(modelClickListener);
        Assert.assertNotSame(model.clickListener(), viewClickListener);
        lifecycleHelper.buildModelsAndBind(controller);
        mockModelForClicking(model);
        Assert.assertNotNull(model.clickListener());
        View viewMock = mockModelForClicking(model);
        model.clickListener().onClick(viewMock);
        Assert.assertTrue(modelClickListener.clicked);
        Assert.assertFalse(viewClickListener.clicked);
    }

    @Test
    public void viewClickListenerOverridesModelClickListener() {
        final ModelWithClickListener_ model = new ModelWithClickListener_();
        ModelClickListenerTest.TestController controller = new ModelClickListenerTest.TestController();
        controller.setModel(model);
        ModelClickListenerTest.ModelClickListener modelClickListener = new ModelClickListenerTest.ModelClickListener();
        model.clickListener(modelClickListener);
        ModelClickListenerTest.ViewClickListener viewClickListener = new ModelClickListenerTest.ViewClickListener();
        model.clickListener(viewClickListener);
        lifecycleHelper.buildModelsAndBind(controller);
        Assert.assertNotNull(model.clickListener());
        model.clickListener().onClick(null);
        Assert.assertTrue(viewClickListener.clicked);
        Assert.assertFalse(modelClickListener.clicked);
    }

    @Test
    public void resetClearsModelClickListener() {
        final ModelWithClickListener_ model = new ModelWithClickListener_();
        ModelClickListenerTest.TestController controller = new ModelClickListenerTest.TestController();
        controller.setModel(model);
        ModelClickListenerTest.ModelClickListener modelClickListener = Mockito.spy(new ModelClickListenerTest.ModelClickListener());
        model.clickListener(modelClickListener);
        model.reset();
        lifecycleHelper.buildModelsAndBind(controller);
        Assert.assertNull(model.clickListener());
    }

    @Test
    public void modelClickListenerIsDiffed() {
        // Internally we wrap the model click listener with an anonymous click listener. We can't hash
        // the anonymous click listener since that changes the model state, instead our anonymous
        // click listener should use the hashCode of the user's click listener
        ModelClickListenerTest.ModelClickListener modelClickListener = new ModelClickListenerTest.ModelClickListener();
        ModelClickListenerTest.ViewClickListener viewClickListener = new ModelClickListenerTest.ViewClickListener();
        ModelClickListenerTest.TestController controller = new ModelClickListenerTest.TestController();
        AdapterDataObserver observerMock = Mockito.mock(AdapterDataObserver.class);
        getAdapter().registerAdapterDataObserver(observerMock);
        ModelWithClickListener_ model = new ModelWithClickListener_();
        controller.setModel(model);
        requestModelBuild();
        Mockito.verify(observerMock).onItemRangeInserted(ArgumentMatchers.eq(0), ArgumentMatchers.eq(1));
        model = new ModelWithClickListener_();
        model.clickListener(modelClickListener);
        controller.setModel(model);
        lifecycleHelper.buildModelsAndBind(controller);
        // The second update shouldn't cause a item change
        model = new ModelWithClickListener_();
        model.clickListener(modelClickListener);
        controller.setModel(model);
        lifecycleHelper.buildModelsAndBind(controller);
        model = new ModelWithClickListener_();
        model.clickListener(viewClickListener);
        controller.setModel(model);
        lifecycleHelper.buildModelsAndBind(controller);
        Mockito.verify(observerMock, Mockito.times(2)).onItemRangeChanged(ArgumentMatchers.eq(0), ArgumentMatchers.eq(1), ArgumentMatchers.any());
        Mockito.verifyNoMoreInteractions(observerMock);
    }

    @Test
    public void viewClickListenerIsDiffed() {
        ModelClickListenerTest.TestController controller = new ModelClickListenerTest.TestController();
        AdapterDataObserver observerMock = Mockito.mock(AdapterDataObserver.class);
        getAdapter().registerAdapterDataObserver(observerMock);
        ModelWithClickListener_ model = new ModelWithClickListener_();
        controller.setModel(model);
        requestModelBuild();
        Mockito.verify(observerMock).onItemRangeInserted(ArgumentMatchers.eq(0), ArgumentMatchers.eq(1));
        ModelClickListenerTest.ViewClickListener viewClickListener = new ModelClickListenerTest.ViewClickListener();
        model = new ModelWithClickListener_();
        model.clickListener(viewClickListener);
        controller.setModel(model);
        requestModelBuild();
        // The second update shouldn't cause a item change
        model = new ModelWithClickListener_();
        model.clickListener(viewClickListener);
        controller.setModel(model);
        requestModelBuild();
        ModelClickListenerTest.ModelClickListener modelClickListener = new ModelClickListenerTest.ModelClickListener();
        model = new ModelWithClickListener_();
        model.clickListener(modelClickListener);
        controller.setModel(model);
        requestModelBuild();
        Mockito.verify(observerMock, Mockito.times(2)).onItemRangeChanged(ArgumentMatchers.eq(0), ArgumentMatchers.eq(1), ArgumentMatchers.any());
        Mockito.verifyNoMoreInteractions(observerMock);
    }
}

