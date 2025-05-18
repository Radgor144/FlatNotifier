package com.FlatNotifier.FlatNotifier;

import com.FlatNotifier.FlatNotifier.Email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class RequestScheduler {

    private final ScraperService scraperService;
    private final EmailService emailService;

    @Scheduled(fixedRateString = "#{@timeToSchedule}")
    public void executeTask() throws IOException {
        List<FlatOffer> flatOfferList = scraperService.getFlats();
        if (flatOfferList.size() > 0) {
            log.info("scraping successful, found {} offers", flatOfferList.size());
            emailService.sendEmail(flatOfferList);
            log.info("mail sent successfully");
        } else {
            log.info("scraping successful, no new offers found");
        }

    }
}
