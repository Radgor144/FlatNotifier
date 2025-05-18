package com.FlatNotifier.FlatNotifier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SchedulerConfig {

    @Value("${scheduler.interval.minutes}")
    private long intervalMinutes;

    @Bean
    public Long timeToSchedule() {
        return intervalMinutes * 60 * 1000;
    }
}

