package com.spring.unictive.module.notification.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReportScheduler {
    private static final Logger log = LoggerFactory.getLogger(ReportScheduler.class);

    // Every 10 seconds
    @Scheduled(fixedRate = 10000)
    public void generateReport() {
        log.info("Generating report in background...");
    }

    // Every day at 1 AM
    @Scheduled(cron = "0 0 1 * * ?")
    public void dailyJob() {
        log.info("Running daily job at 1 AM");
    }
}
