package io.github.hidroh.materialistic;


import ItemManager.ASK_FETCH_MODE;
import ItemManager.JOBS_FETCH_MODE;
import ItemManager.NEW_FETCH_MODE;
import ItemManager.SHOW_FETCH_MODE;
import R.string.title_activity_ask;
import R.string.title_activity_jobs;
import R.string.title_activity_new;
import R.string.title_activity_show;
import io.github.hidroh.materialistic.test.TestRunner;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;


@RunWith(TestRunner.class)
public class StoriesActivityTest {
    @Test
    public void testShowActivity() {
        ActivityController<ShowActivity> controller = Robolectric.buildActivity(ShowActivity.class);
        ShowActivity activity = controller.create().start().resume().get();
        Assert.assertEquals(activity.getString(title_activity_show), activity.getDefaultTitle());
        Assert.assertEquals(SHOW_FETCH_MODE, activity.getFetchMode());
        controller.pause().stop().destroy();
    }

    @Test
    public void testNewActivity() {
        ActivityController<NewActivity> controller = Robolectric.buildActivity(NewActivity.class);
        NewActivity activity = controller.create().start().resume().get();
        Assert.assertEquals(activity.getString(title_activity_new), activity.getDefaultTitle());
        Assert.assertEquals(NEW_FETCH_MODE, activity.getFetchMode());
        controller.pause().stop().destroy();
    }

    @Test
    public void testAskActivity() {
        ActivityController<AskActivity> controller = Robolectric.buildActivity(AskActivity.class);
        AskActivity activity = controller.create().start().resume().get();
        Assert.assertEquals(activity.getString(title_activity_ask), activity.getDefaultTitle());
        Assert.assertEquals(ASK_FETCH_MODE, activity.getFetchMode());
        controller.pause().stop().destroy();
    }

    @Test
    public void testJobsActivity() {
        ActivityController<JobsActivity> controller = Robolectric.buildActivity(JobsActivity.class);
        JobsActivity activity = controller.create().start().resume().get();
        Assert.assertEquals(activity.getString(title_activity_jobs), activity.getDefaultTitle());
        Assert.assertEquals(JOBS_FETCH_MODE, activity.getFetchMode());
        controller.pause().stop().destroy();
    }
}

