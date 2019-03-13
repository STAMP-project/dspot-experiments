package io.github.hidroh.materialistic;


import R.id.content_frame;
import R.string.pref_lazy_load;
import ReadabilityClient.Callback;
import android.preference.PreferenceManager;
import io.github.hidroh.materialistic.data.ReadabilityClient;
import io.github.hidroh.materialistic.test.TestReadabilityActivity;
import io.github.hidroh.materialistic.test.TestRunner;
import javax.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.android.controller.ActivityController;


@RunWith(TestRunner.class)
public class ReadabilityFragmentLazyLoadTest {
    private TestReadabilityActivity activity;

    private ActivityController<TestReadabilityActivity> controller;

    @Inject
    ReadabilityClient readabilityClient;

    private WebFragment fragment;

    @Test
    public void testLazyLoadByDefault() {
        getSupportFragmentManager().beginTransaction().replace(content_frame, fragment, "tag").commit();
        Mockito.verify(readabilityClient, Mockito.never()).parse(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(Callback.class));
        Mockito.reset(readabilityClient);
        fragment.loadNow();
        Mockito.verify(readabilityClient).parse(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(Callback.class));
    }

    @Test
    public void testVisible() {
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean(activity.getString(pref_lazy_load), false).apply();
        fragment.setUserVisibleHint(true);
        Mockito.verify(readabilityClient, Mockito.never()).parse(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(Callback.class));
        Mockito.reset(readabilityClient);
        getSupportFragmentManager().beginTransaction().replace(content_frame, fragment, "tag").commit();
        Mockito.verify(readabilityClient).parse(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(Callback.class));
    }
}

