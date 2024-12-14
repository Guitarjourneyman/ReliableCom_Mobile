package com.example.reliablecom.main;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Ack implements Serializable {
	private static final long serialVersionUID = 1L; // ����ȭ ���� ���� ID

    public boolean isCompleted; // ��� �迭 ���� 1���� ����
    private int sizeOfMessage;   // ����Ʈ �迭 ũ��
    public byte[] byteMessage;      // ����Ʈ �迭
    private String time;         // Ÿ�ӽ�����

    // ������
    public Ack(byte[] byteArray) {
        if (byteArray == null) {
            throw new IllegalArgumentException("Byte array cannot be null");
        }
        this.byteMessage = byteArray;
        this.sizeOfMessage = byteArray.length;
        this.isCompleted = true; // ��� ���� 1���� Ȯ��
        // ���� �ð��� hh:mm:ss.SSS �������� ��������
        String timeStamp = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());
        this.time = " From Window " + "[" + timeStamp + "]";              // Ÿ�ӽ����� ����
    }


    // Getter �޼���
    public boolean isCompleted() {
        return isCompleted;
    }

    public int getSizeOfMessage() {
        return sizeOfMessage;
    }

    public byte[] getMessage() {
        return byteMessage;
    }

    public String getTime() {
        return time;
    }

    // toString �޼��� �������̵� (����� �� ��¿�)
    @Override
    public String toString() {
        return "Ack{" +
                "isCompleted=" + isCompleted +
                ", sizeOfMessage=" + sizeOfMessage +
                ", message=" + (byteMessage != null ? new String(byteMessage) : "null") +
                ", time='" + time + '\'' +
                '}';
    }
}

