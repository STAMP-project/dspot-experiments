package jenkins.model;


import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;

import static IdStrategy.CASE_INSENSITIVE;


public class IdStrategyTest {
    @Test
    public void caseInsensitive() {
        IdStrategy idStrategy = new IdStrategy.CaseInsensitive();
        assertRestrictedNames(idStrategy);
        Assert.assertThat(idStrategy.idFromFilename("foo"), Is.is("foo"));
        Assert.assertThat(idStrategy.idFromFilename("foo$002fbar"), Is.is("foo/bar"));
        Assert.assertThat(idStrategy.idFromFilename("..$002ftest"), Is.is("../test"));
        Assert.assertThat(idStrategy.idFromFilename("0123 _-@$007ea"), Is.is("0123 _-@~a"));
        Assert.assertThat(idStrategy.idFromFilename("foo$002e"), Is.is("foo."));
        Assert.assertThat(idStrategy.idFromFilename("$002dfoo"), Is.is("-foo"));
        // Should not return the same username due to case insensitivity
        Assert.assertThat(idStrategy.idFromFilename("Foo"), Is.is("foo"));
        Assert.assertThat(idStrategy.idFromFilename("Foo$002fBar"), Is.is("foo/bar"));
        Assert.assertThat(idStrategy.idFromFilename("..$002fTest"), Is.is("../test"));
        Assert.assertThat(idStrategy.idFromFilename("$006eul"), Is.is("nul"));
        Assert.assertThat(idStrategy.idFromFilename("~foo"), Is.is("~foo"));
        Assert.assertThat(idStrategy.idFromFilename("0123 _-@~a"), Is.is("0123 _-@~a"));
        Assert.assertThat(idStrategy.idFromFilename("big$money"), Is.is("big$money"));
        Assert.assertThat(idStrategy.idFromFilename("$00c1aaa"), Is.is("\u00e1aaa"));
        Assert.assertThat(idStrategy.idFromFilename("$00e1aaa"), Is.is("\u00e1aaa"));
        Assert.assertThat(idStrategy.idFromFilename("aaaa$00e1"), Is.is("aaaa\u00e1"));
        Assert.assertThat(idStrategy.idFromFilename("aaaa$00e1kkkk"), Is.is("aaaa\u00e1kkkk"));
        Assert.assertThat(idStrategy.idFromFilename("aa$00e1zz$00e9pp"), Is.is("aa\u00e1zz\u00e9pp"));
        Assert.assertThat(idStrategy.idFromFilename("$306f$56fd$5185$3067$6700$5927"), Is.is("\u306f\u56fd\u5185\u3067\u6700\u5927"));
        Assert.assertThat(idStrategy.idFromFilename("$00E1aaa"), Is.is("$00e1aaa"));
        Assert.assertThat(idStrategy.idFromFilename("$001gggg"), Is.is("$001gggg"));
        Assert.assertThat(idStrategy.idFromFilename("rrr$t123"), Is.is("rrr$t123"));
    }

    @Test
    public void caseInsensitivePassesThroughOldLegacy() {
        IdStrategy idStrategy = new IdStrategy.CaseInsensitive();
        Assert.assertThat(idStrategy.idFromFilename("make\u1000000"), Is.is("make\u1000000"));
        Assert.assertThat(idStrategy.idFromFilename("\u306f\u56fd\u5185\u3067\u6700\u5927"), Is.is("\u306f\u56fd\u5185\u3067\u6700\u5927"));
        Assert.assertThat(idStrategy.idFromFilename("~fred"), Is.is("~fred"));
        Assert.assertThat(idStrategy.idFromFilename("~1fred"), Is.is("~1fred"));
    }

