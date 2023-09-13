package com.kinandcarta.ecommerce;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountsRepository extends JpaRepository<Accounts, Long> {
    Optional<Accounts> findAccountsByAccountRefId(final String accountRefId);
}
