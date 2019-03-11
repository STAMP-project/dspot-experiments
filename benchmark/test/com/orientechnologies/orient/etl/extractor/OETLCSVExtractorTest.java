package com.orientechnologies.orient.etl.extractor;


import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.etl.OETLBaseTest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by frank on 10/5/15.
 */
public class OETLCSVExtractorTest extends OETLBaseTest {
    @Test
    public void testOneObject() {
        configure("{source: { content: { value: \'name,surname\nJay,Miner\' } }, extractor : { csv: {} }, loader: { test: {} } }");
        proc.execute();
        Assert.assertEquals(1, getResult().size());
        ODocument doc = getResult().get(0);
        Assert.assertEquals(2, doc.fields());
        Assert.assertEquals("Jay", doc.field("name"));
        Assert.assertEquals("Miner", doc.field("surname"));
    }

    @Test
    public void testEmpty() {
        String cfgJson = "{source: { content: { value: '' }  }, extractor : { csv: {} }, loader: { test: {} } }";
        configure(cfgJson);
        proc.execute();
        Assert.assertEquals(0, getResult().size());
    }

    @Test
    public void testSmallSet() {
        String content = "name,surname,id";
        for (int i = 0; i < (names.length); ++i)
            content += (((("\n" + (names[i])) + ",") + (surnames[i])) + ",") + i;

        configure((("{source: { content: { value: '" + content) + "' } }, extractor : { csv: {} },  loader: { test: {} } }"));
        proc.execute();
        Assert.assertEquals(names.length, getResult().size());
        int i = 0;
        for (ODocument doc : getResult()) {
            Assert.assertEquals(3, doc.fields());
            Assert.assertEquals(names[i], doc.field("name"));
            Assert.assertEquals(surnames[i], doc.field("surname"));
            Assert.assertThat(doc.<Integer>field("id")).isEqualTo(i);
            i++;
        }
    }

    @Test
    public void testSkipFromTwoToFour() {
        String content = "name,surname,id";
        for (int i = 0; i < (names.length); ++i)
            content += (((("\n" + (names[i])) + ",") + (surnames[i])) + ",") + i;

        configure((((("{source: { content: { value: '" + content) + "' } }, ") + "extractor : { csv: {skipFrom: 1, skipTo: 4} },  ") + "loader: { test: {} } }"));
        proc.execute();
        Assert.assertThat(getResult()).hasSize(((names.length) - 4));
    }

    @Test
    public void testDateTypeAutodetection() {
        String cfgJson = "{source: { content: { value: \'BirthDay\n2008-04-30\' }  }, extractor : { csv: {} }, loader: { test: {} } }";
        configure(cfgJson);
        proc.execute();
        List<ODocument> res = getResult();
        ODocument doc = res.get(0);
        Date birthday = doc.field("BirthDay");
        Assert.assertEquals(2008, ((birthday.getYear()) + 1900));
        Assert.assertEquals(4, ((birthday.getMonth()) + 1));
        Assert.assertEquals(30, birthday.getDate());
    }

    @Test
    public void testDateTypeAutodetectionWithCustomDateFormat() {
        String cfgJson = "{source: { content: { value: \'BirthDay\n30-04-2008\' }  }, extractor : { csv: {dateFormat: \"dd-MM-yyyy\"} }, loader: { test: {} } }";
        configure(cfgJson);
        proc.execute();
        List<ODocument> res = getResult();
        ODocument doc = res.get(0);
        Date birthday = doc.field("BirthDay");
        Assert.assertEquals(2008, ((birthday.getYear()) + 1900));
        Assert.assertEquals(4, ((birthday.getMonth()) + 1));
        Assert.assertEquals(30, birthday.getDate());
    }

    @Test
    public void testStringInDblQuotes() throws Exception {
        String cfgJson = "{source: { content: { value: \'text\n\"Hello, quotes are here!\"\' }  }, extractor : { csv: {} }, loader: { test: {} } }";
        configure(cfgJson);
        proc.execute();
        List<ODocument> res = getResult();
        Assert.assertFalse(res.isEmpty());
        ODocument doc = res.get(0);
        String text = doc.field("text");
        Assert.assertEquals("Hello, quotes are here!", text);
    }

