package net.gallery.library.sample.server.handle;


import net.gallery.library.sample.foo.Foo;
import net.gallery.library.clink.core.Connector;
import net.gallery.library.clink.core.Packet;
import net.gallery.library.clink.core.ReceivePacket;
import net.gallery.library.clink.utils.CloseUtils;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class ClientHandler extends Connector {
    private final File cachePath;
    private final ClientHandlerCallback clientHandlerCallback;
    private final String ip;
//    private final String clientInfo;

    public ClientHandler(SocketChannel socketChannel, ClientHandlerCallback clientHandlerCallback, File cachePath) throws IOException {
        this.clientHandlerCallback = clientHandlerCallback;
//        this.clientInfo = socketChannel.getRemoteAddress().toString();
        this.cachePath = cachePath;
        this.ip=((InetSocketAddress)socketChannel.getRemoteAddress()).getHostString();

        setup(socketChannel);
    }

    public void exit() {
        CloseUtils.close(this);
    }

    @Override
    public void onChannelClosed(SocketChannel channel) {
        super.onChannelClosed(channel);
        exitBySelf();
    }

    @Override
    protected File createNewReceiveFile() {
        return Foo.createRandomTemp(cachePath);
    }

    @Override
    protected void onReceivedPacket(ReceivePacket packet) {
        super.onReceivedPacket(packet);
        if (packet.type() == Packet.TYPE_MEMORY_STRING) {
            String string = (String) packet.entity();
//            System.out.println(key.toString() + ":" + string);
            clientHandlerCallback.onNewMessageArrived(this, string,this.ip);
        }
    }

    private void exitBySelf() {
        exit();
        clientHandlerCallback.onSelfClosed(this);
    }

    public interface ClientHandlerCallback {
        void onSelfClosed(ClientHandler handler);

        void onNewMessageArrived(ClientHandler handler, String msg, String ip);
    }


}
