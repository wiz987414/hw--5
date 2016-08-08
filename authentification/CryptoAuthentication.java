package authentification;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

abstract class CryptoAuthentication {
    // Block with constant declarations:
    // keyLength 		- integer length of key(sault)
    // algorythmPBKDF2	- string
    // iterations		- count of iterations of key generating
    private final static int keyLength;
    private final static int iterations;
    private final static String algorythmPBKDF2;
    // Block with class fields:
    private byte[] storedHash, storedSault, checkedHash;

    static {
        keyLength = 64;
        iterations = 5000;
        algorythmPBKDF2 = "PBKDF2WithHmacSHA1";                     // standard hash generation algorythm with PBKDF2 based by SHA1
        //algorythmPBKDF2 = "PBKDF2WithHmacSHA512";					// extended hash generation algorythm with PBKDF2 based by SHA512
    }

    CryptoAuthentication() {
        storedHash = new byte[keyLength];
        storedSault = new byte[keyLength];
        checkedHash = new byte[keyLength];
    }

    CryptoAuthentication(String password)
            throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchFieldException {
        storedHash = new byte[keyLength];
        storedSault = new byte[keyLength];
        checkedHash = new byte[keyLength];
        calcCryptoHash(password);
    }

    private boolean slowEquals(byte[] baseHash, byte[] gettingHash) {
        int diff = baseHash.length ^ gettingHash.length;
        for (int i = 0; i < baseHash.length && i < gettingHash.length; i++)
            diff |= baseHash[i] ^ gettingHash[i];
        return diff == 0;
    }

    private byte[] getCryptoHash(char[] password, byte[] salt, int iterations, int bytes)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, bytes * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(algorythmPBKDF2);
        return skf.generateSecret(spec).getEncoded();
    }

    private byte[] getSimpleHash(char[] password)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(algorythmPBKDF2);
        return skf.generateSecret(spec).getEncoded();
    }

    String toHexString(byte[] hashArray) {
        BigInteger integerArray = new BigInteger(1, hashArray);
        String hexString = integerArray.toString(16);
        int arrayLength = (hashArray.length * 2) - hexString.length();
        if (arrayLength > 0) {
            //System.out.println(arrayLength);
            return String.format("%0" + arrayLength + "d", 0) + hexString;
        } else
            return hexString;
    }

    private byte[] toByteArray(String hexHashString) {
        int arrayLength = hexHashString.length();
        byte[] data = new byte[arrayLength / 2];
        for (int i = 0; i < arrayLength; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexHashString.charAt(i), 16) << 4)
                    + Character.digit(hexHashString.charAt(i + 1), 16));
        }
        return data;
    }

    public void calcCryptoHash(String userPassword)
            throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchFieldException {
        char[] charsPassword = userPassword.toCharArray();
        if (charsPassword.length != 0) {
            SecureRandom secureRandom = new SecureRandom();
            byte[] saultPassword = new byte[keyLength];
            secureRandom.nextBytes(saultPassword);
            this.storedHash = getCryptoHash(charsPassword, saultPassword, iterations, keyLength);
            this.storedSault = saultPassword;
        } else throw new NoSuchFieldException();
    }

    public boolean checkCryptoHash(String userPassword, String storedSault, String storedHash)
            throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchFieldException {
        char[] charsPassword = userPassword.toCharArray();
        if (charsPassword.length != 0 && storedSault.length() != 0 && storedHash.length() != 0) {
            this.checkedHash = getCryptoHash(charsPassword, toByteArray(storedSault), iterations, keyLength);
            return slowEquals(toByteArray(storedHash), this.checkedHash);
        } else throw new NoSuchFieldException();
    }

    byte[] getStoredHash() {
        return this.storedHash;
    }

    byte[] getStoredSault() {
        return this.storedSault;
    }

    byte[] getCheckedHash() {
        return this.checkedHash;
    }


}
