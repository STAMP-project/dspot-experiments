package org.robolectric.shadows;


import android.content.AsyncTaskLoader;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;


@RunWith(AndroidJUnit4.class)
public class ShadowAsyncTaskLoaderTest {
    private final List<String> transcript = new ArrayList<>();

    @Test
    public void forceLoad_shouldEnqueueWorkOnSchedulers() {
        forceLoad();
        assertThat(transcript).isEmpty();
        Robolectric.flushBackgroundThreadScheduler();
        assertThat(transcript).containsExactly("loadInBackground");
        transcript.clear();
        Robolectric.flushForegroundThreadScheduler();
        assertThat(transcript).containsExactly("deliverResult 42");
    }

    @Test
    public void forceLoad_multipleLoads() {
        ShadowAsyncTaskLoaderTest.TestLoader testLoader = new ShadowAsyncTaskLoaderTest.TestLoader(42);
        forceLoad();
        assertThat(transcript).isEmpty();
        Robolectric.flushBackgroundThreadScheduler();
        assertThat(transcript).containsExactly("loadInBackground");
        transcript.clear();
        Robolectric.flushForegroundThreadScheduler();
        assertThat(transcript).containsExactly("deliverResult 42");
        testLoader.setData(43);
        transcript.clear();
        forceLoad();
        Robolectric.flushBackgroundThreadScheduler();
        assertThat(transcript).containsExactly("loadInBackground");
        transcript.clear();
        Robolectric.flushForegroundThreadScheduler();
        assertThat(transcript).containsExactly("deliverResult 43");
    }

    public class TestLoader extends AsyncTaskLoader<Integer> {
        private Integer data;

        public TestLoader(Integer data) {
            super(ApplicationProvider.getApplicationContext());
            this.data = data;
        }

        @Override
        public Integer loadInBackground() {
            transcript.add("loadInBackground");
            return data;
        }

        @Override
        public void deliverResult(Integer data) {
            transcript.add(("deliverResult " + (data.toString())));
        }

        public void setData(int newData) {
            this.data = newData;
        }
    }
}

