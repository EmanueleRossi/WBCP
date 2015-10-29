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

import edu.vt.middleware.password.CharacterCharacteristicsRule;
import edu.vt.middleware.password.DigitCharacterRule;
import edu.vt.middleware.password.LengthRule;
import edu.vt.middleware.password.LowercaseCharacterRule;
import edu.vt.middleware.password.MessageResolver;
import edu.vt.middleware.password.Password;
import edu.vt.middleware.password.PasswordData;
import edu.vt.middleware.password.PasswordValidator;
import edu.vt.middleware.password.QwertySequenceRule;
import edu.vt.middleware.password.Rule;
import edu.vt.middleware.password.RuleResult;
import edu.vt.middleware.password.UppercaseCharacterRule;
import edu.vt.middleware.password.WhitespaceRule;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public class PasswordUtil {
    
    private final PasswordValidator pwValidator;
    private final RuleResult pwValidatorResult;
        
    public PasswordUtil(String aPassword, Locale aLocale) {
         
        LengthRule lengthRule = new LengthRule(8, 16);
        WhitespaceRule whitespaceRule = new WhitespaceRule();

        CharacterCharacteristicsRule charRule = new CharacterCharacteristicsRule();
        charRule.getRules().add(new DigitCharacterRule(1));
        charRule.getRules().add(new UppercaseCharacterRule(1));
        charRule.getRules().add(new LowercaseCharacterRule(1));
        charRule.setNumberOfCharacteristics(3);
        
        QwertySequenceRule qwertySeqRule = new QwertySequenceRule();
        
        List<Rule> ruleList = new ArrayList<>();
        ruleList.add(lengthRule);
        ruleList.add(whitespaceRule);
        ruleList.add(charRule);
        ruleList.add(qwertySeqRule);
                
        ResourceBundle localizedErrorMessagesBundle = ResourceBundle.getBundle("PasswordUtil", aLocale);
        Properties localizedErrorMessagesProperties = this.convertToProperties(localizedErrorMessagesBundle);
        MessageResolver resolver = new MessageResolver(localizedErrorMessagesProperties);
        
        pwValidator = new PasswordValidator(resolver, ruleList);            
        
        PasswordData passwordData = new PasswordData(new Password(aPassword));
        pwValidatorResult = pwValidator.validate(passwordData);    
    }
    
   private Properties convertToProperties(ResourceBundle resource) {
        Properties properties = new Properties(); 
        Enumeration<String> keys = resource.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            properties.put(key, resource.getString(key));
        } 
        return properties;
    }        
    
    private String concatStringsListWSep(Iterable<String> strings, String separator) {
        StringBuilder sb = new StringBuilder();
        String sep = "";
        for(String s: strings) {
            sb.append(sep).append(s);
            sep = separator;
        }
        return sb.toString();                           
    }
   
    public boolean isAValidPassword() {                
        return pwValidatorResult.isValid();
    }   
    
    public List<String> getValidationResultMessages() {
        return pwValidator.getMessages(pwValidatorResult);
    }    
    public String getValidationResultMessagesConcat() {
        return this.concatStringsListWSep(this.getValidationResultMessages(), "\n");
    }    
}
