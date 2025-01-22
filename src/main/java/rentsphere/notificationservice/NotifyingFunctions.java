package rentsphere.notificationservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.function.Function;

@Configuration
public class NotifyingFunctions {
    private static final Logger log =
            LoggerFactory.getLogger(NotifyingFunctions.class);

    @Bean
    public Function<BookingAcceptedMessage, Long> prepared() {
        return bookingAcceptedMessage -> {
            log.info("The booking with id {} is prepared.", bookingAcceptedMessage.bookingId());
            return bookingAcceptedMessage.bookingId();
        };
    }

    @Bean
    public Function<Flux<Long>, Flux<BookingNotifiedMessage>> notified() {
        return bookingFlux -> bookingFlux.map(bookingId -> {
            log.info("The booking with id {} is notified.", bookingId);
            return new BookingNotifiedMessage(bookingId);
        });
    }
}
