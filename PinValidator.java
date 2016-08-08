import authentification.CryptoHash;
import terminalExceptions.CheckAccountException;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

class PinValidator {
    private final ArrayList<CryptoHash> clientsAccounts;

    PinValidator() {
        this.clientsAccounts = new ArrayList<>();
    }

    PinValidator(ArrayList<CryptoHash> existAccounts, String userPin) {
        this();
        this.clientsAccounts.addAll(existAccounts);

        try {
            this.clientsAccounts.add(new CryptoHash(userPin));
        } catch (NoSuchAlgorithmException var4) {
            throw new CheckAccountException("Select valid algorithm title in field String algorythmPBKDF2 " +
                    "in class CryptoAuthentication, CryptoHash", var4, "Invalid crypto authentication algorithm");
        } catch (InvalidKeySpecException var5) {
            throw new CheckAccountException("Check parameters creating new object PBEKeySpec class in class " +
                    "CryptoAuthentication, CryptoHash", var5, "Invalid PBEKeySpec object");
        } catch (NoSuchFieldException var6) {
            throw new CheckAccountException("Check methods in classes CryptoAuthentication, CryptoHash on " +
                    "empty input arguments", var6, "Empty arguments in methods");
        }
    }

    PinValidator addAccount(String newUserPin) {
        return new PinValidator(this.getAccountsList(), newUserPin);
    }

    ArrayList<CryptoHash> getAccountsList() {
        return this.clientsAccounts;
    }
}
