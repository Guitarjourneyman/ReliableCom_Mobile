package com.example.reliablecom.client_Source;



import android.widget.TextView;

import com.example.reliablecom.MainActivity;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;


public class UDPReceive {

    private static final int PORT = 1996;
    private static final int BUFFER_SIZE = 1024;
    private static final int TOTAL_PACKETS = 63; // ��ü ��Ŷ �� (�ʿ信 �°� ����)
    private static final boolean MESSAGE_NUM = true;
    private static final boolean PACKET_NUM = false;
    //private JTextArea receivedMessagesArea;  // GUI�� receive message â
    private static int receive_message_num = 0;
    private volatile boolean newMessageReceived_udp = false;
    public static int receivedMessageNum = 1; //���� �ް� �ִ� �޽��� ��ȣ
    public static int array_index= 0;
    public static int ignored_bits = 0;
    private static int checkSerial; //���� UDP �޽����� �� ��° �޽������� ����
    //edit: Defined socket as a static 
    private static DatagramSocket socket;
    
    public static byte[] checkNewMessage; // ���� ��Ŷ�� üũ�ϴ� �迭
    public static byte[] lastMessage; // ���� �迭(�迭�� ��ȭ�� ������ ���� ack ����)
    public static byte[][] imageData; // 패킷 데이터를 저장할 배열

    // �����ڿ��� JTextArea ���� ����
    public UDPReceive() {
        //this.receivedMessagesArea = GUI.receivedMessagesArea;

    }
    

    
    //���� UDP �޽����� �� ��° �޽������� �����ϰ� �ִ� checkSerial ������ ����ϴ� �޼ҵ�
    public int Print_checkSerial() {
    	return checkSerial;
    }
    
    
    public static void reset_message_num() {
        receive_message_num = 0;
    }
    
    public boolean hasNewMessage() {
        return newMessageReceived_udp;
    }
    
    public void resetNewMessageFlag() {
        newMessageReceived_udp = false;
    }

    // getLeading을 true로 주면 "_"를 기준으로 앞의 숫자를, false로 주면 뒤의 숫자를 return함
    public static int extractNumberPart(String input, boolean getLeading) {
        // 정규식을 사용하여 숫자와 `_`를 기준으로 문자열을 분리
        String[] parts = input.split("_");


        String leadingNumber = parts[0].replaceAll("\\D", ""); // 앞부분 숫자만 추출

        String trailingNumber = parts[1].replaceAll("\\D", ""); // 뒷부분 숫자만 추출

        // getLeading이 true일 경우 앞부분 숫자 반환, false일 경우 뒷부분 숫자 반환
        String numberString = getLeading ? leadingNumber : trailingNumber;

        // 빈 문자열을 처리하여 int로 변환
        return numberString.isEmpty() ? 0 : Integer.parseInt(numberString);


    }

    public static void initializePacketTracking() {
        array_index = (TOTAL_PACKETS + 7) / 8;
        ignored_bits = 8 - (TOTAL_PACKETS % 8);
        checkNewMessage = new byte[array_index];
        imageData = new byte[TOTAL_PACKETS][]; // 이미지 데이터 저장

        // 무시할 비트 초기화
        for (int i = array_index * 8 - ignored_bits + 1; i <= array_index * 8; i++) {
            SetNewMsgBit(i, null, 0);
        }
        lastMessage = checkNewMessage.clone();
    }


