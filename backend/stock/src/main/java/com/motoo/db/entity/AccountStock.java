package com.motoo.db.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="accountstock")
public class AccountStock {


    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountStockId;

    private int amount;

    private int price;

    @ManyToOne(fetch = FetchType.LAZY)
    private Accounts accounts;

    @ManyToOne(fetch = FetchType.LAZY)
    private Stock stock;

    public void createAccountStock(Accounts accounts, Stock stock, int amount, int price){
        this.accounts =accounts;
        this.stock = stock;
        this.price =price;
        this.amount = amount;
    }
}