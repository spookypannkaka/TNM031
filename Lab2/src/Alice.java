import java.math.BigInteger;

public class Alice {
    public BigInteger ciphertext; // Encrypted public message
    private String message; // Private original message string

    // Constructor that takes the message and encrypts it using Bob's public key
    public Alice(String message, BigInteger e, BigInteger n) {
        this.message = message;
        this.ciphertext = encryptMessage(e, n);
    }

    // Encrypts the private message string using Bob's public key
    private BigInteger encryptMessage(BigInteger e, BigInteger n) {
        // Convert to integer and encrypt with m^e
        BigInteger m = new BigInteger(message.getBytes());
        return m.modPow(e, n);
    }
}