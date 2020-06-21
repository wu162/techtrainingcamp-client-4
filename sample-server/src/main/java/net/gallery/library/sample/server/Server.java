package net.gallery.library.sample.server;

import net.gallery.library.sample.foo.Foo;
import net.gallery.library.clink.core.IoContext;
import net.gallery.library.clink.impl.IoSelectorProvider;

import java.io.File;
import java.io.IOException;

public class Server {
    public static void main(String[] args) throws IOException {
        File cachePath = Foo.getCacheDir("server");

        IoContext.setup()
                .ioProvider(new IoSelectorProvider())
                .start();

//        TCPServer tcpServer = new TCPServer(TCPConstants.PORT_SERVER, cachePath);
//        boolean isSucceed = tcpServer.start();
//        if (!isSucceed) {
            System.out.println("Start TCP server failed!");
//            return;
//        }
//
//        UDPProvider.start(TCPConstants.PORT_SERVER);
//
//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
//        String str;
//        do {
//            str = bufferedReader.readLine();
//            if ("00bye00".equalsIgnoreCase(str)) {
//                break;
//            }
//            // 发送字符串
//            tcpServer.broadcast(str);
//        } while (true);
//
//        UDPProvider.stop();
//        tcpServer.stop();
//
//        IoContext.close();
    }
}
