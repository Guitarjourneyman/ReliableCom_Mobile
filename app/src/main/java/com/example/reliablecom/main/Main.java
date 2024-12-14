package com.example.reliablecom.main;

import com.example.reliablecom.MainActivity;
import com.example.reliablecom.client_Source.*;



public class Main implements Runnable {

    //이미지 보내는 앱 프로젝트

    /* Client */
    public  static UDPReceive receiverUdp;
    private static TcpSocketConnection tcpConnection;



    @Override
    public void run() {
        ComReceive(); // 네트워크 소켓 프로그래밍 호출
    }

    /*   The methods are necessary by client */

    public static void ComSetupResponse () {
        tcpConnection = new TcpSocketConnection();
        receiverUdp = new UDPReceive();
        String serverIP = receiverUdp.startConnect_to_tcp();
        tcpConnection.startClient(serverIP);
        //GUI.consoleArea.append("Client: "+serverIP+"   TCP    ϰ      Ǿ    ϴ . \n");
    }

    public void ComReceive() {


        receiverUdp = new UDPReceive();
        new Thread(() -> receiverUdp.startServer()).start();


    }
}


