package com.kinandcarta.ecommerce;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@RestController
@Slf4j
public class AccountsController implements CrudUseCase<Accounts>, AccountsUseCase{
    private static final int LENGTH_LIMIT_FOR_STRING = 255;
    final
    AccountsHandler accountsHandler;

    public AccountsController(AccountsHandler accountsHandler) {
        this.accountsHandler = accountsHandler;
    }

    @Override
    @PostMapping(value = "/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Accounts> create(@RequestBody final Accounts model) {
        try {
            if (model == null) return ResponseEntity.badRequest().build();

            model.setAccountRefId(limitTo255Length(UUID.randomUUID().toString()));

            return new ResponseEntity<>(accountsHandler.create(model), HttpStatus.OK);
        } catch (final Exception e) {
            if (e instanceof EmailNotValidException) {
                log.error("::METHOD, create, E-mail invalid Exception.");
                return ResponseEntity.badRequest().build();
            }
            log.error("::METHOD, create, exception occured.", e);
            return ResponseEntity.notFound().build();
        }

        // NOTES: return (current_status: success|failure, data: 0, 1 or Many
    }

    @Override
    @PutMapping(value = "/accounts/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Accounts> update(@PathVariable("id") @NotNull final Long id, @RequestBody final Accounts model) {
        Objects.requireNonNull(id, "ID not provided for, update");
        log.debug("update  for id - " + id);
        try {
            return new ResponseEntity<>(accountsHandler.update(id, model), HttpStatus.OK);
        } catch (final Exception e) {
            if (e instanceof EntityNotFoundException) {
                log.error("EntityNotFoundException: Account not updated for ID -> " + id);
                return ResponseEntity.badRequest().build();
            }
            log.error("::METHOD, update, exception occured.", e);
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    @ResponseStatus(HttpStatus.ACCEPTED)
    @DeleteMapping(value = "/accounts/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void delete(@PathVariable("id") @NotNull final Long id) {
        Objects.requireNonNull(id, "ID not provided for, delete");
        log.debug("delete  for id - " + id);
        try {
            accountsHandler.delete(id);
        } catch (final Exception e) {
            log.error("::METHOD, delete, exception occurred.", e);
        }
    }

    @Override
    @GetMapping(value = "/accounts/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Accounts> findById(@PathVariable("id") @NotNull final Long id) {
        Objects.requireNonNull(id, "ID not provided for, findById");
        log.debug("findById  for id - " + id);
        try {
            return new ResponseEntity<>(accountsHandler.findById(id), HttpStatus.OK);
        } catch (final Exception e) {
            log.error("::METHOD, findById, exception occurred.", e);
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    @GetMapping(value = "/accounts/{id}/reference", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Accounts> findByAccountIdRef(@PathVariable("id") @NotNull final String id) {
        Objects.requireNonNull(id, "ID not provided for, findByAccountIdRef");
        log.debug("findByAccountIdRef  for id - " + id);
        try {
            return new ResponseEntity<>(accountsHandler.findByAccountIdRef(id), HttpStatus.OK);
        } catch (final Exception e) {
            log.error("::METHOD, findByAccountIdRef, exception occurred.", e);
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping(value = "/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<Set<Accounts>> findAll() {
        log.debug("findAll...");
        try {
        return new ResponseEntity<>(accountsHandler.findAll(), HttpStatus.OK);
        } catch (final Exception e) {
            log.error("::METHOD, findAll, exception occurred.", e);
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    @GetMapping(value = "/accounts/{id}/addresses", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Set<Address>> findAllAddressesForAccount(@PathVariable("id") @NotNull final Long id) {
        Objects.requireNonNull(id, "ID not provided for, findAllAddressesForAccount");
        log.debug("findAllAddressesForAccount  for id - " + id);
        try {
            return new ResponseEntity<>(accountsHandler.findAllAddressesForAccount(id), HttpStatus.OK);
        } catch (final Exception e) {
            log.error("::METHOD, findAllAddressesForAccount, exception occurred.", e);
            return ResponseEntity.notFound().build();
        }
    }

    // Helper methods
    private Accounts toAccounts(final Accounts account) {
        Objects.requireNonNull(account, "AccountServiceClient, null Account");
        Objects.requireNonNull(account, "AccountServiceClient, null Account-> First name");
        Objects.requireNonNull(account, "AccountServiceClient, null Account-> Last name");
        Objects.requireNonNull(account, "AccountServiceClient, null Account-> Email address");
        Objects.requireNonNull(account, "AccountServiceClient, null Account-> Addresses");

        if (account.getAddresses().isEmpty()) {
            throw new IllegalArgumentException("AccountServiceClient, at least ONE address is required");
        }

        return Accounts.builder()
                .id(account.getId())
                .accountRefId(limitTo255Length(UUID.randomUUID().toString()))
                .accountRefId(account.getAccountRefId())
                .firstName(account.getFirstName())
                .lastName(account.getLastName())
                .emailAddress(account.getEmailAddress())
                .addresses(account.getAddresses()).build();
    }

    private String limitTo255Length(final String data) {
        Objects.requireNonNull(data, "limitTo255Length, input null or missing.");
        if (data.length() > LENGTH_LIMIT_FOR_STRING) {
            return data.substring(0,255);
        }

        return data;
    }
}
