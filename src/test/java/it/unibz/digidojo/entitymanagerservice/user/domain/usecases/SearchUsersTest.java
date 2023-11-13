package it.unibz.digidojo.entitymanagerservice.user.domain.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.unibz.digidojo.entitymanagerservice.user.domain.User;
import it.unibz.digidojo.entitymanagerservice.user.domain.UserRepository;

@ExtendWith(MockitoExtension.class)
public class SearchUsersTest {
    @Mock
    private UserRepository userRepository;
    private SearchUser useCase;

    @BeforeEach
    void setUp() {
        useCase = new SearchUser(userRepository);
    }

    @Test
    public void findByMailAddressReturnsUser() {
        when(userRepository.findByEmailAddress("malarcher@unibz.it"))
                .thenReturn(Optional.of(new User("Matteo", "malarcher@unibz.it", "password")));

        User result = useCase.findByMailAddress("malarcher@unibz.it");
        assertThat(result).isInstanceOf(User.class);
        assertThat(result.getEmailAddress()).isEqualTo("malarcher@unibz.it");
        assertThat(result.getName()).isEqualTo("Matteo");
        assertThat(result.getPassword()).isEqualTo("password");
    }

    @Test
    public void findByMailAddressThrowsIllegalArgumentException() {
        when(userRepository.findByEmailAddress(anyString())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> useCase.findByMailAddress("malarcher@unibz.it"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No user found with the mail address: malarcher@unibz.it");
    }

    @Test
    public void findByIdReturnsUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User("Matteo", "malarcher@unibz.it", "password")));

        User result = useCase.findById(1);

        assertThat(result).isInstanceOf(User.class);
        assertThat(result.getEmailAddress()).isEqualTo("malarcher@unibz.it");
        assertThat(result.getName()).isEqualTo("Matteo");
        assertThat(result.getPassword()).isEqualTo("password");
    }

    @Test
    public void findByIdThrowsIllegalArgumentException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> useCase.findById(1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No user found with id: 1");
    }
}
