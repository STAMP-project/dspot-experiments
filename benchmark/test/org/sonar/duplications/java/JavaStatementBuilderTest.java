/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.duplications.java;


import java.util.List;
import org.junit.Test;
import org.sonar.duplications.DuplicationsTestUtil;
import org.sonar.duplications.statement.Statement;
import org.sonar.duplications.statement.StatementChunker;
import org.sonar.duplications.token.TokenChunker;


public class JavaStatementBuilderTest {
    private final TokenChunker tokenChunker = JavaTokenProducer.build();

    private final StatementChunker statementChunker = JavaStatementBuilder.build();

    @Test
    public void shouldIgnoreImportStatement() {
        assertThat(chunk("import org.sonar.duplications.java;")).isEmpty();
    }

    @Test
    public void shouldIgnorePackageStatement() {
        assertThat(chunk("package org.sonar.duplications.java;")).isEmpty();
    }

    @Test
    public void shouldHandleAnnotation() {
        List<Statement> statements = chunk(("" + (("@Entity" + "@Table(name = \"properties\")") + "@Column(updatable = true, nullable = true)")));
        assertThat(statements).hasSize(3);
        assertThat(statements.get(0).getValue()).isEqualTo("@Entity");
        assertThat(statements.get(1).getValue()).isEqualTo("@Table(name=$CHARS)");
        assertThat(statements.get(2).getValue()).isEqualTo("@Column(updatable=true,nullable=true)");
    }

    @Test
    public void shouldHandleIf() {
        List<Statement> statements = chunk("if (a > b) { something(); }");
        assertThat(statements.size()).isEqualTo(2);
        assertThat(statements.get(0).getValue()).isEqualTo("if(a>b)");
        assertThat(statements.get(1).getValue()).isEqualTo("something()");
        statements = chunk("if (a > b) { something(); } else { somethingOther(); }");
        assertThat(statements.size()).isEqualTo(4);
        assertThat(statements.get(0).getValue()).isEqualTo("if(a>b)");
        assertThat(statements.get(1).getValue()).isEqualTo("something()");
        assertThat(statements.get(2).getValue()).isEqualTo("else");
        assertThat(statements.get(3).getValue()).isEqualTo("somethingOther()");
        statements = chunk("if (a > 0) { something(); } else if (a == 0) { somethingOther(); }");
        assertThat(statements.size()).isEqualTo(4);
        assertThat(statements.get(0).getValue()).isEqualTo("if(a>$NUMBER)");
        assertThat(statements.get(1).getValue()).isEqualTo("something()");
        assertThat(statements.get(2).getValue()).isEqualTo("elseif(a==$NUMBER)");
        assertThat(statements.get(3).getValue()).isEqualTo("somethingOther()");
    }

    @Test
    public void shouldHandleFor() {
        List<Statement> statements = chunk("for (int i = 0; i < 10; i++) { something(); }");
        assertThat(statements.size()).isEqualTo(2);
        assertThat(statements.get(0).getValue()).isEqualTo("for(inti=$NUMBER;i<$NUMBER;i++)");
        assertThat(statements.get(1).getValue()).isEqualTo("something()");
        statements = chunk("for (Item item : items) { something(); }");
        assertThat(statements.size()).isEqualTo(2);
        assertThat(statements.get(0).getValue()).isEqualTo("for(Itemitem:items)");
        assertThat(statements.get(1).getValue()).isEqualTo("something()");
    }

    @Test
    public void shouldHandleWhile() {
        List<Statement> statements = chunk("while (i < args.length) { something(); }");
        assertThat(statements.size()).isEqualTo(2);
        assertThat(statements.get(0).getValue()).isEqualTo("while(i<args.length)");
        assertThat(statements.get(1).getValue()).isEqualTo("something()");
        statements = chunk("while (true);");
        assertThat(statements.size()).isEqualTo(1);
        assertThat(statements.get(0).getValue()).isEqualTo("while(true)");
    }

    @Test
    public void shouldHandleDoWhile() {
        List<Statement> statements = chunk("do { something(); } while (true);");
        assertThat(statements.size()).isEqualTo(3);
        assertThat(statements.get(0).getValue()).isEqualTo("do");
        assertThat(statements.get(1).getValue()).isEqualTo("something()");
        assertThat(statements.get(2).getValue()).isEqualTo("while(true)");
    }

    @Test
    public void shouldHandleSwitch() {
        List<Statement> statements = chunk(("" + (((("switch (month) {" + "  case 1 : monthString=\"January\"; break;") + "  case 2 : monthString=\"February\"; break;") + "  default: monthString=\"Invalid\";") + "}")));
        assertThat(statements.size()).isEqualTo(6);
        assertThat(statements.get(0).getValue()).isEqualTo("switch(month)");
        assertThat(statements.get(1).getValue()).isEqualTo("case$NUMBER:monthString=$CHARS");
        assertThat(statements.get(2).getValue()).isEqualTo("break");
        assertThat(statements.get(3).getValue()).isEqualTo("case$NUMBER:monthString=$CHARS");
        assertThat(statements.get(4).getValue()).isEqualTo("break");
        assertThat(statements.get(5).getValue()).isEqualTo("default:monthString=$CHARS");
    }

