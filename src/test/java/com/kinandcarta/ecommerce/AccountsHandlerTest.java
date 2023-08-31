package com.kinandcarta.ecommerce;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class AccountsHandlerTest {

    TestEntityManager entityManager = Mockito.mock(TestEntityManager.class);

    AccountsRepository accountsRepository = Mockito.mock(AccountsRepository.class);

    AccountsHandler accountsHandler;

    @BeforeEach void setUp() {
        accountsHandler = new AccountsHandler(accountsRepository);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "あいうえお@example.com",
            "email@example",
            "email@example.weber",
            "email@111.222.333.44444",
            "just”not”right@example.com"
    })
    @DisplayName("Should return HTTP 400 if email is invalid")
    void shouldReturnHttp400_ifEmailIsInvalid(final String email) {

        Accounts account = Accounts.builder()
                .id(100L)
                .firstName("DukeFirstName")
                .lastName("DukeLastName")
                .emailAddress(email)
                .addresses(
                        Set.of(Address.builder()
                                .id(100L)
                                .address1("100")
                                .address2("")
                                .city("Food Forest City")
                                .state("FL")
                                .province("")
                                .postalCode("33000")
                                .country("US").build())).build();

        when (this.accountsHandler.create(account))
                .thenThrow(EmailNotValidException.class);

        assertThat(account).isNotNull();
        assertThatThrownBy(() -> accountsHandler.create(account)).isInstanceOf(EmailNotValidException.class);
    }

}
