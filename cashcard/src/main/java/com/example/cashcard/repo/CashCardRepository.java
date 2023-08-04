package com.example.cashcard.repo;

import com.example.cashcard.model.CashCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

//  Extending CrudRepository interface allows SpringBoot and SpringData to work together to generate CRUD methods for our database
//  CrudRepository<CashCard, Long> indicates that it manages data of type CashCard that has an id of type Long
public interface CashCardRepository extends CrudRepository<CashCard, Long>, PagingAndSortingRepository<CashCard, Long> {

    //  Spring Data will automatically handle SQL queries for these method implementations
    //  or we can provide query explicitly using annotations something like
    //  @Query("SELECT * FROM cash_card WHERE id = :id AND owner = :owner")
    CashCard findByIdAndCardOwner(Long id, String owner);

    Page<CashCard> findByCardOwner(String owner, PageRequest amount);

    boolean existsByIdAndCardOwner(Long id, String owner);
}
