package com.zendesk.maxwell.schema.columndef;


import com.zendesk.maxwell.TestWithNameLogging;
import com.zendesk.maxwell.producer.MaxwellOutputConfig;
import com.zendesk.maxwell.row.RawJSONString;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class ColumnDefTest extends TestWithNameLogging {
    @Test
    public void testTinyInt() {
        ColumnDef d = build("tinyint", true);
        Assert.assertThat(d, CoreMatchers.instanceOf(IntColumnDef.class));
        Assert.assertThat(d.toSQL(Integer.valueOf(5)), CoreMatchers.is("5"));
        Assert.assertThat(d.toSQL(Integer.valueOf((-5))), CoreMatchers.is("-5"));
        d = build("tinyint", false);
        Assert.assertThat(d.toSQL(Integer.valueOf(10)), CoreMatchers.is("10"));
        Assert.assertThat(d.toSQL(Integer.valueOf((-10))), CoreMatchers.is("246"));
    }

    @Test
    public void testShortInt() {
        ColumnDef d = build("smallint", true);
        Assert.assertThat(d, CoreMatchers.instanceOf(IntColumnDef.class));
        Assert.assertThat(d.toSQL(Integer.valueOf(5)), CoreMatchers.is("5"));
        Assert.assertThat(d.toSQL(Integer.valueOf((-5))), CoreMatchers.is("-5"));
        d = build("smallint", false);
        Assert.assertThat(d.toSQL(Integer.valueOf((-10))), CoreMatchers.is("65526"));
    }

    @Test
    public void testMediumInt() {
        ColumnDef d = build("mediumint", true);
        Assert.assertThat(d, CoreMatchers.instanceOf(IntColumnDef.class));
        Assert.assertThat(d.toSQL(Integer.valueOf(5)), CoreMatchers.is("5"));
        Assert.assertThat(d.toSQL(Integer.valueOf((-5))), CoreMatchers.is("-5"));
        d = build("mediumint", false);
        Assert.assertThat(d.toSQL(Integer.valueOf((-10))), CoreMatchers.is("16777206"));
    }

    @Test
    public void testInt() {
        ColumnDef d = build("int", true);
        Assert.assertThat(d, CoreMatchers.instanceOf(IntColumnDef.class));
        Assert.assertThat(d.toSQL(Integer.valueOf(5)), CoreMatchers.is("5"));
        Assert.assertThat(d.toSQL(Integer.valueOf((-5))), CoreMatchers.is("-5"));
        d = build("int", false);
        Assert.assertThat(d.toSQL(Integer.valueOf((-10))), CoreMatchers.is("4294967286"));
    }

    @Test
    public void testBigInt() {
        ColumnDef d = build("bigint", true);
        Assert.assertThat(d, CoreMatchers.instanceOf(BigIntColumnDef.class));
        Assert.assertThat(d.toSQL(Long.valueOf(5)), CoreMatchers.is("5"));
        Assert.assertThat(d.toSQL(Long.valueOf((-5))), CoreMatchers.is("-5"));
        d = build("bigint", false);
        Assert.assertThat(d.toSQL(Long.valueOf((-10))), CoreMatchers.is("18446744073709551606"));
    }

    @Test
    public void testUTF8String() {
        ColumnDef d = ColumnDef.build("bar", "utf8", "varchar", ((short) (1)), false, null, null);
        Assert.assertThat(d, CoreMatchers.instanceOf(StringColumnDef.class));
        byte[] input = "He????".getBytes();
        Assert.assertThat(d.toSQL(input), CoreMatchers.is("'He????'"));
    }

    @Test
    public void TestUTF8MB4String() {
        String utf8_4 = "?";
        ColumnDef d = ColumnDef.build("bar", "utf8mb4", "varchar", ((short) (1)), false, null, null);
        byte[] input = utf8_4.getBytes();
        Assert.assertThat(d.toSQL(input), CoreMatchers.is("'?'"));
    }

    @Test
    public void TestAsciiString() {
        byte[] input = new byte[]{ ((byte) (126)), ((byte) (126)), ((byte) (126)), ((byte) (126)) };
        ColumnDef d = ColumnDef.build("bar", "ascii", "varchar", ((short) (1)), false, null, null);
        Assert.assertThat(((String) (d.asJSON(input, null))), CoreMatchers.is("~~~~"));
    }

    @Test
    public void TestLatin1String() {
        byte[] input = new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (128)) };
        ColumnDef d = ColumnDef.build("bar", "latin1", "varchar", ((short) (1)), false, null, null);
        Assert.assertThat(((String) (d.asJSON(input, null))), CoreMatchers.is("????"));
    }

    @Test
    public void TestStringAsJSON() {
        byte[] input = new byte[]{ ((byte) (169)), ((byte) (169)), ((byte) (169)), ((byte) (169)) };
        ColumnDef d = ColumnDef.build("bar", "latin1", "varchar", ((short) (1)), false, null, null);
        Assert.assertThat(((String) (d.asJSON(input, null))), CoreMatchers.is("????"));
    }

    @Test
    public void TestJSON() {
        byte[] input = new byte[]{ ((byte) (0)), ((byte) (1)), ((byte) (0)), ((byte) (13)), ((byte) (0)), ((byte) (11)), ((byte) (0)), ((byte) (2)), ((byte) (0)), ((byte) (5)), ((byte) (3)), ((byte) (0)), ((byte) (105)), ((byte) (100)) };
        ColumnDef d = ColumnDef.build("bar", "ascii", "json", ((short) (1)), false, null, null);
        RawJSONString result = ((RawJSONString) (d.asJSON(input, null)));
        Assert.assertThat(result.json, CoreMatchers.is("{\"id\":3}"));
    }

    @Test
    public void TestEmptyJSON() {
        byte[] input = new byte[0];
        ColumnDef d = ColumnDef.build("bar", "ascii", "json", ((short) (1)), false, null, null);
        RawJSONString result = ((RawJSONString) (d.asJSON(input, null)));
        Assert.assertThat(result.json, CoreMatchers.is("null"));
    }

    @Test
    public void TestFloat() {
        ColumnDef d = build("float", true);
        Assert.assertThat(d, CoreMatchers.instanceOf(FloatColumnDef.class));
        Assert.assertThat(d.toSQL(Float.valueOf(1.2F)), CoreMatchers.is("1.2"));
    }

    @Test
    public void TestDouble() {
        ColumnDef d = build("double", true);
        Assert.assertThat(d, CoreMatchers.instanceOf(FloatColumnDef.class));
        String maxDouble = Double.valueOf(Double.MAX_VALUE).toString();
        Assert.assertThat(d.toSQL(Double.valueOf(Double.MAX_VALUE)), CoreMatchers.is(maxDouble));
    }

    @Test
    public void TestTime() throws ParseException {
        ColumnDef d = build("time", true);
        Assert.assertThat(d, CoreMatchers.instanceOf(TimeColumnDef.class));
        Timestamp t = new Timestamp((307653559000L - (TimeZone.getDefault().getOffset(307653559000L))));
        Assert.assertThat(d.toSQL(t), CoreMatchers.is("'19:19:19'"));
    }

    @Test
    public void TestTimeWithMillisecTimestamp() throws ParseException {
        ColumnDef d = build("time", true, 3L);
        Assert.assertThat(d, CoreMatchers.instanceOf(TimeColumnDef.class));
        Timestamp t = new Timestamp((307653559000L - (TimeZone.getDefault().getOffset(307653559000L))));
        t.setNanos(0);
        Assert.assertThat(d.toSQL(t), CoreMatchers.is("'19:19:19.000'"));
        t.setNanos(123000);
        Assert.assertThat(d.toSQL(t), CoreMatchers.is("'19:19:19.000'"));
        t.setNanos(123456789);
        Assert.assertThat(d.toSQL(t), CoreMatchers.is("'19:19:19.123'"));
    }

    @Test
    public void TestTimeWithMicrosecTimestamp() throws ParseException {
        ColumnDef d = build("time", true, 6L);
        Assert.assertThat(d, CoreMatchers.instanceOf(TimeColumnDef.class));
        Timestamp t = new Timestamp((307653559000L - (TimeZone.getDefault().getOffset(307653559000L))));
        t.setNanos(0);
        Assert.assertThat(d.toSQL(t), CoreMatchers.is("'19:19:19.000000'"));
        t.setNanos(123456789);
        Assert.assertThat(d.toSQL(t), CoreMatchers.is("'19:19:19.123456'"));
        t.setNanos(123000);
        Assert.assertThat(d.toSQL(t), CoreMatchers.is("'19:19:19.000123'"));
    }

    @Test
    public void TestDate() {
        ColumnDef d = build("date", true);
        Assert.assertThat(d, CoreMatchers.instanceOf(DateColumnDef.class));
        Date date = new GregorianCalendar(1979, 10, 1).getTime();
        Assert.assertThat(d.toSQL(date), CoreMatchers.is("'1979-11-01'"));
    }

    @Test
    public void TestDateZeroDates() {
        ColumnDef d = build("date", true);
        MaxwellOutputConfig config = new MaxwellOutputConfig();
        config.zeroDatesAsNull = true;
        Assert.assertEquals(null, d.asJSON(Long.MIN_VALUE, config));
    }

    @Test
    public void TestDateTime() throws ParseException {
        ColumnDef d = build("datetime", true);
        Assert.assertThat(d, CoreMatchers.instanceOf(DateTimeColumnDef.class));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse("1979-10-01 19:19:19");
        Assert.assertThat(d.toSQL(date), CoreMatchers.is("'1979-10-01 19:19:19'"));
    }

    @Test
    public void TestDateTimeZeroDates() {
        ColumnDef d = build("datetime", true);
        MaxwellOutputConfig config = new MaxwellOutputConfig();
        config.zeroDatesAsNull = true;
        Assert.assertEquals(null, d.asJSON(Long.MIN_VALUE, config));
    }

    @Test
    public void TestDateTimeWithTimestamp() throws ParseException {
        ColumnDef d = build("datetime", true);
        Assert.assertThat(d, CoreMatchers.instanceOf(DateTimeColumnDef.class));
        Timestamp t = Timestamp.valueOf("1979-10-01 19:19:19");
        Assert.assertThat(d.toSQL(t), CoreMatchers.is("'1979-10-01 19:19:19'"));
    }

    @Test
    public void TestDateTimeWithMillisecprecision() throws ParseException {
        ColumnDef d = build("datetime", true, 3L);
        Assert.assertThat(d, CoreMatchers.instanceOf(DateTimeColumnDef.class));
        Timestamp t = Timestamp.valueOf("1979-10-01 19:19:19.123");
        Assert.assertThat(d.toSQL(t), CoreMatchers.is("'1979-10-01 19:19:19.123'"));
        t = Timestamp.valueOf("1979-10-01 19:19:19");
        Assert.assertThat(d.toSQL(t), CoreMatchers.is("'1979-10-01 19:19:19.000'"));
        t = Timestamp.valueOf("1979-10-01 19:19:19.001");
        Assert.assertThat(d.toSQL(t), CoreMatchers.is("'1979-10-01 19:19:19.001'"));
    }

    @Test
    public void TestDateTimeWithMicroPrecision() throws ParseException {
        ColumnDef d = build("datetime", true, 6L);
        Assert.assertThat(d, CoreMatchers.instanceOf(DateTimeColumnDef.class));
        Timestamp t = Timestamp.valueOf("1979-10-01 19:19:19.001000");
        Assert.assertEquals(1000000, t.getNanos());
        Assert.assertThat(d.toSQL(t), CoreMatchers.is("'1979-10-01 19:19:19.001000'"));
        t = Timestamp.valueOf("1979-10-01 19:19:19.000001");
        Assert.assertEquals(1000, t.getNanos());
        Assert.assertThat(d.toSQL(t), CoreMatchers.is("'1979-10-01 19:19:19.000001'"));
        t = Timestamp.valueOf("1979-10-01 19:19:19.345678");
        Assert.assertThat(d.toSQL(t), CoreMatchers.is("'1979-10-01 19:19:19.345678'"));
        t = Timestamp.valueOf("1979-10-01 19:19:19.100000");
        Assert.assertThat(d.toSQL(t), CoreMatchers.is("'1979-10-01 19:19:19.100000'"));
    }

    @Test
    public void TestTimestamp() throws ParseException {
        ColumnDef d = build("timestamp", true);
        Assert.assertThat(d, CoreMatchers.instanceOf(DateTimeColumnDef.class));
        Timestamp t = Timestamp.valueOf("1979-10-01 19:19:19");
        Assert.assertThat(d.toSQL(t), CoreMatchers.is("'1979-10-01 19:19:19'"));
    }

    @Test
    public void TestTimestampWithMilliSecPrecision() throws ParseException {
        ColumnDef d = build("timestamp", true, 3L);
        Assert.assertThat(d, CoreMatchers.instanceOf(DateTimeColumnDef.class));
        Timestamp t = Timestamp.valueOf("1979-10-01 19:19:19.123456000");
        Assert.assertThat(d.toSQL(t), CoreMatchers.is("'1979-10-01 19:19:19.123'"));
        t = Timestamp.valueOf("1979-10-01 19:19:19");
        Assert.assertThat(d.toSQL(t), CoreMatchers.is("'1979-10-01 19:19:19.000'"));
        t = Timestamp.valueOf("1979-10-01 19:19:19.000");
        Assert.assertThat(d.toSQL(t), CoreMatchers.is("'1979-10-01 19:19:19.000'"));
        t = Timestamp.valueOf("1979-10-01 19:19:19.001");
        Assert.assertThat(d.toSQL(t), CoreMatchers.is("'1979-10-01 19:19:19.001'"));
    }

    @Test
    public void TestTimestampWithMicroSecPrecision() throws ParseException {
        ColumnDef d = build("timestamp", true, 6L);
        Assert.assertThat(d, CoreMatchers.instanceOf(DateTimeColumnDef.class));
        Timestamp t = Timestamp.valueOf("1979-10-01 19:19:19.123456000");
        Assert.assertThat(d.toSQL(t), CoreMatchers.is("'1979-10-01 19:19:19.123456'"));
        t = Timestamp.valueOf("1979-10-01 19:19:19.000123");
        Assert.assertThat(d.toSQL(t), CoreMatchers.is("'1979-10-01 19:19:19.000123'"));
        t = Timestamp.valueOf("1979-10-01 19:19:19.000001");
        Assert.assertThat(d.toSQL(t), CoreMatchers.is("'1979-10-01 19:19:19.000001'"));
    }

    @Test
    public void TestBit() {
        ColumnDef d = build("bit", true);
        Assert.assertThat(d, CoreMatchers.instanceOf(BitColumnDef.class));
        byte[] b = new byte[]{ 1 };
        Assert.assertThat(d.toSQL(b), CoreMatchers.is("1"));
        b = new byte[]{ 0 };
        Assert.assertThat(d.toSQL(b), CoreMatchers.is("0"));
        Boolean bO = Boolean.TRUE;
        Assert.assertThat(d.toSQL(bO), CoreMatchers.is("1"));
        bO = Boolean.FALSE;
        Assert.assertThat(d.toSQL(bO), CoreMatchers.is("0"));
    }
}

