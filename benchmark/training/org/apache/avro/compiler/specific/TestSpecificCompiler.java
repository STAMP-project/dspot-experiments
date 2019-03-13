/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.avro.compiler.specific;


import Type.INT;
import Type.STRING;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import org.apache.avro.Protocol;
import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.TestAnnotation;
import org.apache.avro.TestProtocolParsing;
import org.apache.avro.compiler.specific.SpecificCompiler.OutputFile;
import org.apache.avro.generic.GenericData.StringType;
import org.apache.avro.test.Kind;
import org.apache.avro.test.MD5;
import org.apache.avro.test.Simple;
import org.apache.avro.test.TestRecord;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;


public class TestSpecificCompiler {
    @Rule
    public TestName name = new TestName();

    @Rule
    public TemporaryFolder INPUT_DIR = new TemporaryFolder();

    @Rule
    public TemporaryFolder OUTPUT_DIR = new TemporaryFolder();

    static final String PROTOCOL = "" + ((((((((((((((("{ \"protocol\": \"default\",\n" + "  \"types\":\n") + "    [\n") + "      {\n") + "       \"name\": \"finally\",\n") + "       \"type\": \"error\",\n") + "       \"fields\": [{\"name\": \"catch\", \"type\": \"boolean\"}]\n") + "      }\n") + "    ],\n") + "  \"messages\": { \"goto\":\n") + "    { \"request\": [{\"name\": \"break\", \"type\": \"string\"}],\n") + "      \"response\": \"string\",\n") + "      \"errors\": [\"finally\"]\n") + "    }") + "   }\n") + "}\n");

    @Test
    public void testEsc() {
        Assert.assertEquals("\\\"", SpecificCompiler.javaEscape("\""));
    }

    @Test
    public void testMakePath() {
        SpecificCompiler compiler = new SpecificCompiler();
        Assert.assertEquals("foo/bar/Baz.java".replace("/", File.separator), compiler.makePath("Baz", "foo.bar"));
        Assert.assertEquals("baz.java", compiler.makePath("baz", ""));
    }

    @Test
    public void testPrimitiveSchemaGeneratesNothing() {
        Assert.assertEquals(0, compile().size());
    }

    @Test
    public void testSimpleEnumSchema() throws IOException {
        Collection<OutputFile> outputs = new SpecificCompiler(Schema.parse(org.apache.avro.TestSchema.BASIC_ENUM_SCHEMA)).compile();
        Assert.assertEquals(1, outputs.size());
        OutputFile o = outputs.iterator().next();
        Assert.assertEquals(o.path, "Test.java");
        Assert.assertTrue(o.contents.contains("public enum Test"));
        TestSpecificCompiler.assertCompilesWithJavaCompiler(new File(INPUT_DIR.getRoot(), name.getMethodName()), outputs);
    }

    @Test
    public void testMangleIfReserved() {
        Assert.assertEquals("foo", SpecificCompiler.mangle("foo"));
        Assert.assertEquals("goto$", SpecificCompiler.mangle("goto"));
    }

    @Test
    public void testManglingForProtocols() throws IOException {
        Collection<OutputFile> outputs = new SpecificCompiler(Protocol.parse(TestSpecificCompiler.PROTOCOL)).compile();
        Iterator<OutputFile> i = outputs.iterator();
        String errType = i.next().contents;
        String protocol = i.next().contents;
        Assert.assertTrue(errType.contains("public class finally$ extends org.apache.avro.specific.SpecificExceptionBase"));
        Assert.assertTrue(errType.contains("public boolean catch$;"));
        Assert.assertTrue(protocol.contains("java.lang.CharSequence goto$(java.lang.CharSequence break$)"));
        Assert.assertTrue(protocol.contains("public interface default$"));
        Assert.assertTrue(protocol.contains("throws org.apache.avro.AvroRemoteException, finally$"));
        TestSpecificCompiler.assertCompilesWithJavaCompiler(new File(INPUT_DIR.getRoot(), name.getMethodName()), outputs);
    }

    private static String SCHEMA = "{ \"name\": \"volatile\", \"type\": \"record\", " + ((((("  \"fields\": [ {\"name\": \"package\", \"type\": \"string\" }," + "                {\"name\": \"data\", \"type\": \"int\" },") + "                {\"name\": \"value\", \"type\": \"int\" },") + "                {\"name\": \"defaultValue\", \"type\": \"int\" },") + "                {\"name\": \"other\", \"type\": \"int\" },") + "                {\"name\": \"short\", \"type\": \"volatile\" } ] }");

