package com.zendesk.maxwell;


import com.github.shyiko.mysql.binlog.network.SSLMode;
import java.net.URISyntaxException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MaxwellMysqlConfigTest {
    @Test
    public void testBasicUri() {
        MaxwellMysqlConfig config = new MaxwellMysqlConfig();
        config.host = "localhost";
        config.port = 3306;
        config.user = "maxwell";
        config.password = "maxwell";
        config.database = "maxwell";
        config.sslMode = SSLMode.DISABLED;
        config.setJDBCOptions("autoReconnect=true&initialTimeout=2&maxReconnects=10");
        try {
            final String uri = config.getConnectionURI();
            Assert.assertThat(uri, CoreMatchers.is(CoreMatchers.equalTo("jdbc:mysql://localhost:3306/maxwell?connectTimeout=5000&zeroDateTimeBehavior=convertToNull&initialTimeout=2&autoReconnect=true&maxReconnects=10&useSSL=false&characterEncoding=UTF-8&tinyInt1isBit=false")));
        } catch (URISyntaxException e) {
            Assert.fail();
        }
    }

    @Test
    public void testSSLPreferred() {
        MaxwellMysqlConfig config = new MaxwellMysqlConfig();
        config.host = "localhost";
        config.port = 3306;
        config.user = "maxwell";
        config.password = "maxwell";
        config.database = "maxwell";
        config.sslMode = SSLMode.PREFERRED;
        try {
            final String uri = config.getConnectionURI();
            Assert.assertThat(uri, CoreMatchers.is(CoreMatchers.containsString("useSSL=true")));
            Assert.assertThat(uri, CoreMatchers.is(CoreMatchers.containsString("requireSSL=false")));
            Assert.assertThat(uri, CoreMatchers.is(CoreMatchers.containsString("verifyServerCertificate=false")));
        } catch (URISyntaxException e) {
            Assert.fail();
        }
    }

    @Test
    public void testSSLRequired() {
        MaxwellMysqlConfig config = new MaxwellMysqlConfig();
        config.host = "localhost";
        config.port = 3306;
        config.user = "maxwell";
        config.password = "maxwell";
        config.database = "maxwell";
        config.sslMode = SSLMode.REQUIRED;
        try {
            String uri = config.getConnectionURI();
            Assert.assertThat(uri, CoreMatchers.is(CoreMatchers.containsString("requireSSL=true")));
            Assert.assertThat(uri, CoreMatchers.is(CoreMatchers.containsString("useSSL=true")));
            Assert.assertThat(uri, CoreMatchers.is(CoreMatchers.containsString("verifyServerCertificate=false")));
        } catch (URISyntaxException e) {
            Assert.fail();
        }
    }

    @Test
    public void testSSLVerifyCA() {
        MaxwellMysqlConfig config = new MaxwellMysqlConfig();
        config.host = "localhost";
        config.port = 3306;
        config.user = "maxwell";
        config.password = "maxwell";
        config.database = "maxwell";
        config.sslMode = SSLMode.VERIFY_CA;
        try {
            final String uri = config.getConnectionURI();
            Assert.assertThat(uri, CoreMatchers.is(CoreMatchers.containsString("requireSSL=true")));
            Assert.assertThat(uri, CoreMatchers.is(CoreMatchers.containsString("useSSL=true")));
            Assert.assertThat(uri, CoreMatchers.is(CoreMatchers.containsString("verifyServerCertificate=false")));
        } catch (URISyntaxException e) {
            Assert.fail();
        }
    }

    @Test
    public void testSSLVerifyId() {
        MaxwellMysqlConfig config = new MaxwellMysqlConfig();
        config.host = "localhost";
        config.port = 3306;
        config.user = "maxwell";
        config.password = "maxwell";
        config.database = "maxwell";
        config.sslMode = SSLMode.VERIFY_IDENTITY;
        try {
            final String uri = config.getConnectionURI();
            Assert.assertThat(uri, CoreMatchers.is(CoreMatchers.containsString("requireSSL=true")));
            Assert.assertThat(uri, CoreMatchers.is(CoreMatchers.containsString("useSSL=true")));
            Assert.assertThat(uri, CoreMatchers.is(CoreMatchers.containsString("verifyServerCertificate=true")));
        } catch (URISyntaxException e) {
            Assert.fail();
        }
    }
}

