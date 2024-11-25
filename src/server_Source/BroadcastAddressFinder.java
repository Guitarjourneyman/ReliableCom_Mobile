package server_Source;
import java.net.*;
import java.util.Enumeration;

public class BroadcastAddressFinder {

    public static InetAddress getWiFiBroadcastAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            //Enumeration의 hasMoreElements() 메소드 사용하기위해 열거타입 사용
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                
                // "Wi-Fi" 어댑터 이름을 가진 활성화된 인터페이스만 찾음
                if (networkInterface.isUp() && !networkInterface.isLoopback() 
                		&& networkInterface.getDisplayName().contains("Wi-Fi")) {
                	System.out.println(networkInterface.getDisplayName()); 
                	
                    for (InterfaceAddress address : networkInterface.getInterfaceAddresses()) {
                    	//IP주소로부터 브로드캐스트 주소를 받아옴
                    	InetAddress broadcast = address.getBroadcast();
                        if (broadcast != null) {
                            return broadcast; // 브로드캐스트 주소 반환
                        }
                    }
                  }
                
            }
        } catch (SocketException e) {
            System.out.println("Error retrieving network interfaces: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null; // "Wi-Fi" 어댑터의 브로드캐스트 주소를 찾지 못한 경우
    }
}
