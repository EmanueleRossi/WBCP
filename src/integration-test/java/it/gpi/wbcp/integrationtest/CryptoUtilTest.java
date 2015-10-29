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
package it.gpi.wbcp.integrationtest;

import it.gpi.wbcp.util.CryptoUtil;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CryptoUtilTest {
	
    private CryptoUtil cu;
       
    @Before 
    public void initialize() {
        try {
            cu = new CryptoUtil();
        } catch (NoSuchAlgorithmException e) {
            fail("Exception!" + e.getMessage());
        }
    }    
    
    @Test
    public void testEncrypt_RSA_NoKeys() {
        byte[] message = "This message should be secrete!".getBytes();
        try {
            byte[] cryptedMessage = cu.encrypt_RSA(message);
            byte[] clearMessage = cu.decrypt_RSA(cryptedMessage);		

            assertArrayEquals("Match!", message,  clearMessage);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            fail("Exception!" + e.getMessage());
        }
    }

    @Test
    public void testEncrypt_RSA_WithKeys() {
        byte[] message = "This message should be secrete!".getBytes();
        try {
            PublicKey publicK = cu.getPublicKey();
            PrivateKey privateK = cu.getPrivateKey();			

            cu = new CryptoUtil(publicK, privateK);

            byte[] cryptedMessage =  cu.encrypt_RSA(message);
            byte[] clearMessage = cu.decrypt_RSA(cryptedMessage);		

            assertArrayEquals("Match!", message,  clearMessage);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            fail("Exception!" + e.getMessage());
        }		
    }
	
    @Test
    public void testEncrypt_AES() {
        byte[] message = "This message should be secret, but can be very very long!".getBytes();
        try {		
            SecretKey aesK = cu.newAESKey();

            byte[] cryptedMessage =  cu.encrypt_AES(message, aesK);
            byte[] clearMessage = cu.decrypt_AES(cryptedMessage, aesK);		

            assertArrayEquals("Match!", message,  clearMessage);

        } catch (NoSuchAlgorithmException | IOException | InvalidKeyException | NoSuchPaddingException | InvalidAlgorithmParameterException e) {
            fail("Exception!" + e.getMessage());
        }		
    }		
}
