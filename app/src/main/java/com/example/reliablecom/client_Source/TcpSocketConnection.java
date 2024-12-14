package com.example.reliablecom.client_Source;

import java.io.*;
import java.net.Socket;

public class TcpSocketConnection {
    private static final int PORT = 49152;
    private Socket socket;
    private TCPSend client; // SenderViewModel �ν��Ͻ�
    


    public void startClient(String serverIP) {
        try {
            socket = new Socket(serverIP, PORT);
            client = new TCPSend(socket);
            // UDPCheckThread 생성 및 실행
            UDPCheckThread udpCheckThread = new UDPCheckThread(this, client);
            new Thread(udpCheckThread).start();

            System.out.println("Server: " + serverIP + " is connected by TCP");
            System.out.println("My IP: " + socket.getLocalAddress());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 
    // TCP ���� �޽����� �����ϴ� �޼���
    public void sendAckMessage(String message) {
        if (client != null) {
            client.sendMessage_tcp(message); // Client_Tcp�� ����Ͽ� �޽��� ����
        } else {
        	System.out.println("TCPSender is null");
        }
    }
    //byte�迭�� TCP check �޽����� �����ϴ� �޼���
    public void sendAckMessage(byte[] message) {
        if (client != null) {
            client.sendMessage_tcp(message); // Client_Tcp�� ����Ͽ� �޽��� ����
        } else {
        	System.out.println("TCPSender is null");
        }
    }
    
    //byte�迭�� TCP check �޽����� �����ϴ� �޼���
    public void sendAckMessage_alltrue(boolean message) {
        if (client != null) {
            client.sendMessage_tcp_alltrue(message); // Client_Tcp�� ����Ͽ� �޽��� ����
        } else {
        	System.out.println("TCPSender is null");
        }
    }
    public void sendAckObject(byte[] byteArray) {
    	if (client != null) {
            client.sendAckObject(byteArray);
        } else {
            System.out.println("TCPSender is null");
        }
    }
    
    
    
    // ���� ���� �޼��� �߰�
    public void closeSocket() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("TCP ������ �������ϴ�.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
