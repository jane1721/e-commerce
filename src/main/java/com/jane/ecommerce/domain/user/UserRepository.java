package com.jane.ecommerce.domain.user;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(Long id);
    User save(User user);
    Optional<User> findByUsername(String username);
}
