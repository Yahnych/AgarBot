package net.gegy1000.agarbot.network;

import net.gegy1000.agarbot.Game;
import net.gegy1000.agarbot.api.GameMode;
import net.gegy1000.agarbot.api.ServerData;
import net.gegy1000.agarbot.api.ServerLocation;
import net.gegy1000.agarbot.network.packet.PacketAgarBase;
import net.gegy1000.agarbot.network.packet.PacketServer0SetNick;
import net.gegy1000.agarbot.network.packet.PacketServer254Init1;
import net.gegy1000.agarbot.network.packet.PacketServer255Init2;
import net.gegy1000.agarbot.network.packet.PacketServer80SendToken;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

public class NetworkManager extends WebSocketClient
{
    private static final Map<Integer, Class<? extends PacketAgarBase>> clientPackets = new HashMap<>();
    private static final Map<Integer, Class<? extends PacketAgarBase>> serverPackets = new HashMap<>();

    private static String serverIP;
    private static String token;

    public boolean open = false;

    private NetworkManager(URI uri, Map<String, String> headers)
    {
        super(uri, new Draft_17(), headers, 0);
        this.connect();
    }

    public static NetworkManager create(ServerLocation serverLocation, GameMode gameMode) throws Exception
    {
        URI uri = getURIAndInit(serverLocation, gameMode);

        Map<String, String> headers = new HashMap<>();
        headers.put("Origin", "http://agar.io");

        return new NetworkManager(uri, headers);
    }

    public void sendPacketToServer(PacketAgarBase packet)
    {
        if (open)
        {
            try
            {
                AgarByteBuffer buffer = new AgarByteBuffer();
                packet.send(buffer);

                buffer.resetIndex();
                buffer.incrementIndex(-1);
                buffer.writeByte((byte) (packet.getDiscriminator()));

                byte[] data = buffer.toBytes();
                send(data);
//            System.out.println("Sent packet " + packet.getClass().getSimpleName() + " (id " + packet.getDiscriminator() + ") with data " + Arrays.toString(data));
            }
            catch (Exception e)
            {
                System.err.println("Error while sending packet with id " + packet.getDiscriminator() + "!");
                e.printStackTrace();
            }
        }
    }

    public static Class<? extends PacketAgarBase> getServerPacketForId(int id)
    {
        return serverPackets.get(id);
    }

    public static Class<? extends PacketAgarBase> getClientPacketForId(int id)
    {
        return clientPackets.get(id);
    }

    public static void registerServerPacket(int id, Class<? extends PacketAgarBase> packet)
    {
        serverPackets.put(id, packet);
    }

    public static void registerClientPacket(int id, Class<? extends PacketAgarBase> packet)
    {
        clientPackets.put(id, packet);
    }

    public static URI getURIAndInit(ServerLocation serverLocation, GameMode gameMode) throws Exception
    {
        ServerData serverData = getServerData(serverLocation, gameMode);
        serverIP = "ws://" + serverData.getIp();
        token = serverData.getToken();

        return new URI(serverIP);
    }

    public static ServerData getServerData(ServerLocation serverLocation, GameMode gameMode) throws Exception
    {
        URL url = new URL("http://m.agar.io");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");

        String gameName = serverLocation.getFriendlyName();

        String gameModeFriendlyName = gameMode.getFriendlyName();

        if (gameModeFriendlyName.length() > 0)
        {
            gameName += ":" + gameModeFriendlyName;
        }

        String urlParameters = gameName + "\n" + Game.VERSION;

        connection.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(urlParameters);
        out.flush();
        out.close();

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        return new ServerData(in.readLine(), in.readLine());
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake)
    {
        System.out.println("Connected to " + uri + " with token " + token + " and nickname " + Game.NICK);

        open = true;

        sendPacketToServer(new PacketServer254Init1());
        sendPacketToServer(new PacketServer255Init2());
        sendPacketToServer(new PacketServer80SendToken(token));
        sendPacketToServer(new PacketServer0SetNick(Game.NICK));
    }

    @Override
    public void onMessage(String data)
    {
    }

    @Override
    public void onMessage(ByteBuffer byteBuffer)
    {
        byte[] message = byteBuffer.order(ByteOrder.LITTLE_ENDIAN).array();

        AgarByteBuffer buffer = new AgarByteBuffer(message);

        buffer.incrementIndex(-1);
        int id = buffer.readByte();
        buffer.resetIndex();

        try
        {
            PacketAgarBase packet = NetworkManager.getClientPacketForId(id).getDeclaredConstructor().newInstance();
            packet.receive(buffer);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote)
    {
        System.out.println("Connection closed with code " + code + " for reason \"" + reason + "\". Remote = " + remote);

        open = false;

        System.exit(-1);
    }

    @Override
    public void onError(Exception e)
    {
        e.printStackTrace();

        System.exit(-1);
    }
}
