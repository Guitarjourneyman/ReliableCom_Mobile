package com.example.reliablecom.client_Source;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

import com.example.reliablecom.MainActivity;

import java.util.Arrays;

public class UDPCheckThread implements Runnable {
   private final TCPSend tcpsend;
    private final TcpSocketConnection tcpConnection;
    private final Handler uiHandler;

    public UDPCheckThread(TcpSocketConnection tcpConnection,TCPSend tcpsend) {
        this.tcpsend = tcpsend;
        this.tcpConnection = tcpConnection;

        this.uiHandler = new Handler(Looper.getMainLooper()); // UI 작업을 처리할 Handler
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (!Arrays.equals(UDPReceive.checkNewMessage, UDPReceive.lastMessage)) {
                    boolean allBitsOne = true;
                    for (byte b : UDPReceive.checkNewMessage) {
                        if (b != (byte) 0xFF) {
                            allBitsOne = false;
                            break;
                        }
                    }

                    if (allBitsOne) {
                        Log.d("UDPCheckThread", "All packets received");
                        tcpsend.sendMessage_tcp_alltrue(allBitsOne);

                        boolean allRowsValid = true;
                        for (int i = 0; i < UDPReceive.imageData.length; i++) {
                            if (UDPReceive.imageData[i] == null) {
                                allRowsValid = false;
                                Log.e("UDPCheckThread", "Missing data for row: " + i);
                            }
                        }

                        if (allRowsValid) {
                            byte[] imageData1D = combineImageData(UDPReceive.imageData);
                            if (imageData1D != null) {
                                updateImageView(imageData1D);
                            } else {
                                Log.e("UDPCheckThread", "Failed to combine image data");
                            }
                        }

                        UDPReceive.initializePacketTracking();
                        //Arrays.fill(receiverUdp.checkNewMessage, (byte) 0);
                        UDPReceive.receivedMessageNum++;
                        Log.d("UDPCheckThread", "receivedMessageNum: " + UDPReceive.receivedMessageNum);
                    }

                    UDPReceive.lastMessage = Arrays.copyOf(UDPReceive.checkNewMessage, UDPReceive.checkNewMessage.length);
                }

                Thread.sleep(50);
            } catch (InterruptedException e) {
                Log.e("UDPCheckThread", "Thread was interrupted", e);
                break;
            }
        }
    }

    private byte[] combineImageData(byte[][] imageData2D) {
        if (imageData2D == null || imageData2D.length == 0) return null;

        int rows = imageData2D.length;
        final int FIXED_DATA_SIZE = 1014;
        int totalLength = rows * FIXED_DATA_SIZE;
        byte[] imageData1D = new byte[totalLength];
        int index = 0;

        for (byte[] row : imageData2D) {
            if (row != null) {
                int lengthToCopy = Math.min(row.length, FIXED_DATA_SIZE);
                System.arraycopy(row, 0, imageData1D, index, lengthToCopy);
                index += FIXED_DATA_SIZE;
            } else {
                Arrays.fill(imageData1D, index, index + FIXED_DATA_SIZE, (byte) 0);
                index += FIXED_DATA_SIZE;
            }
        }
        return imageData1D;
    }

    private void updateImageView(byte[] imageData) {
        uiHandler.post(() -> {
            try {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                if (bitmap != null) {
                    MainActivity.imageView.setImageBitmap(bitmap);
                } else {
                    Log.e("UDPCheckThread", "Failed to decode image data");
                }
            } catch (Exception e) {
                Log.e("UDPCheckThread", "Error updating ImageView", e);
            }
        });
    }

    public static void printByteArrayAsBinary(byte[] byteArray) {
        for (byte b : byteArray) {
            //        Ʈ   0   1     ȯ
            String binaryString = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            System.out.println(binaryString); //   ȯ
        }
    }

}
