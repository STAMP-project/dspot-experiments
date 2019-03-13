/**
 * Copyright 2017 TWO SIGMA OPEN SOURCE, LLC
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
package com.twosigma.beakerx.groovy.evaluator.autocomplete;


import com.twosigma.beakerx.autocomplete.AutocompleteResult;
import com.twosigma.beakerx.evaluator.BaseEvaluator;
import java.util.stream.Collectors;
import org.junit.Test;


public class GroovyEvaluatorAutocompleteTest {
    private static BaseEvaluator groovyEvaluator;

    @Test
    public void shouldReturnPrintlnForFirstLine() throws Exception {
        // when
        AutocompleteResult autocomplete = GroovyEvaluatorAutocompleteTest.groovyEvaluator.autocomplete(("System.out.printl\n" + (("System.out.print\n" + "System.out.prin\n") + "System.out.pri\n")), 17);
        // then
        assertThat(autocomplete.getMatches()).isNotEmpty();
        assertThat(autocomplete.getStartIndex()).isEqualTo(11);
    }

    @Test
    public void shouldReturnPrintlnForSecondLine() throws Exception {
        // when
        AutocompleteResult autocomplete = GroovyEvaluatorAutocompleteTest.groovyEvaluator.autocomplete(("System.out.printl\n" + (("System.out.print\n" + "System.out.prin\n") + "System.out.pri\n")), 34);
        // then
        assertThat(autocomplete.getMatches()).isNotEmpty();
        assertThat(autocomplete.getStartIndex()).isEqualTo(29);
    }

    @Test
    public void shouldReturnAutocompleteForPrintlnWithComment() throws Exception {
        // when
        AutocompleteResult autocomplete = GroovyEvaluatorAutocompleteTest.groovyEvaluator.autocomplete(("//comment\n" + ("System.out.printl\n" + "System.out.printl")), 27);
        // then
        assertThat(autocomplete.getMatches()).isNotEmpty();
        assertThat(autocomplete.getStartIndex()).isEqualTo(21);
    }

    @Test
    public void autocompleteMatchesForSystemAfterDot() throws Exception {
        String code = "System.";
        // when
        AutocompleteResult autocomplete = GroovyEvaluatorAutocompleteTest.groovyEvaluator.autocomplete(code, code.length());
        // then
        assertThat(autocomplete.getMatches()).isNotEmpty();
    }

    @Test
    public void autocompleteMatchesForSystemOutAfterDot() throws Exception {
        String code = "System.out.";
        // when
        AutocompleteResult autocomplete = GroovyEvaluatorAutocompleteTest.groovyEvaluator.autocomplete(code, code.length());
        // then
        assertThat(autocomplete.getMatches()).isNotEmpty();
        assertThat(autocomplete.getMatches()).contains("println");
        assertThat(autocomplete.getStartIndex()).isEqualTo(code.length());
    }

    @Test
    public void shouldReturnResultEqualToImport() throws Exception {
        String code = "imp";
        // when
        AutocompleteResult autocomplete = GroovyEvaluatorAutocompleteTest.groovyEvaluator.autocomplete(code, code.length());
        // then
        assertThat(autocomplete.getMatches().get(0)).isEqualToIgnoringCase("import");
    }

    @Test
    public void shouldReturnResultEqualToToString() throws Exception {
        String code = "def v = \'str\'\nv.toS";
        // when
        AutocompleteResult autocomplete = GroovyEvaluatorAutocompleteTest.groovyEvaluator.autocomplete(code, code.length());
        // then
        assertThat(autocomplete.getMatches().get(0)).isEqualToIgnoringCase("toString");
    }

    @Test
    public void shouldReturnResultEqualToParamInt() throws Exception {
        String code = "int paramInt = 10\n" + "println par";
        // when
        AutocompleteResult autocomplete = GroovyEvaluatorAutocompleteTest.groovyEvaluator.autocomplete(code, code.length());
        // then
        assertThat(autocomplete.getMatches().get(0)).isEqualToIgnoringCase("paramInt");
    }

    @Test
    public void shouldReturnResultEqualToParamDouble() throws Exception {
        String code = "def paramDouble = 10.0\n" + "println par";
        // when
        AutocompleteResult autocomplete = GroovyEvaluatorAutocompleteTest.groovyEvaluator.autocomplete(code, code.length());
        // then
        assertThat(autocomplete.getMatches().get(0)).isEqualToIgnoringCase("paramDouble");
    }

    @Test
    public void shouldReturnResultEqualToParamString() throws Exception {
        String code = "def paramString = \'str\'\n" + "println \"test ${par";
        // when
        AutocompleteResult autocomplete = GroovyEvaluatorAutocompleteTest.groovyEvaluator.autocomplete(code, code.length());
        // then
        assertThat(autocomplete.getMatches().get(0)).isEqualToIgnoringCase("paramString");
    }

    @Test
    public void shouldReturnResultEqualToParamArray() throws Exception {
        String code = "def paramArray = [1, 3, 5]\n" + "println par";
        // when
        AutocompleteResult autocomplete = GroovyEvaluatorAutocompleteTest.groovyEvaluator.autocomplete(code, code.length());
        // then
        assertThat(autocomplete.getMatches().get(0)).isEqualToIgnoringCase("paramArray");
    }

    @Test
    public void shouldReturnResultEqualToParamMap() throws Exception {
        String code = "def paramMap = [\'abc\':1, \'def\':2, \'xyz\':3]\n" + "println par";
        // when
        AutocompleteResult autocomplete = GroovyEvaluatorAutocompleteTest.groovyEvaluator.autocomplete(code, code.length());
        // then
        assertThat(autocomplete.getMatches().get(0)).isEqualToIgnoringCase("paramMap");
    }

    @Test
    public void shouldReturnResultEqualToBLUE() throws Exception {
        String code = "import static java.awt.Color.BLUE\n" + "println BL";
        // when
        AutocompleteResult autocomplete = GroovyEvaluatorAutocompleteTest.groovyEvaluator.autocomplete(code, code.length());
        // then
        assertThat(autocomplete.getMatches().get(0)).isEqualToIgnoringCase("BLUE");
        assertThat(autocomplete.getStartIndex()).isEqualTo(((code.length()) - 2));
    }

    @Test
    public void autocompleteForClass_shouldReturnResultEqualToCoordinates() throws Exception {
        String code = "class Coordinates {\n" + ((("double latitude\n" + "double longitude }\n") + "def coordinates = new Coordinates(latitude: 43.23, longitude: 3.67)\n") + "this.class.co");
        // when
        AutocompleteResult autocomplete = GroovyEvaluatorAutocompleteTest.groovyEvaluator.autocomplete(code, code.length());
        // then
        assertThat(autocomplete.getMatches().get(0)).isEqualToIgnoringCase("coordinates");
    }

    @Test
    public void shouldReturnResultEqualToPackage() throws Exception {
        String code = "pack";
        // when
        AutocompleteResult autocomplete = GroovyEvaluatorAutocompleteTest.groovyEvaluator.autocomplete(code, code.length());
        // then
        assertThat(autocomplete.getMatches()).isNotEmpty();
        assertThat(autocomplete.getStartIndex()).isEqualTo(0);
    }

    @Test
    public void shouldReturnImplements() throws Exception {
        String code = "class Coordinates implemen";
        // when
        AutocompleteResult autocomplete = GroovyEvaluatorAutocompleteTest.groovyEvaluator.autocomplete(code, code.length());
        // then
        assertThat(autocomplete.getMatches().get(0)).isEqualTo("implements");
        assertThat(autocomplete.getStartIndex()).isEqualTo(((code.length()) - 8));
    }

    @Test
    public void shouldReturnExtends() throws Exception {
        String code = "class Coordinates exten";
        // when
        AutocompleteResult autocomplete = GroovyEvaluatorAutocompleteTest.groovyEvaluator.autocomplete(code, code.length());
        // then
        assertThat(autocomplete.getMatches().get(0)).isEqualTo("extends");
        assertThat(autocomplete.getStartIndex()).isEqualTo(((code.length()) - 5));
    }

    @Test
    public void shouldReturnClass() throws Exception {
        String code = "cla";
        // when
        AutocompleteResult autocomplete = GroovyEvaluatorAutocompleteTest.groovyEvaluator.autocomplete(code, code.length());
        // then
        assertThat(autocomplete.getMatches().get(0)).isEqualTo("class");
        assertThat(autocomplete.getStartIndex()).isEqualTo(0);
    }

    @Test
    public void shouldAutocompleteToSystem() throws Exception {
        String code = "Syste";
        // when
        AutocompleteResult autocomplete = GroovyEvaluatorAutocompleteTest.groovyEvaluator.autocomplete(code, code.length());
        // then
        assertThat(autocomplete.getMatches().get(0)).isEqualTo("System");
        assertThat(autocomplete.getStartIndex()).isEqualTo(0);
    }

    @Test
    public void shouldAutocompleteToB() throws Exception {
        String code = "import java.awt.Color\n" + "println Color.B";
        // when
        AutocompleteResult autocomplete = GroovyEvaluatorAutocompleteTest.groovyEvaluator.autocomplete(code, code.length());
        // then
        assertThat(autocomplete.getMatches()).isNotEmpty();
        assertThat(autocomplete.getStartIndex()).isEqualTo(((code.length()) - 1));
    }

    @Test
    public void shouldAutocompleteWithAsterisk() throws Exception {
        String code = "import java.awt.*\n" + "println Color.B";
        // when
        AutocompleteResult autocomplete = GroovyEvaluatorAutocompleteTest.groovyEvaluator.autocomplete(code, code.length());
        // then
        assertThat(autocomplete.getMatches()).isNotEmpty();
        assertThat(autocomplete.getStartIndex()).isEqualTo(((code.length()) - 1));
    }

    @Test
    public void autocompleteShouldNotMatchForEmptyString() throws Exception {
        String code = "";
        // when
        AutocompleteResult autocomplete = GroovyEvaluatorAutocompleteTest.groovyEvaluator.autocomplete(code, code.length());
        // then
        assertThat(autocomplete.getMatches()).isEmpty();
        assertThat(autocomplete.getStartIndex()).isEqualTo(0);
    }

    @Test
    public void defaultImportsAutocompleteToRED() throws Exception {
        String code = "def colors = [ Color.RE";
        // when
        AutocompleteResult autocomplete = GroovyEvaluatorAutocompleteTest.groovyEvaluator.autocomplete(code, code.length());
        // then
        assertThat(autocomplete.getMatches()).isNotEmpty();
        assertThat(autocomplete.getStartIndex()).isEqualTo(((code.length()) - 2));
    }

    @Test
    public void autocompleteMatchesForColorAfterDot() throws Exception {
        String code = "Color.";
        // when
        AutocompleteResult autocomplete = GroovyEvaluatorAutocompleteTest.groovyEvaluator.autocomplete(code, code.length());
        // then
        assertThat(autocomplete.getMatches()).isNotEmpty();
        assertThat(autocomplete.getStartIndex()).isEqualTo(code.length());
    }

    @Test
    public void autocompleteMatchesToColor() throws Exception {
        String code = "Colo";
        // when
        AutocompleteResult autocomplete = GroovyEvaluatorAutocompleteTest.groovyEvaluator.autocomplete(code, code.length());
        // then
        assertThat(autocomplete.getMatches()).isNotEmpty();
        assertThat(autocomplete.getStartIndex()).isEqualTo(0);
    }

    @Test
    public void autocompleteMatchesToRED() throws Exception {
        String code = "Color.R";
        // when
        AutocompleteResult autocomplete = GroovyEvaluatorAutocompleteTest.groovyEvaluator.autocomplete(code, code.length());
        // then
        assertThat(autocomplete.getMatches()).isNotEmpty();
        assertThat(autocomplete.getStartIndex()).isEqualTo(((code.length()) - 1));
    }

    @Test
    public void autocompleteWithMagicCommands() throws Exception {
        String code = "%classpath add jar demoResources/BeakerXClasspathTest.jar\n" + "System.";
        // when
        AutocompleteResult autocomplete = GroovyEvaluatorAutocompleteTest.groovyEvaluator.autocomplete(code, code.length());
        // then
        assertThat(autocomplete.getMatches()).isNotEmpty();
        assertThat(autocomplete.getStartIndex()).isEqualTo(code.length());
    }

    @Test
    public void autocompleteToArrayList() throws Exception {
        String code = "ArrayLi";
        // when
        AutocompleteResult autocomplete = GroovyEvaluatorAutocompleteTest.groovyEvaluator.autocomplete(code, code.length());
        // then
        assertThat(autocomplete.getMatches()).isNotEmpty();
        assertThat(autocomplete.getStartIndex()).isEqualTo(0);
    }

    @Test
    public void autocompleteToList() throws Exception {
        String code = "Li";
        // when
        AutocompleteResult autocomplete = GroovyEvaluatorAutocompleteTest.groovyEvaluator.autocomplete(code, code.length());
        // then
        assertThat(autocomplete.getMatches()).isNotEmpty();
        assertThat(autocomplete.getStartIndex()).isEqualTo(0);
    }

    @Test
    public void autocompleteArrayListAfterDot() throws Exception {
        String code = "List myList = new ArrayList();\n" + "myList.";
        // when
        AutocompleteResult autocomplete = GroovyEvaluatorAutocompleteTest.groovyEvaluator.autocomplete(code, code.length());
        // then
        assertThat(autocomplete.getMatches()).isNotEmpty();
        assertThat(autocomplete.getMatches().stream().filter(( x) -> x.contains("add")).collect(Collectors.toList())).isNotEmpty();
        assertThat(autocomplete.getStartIndex()).isEqualTo(code.length());
    }

    @Test
    public void autocompleteMapAfterDot() throws Exception {
        String code = "Map myMap = new HashMap<>();\n" + "myMap.";
        // when
        AutocompleteResult autocomplete = GroovyEvaluatorAutocompleteTest.groovyEvaluator.autocomplete(code, code.length());
        // then
        assertThat(autocomplete.getMatches()).isNotEmpty();
        assertThat(autocomplete.getMatches().stream().filter(( x) -> x.contains("put")).collect(Collectors.toList())).isNotEmpty();
        assertThat(autocomplete.getStartIndex()).isEqualTo(code.length());
    }

    @Test
    public void autocompleteArrayListWithGenericsAfterDot() throws Exception {
        String code = "List<String> myList = new ArrayList();\n" + "myList.";
        // when
        AutocompleteResult autocomplete = GroovyEvaluatorAutocompleteTest.groovyEvaluator.autocomplete(code, code.length());
        // then
        assertThat(autocomplete.getMatches()).isNotEmpty();
        assertThat(autocomplete.getMatches().stream().filter(( x) -> x.contains("add")).collect(Collectors.toList())).isNotEmpty();
        assertThat(autocomplete.getStartIndex()).isEqualTo(code.length());
    }

    @Test
    public void autocompleteMapWithGenericsAfterDot() throws Exception {
        String code = "Map<String,String> myMap = new HashMap<>();\n" + "myMap.";
        // when
        AutocompleteResult autocomplete = GroovyEvaluatorAutocompleteTest.groovyEvaluator.autocomplete(code, code.length());
        // then
        assertThat(autocomplete.getMatches()).isNotEmpty();
        assertThat(autocomplete.getMatches().stream().filter(( x) -> x.contains("put")).collect(Collectors.toList())).isNotEmpty();
        assertThat(autocomplete.getStartIndex()).isEqualTo(code.length());
    }

    @Test
    public void autocompleteForJavaAwtAfterDotPackage() throws Exception {
        String code = "java.awt.";
        // when
        AutocompleteResult autocomplete = GroovyEvaluatorAutocompleteTest.groovyEvaluator.autocomplete(code, code.length());
        // then
        assertThat(autocomplete.getMatches()).isNotEmpty();
        assertThat(autocomplete.getStartIndex()).isEqualTo(code.length());
    }

    @Test
    public void autocompleteForJavaAwtPackage() throws Exception {
        String code = "java.aw";
        // when
        AutocompleteResult autocomplete = GroovyEvaluatorAutocompleteTest.groovyEvaluator.autocomplete(code, code.length());
        // then
        assertThat(autocomplete.getMatches()).isNotEmpty();
        assertThat(autocomplete.getStartIndex()).isEqualTo(((code.length()) - 2));
    }

    @Test
    public void autocompleteForJavaPackage() throws Exception {
        String code = "java.";
        // when
        AutocompleteResult autocomplete = GroovyEvaluatorAutocompleteTest.groovyEvaluator.autocomplete(code, code.length());
        // then
        assertThat(autocomplete.getMatches()).isNotEmpty();
        assertThat(autocomplete.getStartIndex()).isEqualTo(code.length());
    }

    @Test
    public void autocompleteToJavaPackage() throws Exception {
        String code = "jav";
        // when
        AutocompleteResult autocomplete = GroovyEvaluatorAutocompleteTest.groovyEvaluator.autocomplete(code, code.length());
        // then
        assertThat(autocomplete.getMatches()).isNotEmpty();
        assertThat(autocomplete.getStartIndex()).isEqualTo(0);
    }

    @Test
    public void autocompleteStringMethod() throws Exception {
        String code = "a = \"ABC\";\n" + "a.";
        // when
        AutocompleteResult autocomplete = GroovyEvaluatorAutocompleteTest.groovyEvaluator.autocomplete(code, code.length());
        // then
        assertThat(autocomplete.getMatches()).isNotEmpty();
        assertThat(autocomplete.getStartIndex()).isEqualTo(code.length());
    }

    @Test
    public void autocompleteToMethodsForImportedClassesSeparatedByNewLine() throws Exception {
        String code = "import com.twosigma.beakerx.mimetype.MIMEContainer\n" + (("import groovy.json.JsonSlurper\n" + "def jsonSlurper = new JsonSlurper()\n") + "def json = jsonSlurper.");
        // when
        AutocompleteResult autocomplete = GroovyEvaluatorAutocompleteTest.groovyEvaluator.autocomplete(code, code.length());
        // then
        assertThat(autocomplete.getMatches()).isNotEmpty();
        assertThat(autocomplete.getStartIndex()).isEqualTo(code.length());
    }

    @Test
    public void autocompleteToMethodsForImportedClassesSeparatedBySemicolon() throws Exception {
        String code = "import com.twosigma.beakerx.mimetype.MIMEContainer;" + (("import groovy.json.JsonSlurper\n" + "def jsonSlurper = new JsonSlurper()\n") + "def json = jsonSlurper.");
        // when
        AutocompleteResult autocomplete = GroovyEvaluatorAutocompleteTest.groovyEvaluator.autocomplete(code, code.length());
        // then
        assertThat(autocomplete.getMatches()).isNotEmpty();
        assertThat(autocomplete.getStartIndex()).isEqualTo(code.length());
    }

    @Test
    public void autocompleteToFileMethods() throws Exception {
        String code = "fname = \"demoResources/bar-chart.vg.json\"\n" + ("fileContents = new File(fname)\n" + "fileContents.t");
        // when
        AutocompleteResult autocomplete = GroovyEvaluatorAutocompleteTest.groovyEvaluator.autocomplete(code, code.length());
        // then
        assertThat(autocomplete.getMatches()).isNotEmpty();
        assertThat(autocomplete.getStartIndex()).isEqualTo(((code.length()) - 1));
    }

    @Test
    public void shouldReturnEmptyResultForIncorrectCode() throws Exception {
        String code = "]";
        // when
        AutocompleteResult autocomplete = GroovyEvaluatorAutocompleteTest.groovyEvaluator.autocomplete(code, code.length());
        // then
        assertThat(autocomplete.getMatches()).isEmpty();
        assertThat(autocomplete.getStartIndex()).isEqualTo(0);
    }

    @Test
    public void shouldCompleteToSINGLE_COLUMN() throws Exception {
        // when
        String code = "import com.twosigma.beakerx.table.highlight.*\n" + (((((((((((("\n" + "def table = new TableDisplay([[1,2,3], \n") + "                              [3,4,5], \n") + "                              [6,2,8], \n") + "                              [6,2,8], \n") + "                              [6,2,8], \n") + "                              [6,4,8], \n") + "                              [6,2,8], \n") + "                              [6,2,8], \n") + "                              [6,5,8]], \n") + "                             [\'a\', \'b\', \'c\'], \n") + "                             [\'double\', \'double\', \'double\'])\n") + "table.addCellHighlighter(TableDisplayCellHighlighter.getUniqueEntriesHighlighter(\"b\", TableDisplayCellHighlighter.");
        AutocompleteResult autocomplete = GroovyEvaluatorAutocompleteTest.groovyEvaluator.autocomplete(code, code.length());
        // then
        assertThat(autocomplete.getMatches()).isNotEmpty();
        assertThat(autocomplete.getStartIndex()).isEqualTo(code.length());
    }

    @Test
    public void shouldOnlyShowAutocompleteForTableDisplayCellHighlighter() throws Exception {
        // when
        String code = "TableDisplayCellHighlighter.";
        AutocompleteResult autocomplete = GroovyEvaluatorAutocompleteTest.groovyEvaluator.autocomplete(code, code.length());
        // then
        assertThat(autocomplete.getStartIndex()).isEqualTo(code.length());
        assertThat(autocomplete.getMatches().size()).isEqualTo(4);
    }

    @Test
    public void shouldAutocompleteToPrintln() throws Exception {
        // when
        String code = "System.out.printl";
        AutocompleteResult autocomplete = GroovyEvaluatorAutocompleteTest.groovyEvaluator.autocomplete(code, code.length());
        // then
        assertThat(autocomplete.getStartIndex()).isEqualTo(11);
        assertThat(autocomplete.getMatches().size()).isEqualTo(1);
    }
}

