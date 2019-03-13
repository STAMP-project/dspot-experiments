

import java.io.IOException;
import org.junit.Test;


public class PropGen {
    @Test
    public void printProps() throws IOException {
        byte[] dll32 = load("sjkwinh32.dll");
        String hash32 = PropGen.digest(dll32, "SHA-256");
        byte[] dll64 = load("sjkwinh64.dll");
        String hash64 = PropGen.digest(dll64, "SHA-256");
        System.out.println(("dll32.hash: " + hash32));
        System.out.println(("dll32.len: " + (dll32.length)));
        System.out.println(("dll64.hash: " + hash64));
        System.out.println(("dll64.len: " + (dll64.length)));
    }
}

