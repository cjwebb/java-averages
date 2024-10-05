package com.cjwebb.javaperf.playground.echo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;


public class EchoUDPServer {
    public static void main(String[] args) throws IOException {
        System.out.println("Listening on port 9000");

        try (DatagramChannel c = DatagramChannel.open()) {
            c.bind(new InetSocketAddress(9000));
            var bb = ByteBuffer.allocateDirect(4);

            while (true) {
                var address = c.receive(bb);
                bb.flip();
                c.send(bb, address);
                bb.clear();
            }
        }
    }
}
