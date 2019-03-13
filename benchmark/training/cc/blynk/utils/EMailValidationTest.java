package cc.blynk.utils;


import cc.blynk.utils.validators.BlynkEmailValidator;
import org.junit.Assert;
import org.junit.Test;


/**
 * User: ddumanskiy
 * Date: 8/11/13
 * Time: 6:43 PM
 */
public class EMailValidationTest {
    @Test
    public void testAllValid() {
        String[] mailList = new String[]{ "xxxx.yyy@rsa.rohde-schwarz.com", "1@mail.ru", "google@gmail.com", "dsasd234e021-0+@mail.ua", "ddd@yahoo.com", "mmmm@yahoo.com", "mmmmm-100@yahoo.com", "mmmmm.100@yahoo.com", "mmmm111@mmmm.com", "mmmm-100@mmmm.net", "mmmm.100@mmmm.com.au", "mmmm@1.com", "mmmm@gmail.com.com", "mmmm+100@gmail.com", "bla@bla.com.ua", "bla@bla.cc", "mmmm-100@yahoo-test.com" };
        for (String email : mailList) {
            Assert.assertFalse(email, BlynkEmailValidator.isNotValidEmail(email));
        }
    }

    @Test
    public void testAllInValid() {
        String[] mailList = new String[]{ "mmmm", "mmmm@.com.my", "mmm@hey.con", "mmm@hey.cpm", "mmm@hey.comcom", "mmm@hey.fe", "mmm@hey.hshs", "mmm@hey.aa", "mmm@hey.cim", "mmmm123@.com.com", ".mmmm@mmmm.com", "mmmm()*@gmail.com", "mmmm..2002@gmail.com", "mmmm.@gmail.com", "mmmm@mmmm@gmail.com", "ji?pui@gmail.com", "bla@bla" };
        for (String email : mailList) {
            Assert.assertTrue(email, BlynkEmailValidator.isNotValidEmail(email));
        }
    }
}