    @Test
    public void testStringStartedFromDigit() throws Exception {
        String cfgJson = "{source: { content: { value: \'address\n\"401 Congress Ave, Suite 2450\"\' }  }, extractor : { csv: {} }, loader: { test: {} } }";
        configure(cfgJson);
        proc.execute();
        List<ODocument> res = getResult();
        Assert.assertFalse(res.isEmpty());
        ODocument doc = res.get(0);
        String address = doc.field("address");
        Assert.assertEquals("401 Congress Ave, Suite 2450", address);
    }

    @Test
    public void testFloat() {
        String cfgJson = "{source: { content: { value: \'firstNumber\n10.78\'}  }, extractor : { csv: {} }, loader: { test: {} } }";
        configure(cfgJson);
        proc.execute();
        List<ODocument> res = getResult();
        Assert.assertFalse(res.isEmpty());
        ODocument doc = res.get(0);
        // assertEquals(10.78f, doc.field("firstNumber"));
        Assert.assertThat(doc.<Float>field("firstNumber")).isEqualTo(10.78F);
    }

    @Test
    public void testFloatWithinQuotesAndDotAsDecimalSeparator() {
        String cfgJson = "{source: { content: { value: \'firstNumber\n\"10.78\"\'}  }, extractor : { csv: {} }, loader: { test: {} } }";
        configure(cfgJson);
        proc.execute();
        List<ODocument> res = getResult();
        Assert.assertFalse(res.isEmpty());
        ODocument doc = res.get(0);
        // assertEquals(10.78f, doc.field("firstNumber"));
        Assert.assertThat(doc.<Float>field("firstNumber")).isEqualTo(10.78F);
    }

    @Test
    public void testFloatWithinQuotesAndCommaAsDecimalSeparator() {
        String cfgJson = "{source: { content: { value: \'firstNumber\n\"10,78\"\'}  }, extractor : { csv: {} }, loader: { test: {} } }";
        configure(cfgJson);
        proc.execute();
        List<ODocument> res = getResult();
        Assert.assertFalse(res.isEmpty());
        ODocument doc = res.get(0);
        // assertEquals(10.78f, doc.field("firstNumber"));
        Assert.assertThat(doc.<Float>field("firstNumber")).isEqualTo(10.78F);
    }

    @Test
    public void testDouble() {
        Double minDouble = 5.402823466385289E38;
        String cfgJson = "{source: { content: { value: \'secondNumber\n540282346638528870000000000000000000000.0\'}  }, extractor : { csv: {} }, loader: { test: {} } }";
        configure(cfgJson);
        proc.execute();
        List<ODocument> res = getResult();
        Assert.assertFalse(res.isEmpty());
        ODocument doc = res.get(0);
        Assert.assertEquals(minDouble, ((Double) (doc.field("secondNumber"))));
    }

    @Test
    public void testDoubleWithingQuotes() {
        Double minDouble = 5.402823466385289E38;
        String cfgJson = "{source: { content: { value: \'secondNumber\n\"540282346638528870000000000000000000000.0\"\'}  }, extractor : { csv: {} }, loader: { test: {} } }";
        configure(cfgJson);
        proc.execute();
        List<ODocument> res = getResult();
        Assert.assertFalse(res.isEmpty());
        ODocument doc = res.get(0);
        Assert.assertEquals(minDouble, ((Double) (doc.field("secondNumber"))));
    }

    @Test
    public void testDoubleWithingQuotesAndCommaAsDecimalSeparator() {
        Double minDouble = 5.402823466385289E38;
        String cfgJson = "{source: { content: { value: \'secondNumber\n\"540282346638528870000000000000000000000,0\"\'}  }, extractor : { csv: {} }, loader: { test: {} } }";
        configure(cfgJson);
        proc.execute();
        List<ODocument> res = getResult();
        Assert.assertFalse(res.isEmpty());
        ODocument doc = res.get(0);
        Assert.assertEquals(minDouble, ((Double) (doc.field("secondNumber"))));
    }

    @Test
    public void testInteger() {
        String cfgJson = "{source: { content: { value: \'number\n100\'} }, extractor : { csv : {} }, loader : { test: {} } }";
        configure(cfgJson);
        proc.execute();
        List<ODocument> res = getResult();
        Assert.assertFalse(res.isEmpty());
        ODocument doc = res.get(0);
        Assert.assertEquals(new Integer(100), ((Integer) (doc.field("number"))));
    }

