/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.typeresolution;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition;
import net.sourceforge.pmd.lang.java.typeresolution.typeinference.Bound;
import net.sourceforge.pmd.lang.java.typeresolution.typeinference.BoundOrConstraint;
import net.sourceforge.pmd.lang.java.typeresolution.typeinference.Constraint;
import net.sourceforge.pmd.lang.java.typeresolution.typeinference.TypeInferenceResolver;
import net.sourceforge.pmd.lang.java.typeresolution.typeinference.Variable;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.SuperClassA;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.SuperClassA2;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.SuperClassAOther;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.SuperClassAOther2;
import org.junit.Assert;
import org.junit.Test;


public class TypeInferenceTest {
    private JavaTypeDefinition number = JavaTypeDefinition.forClass(Number.class);

    private JavaTypeDefinition integer = JavaTypeDefinition.forClass(Integer.class);

    private JavaTypeDefinition primitiveInt = JavaTypeDefinition.forClass(int.class);

    private JavaTypeDefinition generic = JavaTypeDefinition.forClass(Map.class, number, integer);

    private Variable alpha = new Variable();

    private Variable beta = new Variable();

    private JavaTypeDefinition s = JavaTypeDefinition.forClass(int.class);

    private JavaTypeDefinition t = JavaTypeDefinition.forClass(double.class);

    @Test
    public void testEqualityReduceProperVsProper() {
        // If S and T are proper types, the constraint reduces to true if S is the same as T (?4.3.4), and false
        // otherwise.
        Assert.assertTrue(reduce().isEmpty());
        Assert.assertNull(reduce());
        // Otherwise, if S or T is the null type, the constraint reduces to false. TODO
    }

    @Test
    public void testEqualityReduceVariableVsNotPrimitive() {
        // Otherwise, if S is an inference variable, ?, and T is not a primitive type, the constraint reduces to
        // the bound ? = T.
        List<BoundOrConstraint> result = new Constraint(alpha, number, EQUALITY).reduce();
        Assert.assertEquals(1, result.size());
        testBoundOrConstraint(result.get(0), alpha, number, EQUALITY, Bound.class);
    }

    @Test
    public void testEqualityReduceNotPrimitiveVsVariable() {
        // Otherwise, if T is an inference variable, ?, and S is not a primitive type, the constraint reduces
        // to the bound S = ?.
        List<BoundOrConstraint> result = new Constraint(number, alpha, EQUALITY).reduce();
        Assert.assertEquals(1, result.size());
        testBoundOrConstraint(result.get(0), number, alpha, EQUALITY, Bound.class);
        result = reduce();
        Assert.assertEquals(1, result.size());
        testBoundOrConstraint(result.get(0), alpha, beta, EQUALITY, Bound.class);
    }

    @Test
    public void testEqualityReduceSameErasure() {
        // Otherwise, if S and T are class or interface types with the same erasure, where S has type
        // arguments B1, ..., Bn and T has type arguments A1, ..., An, the constraint reduces to the
        // following new constraints: for all i (1 ? i ? n), ?Bi = Ai?.
        List<BoundOrConstraint> result = new Constraint(generic, generic, EQUALITY).reduce();
        Assert.assertEquals(2, result.size());
        testBoundOrConstraint(result.get(0), number, number, EQUALITY, Constraint.class);
        testBoundOrConstraint(result.get(1), integer, integer, EQUALITY, Constraint.class);
    }

    @Test
    public void testEqualityReduceArrayTypes() {
        // Otherwise, if S and T are array types, S'[] and T'[], the constraint reduces to ?S' = T'?.
        List<BoundOrConstraint> result = new Constraint(JavaTypeDefinition.forClass(Number[].class), JavaTypeDefinition.forClass(Integer[].class), EQUALITY).reduce();
        Assert.assertEquals(1, result.size());
        testBoundOrConstraint(result.get(0), number, integer, EQUALITY, Constraint.class);
    }

