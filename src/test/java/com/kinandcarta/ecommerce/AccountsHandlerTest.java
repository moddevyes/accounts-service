package com.kinandcarta.ecommerce;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class AccountsHandlerTest {
    final String expectedAccountIdRef = "4f464483-a1f0-4ce9-a19e-3c0f23e84a67";
    AccountsRepository accountsRepository = Mockito.mock(AccountsRepository.class);

    AddressRepository addressRepository = Mockito.mock(AddressRepository.class);

    AccountsHandler accountsHandler;

    Address address = Address.builder().id(100L)
            .address1("100")
            .address2("")
            .city("Food Forest City")
            .state("FL")
            .province("")
            .postalCode("33000")
            .country("US").build();
    Address shippingAddress = Address.builder().id(100L)
            .address1("100 Peaches and Fructose Way")
            .address2("")
            .city("PO Box City")
            .state("FL")
            .province("")
            .postalCode("33000")
            .country("US").build();
    Address beachVacayDddress = Address.builder().id(100L)
            .address1("1 Beach Sunny Drive")
            .address2("")
            .city("Beach Waves City")
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

    Accounts accountMultipleAddresses = Accounts.builder()
            .id(1L)
            .accountRefId(expectedAccountIdRef)
            .firstName("Minimal")
            .lastName("CreateAccount")
            .emailAddress("dukefirst.last@enjoy.com")
            .addresses(
                    Set.of(address, beachVacayDddress, shippingAddress)).build();

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
                .accountRefId(expectedAccountIdRef)
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

    @Test
    void shouldThrowException_whenAccountNotFound() {
        when(accountsRepository.findById(1L)).thenThrow(EntityNotFoundException.class);
        assertThatThrownBy(() -> accountsHandler.findById(1L)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldDeleteAccount() {
        doNothing().when(accountsRepository).deleteById(1L);
        accountsHandler.delete(1L);
        verify(accountsRepository).deleteById(1L);
    }

    @Test
    void shouldFindAllAccounts() {
        when(accountsRepository.findAll()).thenReturn(List.of(account));
        Set<Accounts> accounts = accountsHandler.findAll();
        assertThat(accounts).isNotNull().hasSize(1);
    }

    @Test
    void shouldCreateNewAccount() {
        when(accountsRepository.save(account)).thenReturn(account);
        Accounts accountCreated = accountsHandler.create(account);
        assertThat(accountCreated).isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("accountRefId", expectedAccountIdRef)
                .hasFieldOrPropertyWithValue("firstName", "Minimal")
                .hasFieldOrPropertyWithValue("lastName", "CreateAccount")
                .hasFieldOrPropertyWithValue("emailAddress", "dukefirst.last@enjoy.com");
    }

    @Test
    void shouldNot_UpdateAccountThatDoesNot_Exist() {
        when(accountsRepository.findById(100L)).thenThrow(EntityNotFoundException.class);
        assertThatThrownBy(() -> accountsHandler.update(1L, account))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldUpdateAccount() {
        when(accountsRepository.findById(100L)).thenReturn(Optional.ofNullable(account));
        when(accountsRepository.save(account)).thenReturn(account);
        Accounts foundAccountForUser = accountsHandler.update(100L, account);
        assertThat(foundAccountForUser).isEqualTo(account);
        foundAccountForUser.setEmailAddress("updatedemail@testing.net");
        assertThat(foundAccountForUser.getEmailAddress()).isEqualTo(account.getEmailAddress());
    }

    @Test
    void shouldFindAllAddresses_forAccount() {
        when(accountsRepository.findById(1L))
                .thenReturn(Optional.ofNullable(accountMultipleAddresses));
        Set<Address> allAccountAddresses = accountsHandler.findAllAddressesForAccount(1L);
        assertThat(allAccountAddresses).isNotNull().hasSize(3);
    }

    @Test
    void shouldUpdateTheAddresses_forGivenAccount() {
        when(accountsRepository.findById(100L)).thenReturn(Optional.ofNullable(account));
        when(addressRepository.findById(100L)).thenReturn(Optional.ofNullable(address));
        // Okay account 100L has address -> 100L, update the address
        Accounts foundAccount100L = accountsHandler.update(100L, account);
        assertThat(foundAccount100L).isEqualTo(account);
        assertThat(foundAccount100L.getAddresses()).hasSize(1);
        assertThat(foundAccount100L.getAddresses().stream().findFirst()).contains(address);
        Address updateAddress = foundAccount100L.getAddresses().stream().findFirst().get();
        /*
        Current Address:
        .id(100L)
            .address1("100")
            .address2("")
            .city("Food Forest City")
            .state("FL")
            .province("")
            .postalCode("33000")
            .country("US").build();
         */
        updateAddress.setAddress1("1001 SpringBoot Address Lane");
        updateAddress.setAddress2("Suite 1001");
        assertThat(address).isEqualTo(updateAddress);
    }

    @Test
    void shouldFindAccount_byId() {
        when(accountsRepository.findById(2L))
                .thenReturn(Optional.ofNullable(accountMultipleAddresses));
        Accounts accountFoundWithId2 = accountsHandler.findById(2L);
        assertThat(accountFoundWithId2).isNotNull();
    }
}
