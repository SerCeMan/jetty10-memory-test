package me.serce;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.WriteCallback;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
//import reactor.core.publisher.Flux;
//import io.netty.buffer.Unpooled;
//import reactor.netty.http.client.HttpClient;
//import reactor.netty.http.websocket.WebsocketOutbound;
//import reactor.netty.resources.ConnectionProvider;
//
//import java.nio.charset.StandardCharsets;
//import java.time.Duration;
//import java.util.Collections;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.CountDownLatch;

public class LoadTest {

    public static class Socket implements org.eclipse.jetty.websocket.api.WebSocketListener {

        private final CountDownLatch latch;
        Session session;

        public Socket(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void onWebSocketBinary(byte[] payload, int offset, int len) {
            if (new String(payload, offset, len).equals("hello")) {
                latch.countDown();
            } else {
                System.out.println("WTF?");
            }
        }

        @Override
        public void onWebSocketText(String message) {
            if (message.equals("hello")) {

            }
            System.out.println("WTF?");
        }

        @Override
        public void onWebSocketClose(int statusCode, String reason) {

        }

        @Override
        public void onWebSocketConnect(Session session) {
            this.session = session;
            this.session.getRemote() //
                .sendString("hello", new WriteCallback() {
                    @Override
                    public void writeFailed(Throwable x) {
                    }

                    @Override
                    public void writeSuccess() {
                    }
                });
        }

        @Override
        public void onWebSocketError(Throwable cause) {

        }
    }

    @Test
    void test1000Connections() throws Exception {
        var client = new WebSocketClient();
        client.start();
        var sockets = 10_000;
        var latch = new CountDownLatch(sockets);

        var sessions = new ArrayList<Session>();
        for (int i = 0; i < sockets; i++) {
            try {
                sessions.add(client.connect(new Socket(latch), URI.create("ws://localhost:8083/ws")).get());
            } catch (Exception e) {
                throw new RuntimeException("failed after " + i);
            }
        }

        latch.await();
        for (Session session : sessions) {
            session.close();
        }


//        HttpClient client = HttpClient.create(ConnectionProvider.builder("new")
//            .maxConnections(Integer.MAX_VALUE)
//            .pendingAcquireTimeout(Duration.ofMillis(0))
//            .pendingAcquireMaxCount(-1)
//            .build());
//
//
//        var sockets = 10_000;
//        var latch = new CountDownLatch(sockets);
//        var outs = Collections.<WebsocketOutbound>newSetFromMap(new ConcurrentHashMap<>());
//        for (int i = 0; i < sockets; i++) {
//            Thread.sleep(1);
//            client.websocket()
//                .uri("ws://localhost:8083/ws")
//                .handle((inbound, outbound) -> {
//                    inbound.receive()
//                        .asString()
//                        .subscribe((r) -> {
//                            latch.countDown();
//                        });
//
//                    final byte[] msgBytes = "hello".getBytes(StandardCharsets.UTF_8);
//                    outs.add(outbound);
//                    return outbound.send(Flux.just(Unpooled.wrappedBuffer(msgBytes), Unpooled.wrappedBuffer(msgBytes)))
//                        .neverComplete();
//                })
//                .doOnError(Throwable::printStackTrace)
//                .subscribe();
//        }
//
//        latch.await();
//        for (WebsocketOutbound out : outs) {
//            out.sendClose().block();
//        }
    }
}
