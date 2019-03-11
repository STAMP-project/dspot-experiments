package cc.blynk.server.core.model.widgets.ui.reporting;


import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.widgets.outputs.graph.GraphGranularityType;
import cc.blynk.server.core.model.widgets.ui.reporting.source.ReportDataStream;
import cc.blynk.server.core.model.widgets.ui.reporting.source.ReportSource;
import cc.blynk.server.core.model.widgets.ui.reporting.type.DayOfMonth;
import cc.blynk.server.core.model.widgets.ui.reporting.type.OneTimeReport;
import cc.blynk.server.core.model.widgets.ui.reporting.type.ReportDurationType;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import org.junit.Assert;
import org.junit.Test;


public class ReportingModelTest {
    private ObjectWriter ow = JsonParser.init().writerWithDefaultPrettyPrinter().forType(ReportingWidget.class);

    @Test
    public void printModel() throws Exception {
        ReportDataStream reportDataStream = new ReportDataStream(((short) (1)), PinType.VIRTUAL, "Temperature", true);
        ReportSource reportSource = new cc.blynk.server.core.model.widgets.ui.reporting.source.TileTemplateReportSource(new ReportDataStream[]{ reportDataStream }, 1, null);
        ReportSource reportSource2 = new cc.blynk.server.core.model.widgets.ui.reporting.source.TileTemplateReportSource(new ReportDataStream[]{ reportDataStream }, 1, new int[]{ 0, 1 });
        Report report = new Report(1, "My One Time Report", new ReportSource[]{ reportSource2 }, new OneTimeReport(86400), "test@gmail.com", GraphGranularityType.MINUTE, true, ReportOutput.CSV_FILE_PER_DEVICE_PER_PIN, null, ZoneId.of("UTC"), 0, 0, null);
        Report report2 = new Report(2, "My Daily Report", new ReportSource[]{ reportSource2 }, new cc.blynk.server.core.model.widgets.ui.reporting.type.DailyReport(60, ReportDurationType.CUSTOM, 100, 200), "test@gmail.com", GraphGranularityType.MINUTE, true, ReportOutput.CSV_FILE_PER_DEVICE_PER_PIN, null, ZoneId.of("UTC"), 0, 0, null);
        Report report3 = new Report(3, "My Daily Report", new ReportSource[]{ reportSource2 }, new cc.blynk.server.core.model.widgets.ui.reporting.type.WeeklyReport(60, ReportDurationType.CUSTOM, 100, 200, 1), "test@gmail.com", GraphGranularityType.MINUTE, true, ReportOutput.CSV_FILE_PER_DEVICE_PER_PIN, null, ZoneId.of("UTC"), 0, 0, null);
        Report report4 = new Report(4, "My Daily Report", new ReportSource[]{ reportSource2 }, new cc.blynk.server.core.model.widgets.ui.reporting.type.MonthlyReport(60, ReportDurationType.CUSTOM, 100, 200, DayOfMonth.FIRST), "test@gmail.com", GraphGranularityType.MINUTE, true, ReportOutput.CSV_FILE_PER_DEVICE_PER_PIN, null, ZoneId.of("UTC"), 0, 0, null);
        ReportingWidget reportingWidget = new ReportingWidget();
        reportingWidget.reportSources = new ReportSource[]{ reportSource };
        reportingWidget.reports = new Report[]{ report, report2, report3, report4 };
        System.out.println(ow.writeValueAsString(reportingWidget));
    }

