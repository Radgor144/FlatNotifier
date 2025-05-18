package com.FlatNotifier.FlatNotifier;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FlatOffer {
    private String title;
    private String location;
    private String link;

    @Override
    public String toString() {
        return "Title: " + title + "\n" +
                "Location: " + location + "\n" +
                "Link: " + link + "\n";
    }
}
