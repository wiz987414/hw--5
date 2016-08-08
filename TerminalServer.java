import authentification.CryptoHash;
import terminalExceptions.AmountOperationException;
import terminalExceptions.CheckAccountException;
import terminalExceptions.TerminalServerException;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Iterator;
import java.util.Objects;

class TerminalServer implements AutoCloseable {
    private final PinValidator checkAccount;

    TerminalServer() {
        PinValidator accountsList = new PinValidator();
        accountsList = accountsList.addAccount("1234");
        accountsList.getAccountsList().get(accountsList.getAccountsList().size() - 1).setAccount(1000000L);
        accountsList = accountsList.addAccount("1111");
        accountsList.getAccountsList().get(accountsList.getAccountsList().size() - 1).setAccount(500L);
        accountsList = accountsList.addAccount("0012");
        accountsList.getAccountsList().get(accountsList.getAccountsList().size() - 1).setAccount(40L);
        this.checkAccount = accountsList;
    }

    private TerminalServer(TerminalServer serverOldStage, String userNewPin) {
        this.checkAccount = new PinValidator(serverOldStage.getCheckAccount().getAccountsList(), userNewPin);
    }

    boolean checkPin(String pin) {
        boolean checkStatus = false;
        Iterator var3 = this.checkAccount.getAccountsList().iterator();

        while(var3.hasNext()) {
            CryptoHash account = (CryptoHash)var3.next();
            try {
                if(account.checkCryptoHash(pin, account.getSaultHexString(), account.getHashHexString())) {
                    checkStatus = true;
                }
            } catch (NoSuchAlgorithmException var6) {
                throw new CheckAccountException("Select valid algorithm title in field String algorythmPBKDF2 " +
                        "in class CryptoAuthentication, CryptoHash", var6, "Invalid crypto authentication algorithm");
            } catch (InvalidKeySpecException var7) {
                throw new CheckAccountException("Check parameters creating new object PBEKeySpec class in class " +
                        "CryptoAuthentication, CryptoHash", var7, "Invalid PBEKeySpec object");
            } catch (NoSuchFieldException var8) {
                throw new CheckAccountException("Check methods in classes CryptoAuthentication, CryptoHash on empty " +
                        "input arguments", var8, "Empty arguments in methods");
            }
        }

        return checkStatus;
    }

    Long getUserAccount(String pin) {
        Long userAccount = 0L;
        Iterator var3 = this.checkAccount.getAccountsList().iterator();
        if(var3.hasNext()) {
            CryptoHash account = (CryptoHash)var3.next();

            try {
                if(!account.checkCryptoHash(pin, account.getSaultHexString(), account.getHashHexString())) {
                    throw new CheckAccountException("Not exists user account on server", new NoSuchFieldException(),
                            "Not registered user");
                }

                userAccount = account.getAccount();
            } catch (NoSuchAlgorithmException var6) {
                throw new CheckAccountException("Select valid algorithm title in field String algorythmPBKDF2 in " +
                        "class CryptoAuthentication, CryptoHash", var6, "Invalid crypto authentication algorithm");
            } catch (InvalidKeySpecException var7) {
                throw new CheckAccountException("Check parameters creating new object PBEKeySpec class in class " +
                        "CryptoAuthentication, CryptoHash", var7, "Invalid PBEKeySpec object");
            } catch (NoSuchFieldException var8) {
                throw new CheckAccountException("Check methods in classes CryptoAuthentication, CryptoHash on empty " +
                        "input arguments", var8, "Empty arguments in methods");
            }
        }

        return userAccount;
    }

    Long setAmount(String pin, long amount, String modifyStatus) {
        Long userAccount = 0L;
        if(amount % 100L != 0L) {
            throw new AmountOperationException("Getting amount not multiply by 100", new IllegalArgumentException(),
                    "Incorrect getting amount");
        } else {
            Iterator var6 = this.checkAccount.getAccountsList().iterator();
            if(var6.hasNext()) {
                CryptoHash account = (CryptoHash)var6.next();

                try {
                    if(!account.checkCryptoHash(pin, account.getSaultHexString(), account.getHashHexString())) {
                        throw new CheckAccountException("Not exists user account on server", new NoSuchFieldException(),
                                "Not registered user");
                    }

                    if(Objects.equals(modifyStatus, "get")) {
                        if(amount > account.getAccount()) {
                            throw new AmountOperationException("Lov balance for getting", new IllegalArgumentException(), "Low balance");
                        }

                        account.setAccount(account.getAccount() - amount);
                    } else {
                        if(!Objects.equals(modifyStatus, "put")) {
                            throw new AmountOperationException("Incorrect operation status", new IllegalArgumentException(), "Incorrect operation");
                        }

                        account.setAccount(account.getAccount() + amount);
                    }

                    userAccount = account.getAccount();
                } catch (NoSuchAlgorithmException var9) {
                    throw new CheckAccountException("Select valid algorithm title in field String algorythmPBKDF2" +
                            " in class CryptoAuthentication, CryptoHash", var9, "Invalid crypto authentication algorithm");
                } catch (InvalidKeySpecException var10) {
                    throw new CheckAccountException("Check parameters creating new object PBEKeySpec class in class" +
                            " CryptoAuthentication, CryptoHash", var10, "Invalid PBEKeySpec object");
                } catch (NoSuchFieldException var11) {
                    throw new CheckAccountException("Check methods in classes CryptoAuthentication, CryptoHash on empty " +
                            "input arguments", var11, "Empty arguments in methods");
                }
            }

            return userAccount;
        }
    }

    private PinValidator getCheckAccount() {
        return this.checkAccount;
    }

    public void close() {
        try {
            this.checkAccount.getAccountsList().clear();
        } catch (Exception var2) {
            throw new TerminalServerException("Problem with server disconnect", var2, "Unable to disconnect with");
        }
    }
}
