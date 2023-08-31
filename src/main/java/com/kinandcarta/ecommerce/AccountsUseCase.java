package com.kinandcarta.ecommerce;

import org.springframework.http.ResponseEntity;

import java.util.Set;

public interface AccountsUseCase {

    ResponseEntity<Set<Address>> findAllAddressesForAccount(final Long id);
}
