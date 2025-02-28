package it.unibz.digidojo.entitymanagerservice.user.domain.usecases;

import it.unibz.digidojo.entitymanagerservice.user.domain.UserRepository;
import it.unibz.digidojo.entitymanagerservice.user.domain.model.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SearchUser {
    private final UserRepository userRepository;

    /**
     * @param emailAddress the email address of the user we want to find
     * @return the user with the provided mail address
     * @throws IllegalArgumentException if no user with the provided mail address is found
     */
    public User findByMailAddress(String emailAddress) {
        Optional<User> maybeUser = userRepository.findByEmailAddress(emailAddress);

        if (maybeUser.isEmpty()) {
            throw new IllegalArgumentException("No user found with the mail address: " + emailAddress);
        }
        return maybeUser.get();
    }

    /**
     * @param id id of the user we want to find
     * @return the user with the provided id
     * @throws IllegalArgumentException if no user with the provided id is found
     */
    public User findById(long id) {
        Optional<User> maybeUser = userRepository.findById(id);

        if (maybeUser.isEmpty()) {
            throw new IllegalArgumentException("No user found with id: " + id);
        }
        return maybeUser.get();
    }
}
