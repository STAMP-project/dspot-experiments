package water.jdbc;


import SqlFetchMode.SINGLE;
import Vec.T_NUM;
import Vec.T_STR;
import java.io.File;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import water.Job;
import water.Scope;
import water.TestUtil;
import water.fvec.Frame;
import water.fvec.TestFrameBuilder;


public class SQLManagerIntegTest extends TestUtil {
    private static final File BUILD_DIR = new File("build").getAbsoluteFile();

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder(SQLManagerIntegTest.BUILD_DIR);

    private String connectionString;

    @Test
    public void importSqlTable() {
        Scope.enter();
        try {
            Frame expected = Scope.track(new TestFrameBuilder().withColNames("ID", "NAME").withVecTypes(T_NUM, T_STR).withDataForCol(0, new long[]{ 1, 2, 3, 4 }).withDataForCol(1, new String[]{ "TOM", "BILL", "AMY", "OWEN" }).build());
            Job<Frame> j = SQLManager.importSqlTable(connectionString, "TestData", "", "", "", "*", SINGLE);
            Frame fr = Scope.track(j.get());
            Assert.assertArrayEquals(expected._names, fr._names);
            TestUtil.assertVecEquals(expected.vec(0), fr.vec(0), 0);
            TestUtil.assertStringVecEquals(expected.vec(1), fr.vec(1));
        } finally {
            Scope.exit();
        }
    }
}

