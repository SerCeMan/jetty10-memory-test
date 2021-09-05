package me.serce;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WriteCallback;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class LoadTest {

    public static class Socket implements org.eclipse.jetty.websocket.api.WebSocketListener {

        private final CountDownLatch latch;

        public Socket(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void onWebSocketBinary(byte[] payload, int offset, int len) {
            if (new String(payload, offset, len).equals("hello")) {
                latch.countDown();
            }
        }

        @Override
        public void onWebSocketText(String message) {
        }

        @Override
        public void onWebSocketClose(int statusCode, String reason) {
        }

        @Override
        public void onWebSocketConnect(Session session) {
            session.getRemote() //
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
    }
}
