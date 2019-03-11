package water.api;


import H2O.LOW_PRIORITY_API_WORK;
import H2O.LOW_PRIORITY_API_WORK_CLASS;
import java.io.IOException;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;
import water.fvec.Frame;
import water.fvec.Vec;

import static H2O.LOW_PRIORITY_API_WORK;
import static H2O.LOW_PRIORITY_API_WORK_CLASS;


/**
 * Test that short, interactive work runs at a higher priority than long
 *  running model-building work.
 */
public class APIThrPriorTest extends TestUtil {
    @Test
    public void testAPIThrPriorities() throws IOException {
        Frame fr = null;
        Bogus blder = null;
        Job<BogusModel> job = null;
        Vec vec = null;
        try {
            // Get some keys & frames loaded
            fr = TestUtil.parse_test_file(Key.make("iris.hex"), "smalldata/iris/iris_wheader.csv");
            vec = Vec.makeZero(100);
            // Basic test plan:
            // Start a "long running model-builder job".  This job will start using the
            // nomial model-builder strategy, then block in the driver "as if" it's
            // working hard.  Imagine DL slamming all cores.  We record the F/J
            // priority we're running on.
            // 
            // Then we make a REST-style call thru RequestServer looking for some
            // stuff; list all frames, cloud status, view a frame (rollups).  During
            // these actions we record F/J queue priorities - and assert this work is
            // all running higher than the DL/model-build priority.
            // TODO: Make a more sophisticated builder that launches a MRTask internally,
            // which blocks on ALL NODES - before we begin doing rollups.  Then check
            // the rollups priorities ON ALL NODES, not just this one.
            // Build and launch the builder
            BogusModel.BogusParameters parms = new BogusModel.BogusParameters();
            blder = new Bogus(parms);
            job = trainModel();
            // Block till the builder sets _driver_priority, and is blocked on state==1
            synchronized(blder) {
                while ((blder._state) == 0)
                    try {
                        blder.wait();
                    } catch (InterruptedException ignore) {
                    }

                assert (blder._state) == 1;
            }
            int driver_prior = blder._driver_priority;
            Properties urlparms;
            // Now that the builder is blocked at some priority, do some GUI work which
            // needs to be at a higher priority.  It comes in on a non-FJ thread
            // (probably Nano or Jetty) but anything that hits the F/J queue needs to
            // be higher
            Assert.assertEquals(0, LOW_PRIORITY_API_WORK);
            Assert.assertNull(LOW_PRIORITY_API_WORK_CLASS);
            LOW_PRIORITY_API_WORK = driver_prior + 1;
            // Many URLs behave.
            // Broken hack URLs:
            serve("/", null, 301);
            serve("/junk", null, 404);
            serve("/HTTP404", null, 404);
            // Basic: is H2O up?
            serve("/3/Cloud", null, 200);
            serve("/3/About", null, 200);
            // What is H2O doing?
            urlparms = new Properties();
            urlparms.setProperty("depth", "10");
            serve("/3/Profiler", urlparms, 200);
            serve("/3/JStack", null, 200);
            serve("/3/KillMinus3", null, 200);
            serve("/3/Timeline", null, 200);
            serve("/3/Jobs", null, 200);
            serve("/3/WaterMeterCpuTicks/0", null, 200);
            serve("/3/WaterMeterIo", null, 200);
            serve("/3/Logs/download", null, 200);
            serve("/3/NetworkTest", null, 200);
            // Rollup stats behave
            final Key rskey = vec.rollupStatsKey();
            Assert.assertNull(DKV.get(rskey));// Rollups on my zeros not computed yet

            vec.sigma();
            Assert.assertNotNull(DKV.get(rskey));// Rollups on my zeros not computed yet

            serve("/3/Frames/iris.hex", null, 200);// Rollups already done at parse, but gets ChunkSummary

            // Convenience; inspection of simple stuff
            urlparms = new Properties();
            urlparms.setProperty("src", "./smalldata/iris");
            serve("/3/Typeahead/files", urlparms, 200);
            urlparms = new Properties();
            urlparms.setProperty("key", "iris.hex");
            urlparms.setProperty("row", "0");
            urlparms.setProperty("match", "foo");
            serve("/3/Find", urlparms, 200);
            serve("/3/Metadata/endpoints", null, 200);
            serve("/3/Frames", null, 200);
            serve("/3/Models", null, 200);
            serve("/3/ModelMetrics", null, 200);
            serve("/3/NodePersistentStorage/configured", null, 200);
            // Recovery
            // serve("/3/Shutdown", null,200); // OOPS!  Don't really want to run this one, unless we're all done with testing
            serve("/3/DKV", null, 200, "DELETE");// delete must happen after rollups above!

            serve("/3/LogAndEcho", null, 200, "POST");
            serve("/3/InitID", null, 200);
            serve("/3/GarbageCollect", null, 200, "POST");
            // Turn off debug tracking
            LOW_PRIORITY_API_WORK = 0;
            LOW_PRIORITY_API_WORK_CLASS = null;
            // Allow the builder to complete.
            DKV.put(job);// reinstate the JOB in the DKV, because JOB demands it.

            synchronized(blder) {
                blder._state = 2;
                blder.notify();
            }
            job.get();// Block for builder to complete

        } finally {
            // Turn off debug tracking
            LOW_PRIORITY_API_WORK = 0;
            LOW_PRIORITY_API_WORK_CLASS = null;
            if (blder != null)
                synchronized(blder) {
                    blder._state = 2;
                    blder.notify();
                }

            if (job != null)
                job.remove();

            if (vec != null)
                vec.remove();

            if (fr != null)
                fr.delete();

        }
    }
}

