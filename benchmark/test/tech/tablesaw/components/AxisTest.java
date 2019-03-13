package tech.tablesaw.components;


import Axis.Type.DEFAULT;
import Font.Family.ARIAL;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import tech.tablesaw.plotly.components.Axis;
import tech.tablesaw.plotly.components.Font;


@Disabled
public class AxisTest {
    @Test
    public void asJavascript() {
        Axis x = Axis.builder().title("x Axis 1").visible(true).type(DEFAULT).titleFont(Font.builder().family(ARIAL).size(8).color("red").build()).build();
        System.out.println(x);
    }
}

