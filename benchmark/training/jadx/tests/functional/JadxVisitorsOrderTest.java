package jadx.tests.functional;


import jadx.api.JadxArgs;
import jadx.core.Jadx;
import jadx.core.dex.visitors.IDexTreeVisitor;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JadxVisitorsOrderTest {
    private static final Logger LOG = LoggerFactory.getLogger(JadxVisitorsOrderTest.class);

    @Test
    public void testOrder() {
        List<IDexTreeVisitor> passes = Jadx.getPassesList(new JadxArgs());
        List<String> errors = JadxVisitorsOrderTest.check(passes);
        for (String str : errors) {
            JadxVisitorsOrderTest.LOG.error(str);
        }
        Assert.assertThat(errors, Matchers.empty());
    }
}

