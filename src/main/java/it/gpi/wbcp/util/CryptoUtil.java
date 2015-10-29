/*
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.

 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.gpi.wbcp.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class CryptoUtil {

    private static final Integer RSA_KEY_SIZE = 4096;
    private static final String CIPHER_AES_ALGORITHM = "AES/CTR/NoPadding";
    private static final int AES_KEY_SIZE = 256;
    private static final int IV_LENGTH = 16;

    private PublicKey publicKey;
    public PublicKey getPublicKey() { return publicKey; }

    private PrivateKey privateKey;
    public PrivateKey getPrivateKey() {	return privateKey; }

    public CryptoUtil() throws NoSuchAlgorithmException {
        this.generateKeys();
    }

    public CryptoUtil(PublicKey publicKey) {
        this.publicKey = publicKey;
    }
    public CryptoUtil(String publicKeyBase64) throws NoSuchAlgorithmException, InvalidKeySpecException, UnsupportedEncodingException {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec X509PublicKey = new X509EncodedKeySpec(StringUtil.decodeBase64(publicKeyBase64));
        this.publicKey = kf.generatePublic(X509PublicKey);
    }

    public CryptoUtil(PublicKey publicKey, PrivateKey privateKey) {
        this(publicKey);
        this.privateKey = privateKey;
    }
    public CryptoUtil(String publicKeyBase64, String privateKeyBase64) throws NoSuchAlgorithmException, InvalidKeySpecException, UnsupportedEncodingException {
        this(publicKeyBase64);

        KeyFactory kf = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec PKCS8EncodedPrivateKey = new PKCS8EncodedKeySpec(StringUtil.decodeBase64(privateKeyBase64));
        this.privateKey = kf.generatePrivate(PKCS8EncodedPrivateKey);
    }

    private void generateKeys() throws NoSuchAlgorithmException {
        final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(CryptoUtil.RSA_KEY_SIZE);
        final KeyPair kp = keyGen.generateKeyPair();
        publicKey = kp.getPublic();
        privateKey = kp.getPrivate();
    }

    public SecretKey newAESKey() throws NoSuchAlgorithmException {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(CryptoUtil.AES_KEY_SIZE);
        return kgen.generateKey();
    }

    public byte[] encrypt_RSA(byte[] text) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] cipherText;
        final Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        cipherText = cipher.doFinal(text);

        return cipherText;
    }

    public byte[] decrypt_RSA(byte[] text) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] dectyptedText;

        final Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        dectyptedText = cipher.doFinal(text);

        return dectyptedText;
    }

    public byte[] encrypt_AES(byte[] text, SecretKey aesKey) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException {
        Cipher aesCipher = Cipher.getInstance(CryptoUtil.CIPHER_AES_ALGORITHM);
        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[CryptoUtil.IV_LENGTH];
        random.nextBytes(iv);
        aesCipher.init(Cipher.ENCRYPT_MODE, aesKey, new IvParameterSpec(iv));

        ByteArrayInputStream bais = new ByteArrayInputStream(text);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        CipherOutputStream cos = new CipherOutputStream(baos, aesCipher);
        this.copy(bais, cos);

        byte[] response = new byte[baos.toByteArray().length + CryptoUtil.IV_LENGTH];
        System.arraycopy(baos.toByteArray(), 0, response, 0, baos.toByteArray().length);
        System.arraycopy(iv, 0, response, baos.toByteArray().length, CryptoUtil.IV_LENGTH);

        bais.close();
        cos.close();
        baos.close();

        return response;
    }

    public byte[] decrypt_AES(byte[] text, SecretKey aesKey) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException {
        byte[] message = new byte[text.length - CryptoUtil.IV_LENGTH];
        System.arraycopy(text, 0, message, 0, text.length - CryptoUtil.IV_LENGTH);
        byte[] iv = new byte[CryptoUtil.IV_LENGTH];
        System.arraycopy(text, text.length - CryptoUtil.IV_LENGTH, iv, 0, CryptoUtil.IV_LENGTH);

        Cipher aesCipher = Cipher.getInstance(CryptoUtil.CIPHER_AES_ALGORITHM);
        aesCipher.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(iv));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        CipherInputStream cbais = new CipherInputStream(new ByteArrayInputStream(message), aesCipher);
        this.copy(cbais, baos);

        byte[] response  = baos.toByteArray();

        cbais.close();
        baos.close();

        return response;
    }

    private void copy(InputStream is, OutputStream os) throws IOException {
        int i;
        byte[] b = new byte[1024];
        while((i = is.read(b)) != -1) {
          os.write(b, 0, i);
        }
    }
}