    @Test
    public void testSubtypeReduceProperVsProper() {
        // A constraint formula of the form ?S <: T? is reduced as follows:
        // If S and T are proper types, the constraint reduces to true if S is a subtype of T (?4.10),
        // and false otherwise.
        List<BoundOrConstraint> result = new Constraint(integer, number, SUBTYPE).reduce();
        Assert.assertEquals(0, result.size());
        result = reduce();
        Assert.assertNull(result);
        // Otherwise, if S is the null type, the constraint reduces to true. TODO
        // Otherwise, if T is the null type, the constraint reduces to false. TODO
    }

    @Test
    public void testSubtypeReduceVariableVsAny() {
        // Otherwise, if S is an inference variable, ?, the constraint reduces to the bound ? <: T.
        List<BoundOrConstraint> result = new Constraint(alpha, integer, SUBTYPE).reduce();
        Assert.assertEquals(1, result.size());
        testBoundOrConstraint(result.get(0), alpha, integer, SUBTYPE, Bound.class);
    }

    @Test
    public void testSubtypeReduceAnyVsVariable() {
        // Otherwise, if T is an inference variable, ?, the constraint reduces to the bound S <: ?.
        List<BoundOrConstraint> result = new Constraint(integer, alpha, SUBTYPE).reduce();
        Assert.assertEquals(1, result.size());
        testBoundOrConstraint(result.get(0), integer, alpha, SUBTYPE, Bound.class);
        result = reduce();
        Assert.assertEquals(1, result.size());
        testBoundOrConstraint(result.get(0), alpha, beta, SUBTYPE, Bound.class);
    }

