package com.kickstarter.viewmodels;


import CreatorDashboardBottomSheetHolderViewModel.ViewModel;
import com.kickstarter.KSRobolectricTestCase;
import com.kickstarter.mock.factories.ProjectFactory;
import com.kickstarter.models.Project;
import org.joda.time.DateTime;
import org.junit.Test;
import rx.observers.TestSubscriber;


public class CreatorDashboardBottomSheetHolderViewModelTest extends KSRobolectricTestCase {
    private ViewModel vm;

    private final TestSubscriber<String> projectName = new TestSubscriber();

    private final TestSubscriber<DateTime> projectLaunchDate = new TestSubscriber();

    @Test
    public void testProjectNameText() {
        setUpEnvironment(environment());
        final String projectName = "Test Project";
        final DateTime now = DateTime.now();
        final Project project = ProjectFactory.project().toBuilder().name(projectName).launchedAt(now).build();
        this.vm.inputs.projectInput(project);
        this.projectName.assertValues(projectName);
        this.projectLaunchDate.assertValue(now);
    }
}

