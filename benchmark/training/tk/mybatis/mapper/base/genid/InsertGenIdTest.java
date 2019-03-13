package tk.mybatis.mapper.base.genid;


import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;
import tk.mybatis.mapper.base.BaseTest;


/**
 *
 *
 * @author liuzh
 */
public class InsertGenIdTest extends BaseTest {
    private String[][] countries = new String[][]{ new String[]{ "Angola", "AO" }, new String[]{ "Afghanistan", "AF" }, new String[]{ "Albania", "AL" }, new String[]{ "Algeria", "DZ" }, new String[]{ "Andorra", "AD" }, new String[]{ "Anguilla", "AI" }, new String[]{ "Antigua and Barbuda", "AG" }, new String[]{ "Argentina", "AR" }, new String[]{ "Armenia", "AM" }, new String[]{ "Australia", "AU" }, new String[]{ "Austria", "AT" }, new String[]{ "Azerbaijan", "AZ" }, new String[]{ "Bahamas", "BS" }, new String[]{ "Bahrain", "BH" }, new String[]{ "Bangladesh", "BD" }, new String[]{ "Barbados", "BB" }, new String[]{ "Belarus", "BY" }, new String[]{ "Belgium", "BE" }, new String[]{ "Belize", "BZ" }, new String[]{ "Benin", "BJ" }, new String[]{ "Bermuda Is.", "BM" }, new String[]{ "Bolivia", "BO" }, new String[]{ "Botswana", "BW" }, new String[]{ "Brazil", "BR" }, new String[]{ "Brunei", "BN" }, new String[]{ "Bulgaria", "BG" }, new String[]{ "Burkina-faso", "BF" }, new String[]{ "Burma", "MM" }, new String[]{ "Burundi", "BI" }, new String[]{ "Cameroon", "CM" }, new String[]{ "Canada", "CA" }, new String[]{ "Central African Republic", "CF" }, new String[]{ "Chad", "TD" }, new String[]{ "Chile", "CL" }, new String[]{ "China", "CN" } };

    @Test
    public void testGenId() {
        SqlSession sqlSession = getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            for (int i = 0; i < (countries.length); i++) {
                Country country = new Country(countries[i][0], countries[i][1]);
                Assert.assertEquals(1, mapper.insert(country));
                Assert.assertNotNull(country.getId());
                System.out.println(country.getId());
            }
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testGenIdWithExistsId() {
        SqlSession sqlSession = getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            Country country = new Country("test", "T");
            country.setId(9999L);
            Assert.assertEquals(1, mapper.insert(country));
            Assert.assertNotNull(country.getId());
            Assert.assertEquals(new Long(9999), country.getId());
            System.out.println(country.getId());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testUUID() {
        SqlSession sqlSession = getSqlSession();
        try {
            UserMapper mapper = sqlSession.getMapper(UserMapper.class);
            for (int i = 0; i < (countries.length); i++) {
                User user = new User(countries[i][0], countries[i][1]);
                Assert.assertEquals(1, insert(user));
                Assert.assertNotNull(user.getId());
                System.out.println(user.getId());
            }
        } finally {
            sqlSession.close();
        }
    }
}

