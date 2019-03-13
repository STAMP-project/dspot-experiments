package com.airbnb.epoxy;


import android.view.View;
import com.airbnb.epoxy.EpoxyModel.SpanSizeOverrideCallback;
import com.airbnb.epoxy.integrationtest.BuildConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class EpoxyModelIntegrationTest {
    static class ModelWithSpanCount extends EpoxyModel<View> {
        @Override
        protected int getDefaultLayout() {
            return 0;
        }

        @Override
        public int getSpanSize(int totalSpanCount, int position, int itemCount) {
            return 6;
        }
    }

    @Test
    public void modelReturnsSpanCount() {
        EpoxyModelIntegrationTest.ModelWithSpanCount model = new EpoxyModelIntegrationTest.ModelWithSpanCount();
        Assert.assertEquals(6, getSpanSizeInternal(0, 0, 0));
    }

    static class ModelWithSpanCountCallback extends EpoxyModel<View> {
        @Override
        protected int getDefaultLayout() {
            return 0;
        }
    }

    @Test
    public void modelReturnsSpanCountFromCallback() {
        EpoxyModelIntegrationTest.ModelWithSpanCountCallback model = new EpoxyModelIntegrationTest.ModelWithSpanCountCallback();
        spanSizeOverride(new SpanSizeOverrideCallback() {
            @Override
            public int getSpanSize(int totalSpanCount, int position, int itemCount) {
                return 7;
            }
        });
        Assert.assertEquals(7, getSpanSizeInternal(0, 0, 0));
    }
}

