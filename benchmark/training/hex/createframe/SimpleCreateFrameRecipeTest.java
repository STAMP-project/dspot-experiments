package hex.createframe;


import hex.createframe.recipes.SimpleCreateFrameRecipe;
import org.junit.Assert;
import org.junit.Test;
import water.Scope;
import water.TestUtil;
import water.api.schemas4.input.CreateFrameSimpleIV4;
import water.fvec.Frame;
import water.fvec.Vec;
import water.util.Log;

import static ResponseType.NONE;


/**
 * Test for the {@link SimpleCreateFrameRecipe} class.
 */
public class SimpleCreateFrameRecipeTest extends TestUtil {
    /**
     * Test that the frame with all default arguments can be constructed.
     */
    @Test
    public void emptyTest() {
        Scope.enter();
        try {
            CreateFrameSimpleIV4 s = new CreateFrameSimpleIV4().fillFromImpl();
            SimpleCreateFrameRecipe cf = s.createAndFillImpl();
            Frame frame = cf.exec().get();
            Scope.track(frame);
            Log.info(frame);
            Assert.assertNotNull(frame);
            Assert.assertEquals(0, frame.numCols());
            Assert.assertEquals(0, frame.numRows());
        } finally {
            Scope.exit();
        }
    }

    /**
     * Simple initial test: verify that the random frame can be created, that it has the correct
     * dimensions and column names.
     */
    @Test
    public void basicTest() {
        Scope.enter();
        try {
            CreateFrameSimpleIV4 s = new CreateFrameSimpleIV4().fillFromImpl();
            s.nrows = ((int) ((Math.random()) * 200)) + 50;
            s.ncols_int = ((int) ((Math.random()) * 10)) + 3;
            s.ncols_real = ((int) ((Math.random()) * 10)) + 3;
            s.ncols_bool = ((int) ((Math.random()) * 10)) + 3;
            s.ncols_enum = ((int) ((Math.random()) * 10)) + 3;
            s.ncols_time = ((int) ((Math.random()) * 10)) + 3;
            s.ncols_str = ((int) ((Math.random()) * 5)) + 2;
            SimpleCreateFrameRecipe cf = s.createAndFillImpl();
            Frame frame = cf.exec().get();
            Scope.track(frame);
            Assert.assertNotNull(frame);
            Assert.assertEquals(s.nrows, frame.numRows());
            for (int i = (frame.numCols()) - 1; i >= 0; i--) {
                char firstLetter = frame.name(i).charAt(0);
                int num = Integer.parseInt(frame.name(i).substring(1));
                Vec v = frame.vec(i);
                switch (firstLetter) {
                    case 'B' :
                        Assert.assertTrue(v.isBinary());
                        Assert.assertEquals(num, ((s.ncols_bool)--));
                        break;
                    case 'E' :
                        Assert.assertTrue(v.isCategorical());
                        Assert.assertEquals(num, ((s.ncols_enum)--));
                        break;
                    case 'I' :
                        Assert.assertTrue(v.isInt());
                        Assert.assertEquals(num, ((s.ncols_int)--));
                        break;
                    case 'R' :
                        Assert.assertTrue(((v.isNumeric()) && (!(v.isInt()))));
                        Assert.assertEquals(num, ((s.ncols_real)--));
                        break;
                    case 'S' :
                        Assert.assertTrue(v.isString());
                        Assert.assertEquals(num, ((s.ncols_str)--));
                        break;
                    case 'T' :
                        Assert.assertTrue(v.isTime());
                        Assert.assertEquals(num, ((s.ncols_time)--));
                        break;
                }
            }
            Assert.assertTrue((((((((s.ncols_bool) == 0) && ((s.ncols_enum) == 0)) && ((s.ncols_int) == 0)) && ((s.ncols_real) == 0)) && ((s.ncols_str) == 0)) && ((s.ncols_time) == 0)));
            Log.info(frame.toString());
        } finally {
            Scope.exit();
        }
    }

    @Test
    public void testResponses() {
        Scope.enter();
        try {
            CreateFrameSimpleIV4 s = new CreateFrameSimpleIV4().fillFromImpl();
            s.nrows = 10;
            s.ncols_int = 1;
            s.ncols_real = 1;
            s.ncols_bool = 1;
            for (ResponseType rt : ResponseType.values()) {
                if (rt == (NONE))
                    continue;

                s.response_type = rt;
                SimpleCreateFrameRecipe cf = s.createAndFillImpl();
                Frame frame = cf.exec().get();
                Scope.track(frame);
                Assert.assertNotNull(frame);
                Assert.assertEquals(10, frame.numRows());
                Assert.assertEquals(4, frame.numCols());
                Assert.assertEquals("response", frame.name(0));
                Vec vres = frame.vec(0);
                switch (rt) {
                    case BOOL :
                        Assert.assertTrue(vres.isBinary());
                        break;
                    case INT :
                        Assert.assertTrue(vres.isInt());
                        break;
                    case TIME :
                        Assert.assertTrue(vres.isTime());
                        break;
                    case ENUM :
                        Assert.assertTrue(vres.isCategorical());
                        break;
                    case REAL :
                        Assert.assertTrue(vres.isNumeric());
                        break;
                }
            }
        } finally {
            Scope.exit();
        }
    }
}

