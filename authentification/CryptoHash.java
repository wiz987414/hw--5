package authentification;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class CryptoHash extends CryptoAuthentication {
    private String hashHexString;
    private String saultHexString;
    private String checkHashString;
    private Long account;

    public CryptoHash() {
        this.hashHexString = "";
        this.saultHexString = "";
        this.checkHashString = "";
        this.account = 0L;
    }

    public CryptoHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchFieldException {
        super(password);
        this.hashHexString = this.toHexString(this.getStoredHash());
        this.saultHexString = this.toHexString(this.getStoredSault());
        this.checkHashString = "";
        this.account = 0L;
    }

    private void updateHashStrings() {
        if(this.getStoredHash() != null) {
            this.hashHexString = this.toHexString(this.getStoredHash());
        }

        if(this.getStoredSault() != null) {
            this.saultHexString = this.toHexString(this.getStoredSault());
        }

        if(this.getCheckedHash() != null) {
            this.checkHashString = this.toHexString(this.getCheckedHash());
        }

    }

    public void calcCryptoHash(String userPassword)
            throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchFieldException {
        super.calcCryptoHash(userPassword);
        this.updateHashStrings();
    }

    public boolean checkCryptoHash(String userPassword, String storedSault, String storedHash)
            throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchFieldException {
        boolean res = super.checkCryptoHash(userPassword, storedSault, storedHash);
        this.updateHashStrings();
        return res;
    }

    public void setAccount(Long account) {
        this.account = account;
    }

    public String getHashHexString() {
        return this.hashHexString;
    }

    public String getSaultHexString() {
        return this.saultHexString;
    }

    public String getCheckHashString() {
        return this.checkHashString;
    }

    public Long getAccount() {
        return this.account;
    }
}
