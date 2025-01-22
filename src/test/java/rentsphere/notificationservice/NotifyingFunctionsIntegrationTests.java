package rentsphere.notificationservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.cloud.function.context.test.FunctionalSpringBootTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.function.Function;
import static org.assertj.core.api.Assertions.assertThat;

@FunctionalSpringBootTest
public class NotifyingFunctionsIntegrationTests {

    @Autowired
    private FunctionCatalog catalog;

    @Test
    void prepareBooking() {
        Function<BookingAcceptedMessage, Long> pack = catalog.lookup(Function.class, "prepared");
        long bookingId = 121;
        assertThat(pack.apply(new BookingAcceptedMessage(bookingId))).isEqualTo(bookingId);
    }

    @Test
    void labelOrder() {
        Function<Flux<Long>, Flux<BookingNotifiedMessage>> label = catalog.lookup(Function.class, "notified");
        Flux<Long> orderId = Flux.just(121L);

        StepVerifier.create(label.apply(orderId))
                .expectNextMatches(notifiedBooking ->
                        notifiedBooking.equals(new BookingNotifiedMessage(121L)))
                .verifyComplete();
    }

    @Test
    void packAndLabelOrder() {
        Function<BookingAcceptedMessage, Flux<BookingNotifiedMessage>>
                packAndLabel = catalog.lookup(
                Function.class,
                "prepared|notified");
        long bookingId = 121;

        StepVerifier.create(packAndLabel.apply(
                        new BookingAcceptedMessage(bookingId)
                ))
                .expectNextMatches(notifiedBooking ->
                        notifiedBooking.equals(new BookingNotifiedMessage(bookingId)))
                .verifyComplete();
    }
}