    @Test
    public void testManglingForRecords() throws IOException {
        Collection<OutputFile> outputs = new SpecificCompiler(Schema.parse(TestSpecificCompiler.SCHEMA)).compile();
        Assert.assertEquals(1, outputs.size());
        String contents = outputs.iterator().next().contents;
        Assert.assertTrue(contents.contains("public java.lang.CharSequence package$;"));
        Assert.assertTrue(contents.contains("class volatile$ extends"));
        Assert.assertTrue(contents.contains("volatile$ short$;"));
        TestSpecificCompiler.assertCompilesWithJavaCompiler(new File(INPUT_DIR.getRoot(), name.getMethodName()), outputs);
    }

    @Test
    public void testManglingForEnums() throws IOException {
        String enumSchema = "" + ("{ \"name\": \"instanceof\", \"type\": \"enum\"," + "  \"symbols\": [\"new\", \"super\", \"switch\"] }");
        Collection<OutputFile> outputs = new SpecificCompiler(Schema.parse(enumSchema)).compile();
        Assert.assertEquals(1, outputs.size());
        String contents = outputs.iterator().next().contents;
        Assert.assertTrue(contents.contains("new$"));
        TestSpecificCompiler.assertCompilesWithJavaCompiler(new File(INPUT_DIR.getRoot(), name.getMethodName()), outputs);
    }

    @Test
    public void testSchemaSplit() throws IOException {
        SpecificCompiler compiler = new SpecificCompiler(Schema.parse(TestSpecificCompiler.SCHEMA));
        compiler.maxStringChars = 10;
        Collection<OutputFile> files = compiler.compile();
        TestSpecificCompiler.assertCompilesWithJavaCompiler(new File(INPUT_DIR.getRoot(), name.getMethodName()), files);
    }

    @Test
    public void testProtocolSplit() throws IOException {
        SpecificCompiler compiler = new SpecificCompiler(Protocol.parse(TestSpecificCompiler.PROTOCOL));
        compiler.maxStringChars = 10;
        Collection<OutputFile> files = compiler.compile();
        TestSpecificCompiler.assertCompilesWithJavaCompiler(new File(INPUT_DIR.getRoot(), name.getMethodName()), files);
    }

    @Test
    public void testSchemaWithDocs() {
        Collection<OutputFile> outputs = new SpecificCompiler(Schema.parse(org.apache.avro.TestSchema.SCHEMA_WITH_DOC_TAGS)).compile();
        Assert.assertEquals(3, outputs.size());
        int count = 0;
        for (OutputFile o : outputs) {
            if (o.path.endsWith("outer_record.java")) {
                count++;
                Assert.assertTrue(o.contents.contains("/** This is not a world record. */"));
                Assert.assertTrue(o.contents.contains("/** Inner Fixed */"));
                Assert.assertTrue(o.contents.contains("/** Inner Enum */"));
                Assert.assertTrue(o.contents.contains("/** Inner String */"));
            }
            if (o.path.endsWith("very_inner_fixed.java")) {
                count++;
                Assert.assertTrue(o.contents.contains("/** Very Inner Fixed */"));
                Assert.assertTrue(o.contents.contains("@org.apache.avro.specific.FixedSize(1)"));
            }
            if (o.path.endsWith("very_inner_enum.java")) {
                count++;
                Assert.assertTrue(o.contents.contains("/** Very Inner Enum */"));
            }
        }
        Assert.assertEquals(3, count);
    }

    @Test
    public void testProtocolWithDocs() throws IOException {
        Protocol protocol = TestProtocolParsing.getSimpleProtocol();
        Collection<OutputFile> out = new SpecificCompiler(protocol).compile();
        Assert.assertEquals(6, out.size());
        int count = 0;
        for (OutputFile o : out) {
            if (o.path.endsWith("Simple.java")) {
                count++;
                Assert.assertTrue(o.contents.contains("/** Protocol used for testing. */"));
                Assert.assertTrue(o.contents.contains("* Send a greeting"));
            }
        }
        Assert.assertEquals("Missed generated protocol!", 1, count);
    }