    @Test
    public void caseSensitive() {
        IdStrategy idStrategy = new IdStrategy.CaseSensitive();
        assertRestrictedNames(idStrategy);
        Assert.assertThat(idStrategy.idFromFilename("foo"), Is.is("foo"));
        Assert.assertThat(idStrategy.idFromFilename("~foo"), Is.is("Foo"));
        Assert.assertThat(idStrategy.idFromFilename("foo$002fbar"), Is.is("foo/bar"));
        Assert.assertThat(idStrategy.idFromFilename("~foo$002f~bar"), Is.is("Foo/Bar"));
        Assert.assertThat(idStrategy.idFromFilename("..$002ftest"), Is.is("../test"));
        Assert.assertThat(idStrategy.idFromFilename("..$002f~test"), Is.is("../Test"));
        Assert.assertThat(idStrategy.idFromFilename("0123 _-@$007ea"), Is.is("0123 _-@~a"));
        Assert.assertThat(idStrategy.idFromFilename("0123 _-@~a"), Is.is("0123 _-@A"));
        Assert.assertThat(idStrategy.idFromFilename("foo$002e"), Is.is("foo."));
        Assert.assertThat(idStrategy.idFromFilename("$002dfoo"), Is.is("-foo"));
        Assert.assertThat(idStrategy.idFromFilename("~con"), Is.is("Con"));
        Assert.assertThat(idStrategy.idFromFilename("~prn"), Is.is("Prn"));
        Assert.assertThat(idStrategy.idFromFilename("~aux"), Is.is("Aux"));
        Assert.assertThat(idStrategy.idFromFilename("~nul"), Is.is("Nul"));
        Assert.assertThat(idStrategy.idFromFilename("~com1"), Is.is("Com1"));
        Assert.assertThat(idStrategy.idFromFilename("~lpt1"), Is.is("Lpt1"));
        Assert.assertThat(idStrategy.idFromFilename("big$money"), Is.is("big$money"));
        Assert.assertThat(idStrategy.idFromFilename("$00c1aaa"), Is.is("\u00c1aaa"));
        Assert.assertThat(idStrategy.idFromFilename("$00e1aaa"), Is.is("\u00e1aaa"));
        Assert.assertThat(idStrategy.idFromFilename("aaaa$00e1"), Is.is("aaaa\u00e1"));
        Assert.assertThat(idStrategy.idFromFilename("aaaa$00e1kkkk"), Is.is("aaaa\u00e1kkkk"));
        Assert.assertThat(idStrategy.idFromFilename("aa$00e1zz$00e9pp"), Is.is("aa\u00e1zz\u00e9pp"));
        Assert.assertThat(idStrategy.idFromFilename("$306f$56fd$5185$3067$6700$5927"), Is.is("\u306f\u56fd\u5185\u3067\u6700\u5927"));
        Assert.assertThat(idStrategy.idFromFilename("$00E1aaa"), Is.is("$00E1aaa"));
        Assert.assertThat(idStrategy.idFromFilename("$001gggg"), Is.is("$001gggg"));
        Assert.assertThat(idStrategy.idFromFilename("rRr$t123"), Is.is("rRr$t123"));
        Assert.assertThat(idStrategy.idFromFilename("iiii _-@$007~ea"), Is.is("iiii _-@$007Ea"));
    }

    @Test
    public void caseSensitivePassesThroughOldLegacy() {
        IdStrategy idStrategy = new IdStrategy.CaseSensitive();
        Assert.assertThat(idStrategy.idFromFilename("make\u1000000"), Is.is("make\u1000000"));
        Assert.assertThat(idStrategy.idFromFilename("\u306f\u56fd\u5185\u3067\u6700\u5927"), Is.is("\u306f\u56fd\u5185\u3067\u6700\u5927"));
        Assert.assertThat(idStrategy.idFromFilename("~1fred"), Is.is("~1fred"));
    }

    @Test
    public void testEqualsCaseInsensitive() {
        IdStrategy idStrategy = CASE_INSENSITIVE;
        Assert.assertTrue(idStrategy.equals("user1", "User1"));
        Assert.assertTrue(idStrategy.equals("User1", "user1"));
        Assert.assertFalse(idStrategy.equals("User1", "user2"));
        String sameUser = "sameUser";
        Assert.assertTrue(idStrategy.equals(sameUser, sameUser));
    }

    @Test
    public void testEqualsCaseSensitive() {
        IdStrategy idStrategy = new IdStrategy.CaseSensitive();
        Assert.assertFalse(idStrategy.equals("user1", "User1"));
        Assert.assertFalse(idStrategy.equals("User1", "user1"));
        Assert.assertFalse(idStrategy.equals("User1", "user2"));
        String sameUser = "sameUser";
        Assert.assertTrue(idStrategy.equals(sameUser, sameUser));
    }

    @Test
    public void testEqualsCaseSensitiveEmailAddress() {
        IdStrategy idStrategy = new IdStrategy.CaseSensitiveEmailAddress();
        Assert.assertFalse(idStrategy.equals("john.smith@acme.org", "John.Smith@acme.org"));
        Assert.assertFalse(idStrategy.equals("john.smith@acme.org", "John.Smith@ACME.org"));
        Assert.assertFalse(idStrategy.equals("john.smith@acme.org", "John.Smith@ACME.org"));
        Assert.assertFalse(idStrategy.equals("john.smith@acme.org", "John.Smith@acme.ORG"));
        Assert.assertFalse(idStrategy.equals("John@smith@acme.org", "john@Smith@acme.ORG"));
        String sameUser = "john.smith@acme.org";
        Assert.assertTrue(idStrategy.equals(sameUser, sameUser));
        Assert.assertTrue(idStrategy.equals("John.Smith@ACME.org", "John.Smith@acme.org"));
        Assert.assertTrue(idStrategy.equals("John.Smith@acme.ORG", "John.Smith@acme.org"));
        Assert.assertTrue(idStrategy.equals("john@smith@ACME.org", "john@smith@acme.org"));
    }

