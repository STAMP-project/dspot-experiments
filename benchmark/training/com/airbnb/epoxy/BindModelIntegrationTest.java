package com.airbnb.epoxy;


import android.widget.TextView;
import com.airbnb.epoxy.integrationtest.BuildConfig;
import com.airbnb.epoxy.integrationtest.Model_;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class BindModelIntegrationTest {
    private Model_ model;

    class TestAdapter extends BaseEpoxyAdapter {
        private boolean diffPayloadsEnabled;

        private List<EpoxyModel<?>> models;

        TestAdapter(boolean diffPayloadsEnabled) {
            this.diffPayloadsEnabled = diffPayloadsEnabled;
            this.models = new ArrayList();
            models.add(model);
        }

        @Override
        List<EpoxyModel<?>> getCurrentModels() {
            return models;
        }

        @Override
        boolean diffPayloadsEnabled() {
            return diffPayloadsEnabled;
        }
    }

    @Test
    public void bindNoPayloads() {
        BindModelIntegrationTest.TestAdapter adapter = new BindModelIntegrationTest.TestAdapter(false);
        EpoxyViewHolder viewHolder = ControllerLifecycleHelper.createViewHolder(adapter, 0);
        adapter.onBindViewHolder(viewHolder, 0);
        Mockito.verify(model).bind(((TextView) (viewHolder.itemView)));
        Mockito.verify(model, Mockito.never()).bind(ArgumentMatchers.any(TextView.class), ArgumentMatchers.any(List.class));
        Mockito.verify(model, Mockito.never()).bind(ArgumentMatchers.any(TextView.class), ArgumentMatchers.any(EpoxyModel.class));
    }

    @Test
    public void bindWithPayloads() {
        BindModelIntegrationTest.TestAdapter adapter = new BindModelIntegrationTest.TestAdapter(false);
        EpoxyViewHolder viewHolder = ControllerLifecycleHelper.createViewHolder(adapter, 0);
        ArrayList<Object> payloads = new ArrayList<>();
        payloads.add("hello");
        adapter.onBindViewHolder(viewHolder, 0, payloads);
        Mockito.verify(model).bind(((TextView) (viewHolder.itemView)), payloads);
        // This is called if the payloads bind call isn't implemented
        Mockito.verify(model).bind(ArgumentMatchers.any(TextView.class));
        Mockito.verify(model, Mockito.never()).bind(ArgumentMatchers.any(TextView.class), ArgumentMatchers.any(EpoxyModel.class));
    }

    @Test
    public void bindWithDiffPayload() {
        BindModelIntegrationTest.TestAdapter adapter = new BindModelIntegrationTest.TestAdapter(true);
        EpoxyViewHolder viewHolder = ControllerLifecycleHelper.createViewHolder(adapter, 0);
        Model_ originallyBoundModel = new Model_();
        originallyBoundModel.id(model.id());
        List<Object> payloads = DiffPayloadTestUtil.payloadsWithChangedModels(originallyBoundModel);
        adapter.onBindViewHolder(viewHolder, 0, payloads);
        Mockito.verify(model).bind(((TextView) (viewHolder.itemView)), originallyBoundModel);
        // This is called if the payloads bind call isn't implemented
        Mockito.verify(model).bind(ArgumentMatchers.any(TextView.class));
        Mockito.verify(model, Mockito.never()).bind(ArgumentMatchers.any(TextView.class), ArgumentMatchers.any(List.class));
    }
}

