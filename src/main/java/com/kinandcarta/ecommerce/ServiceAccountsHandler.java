package com.kinandcarta.ecommerce;

import java.util.Set;

public interface ServiceAccountsHandler {
    Set<Address> findAllAddressesForAccount(final Long id);
}
