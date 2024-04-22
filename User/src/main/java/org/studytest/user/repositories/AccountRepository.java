package org.studytest.user.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.studytest.user.models.Account;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findByUsername(String username);

    Boolean existsByUsername(String username);
}
