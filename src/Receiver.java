import java.io.IOException;
import java.net.*;
import java.util.Enumeration;

public class Receiver extends Thread {

	private MulticastSocket socket;
	private InetSocketAddress groupAddress;
	private int port;
	private boolean running;
	private Writable output;

	public Receiver(Writable output, int port, String group) throws IOException {
		if(socket != null)
			throw new IllegalStateException("Already listening");

		this.output = output;
		this.running = true;
		this.port = port;

		try {
			groupAddress = new InetSocketAddress(group, port);
		} catch(IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid group address");
		}

		Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
		while (nets.hasMoreElements()) {
			NetworkInterface networkInterface = nets.nextElement();
			System.out.println(networkInterface.getName() + " - " + networkInterface.getDisplayName());
			for(InterfaceAddress address : networkInterface.getInterfaceAddresses()) {
				System.out.println(address.getAddress());
			}
		}

		socket = new MulticastSocket(port);
		socket.setReuseAddress(true);
		socket.joinGroup(groupAddress, NetworkInterface.getByName("wlan0"));
	}

	public void stopListening() {
		output.write("Listening stopped");
		running = false;
	}

	@Override
	public void run() {
		byte[] receiveData = new byte[100];

		output.write("Listening on port " + port + " in group " + groupAddress.getAddress().toString().substring(1));

		while(running) {
			try {
				DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);
				socket.receive(packet);
				String sentence = new String(packet.getData(), packet.getOffset(), packet.getLength());
				output.write(String.format("Received %d bytes from %s: %s", packet.getLength(), packet.getAddress(), sentence));
			} catch(IOException e) {
				e.printStackTrace();
			}
		}

		try {
			socket.leaveGroup(groupAddress, NetworkInterface.getByName("wlan0"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		socket.close();
	}
}
