package jadx.tests.integration.others;


import com.android.dex.Code;
import com.android.dx.io.instructions.DecodedInstruction;
import com.android.dx.io.instructions.ShortArrayCodeInput;
import jadx.core.dex.nodes.ClassNode;
import jadx.core.dex.nodes.DexNode;
import jadx.core.dex.nodes.MethodNode;
import jadx.core.utils.exceptions.DecodeException;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import java.io.EOFException;
import org.junit.Assert;
import org.junit.Test;


public class TestLoopInTry2 extends IntegrationTest {
    public static class TestCls {
        private MethodNode method;

        private DexNode dex;

        private DecodedInstruction[] insnArr;

        public void test(Code mthCode) throws DecodeException {
            short[] encodedInstructions = mthCode.getInstructions();
            int size = encodedInstructions.length;
            DecodedInstruction[] decoded = new DecodedInstruction[size];
            ShortArrayCodeInput in = new ShortArrayCodeInput(encodedInstructions);
            try {
                while (in.hasMore()) {
                    decoded[in.cursor()] = DecodedInstruction.decode(in);
                } 
            } catch (EOFException e) {
                throw new DecodeException(method, "", e);
            }
            insnArr = decoded;
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestLoopInTry2.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("try {"));
        Assert.assertThat(code, JadxMatchers.containsOne("while (in.hasMore()) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("decoded[in.cursor()] = DecodedInstruction.decode(in);"));
        Assert.assertThat(code, JadxMatchers.containsOne("} catch (EOFException e) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("throw new DecodeException"));
    }
}

