package org.nlpcn.es4sql;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.ElasticSearchDruidDataSourceFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by allwefantasy on 8/26/16.
 */
public class JDBCTests {
    @Test
    public void testJDBC() throws Exception {
        Properties properties = new Properties();
        properties.put(DruidDataSourceFactory.PROP_URL, ("jdbc:elasticsearch://127.0.0.1:9300/" + (TestsConstants.TEST_INDEX_ACCOUNT)));
        properties.put(DruidDataSourceFactory.PROP_CONNECTIONPROPERTIES, "client.transport.ignore_cluster_name=true");
        DruidDataSource dds = ((DruidDataSource) (ElasticSearchDruidDataSourceFactory.createDataSource(properties)));
        Connection connection = dds.getConnection();
        PreparedStatement ps = connection.prepareStatement((("SELECT  gender,lastname,age from  " + (TestsConstants.TEST_INDEX_ACCOUNT)) + " where lastname='Heath'"));
        ResultSet resultSet = ps.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();
        MatcherAssert.assertThat(metaData.getColumnName(1), Matchers.equalTo("gender"));
        MatcherAssert.assertThat(metaData.getColumnName(2), Matchers.equalTo("lastname"));
        MatcherAssert.assertThat(metaData.getColumnName(3), Matchers.equalTo("age"));
        List<String> result = new ArrayList<String>();
        while (resultSet.next()) {
            result.add((((((resultSet.getString("lastname")) + ",") + (resultSet.getInt("age"))) + ",") + (resultSet.getString("gender"))));
        } 
        ps.close();
        connection.close();
        dds.close();
        Assert.assertTrue(((result.size()) == 1));
        Assert.assertTrue(result.get(0).equals("Heath,39,F"));
    }
}

