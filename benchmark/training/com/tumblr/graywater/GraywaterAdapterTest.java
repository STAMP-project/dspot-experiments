package com.tumblr.graywater;


import GraywaterAdapter.Binder;
import GraywaterAdapter.BinderResult;
import RecyclerView.ViewHolder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Provider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class GraywaterAdapterTest {
    private abstract static class TestBinder<U, V extends ViewHolder, W extends V> implements Binder<U, V, W> {}

    private static class TestAdapter extends GraywaterAdapter<Object, RecyclerView.ViewHolder, GraywaterAdapterTest.TestBinder<?, RecyclerView.ViewHolder, ? extends RecyclerView.ViewHolder>, Class<?>> {
        @Override
        protected Class<?> getModelType(final Object model) {
            Class<?> modelType = model.getClass();
            // Types are messy.
            final ItemBinder itemBinder = mItemBinderMap.get(modelType);
            final Class<?> declaringClass = modelType.getDeclaringClass();
            if ((itemBinder == null) && (declaringClass != null)) {
                modelType = declaringClass;
            }
            return modelType;
        }

        private static class TextViewHolder extends RecyclerView.ViewHolder {
            TextViewHolder(final View itemView) {
                super(itemView);
            }
        }

        private static class ImageViewHolder extends RecyclerView.ViewHolder {
            ImageViewHolder(final View itemView) {
                super(itemView);
            }
        }

        private static class TextViewHolderCreator implements GraywaterAdapter.ViewHolderCreator {
            static final int VIEW_TYPE = 1;

            @Override
            public GraywaterAdapterTest.TestAdapter.TextViewHolder create(final ViewGroup parent) {
                return new GraywaterAdapterTest.TestAdapter.TextViewHolder(new android.widget.TextView(parent.getContext()));
            }

            @Override
            public int getViewType() {
                return GraywaterAdapterTest.TestAdapter.TextViewHolderCreator.VIEW_TYPE;
            }
        }

        private static class ImageViewHolderCreator implements GraywaterAdapter.ViewHolderCreator {
            static final int VIEW_TYPE = 2;

            @Override
            public GraywaterAdapterTest.TestAdapter.ImageViewHolder create(final ViewGroup parent) {
                return new GraywaterAdapterTest.TestAdapter.ImageViewHolder(new ImageView(parent.getContext()));
            }

            @Override
            public int getViewType() {
                return GraywaterAdapterTest.TestAdapter.ImageViewHolderCreator.VIEW_TYPE;
            }
        }

        private static class TextBinder extends GraywaterAdapterTest.TestBinder<String, RecyclerView.ViewHolder, GraywaterAdapterTest.TestAdapter.TextViewHolder> {
            @Override
            public int getViewType(@NonNull
            final String model) {
                return GraywaterAdapterTest.TestAdapter.TextViewHolderCreator.VIEW_TYPE;
            }

            @Override
            public void prepare(@NonNull
            final String model, final List<Provider<Binder<? super String, RecyclerView.ViewHolder, ? extends RecyclerView.ViewHolder>>> binderList, final int binderIndex) {
            }

            @Override
            public void bind(@NonNull
            final String model, @NonNull
            final GraywaterAdapterTest.TestAdapter.TextViewHolder holder, @NonNull
            final List<Provider<Binder<? super String, RecyclerView.ViewHolder, ? extends RecyclerView.ViewHolder>>> binderList, final int binderIndex, @Nullable
            final ActionListener<String, RecyclerView.ViewHolder, GraywaterAdapterTest.TestAdapter.TextViewHolder> actionListener) {
                setText(model);
            }

            @Override
            public void unbind(@NonNull
            final GraywaterAdapterTest.TestAdapter.TextViewHolder holder) {
                setText("");
            }
        }

        private static class ImageBinder extends GraywaterAdapterTest.TestBinder<Uri, RecyclerView.ViewHolder, GraywaterAdapterTest.TestAdapter.ImageViewHolder> {
            @Override
            public int getViewType(@NonNull
            final Uri model) {
                return GraywaterAdapterTest.TestAdapter.ImageViewHolderCreator.VIEW_TYPE;
            }

            @Override
            public void prepare(@NonNull
            final Uri model, final List<Provider<Binder<? super Uri, RecyclerView.ViewHolder, ? extends RecyclerView.ViewHolder>>> binderList, final int binderIndex) {
            }

            @Override
            public void bind(@NonNull
            final Uri model, @NonNull
            final GraywaterAdapterTest.TestAdapter.ImageViewHolder holder, @NonNull
            final List<Provider<Binder<? super Uri, RecyclerView.ViewHolder, ? extends RecyclerView.ViewHolder>>> binderList, final int binderIndex, @Nullable
            final ActionListener<Uri, RecyclerView.ViewHolder, GraywaterAdapterTest.TestAdapter.ImageViewHolder> actionListener) {
                ((ImageView) (holder.itemView)).setImageURI(model);// not a good idea in production ;)

            }

            @Override
            public void unbind(@NonNull
            final GraywaterAdapterTest.TestAdapter.ImageViewHolder holder) {
            }
        }

        public TestAdapter() {
            super();
            register(new GraywaterAdapterTest.TestAdapter.TextViewHolderCreator(), GraywaterAdapterTest.TestAdapter.TextViewHolder.class);
            register(new GraywaterAdapterTest.TestAdapter.ImageViewHolderCreator(), GraywaterAdapterTest.TestAdapter.ImageViewHolder.class);
            final Provider<GraywaterAdapterTest.TestAdapter.TextBinder> textBinder = new Provider<GraywaterAdapterTest.TestAdapter.TextBinder>() {
                @Override
                public GraywaterAdapterTest.TestAdapter.TextBinder get() {
                    return new GraywaterAdapterTest.TestAdapter.TextBinder();
                }
            };
            final Provider<GraywaterAdapterTest.TestAdapter.ImageBinder> imageBinder = new Provider<GraywaterAdapterTest.TestAdapter.ImageBinder>() {
                @Override
                public GraywaterAdapterTest.TestAdapter.ImageBinder get() {
                    return new GraywaterAdapterTest.TestAdapter.ImageBinder();
                }
            };
            register(String.class, new ItemBinder<String, RecyclerView.ViewHolder, GraywaterAdapterTest.TestBinder<String, RecyclerView.ViewHolder, ? extends RecyclerView.ViewHolder>>() {
                @NonNull
                @Override
                public List<Provider<? extends GraywaterAdapterTest.TestBinder<String, RecyclerView.ViewHolder, ? extends RecyclerView.ViewHolder>>> getBinderList(@NonNull
                final String model, final int position) {
                    return new ArrayList<Provider<? extends GraywaterAdapterTest.TestBinder<String, RecyclerView.ViewHolder, ? extends RecyclerView.ViewHolder>>>() {
                        {
                            add(textBinder);
                            add(textBinder);
                        }
                    };
                }
            }, null);
            register(Uri.class, new ItemBinder<Uri, RecyclerView.ViewHolder, GraywaterAdapterTest.TestBinder<Uri, RecyclerView.ViewHolder, ? extends RecyclerView.ViewHolder>>() {
                @NonNull
                @Override
                public List<Provider<? extends GraywaterAdapterTest.TestBinder<Uri, RecyclerView.ViewHolder, ? extends RecyclerView.ViewHolder>>> getBinderList(@NonNull
                final Uri model, final int position) {
                    return new ArrayList<Provider<? extends GraywaterAdapterTest.TestBinder<Uri, RecyclerView.ViewHolder, ? extends RecyclerView.ViewHolder>>>() {
                        {
                            add(imageBinder);
                            add(imageBinder);
                            add(imageBinder);
                        }
                    };
                }
            }, null);
        }
    }

    @Test
    public void testAdd() throws Exception {
        final GraywaterAdapterTest.TestAdapter adapter = new GraywaterAdapterTest.TestAdapter();
        add("one");
        Assert.assertEquals(2, getItemCount());
        add("two");
        Assert.assertEquals(4, getItemCount());
        add("three");
        Assert.assertEquals(6, getItemCount());
    }

    @Test
    public void testRemove() throws Exception {
        final GraywaterAdapterTest.TestAdapter adapter = new GraywaterAdapterTest.TestAdapter();
        add("zero");
        add("one");
        add("two");
        add("three");
        add("four");
        add("five");
        // remove
        remove(0);
        Assert.assertEquals((2 * 5), getItemCount());
        remove(4);
        Assert.assertEquals((2 * 4), getItemCount());
        remove(2);
        Assert.assertEquals((2 * 3), getItemCount());
        // state
        final List<Object> items = getItems();
        Assert.assertEquals(3, items.size());
        Assert.assertEquals("one", items.get(0));
        Assert.assertEquals("two", items.get(1));
        Assert.assertEquals("four", items.get(2));
    }

    @Test
    public void testViewHolderPosition() throws Exception {
        final GraywaterAdapterTest.TestAdapter adapter = new GraywaterAdapterTest.TestAdapter();
        add("zero");
        add("one");
        add("two");
        add("three");
        add("four");
        add("five");
        // ["zero", "zero", "one", "one, ... ]
        final GraywaterAdapter.BinderResult one = computeItemAndBinderIndex(2);
        Assert.assertEquals(1, one.itemPosition);
        Assert.assertEquals(0, one.binderIndex);
        Assert.assertEquals("one", one.item);
        final GraywaterAdapter.BinderResult oneDouble = computeItemAndBinderIndex(3);
        Assert.assertEquals(1, oneDouble.itemPosition);
        Assert.assertEquals(1, oneDouble.binderIndex);
        Assert.assertEquals("one", oneDouble.item);
        final GraywaterAdapter.BinderResult three = computeItemAndBinderIndex(6);
        Assert.assertEquals(3, three.itemPosition);
        Assert.assertEquals(0, three.binderIndex);
        Assert.assertEquals("three", three.item);
        final GraywaterAdapter.BinderResult threeDouble = computeItemAndBinderIndex(7);
        Assert.assertEquals(3, threeDouble.itemPosition);
        Assert.assertEquals(1, threeDouble.binderIndex);
        Assert.assertEquals("three", three.item);
        final GraywaterAdapter.BinderResult zero = computeItemAndBinderIndex(0);
        Assert.assertEquals(0, zero.itemPosition);
        Assert.assertEquals(0, zero.binderIndex);
        Assert.assertEquals("zero", zero.item);
        final GraywaterAdapter.BinderResult five = computeItemAndBinderIndex(11);
        Assert.assertEquals(5, five.itemPosition);
        Assert.assertEquals(1, five.binderIndex);
        Assert.assertEquals("five", five.item);
    }

    @Test
    public void testViewHolderPositionWithRemove() throws Exception {
        final GraywaterAdapterTest.TestAdapter adapter = new GraywaterAdapterTest.TestAdapter();
        add("zero");
        add("one");
        add("two");
        add("three");
        add("four");
        add("five");
        remove(0);
        // ["one", "one, "two", "two", ... ]
        final GraywaterAdapter.BinderResult two = computeItemAndBinderIndex(2);
        Assert.assertEquals(1, two.itemPosition);
        Assert.assertEquals(0, two.binderIndex);
        // four is in position five
        final GraywaterAdapter.BinderResult four = computeItemAndBinderIndex(9);
        Assert.assertEquals(4, four.itemPosition);
        Assert.assertEquals(1, four.binderIndex);
    }

    @Test
    public void testViewHolderPositionWithRemoveThenAdd() throws Exception {
        final GraywaterAdapterTest.TestAdapter adapter = new GraywaterAdapterTest.TestAdapter();
        add("zero");
        add("one");
        add("two");
        add("three");
        add("four");
        add("five");
        remove(0);
        adapter.add(0, "zero", false);
        // ["zero", "zero", "one", "one, ... ]
        final GraywaterAdapter.BinderResult one = computeItemAndBinderIndex(2);
        Assert.assertEquals(1, one.itemPosition);
        Assert.assertEquals(0, one.binderIndex);
        Assert.assertEquals("one", one.item);
        final GraywaterAdapter.BinderResult oneDouble = computeItemAndBinderIndex(3);
        Assert.assertEquals(1, oneDouble.itemPosition);
        Assert.assertEquals(1, oneDouble.binderIndex);
        Assert.assertEquals("one", oneDouble.item);
        final GraywaterAdapter.BinderResult three = computeItemAndBinderIndex(6);
        Assert.assertEquals(3, three.itemPosition);
        Assert.assertEquals(0, three.binderIndex);
        Assert.assertEquals("three", three.item);
        final GraywaterAdapter.BinderResult threeDouble = computeItemAndBinderIndex(7);
        Assert.assertEquals(3, threeDouble.itemPosition);
        Assert.assertEquals(1, threeDouble.binderIndex);
        Assert.assertEquals("three", three.item);
        final GraywaterAdapter.BinderResult zero = computeItemAndBinderIndex(0);
        Assert.assertEquals(0, zero.itemPosition);
        Assert.assertEquals(0, zero.binderIndex);
        Assert.assertEquals("zero", zero.item);
        final GraywaterAdapter.BinderResult five = computeItemAndBinderIndex(11);
        Assert.assertEquals(5, five.itemPosition);
        Assert.assertEquals(1, five.binderIndex);
        Assert.assertEquals("five", five.item);
    }

    @Test
    public void testViewHolderPositionWithRemoveThenAddMiddle() throws Exception {
        final GraywaterAdapterTest.TestAdapter adapter = new GraywaterAdapterTest.TestAdapter();
        add("zero");
        add("one");
        add("two");
        add("three");
        add("four");
        add("five");
        final Object obj = adapter.remove(2);
        Assert.assertEquals("two", obj);
        adapter.add(2, "two", false);
        // ["zero", "zero", "one", "one, ... ]
        final GraywaterAdapter.BinderResult one = computeItemAndBinderIndex(2);
        Assert.assertEquals(1, one.itemPosition);
        Assert.assertEquals(0, one.binderIndex);
        Assert.assertEquals("one", one.item);
        final GraywaterAdapter.BinderResult oneDouble = computeItemAndBinderIndex(3);
        Assert.assertEquals(1, oneDouble.itemPosition);
        Assert.assertEquals(1, oneDouble.binderIndex);
        Assert.assertEquals("one", oneDouble.item);
        final GraywaterAdapter.BinderResult three = computeItemAndBinderIndex(6);
        Assert.assertEquals(3, three.itemPosition);
        Assert.assertEquals(0, three.binderIndex);
        Assert.assertEquals("three", three.item);
        final GraywaterAdapter.BinderResult threeDouble = computeItemAndBinderIndex(7);
        Assert.assertEquals(3, threeDouble.itemPosition);
        Assert.assertEquals(1, threeDouble.binderIndex);
        Assert.assertEquals("three", three.item);
        final GraywaterAdapter.BinderResult zero = computeItemAndBinderIndex(0);
        Assert.assertEquals(0, zero.itemPosition);
        Assert.assertEquals(0, zero.binderIndex);
        Assert.assertEquals("zero", zero.item);
        final GraywaterAdapter.BinderResult five = computeItemAndBinderIndex(11);
        Assert.assertEquals(5, five.itemPosition);
        Assert.assertEquals(1, five.binderIndex);
        Assert.assertEquals("five", five.item);
    }

    @Test
    public void testGetItemViewType() throws Exception {
        final GraywaterAdapterTest.TestAdapter adapter = new GraywaterAdapterTest.TestAdapter();
        // ["https://www.tumblr.com", "https://www.tumblr.com", "https://www.tumblr.com", "one", "one",
        // "http://dreamynomad.com", "http://dreamynomad.com", "http://dreamynomad.com", ...]
        adapter.add(Uri.parse("https://www.tumblr.com"));
        Assert.assertEquals(3, getItemCount());
        add("one");
        Assert.assertEquals(5, getItemCount());
        adapter.add(Uri.parse("http://dreamynomad.com"));
        Assert.assertEquals(8, getItemCount());
        add("three");
        Assert.assertEquals(10, getItemCount());
        adapter.add(Uri.parse("https://google.com"));
        Assert.assertEquals(13, getItemCount());
        add("five");
        Assert.assertEquals(15, getItemCount());
        Assert.assertEquals(GraywaterAdapterTest.TestAdapter.ImageViewHolderCreator.VIEW_TYPE, getItemViewType(0));
        Assert.assertEquals(GraywaterAdapterTest.TestAdapter.ImageViewHolderCreator.VIEW_TYPE, getItemViewType(1));
        Assert.assertEquals(GraywaterAdapterTest.TestAdapter.ImageViewHolderCreator.VIEW_TYPE, getItemViewType(2));
        Assert.assertEquals(GraywaterAdapterTest.TestAdapter.TextViewHolderCreator.VIEW_TYPE, getItemViewType(3));
        Assert.assertEquals(GraywaterAdapterTest.TestAdapter.TextViewHolderCreator.VIEW_TYPE, getItemViewType(4));
        Assert.assertEquals(GraywaterAdapterTest.TestAdapter.ImageViewHolderCreator.VIEW_TYPE, getItemViewType(5));
        Assert.assertEquals(GraywaterAdapterTest.TestAdapter.ImageViewHolderCreator.VIEW_TYPE, getItemViewType(6));
        // ...
        Assert.assertEquals(GraywaterAdapterTest.TestAdapter.ImageViewHolderCreator.VIEW_TYPE, getItemViewType(10));
        Assert.assertEquals(GraywaterAdapterTest.TestAdapter.ImageViewHolderCreator.VIEW_TYPE, getItemViewType(11));
        Assert.assertEquals(GraywaterAdapterTest.TestAdapter.ImageViewHolderCreator.VIEW_TYPE, getItemViewType(12));
        Assert.assertEquals(GraywaterAdapterTest.TestAdapter.TextViewHolderCreator.VIEW_TYPE, getItemViewType(13));
        Assert.assertEquals(GraywaterAdapterTest.TestAdapter.TextViewHolderCreator.VIEW_TYPE, getItemViewType(14));
    }

    @Test
    public void testMultiViewHolderPosition() throws Exception {
        final GraywaterAdapterTest.TestAdapter adapter = new GraywaterAdapterTest.TestAdapter();
        // ["https://www.tumblr.com", "https://www.tumblr.com", "https://www.tumblr.com", "one", "one",
        // "http://dreamynomad.com", "http://dreamynomad.com", "http://dreamynomad.com", ...]
        final Uri tumblrUri = Uri.parse("https://www.tumblr.com");
        adapter.add(tumblrUri);
        Assert.assertEquals(3, getItemCount());
        add("one");
        Assert.assertEquals(5, getItemCount());
        adapter.add(Uri.parse("http://dreamynomad.com"));
        Assert.assertEquals(8, getItemCount());
        add("three");
        Assert.assertEquals(10, getItemCount());
        final Uri googleUri = Uri.parse("https://google.com");
        adapter.add(googleUri);
        Assert.assertEquals(13, getItemCount());
        add("five");
        Assert.assertEquals(15, getItemCount());
        final GraywaterAdapter.BinderResult tumblr = computeItemAndBinderIndex(1);
        Assert.assertEquals(0, tumblr.itemPosition);
        Assert.assertEquals(1, tumblr.binderIndex);
        Assert.assertEquals(tumblrUri, tumblr.item);
        final GraywaterAdapter.BinderResult one = computeItemAndBinderIndex(3);
        Assert.assertEquals(1, one.itemPosition);
        Assert.assertEquals(0, one.binderIndex);
        Assert.assertEquals("one", one.item);
        final GraywaterAdapter.BinderResult google = computeItemAndBinderIndex(12);
        Assert.assertEquals(4, google.itemPosition);
        Assert.assertEquals(2, google.binderIndex);
        Assert.assertEquals(googleUri, google.item);
        final GraywaterAdapter.BinderResult five = computeItemAndBinderIndex(14);
        Assert.assertEquals(5, five.itemPosition);
        Assert.assertEquals(1, five.binderIndex);
        Assert.assertEquals("five", five.item);
    }

    @Test
    public void testMultiViewHolderPositionWithRemoveThenAdd() throws Exception {
        final GraywaterAdapterTest.TestAdapter adapter = new GraywaterAdapterTest.TestAdapter();
        // ["https://www.tumblr.com", "https://www.tumblr.com", "https://www.tumblr.com", "one", "one",
        // "http://dreamynomad.com", "http://dreamynomad.com", "http://dreamynomad.com", ...]
        final Uri tumblrUri = Uri.parse("https://www.tumblr.com");
        adapter.add(tumblrUri);
        Assert.assertEquals(3, getItemCount());
        add("one");
        Assert.assertEquals(5, getItemCount());
        adapter.add(Uri.parse("http://dreamynomad.com"));
        Assert.assertEquals(8, getItemCount());
        add("three");
        Assert.assertEquals(10, getItemCount());
        final Uri googleUri = Uri.parse("https://google.com");
        adapter.add(googleUri);
        Assert.assertEquals(13, getItemCount());
        add("five");
        Assert.assertEquals(15, getItemCount());
        remove(3);
        adapter.add(3, "three", false);
        final GraywaterAdapter.BinderResult tumblr = computeItemAndBinderIndex(1);
        Assert.assertEquals(0, tumblr.itemPosition);
        Assert.assertEquals(1, tumblr.binderIndex);
        Assert.assertEquals(tumblrUri, tumblr.item);
        final GraywaterAdapter.BinderResult one = computeItemAndBinderIndex(3);
        Assert.assertEquals(1, one.itemPosition);
        Assert.assertEquals(0, one.binderIndex);
        Assert.assertEquals("one", one.item);
        final GraywaterAdapter.BinderResult google = computeItemAndBinderIndex(12);
        Assert.assertEquals(4, google.itemPosition);
        Assert.assertEquals(2, google.binderIndex);
        Assert.assertEquals(googleUri, google.item);
        final GraywaterAdapter.BinderResult five = computeItemAndBinderIndex(14);
        Assert.assertEquals(5, five.itemPosition);
        Assert.assertEquals(1, five.binderIndex);
        Assert.assertEquals("five", five.item);
    }

    @Test
    public void testClear() throws Exception {
        final GraywaterAdapterTest.TestAdapter adapter = new GraywaterAdapterTest.TestAdapter();
        // ["https://www.tumblr.com", "https://www.tumblr.com", "https://www.tumblr.com", "one", "one",
        // "http://dreamynomad.com", "http://dreamynomad.com", "http://dreamynomad.com", ...]
        final Uri tumblrUri = Uri.parse("https://www.tumblr.com");
        adapter.add(tumblrUri);
        Assert.assertEquals(3, getItemCount());
        add("one");
        Assert.assertEquals(5, getItemCount());
        adapter.add(Uri.parse("http://dreamynomad.com"));
        Assert.assertEquals(8, getItemCount());
        add("three");
        Assert.assertEquals(10, getItemCount());
        final Uri googleUri = Uri.parse("https://google.com");
        adapter.add(googleUri);
        Assert.assertEquals(13, getItemCount());
        add("five");
        Assert.assertEquals(15, getItemCount());
        clear();
        Assert.assertEquals(0, getItemCount());
    }

    @Test
    public void testClearThenAdd() throws Exception {
        final GraywaterAdapterTest.TestAdapter adapter = new GraywaterAdapterTest.TestAdapter();
        // ["https://www.tumblr.com", "https://www.tumblr.com", "https://www.tumblr.com", "one", "one",
        // "http://dreamynomad.com", "http://dreamynomad.com", "http://dreamynomad.com", ...]
        final Uri tumblrUri = Uri.parse("https://www.tumblr.com");
        adapter.add(tumblrUri);
        Assert.assertEquals(3, getItemCount());
        add("one");
        Assert.assertEquals(5, getItemCount());
        adapter.add(Uri.parse("http://dreamynomad.com"));
        Assert.assertEquals(8, getItemCount());
        add("three");
        Assert.assertEquals(10, getItemCount());
        final Uri googleUri = Uri.parse("https://google.com");
        adapter.add(googleUri);
        Assert.assertEquals(13, getItemCount());
        add("five");
        Assert.assertEquals(15, getItemCount());
        // Clear!
        clear();
        // ["https://www.tumblr.com", "https://www.tumblr.com", "https://www.tumblr.com", "one", "one",
        // "http://dreamynomad.com", "http://dreamynomad.com", "http://dreamynomad.com", ...]
        adapter.add(tumblrUri);
        Assert.assertEquals(3, getItemCount());
        add("one");
        Assert.assertEquals(5, getItemCount());
        adapter.add(Uri.parse("http://dreamynomad.com"));
        Assert.assertEquals(8, getItemCount());
        add("three");
        Assert.assertEquals(10, getItemCount());
        adapter.add(googleUri);
        Assert.assertEquals(13, getItemCount());
        add("five");
        Assert.assertEquals(15, getItemCount());
        final GraywaterAdapter.BinderResult tumblr = computeItemAndBinderIndex(1);
        Assert.assertEquals(0, tumblr.itemPosition);
        Assert.assertEquals(1, tumblr.binderIndex);
        Assert.assertEquals(tumblrUri, tumblr.item);
        final GraywaterAdapter.BinderResult one = computeItemAndBinderIndex(3);
        Assert.assertEquals(1, one.itemPosition);
        Assert.assertEquals(0, one.binderIndex);
        Assert.assertEquals("one", one.item);
        final GraywaterAdapter.BinderResult google = computeItemAndBinderIndex(12);
        Assert.assertEquals(4, google.itemPosition);
        Assert.assertEquals(2, google.binderIndex);
        Assert.assertEquals(googleUri, google.item);
        final GraywaterAdapter.BinderResult five = computeItemAndBinderIndex(14);
        Assert.assertEquals(5, five.itemPosition);
        Assert.assertEquals(1, five.binderIndex);
        Assert.assertEquals("five", five.item);
    }

    @Test
    public void GraywaterAdapter_FirstVHPosition_FoundVHPosition() throws Exception {
        final GraywaterAdapterTest.TestAdapter adapter = new GraywaterAdapterTest.TestAdapter();
        add("Testing");
        final Uri tumblrUri = Uri.parse("https://www.tumblr.com");
        adapter.add(tumblrUri);
        add("Testing");
        final int imageViewHolderPosition = getFirstViewHolderPosition(1, GraywaterAdapterTest.TestAdapter.ImageViewHolder.class);
        Assert.assertEquals(2, imageViewHolderPosition);
    }

    @Test
    public void GraywaterAdapter_FirstVHPosition_DidNotFindVHPosition() throws Exception {
        final GraywaterAdapterTest.TestAdapter adapter = new GraywaterAdapterTest.TestAdapter();
        add("Testing");
        final Uri tumblrUri = Uri.parse("https://www.tumblr.com");
        adapter.add(tumblrUri);
        add("Testing");
        final int imageViewHolderPosition = getFirstViewHolderPosition(0, GraywaterAdapterTest.TestAdapter.ImageViewHolder.class);
        Assert.assertEquals((-1), imageViewHolderPosition);
    }

    @Test
    public void GraywaterAdapter_FirstVHPosition_InvalidItemPosition() throws Exception {
        final GraywaterAdapterTest.TestAdapter adapter = new GraywaterAdapterTest.TestAdapter();
        add("Testing");
        final Uri tumblrUri = Uri.parse("https://www.tumblr.com");
        adapter.add(tumblrUri);
        add("Testing");
        final int imageViewHolderPosition = getFirstViewHolderPosition(-559038737, GraywaterAdapterTest.TestAdapter.ImageViewHolder.class);
        Assert.assertEquals((-1), imageViewHolderPosition);
    }
}

