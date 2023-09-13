package com.kinandcarta.ecommerce;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class AccountsControllerTest {
    final String expectedAccountIdRef = "4f464483-a1f0-4ce9-a19e-3c0f23e84a67";

    Address address = Address.builder().id(100L)
            .address1("100")
            .address2("")
            .city("Food Forest City")
            .state("FL")
            .province("")
            .postalCode("33000")
            .country("US").build();
    Accounts account = Accounts.builder()
            .id(1L)
            .accountRefId(expectedAccountIdRef)
            .firstName("Minimal")
            .lastName("CreateAccount")
            .emailAddress("dukefirst.last@enjoy.com")
            .addresses(
                    Set.of(address)).build();

    final String emailAddress = "あいうえお@example.com";
    Accounts accountEmailSpace = Accounts.builder()
            .id(1L)
            .accountRefId(expectedAccountIdRef)
            .firstName("Email")
            .lastName("Space")
            .emailAddress(emailAddress)
            .addresses(
                    Set.of(address)).build();

    @Mock
    AccountsHandler accountsHandler;

    AccountsController controller;

    @BeforeEach
    void setUp() {
        controller = new AccountsController(accountsHandler);
    }

    @Test void should_CreateNewAccountWithAddress() {
        ResponseEntity<Accounts> accountCreateCommand = performCreate_Account_Given();
        assertThat(accountCreateCommand).isNotNull();
        assertThat(accountCreateCommand.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(accountCreateCommand.getBody()).isNotNull();
        Accounts accountCreated = accountCreateCommand.getBody();
        assertThat(accountCreated).hasFieldOrProperty("addresses");
    }

    @Test void shouldNot_CreateNewAccount_whenEmailIsInvalid() {
        performCreate_Account_WithEmailException();
        ResponseEntity<Accounts> createdAccount = controller.create(accountEmailSpace);
        assertThat(createdAccount.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(400));
    }
    @Test void should_FindAllAccounts() {
        ResponseEntity<Set<Accounts>> allAccounts = performFindAll_Accounts_Given();
        assertThat(allAccounts).isNotNull();
        assertThat(allAccounts.getBody()).isNotNull();
        Set<Accounts> accountsFound = allAccounts.getBody();
        assertThat(accountsFound).hasSize(1);
    }
    @Test void should_FindAllAccount_ByID() {
        ResponseEntity<Accounts> findOneAccountCommand = performFindOne_Account_Given(1L);
        assertThat(findOneAccountCommand).isNotNull();
        assertThat(findOneAccountCommand.getBody()).isNotNull();
        Accounts accountOfOne = findOneAccountCommand.getBody();

        assertThat(accountOfOne).hasFieldOrPropertyWithValue("id",1L)
                .hasFieldOrPropertyWithValue("firstName","Minimal")
                .hasFieldOrPropertyWithValue("lastName","CreateAccount")
                .hasFieldOrPropertyWithValue("emailAddress","dukefirst.last@enjoy.com");
    }
    @Test void should_UpdateExisting_Account() {
        Accounts updatingThisAccount = performUpdate_Account_Given(1L);
        updatingThisAccount.setEmailAddress("updatedemail@test.com");
        assertThat(updatingThisAccount.getEmailAddress()).isEqualTo(account.getEmailAddress());
    }
    @Test void shouldNot_UpdateAccount_thatDoesNotExist() {
        when(accountsHandler.update(1L, account)).thenThrow(EntityNotFoundException.class);
        assertThat(controller.update(1L, account).getStatusCode()).isEqualTo(HttpStatusCode.valueOf(400));
    }

    @Test void shouldDeleteAccounts_withId1() {
        ResponseEntity<Accounts> createAccountCmd = performCreate_Account_Given();
        assertThat(createAccountCmd).isNotNull();
        assertThat(createAccountCmd.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        doNothing().when(accountsHandler).delete(1L);
        controller.delete(1L);
        verify(accountsHandler, times(1)).delete(1L);
    }

    // Helper Methods
    private ResponseEntity<Accounts> performCreate_Account_Given() {
        when(accountsHandler.create(account)).thenReturn(account);
        return controller.create(account);
    }
    private void performCreate_Account_WithEmailException() {
        when(accountsHandler.create(accountEmailSpace)).thenThrow(EmailNotValidException.class);
    }

    private ResponseEntity<Set<Accounts>> performFindAll_Accounts_Given() {
        when(accountsHandler.findAll()).thenReturn(Set.of(account));
        return controller.findAll();
    }

    private ResponseEntity<Accounts> performFindOne_Account_Given(final Long accountId) {
        assertAccountId(accountId);
        when(accountsHandler.findById(accountId)).thenReturn(account);
        return controller.findById(accountId);
    }

    private Accounts performUpdate_Account_Given(final Long accountId) {
        assertAccountId(accountId);
        when(accountsHandler.update(accountId, account)).thenReturn(account);
        ResponseEntity<Accounts> updateAccountCommand = controller.update(accountId, account);
        assertThat(updateAccountCommand).isNotNull();
        assertThat(updateAccountCommand.getBody()).isNotNull();
        return updateAccountCommand.getBody();
    }

    private void assertAccountId(final Long id) {
        if (id != 1L) throw new IllegalArgumentException("Invalid account id");
    }
}
