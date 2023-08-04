package com.example.cashcard.controller;

import com.example.cashcard.model.CashCard;
import com.example.cashcard.model.CashCardUser;
import com.example.cashcard.repo.CashCardRepository;
import com.example.cashcard.repo.CashCardUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/home")       //  creates /cashcards endpoint and handles requests through this endpoint
public class CashCardController {

    private CashCardRepository cashCardRepository;
    private CashCardUserRepository cashCardUserRepository;

    public CashCardController(CashCardRepository cashCardRepository, CashCardUserRepository cashCardUserRepository) {
        this.cashCardRepository = cashCardRepository;
        this.cashCardUserRepository = cashCardUserRepository;
    }

    @GetMapping
    public String welcomeMessage(Principal principal) {
        return "Welcome to your CashCards Dashboard " +principal.getName();
    }

    @GetMapping("/cashcards/{requestedId}")       //  handler method for /cashcards/{requestedId} endpoint using GET
    public ResponseEntity<CashCard> findById(@PathVariable Long requestedId, Principal principal) {      //  method returns http response as ResponseEntity<String> which contains the requested resource

        //  Principal object holds details of the user currently logged in - like authentication and authorization information

        CashCard cashCard = findCashCard(requestedId, principal);      //  CrudRepository generates findByIdAndOwner(Id, owner) method and returns Optional object which may or may not contain the CashCard
        if (cashCard != null) {
            return ResponseEntity.ok(cashCard);     //  .ok() -> generates 200 OK response
        } else {
            return ResponseEntity.notFound().build();       //  .notFound() -> generates 404 NOT_FOUND response     .build() -> builds Http response entity with no body
        }
    }

    @PostMapping("/cashcards")
    private ResponseEntity<Void> createCashCard(@RequestBody CashCard newCashCardRequest, UriComponentsBuilder ucb, Principal principal) {       //  ucb helps in creating uri for created resource

        CashCard cashCardWithOwner = new CashCard(null, newCashCardRequest.getAmount(), principal.getName());

        CashCard savedCashCard = cashCardRepository.save(cashCardWithOwner);       //  .save() is given by CrudRepository to save cashCard and return its unique ID

        URI locationOfNewCashCard = ucb
                //  Append to the path of this builder
                .path("/home/cashcards/{id}")
                //  Builds URI Components instance and replaces template {id} with savedCashCard.id()
                .buildAndExpand(savedCashCard.getId())
                //  Creates URI
                .toUri();

        return ResponseEntity.created(locationOfNewCashCard).build();
    }

    @GetMapping("/cashcards")
    public ResponseEntity<List<CashCard>> findAll(Pageable pageable, Principal principal) {
        Page<CashCard> page = cashCardRepository.findByCardOwner(       //  .findByOwner() is provided by SpringData CrudRepository to retrieve all records of that user that takes Pageable type argument and returns page of entities meeting paging restrictions in Pageable object argument
                principal.getName(),
                PageRequest.of(                                 //  PageRequest is java implementation of Pageable
                        pageable.getPageNumber(),               //  extracts page number from url if present or 0 index by default
                        pageable.getPageSize(),                 //  extracts page size from url if present or 20 by default
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))                      //  extracts sort query parameter from url if present OR sorts amounts in ascending by default
                ));

        return ResponseEntity.ok(page.getContent());
    }

    @PutMapping("/cashcards/{requestedId}")
    private ResponseEntity<Void> putCashCard(@PathVariable Long requestedId, @RequestBody CashCard cashCardUpdate, Principal principal) {

        CashCard cashCard = findCashCard(requestedId, principal);

        if(cashCard != null) {
            CashCard updatedCashCard = new CashCard(cashCard.getId(), cashCardUpdate.getAmount(), principal.getName());
            cashCardRepository.save(updatedCashCard);

            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    private CashCard findCashCard(Long requestedId, Principal principal) {
        return cashCardRepository.findByIdAndCardOwner(requestedId, principal.getName());
    }

    @DeleteMapping("/cashcards/{id}")
    private ResponseEntity<Void> deleteCashCard(@PathVariable Long id, Principal principal) {
        if (cashCardRepository.existsByIdAndCardOwner(id, principal.getName())) {           //  .existsByIdAndOwner() is a method whose implementation is automatically by Spring Data
            cashCardRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/changepwd")
    private ResponseEntity<Void> changePassword(@RequestBody String newPassword, Principal principal) {

        CashCardUser cashCardUser = cashCardUserRepository.findById(principal.getName()).get();
        CashCardUser updatedCashCardUser = new CashCardUser(principal.getName(), new BCryptPasswordEncoder().encode(newPassword));
        cashCardUserRepository.save(updatedCashCardUser);

        return ResponseEntity.noContent().build();
    }

//    @PostMapping("/logout")
//    private String userLogout() {
//        return "redirect:/index/login?logout";
//    }
}