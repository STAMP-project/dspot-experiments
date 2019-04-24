package org.xwiki.component.internal.namespace;


import org.junit.Assert;
import org.junit.Test;
import org.xwiki.component.namespace.Namespace;
import org.xwiki.component.namespace.NamespaceUtils;


public class AmplNamespaceUtilsTest {
    @Test(timeout = 10000)
    public void toNamespace_literalMutationString8851() throws Exception {
        Namespace o_toNamespace_literalMutationString8851__1 = NamespaceUtils.toNamespace(null);
        Assert.assertNull(o_toNamespace_literalMutationString8851__1);
        new Namespace(null, "namespace");
        Namespace o_toNamespace_literalMutationString8851__3 = NamespaceUtils.toNamespace("");
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString8851__3)).toString());
        Assert.assertEquals(23273, ((int) (((Namespace) (o_toNamespace_literalMutationString8851__3)).hashCode())));
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString8851__3)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString8851__3)).getType());
        new Namespace("type", "value");
        Namespace o_toNamespace_literalMutationString8851__5 = NamespaceUtils.toNamespace("type:value");
        Assert.assertEquals("type:value", ((Namespace) (o_toNamespace_literalMutationString8851__5)).toString());
        Assert.assertEquals(244293564, ((int) (((Namespace) (o_toNamespace_literalMutationString8851__5)).hashCode())));
        Assert.assertEquals("value", ((Namespace) (o_toNamespace_literalMutationString8851__5)).getValue());
        Assert.assertEquals("type", ((Namespace) (o_toNamespace_literalMutationString8851__5)).getType());
        new Namespace("t:pe", "val\\ue");
        Namespace o_toNamespace_literalMutationString8851__7 = NamespaceUtils.toNamespace("t\\:pe:val\\ue");
        Assert.assertEquals("t\\:pe:val\\ue", ((Namespace) (o_toNamespace_literalMutationString8851__7)).toString());
        Assert.assertEquals(-693755621, ((int) (((Namespace) (o_toNamespace_literalMutationString8851__7)).hashCode())));
        Assert.assertEquals("val\\ue", ((Namespace) (o_toNamespace_literalMutationString8851__7)).getValue());
        Assert.assertEquals("t:pe", ((Namespace) (o_toNamespace_literalMutationString8851__7)).getType());
        new Namespace("", "value");
        Namespace o_toNamespace_literalMutationString8851__9 = NamespaceUtils.toNamespace(":value");
        Assert.assertEquals(":value", ((Namespace) (o_toNamespace_literalMutationString8851__9)).toString());
        Assert.assertEquals(111995994, ((int) (((Namespace) (o_toNamespace_literalMutationString8851__9)).hashCode())));
        Assert.assertEquals("value", ((Namespace) (o_toNamespace_literalMutationString8851__9)).getValue());
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString8851__9)).getType());
        Assert.assertNull(o_toNamespace_literalMutationString8851__1);
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString8851__3)).toString());
        Assert.assertEquals(23273, ((int) (((Namespace) (o_toNamespace_literalMutationString8851__3)).hashCode())));
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString8851__3)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString8851__3)).getType());
        Assert.assertEquals("type:value", ((Namespace) (o_toNamespace_literalMutationString8851__5)).toString());
        Assert.assertEquals(244293564, ((int) (((Namespace) (o_toNamespace_literalMutationString8851__5)).hashCode())));
        Assert.assertEquals("value", ((Namespace) (o_toNamespace_literalMutationString8851__5)).getValue());
        Assert.assertEquals("type", ((Namespace) (o_toNamespace_literalMutationString8851__5)).getType());
        Assert.assertEquals("t\\:pe:val\\ue", ((Namespace) (o_toNamespace_literalMutationString8851__7)).toString());
        Assert.assertEquals(-693755621, ((int) (((Namespace) (o_toNamespace_literalMutationString8851__7)).hashCode())));
        Assert.assertEquals("val\\ue", ((Namespace) (o_toNamespace_literalMutationString8851__7)).getValue());
        Assert.assertEquals("t:pe", ((Namespace) (o_toNamespace_literalMutationString8851__7)).getType());
    }

    @Test(timeout = 10000)
    public void toNamespace_literalMutationString8863() throws Exception {
        Namespace o_toNamespace_literalMutationString8863__1 = NamespaceUtils.toNamespace(null);
        Assert.assertNull(o_toNamespace_literalMutationString8863__1);
        new Namespace(null, "namespace");
        Namespace o_toNamespace_literalMutationString8863__3 = NamespaceUtils.toNamespace("namespace");
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString8863__3)).toString());
        Assert.assertEquals(1252241476, ((int) (((Namespace) (o_toNamespace_literalMutationString8863__3)).hashCode())));
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString8863__3)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString8863__3)).getType());
        new Namespace("type", "value");
        Namespace o_toNamespace_literalMutationString8863__5 = NamespaceUtils.toNamespace("type:value");
        Assert.assertEquals("type:value", ((Namespace) (o_toNamespace_literalMutationString8863__5)).toString());
        Assert.assertEquals(244293564, ((int) (((Namespace) (o_toNamespace_literalMutationString8863__5)).hashCode())));
        Assert.assertEquals("value", ((Namespace) (o_toNamespace_literalMutationString8863__5)).getValue());
        Assert.assertEquals("type", ((Namespace) (o_toNamespace_literalMutationString8863__5)).getType());
        new Namespace("t:pe", "val\\ue");
        Namespace o_toNamespace_literalMutationString8863__7 = NamespaceUtils.toNamespace("");
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString8863__7)).toString());
        Assert.assertEquals(23273, ((int) (((Namespace) (o_toNamespace_literalMutationString8863__7)).hashCode())));
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString8863__7)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString8863__7)).getType());
        new Namespace("", "value");
        Namespace o_toNamespace_literalMutationString8863__9 = NamespaceUtils.toNamespace(":value");
        Assert.assertEquals(":value", ((Namespace) (o_toNamespace_literalMutationString8863__9)).toString());
        Assert.assertEquals(111995994, ((int) (((Namespace) (o_toNamespace_literalMutationString8863__9)).hashCode())));
        Assert.assertEquals("value", ((Namespace) (o_toNamespace_literalMutationString8863__9)).getValue());
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString8863__9)).getType());
        Assert.assertNull(o_toNamespace_literalMutationString8863__1);
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString8863__3)).toString());
        Assert.assertEquals(1252241476, ((int) (((Namespace) (o_toNamespace_literalMutationString8863__3)).hashCode())));
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString8863__3)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString8863__3)).getType());
        Assert.assertEquals("type:value", ((Namespace) (o_toNamespace_literalMutationString8863__5)).toString());
        Assert.assertEquals(244293564, ((int) (((Namespace) (o_toNamespace_literalMutationString8863__5)).hashCode())));
        Assert.assertEquals("value", ((Namespace) (o_toNamespace_literalMutationString8863__5)).getValue());
        Assert.assertEquals("type", ((Namespace) (o_toNamespace_literalMutationString8863__5)).getType());
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString8863__7)).toString());
        Assert.assertEquals(23273, ((int) (((Namespace) (o_toNamespace_literalMutationString8863__7)).hashCode())));
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString8863__7)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString8863__7)).getType());
    }

    @Test(timeout = 10000)
    public void toNamespace_literalMutationString8857() throws Exception {
        Namespace o_toNamespace_literalMutationString8857__1 = NamespaceUtils.toNamespace(null);
        Assert.assertNull(o_toNamespace_literalMutationString8857__1);
        new Namespace(null, "namespace");
        Namespace o_toNamespace_literalMutationString8857__3 = NamespaceUtils.toNamespace("namespace");
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString8857__3)).toString());
        Assert.assertEquals(1252241476, ((int) (((Namespace) (o_toNamespace_literalMutationString8857__3)).hashCode())));
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString8857__3)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString8857__3)).getType());
        new Namespace("type", "value");
        Namespace o_toNamespace_literalMutationString8857__5 = NamespaceUtils.toNamespace("");
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString8857__5)).toString());
        Assert.assertEquals(23273, ((int) (((Namespace) (o_toNamespace_literalMutationString8857__5)).hashCode())));
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString8857__5)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString8857__5)).getType());
        new Namespace("t:pe", "val\\ue");
        Namespace o_toNamespace_literalMutationString8857__7 = NamespaceUtils.toNamespace("t\\:pe:val\\ue");
        Assert.assertEquals("t\\:pe:val\\ue", ((Namespace) (o_toNamespace_literalMutationString8857__7)).toString());
        Assert.assertEquals(-693755621, ((int) (((Namespace) (o_toNamespace_literalMutationString8857__7)).hashCode())));
        Assert.assertEquals("val\\ue", ((Namespace) (o_toNamespace_literalMutationString8857__7)).getValue());
        Assert.assertEquals("t:pe", ((Namespace) (o_toNamespace_literalMutationString8857__7)).getType());
        new Namespace("", "value");
        Namespace o_toNamespace_literalMutationString8857__9 = NamespaceUtils.toNamespace(":value");
        Assert.assertEquals(":value", ((Namespace) (o_toNamespace_literalMutationString8857__9)).toString());
        Assert.assertEquals(111995994, ((int) (((Namespace) (o_toNamespace_literalMutationString8857__9)).hashCode())));
        Assert.assertEquals("value", ((Namespace) (o_toNamespace_literalMutationString8857__9)).getValue());
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString8857__9)).getType());
        Assert.assertNull(o_toNamespace_literalMutationString8857__1);
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString8857__3)).toString());
        Assert.assertEquals(1252241476, ((int) (((Namespace) (o_toNamespace_literalMutationString8857__3)).hashCode())));
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString8857__3)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString8857__3)).getType());
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString8857__5)).toString());
        Assert.assertEquals(23273, ((int) (((Namespace) (o_toNamespace_literalMutationString8857__5)).hashCode())));
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString8857__5)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString8857__5)).getType());
        Assert.assertEquals("t\\:pe:val\\ue", ((Namespace) (o_toNamespace_literalMutationString8857__7)).toString());
        Assert.assertEquals(-693755621, ((int) (((Namespace) (o_toNamespace_literalMutationString8857__7)).hashCode())));
        Assert.assertEquals("val\\ue", ((Namespace) (o_toNamespace_literalMutationString8857__7)).getValue());
        Assert.assertEquals("t:pe", ((Namespace) (o_toNamespace_literalMutationString8857__7)).getType());
    }

    @Test(timeout = 10000)
    public void toNamespace_literalMutationString8869() throws Exception {
        Namespace o_toNamespace_literalMutationString8869__1 = NamespaceUtils.toNamespace(null);
        Assert.assertNull(o_toNamespace_literalMutationString8869__1);
        new Namespace(null, "namespace");
        Namespace o_toNamespace_literalMutationString8869__3 = NamespaceUtils.toNamespace("namespace");
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString8869__3)).toString());
        Assert.assertEquals(1252241476, ((int) (((Namespace) (o_toNamespace_literalMutationString8869__3)).hashCode())));
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString8869__3)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString8869__3)).getType());
        new Namespace("type", "value");
        Namespace o_toNamespace_literalMutationString8869__5 = NamespaceUtils.toNamespace("type:value");
        Assert.assertEquals("type:value", ((Namespace) (o_toNamespace_literalMutationString8869__5)).toString());
        Assert.assertEquals(244293564, ((int) (((Namespace) (o_toNamespace_literalMutationString8869__5)).hashCode())));
        Assert.assertEquals("value", ((Namespace) (o_toNamespace_literalMutationString8869__5)).getValue());
        Assert.assertEquals("type", ((Namespace) (o_toNamespace_literalMutationString8869__5)).getType());
        new Namespace("t:pe", "val\\ue");
        Namespace o_toNamespace_literalMutationString8869__7 = NamespaceUtils.toNamespace("t\\:pe:val\\ue");
        Assert.assertEquals("t\\:pe:val\\ue", ((Namespace) (o_toNamespace_literalMutationString8869__7)).toString());
        Assert.assertEquals(-693755621, ((int) (((Namespace) (o_toNamespace_literalMutationString8869__7)).hashCode())));
        Assert.assertEquals("val\\ue", ((Namespace) (o_toNamespace_literalMutationString8869__7)).getValue());
        Assert.assertEquals("t:pe", ((Namespace) (o_toNamespace_literalMutationString8869__7)).getType());
        new Namespace("", "value");
        Namespace o_toNamespace_literalMutationString8869__9 = NamespaceUtils.toNamespace("");
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString8869__9)).toString());
        Assert.assertEquals(23273, ((int) (((Namespace) (o_toNamespace_literalMutationString8869__9)).hashCode())));
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString8869__9)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString8869__9)).getType());
        Assert.assertNull(o_toNamespace_literalMutationString8869__1);
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString8869__3)).toString());
        Assert.assertEquals(1252241476, ((int) (((Namespace) (o_toNamespace_literalMutationString8869__3)).hashCode())));
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString8869__3)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString8869__3)).getType());
        Assert.assertEquals("type:value", ((Namespace) (o_toNamespace_literalMutationString8869__5)).toString());
        Assert.assertEquals(244293564, ((int) (((Namespace) (o_toNamespace_literalMutationString8869__5)).hashCode())));
        Assert.assertEquals("value", ((Namespace) (o_toNamespace_literalMutationString8869__5)).getValue());
        Assert.assertEquals("type", ((Namespace) (o_toNamespace_literalMutationString8869__5)).getType());
        Assert.assertEquals("t\\:pe:val\\ue", ((Namespace) (o_toNamespace_literalMutationString8869__7)).toString());
        Assert.assertEquals(-693755621, ((int) (((Namespace) (o_toNamespace_literalMutationString8869__7)).hashCode())));
        Assert.assertEquals("val\\ue", ((Namespace) (o_toNamespace_literalMutationString8869__7)).getValue());
        Assert.assertEquals("t:pe", ((Namespace) (o_toNamespace_literalMutationString8869__7)).getType());
    }
}

