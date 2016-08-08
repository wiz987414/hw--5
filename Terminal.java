/**
 * Created by belyakov on 04.08.2016.
 * Base interface performing base terminal operations:
 * accountCheck() - receive personal information (String personalID, String personalPin) and returns account balance
 * getAmount() - receive personal information (String personalID, String personalPin) and returns operation status
 * putAmount() - receive personal information (String personalID, String personalPin) and returns operation status
 */
public interface Terminal {

    String accountCheck(String var1);

    String getAmount(String var1, long var2);

    String putAmount(String var1, long var2);
}
