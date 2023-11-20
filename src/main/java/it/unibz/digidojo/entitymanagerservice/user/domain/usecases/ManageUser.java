package it.unibz.digidojo.entitymanagerservice.user.domain.usecases;

import it.unibz.digidojo.entitymanagerservice.user.domain.UserBroadcaster;
import it.unibz.digidojo.entitymanagerservice.user.domain.UserRepository;
import it.unibz.digidojo.entitymanagerservice.user.domain.model.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//TODO: Implement name update
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ManageUser {
    private final UserRepository userRepository;
    private final UserBroadcaster userBroadcaster;

    /**
     * @param name        name of the user
     * @param emailAddress email address of the user
     * @param password    password of the user
     * @return the newly created user
     * @throws IllegalArgumentException if a user with the provided mail address already exists
     */
    public User createUser(String name, String emailAddress, String password) {
        Optional<User> maybeUser = userRepository.findByEmailAddress(emailAddress);

        if (maybeUser.isPresent()) {
            throw new IllegalArgumentException("A user already exists with this mail address");
        }

        User user = userRepository.save(new User(name, emailAddress, password));

        userBroadcaster.emitUserCreated(user);

        return user;
    }

    /**
     * @param emailAddress email address of the user we want to delete
     * @return true if the operation is successful
     * @throws IllegalArgumentException if no user with the provided mail address is found
     */
    public boolean deleteUser(String emailAddress) {
        Optional<User> maybeUser = userRepository.findByEmailAddress(emailAddress);

        if (maybeUser.isEmpty()) {
            throw new IllegalArgumentException("User doesn't exist");
        }

        userRepository.delete(maybeUser.get());

        userBroadcaster.emitUserDeleted(maybeUser.get());

        return true;
    }

    /**
     * @param user        the user, whose password we want to change
     * @param newPassword the new password that will replace the current one
     * @return the newly updated user
     */
    public User updatePassword(User user, String newPassword) {
        user.setPassword(newPassword);
        userBroadcaster.emitUserUpdated(user);
        return userRepository.save(user);
    }

    /**
     * @param oldEmail the current email of the user
     * @param newEmail the new email that will replace the current one
     * @return the newly updated user
     * @throws IllegalArgumentException if a user with the new mail already exists, or if no user with the old email is found
     */
    public User updateUserMail(String oldEmail, String newEmail) {
        Optional<User> maybeUser = userRepository.findByEmailAddress(newEmail);

        if (maybeUser.isPresent()) {
            throw new IllegalArgumentException("User with mail address " + newEmail + " already exists");
        }

        maybeUser = userRepository.findByEmailAddress(oldEmail);
        if (maybeUser.isEmpty()) {
            throw new IllegalArgumentException("User with mail address " + oldEmail + " does not exist");
        }
        User user = maybeUser.get();

        user.setEmailAddress(newEmail);

        userBroadcaster.emitUserUpdated(user);

        return userRepository.save(user);
    }
}
