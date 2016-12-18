package com.example.rached.memory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rached on 18/12/16.
 */

public class Collection {

    public String name;
    List<Card> cards;

    public Collection(String name){
        this.name = name;
        cards = new ArrayList<Card>();
    }

    public void addCard(Card c){
        cards.add(c);
    }
}