    @Test
    public void testIntegerWithingQuotes() {
        String cfgJson = "{source: { content: { value: \'number\n\"100\"\'} }, extractor : { csv : {} }, loader : { test: {} } }";
        configure(cfgJson);
        proc.execute();
        List<ODocument> res = getResult();
        Assert.assertFalse(res.isEmpty());
        ODocument doc = res.get(0);
        Assert.assertEquals(new Integer(100), ((Integer) (doc.field("number"))));
    }

    @Test
    public void testLong() {
        String cfgJson = "{source: { content: { value: \'number\n3000000000\'} }, extractor : { csv: {} },  loader : { test: {} } }";
        configure(cfgJson);
        proc.execute();
        List<ODocument> res = getResult();
        Assert.assertFalse(res.isEmpty());
        ODocument doc = res.get(0);
        Assert.assertEquals(new Long(3000000000L), ((Long) (doc.field("number"))));
    }

    @Test
    public void testLongWithingQuotes() {
        String cfgJson = "{source: { content: { value: \'number\n\"3000000000\"\'} }, extractor : { csv : {} }, loader : { test: {} } }";
        configure(cfgJson);
        proc.execute();
        List<ODocument> res = getResult();
        Assert.assertFalse(res.isEmpty());
        ODocument doc = res.get(0);
        Assert.assertEquals(new Long(3000000000L), ((Long) (doc.field("number"))));
    }

    @Test
    public void testIsFiniteFloat() {
        OETLCSVExtractor OETLCSVExtractor = new OETLCSVExtractor();
        Assert.assertFalse(OETLCSVExtractor.isFinite(Float.NaN));
        Assert.assertFalse(OETLCSVExtractor.isFinite(Float.POSITIVE_INFINITY));
        Assert.assertFalse(OETLCSVExtractor.isFinite(Float.NEGATIVE_INFINITY));
        Assert.assertTrue(OETLCSVExtractor.isFinite(0.0F));
    }

    @Test
    public void testNullCell() {
        String cfgJson = "{source: { content: { value: \'id,postId,text\n1,,Hello\'} }, extractor : { csv : {} }, loader : { test: {} } }";
        configure(cfgJson);
        proc.execute();
        List<ODocument> res = getResult();
        ODocument doc = res.get(0);
        Assert.assertEquals(new Integer(1), ((Integer) (doc.field("id"))));
        Assert.assertNull(doc.field("postId"));
        Assert.assertEquals("Hello", ((String) (doc.field("text"))));
    }

    @Test
    public void testNullValueInCell() {
        String cfgJson = "{source: { content: { value: \'id,postId,text\n1,NULL,Hello\'} }, extractor : { csv : {} },  loader : { test: {} } }";
        configure(cfgJson);
        proc.execute();
        List<ODocument> res = getResult();
        Assert.assertFalse(res.isEmpty());
        ODocument doc = res.get(0);
        Assert.assertEquals(new Integer(1), ((Integer) (doc.field("id"))));
        Assert.assertNull(doc.field("postId"));
        Assert.assertEquals("Hello", ((String) (doc.field("text"))));
    }

    @Test
    public void testCustomNullValueInCell() {
        String cfgJson = "{source: { content: { value: \'id,postId,text\n1,?,Hello\'} }, extractor : { csv : {nullValue: \"?\"} },  loader : { test: {} } }";
        configure(cfgJson);
        proc.execute();
        List<ODocument> res = getResult();
        Assert.assertFalse(res.isEmpty());
        ODocument doc = res.get(0);
        Assert.assertEquals(new Integer(1), ((Integer) (doc.field("id"))));
        Assert.assertNull(doc.field("postId"));
        Assert.assertEquals("Hello", ((String) (doc.field("text"))));
    }

    @Test
    public void testNullValueInCellEmptyString() {
        String cfgJson = "{source: { content: { value: \'id,title,text\n1,\"\",Hello\'} }, extractor : { csv: {} },  loader : { test: {} } }";
        configure(cfgJson);
        proc.execute();
        List<ODocument> res = getResult();
        Assert.assertFalse(res.isEmpty());
        ODocument doc = res.get(0);
        Assert.assertEquals(new Integer(1), ((Integer) (doc.field("id"))));
        Assert.assertThat(doc.<String>field("title")).isNull();
        // assertEquals("", (String) doc.field("title"));
        Assert.assertEquals("Hello", ((String) (doc.field("text"))));
    }

