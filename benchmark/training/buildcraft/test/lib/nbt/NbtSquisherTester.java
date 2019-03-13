package buildcraft.test.lib.nbt;


import NbtSquishConstants.BUILDCRAFT_V1;
import buildcraft.lib.nbt.NbtSquisher;
import java.io.IOException;
import java.util.Arrays;
import net.minecraft.nbt.NBTTagCompound;
import org.junit.Test;


public class NbtSquisherTester {
    private static final String[] IDS = new String[]{ // 
    "minecraft:dirt", "minecraft:cooked_steak", "minecraft:cooked_beef", "minecraft:stick"// 
    , "minecraft:diamond", "buildcraftcore:gear_wood", "buildcraftcore:gear_stone"// 
     };

    public static final NBTTagCompound nbt = NbtSquisherTester.genNbt(((64 * 64) * 64));

    public static final NBTTagCompound nbtSmall = NbtSquisherTester.genNbt(10);

    @Test
    public void printSimpleBytes() {
        byte[] bytes = NbtSquisher.squish(NbtSquisherTester.nbtSmall, BUILDCRAFT_V1);
        char[] chars = new char[32];
        int len = (bytes.length) / 32;
        if ((len * 32) < (bytes.length)) {
            len++;
        }
        for (int y = 0; y < len; y++) {
            for (int x = 0; x < 32; x++) {
                int idx = (y * 32) + x;
                if (idx >= (bytes.length)) {
                    Arrays.fill(chars, x, 32, ' ');
                    break;
                }
                byte val = bytes[idx];
                int ubyte = Byte.toUnsignedInt(val);
                char c = ((char) (ubyte));
                if ((!(Character.isDefined(c))) || (Character.isISOControl(c))) {
                    c = '.';
                }
                chars[x] = c;
                String hex = Integer.toHexString(ubyte);
                if ((hex.length()) < 2) {
                    hex = " " + hex;
                }
                System.out.print((hex + " "));
            }
            System.out.println(("|" + (new String(chars))));
        }
    }

    @Test
    public void testSimpleNBT() throws IOException {
        NbtSquisherTester.test(true, NbtSquisherTester.nbt);
    }
}

