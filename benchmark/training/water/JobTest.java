package water;


import H2O.H2OCountedCompleter;
import Job.WORK_UNKNOWN;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import water.fvec.Frame;


public class JobTest extends TestUtil {
    @Rule
    public ExpectedException ee = ExpectedException.none();

    @Test
    public void setWork() {
        final Job<Frame> j = new Job(Key.<Frame>make(), Frame.class.getName(), "Test Job");
        H2O.H2OCountedCompleter worker = new H2O.H2OCountedCompleter() {
            @Override
            public void compute2() {
                j.setWork(42L);
                j.update(21L);
                Assert.assertEquals(0.5, j.progress(), 0);
                tryComplete();
            }
        };
        j.start(worker, WORK_UNKNOWN).get();
        Assert.assertEquals(42L, j.getWork());
    }

    @Test
    public void setWorkIllegal() {
        final Job<Frame> j = new Job(Key.<Frame>make(), Frame.class.getName(), "Test Job");
        H2O.H2OCountedCompleter worker = new H2O.H2OCountedCompleter() {
            @Override
            public void compute2() {
                j.setWork(13);
                tryComplete();
            }
        };
        ee.expect(IllegalStateException.class);
        ee.expectMessage("Cannot set work amount if it was already previously specified");
        j.start(worker, 12).get();
    }
}

