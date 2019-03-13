package plm;


import java.util.Collection;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import plm.beans.Config;
import plm.common.exceptions.CheckException;
import plm.services.ConfigService;


/**
 * SpringMVC????
 *
 * ???????tomcat-embed-core
 *
 * @author ??? https://github.com/xwjie/PLMCodeTemplate
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/spring/root-context.xml", "file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml" })
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CongfigServiceTest {
    private static final String CONFIG_NAME = "?????";

    @Autowired
    ConfigService configService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void test0_Full() {
        Config config = new Config();
        config.setName(CongfigServiceTest.CONFIG_NAME);
        config.setValue("????");
        // ????
        long newId = configService.add(config);
        Assert.assertTrue((newId > 1));
        // ????
        Collection<Config> all = configService.getAll();
        Assert.assertTrue(((all.size()) == 1));
        // ????
        boolean result = configService.delete(newId);
        Assert.assertTrue(result);
        // ????
        Collection<Config> all2 = configService.getAll();
        Assert.assertTrue(((all2.size()) == 0));
    }

    @Test
    public void test1_addConfigException() {
        System.out.println("\n\n--\u6d4b\u8bd5[\u53c2\u6570\u4e3a\u7a7a]---\n\n");
        thrown.expect(CheckException.class);
        thrown.expectMessage("????");
        configService.add(null);
    }

    @Test
    public void test2_addConfigException() {
        System.out.println("\n\n--\u6d4b\u8bd5[\u53d6\u503c\u4e3a\u7a7a]---\n\n");
        thrown.expect(CheckException.class);
        thrown.expectMessage("????");
        Config config = new Config();
        config.setName(CongfigServiceTest.CONFIG_NAME);
        config.setValue(null);
        configService.add(config);
    }

    @Test
    public void test3_addConfigException() {
        // ?????
        {
            Config config = new Config();
            config.setName(CongfigServiceTest.CONFIG_NAME);
            config.setValue("????");
            // ????
            long newId = configService.add(config);
            Assert.assertTrue((newId > 1));
        }
        // ?????
        {
            System.out.println("\n\n--\u6d4b\u8bd5[\u540d\u79f0\u5df2\u7ecf\u5b58\u5728]---\n\n");
            thrown.expect(CheckException.class);
            thrown.expectMessage("??????");
            Config config = new Config();
            config.setName(CongfigServiceTest.CONFIG_NAME);
            config.setValue("https://github.com/xwjie");
            configService.add(config);
        }
    }
}