    @Test
    public void testQuotedEmptyString() {
        String cfgJson = "{source: { content: { value: \'id,title,text\n1,\"\",Hello\'} }, extractor : { csv : {} }, loader : { test: {} } }";
        configure(cfgJson);
        proc.execute();
        List<ODocument> res = getResult();
        Assert.assertFalse(res.isEmpty());
        ODocument doc = res.get(0);
        Assert.assertEquals(new Integer(1), ((Integer) (doc.field("id"))));
        Assert.assertThat(doc.<String>field("title")).isNull();
        Assert.assertEquals("Hello", ((String) (doc.field("text"))));
    }

    @Test
    public void testCRLFDelimiter() {
        String cfgJson = "{source: { content: { value: \'id,text,num\r\n1,my test text,1\'} }, extractor : { csv : {} }, loader : { test: {} } }";
        configure(cfgJson);
        proc.execute();
        List<ODocument> res = getResult();
        Assert.assertFalse(res.isEmpty());
        ODocument doc = res.get(0);
        Assert.assertThat(doc.<Integer>field("id")).isEqualTo(1);
        Assert.assertThat(doc.<String>field("text")).isEqualTo("my test text");
        Assert.assertThat(doc.<Integer>field("num")).isEqualTo(1);
    }

    @Test
    public void testEndingLineBreak() {
        String cfgJson = "{source: { content: { value: \'id,text,num\r\n1,my test text,1\r\n\'} }, extractor : { csv : {} }, loader : { test: {} } }";
        configure(cfgJson);
        proc.execute();
        List<ODocument> res = getResult();
        Assert.assertFalse(res.isEmpty());
        ODocument doc = res.get(0);
        Assert.assertEquals(new Integer(1), ((Integer) (doc.field("id"))));
        Assert.assertEquals("my test text", ((String) (doc.field("text"))));
        Assert.assertEquals(new Integer(1), ((Integer) (doc.field("num"))));
    }

    @Test
    public void testEndingSpaceInFieldName() {
        String cfgJson = "{source: { content: { value: \'id ,text ,num \r\n1,my test text,1\r\n\'} }, extractor : { csv : {} }, loader : { test: {} } }";
        configure(cfgJson);
        proc.execute();
        List<ODocument> res = getResult();
        Assert.assertFalse(res.isEmpty());
        ODocument doc = res.get(0);
        Assert.assertThat(doc.<Integer>field("id ")).isEqualTo(1);
        Assert.assertThat(doc.<String>field("text")).isNull();
        Assert.assertThat(doc.<String>field("text ")).isEqualTo("my test text");
        Assert.assertThat(doc.<Integer>field("num ")).isEqualTo(1);
    }

    @Test
    public void testCRLFIWithinQuotes() {
        String cfgJson = "{source: { content: { value: \'id ,text ,num \r\n1,\"my test\r\n text\",1\r\n\'} }, extractor : { csv : {} }, loader : { test: {} } }";
        configure(cfgJson);
        proc.execute();
        List<ODocument> res = getResult();
        Assert.assertFalse(res.isEmpty());
        ODocument doc = res.get(0);
        Assert.assertThat(((Integer) (doc.field("id ")))).isEqualTo(1);
        Assert.assertThat(((String) (doc.field("text ")))).isEqualTo("my test\r\n text");
        Assert.assertThat(((Integer) (doc.field("num ")))).isEqualTo(1);
    }

    @Test
    public void testEscapingDoubleQuotes() {
        String cfgJson = "{source: { content: { value: \'id ,text ,num \r\n1,my test \"\" text,1\r\n\'} }, extractor : { csv : {} }, loader : { test: {} } }";
        configure(cfgJson);
        proc.execute();
        List<ODocument> res = getResult();
        Assert.assertFalse(res.isEmpty());
        ODocument doc = res.get(0);
        Assert.assertThat(doc.<Integer>field("id ")).isEqualTo(1);
        Assert.assertThat(doc.<String>field("text ")).isEqualTo("my test \"\" text");
        Assert.assertThat(doc.<Integer>field("num ")).isEqualTo(1);
    }

    @Test
    public void testNegativeInteger() {
        String cfgJson = "{source: { content: { value: \'id\r\n-1\'} }, extractor : { csv : {} }, loader : { test: {} } }";
        configure(cfgJson);
        proc.execute();
        List<ODocument> res = getResult();
        Assert.assertFalse(res.isEmpty());
        ODocument doc = res.get(0);
        Assert.assertThat(doc.<Integer>field("id")).isEqualTo((-1));
    }

