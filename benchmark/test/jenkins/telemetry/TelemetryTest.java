package jenkins.telemetry;


import Telemetry.TelemetryReporter;
import hudson.ExtensionList;
import hudson.model.UnprotectedRootAction;
import hudson.security.csrf.CrumbExclusion;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.regex.Pattern;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.LoggerRule;
import org.jvnet.hudson.test.TestExtension;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;


public class TelemetryTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Rule
    public LoggerRule logger = new LoggerRule().record(Telemetry.class, Level.ALL).capture(100);

    private static int counter = 0;

    @Test
    public void testSubmission() throws Exception {
        j.jenkins.setNoUsageStatistics(false);// tests usually don't submit this, but we need this

        Assert.assertEquals("no requests received", 0, TelemetryTest.counter);
        ExtensionList.lookupSingleton(TelemetryReporter.class).doRun();
        do {
            Thread.sleep(250);
        } while ((TelemetryTest.counter) == 0 );// this might end up being flaky due to 1 to many active telemetry trials

        Assert.assertThat(logger.getMessages(), CoreMatchers.hasItem("Telemetry submission received response '200 OK' for: test-data"));
        Assert.assertThat(logger.getMessages(), CoreMatchers.hasItem("Skipping telemetry for 'future' as it is configured to start later"));
        Assert.assertThat(logger.getMessages(), CoreMatchers.hasItem("Skipping telemetry for 'past' as it is configured to end in the past"));
        Assert.assertThat(logger.getMessages(), CoreMatchers.hasItem("Skipping telemetry for 'empty' as it has no data"));
        Assert.assertThat(TelemetryTest.types, CoreMatchers.hasItem("test-data"));
        Assert.assertThat(TelemetryTest.types, CoreMatchers.not(CoreMatchers.hasItem("future")));
        Assert.assertThat(TelemetryTest.types, CoreMatchers.not(CoreMatchers.hasItem("past")));
        Assert.assertThat(TelemetryTest.correlators.size(), CoreMatchers.is(TelemetryTest.counter));
        Assert.assertTrue(Pattern.compile("[0-9a-f]+").matcher(TelemetryTest.correlators.first()).matches());
        Assert.assertThat(TelemetryTest.types, CoreMatchers.not(CoreMatchers.hasItem("empty")));
        Assert.assertTrue("at least one request received", ((TelemetryTest.counter) > 0));// TestTelemetry plus whatever real impls exist

    }

    @Test
    public void testPerTrialCorrelator() throws Exception {
        Correlator correlator = ExtensionList.lookupSingleton(Correlator.class);
        String correlationId = "00000000-0000-0000-0000-000000000000";
        correlator.setCorrelationId(correlationId);
        ExtensionList.lookupSingleton(TelemetryReporter.class).doRun();
        do {
            Thread.sleep(250);
        } while ((TelemetryTest.counter) == 0 );// this might end up being flaky due to 1 to many active telemetry trials

        Assert.assertThat(TelemetryTest.types, CoreMatchers.hasItem("test-data"));
        // 90ecf3ce1cd5ba1e5ad3cde7ad08a941e884f2e4d9bd463361715abab8efedc5
        Assert.assertThat(TelemetryTest.correlators, CoreMatchers.hasItem(DigestUtils.sha256Hex((correlationId + "test-data"))));
    }

    @TestExtension
    public static class EmptyTelemetry extends Telemetry {
        @Nonnull
        @Override
        public String getDisplayName() {
            return "empty";
        }

        @Nonnull
        @Override
        public String getId() {
            return "empty";
        }

        @Nonnull
        @Override
        public LocalDate getStart() {
            return LocalDate.MIN;
        }

        @Nonnull
        @Override
        public LocalDate getEnd() {
            return LocalDate.MAX;
        }

        @Override
        public JSONObject createContent() {
            return null;
        }
    }

    @TestExtension
    public static class DisabledFutureTelemetry extends Telemetry {
        @Nonnull
        @Override
        public String getId() {
            return "future";
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return "future";
        }

        @Nonnull
        @Override
        public LocalDate getStart() {
            return LocalDate.now().plus(1, ChronoUnit.DAYS);
        }

        @Nonnull
        @Override
        public LocalDate getEnd() {
            return LocalDate.MAX;
        }

        @Nonnull
        @Override
        public JSONObject createContent() {
            return new JSONObject();
        }
    }

    @TestExtension
    public static class DisabledPastTelemetry extends Telemetry {
        @Nonnull
        @Override
        public String getId() {
            return "past";
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return "past";
        }

        @Nonnull
        @Override
        public LocalDate getStart() {
            return LocalDate.MIN;
        }

        @Nonnull
        @Override
        public LocalDate getEnd() {
            return LocalDate.now().minus(1, ChronoUnit.DAYS);
        }

        @Nonnull
        @Override
        public JSONObject createContent() {
            return new JSONObject();
        }
    }

    @TestExtension
    public static class TestTelemetry extends Telemetry {
        @Nonnull
        @Override
        public String getId() {
            return "test-data";
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return "test-data";
        }

        @Nonnull
        @Override
        public LocalDate getStart() {
            return LocalDate.MIN;
        }

        @Nonnull
        @Override
        public LocalDate getEnd() {
            return LocalDate.MAX;
        }

        @Nonnull
        @Override
        public JSONObject createContent() {
            return new JSONObject();
        }
    }

    @TestExtension
    public static class NoCrumb extends CrumbExclusion {
        @Override
        public boolean process(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
            String pathInfo = request.getPathInfo();
            if ((pathInfo != null) && (pathInfo.startsWith("/uplink"))) {
                chain.doFilter(request, response);
                return true;
            }
            return false;
        }
    }

    private static SortedSet<String> correlators = new TreeSet<>();

    private static Set<String> types = new HashSet<>();

    @TestExtension
    public static class TelemetryReceiver implements UnprotectedRootAction {
        public void doEvents(StaplerRequest request, StaplerResponse response) throws IOException {
            StringWriter sw = new StringWriter();
            IOUtils.copy(request.getInputStream(), sw, StandardCharsets.UTF_8);
            JSONObject json = JSONObject.fromObject(sw.toString());
            TelemetryTest.correlators.add(json.getString("correlator"));
            TelemetryTest.types.add(json.getString("type"));
            (TelemetryTest.counter)++;
        }

        @CheckForNull
        @Override
        public String getIconFileName() {
            return null;
        }

        @CheckForNull
        @Override
        public String getDisplayName() {
            return null;
        }

        @CheckForNull
        @Override
        public String getUrlName() {
            return "uplink";
        }
    }
}

