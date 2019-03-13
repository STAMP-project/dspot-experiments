package net.bytebuddy.description.type;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class TypeDefinitionSortTest {
    private final TypeDefinition.Sort sort;

    private final boolean nonGeneric;

    private final boolean parameterized;

    private final boolean typeVariable;

    private final boolean genericArray;

    private final boolean wildcard;

    public TypeDefinitionSortTest(TypeDefinition.Sort sort, boolean nonGeneric, boolean parameterized, boolean typeVariable, boolean genericArray, boolean wildcard) {
        this.sort = sort;
        this.nonGeneric = nonGeneric;
        this.parameterized = parameterized;
        this.typeVariable = typeVariable;
        this.genericArray = genericArray;
        this.wildcard = wildcard;
    }

    @Test
    public void testNonGeneric() throws Exception {
        MatcherAssert.assertThat(sort.isNonGeneric(), CoreMatchers.is(nonGeneric));
    }

    @Test
    public void testParameterized() throws Exception {
        MatcherAssert.assertThat(sort.isParameterized(), CoreMatchers.is(parameterized));
    }

    @Test
    public void testTypeVariable() throws Exception {
        MatcherAssert.assertThat(sort.isTypeVariable(), CoreMatchers.is(typeVariable));
    }

    @Test
    public void testGenericArray() throws Exception {
        MatcherAssert.assertThat(sort.isGenericArray(), CoreMatchers.is(genericArray));
    }

    @Test
    public void testWildcard() throws Exception {
        MatcherAssert.assertThat(sort.isWildcard(), CoreMatchers.is(wildcard));
    }
}