    @Test
    public void testNeedCompile() throws IOException, InterruptedException {
        String schema = "" + (("{ \"name\": \"Foo\", \"type\": \"record\", " + "  \"fields\": [ {\"name\": \"package\", \"type\": \"string\" },") + "                {\"name\": \"short\", \"type\": \"Foo\" } ] }");
        File inputFile = new File(INPUT_DIR.getRoot().getPath(), "input.avsc");
        try (FileWriter fw = new FileWriter(inputFile)) {
            fw.write(schema);
        }
        File outputDir = OUTPUT_DIR.getRoot();
        File outputFile = new File(outputDir, "Foo.java");
        outputFile.delete();
        Assert.assertTrue((!(outputFile.exists())));
        outputDir.delete();
        Assert.assertTrue((!(outputDir.exists())));
        SpecificCompiler.compileSchema(inputFile, outputDir);
        Assert.assertTrue(outputDir.exists());
        Assert.assertTrue(outputFile.exists());
        long lastModified = outputFile.lastModified();
        Thread.sleep(1000);// granularity of JVM doesn't seem to go below 1 sec

        SpecificCompiler.compileSchema(inputFile, outputDir);
        Assert.assertEquals(lastModified, outputFile.lastModified());
        try (FileWriter fw = new FileWriter(inputFile)) {
            fw.write(schema);
        }
        SpecificCompiler.compileSchema(inputFile, outputDir);
        Assert.assertTrue((lastModified != (outputFile.lastModified())));
    }

    @Test
    public void generateGetMethod() {
        Field height = new Field("height", Schema.create(INT), null, null);
        Field Height = new Field("Height", Schema.create(INT), null, null);
        Field height_and_width = new Field("height_and_width", Schema.create(STRING), null, null);
        Field message = new Field("message", Schema.create(STRING), null, null);
        Field Message = new Field("Message", Schema.create(STRING), null, null);
        Field cause = new Field("cause", Schema.create(STRING), null, null);
        Field clasz = new Field("class", Schema.create(STRING), null, null);
        Field schema = new Field("schema", Schema.create(STRING), null, null);
        Field Schema$ = new Field("Schema", Schema.create(STRING), null, null);
        Assert.assertEquals("getHeight", SpecificCompiler.generateGetMethod(createRecord("test", false, height), height));
        Assert.assertEquals("getHeightAndWidth", SpecificCompiler.generateGetMethod(createRecord("test", false, height_and_width), height_and_width));
        Assert.assertEquals("getMessage", SpecificCompiler.generateGetMethod(createRecord("test", false, message), message));
        message = new Field("message", Schema.create(STRING), null, null);
        Assert.assertEquals("getMessage$", SpecificCompiler.generateGetMethod(createRecord("test", true, message), message));
        Assert.assertEquals("getCause", SpecificCompiler.generateGetMethod(createRecord("test", false, cause), cause));
        cause = new Field("cause", Schema.create(STRING), null, null);
        Assert.assertEquals("getCause$", SpecificCompiler.generateGetMethod(createRecord("test", true, cause), cause));
        Assert.assertEquals("getClass$", SpecificCompiler.generateGetMethod(createRecord("test", false, clasz), clasz));
        clasz = new Field("class", Schema.create(STRING), null, null);
        Assert.assertEquals("getClass$", SpecificCompiler.generateGetMethod(createRecord("test", true, clasz), clasz));
        Assert.assertEquals("getSchema$", SpecificCompiler.generateGetMethod(createRecord("test", false, schema), schema));
        schema = new Field("schema", Schema.create(STRING), null, null);
        Assert.assertEquals("getSchema$", SpecificCompiler.generateGetMethod(createRecord("test", true, schema), schema));
        height = new Field("height", Schema.create(INT), null, null);
        Height = new Field("Height", Schema.create(INT), null, null);
        Assert.assertEquals("getHeight", SpecificCompiler.generateGetMethod(createRecord("test", false, Height), Height));
        height = new Field("height", Schema.create(INT), null, null);
        Height = new Field("Height", Schema.create(INT), null, null);
        Assert.assertEquals("getHeight$0", SpecificCompiler.generateGetMethod(createRecord("test", false, height, Height), height));
        height = new Field("height", Schema.create(INT), null, null);
        Height = new Field("Height", Schema.create(INT), null, null);
        Assert.assertEquals("getHeight$1", SpecificCompiler.generateGetMethod(createRecord("test", false, height, Height), Height));
        message = new Field("message", Schema.create(STRING), null, null);
        Message = new Field("Message", Schema.create(STRING), null, null);
        Assert.assertEquals("getMessage$", SpecificCompiler.generateGetMethod(createRecord("test", true, Message), Message));
        message = new Field("message", Schema.create(STRING), null, null);
        Message = new Field("Message", Schema.create(STRING), null, null);
        Assert.assertEquals("getMessage$0", SpecificCompiler.generateGetMethod(createRecord("test", true, message, Message), message));
        message = new Field("message", Schema.create(STRING), null, null);
        Message = new Field("Message", Schema.create(STRING), null, null);
        Assert.assertEquals("getMessage$1", SpecificCompiler.generateGetMethod(createRecord("test", true, message, Message), Message));
        schema = new Field("schema", Schema.create(STRING), null, null);
        Schema$ = new Field("Schema", Schema.create(STRING), null, null);
        Assert.assertEquals("getSchema$", SpecificCompiler.generateGetMethod(createRecord("test", false, Schema$), Schema$));
        schema = new Field("schema", Schema.create(STRING), null, null);
        Schema$ = new Field("Schema", Schema.create(STRING), null, null);
        Assert.assertEquals("getSchema$0", SpecificCompiler.generateGetMethod(createRecord("test", false, schema, Schema$), schema));
        schema = new Field("schema", Schema.create(STRING), null, null);
        Schema$ = new Field("Schema", Schema.create(STRING), null, null);
        Assert.assertEquals("getSchema$1", SpecificCompiler.generateGetMethod(createRecord("test", false, schema, Schema$), Schema$));
    }

