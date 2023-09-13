package com.kinandcarta.ecommerce;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class AccountsHandler implements ServiceHandler {
    private final AccountsRepository repository;

    public AccountsHandler(AccountsRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Accounts create(final Accounts model) {
        log.debug("create: model ->" + model.toString());
        if (validate(model)) {
            throw new EmailNotValidException("Invalid e-mail address.");
        }

        return repository.save(model);
    }

    @Override
    @Transactional
    public Accounts update(final Long id, final Accounts model) {
        log.debug("update: id -> " + id + ", model -> " + model.toString());

        Optional<Accounts> accountsFound = repository.findById(id);
        if (accountsFound.isPresent()) {
            accountsFound.get().setFirstName(model.getFirstName());
            accountsFound.get().setLastName(model.getLastName());
            accountsFound.get().setEmailAddress(model.getEmailAddress());

            if (accountsFound.get().getAddresses().isEmpty()) {
                accountsFound.get().setAddresses(new HashSet<>());
            } else {
                accountsFound.get().setAddresses(model.getAddresses());
            }
            repository.save(accountsFound.get());
        } else {
            throw new EntityNotFoundException("Account not updated for ID -> " + id);
        }

        return model;
    }

    @Override
    public void delete(final Long id) {
        log.debug("delete: id -> " + id);
        repository.deleteById(id);
    }

    @Override
    public Accounts findById(final Long id) {
        log.debug("findById: id -> " + id);
        return repository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public Set<Accounts> findAll() {
        log.debug("findAll");
        return new HashSet<>(repository.findAll());
    }

    @Override
    public Set<Address> findAllAddressesForAccount(final Long id) {
        log.debug("findAll Addresses for Account id -> " + id);
        return findById(id).getAddresses();
    }
    public boolean validate(final Accounts account) {
        Set<ConstraintViolation<Accounts>> violations = new HashSet<>();
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            violations = validator.validate(account);
        } catch (final Exception e) {
            log.error("Exception [validate], " + e);
        }
        return violations.stream().findFirst().isPresent();
    }
}
