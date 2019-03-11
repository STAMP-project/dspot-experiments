package us.codecraft.webmagic.model;


import ExtractBy.Type;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.ExtractByUrl;
import us.codecraft.webmagic.model.annotation.Formatter;
import us.codecraft.webmagic.model.formatter.DateFormatter;


/**
 *
 *
 * @author code4crafter@gmail.com
Date: 2017/6/3
Time: ??9:06
 */
public class PageModelExtractorTest {
    private PageMocker pageMocker = new PageMocker();

    public static class ModelDateStr {
        @ExtractBy(value = "//div[@class='date']/text()", notNull = true)
        private String dateStr;
    }

    public static class ModelDate {
        @Formatter(value = "yyyyMMdd", formatter = DateFormatter.class)
        @ExtractBy(value = "//div[@class='date']/text()", notNull = true)
        private Date date;
    }

    public static class ModelInt {
        @ExtractBy(value = "//div[@class='number']/text()", notNull = true)
        private int number;
    }

    public static class ModelStringList {
        @ExtractBy("//li[@class='list']/a/@href")
        private List<String> links;
    }

    public static class ModelIntList {
        @Formatter(subClazz = Integer.class)
        @ExtractBy("//li[@class='numbers']/text()")
        private List<Integer> numbers;
    }

    public static class ModelDateList {
        @Formatter(subClazz = Date.class, value = "yyyyMMdd")
        @ExtractBy("//li[@class='dates']/text()")
        private List<Date> dates;
    }

    public static class ModelCustomList {
        @Formatter(subClazz = Date.class, value = "yyyyMMdd", formatter = DateFormatter.class)
        @ExtractBy("//li[@class='dates']/text()")
        private List<Date> dates;
    }

    public static class ModelJsonStr {
        @ExtractBy(type = Type.JsonPath, value = "$.name")
        private String name;
    }

    public static class ModelUrl {
        @ExtractByUrl("https://api\\.github\\.com/repos/\\w+/(\\w+)")
        private String name;
    }

    @Test
    public void testXpath() throws Exception {
        PageModelExtractorTest.ModelDateStr modelDate = ((PageModelExtractorTest.ModelDateStr) (PageModelExtractor.create(PageModelExtractorTest.ModelDateStr.class).process(pageMocker.getMockPage())));
        assertThat(modelDate.dateStr).isEqualTo("20170603");
    }

    @Test
    public void testExtractDate() throws Exception {
        PageModelExtractorTest.ModelDate modelDate = ((PageModelExtractorTest.ModelDate) (PageModelExtractor.create(PageModelExtractorTest.ModelDate.class).process(pageMocker.getMockPage())));
        assertThat(DateFormatUtils.format(modelDate.date, "yyyyMMdd")).isEqualTo("20170603");
    }

    @Test
    public void testExtractInt() throws Exception {
        PageModelExtractorTest.ModelInt modelDate = ((PageModelExtractorTest.ModelInt) (PageModelExtractor.create(PageModelExtractorTest.ModelInt.class).process(pageMocker.getMockPage())));
        assertThat(modelDate.number).isEqualTo(12);
    }

    @Test
    public void testExtractList() throws Exception {
        PageModelExtractorTest.ModelStringList modelDate = ((PageModelExtractorTest.ModelStringList) (PageModelExtractor.create(PageModelExtractorTest.ModelStringList.class).process(pageMocker.getMockPage())));
        assertThat(modelDate.links).containsExactly("http://webmagic.io/list/1", "http://webmagic.io/list/2", "http://webmagic.io/list/3", "http://webmagic.io/list/4");
    }

    @Test
    public void testExtractIntList() throws Exception {
        PageModelExtractorTest.ModelIntList modelDate = ((PageModelExtractorTest.ModelIntList) (PageModelExtractor.create(PageModelExtractorTest.ModelIntList.class).process(pageMocker.getMockPage())));
        assertThat(modelDate.numbers).containsExactly(1, 2, 3, 4);
    }

    @Test
    public void testExtractDateList() throws Exception {
        PageModelExtractorTest.ModelDateList modelDate = ((PageModelExtractorTest.ModelDateList) (PageModelExtractor.create(PageModelExtractorTest.ModelDateList.class).process(pageMocker.getMockPage())));
        assertThat(modelDate.dates).containsExactly(DateUtils.parseDate("20170601", "yyyyMMdd"), DateUtils.parseDate("20170602", "yyyyMMdd"), DateUtils.parseDate("20170603", "yyyyMMdd"), DateUtils.parseDate("20170604", "yyyyMMdd"));
    }

    @Test
    public void testExtractCustomList() throws Exception {
        PageModelExtractorTest.ModelCustomList modelDate = ((PageModelExtractorTest.ModelCustomList) (PageModelExtractor.create(PageModelExtractorTest.ModelCustomList.class).process(pageMocker.getMockPage())));
        assertThat(modelDate.dates).containsExactly(DateUtils.parseDate("20170601", "yyyyMMdd"), DateUtils.parseDate("20170602", "yyyyMMdd"), DateUtils.parseDate("20170603", "yyyyMMdd"), DateUtils.parseDate("20170604", "yyyyMMdd"));
    }

    @Test
    public void testExtractJson() throws Exception {
        PageModelExtractorTest.ModelJsonStr modelDate = ((PageModelExtractorTest.ModelJsonStr) (PageModelExtractor.create(PageModelExtractorTest.ModelJsonStr.class).process(pageMocker.getMockJsonPage())));
        assertThat(modelDate.name).isEqualTo("webmagic");
    }

    @Test
    public void testExtractByUrl() throws Exception {
        PageModelExtractorTest.ModelUrl modelDate = ((PageModelExtractorTest.ModelUrl) (PageModelExtractor.create(PageModelExtractorTest.ModelUrl.class).process(pageMocker.getMockJsonPage())));
        assertThat(modelDate.name).isEqualTo("webmagic");
    }
}

