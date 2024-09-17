import java.math.BigInteger;
import java.security.SecureRandom;

public class Bob {
    public BigInteger n; // n = p * q, public key
    public BigInteger e; // A large public prime number, public key
    private BigInteger p; // A large secret prime number
    private BigInteger q; // A large secret prime number
    private BigInteger d; // Private key

    // Constructor to generate keys directly when a Bob object is created
    public Bob() {
        this.p = generatePrime();
        this.q = generatePrime();
        this.n = p.multiply(q);  // Compute n = p * q

        // Choose e by generating a prime
        this.e = generatePrime();

        // Compute d, the modular inverse of e mod φ(n)
        BigInteger phiN = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
        this.d = e.modInverse(phiN);
    }

    // Function to generate prime numbers
    private static BigInteger generatePrime() {
        SecureRandom rand = new SecureRandom();
        return BigInteger.probablePrime(512, rand); // 512 bit size of prime
    }

    // Decrypts a message with Alice's ciphertext
    public String decryptMessage(BigInteger c) {
        // Compute the decrypted number with c^d, using Alice's public ciphertext and Bob's private key
        BigInteger decryptedM = c.modPow(this.d, this.n);

        // Return the number converted to a string with the original message
        return new String(decryptedM.toByteArray());
    }
}
