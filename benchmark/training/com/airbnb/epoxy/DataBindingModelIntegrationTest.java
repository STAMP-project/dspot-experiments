package com.airbnb.epoxy;


import android.view.View;
import android.view.View.OnClickListener;
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver;
import com.airbnb.epoxy.DataBindingEpoxyModel.DataBindingHolder;
import com.airbnb.epoxy.integrationtest.BuildConfig;
import com.airbnb.epoxy.integrationtest.DatabindingTestBindingModel_;
import com.airbnb.epoxy.integrationtest.ModelWithDataBindingBindingModel_;
import java.util.Collections;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class DataBindingModelIntegrationTest {
    @Test
    public void createDataBindingModel() {
        SimpleEpoxyController controller = new SimpleEpoxyController();
        ModelWithDataBindingBindingModel_ firstModel = new ModelWithDataBindingBindingModel_().stringValue("hello").id(1);
        controller.setModels(Collections.singletonList(firstModel));
        ControllerLifecycleHelper lifecycleHelper = new ControllerLifecycleHelper();
        EpoxyViewHolder viewHolder = lifecycleHelper.createViewHolder(controller.getAdapter(), 0);
        controller.getAdapter().onBindViewHolder(viewHolder, 0);
        DataBindingHolder dataBindingHolder = ((DataBindingHolder) (viewHolder.objectToBind()));
        Assert.assertNotNull(dataBindingHolder.getDataBinding());
        // Check that the requiredText was set on the view
        assertEquals(firstModel.stringValue(), getText());
        ModelWithDataBindingBindingModel_ secondModel = new ModelWithDataBindingBindingModel_().stringValue("hello again").id(1);
        controller.setModels(Collections.singletonList(secondModel));
        List<Object> payloads = DiffPayloadTestUtil.payloadsWithChangedModels(firstModel);
        controller.getAdapter().onBindViewHolder(viewHolder, 0, payloads);
        // Check that the requiredText was updated after the change payload
        assertEquals(secondModel.stringValue(), getText());
    }

    @Test
    public void fullyCreateDataBindingModel() {
        SimpleEpoxyController controller = new SimpleEpoxyController();
        ModelWithDataBindingBindingModel_ firstModel = new ModelWithDataBindingBindingModel_().stringValue("hello").id(1);
        controller.setModels(Collections.singletonList(firstModel));
        ControllerLifecycleHelper lifecycleHelper = new ControllerLifecycleHelper();
        EpoxyViewHolder viewHolder = lifecycleHelper.createViewHolder(controller.getAdapter(), 0);
        controller.getAdapter().onBindViewHolder(viewHolder, 0);
        DataBindingHolder dataBindingHolder = ((DataBindingHolder) (viewHolder.objectToBind()));
        Assert.assertNotNull(dataBindingHolder.getDataBinding());
        // Check that the requiredText was set on the view
        assertEquals(firstModel.stringValue(), getText());
        ModelWithDataBindingBindingModel_ secondModel = new ModelWithDataBindingBindingModel_().stringValue("hello again").id(1);
        controller.setModels(Collections.singletonList(secondModel));
        List<Object> payloads = DiffPayloadTestUtil.payloadsWithChangedModels(firstModel);
        controller.getAdapter().onBindViewHolder(viewHolder, 0, payloads);
        // Check that the requiredText was updated after the change payload
        assertEquals(secondModel.stringValue(), getText());
    }

    @Test
    public void typesWithOutHashCodeAreNotDiffed() {
        SimpleEpoxyController controller = new SimpleEpoxyController();
        AdapterDataObserver observerMock = Mockito.mock(AdapterDataObserver.class);
        controller.getAdapter().registerAdapterDataObserver(observerMock);
        ModelWithDataBindingBindingModel_ firstModel = new ModelWithDataBindingBindingModel_().clickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        }).id(1);
        controller.setModels(Collections.singletonList(firstModel));
        Mockito.verify(observerMock).onItemRangeInserted(0, 1);
        ModelWithDataBindingBindingModel_ secondModel = new ModelWithDataBindingBindingModel_().clickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        }).id(1);
        controller.setModels(Collections.singletonList(secondModel));
        Mockito.verifyNoMoreInteractions(observerMock);
    }

    @Test
    public void typesWithHashCodeAreDiffed() {
        SimpleEpoxyController controller = new SimpleEpoxyController();
        AdapterDataObserver observerMock = Mockito.mock(AdapterDataObserver.class);
        controller.getAdapter().registerAdapterDataObserver(observerMock);
        ModelWithDataBindingBindingModel_ firstModel = new ModelWithDataBindingBindingModel_().stringValue("value1").id(1);
        controller.setModels(Collections.singletonList(firstModel));
        Mockito.verify(observerMock).onItemRangeInserted(0, 1);
        ModelWithDataBindingBindingModel_ secondModel = new ModelWithDataBindingBindingModel_().stringValue("value2").id(1);
        controller.setModels(Collections.singletonList(secondModel));
        Mockito.verify(observerMock).onItemRangeChanged(ArgumentMatchers.eq(0), ArgumentMatchers.eq(1), ArgumentMatchers.any());
        Mockito.verifyNoMoreInteractions(observerMock);
    }

    @Test
    public void generatesBindingModelFromNamingPattern() {
        // Make sure that the model was generated from the annotation naming pattern
        new DatabindingTestBindingModel_();
    }
}

