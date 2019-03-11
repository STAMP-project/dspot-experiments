package water.rapids.ast.prims.assign;


import org.junit.Assert;
import org.junit.Test;
import water.DKV;
import water.Key;
import water.Scope;
import water.TestUtil;
import water.fvec.Frame;
import water.rapids.Rapids;
import water.rapids.Session;
import water.util.ArrayUtils;


/**
 *
 */
public class AstTmpAssignTest extends TestUtil {
    @Test
    public void testDollarIds() {
        Frame f = null;
        Frame v;
        Frame w;
        try {
            Session sess = new Session();
            String expid1 = "id1~" + (sess.id());
            f = ArrayUtils.frame(Key.<Frame>make(), TestUtil.ar("a", "b"), TestUtil.ard(1, (-1)), TestUtil.ard(2, 0), TestUtil.ard(3, 1));
            v = Rapids.exec((("(, " + (f._key)) + ")->$id1"), sess).getFrame();
            w = DKV.get(expid1).get();
            Assert.assertArrayEquals(f._names, v._names);
            Assert.assertEquals(expid1, v._key.toString());
            Assert.assertEquals(expid1, expand("$id1"));
            Assert.assertNotEquals(f._key, v._key);
            Assert.assertEquals(w, v);
            String expid2 = "foo~" + (sess.id());
            Rapids.exec("(rename '$id1' '$foo')", sess);
            DKV.get(expid2).get();
            Assert.assertEquals(DKV.get(expid1), null);
            Rapids.exec("(rm $foo)", sess);
            Assert.assertEquals(DKV.get(expid2), null);
        } finally {
            if (f != null)
                f.delete();

        }
    }

    @Test
    public void assignSameId() {
        Scope.enter();
        try {
            Session session = new Session();
            String newid = new water.rapids.Env(session).expand("$frame1");
            Frame f = ArrayUtils.frame(Key.<Frame>make(), TestUtil.ar("a", "b"), TestUtil.ard(1, (-1)), TestUtil.ard(2, 0), TestUtil.ard(3, 1));
            Frame v = Rapids.exec((("(tmp= $frame1 (, " + (f._key)) + ")->$frame1)"), session).getFrame();
            Frame w = DKV.get(newid).get();
            Scope.track(f, v);
            Assert.assertEquals(newid, v._key.toString());
            Assert.assertArrayEquals(f.names(), v.names());
            Assert.assertNotEquals(f._key, v._key);
            Assert.assertEquals(w, v);
            newid = new water.rapids.Env(session).expand("$f");
            v = Rapids.exec("(, (, $frame1)->$f)->$f", session).getFrame();
            Scope.track(v);
            Assert.assertEquals(newid, v._key.toString());
            newid = new water.rapids.Env(session).expand("$g");
            v = Rapids.exec("(colnames= (, $f)->$g [0 1] ['egg', 'ham'])->$g", session).getFrame();
            Scope.track(v);
            Assert.assertEquals(newid, v._key.toString());
            Assert.assertArrayEquals(new String[]{ "egg", "ham" }, v.names());
        } finally {
            Scope.exit();
        }
    }
}