    public void startServer() {
        try {
            initializePacketTracking();
            socket = new DatagramSocket(PORT);
            System.out.println("UDP Server started on port " + PORT + ". Waiting for messages...");

            // ���� ������ �޽��� ��� ����
            while (true) {
                try {
                    byte[] buffer = new byte[BUFFER_SIZE];
                    DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);

                    // ������ ����
                    socket.receive(receivePacket);

                    // ���ŵ� ������ ó��
                    String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    InetAddress senderAddress = receivePacket.getAddress();
                    String senderIP = senderAddress.getHostAddress();
                    String timeStamp = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());
                    String truncatedMessage = receivedMessage.length() > 10
                            ? receivedMessage.substring(0, 10)
                            : receivedMessage;
                    
                    //int before =0;
                    // ���� �޽��� GUI�� ǥ��
                    receive_message_num++;
                    //receivedMessagesArea.append("[" + receive_message_num + "] Received UDP message from " + senderIP + ": " + truncatedMessage + " [" + timeStamp + "]\n");
                    
                    //System.out.println("I got Message: " + truncatedMessage);

                    // �޽��� ��ȣ�� ��Ŷ ��ȣ ����
                    int message_num = extractNumberPart(truncatedMessage,MESSAGE_NUM);
                    int packet_num = extractNumberPart(truncatedMessage,PACKET_NUM);


                    // 유효한 패킷 번호인지 확인
                    if (packet_num > 0 && packet_num <= TOTAL_PACKETS) {
                        int headerLength = Integer.toString(message_num).length() + Integer.toString(packet_num).length() + 2; // "_" 문자 2개 포함
                        byte[] imagePacketData = Arrays.copyOfRange(receivePacket.getData(), headerLength, receivePacket.getLength());

                        // 패킷 번호가 10 이상이면 크기 조정
                        if (packet_num >= 10) {
                            byte[] extendedPacketData = new byte[imagePacketData.length + 1]; // 기존 크기 + 1
                            System.arraycopy(imagePacketData, 0, extendedPacketData, 0, imagePacketData.length); // 기존 데이터 복사
                            extendedPacketData[extendedPacketData.length - 1] = 0; // 마지막에 빈 공간(0) 추가
                            imagePacketData = extendedPacketData; // 크기 조정된 배열로 업데이트
                        }
                        if (receivedMessageNum == message_num) {
                            SetNewMsgBit(packet_num, imagePacketData, 1);

                        }
                    }
                    else {
                        System.out.println("Received wrong message");
                    }

                   

                } catch (NumberFormatException e) {
                    System.out.println("Invalid number format in received message: " + e.getMessage());
                } catch (SocketException e) {
                    System.out.println("Socket error occurred: " + e.getMessage());
                    break; // ���Ͽ� ������ ����� ������ Ż���Ͽ� ������ �ߴ��մϴ�.
                } catch (SecurityException e) {
                    System.out.println("Security exception: " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    System.out.println("Illegal argument: " + e.getMessage());
                } catch (Exception e) {
                    System.out.println("Unexpected error while receiving data: " + e.getMessage());
                    e.printStackTrace(); // �߰����� ���� �α׸� ����Ͽ� ������ �� ��Ȯ�� �ľ��� �� �ְ� �մϴ�.
                }
            }
        } catch (SocketException e) {
            System.out.println("Failed to bind UDP socket to port " + PORT + ": " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Server startup error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("UDP Server socket closed.");
            }
        }
    }

    public String startConnect_to_tcp() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(PORT);
            System.out.println("UDP Server started on port " + PORT + ". Waiting for connect to tcp...");

            // 버퍼 생성
            byte[] buffer = new byte[BUFFER_SIZE];

            // 수신할 패킷 생성
            DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);

            // 데이터 수신 (메시지가 올 때까지 대기)
            socket.receive(receivePacket);

            // 송신자 IP 주소 가져오기
            InetAddress senderAddress = receivePacket.getAddress();
            String senderIP = senderAddress.getHostAddress();

            // IP 주소가 유효하면 반환, 유효하지 않으면 null 반환
            if (senderIP != null && !senderIP.isEmpty()) {
                System.out.println("This is serverIP: " + senderIP);
                return senderIP;
            } else {
                System.out.println("No server IP");
                return null;
            }
        } catch (SocketException e) {
            System.out.println("Failed to bind UDP socket to port " + PORT + ": " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Server startup error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 메시지를 수신한 후 소켓을 닫아 더 이상 메시지를 받지 않도록 함.
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("UDP Server socket closed.");
            }
        }
        return null; // 메시지를 수신하지 못했거나 오류 발생 시 null 반환
    }


    // ������ ��Ŷ ��ȣ�� �´� bit�� set ��Ŵ
    public static void SetNewMsgBit(int packet_num,  byte[] imagePacketData, int mode) {
            // packet_num�� ��ġ�� �ش��ϴ� ��Ʈ�� ����(0��° ��Ʈ���� ä��)
            int byteIndex = (packet_num-1) / 8;   // �ش� ��Ʈ�� ���� ����Ʈ �ε���
            int bitIndex = (packet_num-1) % 8;    // �ش� ����Ʈ ���� ��Ʈ ��ġ

            // �ش� ����Ʈ ������ bitIndex ��ġ�� ��Ʈ�� 0���� 1���� Ȯ��
            if ((checkNewMessage[byteIndex] & (1 << bitIndex)) == 0) {
                // ��Ʈ�� 0�̶�� 1�� ����
            	checkNewMessage[byteIndex] |= (1 << bitIndex);
                //System.out.println("Set checkNewMessage[" + packet_num + "]:");
                UDPCheckThread.printByteArrayAsBinary(checkNewMessage); //�迭 ���
                if(mode ==1) {
                    imageData[packet_num - 1] = imagePacketData; // 이미지 데이터 저장
                    MainActivity.consoleArea.setText(receivedMessageNum + "번 메시지 패킷:\n" + appendByteArrayAsBinary(checkNewMessage));
                }
            } else {
                // �̹� ��Ʈ�� 1�� ���
                //System.out.println("checkNewMessage[" + packet_num + "] is already set to 1.");
              }
           }
    public static String appendByteArrayAsBinary(byte[] byteArray) {
        StringBuilder binaryOutput = new StringBuilder();
        for (byte b : byteArray) {
            // 각 byte 값을 이진 문자열로 변환
            String binaryString = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            binaryOutput.append(binaryString).append("\n"); // 이진 문자열을 문자열에 추가
        }

        // 완성된 문자열을 반환
        return binaryOutput.toString();
    }

    //Server���� Reset ��û�� �� ��
    public static void closeUDPbyReset() {
    	socket.close();
    }
  }            
        




