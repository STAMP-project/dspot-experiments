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


import Schema.Type.BOOLEAN;
import Schema.Type.BYTES;
import Schema.Type.DOUBLE;
import Schema.Type.FLOAT;
import Schema.Type.INT;
import Schema.Type.LONG;
import Schema.Type.NULL;
import Schema.Type.STRING;
import SpecificCompiler.FieldVisibility.PRIVATE;
import SpecificCompiler.FieldVisibility.PUBLIC;
import SpecificCompiler.MAX_FIELD_PARAMETER_UNIT_COUNT;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import org.apache.avro.LogicalTypes;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static SpecificCompiler.MAX_FIELD_PARAMETER_UNIT_COUNT;


@RunWith(JUnit4.class)
public class TestSpecificCompiler {
    private static final Logger LOG = LoggerFactory.getLogger(TestSpecificCompiler.class);

    @Rule
    public TemporaryFolder OUTPUT_DIR = new TemporaryFolder();

    @Rule
    public TestName name = new TestName();

    private File outputFile;

    private File src = new File("src/test/resources/simple_record.avsc");

    @Test
    public void testCanReadTemplateFilesOnTheFilesystem() throws IOException {
        SpecificCompiler compiler = createCompiler();
        compiler.compileToDestination(this.src, OUTPUT_DIR.getRoot());
        Assert.assertTrue(new File(OUTPUT_DIR.getRoot(), "SimpleRecord.java").exists());
    }

