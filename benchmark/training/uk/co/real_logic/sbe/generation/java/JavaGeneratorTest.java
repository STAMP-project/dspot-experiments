/**
 * Copyright 2013-2019 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.real_logic.sbe.generation.java;


import java.lang.reflect.Method;
import java.nio.ByteOrder;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.agrona.generation.StringWriterOutputManager;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import uk.co.real_logic.sbe.generation.CodeGenerator;
import uk.co.real_logic.sbe.ir.Ir;


public class JavaGeneratorTest {
    private static final Class<?> BUFFER_CLASS = MutableDirectBuffer.class;

    private static final String BUFFER_NAME = JavaGeneratorTest.BUFFER_CLASS.getName();

    private static final Class<DirectBuffer> READ_ONLY_BUFFER_CLASS = DirectBuffer.class;

    private static final String READ_ONLY_BUFFER_NAME = JavaGeneratorTest.READ_ONLY_BUFFER_CLASS.getName();

    private static final ByteOrder BYTE_ORDER = ByteOrder.nativeOrder();

    private final StringWriterOutputManager outputManager = new StringWriterOutputManager();

    private final MutableDirectBuffer mockBuffer = Mockito.mock(MutableDirectBuffer.class);

    private Ir ir;

    @Test
    public void shouldGenerateMessageHeaderStub() throws Exception {
        final int bufferOffset = 64;
        final int templateIdOffset = 2;
        final short templateId = ((short) (7));
        final int blockLength = 32;
        final String fqClassName = ((ir.applicableNamespace()) + ".") + (JavaGenerator.MESSAGE_HEADER_ENCODER_TYPE);
        Mockito.when(mockBuffer.getShort((bufferOffset + templateIdOffset), JavaGeneratorTest.BYTE_ORDER)).thenReturn(templateId);
        final JavaGenerator generator = generator();
        generator.generateTypeStubs();
        generator.generateMessageHeaderStub();
        final Class<?> clazz = compile(fqClassName);
        Assert.assertNotNull(clazz);
        final Object flyweight = clazz.getConstructor().newInstance();
        final Method method = flyweight.getClass().getDeclaredMethod("wrap", JavaGeneratorTest.BUFFER_CLASS, int.class);
        method.invoke(flyweight, mockBuffer, bufferOffset);
        clazz.getDeclaredMethod("blockLength", int.class).invoke(flyweight, blockLength);
        Mockito.verify(mockBuffer).putShort(bufferOffset, ((short) (blockLength)), JavaGeneratorTest.BYTE_ORDER);
    }

    @Test
    public void shouldGenerateMessageHeaderDecoderStub() throws Exception {
        final int bufferOffset = 64;
        final int templateIdOffset = 2;
        final short templateId = ((short) (7));
        final String fqClassName = ((ir.applicableNamespace()) + ".") + (CodeGenerator.MESSAGE_HEADER_DECODER_TYPE);
        Mockito.when(mockBuffer.getShort((bufferOffset + templateIdOffset), JavaGeneratorTest.BYTE_ORDER)).thenReturn(templateId);
        final JavaGenerator generator = generator();
        generator.generateTypeStubs();
        generator.generateMessageHeaderStub();
        final Class<?> clazz = compile(fqClassName);
        Assert.assertNotNull(clazz);
        final Object flyweight = clazz.getConstructor().newInstance();
        final Method method = flyweight.getClass().getDeclaredMethod("wrap", JavaGeneratorTest.READ_ONLY_BUFFER_CLASS, int.class);
        method.invoke(flyweight, mockBuffer, bufferOffset);
        final Integer result = ((Integer) (clazz.getDeclaredMethod("templateId").invoke(flyweight)));
        Assert.assertThat(result, CoreMatchers.is(((int) (templateId))));
    }

    @Test
    public void shouldGenerateUint8EnumStub() throws Exception {
        final String className = "BooleanType";
        final String fqClassName = ((ir.applicableNamespace()) + ".") + className;
        generateTypeStubs();
        final Class<?> clazz = compile(fqClassName);
        Assert.assertNotNull(clazz);
        final Object result = clazz.getDeclaredMethod("get", short.class).invoke(null, ((short) (1)));
        Assert.assertThat(result.toString(), CoreMatchers.is("T"));
    }

    @Test
    public void shouldGenerateCharEnumStub() throws Exception {
        generateTypeStubs();
        final Class<?> clazz = compileModel();
        final Object result = ReflectionUtil.getByte(clazz, ((byte) ('B')));
        Assert.assertThat(result, Matchers.hasToString("B"));
    }

    @Test
    public void shouldGenerateChoiceSetStub() throws Exception {
        final int bufferOffset = 8;
        final byte bitset = ((byte) (4));
        final String className = "OptionalExtrasDecoder";
        final String fqClassName = ((ir.applicableNamespace()) + ".") + className;
        Mockito.when(mockBuffer.getByte(bufferOffset)).thenReturn(bitset);
        generateTypeStubs();
        final Class<?> clazz = compile(fqClassName);
        Assert.assertNotNull(clazz);
        final Object flyweight = clazz.getConstructor().newInstance();
        final Method method = flyweight.getClass().getDeclaredMethod("wrap", JavaGeneratorTest.READ_ONLY_BUFFER_CLASS, int.class);
        method.invoke(flyweight, mockBuffer, bufferOffset);
        final Object result = ReflectionUtil.get(flyweight, "cruiseControl");
        Assert.assertThat(result, CoreMatchers.is(Boolean.TRUE));
    }

    @Test
    public void shouldGenerateCompositeEncoder() throws Exception {
        final int bufferOffset = 64;
        final int capacityFieldOffset = bufferOffset;
        final int numCylindersOffset = bufferOffset + 2;
        final int expectedEngineCapacity = 2000;
        final int manufacturerCodeOffset = bufferOffset + 3;
        final byte[] manufacturerCode = new byte[]{ 'A', 'B', 'C' };
        final String className = "EngineEncoder";
        final String fqClassName = ((ir.applicableNamespace()) + ".") + className;
        Mockito.when(mockBuffer.getShort(capacityFieldOffset, JavaGeneratorTest.BYTE_ORDER)).thenReturn(((short) (expectedEngineCapacity)));
        generateTypeStubs();
        final Class<?> clazz = compile(fqClassName);
        Assert.assertNotNull(clazz);
        final Object flyweight = clazz.getConstructor().newInstance();
        JavaGeneratorTest.wrap(bufferOffset, flyweight, mockBuffer, JavaGeneratorTest.BUFFER_CLASS);
        final short numCylinders = ((short) (4));
        clazz.getDeclaredMethod("numCylinders", short.class).invoke(flyweight, numCylinders);
        clazz.getDeclaredMethod("putManufacturerCode", byte[].class, int.class).invoke(flyweight, manufacturerCode, 0);
        Mockito.verify(mockBuffer).putByte(numCylindersOffset, ((byte) (numCylinders)));
        Mockito.verify(mockBuffer).putBytes(manufacturerCodeOffset, manufacturerCode, 0, manufacturerCode.length);
    }

    @Test
    public void shouldGenerateCompositeDecoder() throws Exception {
        final int bufferOffset = 64;
        final int capacityFieldOffset = bufferOffset;
        final int expectedEngineCapacity = 2000;
        final int expectedMaxRpm = 9000;
        final String className = "EngineDecoder";
        final String fqClassName = ((ir.applicableNamespace()) + ".") + className;
        Mockito.when(mockBuffer.getShort(capacityFieldOffset, JavaGeneratorTest.BYTE_ORDER)).thenReturn(((short) (expectedEngineCapacity)));
        generateTypeStubs();
        final Class<?> clazz = compile(fqClassName);
        Assert.assertNotNull(clazz);
        final Object flyweight = clazz.getConstructor().newInstance();
        JavaGeneratorTest.wrap(bufferOffset, flyweight, mockBuffer, JavaGeneratorTest.READ_ONLY_BUFFER_CLASS);
        final int capacityResult = ReflectionUtil.getCapacity(flyweight);
        Assert.assertThat(capacityResult, CoreMatchers.is(expectedEngineCapacity));
        final int maxRpmResult = ReflectionUtil.getInt(flyweight, "maxRpm");
        Assert.assertThat(maxRpmResult, CoreMatchers.is(expectedMaxRpm));
    }

    @Test
    public void shouldGenerateBasicMessage() throws Exception {
        final UnsafeBuffer buffer = new UnsafeBuffer(new byte[4096]);
        generator().generate();
        final Object msgFlyweight = JavaGeneratorTest.wrap(buffer, compileCarEncoder().getConstructor().newInstance());
        final Object groupFlyweight = ReflectionUtil.fuelFiguresCount(msgFlyweight, 0);
        Assert.assertNotNull(groupFlyweight);
        Assert.assertThat(msgFlyweight.toString(), ArgumentMatchers.startsWith("[Car]"));
    }

    @Test
    public void shouldGenerateRepeatingGroupDecoder() throws Exception {
        final UnsafeBuffer buffer = new UnsafeBuffer(new byte[4096]);
        generator().generate();
        final Object encoder = JavaGeneratorTest.wrap(buffer, compileCarEncoder().getConstructor().newInstance());
        final Object decoder = JavaGeneratorTest.wrap(buffer, compileCarDecoder().getConstructor().newInstance(), ReflectionUtil.getSbeBlockLength(encoder), ReflectionUtil.getSbeSchemaVersion(encoder));
        final Integer initialPosition = ReflectionUtil.getLimit(decoder);
        final Object groupFlyweight = ReflectionUtil.getFuelFigures(decoder);
        Assert.assertThat(ReflectionUtil.getLimit(decoder), Matchers.greaterThan(initialPosition));
        Assert.assertThat(ReflectionUtil.getCount(groupFlyweight), CoreMatchers.is(0));
    }

    @Test
    public void shouldGenerateReadOnlyMessage() throws Exception {
        final UnsafeBuffer buffer = new UnsafeBuffer(new byte[4096]);
        generator().generate();
        final Object encoder = JavaGeneratorTest.wrap(buffer, compileCarEncoder().getConstructor().newInstance());
        final Object decoder = getCarDecoder(buffer, encoder);
        final long expectedSerialNumber = 5L;
        ReflectionUtil.putSerialNumber(encoder, expectedSerialNumber);
        final long serialNumber = ReflectionUtil.getSerialNumber(decoder);
        Assert.assertEquals(expectedSerialNumber, serialNumber);
    }

    @Test
    public void shouldGenerateVarDataCodecs() throws Exception {
        final String expectedManufacturer = "Ford";
        final UnsafeBuffer buffer = new UnsafeBuffer(new byte[4096]);
        generator().generate();
        final Object encoder = JavaGeneratorTest.wrap(buffer, compileCarEncoder().getConstructor().newInstance());
        final Object decoder = getCarDecoder(buffer, encoder);
        ReflectionUtil.setManufacturer(encoder, expectedManufacturer);
        final String manufacturer = ReflectionUtil.getManufacturer(decoder);
        Assert.assertEquals(expectedManufacturer, manufacturer);
    }

    @Test
    public void shouldGenerateCompositeDecoding() throws Exception {
        final int expectedEngineCapacity = 2000;
        final UnsafeBuffer buffer = new UnsafeBuffer(new byte[4096]);
        generator().generate();
        final Object encoder = JavaGeneratorTest.wrap(buffer, compileCarEncoder().getConstructor().newInstance());
        final Object decoder = getCarDecoder(buffer, encoder);
        final Object engineEncoder = ReflectionUtil.get(encoder, "engine");
        final Object engineDecoder = ReflectionUtil.get(decoder, "engine");
        ReflectionUtil.setCapacity(engineEncoder, expectedEngineCapacity);
        Assert.assertEquals(expectedEngineCapacity, ReflectionUtil.getCapacity(engineDecoder));
    }

    @Test
    public void shouldGenerateBitSetCodecs() throws Exception {
        final UnsafeBuffer buffer = new UnsafeBuffer(new byte[4096]);
        generator().generate();
        final Object encoder = JavaGeneratorTest.wrap(buffer, compileCarEncoder().getConstructor().newInstance());
        final Object decoder = getCarDecoder(buffer, encoder);
        final Object extrasEncoder = ReflectionUtil.getExtras(encoder);
        final Object extrasDecoder = ReflectionUtil.getExtras(decoder);
        Assert.assertFalse(ReflectionUtil.getCruiseControl(extrasDecoder));
        ReflectionUtil.setCruiseControl(extrasEncoder, true);
        Assert.assertTrue(ReflectionUtil.getCruiseControl(extrasDecoder));
    }

    @Test
    public void shouldGenerateEnumCodecs() throws Exception {
        final UnsafeBuffer buffer = new UnsafeBuffer(new byte[4096]);
        generator().generate();
        final Object encoder = JavaGeneratorTest.wrap(buffer, compileCarEncoder().getConstructor().newInstance());
        final Object decoder = getCarDecoder(buffer, encoder);
        final Class<?> encoderModel = getModelClass(encoder);
        final Object modelB = encoderModel.getEnumConstants()[1];
        ReflectionUtil.set(encoder, "code", encoderModel, modelB);
        Assert.assertThat(ReflectionUtil.get(decoder, "code"), Matchers.hasToString("B"));
    }

    @Test
    public void shouldGenerateGetString() throws Exception {
        final UnsafeBuffer buffer = new UnsafeBuffer(new byte[4096]);
        generator().generate();
        final Object encoder = JavaGeneratorTest.wrap(buffer, compileCarEncoder().getConstructor().newInstance());
        final Object decoder = getCarDecoder(buffer, encoder);
        ReflectionUtil.set(encoder, "vehicleCode", String.class, "R11");
        Assert.assertThat(ReflectionUtil.get(decoder, "vehicleCode"), CoreMatchers.is("R11"));
        ReflectionUtil.set(encoder, "vehicleCode", String.class, "");
        Assert.assertThat(ReflectionUtil.get(decoder, "vehicleCode"), CoreMatchers.is(""));
        ReflectionUtil.set(encoder, "vehicleCode", String.class, "R11R12");
        Assert.assertThat(ReflectionUtil.get(decoder, "vehicleCode"), CoreMatchers.is("R11R12"));
    }

    @Test
    public void shouldGenerateGetFixedLengthStringUsingAppendable() throws Exception {
        final UnsafeBuffer buffer = new UnsafeBuffer(new byte[4096]);
        final StringBuilder result = new StringBuilder();
        generator().generate();
        final Object encoder = JavaGeneratorTest.wrap(buffer, compileCarEncoder().getDeclaredConstructor().newInstance());
        final Object decoder = getCarDecoder(buffer, encoder);
        ReflectionUtil.set(encoder, "vehicleCode", String.class, "R11");
        ReflectionUtil.get(decoder, "vehicleCode", result);
        Assert.assertThat(result.toString(), CoreMatchers.is("R11"));
        result.setLength(0);
        ReflectionUtil.set(encoder, "vehicleCode", String.class, "");
        ReflectionUtil.get(decoder, "vehicleCode", result);
        Assert.assertThat(result.toString(), CoreMatchers.is(""));
        result.setLength(0);
        ReflectionUtil.set(encoder, "vehicleCode", String.class, "R11R12");
        ReflectionUtil.get(decoder, "vehicleCode", result);
        Assert.assertThat(result.toString(), CoreMatchers.is("R11R12"));
    }

    @Test
    public void shouldGenerateGetVariableStringUsingAppendable() throws Exception {
        final UnsafeBuffer buffer = new UnsafeBuffer(new byte[4096]);
        final StringBuilder result = new StringBuilder();
        generator().generate();
        final Object encoder = JavaGeneratorTest.wrap(buffer, compileCarEncoder().getDeclaredConstructor().newInstance());
        final Object decoder = getCarDecoder(buffer, encoder);
        ReflectionUtil.set(encoder, "color", String.class, "Red");
        ReflectionUtil.get(decoder, "color", result);
        Assert.assertThat(result.toString(), CoreMatchers.is("Red"));
        result.setLength(0);
        ReflectionUtil.set(encoder, "color", String.class, "");
        ReflectionUtil.get(decoder, "color", result);
        Assert.assertThat(result.toString(), CoreMatchers.is(""));
        result.setLength(0);
        ReflectionUtil.set(encoder, "color", String.class, "Red and Blue");
        ReflectionUtil.get(decoder, "color", result);
        Assert.assertThat(result.toString(), CoreMatchers.is("Red and Blue"));
    }

    @Test
    public void shouldGeneratePutCharSequence() throws Exception {
        final UnsafeBuffer buffer = new UnsafeBuffer(new byte[4096]);
        generator().generate();
        final Object encoder = JavaGeneratorTest.wrap(buffer, compileCarEncoder().getConstructor().newInstance());
        final Object decoder = getCarDecoder(buffer, encoder);
        ReflectionUtil.set(encoder, "vehicleCode", CharSequence.class, "R11");
        Assert.assertThat(ReflectionUtil.get(decoder, "vehicleCode"), CoreMatchers.is("R11"));
        ReflectionUtil.set(encoder, "vehicleCode", CharSequence.class, "");
        Assert.assertThat(ReflectionUtil.get(decoder, "vehicleCode"), CoreMatchers.is(""));
        ReflectionUtil.set(encoder, "vehicleCode", CharSequence.class, "R11R12");
        Assert.assertThat(ReflectionUtil.get(decoder, "vehicleCode"), CoreMatchers.is("R11R12"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldValidateMissingMutableBufferClass() {
        new JavaGenerator(ir, "dasdsads", JavaGeneratorTest.BUFFER_NAME, false, false, false, outputManager);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldValidateNotImplementedMutableBufferClass() {
        new JavaGenerator(ir, "java.nio.ByteBuffer", JavaGeneratorTest.BUFFER_NAME, false, false, false, outputManager);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldValidateMissingReadOnlyBufferClass() {
        new JavaGenerator(ir, JavaGeneratorTest.BUFFER_NAME, "dasdsads", false, false, false, outputManager);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldValidateNotImplementedReadOnlyBufferClass() {
        new JavaGenerator(ir, JavaGeneratorTest.BUFFER_NAME, "java.nio.ByteBuffer", false, false, false, outputManager);
    }
}

