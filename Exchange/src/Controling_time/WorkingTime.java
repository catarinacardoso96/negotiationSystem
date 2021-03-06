package Controling_time;

import exchange.Exchange;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WorkingTime {

    private static final LocalTime openingTime = LocalTime.of(9, 0); // 9:00
    private static final LocalTime closingTime = LocalTime.of(17, 41); // 17:00

    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private Exchange exchange;
    private int port;

    public WorkingTime(Exchange exchange, int port){
        this.exchange = exchange;
        this.port = port;

    }



    public void scheduleOpenAndClose() {
        ExchangeOpen openExch = new ExchangeOpen(exchange, port);
        ExchangeClose closeExch = new ExchangeClose(openExch, exchange);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fstScheduledOpen = now.with(openingTime);
        LocalDateTime fstScheduledClose = now.with(closingTime);

        if (now.isAfter(fstScheduledOpen)) {
            fstScheduledOpen = fstScheduledOpen.plusDays(1);

            if (now.isBefore(fstScheduledClose)) {
                scheduler.execute(openExch);
            } else {
                fstScheduledClose = fstScheduledClose.plusDays(1);
            }
        }
        long openDelay = Duration.between(now, fstScheduledOpen).getSeconds();
        long closeDelay = Duration.between(now, fstScheduledClose).getSeconds();

        scheduler.scheduleAtFixedRate(openExch, openDelay, 24 * 60 * 60, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(closeExch, closeDelay, 24 * 60 * 60, TimeUnit.SECONDS);
    }
}