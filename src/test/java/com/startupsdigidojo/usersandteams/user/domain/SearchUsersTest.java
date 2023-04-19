package com.startupsdigidojo.usersandteams.user.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class SearchUsersTest {
    private SearchUsers underTest;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp(){
        underTest = new SearchUsers(userRepository);
    }

    @Test
    public void findByMailAddressReturnsUser(){
        when(userRepository.findByMailAddress("malarcher@unibz.it"))
                .thenReturn(Optional.of(new User("Matteo","malarcher@unibz.it","password")));

        User result = underTest.findByMailAddress("malarcher@unibz.it");

        assertThat(result).isInstanceOf(User.class);
        assertThat(result.getMailAddress()).isEqualTo("malarcher@unibz.it");
        assertThat(result.getName()).isEqualTo("Matteo");
        assertThat(result.getPassword()).isEqualTo("password");
    }

    @Test
    public void findByMailAddressThrowsIllegalArgumentException(){
        when(userRepository.findByMailAddress(anyString())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> underTest.findByMailAddress("malarcher@unibz.it"))
                .isInstanceOf(IllegalArgumentException.class);
    }
    @Test
    public void findByIdReturnsUser(){
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User("Matteo","malarcher@unibz.it","password")));

        User result = underTest.findById(1);

        assertThat(result).isInstanceOf(User.class);
        assertThat(result.getMailAddress()).isEqualTo("malarcher@unibz.it");
        assertThat(result.getName()).isEqualTo("Matteo");
        assertThat(result.getPassword()).isEqualTo("password");
    }

    @Test
    public void findByIdThrowsIllegalArgumentException(){
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> underTest.findById(1))
                .isInstanceOf(IllegalArgumentException.class);
    }


}
