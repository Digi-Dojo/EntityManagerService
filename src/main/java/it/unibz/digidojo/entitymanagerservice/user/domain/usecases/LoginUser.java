package it.unibz.digidojo.entitymanagerservice.user.domain.usecases;

import it.unibz.digidojo.entitymanagerservice.user.domain.UserBroadcaster;
import it.unibz.digidojo.entitymanagerservice.user.domain.model.User;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LoginUser {
    private static final String HASH_ALGORITHM = "SHA-256";
    private final UserBroadcaster userBroadcaster;

    /**
     * The password and mail address of the user are put together in a string before being hashed,
     * so we avoid having multiple users with the same hash because they chose the same password.
     *
     * @param password     the password to be hashed
     * @param emailAddress email address of the user, included in the hashing of the password
     * @return the string containing the hashed password
     * @throws RuntimeException if no Provider supports a MessageDigestSpi implementation for the specified algorithm
     */
    public String hashPassword(String password, String emailAddress) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            password = emailAddress + password;
            byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Check if the informed password belongs to the specific user.
     *
     * @param user            the user owner of the password
     * @param enteredPassword the password entered by the person trying to log in
     * @return the user
     * @throws IllegalArgumentException if the entered password isn't the one that belongs to the user
     */
    public User verifyPassword(User user, String enteredPassword) {
        String hashedEnteredPassword = hashPassword(enteredPassword, user.getEmailAddress());
        if (!hashedEnteredPassword.equals(user.getPassword())) {
            throw new IllegalArgumentException("Wrong password for this user");
        }
        userBroadcaster.emitUserLoggedIn(user);
        return user;
    }

    /**
     * Returns a string of double the size of the bytes array given as input, containing the hexadecimal
     * values of all the bytes in the array.
     * This method traverses the array translating every element in hex and appends it to a StringBuilder;
     * because the string is double the size of the array, for values that translate to a single digit hex,
     * a 0 is appended before the translated value
     *
     * @param bytes array of bytes generated by the digest of the password
     * @return a string of double the size of the bytes array, containing the hexadecimal values of all
     *         the bytes in the array
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