    @Test
    public void generateSetMethod() {
        Field height = new Field("height", Schema.create(INT), null, null);
        Field Height = new Field("Height", Schema.create(INT), null, null);
        Field height_and_width = new Field("height_and_width", Schema.create(STRING), null, null);
        Field message = new Field("message", Schema.create(STRING), null, null);
        Field Message = new Field("Message", Schema.create(STRING), null, null);
        Field cause = new Field("cause", Schema.create(STRING), null, null);
        Field clasz = new Field("class", Schema.create(STRING), null, null);
        Field schema = new Field("schema", Schema.create(STRING), null, null);
        Field Schema$ = new Field("Schema", Schema.create(STRING), null, null);
        Assert.assertEquals("setHeight", SpecificCompiler.generateSetMethod(createRecord("test", false, height), height));
        Assert.assertEquals("setHeightAndWidth", SpecificCompiler.generateSetMethod(createRecord("test", false, height_and_width), height_and_width));
        Assert.assertEquals("setMessage", SpecificCompiler.generateSetMethod(createRecord("test", false, message), message));
        message = new Field("message", Schema.create(STRING), null, null);
        Assert.assertEquals("setMessage$", SpecificCompiler.generateSetMethod(createRecord("test", true, message), message));
        Assert.assertEquals("setCause", SpecificCompiler.generateSetMethod(createRecord("test", false, cause), cause));
        cause = new Field("cause", Schema.create(STRING), null, null);
        Assert.assertEquals("setCause$", SpecificCompiler.generateSetMethod(createRecord("test", true, cause), cause));
        Assert.assertEquals("setClass$", SpecificCompiler.generateSetMethod(createRecord("test", false, clasz), clasz));
        clasz = new Field("class", Schema.create(STRING), null, null);
        Assert.assertEquals("setClass$", SpecificCompiler.generateSetMethod(createRecord("test", true, clasz), clasz));
        Assert.assertEquals("setSchema$", SpecificCompiler.generateSetMethod(createRecord("test", false, schema), schema));
        schema = new Field("schema", Schema.create(STRING), null, null);
        Assert.assertEquals("setSchema$", SpecificCompiler.generateSetMethod(createRecord("test", true, schema), schema));
        height = new Field("height", Schema.create(INT), null, null);
        Height = new Field("Height", Schema.create(INT), null, null);
        Assert.assertEquals("setHeight", SpecificCompiler.generateSetMethod(createRecord("test", false, Height), Height));
        height = new Field("height", Schema.create(INT), null, null);
        Height = new Field("Height", Schema.create(INT), null, null);
        Assert.assertEquals("setHeight$0", SpecificCompiler.generateSetMethod(createRecord("test", false, height, Height), height));
        height = new Field("height", Schema.create(INT), null, null);
        Height = new Field("Height", Schema.create(INT), null, null);
        Assert.assertEquals("setHeight$1", SpecificCompiler.generateSetMethod(createRecord("test", false, height, Height), Height));
        message = new Field("message", Schema.create(STRING), null, null);
        Message = new Field("Message", Schema.create(STRING), null, null);
        Assert.assertEquals("setMessage$", SpecificCompiler.generateSetMethod(createRecord("test", true, Message), Message));
        message = new Field("message", Schema.create(STRING), null, null);
        Message = new Field("Message", Schema.create(STRING), null, null);
        Assert.assertEquals("setMessage$0", SpecificCompiler.generateSetMethod(createRecord("test", true, message, Message), message));
        message = new Field("message", Schema.create(STRING), null, null);
        Message = new Field("Message", Schema.create(STRING), null, null);
        Assert.assertEquals("setMessage$1", SpecificCompiler.generateSetMethod(createRecord("test", true, message, Message), Message));
        schema = new Field("schema", Schema.create(STRING), null, null);
        Schema$ = new Field("Schema", Schema.create(STRING), null, null);
        Assert.assertEquals("setSchema$", SpecificCompiler.generateSetMethod(createRecord("test", false, Schema$), Schema$));
        schema = new Field("schema", Schema.create(STRING), null, null);
        Schema$ = new Field("Schema", Schema.create(STRING), null, null);
        Assert.assertEquals("setSchema$0", SpecificCompiler.generateSetMethod(createRecord("test", false, schema, Schema$), schema));
        schema = new Field("schema", Schema.create(STRING), null, null);
        Schema$ = new Field("Schema", Schema.create(STRING), null, null);
        Assert.assertEquals("setSchema$1", SpecificCompiler.generateSetMethod(createRecord("test", false, schema, Schema$), Schema$));
    }

