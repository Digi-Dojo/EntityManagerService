package it.unibz.digidojo.entitymanagerservice.user.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class LoginServiceTest {
    private LoginService underTest;

    @Mock
    private UserBroadcaster userBroadcaster;

    @BeforeEach
    public void setUp() {
        underTest = new LoginService(userBroadcaster);
    }

    @Test
    public void hashPasswordIsDeterministicTest() {
        String password = "password";
        String mailAddress = "emailAddress";
        String hash1 = underTest.hashPassword(password, mailAddress);
        String hash2 = underTest.hashPassword(password, mailAddress);
        assertEquals(hash1, hash2);
    }

    @ParameterizedTest
    @MethodSource("generateInput")
    public void avoidCollisionsTest(String password) {
        String hash = underTest.hashPassword("a", "@");
        assertNotEquals(hash, underTest.hashPassword(password, "@"));
    }

    @ParameterizedTest
    @MethodSource("generateInput")
    public void avoidCollisionsSamePasswordTest(String mailAddress) {
        String hash = underTest.hashPassword("@", "a");
        assertNotEquals(hash, underTest.hashPassword("@", mailAddress));
    }

    private static Stream<Arguments> generateInput() {
        return Stream.of(
                Arguments.of("b"),
                Arguments.of("c"),
                Arguments.of("d"),
                Arguments.of("e"),
                Arguments.of("f"),
                Arguments.of("g"),
                Arguments.of("h"),
                Arguments.of("i"),
                Arguments.of("j"),
                Arguments.of("k"),
                Arguments.of("l"),
                Arguments.of("m"),
                Arguments.of("n"),
                Arguments.of("o"),
                Arguments.of("p"),
                Arguments.of("q"),
                Arguments.of("r"),
                Arguments.of("s"),
                Arguments.of("t"),
                Arguments.of("u"),
                Arguments.of("v"),
                Arguments.of("w"),
                Arguments.of("x"),
                Arguments.of("y"),
                Arguments.of("z")
        );
    }

    @Test
    public void verifyPasswordTest() {
        User aUser = new User("Matteo", "malarcher@unibz.it", underTest.hashPassword("Matteo Password", "malarcher@unibz.it"));
        User test = underTest.verifyPassword(aUser, "Matteo Password");
        assertThat(test)
                .isInstanceOf(User.class)
                .isNotNull();
        assertThat(test.getEmailAddress())
                .isEqualTo("malarcher@unibz.it");
    }

    @Test
    public void verifyPasswordThrowsExceptionTest() {
        User aUser = new User("Matteo", "malarcher@unibz.it", underTest.hashPassword("Matteo Password", "malarcher@unibz.it"));
        assertThatThrownBy(() -> underTest.verifyPassword(aUser, "Mariolino Password"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Wrong password for this user");
    }
}
