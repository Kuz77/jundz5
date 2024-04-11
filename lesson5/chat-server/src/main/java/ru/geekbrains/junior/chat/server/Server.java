//package ru.geekbrains.junior.chat.server;
//
//import java.io.IOException;
//import java.net.ServerSocket;
//import java.net.Socket;
//
//public class Server {
//
//    private final ServerSocket serverSocket;
//
//    public Server(ServerSocket serverSocket) {
//        this.serverSocket = serverSocket;
//    }
//
//    public void runServer(){
//        while (!serverSocket.isClosed()){
//            try {
//                Socket socket = serverSocket.accept();
//                System.out.println("Подключен новый клиент!");
//                ClientManager clientManager = new ClientManager(socket);
//                Thread thread = new Thread(clientManager);
//                thread.start();
//            }
//            catch (IOException e){
//
//            }
//
//        }
//    }
//
//    /**
//     * Закрытие объекта ServerSocket, в случае возникновения исключения
//     */
//    private void closeSocket()
//    {
//        try{
//            if (serverSocket != null) serverSocket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//}

package ru.geekbrains.junior.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class Server {

    private final ServerSocket serverSocket;
    private final CopyOnWriteArrayList<ClientManager> clients;
    private final BlockingQueue<String> messageQueue;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        this.clients = new CopyOnWriteArrayList<>();
        this.messageQueue = new LinkedBlockingQueue<>();
    }

    public void runServer() {
        startMessageSender();
        while (!serverSocket.isClosed()) {
            try {
                ClientManager clientManager = new ClientManager(serverSocket.accept(), messageQueue);
                clients.add(clientManager);
                System.out.println(clientManager.getName() + " connected.");
                Thread thread = new Thread(clientManager);
                thread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startMessageSender() {
        Thread messageSender = new Thread(() -> {
            while (!serverSocket.isClosed()) {
                try {
                    String message = messageQueue.take();
                    sendToAllClients(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        messageSender.start();
    }

    private void sendToAllClients(String message) {
        for (ClientManager client : clients) {
            try {
                client.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