    @Test
    public void generateHasMethod() {
        Field height = new Field("height", Schema.create(INT), null, null);
        Field Height = new Field("Height", Schema.create(INT), null, null);
        Field height_and_width = new Field("height_and_width", Schema.create(STRING), null, null);
        Field message = new Field("message", Schema.create(STRING), null, null);
        Field Message = new Field("Message", Schema.create(STRING), null, null);
        Field cause = new Field("cause", Schema.create(STRING), null, null);
        Field clasz = new Field("class", Schema.create(STRING), null, null);
        Field schema = new Field("schema", Schema.create(STRING), null, null);
        Field Schema$ = new Field("Schema", Schema.create(STRING), null, null);
        Assert.assertEquals("hasHeight", SpecificCompiler.generateHasMethod(createRecord("test", false, height), height));
        Assert.assertEquals("hasHeightAndWidth", SpecificCompiler.generateHasMethod(createRecord("test", false, height_and_width), height_and_width));
        Assert.assertEquals("hasMessage", SpecificCompiler.generateHasMethod(createRecord("test", false, message), message));
        message = new Field("message", Schema.create(STRING), null, null);
        Assert.assertEquals("hasMessage$", SpecificCompiler.generateHasMethod(createRecord("test", true, message), message));
        Assert.assertEquals("hasCause", SpecificCompiler.generateHasMethod(createRecord("test", false, cause), cause));
        cause = new Field("cause", Schema.create(STRING), null, null);
        Assert.assertEquals("hasCause$", SpecificCompiler.generateHasMethod(createRecord("test", true, cause), cause));
        Assert.assertEquals("hasClass$", SpecificCompiler.generateHasMethod(createRecord("test", false, clasz), clasz));
        clasz = new Field("class", Schema.create(STRING), null, null);
        Assert.assertEquals("hasClass$", SpecificCompiler.generateHasMethod(createRecord("test", true, clasz), clasz));
        Assert.assertEquals("hasSchema$", SpecificCompiler.generateHasMethod(createRecord("test", false, schema), schema));
        schema = new Field("schema", Schema.create(STRING), null, null);
        Assert.assertEquals("hasSchema$", SpecificCompiler.generateHasMethod(createRecord("test", true, schema), schema));
        height = new Field("height", Schema.create(INT), null, null);
        Height = new Field("Height", Schema.create(INT), null, null);
        Assert.assertEquals("hasHeight", SpecificCompiler.generateHasMethod(createRecord("test", false, Height), Height));
        height = new Field("height", Schema.create(INT), null, null);
        Height = new Field("Height", Schema.create(INT), null, null);
        Assert.assertEquals("hasHeight$0", SpecificCompiler.generateHasMethod(createRecord("test", false, height, Height), height));
        height = new Field("height", Schema.create(INT), null, null);
        Height = new Field("Height", Schema.create(INT), null, null);
        Assert.assertEquals("hasHeight$1", SpecificCompiler.generateHasMethod(createRecord("test", false, height, Height), Height));
        message = new Field("message", Schema.create(STRING), null, null);
        Message = new Field("Message", Schema.create(STRING), null, null);
        Assert.assertEquals("hasMessage$", SpecificCompiler.generateHasMethod(createRecord("test", true, Message), Message));
        message = new Field("message", Schema.create(STRING), null, null);
        Message = new Field("Message", Schema.create(STRING), null, null);
        Assert.assertEquals("hasMessage$0", SpecificCompiler.generateHasMethod(createRecord("test", true, message, Message), message));
        message = new Field("message", Schema.create(STRING), null, null);
        Message = new Field("Message", Schema.create(STRING), null, null);
        Assert.assertEquals("hasMessage$1", SpecificCompiler.generateHasMethod(createRecord("test", true, message, Message), Message));
        schema = new Field("schema", Schema.create(STRING), null, null);
        Schema$ = new Field("Schema", Schema.create(STRING), null, null);
        Assert.assertEquals("hasSchema$", SpecificCompiler.generateHasMethod(createRecord("test", false, Schema$), Schema$));
        schema = new Field("schema", Schema.create(STRING), null, null);
        Schema$ = new Field("Schema", Schema.create(STRING), null, null);
        Assert.assertEquals("hasSchema$0", SpecificCompiler.generateHasMethod(createRecord("test", false, schema, Schema$), schema));
        schema = new Field("schema", Schema.create(STRING), null, null);
        Schema$ = new Field("Schema", Schema.create(STRING), null, null);
        Assert.assertEquals("hasSchema$1", SpecificCompiler.generateHasMethod(createRecord("test", false, schema, Schema$), Schema$));
    }

