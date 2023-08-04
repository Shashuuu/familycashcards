package com.example.cashcard.repo;

import com.example.cashcard.model.CashCardUser;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CashCardUserRepository extends CrudRepository<CashCardUser, String> {
    Optional<CashCardUser> findByUsername(String username);
}
