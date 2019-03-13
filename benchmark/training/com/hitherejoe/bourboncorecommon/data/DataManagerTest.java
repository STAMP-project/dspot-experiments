package com.hitherejoe.bourboncorecommon.data;


import com.hitherejoe.bourboncorecommon.data.model.Comment;
import com.hitherejoe.bourboncorecommon.data.model.Shot;
import com.hitherejoe.bourboncorecommon.data.remote.BourbonService;
import com.hitherejoe.bourboncorecommon.util.RxSchedulersOverrideRule;
import com.hitherejoe.bourboncorecommon.util.TestDataFactory;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import rx.Single;
import rx.observers.TestSubscriber;


/**
 * Tests for DataManager methods related to retrieving data
 */
@RunWith(MockitoJUnitRunner.class)
public class DataManagerTest {
    @Mock
    BourbonService mMockBourbonService;

    DataManager mDataManager;

    // Must be added to every test class that targets app code that uses RxJava
    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();

    @Test
    public void getShotsCompletesAndEmitsShots() {
        List<Shot> shots = TestDataFactory.makeShots(10);
        stubBourbonServiceGetShots(Single.just(shots));
        TestSubscriber<List<Shot>> testSubscriber = new TestSubscriber();
        mDataManager.getShots(0, 0).subscribe(testSubscriber);
        testSubscriber.assertCompleted();
        testSubscriber.assertValue(shots);
    }

    @Test
    public void getCommentsCompletesAndEmitsComments() {
        List<Comment> comments = TestDataFactory.makeComments(10);
        stubBourbonServiceGetComments(Single.just(comments));
        TestSubscriber<List<Comment>> testSubscriber = new TestSubscriber();
        mDataManager.getComments(TestDataFactory.randomInt(), 0, 0).subscribe(testSubscriber);
        testSubscriber.assertCompleted();
        testSubscriber.assertValue(comments);
    }
}

