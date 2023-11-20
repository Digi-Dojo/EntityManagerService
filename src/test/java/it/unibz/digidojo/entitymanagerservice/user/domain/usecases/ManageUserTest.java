package it.unibz.digidojo.entitymanagerservice.user.domain.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.unibz.digidojo.entitymanagerservice.user.domain.model.User;
import it.unibz.digidojo.entitymanagerservice.user.domain.UserBroadcaster;
import it.unibz.digidojo.entitymanagerservice.user.domain.UserRepository;
import it.unibz.digidojo.entitymanagerservice.util.NumberGenerator;

@ExtendWith(MockitoExtension.class)
public class ManageUserTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserBroadcaster userBroadcaster;
    private ManageUser useCase;

    @BeforeEach
    void setUp() {
        useCase = new ManageUser(userRepository, userBroadcaster);
    }

    @Test
    public void itCreatesAUser() {
        User user = new User("testUser", "TestUser@testmail.com", "testPassword");
        when(userRepository.findByEmailAddress(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(new User(user.getName(), user.getEmailAddress(), user.getPassword()));

        User effect = useCase.createUser(user.getName(), user.getEmailAddress(), user.getPassword());
        effect.setId(NumberGenerator.randomPositiveLong());

        assertThat(effect).isInstanceOf(User.class);
        assertThat(effect.getName()).isEqualTo(user.getName());
        assertThat(effect.getEmailAddress()).isEqualTo(user.getEmailAddress());
        assertThat(effect.getId())
                .isNotNull()
                .isGreaterThan(0);
    }

    @Test
    public void createUserThrowsExceptionForExistingUser() {
        User user = new User("testUser", "testUser@testmail.com", "testPassword");
        when(userRepository.findByEmailAddress(anyString())).thenReturn(
                Optional.of(new User("testUser", "testUser@testmail.com", "testPassword")));

        assertThatThrownBy(() -> useCase.createUser(user.getName(), user.getEmailAddress(), user.getPassword()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A user already exists with this mail address");
    }

    @Test
    public void UpdatePassword() {
        String userName = "testUser";
        String userMail = "TestUser@testmail.com";
        String userOldPassword = "testPassword";
        String userNewPassword = "NewPassword";
        User u = new User(userName, userMail, userOldPassword);
        u.setPassword(userNewPassword);
        when(userRepository.save(u)).thenReturn(u);

        User effect = useCase.updatePassword(u, userNewPassword);
        assertThat(effect.getPassword()).isEqualTo(userNewPassword);
    }

    @Test
    public void UpdateThrowsExceptionForNonExistingUser() {
        String userName = "testUser";
        String userMail = "TestUser@testmail.com";
        String userOldPassword = "testPassword";
        String userNewPassword = "NewPassword";

        User u = new User(userName, userMail, userOldPassword);
        when(userRepository.save(u)).thenThrow(new IllegalArgumentException());
        assertThatThrownBy(() -> useCase.updatePassword(u, userNewPassword))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void deleteUserThrowsExceptionForNonExistingUser() {
        String userMail = "TestUser@testmail.com";

        when(userRepository.findByEmailAddress(anyString())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> useCase.deleteUser(userMail))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User doesn't exist");
    }

    @Test
    public void UpdatesMailAddressThrowsExceptionForAlreadyExistingNewMailAddress() {
        User user = new User("testUser", "testUser@testmail.com", "testPassword");
        when(userRepository.findByEmailAddress(anyString())).thenReturn(
                Optional.of(new User("testUser", "testUser@testmail.com", "testPassword")));

        assertThatThrownBy(() -> useCase.updateUserMail(user.getEmailAddress(), user.getEmailAddress()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User with mail address testUser@testmail.com already exists");
    }

    @Test
    public void UpdatesMailAddressThrowsExceptionForNonExistingOldMailAddress() {
        User user = new User("testUser", "testUser@testmail.com", "testPassword");
        when(userRepository.findByEmailAddress(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.updateUserMail("NonExistingMail", user.getEmailAddress()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User with mail address NonExistingMail does not exist");
    }
}
