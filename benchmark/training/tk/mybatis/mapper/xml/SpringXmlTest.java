package tk.mybatis.mapper.xml;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 *
 *
 * @author liuzh
 */
public class SpringXmlTest {
    private ClassPathXmlApplicationContext context;

    @Test
    public void testCountryMapper() {
        context = new ClassPathXmlApplicationContext("tk/mybatis/mapper/xml/spring.xml");
        CountryMapper countryMapper = context.getBean(CountryMapper.class);
        List<Country> countries = selectAll();
        Assert.assertNotNull(countries);
        Assert.assertEquals(183, countries.size());
    }
}

