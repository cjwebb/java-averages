package com.cjwebb.javaaverages.playground.echo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;


public class EchoServer {
    public static void main(String[] args) throws IOException {
        System.out.println("Listening on port 9000");

        try (ServerSocketChannel socketChannel = ServerSocketChannel.open()) {
            try(ServerSocket s = socketChannel.socket()) {
                s.bind(new InetSocketAddress(9000));
                var bb = ByteBuffer.allocateDirect(4);

                try(SocketChannel client = socketChannel.accept()) {
                    while (client.read(bb) > 0) {
                        bb.flip();
                        client.write(bb);
                        bb.clear();
                    }
                }
            }
        }
    }
}
