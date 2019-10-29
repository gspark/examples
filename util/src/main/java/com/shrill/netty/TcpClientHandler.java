package com.shrill.netty;

import com.shrill.util.PropertiesHelper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.nio.charset.StandardCharsets;

/**
 * Handler implementation for the echo client.  It initiates the ping-pong
 * traffic between the echo client and server by sending the first message to
 * the server.
 */
@ChannelHandler.Sharable
public class TcpClientHandler extends SimpleChannelInboundHandler {

    private TelnetClient clientd;

    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext){
        System.out.println("channelActive");
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause){
        cause.printStackTrace();
        channelHandlerContext.close();
    }

    @Override

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object info) throws Exception {
        ByteBuf buf = (ByteBuf)info;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req, StandardCharsets.UTF_8);

        System.out.println("Client received: " + body);
//        System.out.println("Client received: " + o.toString(CharsetUtil.UTF_8));

        if (null == clientd ) {
            connnectServerD();
        }
    }

    private void connnectServerD() {
        String port_d = PropertiesHelper.getProperty("server_d_port");
        if ( null == port_d || port_d.isEmpty() ) {
            port_d = "11070";
        }

        String server_d_ip = PropertiesHelper.getProperty("server_d_ip");
        if ( null == server_d_ip || server_d_ip.isEmpty() ) {
            server_d_ip = "47.99.122.103";
        }


        String server_d_ssl = PropertiesHelper.getProperty("server_d_ssl");
        if ( null == server_d_ip || server_d_ip.isEmpty() ) {
            server_d_ssl = "1";
        }

        this.clientd = new TelnetClient(server_d_ip, Integer.parseInt(port_d), "1".equals(server_d_ssl));
        clientd.connect();
    }
}
