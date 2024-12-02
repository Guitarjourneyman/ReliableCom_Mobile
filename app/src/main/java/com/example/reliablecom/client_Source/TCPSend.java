package com.example.reliablecom.client_Source;
import com.example.reliablecom.main.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TCPSend {
    
    private Socket socket;
    private PrintWriter out = null;
    private DataOutputStream dataOutputStream = null;
    private BufferedReader in = null; // ���ſ� BufferedReader �߰�
    private DataInputStream dataInputStream = null; // ����Ʈ ���ſ� DataInputStream �߰�
    private ObjectOutputStream objectOutputStream = null;
    // Socket�� �Ķ���ͷ� �޴� ������
    public TCPSend(Socket socket) {
        this.socket = socket;
        try {
            // PrintWriter �ʱ�ȭ
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            // byteArray�� ���� �߰���
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            //Ack��ü ������ ���� �߰�
            //objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            /*Reset�� ���� tcp ����*/
            // ���� ��Ʈ�� �ʱ�ȭ
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            dataInputStream = new DataInputStream(socket.getInputStream());
            // ���� ������ ����
            startResetReceiving();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // TCP �޽����� �����ϴ� �޼���
    public void sendMessage_tcp(String message) {
        try {
            // ���� �ð��� hh:mm:ss.SSS �������� ��������
            String timeStamp = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());

            // �޽��� ���� + Ÿ�ӽ����� �߰�
            out.println(message + " From Window " + "[" + timeStamp + "]");
            System.out.println(message + " Ack message gets sent");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 // byte�迭�� TCP check �޽����� �����ϴ� �޼���
    public void sendMessage_tcp(byte[] byteArray) {
        try {
            // ���� �ð��� hh:mm:ss.SSS �������� ��������
            String timeStamp = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());

            // �޽��� ���� + Ÿ�ӽ����� �߰�
            // byte �迭�� ���̸� ���� ����
            dataOutputStream.writeInt(byteArray.length);
            // byte �迭 ����
            dataOutputStream.write(byteArray);
            // String �޽��� ����
            dataOutputStream.writeUTF(" From Window " + "[" + timeStamp + "]");
            
            System.out.println(byteArray + " Ack message gets sent");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //��� ��Ʈ�� 1�̸� true�� �۽�
    public void sendMessage_tcp_alltrue(boolean byteArrayAllTrue) {
        try {
            // ���� �ð��� hh:mm:ss.SSS �������� ��������
            String timeStamp = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());

            // boolean ���� 1��Ʈ�� ��ȯ (true -> 1, false -> 0)
            byte bitToSend = (byte) (byteArrayAllTrue ? 1 : 0);

            // 1��Ʈ ����
            dataOutputStream.writeByte(bitToSend);

            // Ÿ�ӽ������� ������ Ȯ�� �޽��� ����
            dataOutputStream.writeUTF(" From Window " + "[" + timeStamp + "]");

            System.out.println(byteArrayAllTrue + " Ack message gets sent");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
public void sendAckObject(byte[] byteArray) {
	try {
            
			//Ack��ü ����
        	Ack ack = new Ack(byteArray);
        
           // Ack ��ü ����
           objectOutputStream.writeObject(ack);
           System.out.println("Ack object sent.");

       } catch (Exception e) {
           e.printStackTrace();
       }
		
	}



    // ���� ���� �޼���
    public void closeSocket() {
        try {
            if (out != null) {
                out.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("TCP socket is closed.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
 // �޽��� ������ ���� ������ ���� �޼���
    private void startResetReceiving() {
        Thread receiveResetThread = new Thread(() -> {
            try {
                String message;
                while ((message = in.readLine()) != null) { // EOF�� Ȯ���ϸ� ����
                    System.out.println("Received message: " + message);
                }
                System.out.println("Server closed the connection.");
                //Message num �ʱ�ȭ
                UDPReceive.receivedMessageNum =1;
                UDPReceive.closeUDPbyReset();
                Main.receiverUdp = null;
            } catch (IOException e) {
                System.out.println("Connection closed.");
            }
        });
        receiveResetThread.start();
    }

	
}
