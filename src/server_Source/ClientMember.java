package server_Source;
import java.net.Socket;
import java.util.ArrayList;

// 클라이언트의 정보를 담은 클래스
public class ClientMember {
    String ip;
    Socket mysocket;
    Boolean connected;
    Boolean newMsg;
    
    // 구성자
    public ClientMember(String ip,Socket mysocket, Boolean connected, Boolean newMsg) {
        this.ip = ip;
        this.mysocket = mysocket;
        this.connected = connected;
        this.newMsg = newMsg;        
    }

    // 편의성을 위해 get과 set 메소드를 생성해놓음
    public String getIp() {
        return ip;
    }
    
    public void setIp(String ip) {
        this.ip = ip;
    }

    public Socket getSocket() {
        return mysocket;
    }
   
    public Boolean getNewMsg() {
        return newMsg;
    }

    public void setNewMsg(Boolean newMsg) {
        this.newMsg = newMsg;
    }

    public Boolean getConnected() {
        return connected;
    }

    public void setConnected(Boolean connected) {
        this.connected = connected;
    }
}