    /**
     * See SONAR-2782
     */
    @Test
    public void shouldHandleNestedSwitch() {
        List<Statement> statements = chunk(("" + ((("switch (a) {" + "  case 'a': case 'b': case 'c': something(); break;") + "  case 'd': case 'e': case 'f': somethingOther(); break;") + "}")));
        assertThat(statements.size()).isEqualTo(5);
        assertThat(statements.get(0).getValue()).isEqualTo("switch(a)");
        assertThat(statements.get(1).getValue()).isEqualTo("case$CHARS:case$CHARS:case$CHARS:something()");
        assertThat(statements.get(2).getValue()).isEqualTo("break");
        assertThat(statements.get(3).getValue()).isEqualTo("case$CHARS:case$CHARS:case$CHARS:somethingOther()");
        assertThat(statements.get(4).getValue()).isEqualTo("break");
    }

    @Test
    public void shouldHandleArray() {
        List<Statement> statements = chunk("new Integer[] { 1, 2, 3, 4 };");
        assertThat(statements.size()).isEqualTo(2);
        assertThat(statements.get(0).getValue()).isEqualTo("newInteger[]");
        assertThat(statements.get(1).getValue()).isEqualTo("{$NUMBER,$NUMBER,$NUMBER,$NUMBER}");
    }

    /**
     * See SONAR-2837
     */
    @Test
    public void shouldHandleMultidimensionalArray() {
        List<Statement> statements = chunk("new Integer[][] { { 1, 2 }, {3, 4} };");
        assertThat(statements.size()).isEqualTo(2);
        assertThat(statements.get(0).getValue()).isEqualTo("newInteger[][]");
        assertThat(statements.get(1).getValue()).isEqualTo("{{$NUMBER,$NUMBER},{$NUMBER,$NUMBER}}");
        statements = chunk("new Integer[][] { null, {3, 4} };");
        assertThat(statements.size()).isEqualTo(2);
        assertThat(statements.get(0).getValue()).isEqualTo("newInteger[][]");
        assertThat(statements.get(1).getValue()).isEqualTo("{null,{$NUMBER,$NUMBER}}");
    }

    @Test
    public void shouldHandleTryCatch() {
        List<Statement> statements;
        statements = chunk("try { } catch (Exception e) { }");
        assertThat(statements.size()).isEqualTo(4);
        assertThat(statements.get(0).getValue()).isEqualTo("try");
        assertThat(statements.get(1).getValue()).isEqualTo("{}");
        assertThat(statements.get(2).getValue()).isEqualTo("catch(Exceptione)");
        assertThat(statements.get(3).getValue()).isEqualTo("{}");
        statements = chunk("try { something(); } catch (Exception e) { }");
        assertThat(statements.size()).isEqualTo(4);
        assertThat(statements.get(0).getValue()).isEqualTo("try");
        assertThat(statements.get(1).getValue()).isEqualTo("something()");
        assertThat(statements.get(2).getValue()).isEqualTo("catch(Exceptione)");
        assertThat(statements.get(3).getValue()).isEqualTo("{}");
        statements = chunk("try { something(); } catch (Exception e) { onException(); }");
        assertThat(statements.size()).isEqualTo(4);
        assertThat(statements.get(0).getValue()).isEqualTo("try");
        assertThat(statements.get(1).getValue()).isEqualTo("something()");
        assertThat(statements.get(2).getValue()).isEqualTo("catch(Exceptione)");
        assertThat(statements.get(3).getValue()).isEqualTo("onException()");
        statements = chunk("try { something(); } catch (Exception1 e) { onException1(); } catch (Exception2 e) { onException2(); }");
        assertThat(statements.size()).isEqualTo(6);
        assertThat(statements.get(0).getValue()).isEqualTo("try");
        assertThat(statements.get(1).getValue()).isEqualTo("something()");
        assertThat(statements.get(2).getValue()).isEqualTo("catch(Exception1e)");
        assertThat(statements.get(3).getValue()).isEqualTo("onException1()");
        assertThat(statements.get(4).getValue()).isEqualTo("catch(Exception2e)");
        assertThat(statements.get(5).getValue()).isEqualTo("onException2()");
    }

    @Test
    public void shouldHandleTryFinnaly() {
        List<Statement> statements;
        statements = chunk("try { } finally { }");
        assertThat(statements.size()).isEqualTo(4);
        assertThat(statements.get(0).getValue()).isEqualTo("try");
        assertThat(statements.get(1).getValue()).isEqualTo("{}");
        assertThat(statements.get(2).getValue()).isEqualTo("finally");
        assertThat(statements.get(3).getValue()).isEqualTo("{}");
        statements = chunk("try { something(); } finally { }");
        assertThat(statements.size()).isEqualTo(4);
        assertThat(statements.get(0).getValue()).isEqualTo("try");
        assertThat(statements.get(1).getValue()).isEqualTo("something()");
        assertThat(statements.get(2).getValue()).isEqualTo("finally");
        assertThat(statements.get(3).getValue()).isEqualTo("{}");
        statements = chunk("try { something(); } finally { somethingOther(); }");
        assertThat(statements.size()).isEqualTo(4);
        assertThat(statements.get(0).getValue()).isEqualTo("try");
        assertThat(statements.get(1).getValue()).isEqualTo("something()");
        assertThat(statements.get(2).getValue()).isEqualTo("finally");
        assertThat(statements.get(3).getValue()).isEqualTo("somethingOther()");
    }

