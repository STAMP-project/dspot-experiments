package org.web3j.codegen;


import AbiDefinition.NamedType;
import TypeSpec.Builder;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.web3j.TempFileProvider;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.StaticArray;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Int256;
import org.web3j.abi.datatypes.generated.StaticArray10;
import org.web3j.abi.datatypes.generated.StaticArray2;
import org.web3j.abi.datatypes.generated.StaticArray3;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint64;
import org.web3j.protocol.core.methods.response.AbiDefinition;


public class SolidityFunctionWrapperTest extends TempFileProvider {
    private SolidityFunctionWrapper solidityFunctionWrapper;

    private GenerationReporter generationReporter;

    @Test
    public void testCreateValidParamName() {
        Assert.assertThat(SolidityFunctionWrapper.createValidParamName("param", 1), Is.is("param"));
        Assert.assertThat(SolidityFunctionWrapper.createValidParamName("", 1), Is.is("param1"));
    }

    @Test
    public void testBuildTypeName() {
        Assert.assertThat(SolidityFunctionWrapper.buildTypeName("uint256"), Is.is(ClassName.get(Uint256.class)));
        Assert.assertThat(SolidityFunctionWrapper.buildTypeName("uint64"), Is.is(ClassName.get(Uint64.class)));
        Assert.assertThat(SolidityFunctionWrapper.buildTypeName("string"), Is.is(ClassName.get(Utf8String.class)));
        Assert.assertThat(SolidityFunctionWrapper.buildTypeName("uint256[]"), Is.is(ParameterizedTypeName.get(DynamicArray.class, Uint256.class)));
        Assert.assertThat(SolidityFunctionWrapper.buildTypeName("uint256[] storage"), Is.is(ParameterizedTypeName.get(DynamicArray.class, Uint256.class)));
        Assert.assertThat(SolidityFunctionWrapper.buildTypeName("uint256[] memory"), Is.is(ParameterizedTypeName.get(DynamicArray.class, Uint256.class)));
        Assert.assertThat(SolidityFunctionWrapper.buildTypeName("uint256[10]"), Is.is(ParameterizedTypeName.get(StaticArray10.class, Uint256.class)));
        Assert.assertThat(SolidityFunctionWrapper.buildTypeName("uint256[33]"), Is.is(ParameterizedTypeName.get(StaticArray.class, Uint256.class)));
        Assert.assertThat(SolidityFunctionWrapper.buildTypeName("uint256[10][3]"), Is.is(ParameterizedTypeName.get(ClassName.get(StaticArray3.class), ParameterizedTypeName.get(StaticArray10.class, Uint256.class))));
        Assert.assertThat(SolidityFunctionWrapper.buildTypeName("uint256[2][]"), Is.is(ParameterizedTypeName.get(ClassName.get(DynamicArray.class), ParameterizedTypeName.get(StaticArray2.class, Uint256.class))));
        Assert.assertThat(SolidityFunctionWrapper.buildTypeName("uint256[33][]"), Is.is(ParameterizedTypeName.get(ClassName.get(DynamicArray.class), ParameterizedTypeName.get(StaticArray.class, Uint256.class))));
        Assert.assertThat(SolidityFunctionWrapper.buildTypeName("uint256[][]"), Is.is(ParameterizedTypeName.get(ClassName.get(DynamicArray.class), ParameterizedTypeName.get(DynamicArray.class, Uint256.class))));
    }

    @Test
    public void testGetNativeType() {
        Assert.assertThat(SolidityFunctionWrapper.getNativeType(TypeName.get(Address.class)), IsEqual.equalTo(TypeName.get(String.class)));
        Assert.assertThat(SolidityFunctionWrapper.getNativeType(TypeName.get(Uint256.class)), IsEqual.equalTo(TypeName.get(BigInteger.class)));
        Assert.assertThat(SolidityFunctionWrapper.getNativeType(TypeName.get(Int256.class)), IsEqual.equalTo(TypeName.get(BigInteger.class)));
        Assert.assertThat(SolidityFunctionWrapper.getNativeType(TypeName.get(Utf8String.class)), IsEqual.equalTo(TypeName.get(String.class)));
        Assert.assertThat(SolidityFunctionWrapper.getNativeType(TypeName.get(Bool.class)), IsEqual.equalTo(TypeName.get(Boolean.class)));
        Assert.assertThat(SolidityFunctionWrapper.getNativeType(TypeName.get(Bytes32.class)), IsEqual.equalTo(TypeName.get(byte[].class)));
        Assert.assertThat(SolidityFunctionWrapper.getNativeType(TypeName.get(DynamicBytes.class)), IsEqual.equalTo(TypeName.get(byte[].class)));
    }