    @Test
    public void testNegativeFloat() {
        String cfgJson = "{source: { content: { value: \'id\r\n-1.0\'} }, extractor : { csv : {} }, loader : { test: {} } }";
        configure(cfgJson);
        proc.execute();
        List<ODocument> res = getResult();
        Assert.assertFalse(res.isEmpty());
        ODocument doc = res.get(0);
        Assert.assertThat(doc.<Float>field("id")).isEqualTo((-1.0F));
    }

    @Test
    public void testLinkType() {
        String cfgJson = "{source: { content: { value: \'num,id\n3,#1:1\'} }, extractor : { csv : {\'columns\':[\'num:INTEGER\',\'id:LINK\']} }, loader : { test: {} } }";
        configure(cfgJson);
        proc.execute();
        List<ODocument> res = getResult();
        Assert.assertThat(res).hasSize(1);
        ODocument doc = res.get(0);
        Assert.assertThat(doc.<ORID>field("id")).isEqualTo(new ORecordId("#1:1"));
    }

    @Test
    public void testBooleanType() {
        String cfgJson = "{source: { content: { value: \'fake\ntrue\'} }, extractor : { csv : {} }, loader : { test: {} } }";
        configure(cfgJson);
        proc.execute();
        List<ODocument> res = getResult();
        Assert.assertThat(res).hasSize(1);
        ODocument doc = res.get(0);
        Assert.assertThat(doc.<Boolean>field("fake")).isTrue();
    }

    @Test
    public void testColumsDefinitions() {
        String cfgJson = "{source: { content: { value: \'name,date,datetime\nfrank,2008-04-30,2015-03-30 11:00\'} }, extractor : { csv : { \'columns\':[\'name:string\',\'date:date\',\'datetime:datetime\']} }, loader : { test: {} } }";
        configure(cfgJson);
        proc.execute();
        List<ODocument> res = getResult();
        Assert.assertThat(res).hasSize(1);
        ODocument doc = res.get(0);
        Assert.assertThat(doc.<Date>field("date")).isEqualTo("2008-04-30");
        // DATETIME: java.util.Date from DB,
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime time = LocalDateTime.parse("2015-03-30 11:00", formatter);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Assert.assertThat(df.format(doc.<Date>field("datetime"))).isEqualTo("2015-03-30 11:00");
    }

    @Test
    public void testCsvParsingFormat() {
        String cfgJson = "{source: { content: { value: \'name,date,datetime\nfrank,2008-04-30,2015-03-30 11:00\'} }, extractor : { csv : { \"predefinedFormat\": \"Default\",\'columns\':[\'name:string\',\'date:date\',\'datetime:datetime\']} }, loader : { test: {} } }";
        configure(cfgJson);
        proc.execute();
        List<ODocument> res = getResult();
        Assert.assertThat(res).hasSize(1);
        ODocument doc = res.get(0);
        Assert.assertThat(doc.<Date>field("date")).isEqualTo("2008-04-30");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Assert.assertThat(df.format(doc.<Date>field("datetime"))).isEqualTo("2015-03-30 11:00");
    }

    @Test
    public void testMissingColumns() {
        String cfgJson = "{source: { content: { value: \'name,value,,\nfrank,myvalue,,\'} }, extractor : { csv : { \"ignoreMissingColumns\": true } }, loader : { test: {} } }";
        configure(cfgJson);
        proc.execute();
        List<ODocument> res = getResult();
        Assert.assertThat(res).hasSize(1);
        ODocument doc = res.get(0);
        Assert.assertThat(doc.<String>field("name")).isEqualTo("frank");
        Assert.assertThat(doc.<String>field("value")).isEqualTo("myvalue");
    }

    @Test
    public void testExcelFormat() {
        String cfgJson = "{source: { content: { value: \'name,value,,\nfrank,myvalue,,\'} }, extractor : { csv : { \"predefinedFormat\": \"Excel\" } }, loader : { test: {} } }";
        configure(cfgJson);
        proc.execute();
        List<ODocument> res = getResult();
        Assert.assertThat(res).hasSize(1);
        ODocument doc = res.get(0);
        Assert.assertThat(doc.<String>field("name")).isEqualTo("frank");
        Assert.assertThat(doc.<String>field("value")).isEqualTo("myvalue");
    }
}