    @Test
    public void testKeyForCaseInsensitive() {
        IdStrategy idStrategy = CASE_INSENSITIVE;
        Assert.assertThat(idStrategy.keyFor("user1"), Is.is("user1"));
        Assert.assertThat(idStrategy.keyFor("User1"), Is.is("user1"));
        Assert.assertThat(idStrategy.keyFor("USER1"), Is.is("user1"));
    }

    @Test
    public void testKeyForCaseSensitive() {
        IdStrategy idStrategy = new IdStrategy.CaseSensitive();
        Assert.assertThat(idStrategy.keyFor("user1"), Is.is("user1"));
        Assert.assertThat(idStrategy.keyFor("User1"), Is.is("User1"));
        Assert.assertThat(idStrategy.keyFor("USER1"), Is.is("USER1"));
    }

    @Test
    public void testKeyForCaseSensitiveEmailAddress() {
        IdStrategy idStrategy = new IdStrategy.CaseSensitiveEmailAddress();
        Assert.assertThat(idStrategy.keyFor("john.smith@acme.org"), Is.is("john.smith@acme.org"));
        Assert.assertThat(idStrategy.keyFor("John.Smith@acme.org"), Is.is("John.Smith@acme.org"));
        Assert.assertThat(idStrategy.keyFor("John.Smith@ACME.org"), Is.is("John.Smith@acme.org"));
        Assert.assertThat(idStrategy.keyFor("John.Smith@acme.ORG"), Is.is("John.Smith@acme.org"));
        Assert.assertThat(idStrategy.keyFor("john.smith"), Is.is("john.smith"));
        Assert.assertThat(idStrategy.keyFor("John.Smith"), Is.is("John.Smith"));
        Assert.assertThat(idStrategy.keyFor("john@smith@acme.org"), Is.is("john@smith@acme.org"));
        Assert.assertThat(idStrategy.keyFor("John@Smith@acme.org"), Is.is("John@Smith@acme.org"));
    }

    @Test
    public void testCompareCaseInsensitive() {
        IdStrategy idStrategy = CASE_INSENSITIVE;
        Assert.assertTrue(((idStrategy.compare("user1", "user2")) < 0));
        Assert.assertTrue(((idStrategy.compare("user2", "user1")) > 0));
        Assert.assertTrue(((idStrategy.compare("user1", "user1")) == 0));
        Assert.assertTrue(((idStrategy.compare("USER1", "user2")) < 0));
        Assert.assertTrue(((idStrategy.compare("USER2", "user1")) > 0));
        Assert.assertTrue(((idStrategy.compare("User1", "user1")) == 0));
    }

    @Test
    public void testCompareCaseSensitive() {
        IdStrategy idStrategy = new IdStrategy.CaseSensitive();
        Assert.assertTrue(((idStrategy.compare("user1", "user2")) < 0));
        Assert.assertTrue(((idStrategy.compare("user2", "user1")) > 0));
        Assert.assertTrue(((idStrategy.compare("user1", "user1")) == 0));
        Assert.assertTrue(((idStrategy.compare("USER1", "user2")) < 0));
        Assert.assertTrue(((idStrategy.compare("USER2", "user1")) < 0));
        Assert.assertTrue(((idStrategy.compare("User1", "user1")) < 0));
    }

    @Test
    public void testCompareCaseSensitiveEmail() {
        IdStrategy idStrategy = new IdStrategy.CaseSensitiveEmailAddress();
        Assert.assertTrue(((idStrategy.compare("john.smith@acme.org", "john.smith@acme.org")) == 0));
        Assert.assertTrue(((idStrategy.compare("John.Smith@acme.org", "John.Smith@acme.org")) == 0));
        Assert.assertTrue(((idStrategy.compare("John.Smith@ACME.org", "John.Smith@acme.org")) == 0));
        Assert.assertTrue(((idStrategy.compare("John.Smith@acme.ORG", "John.Smith@acme.org")) == 0));
        Assert.assertTrue(((idStrategy.compare("john.smith", "john.smith")) == 0));
        Assert.assertTrue(((idStrategy.compare("John.Smith", "John.Smith")) == 0));
        Assert.assertTrue(((idStrategy.compare("john@smith@acme.org", "john@smith@acme.org")) == 0));
        Assert.assertTrue(((idStrategy.compare("John@Smith@acme.org", "John@Smith@acme.org")) == 0));
        Assert.assertTrue(((idStrategy.compare("John.Smith@acme.org", "john.smith@acme.org")) < 0));
        Assert.assertTrue(((idStrategy.compare("john.smith@acme.org", "John.Smith@acme.org")) > 0));
    }
}

