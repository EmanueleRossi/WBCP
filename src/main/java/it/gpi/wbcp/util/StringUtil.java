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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StringUtil {
    
    private static final Logger logger = LogManager.getLogger();    
    
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        
    public static String stringifyStackTrace(Throwable e) {
        StringWriter sw = new StringWriter();    
        try {
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            sw.close();
        } catch (IOException ioe) {
            logger.error("stringifyException=|{}|", ioe.getMessage());
        }    
        return sw.toString();
    }
  
    public static String getSHA512Base64(String s) {        
        String response = null;        
        try {
            MessageDigest md;
            md = MessageDigest.getInstance("SHA-512");
            md.update(s.getBytes(StandardCharsets.UTF_8.name()));
            response = new String(Base64.getEncoder().encode(md.digest()), StandardCharsets.UTF_8.name());
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            logger.error("getSHA512Base64=|{}|", e.getMessage());                
        }
        return response;      
    }
    
    public static String readString(InputStream s) {
        String response = null;        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(s, StandardCharsets.UTF_8.name()))) {
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }
            response = out.toString();
        }
        catch (IOException e) {
            logger.error("readString=|{}|", e.getMessage());       
        }
        return response;
    }
                
    public static boolean isNullOrEmpty(String s) {
        return !(s != null && !s.isEmpty());
    }
    
    public static byte[] encodeBase64(byte[] byteArray) {
        return Base64.getEncoder().encode(byteArray);
    }    
    public static String getBase64EncodedUTF8String(byte[] byteArray) throws UnsupportedEncodingException {
        return new String(StringUtil.encodeBase64(byteArray), StandardCharsets.UTF_8.name());
    }
    
    public static byte[] decodeBase64(byte[] byteArray) {
        return Base64.getDecoder().decode(byteArray);	
    }
    public static byte[] decodeBase64(String base64String) throws UnsupportedEncodingException {
        return StringUtil.decodeBase64(base64String.getBytes(StandardCharsets.UTF_8.name()));
    }        
    public static String getBase64DecodedUTF8String(String base64String) throws UnsupportedEncodingException {
        return new String(StringUtil.decodeBase64(base64String), StandardCharsets.UTF_8.name());
    }
    
    public static boolean validateEmailPattern(String emailAddress) {
        Pattern compiledPattern = Pattern.compile(StringUtil.EMAIL_PATTERN);
        Matcher matcher = compiledPattern.matcher(emailAddress);
        return matcher.matches();
    } 
    
    public static String createDbLikeString(String s) {
        if (!s.startsWith("%")) {
            s = "%".concat(s);
        }
        if (!s.endsWith("%")) {
            s = s.concat("%");
        }
        return s;
    }
    
    public static String padLeft(String string, Integer lenght) {
        return String.format("%1$" + lenght + "s", string);  
    }    
}
