package jadx.tests.functional;


import jadx.core.dex.instructions.args.ArgType;
import jadx.core.dex.nodes.DexNode;
import jadx.core.utils.exceptions.DecodeException;
import java.io.IOException;
import org.junit.Test;


public class TypeMergeTest {
    private DexNode dex;

    @Test
    public void testMerge() throws DecodeException, IOException {
        first(INT, INT);
        first(BOOLEAN, INT);
        reject(INT, LONG);
        first(INT, UNKNOWN);
        reject(INT, UNKNOWN_OBJECT);
        first(INT, NARROW);
        first(CHAR, INT);
        check(unknown(PrimitiveType.INT, PrimitiveType.BOOLEAN, PrimitiveType.FLOAT), unknown(PrimitiveType.INT, PrimitiveType.BOOLEAN), unknown(PrimitiveType.INT, PrimitiveType.BOOLEAN));
        check(unknown(PrimitiveType.INT, PrimitiveType.FLOAT), unknown(PrimitiveType.INT, PrimitiveType.BOOLEAN), INT);
        check(unknown(PrimitiveType.INT, PrimitiveType.OBJECT), unknown(PrimitiveType.OBJECT, PrimitiveType.ARRAY), unknown(PrimitiveType.OBJECT));
        ArgType objExc = object("java.lang.Exception");
        ArgType objThr = object("java.lang.Throwable");
        ArgType objIO = object("java.io.IOException");
        ArgType objArr = object("java.lang.ArrayIndexOutOfBoundsException");
        ArgType objList = object("java.util.List");
        first(objExc, objExc);
        check(objExc, objList, OBJECT);
        first(objExc, OBJECT);
        check(objExc, objThr, objThr);
        check(objIO, objArr, objExc);
        ArgType generic = genericType("T");
        first(generic, objExc);
    }

    @Test
    public void testArrayMerge() {
        check(array(INT), array(BYTE), ArgType.OBJECT);
        first(array(INT), array(INT));
        first(array(STRING), array(STRING));
        first(OBJECT, array(INT));
        first(OBJECT, array(STRING));
        first(array(unknown(PrimitiveType.INT, PrimitiveType.FLOAT)), unknown(PrimitiveType.ARRAY));
    }
}

