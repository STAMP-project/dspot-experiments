package hex;


import H2O.CLOUD;
import org.junit.Test;
import water.TestUtil;
import water.fvec.Frame;
import water.fvec.NFSFileVec;
import water.fvec.ParseDataset2;
import water.fvec.Vec;
import water.util.Log;
import water.util.MRUtils;

import static H2O.NUMCPUS;


public class MRUtilsTest extends TestUtil {
    @Test
    public void run() {
        Frame frame = null;
        Frame f0 = null;
        Frame f1 = null;
        Frame f2 = null;
        Frame f3 = null;
        Frame f4 = null;
        Frame f5 = null;
        Frame f6 = null;
        Vec orig_response = null;
        Key file = null;
        try {
            file = NFSFileVec.make(TestUtil.find_test_file("smalldata/./logreg/prostate.csv"));
            frame = ParseDataset2.parse(Key.make(), new Key[]{ file });
            orig_response = frame.remove(1);
            frame.add("response", orig_response.toEnum());
            frame.add("MOV", frame.remove(2));
            frame.add("ASP", frame.remove("PSA"));
            f0 = MRUtils.sampleFrameStratified(frame, frame.vecs()[frame.find("response")], new float[]{ 1.3F, 2.3F }, 473806003, true);
            f1 = /* nchunks */
            /* shuffle */
            /* create many (global) chunks */
            MRUtils.shuffleAndBalance(frame, ((NUMCPUS) * (CLOUD.size())), 100720045, true, false);
            f2 = MRUtils.sampleFrameStratified(frame, frame.vecs()[frame.find("response")], new float[]{ 1.3F, 2.3F }, 473806003, true);
            f3 = MRUtils.sampleFrame(frame, 7, 912559);
            f4 = MRUtils.sampleFrame(f2, 15, 912559);
            f5 = MRUtils.sampleFrame(f1, 13, 912559);
            f6 = MRUtils.sampleFrameStratified(f2, f2.vecs()[f2.find("response")], new float[]{ 1.0F, 1.3F }, 473806003, true);
            f3.add("SWP", f3.vecs()[f3.find("ASP")]);
            Log.info(f3.toStringAll());
            assert (f3.names().length) == (f3.vecs().length);
            assert (f3.names().length) == 10;
            assert f3.names()[0].equals("ID");
            assert f3.names()[1].equals("AGE");
            assert f3.names()[2].equals("DPROS");
            assert f3.names()[3].equals("DCAPS");
            assert f3.names()[4].equals("VOL");
            assert f3.names()[5].equals("GLEASON");
            assert f3.names()[6].equals("response");
            assert f3.names()[7].equals("MOV");
            assert f3.names()[8].equals("ASP");
            assert f3.names()[9].equals("SWP");
        } finally {
            // cleanup
            if (f6 != null)
                f6.delete();

            if (f5 != null)
                f5.delete();

            if (f4 != null)
                f4.delete();

            if (f3 != null)
                f3.delete();

            if (f2 != null)
                f2.delete();

            if (f1 != null)
                f1.delete();

            if (f0 != null)
                f0.delete();

            if (frame != null)
                frame.delete();

            if (orig_response != null)
                UKV.remove(orig_response._key);

            UKV.remove(file);
        }
    }
}

