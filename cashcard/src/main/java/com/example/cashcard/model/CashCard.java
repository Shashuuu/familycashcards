package com.example.cashcard.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class CashCard {              //  @Id makes id as primary key in the database table

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double amount;
    private String cardOwner;

    protected CashCard() {}

    public CashCard(Long id, Double amount, String cardOwner) {
        this.id = id;
        this.amount = amount;
        this.cardOwner = cardOwner;
    }

    public Long getId() {
        return id;
    }

    public Double getAmount() {
        return amount;
    }

    public String getCardOwner() {
        return cardOwner;
    }

    @Override
    public String toString() {
        return "CashCard{" +
                "id=" + id +
                ", amount=" + amount +
                ", owner='" + cardOwner + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CashCard cashCard = (CashCard) o;
        return id.equals(cashCard.id) && amount.equals(cashCard.amount) && cardOwner.equals(cashCard.cardOwner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount, cardOwner);
    }
}
