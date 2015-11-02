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

import java.security.Key;
import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.AesKey;
import org.jose4j.lang.JoseException;

public class JwtAuthUtil {
 
    public static String encodeJWT(String subject, String stringKey) throws Exception {
        JwtClaims claims = new JwtClaims();
        claims.setIssuer("Issuer");
        claims.setAudience("Audience");
        claims.setExpirationTimeMinutesInTheFuture(1);
        claims.setGeneratedJwtId();
        claims.setIssuedAtToNow(); 
        claims.setNotBeforeMinutesInThePast(2); 
        claims.setSubject(subject); 
        return encodeJWT(claims, stringKey);
    }

    public static String encodeJWT(JwtClaims claims, String stringKey) throws JoseException {
        Key key = new AesKey(stringKey.getBytes());
        JsonWebEncryption jwe = new JsonWebEncryption();
        jwe.setPayload(claims.toJson());
        jwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.A128KW);
        jwe.setEncryptionMethodHeaderParameter(ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256);
        jwe.setKey(key);
        String serializedJwe = jwe.getCompactSerialization();
        return serializedJwe ;
    }

    public static JwtClaims decodeJWT(String serializedJwt, String stringKey) throws InvalidJwtException {
        Key key = new AesKey(stringKey .getBytes());
        JwtConsumer consumer = new JwtConsumerBuilder()
            .setDecryptionKey( key)
            .setDisableRequireSignature()
            .setExpectedAudience("Audience")
            .build();
        JwtClaims receivedClaims = null ;
        receivedClaims = consumer.processToClaims(serializedJwt);
        return receivedClaims ;
    }
}
