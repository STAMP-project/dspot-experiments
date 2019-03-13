/**
 * Copyright 2018 TWO SIGMA OPEN SOURCE, LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.twosigma.beakerx.groovy.inspect;


import com.twosigma.beakerx.evaluator.BaseEvaluator;
import com.twosigma.beakerx.inspect.InspectResult;
import org.junit.Test;


public class GroovyInspectTest {
    private static final String COLOR_RED = "\u001b[31m";

    private static final String COLOR_RESET = "\u001b[0m";

    private final String METHOD_CSV_PLOT_READER_READ = ((printSignature("CsvPlotReader.read(java.lang.String fileName)")) + "\n") + (printEmptyJavaDoc());

    private final String CLASS_CSV_PLOT_READER = (((printClass("com.twosigma.beakerx.fileloader.CsvPlotReader")) + "\n") + (printEmptyJavaDoc())) + "\n\n";

    private final String METHOD_TABLE_DISPLAY_SETDBLCLICK = ((((((printSignature("TableDisplay.setDoubleClickAction(java.lang.String tagName)")) + "\n") + (printEmptyJavaDoc())) + "\n\n") + (printSignature("TableDisplay.setDoubleClickAction(java.lang.Object listener)"))) + "\n") + (printEmptyJavaDoc());

    private final String CLASS_TABLE_DISPLAY = (((printClass("com.twosigma.beakerx.table.TableDisplay")) + "\n") + (printEmptyJavaDoc())) + "\n\n";

    private static BaseEvaluator groovyEvaluator;

    @Test
    public void evaluateInspectFooWoBracketCaretAtEnd() throws Exception {
        // given
        String code = ((((("import com.twosigma.beakerx.table.*" + (System.lineSeparator())) + "import com.twosigma.beakerx.table.format.TableDisplayStringFormat") + (System.lineSeparator())) + "csv = new CsvPlotReader()") + (System.lineSeparator())) + "csv.re\u0000ad";
        String expected = METHOD_CSV_PLOT_READER_READ;
        // when
        InspectResult result = callInspectOnCaretPos(code);
        // then
        assertThat(result.getFound()).isTrue();
        assertThat(result.getData().getTextplain()).isEqualTo(expected);
    }

    @Test
    public void evaluateInspectOnClassName() throws Exception {
        // given
        String code = ((("import com.twosigma.beakerx.table.*" + (System.lineSeparator())) + "import com.twosigma.beakerx.table.format.TableDisplayStringFormat") + (System.lineSeparator())) + "csv = new CsvPlot\u0000Reader()";
        String expected = CLASS_CSV_PLOT_READER;
        // when
        InspectResult result = callInspectOnCaretPos(code);
        assertThat(result.getFound()).isTrue();
        assertThat(result.getData().getTextplain()).isEqualTo(expected);
    }

    @Test
    public void evaluateInspectFooWBracketCaretInBracket() throws Exception {
        // given
        String code = ((((("import com.twosigma.beakerx.table.*" + (System.lineSeparator())) + "import com.twosigma.beakerx.table.format.TableDisplayStringFormat") + (System.lineSeparator())) + "csv = new CsvPlotReader()") + (System.lineSeparator())) + "csv.read(\u0000)";
        String expected = METHOD_CSV_PLOT_READER_READ;
        // when
        InspectResult result = callInspectOnCaretPos(code);
        // then
        assertThat(result.getFound()).isTrue();
        assertThat(result.getData().getTextplain()).isEqualTo(expected);
    }

    @Test
    public void evaluateInspectFooWoSimpleInitCaretInFooName() throws Exception {
        // given
        String code = ((((("import com.twosigma.beakerx.table.*" + (System.lineSeparator())) + "import com.twosigma.beakerx.table.format.TableDisplayStringFormat") + (System.lineSeparator())) + "table = new TableDisplay(new CsvPlotReader().read('../resources/data/interest-rates.csv'))") + (System.lineSeparator())) + "table.setDoubleCli\u0000ckAction()";
        String expected = METHOD_TABLE_DISPLAY_SETDBLCLICK;
        // when
        InspectResult result = callInspectOnCaretPos(code);
        // then
        assertThat(result.getFound()).isTrue();
        assertThat(result.getData().getTextplain()).isEqualTo(expected);
    }

    @Test
    public void evaluateInspectClassNameWithoutNew() throws Exception {
        // given
        String code = ((("import com.twosigma.beakerx.table.*" + (System.lineSeparator())) + "import com.twosigma.beakerx.table.format.TableDisplayStringFormat") + (System.lineSeparator())) + "table = new TableDisplay(new CsvPlotReader().read(\'ar\u0000gs\'))";
        String expected = METHOD_CSV_PLOT_READER_READ;
        // when
        InspectResult result = callInspectOnCaretPos(code);
        // then
        assertThat(result.getFound()).isTrue();
        assertThat(result.getData().getTextplain()).isEqualTo(expected);
    }

    @Test
    public void evaluateInspectClassNameInsideConstructor() throws Exception {
        // given
        String code = ((((("import com.twosigma.beakerx.table.*" + (System.lineSeparator())) + "import com.twosigma.beakerx.table.format.TableDisplayStringFormat") + (System.lineSeparator())) + "display = new TableDisplay(new CsvPlot\u0000Reader().read(\"../resources/data/interest-rates.csv\"))") + (System.lineSeparator())) + "display.setStringFormatForColumn(\"test\")";
        String expected = CLASS_CSV_PLOT_READER;
        // when
        InspectResult result = callInspectOnCaretPos(code);
        assertThat(result.getFound()).isTrue();
        assertThat(result.getData().getTextplain()).isEqualTo(expected);
    }

    @Test
    public void evaluateInspectOnVariableWithDefKeyword() throws Exception {
        // given
        String code = ("def display = new TableDisplay()" + (System.lineSeparator())) + "dis\u0000play.setDoubleClickAction()";
        String expected = CLASS_TABLE_DISPLAY;
        // when
        InspectResult result = callInspectOnCaretPos(code);
        assertThat(result.getFound()).isTrue();
        assertThat(result.getData().getTextplain()).isEqualTo(expected);
    }

    @Test
    public void evaluateInspectOnMethodVarWithDefKeyword() throws Exception {
        // given
        String code = ("def display = new TableDisplay()" + (System.lineSeparator())) + "display.setDoubleCli\u0000ckAction()";
        String expected = METHOD_TABLE_DISPLAY_SETDBLCLICK;
        // when
        InspectResult result = callInspectOnCaretPos(code);
        assertThat(result.getFound()).isTrue();
        assertThat(result.getData().getTextplain()).isEqualTo(expected);
    }

    @Test
    public void evaluateInspectInsideSingleQuotes() throws Exception {
        // given
        String code = "f[\'la\u0000st\'] = \"Last\"";
        // when
        InspectResult result = callInspectOnCaretPos(code);
        // then
        assertThat(result.getFound()).isFalse();
    }

    @Test
    public void evaluateInspectBracketBeforeNew() throws Exception {
        // given
        String code = "def display = new Table\u0000Display(new CSV().read(\"../resources/data/interest-rates.csv\"))";
        String expected = CLASS_TABLE_DISPLAY;
        // when
        InspectResult result = callInspectOnCaretPos(code);
        // then
        assertThat(result.getFound()).isTrue();
        assertThat(result.getData().getTextplain()).isEqualTo(expected);
    }
}

