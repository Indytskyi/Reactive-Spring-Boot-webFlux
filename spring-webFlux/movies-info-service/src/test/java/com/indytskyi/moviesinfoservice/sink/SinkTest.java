package com.indytskyi.moviesinfoservice.sink;

import org.assertj.core.internal.bytebuddy.build.Plugin;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

public class SinkTest {

    @Test
    void sink() {
        //given
        Sinks.Many<Integer> replaySink = Sinks.many().replay().all();

        //when
        replaySink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        replaySink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

        //then
        Flux<Integer> integerFlux = replaySink.asFlux();

        integerFlux.subscribe((i) -> {
            System.out.println("Subscriber 1 : " +  i);
        });

        Flux<Integer> integerFlux1 = replaySink.asFlux();

        integerFlux1.subscribe((i) -> {
            System.out.println("Subscriber 2 : " +  i);
        });

        replaySink.tryEmitNext(3);

    }

    @Test
    void sinks_multicast() {
        //given
        Sinks.Many<Integer> multicast = Sinks.many().multicast().onBackpressureBuffer();

        //when
        multicast.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        multicast.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

        //then
        Flux<Integer> integerFlux = multicast.asFlux();

        integerFlux.subscribe((i) -> {
            System.out.println("Subscriber 1 : " +  i);
        });

        Flux<Integer> integerFlux1 = multicast.asFlux();

        integerFlux1.subscribe((i) -> {
            System.out.println("Subscriber 2 : " +  i);
        });

        multicast.tryEmitNext(3);

        Flux<Integer> integerFlux2 = multicast.asFlux();

        integerFlux2.subscribe((i) -> {
            System.out.println("Subscriber 3: " +  i);
        });

        multicast.tryEmitNext(4);


    }

    @Test
    void sinks_unicast() {
        //given
        Sinks.Many<Integer> unicast = Sinks.many().unicast().onBackpressureBuffer();

        //when
        unicast.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        unicast.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

        //then
        Flux<Integer> integerFlux = unicast.asFlux();

        integerFlux.subscribe((i) -> {
            System.out.println("Subscriber 1 : " +  i);
        });

        Flux<Integer> integerFlux1 = unicast.asFlux();

        integerFlux1.subscribe((i) -> {
            System.out.println("Subscriber 2 : " +  i);
        });

        unicast.tryEmitNext(3);



    }
}
