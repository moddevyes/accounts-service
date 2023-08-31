package com.kinandcarta.ecommerce;

import java.util.Set;

public interface ServiceHandler extends ServiceAccountsHandler {
    Accounts create(final Accounts model);
    Accounts update(final Long id, final Accounts model);

    void delete(final Long id);
    Accounts findById(final Long id);

    Set<Accounts> findAll();
    
}
