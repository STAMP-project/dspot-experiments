package org.web3j.abi;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.Fixed;
import org.web3j.abi.datatypes.Int;
import org.web3j.abi.datatypes.Ufixed;
import org.web3j.abi.datatypes.Uint;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Int64;
import org.web3j.abi.datatypes.generated.StaticArray2;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint64;


public class UtilsTest {
    @Test
    public void testGetTypeName() throws ClassNotFoundException {
        MatcherAssert.assertThat(Utils.getTypeName(new TypeReference<Uint>() {}), CoreMatchers.is("uint256"));
        MatcherAssert.assertThat(Utils.getTypeName(new TypeReference<Int>() {}), CoreMatchers.is("int256"));
        MatcherAssert.assertThat(Utils.getTypeName(new TypeReference<Ufixed>() {}), CoreMatchers.is("ufixed256"));
        MatcherAssert.assertThat(Utils.getTypeName(new TypeReference<Fixed>() {}), CoreMatchers.is("fixed256"));
        MatcherAssert.assertThat(Utils.getTypeName(new TypeReference<Uint64>() {}), CoreMatchers.is("uint64"));
        MatcherAssert.assertThat(Utils.getTypeName(new TypeReference<Int64>() {}), CoreMatchers.is("int64"));
        MatcherAssert.assertThat(Utils.getTypeName(new TypeReference<Bool>() {}), CoreMatchers.is("bool"));
        MatcherAssert.assertThat(Utils.getTypeName(new TypeReference<Utf8String>() {}), CoreMatchers.is("string"));
        MatcherAssert.assertThat(Utils.getTypeName(new TypeReference<DynamicBytes>() {}), CoreMatchers.is("bytes"));
        MatcherAssert.assertThat(Utils.getTypeName(new TypeReference.StaticArrayTypeReference<org.web3j.abi.datatypes.StaticArray<Uint>>(5) {}), CoreMatchers.is("uint256[5]"));
        MatcherAssert.assertThat(Utils.getTypeName(new TypeReference<org.web3j.abi.datatypes.DynamicArray<Uint>>() {}), CoreMatchers.is("uint256[]"));
    }

    @Test
    public void testTypeMap() throws Exception {
        final List<BigInteger> input = Arrays.asList(BigInteger.ZERO, BigInteger.ONE, BigInteger.TEN);
        MatcherAssert.assertThat(Utils.typeMap(input, Uint256.class), CoreMatchers.equalTo(Arrays.asList(new Uint256(BigInteger.ZERO), new Uint256(BigInteger.ONE), new Uint256(BigInteger.TEN))));
    }

    @Test
    public void testTypeMapNested() {
        List<BigInteger> innerList1 = Arrays.asList(BigInteger.valueOf(1), BigInteger.valueOf(2));
        List<BigInteger> innerList2 = Arrays.asList(BigInteger.valueOf(3), BigInteger.valueOf(4));
        final List<List<BigInteger>> input = Arrays.asList(innerList1, innerList2);
        StaticArray2<Uint256> staticArray1 = new StaticArray2(new Uint256(1), new Uint256(2));
        StaticArray2<Uint256> staticArray2 = new StaticArray2(new Uint256(3), new Uint256(4));
        List<StaticArray2<Uint256>> expectedList = Arrays.asList(staticArray1, staticArray2);
        MatcherAssert.assertThat(Utils.typeMap(input, StaticArray2.class, Uint256.class), CoreMatchers.equalTo(expectedList));
    }

    @Test
    public void testTypeMapEmpty() {
        MatcherAssert.assertThat(Utils.typeMap(new ArrayList<BigInteger>(), Uint256.class), CoreMatchers.equalTo(new ArrayList<Uint256>()));
    }
}

