package io.github.hidroh.materialistic;


import ItemManager.MODE_DEFAULT;
import R.id.title;
import R.string.loading_text;
import RecyclerView.ViewHolder;
import android.annotation.SuppressLint;
import io.github.hidroh.materialistic.data.Item;
import io.github.hidroh.materialistic.data.ItemManager;
import io.github.hidroh.materialistic.data.ResponseListener;
import io.github.hidroh.materialistic.test.ListActivity;
import io.github.hidroh.materialistic.test.TestRunner;
import io.github.hidroh.materialistic.test.shadow.ShadowRecyclerViewAdapter;
import javax.inject.Inject;
import javax.inject.Named;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import static ActivityModule.HN;


@Config(shadows = { ShadowRecyclerViewAdapter.class })
@SuppressWarnings("ConstantConditions")
@SuppressLint("WrongViewCast")
@RunWith(TestRunner.class)
public class ListFragmentViewHolderEdgeTest {
    private ActivityController<ListActivity> controller;

    private ViewHolder holder;

    @Inject
    @Named(HN)
    ItemManager itemManager;

    @Captor
    ArgumentCaptor<ResponseListener<Item>> listener;

    @Test
    public void testNullResponse() {
        Mockito.verify(itemManager).getItem(ArgumentMatchers.any(), ArgumentMatchers.eq(MODE_DEFAULT), listener.capture());
        listener.getValue().onResponse(null);
        assertThat(((android.widget.TextView) (holder.itemView.findViewById(title)))).hasText(loading_text);
    }

    @Test
    public void testErrorResponse() {
        Mockito.verify(itemManager).getItem(ArgumentMatchers.any(), ArgumentMatchers.eq(MODE_DEFAULT), listener.capture());
        listener.getValue().onError(null);
        assertThat(((android.widget.TextView) (holder.itemView.findViewById(title)))).hasText(loading_text);
    }
}

