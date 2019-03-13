package com.kickstarter.viewmodels;


import ThanksShareHolderViewModel.ViewModel;
import com.kickstarter.KSRobolectricTestCase;
import com.kickstarter.mock.factories.ProjectFactory;
import com.kickstarter.models.Project;
import org.junit.Test;
import rx.observers.TestSubscriber;


public final class ThanksShareHolderViewModelTest extends KSRobolectricTestCase {
    private ViewModel vm;

    private final TestSubscriber<String> projectName = new TestSubscriber();

    private final TestSubscriber<Project> startShare = new TestSubscriber();

    private final TestSubscriber<Project> startShareOnFacebook = new TestSubscriber();

    private final TestSubscriber<Project> startShareOnTwitter = new TestSubscriber();

    @Test
    public void testThanksShareHolderViewModel_projectName() {
        final Project project = ProjectFactory.project();
        setUpEnvironment(environment());
        this.vm.configureWith(project);
        this.projectName.assertValues(project.name());
    }

    @Test
    public void testThanksShareHolderViewModel_share() {
        final Project project = ProjectFactory.project();
        setUpEnvironment(environment());
        this.vm.configureWith(project);
        this.vm.inputs.shareClick();
        this.startShare.assertValues(project);
        this.koalaTest.assertValues("Checkout Show Share Sheet");
        this.vm.inputs.shareOnFacebookClick();
        this.startShareOnFacebook.assertValues(project);
        this.koalaTest.assertValues("Checkout Show Share Sheet", "Checkout Show Share");
        this.vm.inputs.shareOnTwitterClick();
        this.startShareOnTwitter.assertValues(project);
        this.koalaTest.assertValues("Checkout Show Share Sheet", "Checkout Show Share", "Checkout Show Share");
    }
}

