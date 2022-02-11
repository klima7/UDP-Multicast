import java.io.IOException;
import java.net.*;

public class Sender {

	public static void sendBroadcast(String message, int port) throws IOException {
		sendDatagram(message, "255.255.255.255", port);
	}

	public static void sendDatagram(String message, String address, int port) throws IOException {
		InetAddress inetAddress = InetAddress.getByName(address);

		DatagramSocket socket = new DatagramSocket();
		socket.setBroadcast(true);

		byte[] buffer = message.getBytes();

		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, inetAddress, port);
		socket.send(packet);
		socket.close();
	}

}
