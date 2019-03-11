package tk.mybatis.mapper.annotation;


import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Test;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import tk.mybatis.mapper.MapperException;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Config;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.spring.annotation.MapperScan;


/**
 *
 *
 * @author liuzh
 */
public class SpringAnnotationTest {
    private AnnotationConfigApplicationContext applicationContext;

    @Test
    public void testMyBatisConfigRef() {
        applicationContext.register(SpringAnnotationTest.MyBatisConfigRef.class);
        startContext();
        CountryMapper countryMapper = applicationContext.getBean(CountryMapper.class);
        List<Country> countries = selectAll();
        Assert.assertNotNull(countries);
        Assert.assertEquals(183, countries.size());
    }

    @Test
    public void testMyBatisConfigProperties() {
        applicationContext.register(SpringAnnotationTest.MyBatisConfigProperties.class);
        startContext();
        CountryMapper countryMapper = applicationContext.getBean(CountryMapper.class);
        List<Country> countries = selectAll();
        Assert.assertNotNull(countries);
        Assert.assertEquals(183, countries.size());
    }

    @Test
    public void testMyBatisConfiguration() {
        applicationContext.register(SpringAnnotationTest.MyBatisConfiguration.class);
        startContext();
        CountryMapper countryMapper = applicationContext.getBean(CountryMapper.class);
        List<Country> countries = selectAll();
        Assert.assertNotNull(countries);
        Assert.assertEquals(183, countries.size());
    }

    @Test(expected = MapperException.class)
    public void testMyBatisConfigPropertiesError() {
        applicationContext.register(SpringAnnotationTest.MyBatisConfigPropertiesError.class);
        startContext();
        CountryMapper countryMapper = applicationContext.getBean(CountryMapper.class);
        List<Country> countries = selectAll();
        Assert.assertNotNull(countries);
        Assert.assertEquals(183, countries.size());
    }

    @Configuration
    @MapperScan(value = "tk.mybatis.mapper.annotation", mapperHelperRef = "mapperHelper")
    public static class MyBatisConfigRef {
        @Bean
        public DataSource dataSource() {
            return new EmbeddedDatabaseBuilder().addScript("tk/mybatis/mapper/xml/CreateDB.sql").build();
        }

        @Bean
        public DataSourceTransactionManager transactionManager() {
            return new DataSourceTransactionManager(dataSource());
        }

        @Bean
        public SqlSessionFactory sqlSessionFactory() throws Exception {
            SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
            sessionFactory.setDataSource(dataSource());
            return sessionFactory.getObject();
        }

        @Bean
        public MapperHelper mapperHelper() {
            Config config = new Config();
            List<Class> mappers = new ArrayList<Class>();
            mappers.add(Mapper.class);
            config.setMappers(mappers);
            MapperHelper mapperHelper = new MapperHelper();
            mapperHelper.setConfig(config);
            return mapperHelper;
        }
    }

    @Configuration
    @MapperScan(value = "tk.mybatis.mapper.annotation", properties = { "mappers=tk.mybatis.mapper.common.Mapper", "notEmpty=true" })
    public static class MyBatisConfigProperties {
        @Bean
        public DataSource dataSource() {
            return new EmbeddedDatabaseBuilder().addScript("tk/mybatis/mapper/xml/CreateDB.sql").build();
        }

        @Bean
        public DataSourceTransactionManager transactionManager() {
            return new DataSourceTransactionManager(dataSource());
        }

        @Bean
        public SqlSessionFactory sqlSessionFactory() throws Exception {
            SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
            sessionFactory.setDataSource(dataSource());
            return sessionFactory.getObject();
        }
    }

    @Configuration
    @MapperScan(value = "tk.mybatis.mapper.annotation", properties = { // ??????
    "mapperstk.mybatis.mapper.common.Mapper", "notEmpty=true" })
    public static class MyBatisConfigPropertiesError {
        @Bean
        public DataSource dataSource() {
            return new EmbeddedDatabaseBuilder().addScript("tk/mybatis/mapper/xml/CreateDB.sql").build();
        }

        @Bean
        public DataSourceTransactionManager transactionManager() {
            return new DataSourceTransactionManager(dataSource());
        }

        @Bean
        public SqlSessionFactory sqlSessionFactory() throws Exception {
            SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
            sessionFactory.setDataSource(dataSource());
            return sessionFactory.getObject();
        }
    }

    @Configuration
    @MapperScan("tk.mybatis.mapper.annotation")
    public static class MyBatisConfiguration {
        @Bean
        public DataSource dataSource() {
            return new EmbeddedDatabaseBuilder().addScript("tk/mybatis/mapper/xml/CreateDB.sql").build();
        }

        @Bean
        public DataSourceTransactionManager transactionManager() {
            return new DataSourceTransactionManager(dataSource());
        }

        @Bean
        public SqlSessionFactory sqlSessionFactory() throws Exception {
            SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
            sessionFactory.setDataSource(dataSource());
            tk.mybatis.mapper.session.Configuration configuration = new tk.mybatis.mapper.session.Configuration();
            configuration.setMapperHelper(new MapperHelper());
            sessionFactory.setConfiguration(configuration);
            return sessionFactory.getObject();
        }
    }
}

