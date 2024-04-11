//package ru.geekbrains.junior.chat.client;
//
//import java.io.*;
//import java.net.Socket;
//import java.util.Scanner;
//
//public class Client {
//
//    private final Socket socket;
//    private final String name;
//    private BufferedWriter bufferedWriter;
//    private BufferedReader bufferedReader;
//
//    public Client(Socket socket, String userName){
//        this.socket = socket;
//        name = userName;
//        try {
//            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        }
//        catch (IOException e){
//            closeEverything(socket, bufferedReader, bufferedWriter);
//        }
//    }
//
//    /**
//     * Отправить сообщение
//     */
//    public void sendMessage(){
//
//        try {
//            bufferedWriter.write(name);
//            bufferedWriter.newLine();
//            bufferedWriter.flush();
//            Scanner scanner = new Scanner(System.in);
//            while (socket.isConnected()){
//                String message = scanner.nextLine();
//                bufferedWriter.write(name + ": "+ message);
//                bufferedWriter.newLine();
//                bufferedWriter.flush();
//            }
//        }
//        catch (IOException e){
//            closeEverything(socket, bufferedReader, bufferedWriter);
//        }
//    }
//
//    /**
//     * Слушатель для входящих сообщений
//     */
//    public void listenForMessage(){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                String message;
//                while (socket.isConnected()){
//                    try {
//                        message = bufferedReader.readLine();
//                        System.out.println(message);
//                    }
//                    catch (IOException e){
//                        closeEverything(socket, bufferedReader, bufferedWriter);
//                        break;
//                    }
//                }
//            }
//        }).start();
//        System.out.println("Завершение работы клиента");
//    }
//
//    /**
//     * Завершение работы всех потоков, закрытие клиентского сокета
//     * @param socket клиентский сокет
//     * @param bufferedReader буфер для чтения данных
//     * @param bufferedWriter буфер для отправки данных
//     */
//    private void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
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
//}

package ru.geekbrains.junior.chat.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private final Socket socket;
    private final String name;
    private final BufferedReader bufferedReader;
    private final BufferedWriter bufferedWriter;

    public Client(Socket socket, String userName) throws IOException {
        this.socket = socket;
        this.name = userName;
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public void sendMessage(String message) throws IOException {
        bufferedWriter.write(name + ": " + message);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    public void listenForMessage() {
        new Thread(() -> {
            try {
                while (socket.isConnected()) {
                    String message = bufferedReader.readLine();
                    if (message != null) {
                        System.out.println(message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

}