    @Test
    public void shouldHandleTryCatchFinally() {
        List<Statement> statements;
        statements = chunk("try { } catch (Exception e) {} finally { }");
        assertThat(statements.size()).isEqualTo(6);
        assertThat(statements.get(0).getValue()).isEqualTo("try");
        assertThat(statements.get(1).getValue()).isEqualTo("{}");
        assertThat(statements.get(2).getValue()).isEqualTo("catch(Exceptione)");
        assertThat(statements.get(3).getValue()).isEqualTo("{}");
        assertThat(statements.get(4).getValue()).isEqualTo("finally");
        assertThat(statements.get(5).getValue()).isEqualTo("{}");
        statements = chunk("try { something(); } catch (Exception e) { onException(); } finally { somethingOther(); }");
        assertThat(statements.size()).isEqualTo(6);
        assertThat(statements.get(0).getValue()).isEqualTo("try");
        assertThat(statements.get(1).getValue()).isEqualTo("something()");
        assertThat(statements.get(2).getValue()).isEqualTo("catch(Exceptione)");
        assertThat(statements.get(3).getValue()).isEqualTo("onException()");
        assertThat(statements.get(4).getValue()).isEqualTo("finally");
        assertThat(statements.get(5).getValue()).isEqualTo("somethingOther()");
    }

    /**
     * Java 7.
     */
    @Test
    public void shouldHandleMultiCatch() {
        List<Statement> statements;
        statements = chunk("try { } catch (Exception1 | Exception2 e) { }");
        assertThat(statements.size()).isEqualTo(4);
        assertThat(statements.get(0).getValue()).isEqualTo("try");
        assertThat(statements.get(1).getValue()).isEqualTo("{}");
        assertThat(statements.get(2).getValue()).isEqualTo("catch(Exception1|Exception2e)");
        assertThat(statements.get(3).getValue()).isEqualTo("{}");
        statements = chunk("try { something(); } catch (Exception1 | Exception2 e) { }");
        assertThat(statements.size()).isEqualTo(4);
        assertThat(statements.get(0).getValue()).isEqualTo("try");
        assertThat(statements.get(1).getValue()).isEqualTo("something()");
        assertThat(statements.get(2).getValue()).isEqualTo("catch(Exception1|Exception2e)");
        assertThat(statements.get(3).getValue()).isEqualTo("{}");
        statements = chunk("try { something(); } catch (Exception1 | Exception2 e) { onException(); }");
        assertThat(statements.size()).isEqualTo(4);
        assertThat(statements.get(0).getValue()).isEqualTo("try");
        assertThat(statements.get(1).getValue()).isEqualTo("something()");
        assertThat(statements.get(2).getValue()).isEqualTo("catch(Exception1|Exception2e)");
        assertThat(statements.get(3).getValue()).isEqualTo("onException()");
    }

    /**
     * Java 7.
     */
    @Test
    public void shouldHandleTryWithResource() {
        List<Statement> statements;
        statements = chunk("try (FileInputStream in = new FileInputStream()) {}");
        assertThat(statements.size()).isEqualTo(2);
        assertThat(statements.get(0).getValue()).isEqualTo("try(FileInputStreamin=newFileInputStream())");
        assertThat(statements.get(1).getValue()).isEqualTo("{}");
        statements = chunk("try (FileInputStream in = new FileInputStream(); FileOutputStream out = new FileOutputStream()) {}");
        assertThat(statements.size()).isEqualTo(2);
        assertThat(statements.get(0).getValue()).isEqualTo("try(FileInputStreamin=newFileInputStream();FileOutputStreamout=newFileOutputStream())");
        assertThat(statements.get(1).getValue()).isEqualTo("{}");
        statements = chunk("try (FileInputStream in = new FileInputStream(); FileOutputStream out = new FileOutputStream();) {}");
        assertThat(statements.size()).isEqualTo(2);
        assertThat(statements.get(0).getValue()).isEqualTo("try(FileInputStreamin=newFileInputStream();FileOutputStreamout=newFileOutputStream();)");
        assertThat(statements.get(1).getValue()).isEqualTo("{}");
        statements = chunk("try (FileInputStream in = new FileInputStream()) { something(); }");
        assertThat(statements.size()).isEqualTo(2);
        assertThat(statements.get(0).getValue()).isEqualTo("try(FileInputStreamin=newFileInputStream())");
        assertThat(statements.get(1).getValue()).isEqualTo("something()");
    }

    @Test
    public void realExamples() {
        assertThat(chunk(DuplicationsTestUtil.findFile("/java/MessageResources.java")).size()).isGreaterThan(0);
        assertThat(chunk(DuplicationsTestUtil.findFile("/java/RequestUtils.java")).size()).isGreaterThan(0);
    }
}