    @Test
    public void testGetNativeTypeParameterized() {
        Assert.assertThat(SolidityFunctionWrapper.getNativeType(ParameterizedTypeName.get(ClassName.get(DynamicArray.class), TypeName.get(Address.class))), IsEqual.equalTo(ParameterizedTypeName.get(ClassName.get(List.class), TypeName.get(String.class))));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetNativeTypeInvalid() {
        SolidityFunctionWrapper.getNativeType(TypeName.get(BigInteger.class));
    }

    @Test
    public void testGetEventNativeType() {
        Assert.assertThat(SolidityFunctionWrapper.getEventNativeType(TypeName.get(Utf8String.class)), IsEqual.equalTo(TypeName.get(byte[].class)));
    }

    @Test
    public void testGetEventNativeTypeParameterized() {
        Assert.assertThat(SolidityFunctionWrapper.getEventNativeType(ParameterizedTypeName.get(ClassName.get(DynamicArray.class), TypeName.get(Address.class))), IsEqual.equalTo(TypeName.get(byte[].class)));
    }

    @Test
    public void testBuildFunctionTransaction() throws Exception {
        AbiDefinition functionDefinition = new AbiDefinition(false, Arrays.asList(new AbiDefinition.NamedType("param", "uint8")), "functionName", Collections.emptyList(), "type", false);
        MethodSpec methodSpec = solidityFunctionWrapper.buildFunction(functionDefinition);
        // CHECKSTYLE:OFF
        String expected = "public org.web3j.protocol.core.RemoteCall<org.web3j.protocol.core.methods.response.TransactionReceipt> functionName(java.math.BigInteger param) {\n" + ((((("  final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(\n" + "      FUNC_FUNCTIONNAME, \n") + "      java.util.Arrays.<org.web3j.abi.datatypes.Type>asList(new org.web3j.abi.datatypes.generated.Uint8(param)), \n") + "      java.util.Collections.<org.web3j.abi.TypeReference<?>>emptyList());\n") + "  return executeRemoteCallTransaction(function);\n") + "}\n");
        // CHECKSTYLE:ON
        Assert.assertThat(methodSpec.toString(), Is.is(expected));
    }

    @Test
    public void testBuildingFunctionTransactionThatReturnsValueReportsWarning() throws Exception {
        AbiDefinition functionDefinition = new AbiDefinition(false, Arrays.asList(new AbiDefinition.NamedType("param", "uint8")), "functionName", Arrays.asList(new AbiDefinition.NamedType("result", "uint8")), "type", false);
        solidityFunctionWrapper.buildFunction(functionDefinition);
        // CHECKSTYLE:OFF
        Mockito.verify(generationReporter).report(("Definition of the function functionName returns a value but is not defined as a view function. " + "Please ensure it contains the view modifier if you want to read the return value"));
        // CHECKSTYLE:ON
    }

    @Test
    public void testBuildPayableFunctionTransaction() throws Exception {
        AbiDefinition functionDefinition = new AbiDefinition(false, Arrays.asList(new AbiDefinition.NamedType("param", "uint8")), "functionName", Collections.emptyList(), "type", true);
        MethodSpec methodSpec = solidityFunctionWrapper.buildFunction(functionDefinition);
        // CHECKSTYLE:OFF
        String expected = "public org.web3j.protocol.core.RemoteCall<org.web3j.protocol.core.methods.response.TransactionReceipt> functionName(java.math.BigInteger param, java.math.BigInteger weiValue) {\n" + ((((("  final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(\n" + "      FUNC_FUNCTIONNAME, \n") + "      java.util.Arrays.<org.web3j.abi.datatypes.Type>asList(new org.web3j.abi.datatypes.generated.Uint8(param)), \n") + "      java.util.Collections.<org.web3j.abi.TypeReference<?>>emptyList());\n") + "  return executeRemoteCallTransaction(function, weiValue);\n") + "}\n");
        // CHECKSTYLE:ON
        Assert.assertThat(methodSpec.toString(), Is.is(expected));
    }

    @Test
    public void testBuildFunctionConstantSingleValueReturn() throws Exception {
        AbiDefinition functionDefinition = new AbiDefinition(true, Arrays.asList(new AbiDefinition.NamedType("param", "uint8")), "functionName", Arrays.asList(new AbiDefinition.NamedType("result", "int8")), "type", false);
        MethodSpec methodSpec = solidityFunctionWrapper.buildFunction(functionDefinition);
        // CHECKSTYLE:OFF
        String expected = "public org.web3j.protocol.core.RemoteCall<java.math.BigInteger> functionName(java.math.BigInteger param) {\n" + (((("  final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_FUNCTIONNAME, \n" + "      java.util.Arrays.<org.web3j.abi.datatypes.Type>asList(new org.web3j.abi.datatypes.generated.Uint8(param)), \n") + "      java.util.Arrays.<org.web3j.abi.TypeReference<?>>asList(new org.web3j.abi.TypeReference<org.web3j.abi.datatypes.generated.Int8>() {}));\n") + "  return executeRemoteCallSingleValueReturn(function, java.math.BigInteger.class);\n") + "}\n");
        // CHECKSTYLE:ON
        Assert.assertThat(methodSpec.toString(), Is.is(expected));
    }

    @Test
    public void testBuildFunctionConstantSingleValueRawListReturn() throws Exception {
        AbiDefinition functionDefinition = new AbiDefinition(true, Arrays.asList(new AbiDefinition.NamedType("param", "uint8")), "functionName", Arrays.asList(new AbiDefinition.NamedType("result", "address[]")), "type", false);
        MethodSpec methodSpec = solidityFunctionWrapper.buildFunction(functionDefinition);
        // CHECKSTYLE:OFF
        String expected = "public org.web3j.protocol.core.RemoteCall<java.util.List> functionName(java.math.BigInteger param) {\n" + (((((((((((("  final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_FUNCTIONNAME, \n" + "      java.util.Arrays.<org.web3j.abi.datatypes.Type>asList(new org.web3j.abi.datatypes.generated.Uint8(param)), \n") + "      java.util.Arrays.<org.web3j.abi.TypeReference<?>>asList(new org.web3j.abi.TypeReference<org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>>() {}));\n") + "  return new org.web3j.protocol.core.RemoteCall<java.util.List>(\n") + "      new java.util.concurrent.Callable<java.util.List>() {\n") + "        @java.lang.Override\n") + "        @java.lang.SuppressWarnings(\"unchecked\")\n") + "        public java.util.List call() throws java.lang.Exception {\n") + "          java.util.List<org.web3j.abi.datatypes.Type> result = (java.util.List<org.web3j.abi.datatypes.Type>) executeCallSingleValueReturn(function, java.util.List.class);\n") + "          return convertToNative(result);\n") + "        }\n") + "      });\n") + "}\n");
        // CHECKSTYLE:ON
        Assert.assertThat(methodSpec.toString(), Is.is(expected));
    }

    @Test
    public void testBuildFunctionConstantInvalid() throws Exception {
        AbiDefinition functionDefinition = new AbiDefinition(true, Arrays.asList(new AbiDefinition.NamedType("param", "uint8")), "functionName", Collections.emptyList(), "type", false);
        MethodSpec methodSpec = solidityFunctionWrapper.buildFunction(functionDefinition);
        // CHECKSTYLE:OFF
        String expected = "public void functionName(java.math.BigInteger param) {\n" + ("  throw new RuntimeException(\"cannot call constant function with void return type\");\n" + "}\n");
        // CHECKSTYLE:ON
        Assert.assertThat(methodSpec.toString(), Is.is(expected));
    }

    @Test
    public void testBuildFunctionConstantMultipleValueReturn() throws Exception {
        AbiDefinition functionDefinition = new AbiDefinition(true, Arrays.asList(new AbiDefinition.NamedType("param1", "uint8"), new AbiDefinition.NamedType("param2", "uint32")), "functionName", Arrays.asList(new AbiDefinition.NamedType("result1", "int8"), new AbiDefinition.NamedType("result2", "int32")), "type", false);
        MethodSpec methodSpec = solidityFunctionWrapper.buildFunction(functionDefinition);
        // CHECKSTYLE:OFF
        String expected = "public org.web3j.protocol.core.RemoteCall<org.web3j.tuples.generated.Tuple2<java.math.BigInteger, java.math.BigInteger>> functionName(java.math.BigInteger param1, java.math.BigInteger param2) {\n" + (((((((((((((("  final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_FUNCTIONNAME, \n" + "      java.util.Arrays.<org.web3j.abi.datatypes.Type>asList(new org.web3j.abi.datatypes.generated.Uint8(param1), \n") + "      new org.web3j.abi.datatypes.generated.Uint32(param2)), \n") + "      java.util.Arrays.<org.web3j.abi.TypeReference<?>>asList(new org.web3j.abi.TypeReference<org.web3j.abi.datatypes.generated.Int8>() {}, new org.web3j.abi.TypeReference<org.web3j.abi.datatypes.generated.Int32>() {}));\n") + "  return new org.web3j.protocol.core.RemoteCall<org.web3j.tuples.generated.Tuple2<java.math.BigInteger, java.math.BigInteger>>(\n") + "      new java.util.concurrent.Callable<org.web3j.tuples.generated.Tuple2<java.math.BigInteger, java.math.BigInteger>>() {\n") + "        @java.lang.Override\n") + "        public org.web3j.tuples.generated.Tuple2<java.math.BigInteger, java.math.BigInteger> call() throws java.lang.Exception {\n") + "          java.util.List<org.web3j.abi.datatypes.Type> results = executeCallMultipleValueReturn(function);\n") + "          return new org.web3j.tuples.generated.Tuple2<java.math.BigInteger, java.math.BigInteger>(\n") + "              (java.math.BigInteger) results.get(0).getValue(), \n") + "              (java.math.BigInteger) results.get(1).getValue());\n") + "        }\n") + "      });\n") + "}\n");
        // CHECKSTYLE:ON
        Assert.assertThat(methodSpec.toString(), Is.is(expected));
    }

    @Test
    public void testBuildEventConstantMultipleValueReturn() throws Exception {
        AbiDefinition.NamedType id = new AbiDefinition.NamedType("id", "string", true);
        AbiDefinition.NamedType fromAddress = new AbiDefinition.NamedType("from", "address");
        AbiDefinition.NamedType toAddress = new AbiDefinition.NamedType("to", "address");
        AbiDefinition.NamedType value = new AbiDefinition.NamedType("value", "uint256");
        AbiDefinition.NamedType message = new AbiDefinition.NamedType("message", "string");
        fromAddress.setIndexed(true);
        toAddress.setIndexed(true);
        AbiDefinition functionDefinition = new AbiDefinition(false, Arrays.asList(id, fromAddress, toAddress, value, message), "Transfer", new ArrayList(), "event", false);
        TypeSpec.Builder builder = TypeSpec.classBuilder("testClass");
        builder.addMethods(solidityFunctionWrapper.buildEventFunctions(functionDefinition, builder));
        // CHECKSTYLE:OFF
        String expected = "class testClass {\n" + (((((((((((((((((((((((((((((((((((((((((((((((((((((("  public static final org.web3j.abi.datatypes.Event TRANSFER_EVENT = new org.web3j.abi.datatypes.Event(\"Transfer\", \n" + "      java.util.Arrays.<org.web3j.abi.TypeReference<?>>asList(new org.web3j.abi.TypeReference<org.web3j.abi.datatypes.Utf8String>(true) {}, new org.web3j.abi.TypeReference<org.web3j.abi.datatypes.Address>(true) {}, new org.web3j.abi.TypeReference<org.web3j.abi.datatypes.Address>(true) {}, new org.web3j.abi.TypeReference<org.web3j.abi.datatypes.generated.Uint256>() {}, new org.web3j.abi.TypeReference<org.web3j.abi.datatypes.Utf8String>() {}));\n  ;\n\n") + "  public java.util.List<TransferEventResponse> getTransferEvents(org.web3j.protocol.core.methods.response.TransactionReceipt transactionReceipt) {\n") + "    java.util.List<org.web3j.tx.Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(TRANSFER_EVENT, transactionReceipt);\n") + "    java.util.ArrayList<TransferEventResponse> responses = new java.util.ArrayList<TransferEventResponse>(valueList.size());\n") + "    for (org.web3j.tx.Contract.EventValuesWithLog eventValues : valueList) {\n") + "      TransferEventResponse typedResponse = new TransferEventResponse();\n") + "      typedResponse.log = eventValues.getLog();\n") + "      typedResponse.id = (byte[]) eventValues.getIndexedValues().get(0).getValue();\n") + "      typedResponse.from = (java.lang.String) eventValues.getIndexedValues().get(1).getValue();\n") + "      typedResponse.to = (java.lang.String) eventValues.getIndexedValues().get(2).getValue();\n") + "      typedResponse.value = (java.math.BigInteger) eventValues.getNonIndexedValues().get(0).getValue();\n") + "      typedResponse.message = (java.lang.String) eventValues.getNonIndexedValues().get(1).getValue();\n") + "      responses.add(typedResponse);\n") + "    }\n") + "    return responses;\n") + "  }\n") + "\n") + "  public io.reactivex.Flowable<TransferEventResponse> transferEventFlowable(org.web3j.protocol.core.methods.request.EthFilter filter) {\n") + "    return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<org.web3j.protocol.core.methods.response.Log, TransferEventResponse>() {\n") + "      @java.lang.Override\n") + "      public TransferEventResponse apply(org.web3j.protocol.core.methods.response.Log log) {\n") + "        org.web3j.tx.Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(TRANSFER_EVENT, log);\n") + "        TransferEventResponse typedResponse = new TransferEventResponse();\n") + "        typedResponse.log = log;\n") + "        typedResponse.id = (byte[]) eventValues.getIndexedValues().get(0).getValue();\n") + "        typedResponse.from = (java.lang.String) eventValues.getIndexedValues().get(1).getValue();\n") + "        typedResponse.to = (java.lang.String) eventValues.getIndexedValues().get(2).getValue();\n") + "        typedResponse.value = (java.math.BigInteger) eventValues.getNonIndexedValues().get(0).getValue();\n") + "        typedResponse.message = (java.lang.String) eventValues.getNonIndexedValues().get(1).getValue();\n") + "        return typedResponse;\n") + "      }\n") + "    });\n") + "  }\n") + "\n") + "  public io.reactivex.Flowable<TransferEventResponse> transferEventFlowable(org.web3j.protocol.core.DefaultBlockParameter startBlock, org.web3j.protocol.core.DefaultBlockParameter endBlock) {\n") + "    org.web3j.protocol.core.methods.request.EthFilter filter = new org.web3j.protocol.core.methods.request.EthFilter(startBlock, endBlock, getContractAddress());\n") + "    filter.addSingleTopic(org.web3j.abi.EventEncoder.encode(TRANSFER_EVENT));\n") + "    return transferEventFlowable(filter);\n") + "  }\n") + "\n") + "  public static class TransferEventResponse {\n") + "    public org.web3j.protocol.core.methods.response.Log log;\n") + "\n") + "    public byte[] id;\n") + "\n") + "    public java.lang.String from;\n") + "\n") + "    public java.lang.String to;\n") + "\n") + "    public java.math.BigInteger value;\n") + "\n") + "    public java.lang.String message;\n") + "  }\n") + "}\n");
        // CHECKSTYLE:ON
        Assert.assertThat(builder.build().toString(), Is.is(expected));
    }

    @Test
    public void testBuildFuncNameConstants() throws Exception {
        AbiDefinition functionDefinition = new AbiDefinition(false, Arrays.asList(new AbiDefinition.NamedType("param", "uint8")), "functionName", Collections.emptyList(), "function", true);
        TypeSpec.Builder builder = TypeSpec.classBuilder("testClass");
        builder.addFields(solidityFunctionWrapper.buildFuncNameConstants(Collections.singletonList(functionDefinition)));
        // CHECKSTYLE:OFF
        String expected = "class testClass {\n" + ("  public static final java.lang.String FUNC_FUNCTIONNAME = \"functionName\";\n" + "}\n");
        // CHECKSTYLE:ON
        Assert.assertThat(builder.build().toString(), Is.is(expected));
    }
}

