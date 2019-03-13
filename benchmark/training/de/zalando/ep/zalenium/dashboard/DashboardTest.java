package de.zalando.ep.zalenium.dashboard;


import TestInformation.TestStatus.COMPLETED;
import com.google.gson.JsonObject;
import de.zalando.ep.zalenium.util.TestUtils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static Dashboard.VIDEOS_FOLDER_NAME;


public class DashboardTest {
    private static final String TEST_COUNT_FILE_NAME = "executedTestsInfo.json";

    private TestInformation ti = new TestInformation.TestInformationBuilder().withSeleniumSessionId("seleniumSessionId").withTestName("testName").withProxyName("proxyName").withBrowser("browser").withBrowserVersion("browserVersion").withPlatform("platform").withTestStatus(COMPLETED).build();

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testCountOne() {
        Dashboard dashboard = new Dashboard();
        dashboard.updateDashboard(ti);
        Assert.assertEquals(1, Dashboard.getExecutedTests());
        Assert.assertEquals(1, Dashboard.getExecutedTestsWithVideo());
    }

    @Test
    public void testCountTwo() {
        Dashboard dashboard = new Dashboard();
        dashboard.updateDashboard(ti);
        dashboard.updateDashboard(ti);
        Assert.assertEquals(2, Dashboard.getExecutedTests());
        Assert.assertEquals(2, Dashboard.getExecutedTestsWithVideo());
    }

    @Test
    public void missingExecutedTestsFile() throws IOException {
        Dashboard dashboard = new Dashboard();
        dashboard.updateDashboard(ti);
        cleanTempVideosFolder();
        TestUtils.ensureRequiredInputFilesExist(temporaryFolder);
        dashboard.updateDashboard(ti);
        Assert.assertEquals(1, Dashboard.getExecutedTests());
        Assert.assertEquals(1, Dashboard.getExecutedTestsWithVideo());
    }

    @Test
    public void nonNumberContentsIgnored() throws IOException {
        File testCountFile = new File((((((temporaryFolder.getRoot().getAbsolutePath()) + "/") + (VIDEOS_FOLDER_NAME)) + "/") + (DashboardTest.TEST_COUNT_FILE_NAME)));
        JsonObject testQuantities = new JsonObject();
        testQuantities.addProperty("executedTests", "Not-A-Number");
        testQuantities.addProperty("executedTestsWithVideo", "Not-A-Number");
        FileUtils.writeStringToFile(testCountFile, testQuantities.toString(), StandardCharsets.UTF_8);
        Dashboard.setExecutedTests(0, 0);
        DashboardCollection.updateDashboard(ti);
        Assert.assertEquals(1, Dashboard.getExecutedTests());
        Assert.assertEquals(1, Dashboard.getExecutedTestsWithVideo());
    }
}