    @Test
    public void testLooseInvocationProperVsProper() {
        // A constraint formula of the form ?S ? T? is reduced as follows:
        // If S and T are proper types, the constraint reduces to true if S is compatible in a loose invocation
        // context with T (?5.3), and false otherwise.
        List<BoundOrConstraint> result = new Constraint(number, integer, LOOSE_INVOCATION).reduce();
        Assert.assertNull(result);
        result = reduce();
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testLooseInvocationLeftBoxing() {
        // Otherwise, if S is a primitive type, let S' be the result of applying boxing conversion (?5.1.7) to S.
        // Then the constraint reduces to ?S' ? T?.
        List<BoundOrConstraint> result = new Constraint(primitiveInt, number, LOOSE_INVOCATION).reduce();
        Assert.assertEquals(1, result.size());
        testBoundOrConstraint(result.get(0), integer, number, LOOSE_INVOCATION, Constraint.class);
    }

    @Test
    public void testLooseInvocationRightBoxing() {
        // Otherwise, if T is a primitive type, let T' be the result of applying boxing conversion (?5.1.7) to T.
        // Then the constraint reduces to ?S = T'?.
        List<BoundOrConstraint> result = new Constraint(number, primitiveInt, LOOSE_INVOCATION).reduce();
        Assert.assertEquals(1, result.size());
        testBoundOrConstraint(result.get(0), number, integer, EQUALITY, Constraint.class);
        // Otherwise, if T is a parameterized type of the form G<T1, ..., Tn>, and there exists no type of the
        // form G<...> that is a supertype of S, but the raw type G is a supertype of S, then the constraint
        // reduces to true. TODO
        // Otherwise, if T is an array type of the form G<T1, ..., Tn>[]k, and there exists no type of the form
        // G<...>[]k that is a supertype of S, but the raw type G[]k is a supertype of S, then the constraint
        // reduces to true. (The notation []k indicates an array type of k dimensions.) TODO
    }

    @Test
    public void testLooseInvocationAnythingElse() {
        // Otherwise, the constraint reduces to ?S<:T?.
        List<BoundOrConstraint> result = new Constraint(number, alpha, LOOSE_INVOCATION).reduce();
        Assert.assertEquals(1, result.size());
        testBoundOrConstraint(result.get(0), number, alpha, SUBTYPE, Constraint.class);
        result = reduce();
        Assert.assertEquals(1, result.size());
        testBoundOrConstraint(result.get(0), alpha, number, SUBTYPE, Constraint.class);
    }

    @Test
    public void testContainmentReduceTypeVsType() {
        // A constraint formula of the form ?S <= T?, where S and T are type arguments (?4.5.1), is reduced as
        // follows:
        // If T is a type: // If S is a type, the constraint reduces to ?S = T?.
        List<BoundOrConstraint> result = new Constraint(number, integer, CONTAINS).reduce();
        Assert.assertEquals(1, result.size());
        testBoundOrConstraint(result.get(0), number, integer, EQUALITY, Constraint.class);
        // If T is a type: // If S is a wildcard, the constraint reduces to false. TODO
        // If T is a wildcard of the form ?, the constraint reduces to true. TODO
        // If T is a wildcard of the form ? extends T': TODO
        // If T is a wildcard of the form ? super T': TODO
    }

    @Test
    public void testIncorporationEqualityAndEquality() {
        List<Constraint> result;
        // ### Original rule 1. : ? = S and ? = T imply ?S = T?
        result = incorporationResult(new Bound(alpha, s, EQUALITY), new Bound(alpha, t, EQUALITY));
        Assert.assertEquals(1, result.size());
        testBoundOrConstraint(result.get(0), s, t, EQUALITY, Constraint.class);
        // ? = S and T = ? imply ?S = T?
        result = incorporationResult(new Bound(alpha, s, EQUALITY), new Bound(t, alpha, EQUALITY));
        Assert.assertEquals(1, result.size());
        testBoundOrConstraint(result.get(0), s, t, EQUALITY, Constraint.class);
        // S = ? and ? = T imply ?S = T?
        result = incorporationResult(new Bound(s, alpha, EQUALITY), new Bound(alpha, t, EQUALITY));
        Assert.assertEquals(1, result.size());
        testBoundOrConstraint(result.get(0), s, t, EQUALITY, Constraint.class);
        // S = ? and T = ? imply ?S = T?
        result = incorporationResult(new Bound(s, alpha, EQUALITY), new Bound(t, alpha, EQUALITY));
        Assert.assertEquals(1, result.size());
        testBoundOrConstraint(result.get(0), s, t, EQUALITY, Constraint.class);
    }

    @Test
    public void testIncorporationEqualityAndSubtypeLeftVariable() {
        List<Constraint> result;
        // ### Original rule 2. : ? = S and ? <: T imply ?S <: T?
        result = incorporationResult(new Bound(alpha, s, EQUALITY), new Bound(alpha, t, SUBTYPE));
        Assert.assertEquals(1, result.size());
        testBoundOrConstraint(result.get(0), s, t, SUBTYPE, Constraint.class);
        // S = ? and ? <: T imply ?S <: T?
        result = incorporationResult(new Bound(s, alpha, EQUALITY), new Bound(alpha, t, SUBTYPE));
        Assert.assertEquals(1, result.size());
        testBoundOrConstraint(result.get(0), s, t, SUBTYPE, Constraint.class);
        // ? <: T and ? = S imply ?S <: T?
        result = incorporationResult(new Bound(alpha, t, SUBTYPE), new Bound(alpha, s, EQUALITY));
        Assert.assertEquals(1, result.size());
        testBoundOrConstraint(result.get(0), s, t, SUBTYPE, Constraint.class);
        // ? <: T and S = ? imply ?S <: T?
        result = incorporationResult(new Bound(alpha, t, SUBTYPE), new Bound(s, alpha, EQUALITY));
        Assert.assertEquals(1, result.size());
        testBoundOrConstraint(result.get(0), s, t, SUBTYPE, Constraint.class);
    }

    @Test
    public void testIncorporationEqualityAndSubtypeRightVariable() {
        List<Constraint> result;
        // ### Original rule 3. : ? = S and T <: ? imply ?T <: S?
        result = incorporationResult(new Bound(alpha, s, EQUALITY), new Bound(t, alpha, SUBTYPE));
        Assert.assertEquals(1, result.size());
        testBoundOrConstraint(result.get(0), t, s, SUBTYPE, Constraint.class);
        // S = ? and T <: ? imply ?T <: S?
        result = incorporationResult(new Bound(s, alpha, EQUALITY), new Bound(t, alpha, SUBTYPE));
        Assert.assertEquals(1, result.size());
        testBoundOrConstraint(result.get(0), t, s, SUBTYPE, Constraint.class);
        // T <: ? and ? = S imply ?T <: S?
        result = incorporationResult(new Bound(t, alpha, SUBTYPE), new Bound(alpha, s, EQUALITY));
        Assert.assertEquals(1, result.size());
        testBoundOrConstraint(result.get(0), t, s, SUBTYPE, Constraint.class);
        // T <: ? and S = ? imply ?T <: S?
        result = incorporationResult(new Bound(t, alpha, SUBTYPE), new Bound(s, alpha, EQUALITY));
        Assert.assertEquals(1, result.size());
        testBoundOrConstraint(result.get(0), t, s, SUBTYPE, Constraint.class);
    }

    @Test
    public void testIncorporationSubtypeAndSubtype() {
        List<Constraint> result;
        // ### Original rule 4. : S <: ? and ? <: T imply ?S <: T?
        result = incorporationResult(new Bound(s, alpha, EQUALITY), new Bound(alpha, t, SUBTYPE));
        Assert.assertEquals(1, result.size());
        testBoundOrConstraint(result.get(0), s, t, SUBTYPE, Constraint.class);
        // ? <: T and S <: ? imply ?S <: T?
        result = incorporationResult(new Bound(alpha, t, SUBTYPE), new Bound(s, alpha, EQUALITY));
        Assert.assertEquals(1, result.size());
        testBoundOrConstraint(result.get(0), s, t, SUBTYPE, Constraint.class);
    }

    @Test
    public void testErasedCandidateSet() {
        List<JavaTypeDefinition> types = new ArrayList<>();
        types.add(JavaTypeDefinition.forClass(List.class));
        types.add(JavaTypeDefinition.forClass(Set.class));
        Set<Class<?>> erasedCandidate = TypeInferenceResolver.getErasedCandidateSet(types);
        Assert.assertEquals(3, erasedCandidate.size());
        Assert.assertTrue(erasedCandidate.contains(Object.class));
        Assert.assertTrue(erasedCandidate.contains(Collection.class));
        Assert.assertTrue(erasedCandidate.contains(Iterable.class));
        Set<Class<?>> emptySet = TypeInferenceResolver.getErasedCandidateSet(Collections.<JavaTypeDefinition>emptyList());
        Assert.assertNotNull(emptySet);
        Assert.assertEquals(0, emptySet.size());
    }

    @Test
    public void testMinimalErasedCandidateSet() {
        Set<Class<?>> minimalSet = TypeInferenceResolver.getMinimalErasedCandidateSet(JavaTypeDefinition.forClass(List.class).getErasedSuperTypeSet());
        Assert.assertEquals(1, minimalSet.size());
        Assert.assertTrue(minimalSet.contains(List.class));
    }

    @Test
    public void testLeastUpperBound() {
        List<JavaTypeDefinition> lowerBounds = new ArrayList<>();
        lowerBounds.add(JavaTypeDefinition.forClass(SuperClassA.class));
        lowerBounds.add(JavaTypeDefinition.forClass(SuperClassAOther.class));
        lowerBounds.add(JavaTypeDefinition.forClass(SuperClassAOther2.class));
        Assert.assertEquals(JavaTypeDefinition.forClass(SuperClassA2.class), TypeInferenceResolver.lub(lowerBounds));
    }

    @Test
    public void testResolution() {
        List<Bound> bounds = new ArrayList<>();
        bounds.add(new Bound(JavaTypeDefinition.forClass(SuperClassA.class), alpha, SUBTYPE));
        bounds.add(new Bound(JavaTypeDefinition.forClass(SuperClassAOther.class), alpha, SUBTYPE));
        Map<Variable, JavaTypeDefinition> result = TypeInferenceResolver.resolveVariables(bounds);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(JavaTypeDefinition.forClass(SuperClassA2.class), result.get(alpha));
    }
}

