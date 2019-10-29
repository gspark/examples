package com.shrill.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Simplistic telnet client.
 */
public final class TelnetClient {

//    static final boolean SSL = System.getProperty("ssl") != null;
    private final String host;
    private final int port;
    private final boolean ssl;

//    private TcpClient backwardCli;
    private Channel channel;

    public TelnetClient(String host, int port, boolean ssl) {
        this.host = host;
        this.port = port;
        this.ssl = ssl;
    }

//    public void setBackwardSrv(TcpClient backwardCli) {
//        this.backwardCli = backwardCli;
//    }

    public void connect() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    doconnect();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public void doconnect() throws IOException, InterruptedException {
        // Configure SSL.
        final SslContext sslCtx;

        if (this.ssl) {
            sslCtx = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                .channel(NioSocketChannel.class)
                .handler(new TelnetClientInitializer(this.host, this.port, sslCtx));

            // Start the connection attempt.
            channel = b.connect(host, port).sync().channel();
            channel.closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    public void sendmsg(Object o) {
        channel.writeAndFlush(o);
    }
}
