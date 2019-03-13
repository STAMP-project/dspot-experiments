package com.navercorp.pinpoint.plugin.druid;


import com.alibaba.druid.pool.DruidDataSource;
import com.navercorp.pinpoint.bootstrap.plugin.test.Expectations;
import com.navercorp.pinpoint.bootstrap.plugin.test.PluginTestVerifier;
import com.navercorp.pinpoint.bootstrap.plugin.test.PluginTestVerifierHolder;
import com.navercorp.pinpoint.plugin.AgentPath;
import com.navercorp.pinpoint.test.plugin.Dependency;
import com.navercorp.pinpoint.test.plugin.PinpointAgent;
import com.navercorp.pinpoint.test.plugin.PinpointConfig;
import com.navercorp.pinpoint.test.plugin.PinpointPluginTestSuite;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(PinpointPluginTestSuite.class)
@PinpointAgent(AgentPath.PATH)
@Dependency({ "com.alibaba:druid:[1.0.0][1.0.31],[1.1.0,)", "com.h2database:h2:1.4.191" })
@PinpointConfig("druid/pinpoint-druid-test.config")
public class DruidIT {
    private static final String serviceType = "DRUID";

    private static final String JDBC_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";

    private static Method getConnectionMethod;

    private static Method closeConnectionMethod;

    @Test
    public void test() throws InterruptedException, SQLException {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(DruidIT.JDBC_URL);
        dataSource.setValidationQuery("select 'x'");
        dataSource.setUsername("test");
        dataSource.setPassword("test");
        dataSource.init();
        try {
            Connection connection = dataSource.getConnection();
            Assert.assertNotNull(connection);
            Thread.sleep(500);
            connection.close();
            Thread.sleep(500);
            PluginTestVerifier verifier = PluginTestVerifierHolder.getInstance();
            verifier.printCache();
            verifier.verifyTrace(Expectations.event(DruidIT.serviceType, DruidIT.getConnectionMethod));
            verifier.verifyTrace(Expectations.event(DruidIT.serviceType, DruidIT.closeConnectionMethod));
        } finally {
            if (dataSource != null) {
                dataSource.close();
            }
        }
    }
}

