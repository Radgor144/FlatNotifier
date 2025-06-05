package com.FlatNotifier.FlatNotifier;


import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
@Service
public class ScraperService {

    @Value("${offerTimeLimit}")
    private Integer offerTimeLimit;

    private static final String BASE_URL = "https://www.olx.pl";
    private static final String URL = BASE_URL + "/nieruchomosci/mieszkania/wynajem/krakow/?search%5Border%5D=created_at:desc";
    private static final Pattern TIME_PATTERN = Pattern.compile("\\b\\d{2}:\\d{2}\\b");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);

    public List<FlatOffer> getFlats() throws IOException {
        log.info("Starting scraping...");
        Document document = fetchDocument(URL);
        return extractFlatOffers(document);
    }

    private Document fetchDocument(String url) throws IOException {
        return Jsoup.connect(url)
                .userAgent("Mozilla/5.0")
                .timeout(5000)
                .get();
    }

    private List<FlatOffer> extractFlatOffers(Document document) {
        List<FlatOffer> offers = new ArrayList<>();
        LocalTime now = LocalTime.now().minusHours(2);
        Elements listings = document.select("div[data-testid='listing-grid']");

        for (Element listing : listings.select("div[data-testid='l-card']")) {
            FlatOffer offer = extractFlatOffer(listing, now);
            if (offer != null) {
                offers.add(offer);
            }
        }

        return offers;
    }

    private FlatOffer extractFlatOffer(Element element, LocalTime now) {
        String title = element.select("div[data-cy='ad-card-title']").text();
        String locationInfo = element.select("p[data-testid='location-date']").text();
        String rawLink = element.select("a[href]").attr("href");

        Matcher matcher = TIME_PATTERN.matcher(locationInfo);
        if (matcher.find()) {
            String timeStr = matcher.group();
            LocalTime offerTime = LocalTime.parse(timeStr, TIME_FORMATTER);

            if (isOfferRecent(offerTime, now)) {
                String normalizedLink = normalizeLink(rawLink);
                return new FlatOffer(title, locationInfo, normalizedLink);
            }
        }

        return null;
    }

    private boolean isOfferRecent(LocalTime offerTime, LocalTime now) {
        return now.minusMinutes(offerTimeLimit).isBefore(offerTime) && now.isAfter(offerTime);
    }

    private String normalizeLink(String link) {
        return link.contains("otodom") ? link : BASE_URL + link;
    }
}
