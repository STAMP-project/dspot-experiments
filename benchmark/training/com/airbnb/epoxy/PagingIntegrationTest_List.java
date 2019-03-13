package com.airbnb.epoxy;


import androidx.paging.PagedList;
import com.airbnb.epoxy.integrationtest.BuildConfig;
import com.airbnb.epoxy.integrationtest.Model_;
import com.airbnb.epoxy.paging.PagingEpoxyController;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class PagingIntegrationTest_List {
    private PagingIntegrationTest_List.Controller controller;

    private static class Controller extends PagingEpoxyController<Integer> {
        public void setListWithSize(int size) {
            ArrayList<Integer> list = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                list.add((i + 1));
            }
            setList(list);
        }

        @Override
        protected void buildModels(List<Integer> list) {
            for (Integer position : list) {
                add(new Model_().id(position));
            }
        }
    }

    @Test
    public void initialPageBind() {
        controller.setConfig(new PagedList.Config.Builder().setEnablePlaceholders(false).setPageSize(100).setInitialLoadSizeHint(100).build());
        controller.setListWithSize(500);
        List<EpoxyModel<?>> models = getAdapter().getCopyOfModels();
        Assert.assertEquals(100, models.size());
        Assert.assertEquals(1, models.get(0).id());
    }
}

