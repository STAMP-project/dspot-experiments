/**
 * Copyright 2018 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.backend.javascript.rendering;


import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.teavm.backend.javascript.codegen.SourceWriter;
import org.teavm.backend.javascript.codegen.SourceWriterBuilder;


public class AstWriterTest {
    private StringBuilder sb = new StringBuilder();

    private SourceWriter sourceWriter;

    private AstWriter writer;

    public AstWriterTest() {
        SourceWriterBuilder builder = new SourceWriterBuilder(null);
        builder.setMinified(true);
        sourceWriter = builder.build(sb);
        writer = new AstWriter(sourceWriter);
    }

    @Test
    public void writesReturn() throws IOException {
        Assert.assertThat(transform("return x;"), CoreMatchers.is("return x;"));
    }

    @Test
    public void writesEmptyReturn() throws IOException {
        Assert.assertThat(transform("return;"), CoreMatchers.is("return;"));
    }

    @Test
    public void writesThrow() throws IOException {
        Assert.assertThat(transform("throw x;"), CoreMatchers.is("throw x;"));
    }

    @Test
    public void writesBreak() throws IOException {
        Assert.assertThat(transform("a: while (true) { break a; }"), CoreMatchers.is("a:while(true){break a;}"));
    }

    @Test
    public void writesEmptyBreak() throws IOException {
        Assert.assertThat(transform("while(true) { break; }"), CoreMatchers.is("while(true){break;}"));
    }

    @Test
    public void writesContinue() throws IOException {
        Assert.assertThat(transform("a: while (true) { continue a; }"), CoreMatchers.is("a:while(true){continue a;}"));
    }

    @Test
    public void writesEmptyContinue() throws IOException {
        Assert.assertThat(transform("while(true) { continue; }"), CoreMatchers.is("while(true){continue;}"));
    }

    @Test
    public void writesBlock() throws IOException {
        Assert.assertThat(transform("{ foo(); bar(); }"), CoreMatchers.is("{foo();bar();}"));
    }

    @Test
    public void writesTryCatch() throws IOException {
        Assert.assertThat(transform("try { foo(); } catch (e) { alert(e); }"), CoreMatchers.is("try {foo();}catch(e){alert(e);}"));
        Assert.assertThat(transform("try { foo(); } finally { close(); }"), CoreMatchers.is("try {foo();}finally {close();}"));
    }

    @Test
    public void writesFor() throws IOException {
        Assert.assertThat(transform("for (var i = 0; i < array.length; ++i,++j) foo(array[i]);"), CoreMatchers.is("for(var i=0;i<array.length;++i,++j)foo(array[i]);"));
    }

    @Test
    public void writesEmptyFor() throws IOException {
        Assert.assertThat(transform("for (;;) foo();"), CoreMatchers.is("for(;;)foo();"));
    }

    @Test
    public void writesForIn() throws IOException {
        Assert.assertThat(transform("for (var property in window) alert(property);"), CoreMatchers.is("for(var property in window)alert(property);"));
    }

    @Test
    public void writesWhile() throws IOException {
        Assert.assertThat(transform("while (shouldProceed()) proceed();"), CoreMatchers.is("while(shouldProceed())proceed();"));
    }

    @Test
    public void writesDoWhile() throws IOException {
        Assert.assertThat(transform("do proceed(); while(shouldRepeat());"), CoreMatchers.is("do proceed();while(shouldRepeat());"));
    }

    @Test
    public void writesIfElse() throws IOException {
        Assert.assertThat(transform("if (test()) performTrue(); else performFalse();"), CoreMatchers.is("if(test())performTrue();else performFalse();"));
    }

    @Test
    public void writesIf() throws IOException {
        Assert.assertThat(transform("if (shouldPerform()) perform();"), CoreMatchers.is("if(shouldPerform())perform();"));
    }

    @Test
    public void writesSwitch() throws IOException {
        Assert.assertThat(transform(("switch (c) { " + (("case '.': case '?': matchAny(); break; " + "case '*': matchSequence(); break;") + "default: matchChar(c); break; } "))), CoreMatchers.is(("switch(c){case '.':case '?':matchAny();break;case '*':matchSequence();break;" + "default:matchChar(c);break;}")));
    }

    @Test
    public void writesLet() throws IOException {
        Assert.assertThat(transform("let x = 1; alert(x);"), CoreMatchers.is("let x=1;alert(x);"));
        Assert.assertThat(transform("let x = 1, y; alert(x,y);"), CoreMatchers.is("let x=1,y;alert(x,y);"));
    }

    @Test
    public void writesConst() throws IOException {
        Assert.assertThat(transform("const x = 1,y = 2; alert(x,y);"), CoreMatchers.is("const x=1,y=2;alert(x,y);"));
    }

    @Test
    public void writesElementGet() throws IOException {
        Assert.assertThat(transform("return array[i];"), CoreMatchers.is("return array[i];"));
        Assert.assertThat(transform("return array[i][j];"), CoreMatchers.is("return array[i][j];"));
        Assert.assertThat(transform("return (array[i])[j];"), CoreMatchers.is("return array[i][j];"));
        Assert.assertThat(transform("return (a + b)[i];"), CoreMatchers.is("return (a+b)[i];"));
        Assert.assertThat(transform("return a + b[i];"), CoreMatchers.is("return a+b[i];"));
    }

    @Test
    public void writesPropertyGet() throws IOException {
        Assert.assertThat(transform("return array.length;"), CoreMatchers.is("return array.length;"));
        Assert.assertThat(transform("return (array).length;"), CoreMatchers.is("return array.length;"));
        Assert.assertThat(transform("return (x + y).toString();"), CoreMatchers.is("return (x+y).toString();"));
    }

    @Test
    public void writesFunctionCall() throws IOException {
        Assert.assertThat(transform("return f(x);"), CoreMatchers.is("return f(x);"));
        Assert.assertThat(transform("return (f)(x);"), CoreMatchers.is("return f(x);"));
        Assert.assertThat(transform("return (f + g)(x);"), CoreMatchers.is("return (f+g)(x);"));
    }

    @Test
    public void writesConstructorCall() throws IOException {
        Assert.assertThat(transform("return new (f + g)(x);"), CoreMatchers.is("return new (f+g)(x);"));
        Assert.assertThat(transform("return new f + g(x);"), CoreMatchers.is("return new f()+g(x);"));
        Assert.assertThat(transform("return new f()(x);"), CoreMatchers.is("return new f()(x);"));
        Assert.assertThat(transform("return (new f())(x);"), CoreMatchers.is("return new f()(x);"));
        Assert.assertThat(transform("return new (f())(x);"), CoreMatchers.is("return new (f())(x);"));
        Assert.assertThat(transform("return new f[0](x);"), CoreMatchers.is("return new f[0](x);"));
        Assert.assertThat(transform("return (new f[0](x));"), CoreMatchers.is("return new f[0](x);"));
    }

    @Test
    public void writesConditionalExpr() throws IOException {
        Assert.assertThat(transform("return cond ? 1 : 0;"), CoreMatchers.is("return cond?1:0;"));
        Assert.assertThat(transform("return a < b ? -1 : a > b ? 1 : 0;"), CoreMatchers.is("return a<b? -1:a>b?1:0;"));
        Assert.assertThat(transform("return a < b ? -1 : (a > b ? 1 : 0);"), CoreMatchers.is("return a<b? -1:a>b?1:0;"));
        Assert.assertThat(transform("return (a < b ? x == y : x != y) ? 1 : 0;"), CoreMatchers.is("return (a<b?x==y:x!=y)?1:0;"));
        Assert.assertThat(transform("return a < b ? (x > y ? x : y) : z"), CoreMatchers.is("return a<b?(x>y?x:y):z;"));
    }

    @Test
    public void writesRegExp() throws IOException {
        Assert.assertThat(transform("return /[a-z]+/.match(text);"), CoreMatchers.is("return /[a-z]+/.match(text);"));
        Assert.assertThat(transform("return /[a-z]+/ig.match(text);"), CoreMatchers.is("return /[a-z]+/ig.match(text);"));
    }

    @Test
    public void writesArrayLiteral() throws IOException {
        Assert.assertThat(transform("return [];"), CoreMatchers.is("return [];"));
        Assert.assertThat(transform("return [a, b + c];"), CoreMatchers.is("return [a,b+c];"));
    }

    @Test
    public void writesObjectLiteral() throws IOException {
        Assert.assertThat(transform("return {};"), CoreMatchers.is("return {};"));
        Assert.assertThat(transform("return { foo : bar };"), CoreMatchers.is("return {foo:bar};"));
        Assert.assertThat(transform("return { foo : bar };"), CoreMatchers.is("return {foo:bar};"));
        Assert.assertThat(transform("return { _foo : bar, get foo() { return this._foo; } };"), CoreMatchers.is("return {_foo:bar,get foo(){return this._foo;}};"));
    }

    @Test
    public void writesFunction() throws IOException {
        Assert.assertThat(transform("return function f(x, y) { return x + y; };"), CoreMatchers.is("return function f(x,y){return x+y;};"));
    }

    @Test
    public void writesUnary() throws IOException {
        Assert.assertThat(transform("return -a;"), CoreMatchers.is("return  -a;"));
        Assert.assertThat(transform("return -(a + b);"), CoreMatchers.is("return  -(a+b);"));
        Assert.assertThat(transform("return -a + b;"), CoreMatchers.is("return  -a+b;"));
        Assert.assertThat(transform("return (-a) + b;"), CoreMatchers.is("return  -a+b;"));
        Assert.assertThat(transform("return (-f)(x);"), CoreMatchers.is("return ( -f)(x);"));
        Assert.assertThat(transform("return typeof a;"), CoreMatchers.is("return typeof a;"));
    }

    @Test
    public void writesPostfix() throws IOException {
        Assert.assertThat(transform("return a++;"), CoreMatchers.is("return a++;"));
    }

    @Test
    public void respectsPrecedence() throws IOException {
        Assert.assertThat(transform("return a + b + c;"), CoreMatchers.is("return a+b+c;"));
        Assert.assertThat(transform("return (a + b) + c;"), CoreMatchers.is("return a+b+c;"));
        Assert.assertThat(transform("return a + (b + c);"), CoreMatchers.is("return a+b+c;"));
        Assert.assertThat(transform("return a - b + c;"), CoreMatchers.is("return a -b+c;"));
        Assert.assertThat(transform("return (a - b) + c;"), CoreMatchers.is("return a -b+c;"));
        Assert.assertThat(transform("return a - (b + c);"), CoreMatchers.is("return a -(b+c);"));
    }

    @Test
    public void writesDelete() throws IOException {
        Assert.assertThat(transform("delete a.b;"), CoreMatchers.is("delete a.b;"));
    }
}