    @Test
    public void testEmailDynamicPart() {
        ReportDataStream reportDataStream = new ReportDataStream(((short) (1)), PinType.VIRTUAL, "Temperature", true);
        ReportSource reportSource2 = new cc.blynk.server.core.model.widgets.ui.reporting.source.TileTemplateReportSource(new ReportDataStream[]{ reportDataStream }, 1, new int[]{ 0, 1 });
        Report report = new Report(1, "My One Time Report", new ReportSource[]{ reportSource2 }, new OneTimeReport(86400), "test@gmail.com", GraphGranularityType.MINUTE, true, ReportOutput.CSV_FILE_PER_DEVICE_PER_PIN, null, ZoneId.of("UTC"), 0, 0, null);
        LocalDateTime localDateTime = LocalDateTime.of(2018, 2, 20, 10, 10);
        long millis = (localDateTime.toEpochSecond(ZoneOffset.UTC)) * 1000;
        Report report2 = new Report(2, "My Daily Report", new ReportSource[]{ reportSource2 }, new cc.blynk.server.core.model.widgets.ui.reporting.type.DailyReport(millis, ReportDurationType.CUSTOM, millis, millis), "test@gmail.com", GraphGranularityType.MINUTE, true, ReportOutput.CSV_FILE_PER_DEVICE_PER_PIN, null, ZoneId.of("UTC"), 0, 0, null);
        LocalDateTime start = LocalDateTime.of(2018, 3, 21, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2019, 3, 21, 0, 0, 0);
        Report report3 = new Report(3, "My Weekly Report", new ReportSource[]{ reportSource2 }, new cc.blynk.server.core.model.widgets.ui.reporting.type.WeeklyReport(millis, ReportDurationType.CUSTOM, ((start.toEpochSecond(ZoneOffset.UTC)) * 1000), ((end.toEpochSecond(ZoneOffset.UTC)) * 1000), 1), "test@gmail.com", GraphGranularityType.MINUTE, true, ReportOutput.CSV_FILE_PER_DEVICE_PER_PIN, null, ZoneId.of("UTC"), 0, 0, null);
        Report report4 = new Report(4, "My Monthly Report", new ReportSource[]{ reportSource2 }, new cc.blynk.server.core.model.widgets.ui.reporting.type.MonthlyReport(millis, ReportDurationType.CUSTOM, ((start.toEpochSecond(ZoneOffset.UTC)) * 1000), ((end.toEpochSecond(ZoneOffset.UTC)) * 1000), DayOfMonth.FIRST), "test@gmail.com", GraphGranularityType.MINUTE, true, ReportOutput.CSV_FILE_PER_DEVICE_PER_PIN, null, ZoneId.of("UTC"), 0, 0, null);
        Report report5 = new Report(4, "My Monthly Report 2", new ReportSource[]{ reportSource2 }, new cc.blynk.server.core.model.widgets.ui.reporting.type.MonthlyReport(millis, ReportDurationType.CUSTOM, ((start.toEpochSecond(ZoneOffset.UTC)) * 1000), ((end.toEpochSecond(ZoneOffset.UTC)) * 1000), DayOfMonth.LAST), "test@gmail.com", GraphGranularityType.MINUTE, true, ReportOutput.CSV_FILE_PER_DEVICE_PER_PIN, null, ZoneId.of("UTC"), 0, 0, null);
        Report report6 = new Report(2, "My Daily Report", new ReportSource[]{ reportSource2 }, new cc.blynk.server.core.model.widgets.ui.reporting.type.DailyReport(millis, ReportDurationType.INFINITE, millis, millis), "test@gmail.com", GraphGranularityType.MINUTE, true, ReportOutput.CSV_FILE_PER_DEVICE_PER_PIN, null, ZoneId.of("UTC"), 0, 0, null);
        Assert.assertEquals("Report name: My One Time Report<br>Period: One time", report.buildDynamicSection());
        Assert.assertEquals((("Report name: My Daily Report<br>Period: Daily, at " + (localDateTime.toLocalTime())) + "<br>Start date: 2018-02-20<br>End date: 2018-02-20<br>"), report2.buildDynamicSection());
        Assert.assertEquals((("Report name: My Weekly Report<br>Period: Weekly, at " + (localDateTime.toLocalTime())) + " every Monday<br>Start date: 2018-03-21<br>End date: 2019-03-21<br>"), report3.buildDynamicSection());
        Assert.assertEquals((("Report name: My Monthly Report<br>Period: Monthly, at " + (localDateTime.toLocalTime())) + " at the first day of every month<br>Start date: 2018-03-21<br>End date: 2019-03-21<br>"), report4.buildDynamicSection());
        Assert.assertEquals((("Report name: My Monthly Report 2<br>Period: Monthly, at " + (localDateTime.toLocalTime())) + " at the last day of every month<br>Start date: 2018-03-21<br>End date: 2019-03-21<br>"), report5.buildDynamicSection());
        Assert.assertEquals((("Report name: My Daily Report<br>Period: Daily, at " + (localDateTime.toLocalTime())) + ""), report6.buildDynamicSection());
    }
}

