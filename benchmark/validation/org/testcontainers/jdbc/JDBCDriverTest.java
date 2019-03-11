package org.testcontainers.jdbc;


import com.googlecode.junittoolbox.ParallelParameterized;
import java.sql.SQLException;
import java.util.EnumSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(ParallelParameterized.class)
public class JDBCDriverTest {
    private enum Options {

        ScriptedSchema,
        CharacterSet,
        CustomIniFile,
        JDBCParams,
        PmdKnownBroken;}

    @Parameterized.Parameter
    public String jdbcUrl;

    @Parameterized.Parameter(1)
    public EnumSet<JDBCDriverTest.Options> options;

    @Test
    public void test() throws SQLException {
        performSimpleTest(jdbcUrl);
        if (options.contains(JDBCDriverTest.Options.ScriptedSchema)) {
            performTestForScriptedSchema(jdbcUrl);
        }
        if (options.contains(JDBCDriverTest.Options.JDBCParams)) {
            performTestForJDBCParamUsage(jdbcUrl);
        }
        if (options.contains(JDBCDriverTest.Options.CharacterSet)) {
            // Called twice to ensure that the query string parameters are used when
            // connections are created from cached containers.
            performSimpleTestWithCharacterSet(jdbcUrl);
            performSimpleTestWithCharacterSet(jdbcUrl);
            performTestForCharacterEncodingForInitialScriptConnection(jdbcUrl);
        }
        if (options.contains(JDBCDriverTest.Options.CustomIniFile)) {
            performTestForCustomIniFile(jdbcUrl);
        }
    }
}

