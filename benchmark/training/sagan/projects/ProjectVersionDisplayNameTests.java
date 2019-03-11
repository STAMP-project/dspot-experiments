package sagan.projects;


import org.junit.Test;


public class ProjectVersionDisplayNameTests {
    @Test
    public void getDisplayNameForGeneralAvailability() {
        assertVersionDisplayName("1.2.3.RELEASE", "1.2.3");
    }

    @Test
    public void getDisplayNameForSnapshotRelease() {
        assertVersionDisplayName("1.0.0.BUILD-SNAPSHOT", "1.0.0");
        assertVersionDisplayName("1.0.0.CI-SNAPSHOT", "1.0.0");
        assertVersionDisplayName("1.0.0.SNAPSHOT", "1.0.0");
    }

    @Test
    public void getDisplayNameForMilestoneReleaseVersion() {
        assertVersionDisplayName("1.2.3.M1", "1.2.3 M1");
    }

    @Test
    public void getDisplayNameForReleaseCandidateVersion() {
        assertVersionDisplayName("1.2.3.RC1", "1.2.3 RC1");
    }

    @Test
    public void getDisplayNameForReleaseTrainVersion() {
        assertVersionDisplayName("Angel.RELEASE", "Angel");
    }

    @Test
    public void getDisplayNameForReleaseTrainServiceRelease() {
        assertVersionDisplayName("Angel.SR1", "Angel SR1");
    }

    @Test
    public void getDisplayNameForReleaseTrainSnapshot() {
        assertVersionDisplayName("Angel.BUILD-SNAPSHOT", "Angel");
    }
}

