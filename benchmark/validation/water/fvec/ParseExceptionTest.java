package water.fvec;


import CustomParser.ParserSetup;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;
import water.parser.CustomParser;


public class ParseExceptionTest extends TestUtil {
    @Test
    public void testParserRecoversFromException() {
        Throwable ex = null;
        Key fkey0 = null;
        Key fkey1 = null;
        Key fkey2 = null;
        Key okey = null;
        CustomParser.ParserSetup setup = null;
        try {
            okey = Key.make("junk.hex");
            fkey0 = NFSFileVec.make(new File("smalldata/parse_folder_test/prostate_0.csv"));
            fkey1 = NFSFileVec.make(new File("smalldata/parse_folder_test/prostate_1.csv"));
            fkey2 = NFSFileVec.make(new File("smalldata/parse_folder_test/prostate_2.csv"));
            setup = new water.parser.GuessSetup.GuessSetupTsk(new CustomParser.ParserSetup(), true).invoke(fkey0, fkey1, fkey2)._gSetup._setup;
            // Now "break" one of the files.  Globally.
            invokeOnAllNodes();
            ParseDataset2.parse(okey, new Key[]{ fkey0, fkey1, fkey2 }, setup, true);
        } catch (Throwable e2) {
            ex = e2;// Record expected exception

        }
        Assert.assertTrue("Parse should throw an NPE", (ex != null));
        Assert.assertTrue("All input & output keys not removed", ((DKV.get(fkey0)) == null));
        Assert.assertTrue("All input & output keys not removed", ((DKV.get(fkey1)) == null));
        Assert.assertTrue("All input & output keys not removed", ((DKV.get(fkey2)) == null));
        Assert.assertTrue("All input & output keys not removed", ((DKV.get(okey)) == null));
        // Try again, in the same test, same inputs & outputs but not broken.
        // Should recover completely.
        okey = Key.make("junk.hex");
        fkey0 = NFSFileVec.make(new File("smalldata/parse_folder_test/prostate_0.csv"));
        fkey1 = NFSFileVec.make(new File("smalldata/parse_folder_test/prostate_1.csv"));
        fkey2 = NFSFileVec.make(new File("smalldata/parse_folder_test/prostate_2.csv"));
        Frame fr = ParseDataset2.parse(okey, new Key[]{ fkey0, fkey1, fkey2 });
        fr.delete();
        Assert.assertTrue("All input & output keys not removed", ((DKV.get(fkey0)) == null));
        Assert.assertTrue("All input & output keys not removed", ((DKV.get(fkey1)) == null));
        Assert.assertTrue("All input & output keys not removed", ((DKV.get(fkey2)) == null));
        Assert.assertTrue("All input & output keys not removed", ((DKV.get(okey)) == null));
    }

    private static class Break extends DRemoteTask<ParseExceptionTest.Break> {
        final water.Key _key;

        Break(Key key) {
            _key = key;
        }

        @Override
        public void lcompute() {
            Vec vec = DKV.get(_key).get();
            Chunk chk = vec.chunkForChunkIdx(0);// Load the chunk (which otherwise loads only lazily)

            chk._mem = null;
            // Illegal setup: Chunk _mem should never be null
            tryComplete();
        }

        @Override
        public void reduce(ParseExceptionTest.Break drt) {
        }
    }
}

