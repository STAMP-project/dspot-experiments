package tk.mybatis.mapper.additional.insertlist;


import java.util.ArrayList;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;
import tk.mybatis.mapper.additional.BaseTest;


public class InsertListMapperTest extends BaseTest {
    private String[][] countries = new String[][]{ new String[]{ "Angola", "AO" }, new String[]{ "Afghanistan", "AF" }, new String[]{ "Albania", "AL" }, new String[]{ "Algeria", "DZ" }, new String[]{ "Andorra", "AD" }, new String[]{ "Anguilla", "AI" }, new String[]{ "Antigua and Barbuda", "AG" }, new String[]{ "Argentina", "AR" }, new String[]{ "Armenia", "AM" }, new String[]{ "Australia", "AU" }, new String[]{ "Austria", "AT" }, new String[]{ "Azerbaijan", "AZ" }, new String[]{ "Bahamas", "BS" }, new String[]{ "Bahrain", "BH" }, new String[]{ "Bangladesh", "BD" }, new String[]{ "Barbados", "BB" }, new String[]{ "Belarus", "BY" }, new String[]{ "Belgium", "BE" }, new String[]{ "Belize", "BZ" }, new String[]{ "Benin", "BJ" }, new String[]{ "Bermuda Is.", "BM" }, new String[]{ "Bolivia", "BO" }, new String[]{ "Botswana", "BW" }, new String[]{ "Brazil", "BR" }, new String[]{ "Brunei", "BN" }, new String[]{ "Bulgaria", "BG" }, new String[]{ "Burkina-faso", "BF" }, new String[]{ "Burma", "MM" }, new String[]{ "Burundi", "BI" }, new String[]{ "Cameroon", "CM" }, new String[]{ "Canada", "CA" }, new String[]{ "Central African Republic", "CF" }, new String[]{ "Chad", "TD" }, new String[]{ "Chile", "CL" }, new String[]{ "China", "CN" } };

    @Test
    public void testInsertList() {
        SqlSession sqlSession = getSqlSession();
        try {
            UserMapper mapper = sqlSession.getMapper(UserMapper.class);
            List<User> userList = new ArrayList<User>(countries.length);
            for (int i = 0; i < (countries.length); i++) {
                userList.add(new User(countries[i][0], countries[i][1]));
            }
            Assert.assertEquals(countries.length, insertList(userList));
            for (User user : userList) {
                Assert.assertNotNull(user.getId());
                System.out.println(user.getId());
            }
        } finally {
            sqlSession.close();
        }
    }
}

