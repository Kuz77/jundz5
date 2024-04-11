//package ru.geekbrains.junior.chat.server;
//
//import java.io.*;
//import java.net.Socket;
//import java.util.ArrayList;
//
//public class ClientManager implements Runnable{
//
//    private final Socket socket;
//    private String name;
//    private BufferedWriter bufferedWriter;
//    private BufferedReader bufferedReader;
//    public static ArrayList<ClientManager> clients = new ArrayList<>();
//
//    public ClientManager(Socket socket) {
//        this.socket = socket;
//        try {
//            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            name = bufferedReader.readLine();
//            clients.add(this);
//            System.out.println(name + " подключился к чату.");
//            broadcastMessage("Server: " + name + " подключился к чату.");
//        }
//        catch (IOException e){
//            closeEverything(socket, bufferedReader, bufferedWriter);
//        }
//    }
//
//    private void removeClient(){
//        clients.remove(this);
//        System.out.println(name + " покинул чат.");
//        broadcastMessage("Server: " + name + " покинул чат.");
//    }
//
//    private void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
//        removeClient();
//        try {
//            if (bufferedReader != null) {
//                bufferedReader.close();
//            }
//            if (bufferedWriter != null) {
//                bufferedWriter.close();
//            }
//            if (socket != null) {
//                socket.close();
//            }
//        }
//        catch (IOException e){
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Отправка сообщения всем слушателям
//     *
//     * @param message сообщение
//     */
//    private void broadcastMessage(String message){
//        for(ClientManager client : clients){
//            try {
//                if (!client.name.equals(name) && message != null) {
//                    client.bufferedWriter.write(message);
//                    client.bufferedWriter.newLine();
//                    client.bufferedWriter.flush();
//                }
//            }
//            catch (IOException e){
//                closeEverything(socket, bufferedReader, bufferedWriter);
//            }
//        }
//    }
//
//    /**
//     * Чтение сообщений ОТ клиента
//     */
//    @Override
//    public void run() {
//        String messageFromClient;
//        while (socket.isConnected()) {
//            try {
//                // Чтение данных
//                messageFromClient = bufferedReader.readLine();
//                /*if (massageFromClient == null) {
//                    // для  macOS
//                    closeEverything(socket, bufferedReader, bufferedWriter);
//                    break;
//                }*/
//                broadcastMessage(messageFromClient);
//            }
//            catch (IOException e){
//                closeEverything(socket, bufferedReader, bufferedWriter);
//                /*try {
//                    socket.close();
//                }
//                catch (IOException ex){
//                    ex.printStackTrace();
//                }*/
//                break;
//            }
//        }
//    }
//
//}


package ru.geekbrains.junior.chat.server;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class ClientManager implements Runnable {

    private final Socket socket;
    private final String name;
    private final BufferedReader bufferedReader;
    private final BufferedWriter bufferedWriter;
    private final BlockingQueue<String> messageQueue;
    private boolean running;

    public ClientManager(Socket socket, BlockingQueue<String> messageQueue) throws IOException {
        this.socket = socket;
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.messageQueue = messageQueue;
        this.name = bufferedReader.readLine();
    }

    public void sendMessage(String message) throws IOException {
        bufferedWriter.write(message);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    public String getName() {
        return name;
    }

    @Override
    public void run() {
        running = true;
        try {
            while (running) {
                String message = bufferedReader.readLine();
                if (message != null) {
                    messageQueue.put(name + ": " + message);
                } else {
                    running = false;
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}