    @Test
    public void generateClearMethod() {
        Field height = new Field("height", Schema.create(INT), null, null);
        Field Height = new Field("Height", Schema.create(INT), null, null);
        Field height_and_width = new Field("height_and_width", Schema.create(STRING), null, null);
        Field message = new Field("message", Schema.create(STRING), null, null);
        Field Message = new Field("Message", Schema.create(STRING), null, null);
        Field cause = new Field("cause", Schema.create(STRING), null, null);
        Field clasz = new Field("class", Schema.create(STRING), null, null);
        Field schema = new Field("schema", Schema.create(STRING), null, null);
        Field Schema$ = new Field("Schema", Schema.create(STRING), null, null);
        Assert.assertEquals("clearHeight", SpecificCompiler.generateClearMethod(createRecord("test", false, height), height));
        Assert.assertEquals("clearHeightAndWidth", SpecificCompiler.generateClearMethod(createRecord("test", false, height_and_width), height_and_width));
        Assert.assertEquals("clearMessage", SpecificCompiler.generateClearMethod(createRecord("test", false, message), message));
        message = new Field("message", Schema.create(STRING), null, null);
        Assert.assertEquals("clearMessage$", SpecificCompiler.generateClearMethod(createRecord("test", true, message), message));
        Assert.assertEquals("clearCause", SpecificCompiler.generateClearMethod(createRecord("test", false, cause), cause));
        cause = new Field("cause", Schema.create(STRING), null, null);
        Assert.assertEquals("clearCause$", SpecificCompiler.generateClearMethod(createRecord("test", true, cause), cause));
        Assert.assertEquals("clearClass$", SpecificCompiler.generateClearMethod(createRecord("test", false, clasz), clasz));
        clasz = new Field("class", Schema.create(STRING), null, null);
        Assert.assertEquals("clearClass$", SpecificCompiler.generateClearMethod(createRecord("test", true, clasz), clasz));
        Assert.assertEquals("clearSchema$", SpecificCompiler.generateClearMethod(createRecord("test", false, schema), schema));
        schema = new Field("schema", Schema.create(STRING), null, null);
        Assert.assertEquals("clearSchema$", SpecificCompiler.generateClearMethod(createRecord("test", true, schema), schema));
        height = new Field("height", Schema.create(INT), null, null);
        Height = new Field("Height", Schema.create(INT), null, null);
        Assert.assertEquals("clearHeight", SpecificCompiler.generateClearMethod(createRecord("test", false, Height), Height));
        height = new Field("height", Schema.create(INT), null, null);
        Height = new Field("Height", Schema.create(INT), null, null);
        Assert.assertEquals("clearHeight$0", SpecificCompiler.generateClearMethod(createRecord("test", false, height, Height), height));
        height = new Field("height", Schema.create(INT), null, null);
        Height = new Field("Height", Schema.create(INT), null, null);
        Assert.assertEquals("clearHeight$1", SpecificCompiler.generateClearMethod(createRecord("test", false, height, Height), Height));
        message = new Field("message", Schema.create(STRING), null, null);
        Message = new Field("Message", Schema.create(STRING), null, null);
        Assert.assertEquals("clearMessage$", SpecificCompiler.generateClearMethod(createRecord("test", true, Message), Message));
        message = new Field("message", Schema.create(STRING), null, null);
        Message = new Field("Message", Schema.create(STRING), null, null);
        Assert.assertEquals("clearMessage$0", SpecificCompiler.generateClearMethod(createRecord("test", true, message, Message), message));
        message = new Field("message", Schema.create(STRING), null, null);
        Message = new Field("Message", Schema.create(STRING), null, null);
        Assert.assertEquals("clearMessage$1", SpecificCompiler.generateClearMethod(createRecord("test", true, message, Message), Message));
        schema = new Field("schema", Schema.create(STRING), null, null);
        Schema$ = new Field("Schema", Schema.create(STRING), null, null);
        Assert.assertEquals("clearSchema$", SpecificCompiler.generateClearMethod(createRecord("test", false, Schema$), Schema$));
        schema = new Field("schema", Schema.create(STRING), null, null);
        Schema$ = new Field("Schema", Schema.create(STRING), null, null);
        Assert.assertEquals("clearSchema$0", SpecificCompiler.generateClearMethod(createRecord("test", false, schema, Schema$), schema));
        schema = new Field("schema", Schema.create(STRING), null, null);
        Schema$ = new Field("Schema", Schema.create(STRING), null, null);
        Assert.assertEquals("clearSchema$1", SpecificCompiler.generateClearMethod(createRecord("test", false, schema, Schema$), Schema$));
    }

