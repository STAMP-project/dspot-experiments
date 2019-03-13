package com.kickstarter.viewmodels;


import DeepLinkViewModel.ViewModel;
import KoalaEvent.CONTINUE_USER_ACTIVITY;
import KoalaEvent.OPENED_DEEP_LINK;
import android.content.Intent;
import android.net.Uri;
import com.kickstarter.KSRobolectricTestCase;
import java.util.List;
import org.junit.Test;
import rx.observers.TestSubscriber;


public class DeepLinkViewModelTest extends KSRobolectricTestCase {
    private ViewModel vm;

    private final TestSubscriber<Void> requestPackageManager = new TestSubscriber();

    private final TestSubscriber<List<Intent>> startBrowser = new TestSubscriber();

    private final TestSubscriber<Void> startDiscoveryActivity = new TestSubscriber();

    private final TestSubscriber<Uri> startProjectActivity = new TestSubscriber();

    @Test
    public void testNonDeepLink_startsBrowser() {
        setUpEnvironment();
        final String url = "https://www.kickstarter.com/projects/smithsonian/smithsonian-anthology-of-hip-hop-and-rap/comments";
        this.vm.intent(intentWithData(url));
        this.vm.packageManager(getPackageManager());
        this.requestPackageManager.assertValueCount(1);
        this.startBrowser.assertValueCount(1);
        this.startDiscoveryActivity.assertNoValues();
        this.startProjectActivity.assertNoValues();
        this.koalaTest.assertNoValues();
    }

    @Test
    public void testProjectPreviewLink_startsBrowser() {
        setUpEnvironment();
        final String url = "https://www.kickstarter.com/projects/smithsonian/smithsonian-anthology-of-hip-hop-and-rap?token=beepboop";
        this.vm.intent(intentWithData(url));
        this.vm.packageManager(getPackageManager());
        this.requestPackageManager.assertValueCount(1);
        this.startBrowser.assertValueCount(1);
        this.startDiscoveryActivity.assertNoValues();
        this.startProjectActivity.assertNoValues();
        this.koalaTest.assertNoValues();
    }

    @Test
    public void testProjectDeepLink_startsProjectActivity() {
        setUpEnvironment();
        final String url = "https://www.kickstarter.com/projects/smithsonian/smithsonian-anthology-of-hip-hop-and-rap";
        this.vm.intent(intentWithData(url));
        this.startProjectActivity.assertValue(Uri.parse(url));
        this.startBrowser.assertNoValues();
        this.requestPackageManager.assertNoValues();
        this.startDiscoveryActivity.assertNoValues();
        this.koalaTest.assertValues(CONTINUE_USER_ACTIVITY, OPENED_DEEP_LINK);
    }

    @Test
    public void testDiscoveryDeepLink_startsDiscoveryActivity() {
        setUpEnvironment();
        final String url = "https://www.kickstarter.com/projects";
        this.vm.intent(intentWithData(url));
        this.startDiscoveryActivity.assertValueCount(1);
        this.startBrowser.assertNoValues();
        this.requestPackageManager.assertNoValues();
        this.startProjectActivity.assertNoValues();
        this.koalaTest.assertValues(CONTINUE_USER_ACTIVITY, OPENED_DEEP_LINK);
    }
}

