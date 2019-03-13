package tech.tablesaw.plotly;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.tablesaw.plotly.components.Page;
import tech.tablesaw.plotly.traces.BarTrace;


public class PageTest {
    private final Object[] x = new Object[]{ "sheep", "cows", "fish", "tree sloths" };

    private final double[] y = new double[]{ 1, 4, 9, 16 };

    @Test
    public void testDefaultPlotlyJsLocation() {
        BarTrace trace = BarTrace.builder(x, y).build();
        Page page = Page.pageBuilder(new tech.tablesaw.plotly.components.Figure(trace), "plot").build();
        String html = page.asJavascript();
        Assertions.assertTrue(((html.indexOf(("\"" + ("https://cdn.plot.ly/plotly-latest.min.js" + "\"")))) > 0));
    }

    @Test
    public void testCustomPlotlyJsLocation() {
        BarTrace trace = BarTrace.builder(x, y).build();
        String location = this.getClass().getResource(((this.getClass().getSimpleName()) + ".class")).toString();
        Page page = Page.pageBuilder(new tech.tablesaw.plotly.components.Figure(trace), "plot").plotlyJsLocation(location).build();
        String html = page.asJavascript();
        Assertions.assertTrue(((html.indexOf((("\"" + location) + "\""))) > 0));
    }
}

