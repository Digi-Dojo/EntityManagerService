package it.unibz.digidojo.entitymanagerservice.user.domain.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.unibz.digidojo.entitymanagerservice.user.domain.model.User;
import it.unibz.digidojo.entitymanagerservice.user.domain.UserBroadcaster;

@ExtendWith(MockitoExtension.class)
public class LoginUserTest {
    @Mock
    private UserBroadcaster userBroadcaster;
    private LoginUser useCase;

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

    @BeforeEach
    public void setUp() {
        useCase = new LoginUser(userBroadcaster);
    }

    @Test
    public void hashPasswordIsDeterministicTest() {
        String password = "password";
        String mailAddress = "emailAddress";
        String hash1 = useCase.hashPassword(password, mailAddress);
        String hash2 = useCase.hashPassword(password, mailAddress);
        assertEquals(hash1, hash2);
    }

    @ParameterizedTest
    @MethodSource("generateInput")
    public void avoidCollisionsTest(String password) {
        String hash = useCase.hashPassword("a", "@");
        assertNotEquals(hash, useCase.hashPassword(password, "@"));
    }

    @ParameterizedTest
    @MethodSource("generateInput")
    public void avoidCollisionsSamePasswordTest(String mailAddress) {
        String hash = useCase.hashPassword("@", "a");
        assertNotEquals(hash, useCase.hashPassword("@", mailAddress));
    }

    @Test
    public void verifyPasswordTest() {
        User aUser = new User(
                "Matteo",
                "malarcher@unibz.it",
                useCase.hashPassword("Matteo Password", "malarcher@unibz.it")
        );
        User test = useCase.verifyPassword(aUser, "Matteo Password");
        assertThat(test)
                .isInstanceOf(User.class)
                .isNotNull();
        assertThat(test.getEmailAddress())
                .isEqualTo("malarcher@unibz.it");
    }

    @Test
    public void verifyPasswordThrowsExceptionTest() {
        User aUser = new User(
                "Matteo",
                "malarcher@unibz.it",
                useCase.hashPassword("Matteo Password", "malarcher@unibz.it")
        );
        assertThatThrownBy(() -> useCase.verifyPassword(aUser, "Mariolino Password"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Wrong password for this user");
    }
}