    @Test
    public void testPublicFieldVisibility() throws IOException {
        SpecificCompiler compiler = createCompiler();
        compiler.setFieldVisibility(PUBLIC);
        Assert.assertFalse(compiler.deprecatedFields());
        Assert.assertTrue(compiler.publicFields());
        Assert.assertFalse(compiler.privateFields());
        compiler.compileToDestination(this.src, this.OUTPUT_DIR.getRoot());
        Assert.assertTrue(this.outputFile.exists());
        try (BufferedReader reader = new BufferedReader(new FileReader(this.outputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // No line, once trimmed, should start with a deprecated field declaration
                // nor a private field declaration.  Since the nested builder uses private
                // fields, we cannot do the second check.
                line = line.trim();
                Assert.assertFalse(("Line started with a deprecated field declaration: " + line), line.startsWith("@Deprecated public int value"));
            } 
        }
    }

    @Test
    public void testCreateAllArgsConstructor() throws Exception {
        SpecificCompiler compiler = createCompiler();
        compiler.compileToDestination(this.src, this.OUTPUT_DIR.getRoot());
        Assert.assertTrue(this.outputFile.exists());
        boolean foundAllArgsConstructor = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(this.outputFile))) {
            String line;
            while ((!foundAllArgsConstructor) && ((line = reader.readLine()) != null)) {
                foundAllArgsConstructor = line.contains("All-args constructor");
            } 
        }
        Assert.assertTrue(foundAllArgsConstructor);
    }

    @Test
    public void testMaxValidParameterCounts() throws Exception {
        Schema validSchema1 = TestSpecificCompiler.createSampleRecordSchema(MAX_FIELD_PARAMETER_UNIT_COUNT, 0);
        TestSpecificCompiler.assertCompilesWithJavaCompiler(new File(OUTPUT_DIR.getRoot(), ((name.getMethodName()) + "1")), compile());
        Schema validSchema2 = TestSpecificCompiler.createSampleRecordSchema(((MAX_FIELD_PARAMETER_UNIT_COUNT) - 2), 1);
        TestSpecificCompiler.assertCompilesWithJavaCompiler(new File(OUTPUT_DIR.getRoot(), ((name.getMethodName()) + "2")), compile());
    }

    @Test
    public void testInvalidParameterCounts() throws Exception {
        Schema invalidSchema1 = TestSpecificCompiler.createSampleRecordSchema(((MAX_FIELD_PARAMETER_UNIT_COUNT) + 1), 0);
        SpecificCompiler compiler = new SpecificCompiler(invalidSchema1);
        TestSpecificCompiler.assertCompilesWithJavaCompiler(new File(OUTPUT_DIR.getRoot(), ((name.getMethodName()) + "1")), compiler.compile());
        Schema invalidSchema2 = TestSpecificCompiler.createSampleRecordSchema(MAX_FIELD_PARAMETER_UNIT_COUNT, 10);
        compiler = new SpecificCompiler(invalidSchema2);
        TestSpecificCompiler.assertCompilesWithJavaCompiler(new File(OUTPUT_DIR.getRoot(), ((name.getMethodName()) + "2")), compiler.compile());
    }

    @Test
    public void testMaxParameterCounts() throws Exception {
        Schema validSchema1 = TestSpecificCompiler.createSampleRecordSchema(MAX_FIELD_PARAMETER_UNIT_COUNT, 0);
        Assert.assertTrue(((compile().size()) > 0));
        Schema validSchema2 = TestSpecificCompiler.createSampleRecordSchema(((MAX_FIELD_PARAMETER_UNIT_COUNT) - 2), 1);
        Assert.assertTrue(((compile().size()) > 0));
        Schema validSchema3 = TestSpecificCompiler.createSampleRecordSchema(((MAX_FIELD_PARAMETER_UNIT_COUNT) - 1), 1);
        Assert.assertTrue(((compile().size()) > 0));
        Schema validSchema4 = TestSpecificCompiler.createSampleRecordSchema(((MAX_FIELD_PARAMETER_UNIT_COUNT) + 1), 0);
        Assert.assertTrue(((compile().size()) > 0));
    }

    @Test(expected = RuntimeException.class)
    public void testCalcAllArgConstructorParameterUnitsFailure() {
        Schema nonRecordSchema = SchemaBuilder.array().items().booleanType();
        new SpecificCompiler().calcAllArgConstructorParameterUnits(nonRecordSchema);
    }

    @Test
    public void testPublicDeprecatedFieldVisibility() throws IOException {
        SpecificCompiler compiler = createCompiler();
        Assert.assertTrue(compiler.deprecatedFields());
        Assert.assertTrue(compiler.publicFields());
        Assert.assertFalse(compiler.privateFields());
        compiler.compileToDestination(this.src, this.OUTPUT_DIR.getRoot());
        Assert.assertTrue(this.outputFile.exists());
        try (BufferedReader reader = new BufferedReader(new FileReader(this.outputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // No line, once trimmed, should start with a public field declaration
                line = line.trim();
                Assert.assertFalse(("Line started with a public field declaration: " + line), line.startsWith("public int value"));
            } 
        }
    }

    @Test
    public void testPrivateFieldVisibility() throws IOException {
        SpecificCompiler compiler = createCompiler();
        compiler.setFieldVisibility(PRIVATE);
        Assert.assertFalse(compiler.deprecatedFields());
        Assert.assertFalse(compiler.publicFields());
        Assert.assertTrue(compiler.privateFields());
        compiler.compileToDestination(this.src, this.OUTPUT_DIR.getRoot());
        Assert.assertTrue(this.outputFile.exists());
        try (BufferedReader reader = new BufferedReader(new FileReader(this.outputFile))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                // No line, once trimmed, should start with a public field declaration
                // or with a deprecated public field declaration
                line = line.trim();
                Assert.assertFalse(("Line started with a public field declaration: " + line), line.startsWith("public int value"));
                Assert.assertFalse(("Line started with a deprecated field declaration: " + line), line.startsWith("@Deprecated public int value"));
            } 
        }
    }

    @Test
    public void testSettersCreatedByDefault() throws IOException {
        SpecificCompiler compiler = createCompiler();
        Assert.assertTrue(compiler.isCreateSetters());
        compiler.compileToDestination(this.src, this.OUTPUT_DIR.getRoot());
        Assert.assertTrue(this.outputFile.exists());
        int foundSetters = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(this.outputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // We should find the setter in the main class
                line = line.trim();
                if (line.startsWith("public void setValue(")) {
                    foundSetters++;
                }
            } 
        }
        Assert.assertEquals("Found the wrong number of setters", 1, foundSetters);
    }

    @Test
    public void testSettersNotCreatedWhenOptionTurnedOff() throws IOException {
        SpecificCompiler compiler = createCompiler();
        compiler.setCreateSetters(false);
        Assert.assertFalse(compiler.isCreateSetters());
        compiler.compileToDestination(this.src, this.OUTPUT_DIR.getRoot());
        Assert.assertTrue(this.outputFile.exists());
        try (BufferedReader reader = new BufferedReader(new FileReader(this.outputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // No setter should be found
                line = line.trim();
                Assert.assertFalse(("No line should include the setter: " + line), line.startsWith("public void setValue("));
            } 
        }
    }

    @Test
    public void testSettingOutputCharacterEncoding() throws Exception {
        SpecificCompiler compiler = createCompiler();
        // Generated file in default encoding
        compiler.compileToDestination(this.src, this.OUTPUT_DIR.getRoot());
        byte[] fileInDefaultEncoding = new byte[((int) (this.outputFile.length()))];
        FileInputStream is = new FileInputStream(this.outputFile);
        is.read(fileInDefaultEncoding);
        is.close();// close input stream otherwise delete might fail

        if (!(this.outputFile.delete())) {
            throw new IllegalStateException(("unable to delete " + (this.outputFile)));// delete otherwise compiler might not overwrite because src timestamp hasn't changed.

        }
        // Generate file in another encoding (make sure it has different number of bytes per character)
        String differentEncoding = (Charset.defaultCharset().equals(Charset.forName("UTF-16"))) ? "UTF-32" : "UTF-16";
        compiler.setOutputCharacterEncoding(differentEncoding);
        compiler.compileToDestination(this.src, this.OUTPUT_DIR.getRoot());
        byte[] fileInDifferentEncoding = new byte[((int) (this.outputFile.length()))];
        is = new FileInputStream(this.outputFile);
        is.read(fileInDifferentEncoding);
        is.close();
        // Compare as bytes
        Assert.assertThat("Generated file should contain different bytes after setting non-default encoding", fileInDefaultEncoding, CoreMatchers.not(CoreMatchers.equalTo(fileInDifferentEncoding)));
        // Compare as strings
        Assert.assertThat("Generated files should contain the same characters in the proper encodings", new String(fileInDefaultEncoding), CoreMatchers.equalTo(new String(fileInDifferentEncoding, differentEncoding)));
    }

    @Test
    public void testJavaTypeWithDecimalLogicalTypeEnabled() throws Exception {
        SpecificCompiler compiler = createCompiler();
        compiler.setEnableDecimalLogicalType(true);
        Schema dateSchema = LogicalTypes.date().addToSchema(Schema.create(INT));
        Schema timeSchema = LogicalTypes.timeMillis().addToSchema(Schema.create(INT));
        Schema timestampSchema = LogicalTypes.timestampMillis().addToSchema(Schema.create(LONG));
        Schema decimalSchema = LogicalTypes.decimal(9, 2).addToSchema(Schema.create(BYTES));
        Schema uuidSchema = LogicalTypes.uuid().addToSchema(Schema.create(STRING));
        // Date/time types should always use upper level java classes
        // Decimal type target class depends on configuration
        // UUID should always be CharSequence since we haven't added its
        // support in SpecificRecord
        Assert.assertEquals("Should use Joda LocalDate for date type", "org.joda.time.LocalDate", compiler.javaType(dateSchema));
        Assert.assertEquals("Should use Joda LocalTime for time-millis type", "org.joda.time.LocalTime", compiler.javaType(timeSchema));
        Assert.assertEquals("Should use Joda DateTime for timestamp-millis type", "org.joda.time.DateTime", compiler.javaType(timestampSchema));
        Assert.assertEquals("Should use Java BigDecimal type", "java.math.BigDecimal", compiler.javaType(decimalSchema));
        Assert.assertEquals("Should use Java CharSequence type", "java.lang.CharSequence", compiler.javaType(uuidSchema));
    }

    @Test
    public void testJavaTypeWithDecimalLogicalTypeDisabled() throws Exception {
        SpecificCompiler compiler = createCompiler();
        compiler.setEnableDecimalLogicalType(false);
        Schema dateSchema = LogicalTypes.date().addToSchema(Schema.create(INT));
        Schema timeSchema = LogicalTypes.timeMillis().addToSchema(Schema.create(INT));
        Schema timestampSchema = LogicalTypes.timestampMillis().addToSchema(Schema.create(LONG));
        Schema decimalSchema = LogicalTypes.decimal(9, 2).addToSchema(Schema.create(BYTES));
        Schema uuidSchema = LogicalTypes.uuid().addToSchema(Schema.create(STRING));
        // Date/time types should always use upper level java classes
        // Decimal type target class depends on configuration
        // UUID should always be CharSequence since we haven't added its
        // support in SpecificRecord
        Assert.assertEquals("Should use Joda LocalDate for date type", "org.joda.time.LocalDate", compiler.javaType(dateSchema));
        Assert.assertEquals("Should use Joda LocalTime for time-millis type", "org.joda.time.LocalTime", compiler.javaType(timeSchema));
        Assert.assertEquals("Should use Joda DateTime for timestamp-millis type", "org.joda.time.DateTime", compiler.javaType(timestampSchema));
        Assert.assertEquals("Should use ByteBuffer type", "java.nio.ByteBuffer", compiler.javaType(decimalSchema));
        Assert.assertEquals("Should use Java CharSequence type", "java.lang.CharSequence", compiler.javaType(uuidSchema));
    }

    @Test
    public void testJavaTypeWithJsr310DateTimeTypes() throws Exception {
        SpecificCompiler compiler = createCompiler(DateTimeLogicalTypeImplementation.JSR310);
        Schema dateSchema = LogicalTypes.date().addToSchema(Schema.create(INT));
        Schema timeSchema = LogicalTypes.timeMillis().addToSchema(Schema.create(INT));
        Schema timestampSchema = LogicalTypes.timestampMillis().addToSchema(Schema.create(LONG));
        // Date/time types should always use upper level java classes
        Assert.assertEquals("Should use java.time.LocalDate for date type", "java.time.LocalDate", compiler.javaType(dateSchema));
        Assert.assertEquals("Should use java.time.LocalTime for time-millis type", "java.time.LocalTime", compiler.javaType(timeSchema));
        Assert.assertEquals("Should use java.time.Instant for timestamp-millis type", "java.time.Instant", compiler.javaType(timestampSchema));
    }

    @Test
    public void testJavaUnbox() throws Exception {
        SpecificCompiler compiler = createCompiler();
        compiler.setEnableDecimalLogicalType(false);
        Schema intSchema = Schema.create(INT);
        Schema longSchema = Schema.create(LONG);
        Schema floatSchema = Schema.create(FLOAT);
        Schema doubleSchema = Schema.create(DOUBLE);
        Schema boolSchema = Schema.create(BOOLEAN);
        Assert.assertEquals("Should use int for Type.INT", "int", compiler.javaUnbox(intSchema));
        Assert.assertEquals("Should use long for Type.LONG", "long", compiler.javaUnbox(longSchema));
        Assert.assertEquals("Should use float for Type.FLOAT", "float", compiler.javaUnbox(floatSchema));
        Assert.assertEquals("Should use double for Type.DOUBLE", "double", compiler.javaUnbox(doubleSchema));
        Assert.assertEquals("Should use boolean for Type.BOOLEAN", "boolean", compiler.javaUnbox(boolSchema));
        Schema dateSchema = LogicalTypes.date().addToSchema(Schema.create(INT));
        Schema timeSchema = LogicalTypes.timeMillis().addToSchema(Schema.create(INT));
        Schema timestampSchema = LogicalTypes.timestampMillis().addToSchema(Schema.create(LONG));
        // Date/time types should always use upper level java classes, even though
        // their underlying representations are primitive types
        Assert.assertEquals("Should use Joda LocalDate for date type", "org.joda.time.LocalDate", compiler.javaUnbox(dateSchema));
        Assert.assertEquals("Should use Joda LocalTime for time-millis type", "org.joda.time.LocalTime", compiler.javaUnbox(timeSchema));
        Assert.assertEquals("Should use Joda DateTime for timestamp-millis type", "org.joda.time.DateTime", compiler.javaUnbox(timestampSchema));
    }

    @Test
    public void testJavaUnboxJsr310DateTime() throws Exception {
        SpecificCompiler compiler = createCompiler(DateTimeLogicalTypeImplementation.JSR310);
        Schema dateSchema = LogicalTypes.date().addToSchema(Schema.create(INT));
        Schema timeSchema = LogicalTypes.timeMillis().addToSchema(Schema.create(INT));
        Schema timestampSchema = LogicalTypes.timestampMillis().addToSchema(Schema.create(LONG));
        // Date/time types should always use upper level java classes, even though
        // their underlying representations are primitive types
        Assert.assertEquals("Should use java.time.LocalDate for date type", "java.time.LocalDate", compiler.javaUnbox(dateSchema));
        Assert.assertEquals("Should use java.time.LocalTime for time-millis type", "java.time.LocalTime", compiler.javaUnbox(timeSchema));
        Assert.assertEquals("Should use java.time.Instant for timestamp-millis type", "java.time.Instant", compiler.javaUnbox(timestampSchema));
    }

    @Test
    public void testNullableLogicalTypesJavaUnboxDecimalTypesEnabled() throws Exception {
        SpecificCompiler compiler = createCompiler();
        compiler.setEnableDecimalLogicalType(true);
        // Nullable types should return boxed types instead of primitive types
        Schema nullableDecimalSchema1 = Schema.createUnion(Schema.create(NULL), LogicalTypes.decimal(9, 2).addToSchema(Schema.create(BYTES)));
        Schema nullableDecimalSchema2 = Schema.createUnion(LogicalTypes.decimal(9, 2).addToSchema(Schema.create(BYTES)), Schema.create(NULL));
        Assert.assertEquals("Should return boxed type", compiler.javaUnbox(nullableDecimalSchema1), "java.math.BigDecimal");
        Assert.assertEquals("Should return boxed type", compiler.javaUnbox(nullableDecimalSchema2), "java.math.BigDecimal");
    }

    @Test
    public void testNullableLogicalTypesJavaUnboxDecimalTypesDisabled() throws Exception {
        SpecificCompiler compiler = createCompiler();
        compiler.setEnableDecimalLogicalType(false);
        // Since logical decimal types are disabled, a ByteBuffer is expected.
        Schema nullableDecimalSchema1 = Schema.createUnion(Schema.create(NULL), LogicalTypes.decimal(9, 2).addToSchema(Schema.create(BYTES)));
        Schema nullableDecimalSchema2 = Schema.createUnion(LogicalTypes.decimal(9, 2).addToSchema(Schema.create(BYTES)), Schema.create(NULL));
        Assert.assertEquals("Should return boxed type", compiler.javaUnbox(nullableDecimalSchema1), "java.nio.ByteBuffer");
        Assert.assertEquals("Should return boxed type", compiler.javaUnbox(nullableDecimalSchema2), "java.nio.ByteBuffer");
    }

    @Test
    public void testNullableTypesJavaUnbox() throws Exception {
        SpecificCompiler compiler = createCompiler();
        compiler.setEnableDecimalLogicalType(false);
        // Nullable types should return boxed types instead of primitive types
        Schema nullableIntSchema1 = Schema.createUnion(Schema.create(NULL), Schema.create(INT));
        Schema nullableIntSchema2 = Schema.createUnion(Schema.create(INT), Schema.create(NULL));
        Assert.assertEquals("Should return boxed type", compiler.javaUnbox(nullableIntSchema1), "java.lang.Integer");
        Assert.assertEquals("Should return boxed type", compiler.javaUnbox(nullableIntSchema2), "java.lang.Integer");
        Schema nullableLongSchema1 = Schema.createUnion(Schema.create(NULL), Schema.create(LONG));
        Schema nullableLongSchema2 = Schema.createUnion(Schema.create(LONG), Schema.create(NULL));
        Assert.assertEquals("Should return boxed type", compiler.javaUnbox(nullableLongSchema1), "java.lang.Long");
        Assert.assertEquals("Should return boxed type", compiler.javaUnbox(nullableLongSchema2), "java.lang.Long");
        Schema nullableFloatSchema1 = Schema.createUnion(Schema.create(NULL), Schema.create(FLOAT));
        Schema nullableFloatSchema2 = Schema.createUnion(Schema.create(FLOAT), Schema.create(NULL));
        Assert.assertEquals("Should return boxed type", compiler.javaUnbox(nullableFloatSchema1), "java.lang.Float");
        Assert.assertEquals("Should return boxed type", compiler.javaUnbox(nullableFloatSchema2), "java.lang.Float");
        Schema nullableDoubleSchema1 = Schema.createUnion(Schema.create(NULL), Schema.create(DOUBLE));
        Schema nullableDoubleSchema2 = Schema.createUnion(Schema.create(DOUBLE), Schema.create(NULL));
        Assert.assertEquals("Should return boxed type", compiler.javaUnbox(nullableDoubleSchema1), "java.lang.Double");
        Assert.assertEquals("Should return boxed type", compiler.javaUnbox(nullableDoubleSchema2), "java.lang.Double");
        Schema nullableBooleanSchema1 = Schema.createUnion(Schema.create(NULL), Schema.create(BOOLEAN));
        Schema nullableBooleanSchema2 = Schema.createUnion(Schema.create(BOOLEAN), Schema.create(NULL));
        Assert.assertEquals("Should return boxed type", compiler.javaUnbox(nullableBooleanSchema1), "java.lang.Boolean");
        Assert.assertEquals("Should return boxed type", compiler.javaUnbox(nullableBooleanSchema2), "java.lang.Boolean");
    }

    @Test
    public void testGetUsedConversionClassesForNullableLogicalTypes() throws Exception {
        SpecificCompiler compiler = createCompiler();
        compiler.setEnableDecimalLogicalType(true);
        Schema nullableDecimal1 = Schema.createUnion(Schema.create(NULL), LogicalTypes.decimal(9, 2).addToSchema(Schema.create(BYTES)));
        Schema schemaWithNullableDecimal1 = Schema.createRecord("WithNullableDecimal", "", "", false, Collections.singletonList(new Schema.Field("decimal", nullableDecimal1, "", null)));
        final Collection<String> usedConversionClasses = compiler.getUsedConversionClasses(schemaWithNullableDecimal1);
        Assert.assertEquals(1, usedConversionClasses.size());
        Assert.assertEquals("org.apache.avro.Conversions.DecimalConversion", usedConversionClasses.iterator().next());
    }

    @Test
    public void testGetUsedConversionClassesForNullableLogicalTypesInNestedRecord() throws Exception {
        SpecificCompiler compiler = createCompiler();
        final Schema schema = new Schema.Parser().parse("{\"type\":\"record\",\"name\":\"NestedLogicalTypesRecord\",\"namespace\":\"org.apache.avro.codegentest.testdata\",\"doc\":\"Test nested types with logical types in generated Java classes\",\"fields\":[{\"name\":\"nestedRecord\",\"type\":{\"type\":\"record\",\"name\":\"NestedRecord\",\"fields\":[{\"name\":\"nullableDateField\",\"type\":[\"null\",{\"type\":\"int\",\"logicalType\":\"date\"}]}]}}]}");
        final Collection<String> usedConversionClasses = compiler.getUsedConversionClasses(schema);
        Assert.assertEquals(1, usedConversionClasses.size());
        Assert.assertEquals("org.apache.avro.data.TimeConversions.DateConversion", usedConversionClasses.iterator().next());
    }

    @Test
    public void testGetUsedConversionClassesForNullableLogicalTypesInArray() throws Exception {
        SpecificCompiler compiler = createCompiler();
        final Schema schema = new Schema.Parser().parse("{\"type\":\"record\",\"name\":\"NullableLogicalTypesArray\",\"namespace\":\"org.apache.avro.codegentest.testdata\",\"doc\":\"Test nested types with logical types in generated Java classes\",\"fields\":[{\"name\":\"arrayOfLogicalType\",\"type\":{\"type\":\"array\",\"items\":[\"null\",{\"type\":\"int\",\"logicalType\":\"date\"}]}}]}");
        final Collection<String> usedConversionClasses = compiler.getUsedConversionClasses(schema);
        Assert.assertEquals(1, usedConversionClasses.size());
        Assert.assertEquals("org.apache.avro.data.TimeConversions.DateConversion", usedConversionClasses.iterator().next());
    }

    @Test
    public void testGetUsedConversionClassesForNullableLogicalTypesInArrayOfRecords() throws Exception {
        SpecificCompiler compiler = createCompiler();
        final Schema schema = new Schema.Parser().parse("{\"type\":\"record\",\"name\":\"NestedLogicalTypesArray\",\"namespace\":\"org.apache.avro.codegentest.testdata\",\"doc\":\"Test nested types with logical types in generated Java classes\",\"fields\":[{\"name\":\"arrayOfRecords\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"RecordInArray\",\"fields\":[{\"name\":\"nullableDateField\",\"type\":[\"null\",{\"type\":\"int\",\"logicalType\":\"date\"}]}]}}}]}");
        final Collection<String> usedConversionClasses = compiler.getUsedConversionClasses(schema);
        Assert.assertEquals(1, usedConversionClasses.size());
        Assert.assertEquals("org.apache.avro.data.TimeConversions.DateConversion", usedConversionClasses.iterator().next());
    }

    @Test
    public void testGetUsedConversionClassesForNullableLogicalTypesInUnionOfRecords() throws Exception {
        SpecificCompiler compiler = createCompiler();
        final Schema schema = new Schema.Parser().parse("{\"type\":\"record\",\"name\":\"NestedLogicalTypesUnion\",\"namespace\":\"org.apache.avro.codegentest.testdata\",\"doc\":\"Test nested types with logical types in generated Java classes\",\"fields\":[{\"name\":\"unionOfRecords\",\"type\":[\"null\",{\"type\":\"record\",\"name\":\"RecordInUnion\",\"fields\":[{\"name\":\"nullableDateField\",\"type\":[\"null\",{\"type\":\"int\",\"logicalType\":\"date\"}]}]}]}]}");
        final Collection<String> usedConversionClasses = compiler.getUsedConversionClasses(schema);
        Assert.assertEquals(1, usedConversionClasses.size());
        Assert.assertEquals("org.apache.avro.data.TimeConversions.DateConversion", usedConversionClasses.iterator().next());
    }

    @Test
    public void testGetUsedConversionClassesForNullableLogicalTypesInMapOfRecords() throws Exception {
        SpecificCompiler compiler = createCompiler();
        final Schema schema = new Schema.Parser().parse("{\"type\":\"record\",\"name\":\"NestedLogicalTypesMap\",\"namespace\":\"org.apache.avro.codegentest.testdata\",\"doc\":\"Test nested types with logical types in generated Java classes\",\"fields\":[{\"name\":\"mapOfRecords\",\"type\":{\"type\":\"map\",\"values\":{\"type\":\"record\",\"name\":\"RecordInMap\",\"fields\":[{\"name\":\"nullableDateField\",\"type\":[\"null\",{\"type\":\"int\",\"logicalType\":\"date\"}]}]},\"avro.java.string\":\"String\"}}]}");
        final Collection<String> usedConversionClasses = compiler.getUsedConversionClasses(schema);
        Assert.assertEquals(1, usedConversionClasses.size());
        Assert.assertEquals("org.apache.avro.data.TimeConversions.DateConversion", usedConversionClasses.iterator().next());
    }

    @Test
    public void testLogicalTypesWithMultipleFields() throws Exception {
        Schema logicalTypesWithMultipleFields = new Schema.Parser().parse(new File("src/test/resources/logical_types_with_multiple_fields.avsc"));
        TestSpecificCompiler.assertCompilesWithJavaCompiler(new File(OUTPUT_DIR.getRoot(), name.getMethodName()), compile());
    }

    @Test
    public void testUnionAndFixedFields() throws Exception {
        Schema unionTypesWithMultipleFields = new Schema.Parser().parse(new File("src/test/resources/union_and_fixed_fields.avsc"));
        TestSpecificCompiler.assertCompilesWithJavaCompiler(new File(this.outputFile, name.getMethodName()), compile());
    }

    @Test
    public void testLogicalTypesWithMultipleFieldsJsr310DateTime() throws Exception {
        Schema logicalTypesWithMultipleFields = new Schema.Parser().parse(new File("src/test/resources/logical_types_with_multiple_fields.avsc"));
        TestSpecificCompiler.assertCompilesWithJavaCompiler(new File(this.outputFile, name.getMethodName()), compile());
    }

    @Test
    public void testConversionInstanceWithDecimalLogicalTypeDisabled() throws Exception {
        SpecificCompiler compiler = createCompiler();
        compiler.setEnableDecimalLogicalType(false);
        Schema dateSchema = LogicalTypes.date().addToSchema(Schema.create(INT));
        Schema timeSchema = LogicalTypes.timeMillis().addToSchema(Schema.create(INT));
        Schema timestampSchema = LogicalTypes.timestampMillis().addToSchema(Schema.create(LONG));
        Schema decimalSchema = LogicalTypes.decimal(9, 2).addToSchema(Schema.create(BYTES));
        Schema uuidSchema = LogicalTypes.uuid().addToSchema(Schema.create(STRING));
        Assert.assertEquals("Should use date conversion for date type", "new org.apache.avro.data.TimeConversions.DateConversion()", compiler.conversionInstance(dateSchema));
        Assert.assertEquals("Should use time conversion for time type", "new org.apache.avro.data.TimeConversions.TimeConversion()", compiler.conversionInstance(timeSchema));
        Assert.assertEquals("Should use timestamp conversion for date type", "new org.apache.avro.data.TimeConversions.TimestampConversion()", compiler.conversionInstance(timestampSchema));
        Assert.assertEquals("Should use null for decimal if the flag is off", "null", compiler.conversionInstance(decimalSchema));
        Assert.assertEquals("Should use null for decimal if the flag is off", "null", compiler.conversionInstance(uuidSchema));
    }

    @Test
    public void testConversionInstanceWithDecimalLogicalTypeEnabled() throws Exception {
        SpecificCompiler compiler = createCompiler();
        compiler.setEnableDecimalLogicalType(true);
        Schema dateSchema = LogicalTypes.date().addToSchema(Schema.create(INT));
        Schema timeSchema = LogicalTypes.timeMillis().addToSchema(Schema.create(INT));
        Schema timestampSchema = LogicalTypes.timestampMillis().addToSchema(Schema.create(LONG));
        Schema decimalSchema = LogicalTypes.decimal(9, 2).addToSchema(Schema.create(BYTES));
        Schema uuidSchema = LogicalTypes.uuid().addToSchema(Schema.create(STRING));
        Assert.assertEquals("Should use date conversion for date type", "new org.apache.avro.data.TimeConversions.DateConversion()", compiler.conversionInstance(dateSchema));
        Assert.assertEquals("Should use time conversion for time type", "new org.apache.avro.data.TimeConversions.TimeConversion()", compiler.conversionInstance(timeSchema));
        Assert.assertEquals("Should use timestamp conversion for date type", "new org.apache.avro.data.TimeConversions.TimestampConversion()", compiler.conversionInstance(timestampSchema));
        Assert.assertEquals("Should use null for decimal if the flag is off", "new org.apache.avro.Conversions.DecimalConversion()", compiler.conversionInstance(decimalSchema));
        Assert.assertEquals("Should use null for decimal if the flag is off", "null", compiler.conversionInstance(uuidSchema));
    }

    @Test
    public void testPojoWithOptionalTurnedOffByDefault() throws IOException {
        SpecificCompiler compiler = createCompiler();
        compiler.compileToDestination(this.src, OUTPUT_DIR.getRoot());
        Assert.assertTrue(this.outputFile.exists());
        try (BufferedReader reader = new BufferedReader(new FileReader(this.outputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                Assert.assertFalse(line.contains("Optional"));
            } 
        }
    }

    @Test
    public void testPojoWithOptionalCreatedWhenOptionTurnedOn() throws IOException {
        SpecificCompiler compiler = createCompiler();
        compiler.setGettersReturnOptional(true);
        // compiler.setCreateOptionalGetters(true);
        compiler.compileToDestination(this.src, OUTPUT_DIR.getRoot());
        Assert.assertTrue(this.outputFile.exists());
        int optionalFound = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(this.outputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.contains("Optional")) {
                    optionalFound++;
                }
            } 
        }
        Assert.assertEquals(9, optionalFound);
    }

    @Test
    public void testPojoWithOptionalCreatedWhenOptionalForEverythingTurnedOn() throws IOException {
        SpecificCompiler compiler = createCompiler();
        // compiler.setGettersReturnOptional(true);
        compiler.setCreateOptionalGetters(true);
        compiler.compileToDestination(this.src, OUTPUT_DIR.getRoot());
        Assert.assertTrue(this.outputFile.exists());
        int optionalFound = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(this.outputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.contains("Optional")) {
                    optionalFound++;
                }
            } 
        }
        Assert.assertEquals(17, optionalFound);
    }
}

