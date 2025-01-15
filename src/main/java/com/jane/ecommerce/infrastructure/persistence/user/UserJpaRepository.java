package com.jane.ecommerce.infrastructure.persistence.user;

import com.jane.ecommerce.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {
}
