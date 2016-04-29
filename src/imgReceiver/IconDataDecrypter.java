package imgReceiver;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class IconDataDecrypter {

    private byte[] encryptedData;

    //1 IV value + 4 AES CBC decryption keys
    private byte[][] DecryptionData = new byte[5][];
    private int keyIndex;

    public IconDataDecrypter(byte[] encryptedData, int keyIndex){
        this.encryptedData = encryptedData;
        this.keyIndex = keyIndex;
    }

    public void setKeys(String path) throws Exception{
        Path paths = Paths.get(path);
        byte[] data = Files.readAllBytes(paths);

        for(int i = 0; i<5; )
            this.DecryptionData[i] = Arrays.copyOfRange(data, i*16, ++i*16);
    }

    public byte[] decryptData() throws Exception{
        byte[] decryptedData = new byte[encryptedData.length];

        Cipher decryptCipher = Cipher.getInstance("AES/CBC/NoPadding");
        SecretKey aesKey = new SecretKeySpec(DecryptionData[keyIndex+1], "AES");
        decryptCipher.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(DecryptionData[0]));

        return decryptCipher.doFinal(encryptedData);
    }

}
