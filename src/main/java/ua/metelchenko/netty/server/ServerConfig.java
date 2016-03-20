package ua.metelchenko.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import io.netty.channel.Channel;

public class ServerConfig {

    private final int port;

    public ServerConfig(int port) {
        this.port = port;
    }

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public void start() throws Exception {

        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ServerInitializer());
            Channel ch = b.bind(port).sync().channel();
            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void stop() {
        try {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
