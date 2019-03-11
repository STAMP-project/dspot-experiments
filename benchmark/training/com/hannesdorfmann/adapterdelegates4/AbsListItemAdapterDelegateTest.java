package com.hannesdorfmann.adapterdelegates4;


import RecyclerView.ViewHolder;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Hannes Dorfmann
 */
public class AbsListItemAdapterDelegateTest {
    @Test
    public void invokeMethods() {
        List<AbsListItemAdapterDelegateTest.Animal> items = new ArrayList<>();
        items.add(new AbsListItemAdapterDelegateTest.Cat());
        AbsListItemAdapterDelegateTest.CatAbsListItemAdapterDelegate delegate = new AbsListItemAdapterDelegateTest.CatAbsListItemAdapterDelegate();
        delegate.isForViewType(items, 0);
        Assert.assertTrue(delegate.isForViewTypeCalled);
        ViewGroup parent = Mockito.mock(ViewGroup.class);
        AbsListItemAdapterDelegateTest.CatViewHolder vh = delegate.onCreateViewHolder(parent);
        Assert.assertTrue(delegate.onCreateViewHolderCalled);
        delegate.onBindViewHolder(items, 0, vh, new ArrayList<Object>());
        Assert.assertTrue(delegate.onBindViewHolderCalled);
    }

    interface Animal {}

    class Cat implements AbsListItemAdapterDelegateTest.Animal {}

    class CatViewHolder extends RecyclerView.ViewHolder {
        public CatViewHolder(View itemView) {
            super(itemView);
        }
    }

    class CatAbsListItemAdapterDelegate extends AbsListItemAdapterDelegate<AbsListItemAdapterDelegateTest.Cat, AbsListItemAdapterDelegateTest.Animal, AbsListItemAdapterDelegateTest.CatViewHolder> {
        public boolean isForViewTypeCalled = false;

        public boolean onCreateViewHolderCalled = false;

        public boolean onBindViewHolderCalled = false;

        public boolean onViewDetachedFromWindow = false;

        @Override
        protected boolean isForViewType(@NonNull
        AbsListItemAdapterDelegateTest.Animal item, @NonNull
        List<AbsListItemAdapterDelegateTest.Animal> items, int position) {
            isForViewTypeCalled = true;
            return false;
        }

        @NonNull
        @Override
        public AbsListItemAdapterDelegateTest.CatViewHolder onCreateViewHolder(@NonNull
        ViewGroup parent) {
            onCreateViewHolderCalled = true;
            return new AbsListItemAdapterDelegateTest.CatViewHolder(Mockito.mock(View.class));
        }

        @Override
        protected void onBindViewHolder(@NonNull
        AbsListItemAdapterDelegateTest.Cat item, @NonNull
        AbsListItemAdapterDelegateTest.CatViewHolder holder, @NonNull
        List payloads) {
            onBindViewHolderCalled = true;
        }

        @Override
        public void onViewDetachedFromWindow(@NonNull
        RecyclerView.ViewHolder holder) {
            super.onViewDetachedFromWindow(holder);
            onViewDetachedFromWindow = true;
        }
    }
}

