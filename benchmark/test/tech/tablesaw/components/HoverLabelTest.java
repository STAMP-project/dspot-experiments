package tech.tablesaw.components;


import Font.Family.ARIAL;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import tech.tablesaw.plotly.components.Font;
import tech.tablesaw.plotly.components.HoverLabel;


@Disabled
public class HoverLabelTest {
    @Test
    public void asJavascript() {
        HoverLabel x = HoverLabel.builder().nameLength(10).bgColor("blue").borderColor("green").font(Font.builder().family(ARIAL).size(8).color("red").build()).build();
        System.out.println(x);
    }
}

