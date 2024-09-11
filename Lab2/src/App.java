import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;

public class App {
    public static void main(String[] args) throws Exception {
        // Input a word to encrypt and decrypt
        System.out.println("Enter a string");
        String input = (new BufferedReader(new InputStreamReader(System.in))).readLine();

        // Bob generates his keys using the constructor
        Bob bob = new Bob();
        System.out.println("Bob's public key (n): " + bob.n);
        System.out.println("Bob's public key (e): " + bob.e);

        // Alice creates an instance with the message and Bob's public key
        Alice alice = new Alice(input, bob.e, bob.n);

        // Alice sends the encrypted message (ciphertext) to Bob
        BigInteger encryptedMessage = alice.ciphertext;
        System.out.println("Encrypted message (ciphertext): " + encryptedMessage);

        // Bob decrypts the message
        String decryptedMessage = bob.decryptMessage(encryptedMessage);
        System.out.println("Decrypted message: " + decryptedMessage);
    }
}