    @Test
    public void testAnnotations() throws Exception {
        // an interface generated for protocol
        Assert.assertNotNull(Simple.class.getAnnotation(TestAnnotation.class));
        // a class generated for a record
        Assert.assertNotNull(TestRecord.class.getAnnotation(TestAnnotation.class));
        // a class generated for a fixed
        Assert.assertNotNull(MD5.class.getAnnotation(TestAnnotation.class));
        // a class generated for an enum
        Assert.assertNotNull(Kind.class.getAnnotation(TestAnnotation.class));
        // a field
        Assert.assertNotNull(TestRecord.class.getField("name").getAnnotation(TestAnnotation.class));
        // a method
        Assert.assertNotNull(Simple.class.getMethod("ack").getAnnotation(TestAnnotation.class));
    }

    @Test
    public void testAliases() throws IOException {
        Schema s = Schema.parse(("{\"name\":\"X\",\"type\":\"record\",\"aliases\":[\"Y\"],\"fields\":[" + "{\"name\":\"f\",\"type\":\"int\",\"aliases\":[\"g\"]}]}"));
        SpecificCompiler compiler = new SpecificCompiler(s);
        compiler.setStringType(StringType.valueOf("String"));
        Collection<OutputFile> outputs = compiler.compile();
        Assert.assertEquals(1, outputs.size());
        OutputFile o = outputs.iterator().next();
        Assert.assertEquals(o.path, "X.java");
        Assert.assertTrue(o.contents.contains("[\\\"Y\\\"]"));
        Assert.assertTrue(o.contents.contains("[\\\"g\\\"]"));
    }
}

