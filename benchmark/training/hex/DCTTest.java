package hex;


import MathUtils.DCT;
import org.junit.Test;
import water.TestUtil;
import water.fvec.Frame;
import water.util.Log;
import water.util.PrettyPrint;


public class DCTTest extends TestUtil {
    @Test
    public void DCT_1D() {
        Frame frame = null;
        Frame frameDCT = null;
        Frame frameRec = null;
        try {
            CreateFrame cf = new CreateFrame();
            cf.rows = 100;
            int height = 257;
            int width = 1;
            int depth = 1;
            cf.cols = (height * width) * depth;
            cf.categorical_fraction = 0.0;
            cf.integer_fraction = 0;
            cf.binary_fraction = 0;
            cf.missing_fraction = 0;
            cf.factors = 0;
            cf.seed = 1234;
            frame = cf.execImpl().get();
            long now = System.currentTimeMillis();
            frameDCT = DCT.transform1D(frame, height, false);
            Log.info(((((("Computed 1D DCT of " + (cf.rows)) + " rows of size ") + (cf.cols)) + " in") + (PrettyPrint.msecs(((System.currentTimeMillis()) - now), true))));
            now = System.currentTimeMillis();
            frameRec = DCT.transform1D(frameDCT, height, true);
            Log.info(((((("Computed inverse 1D DCT of " + (cf.rows)) + " rows of size ") + (cf.cols)) + " in") + (PrettyPrint.msecs(((System.currentTimeMillis()) - now), true))));
            for (int i = 0; i < (frame.vecs().length); ++i)
                TestUtil.assertVecEquals(frame.vecs()[i], frameRec.vecs()[i], 1.0E-5);

            Log.info("Identity test passed: DCT^-1(DCT(frame)) == frame");
        } finally {
            if (frame != null)
                frame.delete();

            if (frameDCT != null)
                frameDCT.delete();

            if (frameRec != null)
                frameRec.delete();

        }
    }

    @Test
    public void DCT_2D() {
        Frame frame = null;
        Frame frameDCT = null;
        Frame frameRec = null;
        try {
            CreateFrame cf = new CreateFrame();
            cf.rows = 100;
            int height = 35;
            int width = 17;
            int depth = 1;
            cf.cols = (height * width) * depth;
            cf.categorical_fraction = 0.0;
            cf.integer_fraction = 0;
            cf.binary_fraction = 0;
            cf.missing_fraction = 0;
            cf.factors = 0;
            cf.seed = 1234;
            frame = cf.execImpl().get();
            long now = System.currentTimeMillis();
            frameDCT = DCT.transform2D(frame, height, width, false);
            Log.info(((((("Computed 2D DCT of " + (cf.rows)) + " rows of size ") + (cf.cols)) + " in") + (PrettyPrint.msecs(((System.currentTimeMillis()) - now), true))));
            now = System.currentTimeMillis();
            frameRec = DCT.transform2D(frameDCT, height, width, true);
            Log.info(((((("Computed inverse 2D DCT of " + (cf.rows)) + " rows of size ") + (cf.cols)) + " in") + (PrettyPrint.msecs(((System.currentTimeMillis()) - now), true))));
            for (int i = 0; i < (frame.vecs().length); ++i)
                TestUtil.assertVecEquals(frame.vecs()[i], frameRec.vecs()[i], 1.0E-5);

            Log.info("Identity test passed: DCT^-1(DCT(frame)) == frame");
        } finally {
            if (frame != null)
                frame.delete();

            if (frameDCT != null)
                frameDCT.delete();

            if (frameRec != null)
                frameRec.delete();

        }
    }

    @Test
    public void DCT_3D() {
        Frame frame = null;
        Frame frameDCT = null;
        Frame frameRec = null;
        try {
            CreateFrame cf = new CreateFrame();
            cf.rows = 100;
            int height = 17;
            int width = 7;
            int depth = 9;
            cf.cols = (height * width) * depth;
            cf.categorical_fraction = 0.0;
            cf.integer_fraction = 0;
            cf.binary_fraction = 0;
            cf.missing_fraction = 0;
            cf.factors = 0;
            cf.seed = 1234;
            frame = cf.execImpl().get();
            long now = System.currentTimeMillis();
            frameDCT = DCT.transform3D(frame, height, width, depth, false);
            Log.info(((((("Computed 3D DCT of " + (cf.rows)) + " rows of size ") + (cf.cols)) + " in") + (PrettyPrint.msecs(((System.currentTimeMillis()) - now), true))));
            now = System.currentTimeMillis();
            frameRec = DCT.transform3D(frameDCT, height, width, depth, true);
            Log.info(((((("Computed inverse 3D DCT of " + (cf.rows)) + " rows of size ") + (cf.cols)) + " in") + (PrettyPrint.msecs(((System.currentTimeMillis()) - now), true))));
            for (int i = 0; i < (frame.vecs().length); ++i)
                TestUtil.assertVecEquals(frame.vecs()[i], frameRec.vecs()[i], 1.0E-5);

            Log.info("Identity test passed: DCT^-1(DCT(frame)) == frame");
        } finally {
            if (frame != null)
                frame.delete();

            if (frameDCT != null)
                frameDCT.delete();

            if (frameRec != null)
                frameRec.delete();

        }
    }
}

