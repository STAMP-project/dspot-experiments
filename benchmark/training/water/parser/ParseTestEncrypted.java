package water.parser;


import DecryptionTool.DecryptionSetup;
import java.io.File;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import water.TestUtil;
import water.api.schemas3.ParseSetupV3;
import water.fvec.Frame;
import water.fvec.Vec;


@RunWith(Parameterized.class)
public class ParseTestEncrypted extends TestUtil {
    @ClassRule
    public static TemporaryFolder tmp = new TemporaryFolder();

    private static String PLAINTEXT_FILE = "smalldata/demos/citibike_20k.csv";

    private static String KEYSTORE_TYPE = "JCEKS";// Note: need to use JCEKS, default JKS cannot store non-private keys!


    private static String MY_CIPHER_SPEC = "AES/ECB/PKCS5Padding";

    private static char[] MY_PASSWORD = "Password123".toCharArray();

    private static String MY_KEY_ALIAS = "secretKeyAlias";

    private static File _jks;

    @Parameterized.Parameter
    public String _encrypted_name;

    @Test
    public void testParseEncrypted() {
        Scope.enter();
        try {
            // 1. Upload the Keystore file
            Vec jksVec = Scope.track(TestUtil.makeNfsFileVec(ParseTestEncrypted._jks.getAbsolutePath()));
            // 2. Set Decryption Tool Parameters
            DecryptionTool.DecryptionSetup ds = new DecryptionTool.DecryptionSetup();
            ds._decrypt_tool_id = Key.make("aes_decrypt_tool");
            ds._keystore_id = jksVec._key;
            ds._key_alias = ParseTestEncrypted.MY_KEY_ALIAS;
            ds._keystore_type = ParseTestEncrypted.KEYSTORE_TYPE;
            ds._password = ParseTestEncrypted.MY_PASSWORD;
            ds._cipher_spec = ParseTestEncrypted.MY_CIPHER_SPEC;
            // 3. Instantiate & Install the Decryption Tool into DKV
            Keyed<DecryptionTool> dt = Scope.track_generic(DecryptionTool.make(ds));
            // 4. Load encrypted file into a ByteVec
            Vec encVec = Scope.track(TestUtil.makeNfsFileVec(new File(ParseTestEncrypted.tmp.getRoot(), _encrypted_name).getAbsolutePath()));
            // 5. Create Parse Setup with a given Decryption Tool
            ParseSetup ps = new ParseSetup(new ParseSetupV3()).setDecryptTool(dt._key);
            ParseSetup guessedSetup = ParseSetup.guessSetup(new Key[]{ encVec._key }, ps);
            Assert.assertEquals("aes_decrypt_tool", guessedSetup._decrypt_tool.toString());
            Assert.assertEquals("CSV", guessedSetup._parse_type.name());
            // 6. Parse encrypted dataset
            Key<Frame> fKey = Key.make("decrypted_frame");
            Frame decrypted = Scope.track(ParseDataset.parse(fKey, new Key[]{ encVec._key }, false, guessedSetup));
            // 7. Compare with source dataset
            Frame plaintext = Scope.track(TestUtil.parse_test_file(ParseTestEncrypted.PLAINTEXT_FILE));
            Assert.assertArrayEquals(plaintext._names, decrypted._names);
            for (String n : plaintext._names) {
                switch (plaintext.vec(n).get_type_str()) {
                    case "String" :
                        TestUtil.assertStringVecEquals(plaintext.vec(n), decrypted.vec(n));
                        break;
                    case "Enum" :
                        TestUtil.assertCatVecEquals(plaintext.vec(n), decrypted.vec(n));
                        break;
                    default :
                        TestUtil.assertVecEquals(plaintext.vec(n), decrypted.vec(n), 0.001);
                }
            }
        } finally {
            Scope.exit();
        }
    }
}

