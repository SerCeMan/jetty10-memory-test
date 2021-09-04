package me.serce;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.eclipse.jetty.websocket.api.WriteCallback;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;

public class WebSocketServlet extends org.eclipse.jetty.websocket.servlet.WebSocketServlet {

    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.setCreator((req, resp) -> {
            removeAttributes(req);
            return new WebSocketListener() {
                private RemoteEndpoint remote;

                @Override
                public void onWebSocketBinary(byte[] payload, int offset, int len) {
                    remote.sendBytes(ByteBuffer.wrap(payload, offset, len), new WriteCallback() {
                        @Override
                        public void writeFailed(Throwable x) {
                        }

                        @Override
                        public void writeSuccess() {
                        }
                    });
                }

                @Override
                public void onWebSocketText(String message) {
                    remote.sendBytes(ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8)), new WriteCallback() {
                        @Override
                        public void writeFailed(Throwable x) {
                        }

                        @Override
                        public void writeSuccess() {
                        }
                    });
                }

                @Override
                public void onWebSocketClose(int statusCode, String reason) {
                    System.out.println("closed");
                }

                @Override
                public void onWebSocketConnect(Session session) {
                    System.out.println("connected");
                    this.remote = session.getRemote();
                }

                @Override
                public void onWebSocketError(Throwable cause) {
                }
            };
        });
    }

    private static void removeAttributes(ServletUpgradeRequest req) {
        for (String attributeName : new HashSet<>(req.getServletAttributes().keySet())) {
            req.getHttpServletRequest().removeAttribute(attributeName);
        }
    }
}
