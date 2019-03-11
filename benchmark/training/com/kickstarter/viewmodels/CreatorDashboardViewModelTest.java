package com.kickstarter.viewmodels;


import CreatorDashboardViewModel.ViewModel;
import KoalaEvent.OPENED_PROJECT_SWITCHER;
import KoalaEvent.SWITCHED_PROJECTS;
import KoalaEvent.VIEWED_PROJECT_DASHBOARD;
import android.util.Pair;
import androidx.annotation.NonNull;
import com.kickstarter.KSRobolectricTestCase;
import com.kickstarter.mock.factories.ProjectFactory;
import com.kickstarter.mock.factories.ProjectStatsEnvelopeFactory;
import com.kickstarter.mock.factories.ProjectsEnvelopeFactory;
import com.kickstarter.mock.services.MockApiClient;
import com.kickstarter.models.Project;
import com.kickstarter.services.apiresponses.ProjectStatsEnvelope;
import com.kickstarter.services.apiresponses.ProjectsEnvelope;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;


public class CreatorDashboardViewModelTest extends KSRobolectricTestCase {
    private ViewModel vm;

    private final TestSubscriber<Void> openBottomSheet = new TestSubscriber();

    private final TestSubscriber<Pair<Project, ProjectStatsEnvelope>> projectAndStats = new TestSubscriber();

    private final TestSubscriber<List<Project>> projectsForBottomSheet = new TestSubscriber();

    @Test
    public void testProjectsForBottomSheet_With1Project() {
        final List<Project> projects = Collections.singletonList(ProjectFactory.project());
        final MockApiClient apiClient = new MockApiClient() {
            @Override
            @NonNull
            public Observable<ProjectsEnvelope> fetchProjects(final boolean member) {
                return Observable.just(ProjectsEnvelopeFactory.projectsEnvelope(projects));
            }
        };
        setUpEnvironment(environment().toBuilder().apiClient(apiClient).build());
        this.projectsForBottomSheet.assertNoValues();
    }

    @Test
    public void testProjectsForBottomSheet_WithManyProjects() {
        final Project project1 = ProjectFactory.project();
        final Project project2 = ProjectFactory.project();
        final List<Project> projects = Arrays.asList(project1, project2);
        final MockApiClient apiClient = new MockApiClient() {
            @Override
            @NonNull
            public Observable<ProjectsEnvelope> fetchProjects(final boolean member) {
                return Observable.just(ProjectsEnvelopeFactory.projectsEnvelope(projects));
            }
        };
        setUpEnvironment(environment().toBuilder().apiClient(apiClient).build());
        this.projectsForBottomSheet.assertValue(Collections.singletonList(project2));
    }

    @Test
    public void testProjectSwitcherProjectClickOutput() {
        final Project project1 = ProjectFactory.project();
        final Project project2 = ProjectFactory.project();
        final List<Project> projects = Arrays.asList(project1, project2);
        final ProjectStatsEnvelope projectStatsEnvelope = ProjectStatsEnvelopeFactory.projectStatsEnvelope();
        final MockApiClient apiClient = new MockApiClient() {
            @Override
            @NonNull
            public Observable<ProjectsEnvelope> fetchProjects(final boolean member) {
                return Observable.just(ProjectsEnvelopeFactory.projectsEnvelope(projects));
            }

            @Override
            @NonNull
            public Observable<ProjectStatsEnvelope> fetchProjectStats(@NonNull
            final Project project) {
                return Observable.just(projectStatsEnvelope);
            }
        };
        setUpEnvironment(environment().toBuilder().apiClient(apiClient).build());
        this.vm.inputs.projectSelectionInput(project2);
        this.projectAndStats.assertValues(Pair.create(project1, ProjectStatsEnvelopeFactory.projectStatsEnvelope()), Pair.create(project2, ProjectStatsEnvelopeFactory.projectStatsEnvelope()));
        this.koalaTest.assertValues(VIEWED_PROJECT_DASHBOARD, SWITCHED_PROJECTS, VIEWED_PROJECT_DASHBOARD);
    }

    @Test
    public void testProjectsListButtonClicked() {
        setUpEnvironment(environment());
        this.vm.inputs.projectsListButtonClicked();
        this.openBottomSheet.assertValueCount(1);
        this.koalaTest.assertValue(OPENED_PROJECT_SWITCHER);
    }
}

