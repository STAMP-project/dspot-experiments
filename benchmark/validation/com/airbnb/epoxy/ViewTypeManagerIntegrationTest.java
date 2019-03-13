package com.airbnb.epoxy;


import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;


@Config(sdk = 21, manifest = TestRunner.MANIFEST_PATH)
@RunWith(TestRunner.class)
public class ViewTypeManagerIntegrationTest {
    static class TestModel extends EpoxyModelWithView<View> {
        @Override
        protected View buildView(@NonNull
        ViewGroup parent) {
            return new android.widget.FrameLayout(RuntimeEnvironment.application);
        }
    }

    static class ModelWithViewType extends ViewTypeManagerIntegrationTest.TestModel {
        @Override
        protected int getViewType() {
            return 1;
        }
    }

    static class ModelWithViewType2 extends ViewTypeManagerIntegrationTest.TestModel {
        @Override
        protected int getViewType() {
            return 2;
        }
    }

    @Test
    public void modelWithLayout() {
        SimpleEpoxyAdapter adapter = new SimpleEpoxyAdapter();
        adapter.addModel(new ViewTypeManagerIntegrationTest.ModelWithViewType());
        adapter.addModel(new ViewTypeManagerIntegrationTest.ModelWithViewType2());
        // The view type should be the value declared in the model
        Assert.assertEquals(1, adapter.getItemViewType(0));
        Assert.assertEquals(2, adapter.getItemViewType(1));
    }

    static class ModelWithoutViewType extends ViewTypeManagerIntegrationTest.TestModel {}

    static class ModelWithoutViewType2 extends ViewTypeManagerIntegrationTest.TestModel {}

    static class ModelWithoutViewType3 extends ViewTypeManagerIntegrationTest.TestModel {}

    @Test
    public void modelsWithoutLayoutHaveViewTypesGenerated() {
        SimpleEpoxyAdapter adapter = new SimpleEpoxyAdapter();
        adapter.addModel(new ViewTypeManagerIntegrationTest.ModelWithoutViewType());
        adapter.addModel(new ViewTypeManagerIntegrationTest.ModelWithoutViewType2());
        adapter.addModel(new ViewTypeManagerIntegrationTest.ModelWithoutViewType3());
        adapter.addModel(new ViewTypeManagerIntegrationTest.ModelWithoutViewType());
        adapter.addModel(new ViewTypeManagerIntegrationTest.ModelWithoutViewType2());
        adapter.addModel(new ViewTypeManagerIntegrationTest.ModelWithoutViewType3());
        // Models with view type 0 should have a view type generated for them
        Assert.assertEquals((-1), adapter.getItemViewType(0));
        Assert.assertEquals((-2), adapter.getItemViewType(1));
        Assert.assertEquals((-3), adapter.getItemViewType(2));
        // Models of same class should share the same generated view type
        Assert.assertEquals((-1), adapter.getItemViewType(3));
        Assert.assertEquals((-2), adapter.getItemViewType(4));
        Assert.assertEquals((-3), adapter.getItemViewType(5));
    }

    @Test
    public void fastModelLookupOfLastModel() {
        SimpleEpoxyAdapter adapter = Mockito.spy(new SimpleEpoxyAdapter());
        ViewTypeManagerIntegrationTest.TestModel modelToAdd = Mockito.spy(new ViewTypeManagerIntegrationTest.ModelWithoutViewType());
        adapter.addModel(modelToAdd);
        int itemViewType = adapter.getItemViewType(0);
        adapter.onCreateViewHolder(null, itemViewType);
        // onExceptionSwallowed is called if the fast model look up failed
        Mockito.verify(adapter, Mockito.never()).onExceptionSwallowed(ArgumentMatchers.any(RuntimeException.class));
        Mockito.verify(modelToAdd).buildView(null);
    }

    @Test
    public void fallbackLookupOfUnknownModel() {
        SimpleEpoxyAdapter adapter = Mockito.spy(new SimpleEpoxyAdapter());
        ViewTypeManagerIntegrationTest.TestModel modelToAdd = Mockito.spy(new ViewTypeManagerIntegrationTest.ModelWithViewType());
        adapter.addModel(modelToAdd);
        // If we pass a view type that hasn't been looked up recently it should fallback to searching
        // through all models to find a match.
        adapter.onCreateViewHolder(null, 1);
        // onExceptionSwallowed is called when the fast model look up fails
        Mockito.verify(adapter).onExceptionSwallowed(ArgumentMatchers.any(RuntimeException.class));
        Mockito.verify(modelToAdd).buildView(null);
    }

    @Test
    public void viewTypesSharedAcrossAdapters() {
        SimpleEpoxyAdapter adapter1 = new SimpleEpoxyAdapter();
        SimpleEpoxyAdapter adapter2 = new SimpleEpoxyAdapter();
        adapter1.addModel(new ViewTypeManagerIntegrationTest.ModelWithoutViewType());
        adapter1.addModel(new ViewTypeManagerIntegrationTest.ModelWithoutViewType2());
        adapter2.addModel(new ViewTypeManagerIntegrationTest.ModelWithoutViewType());
        adapter2.addModel(new ViewTypeManagerIntegrationTest.ModelWithoutViewType2());
        Assert.assertEquals(adapter1.getItemViewType(0), adapter2.getItemViewType(0));
        Assert.assertEquals(adapter1.getItemViewType(1), adapter2.getItemViewType(1));
    }
}

