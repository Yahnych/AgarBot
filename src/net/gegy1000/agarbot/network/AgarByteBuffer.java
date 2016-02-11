package net.gegy1000.agarbot.network;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class AgarByteBuffer
{
    private byte[] bytes;
    private int index;
    private int highestIndex;

    public AgarByteBuffer()
    {
        this.bytes = new byte[16384];
        resetIndex();
    }

    public AgarByteBuffer(byte[] bytes)
    {
        this.bytes = bytes;
        resetIndex();
    }

    public void writeByte(byte b)
    {
        bytes[index] = b;
        incrementIndex(1);
    }

    public void writeBytes(byte[] b)
    {
        for (byte by : b)
        {
            writeByte(by);
        }
    }

    public void writeInteger(int i)
    {
        writeBytes(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(i).array());
    }

    public void writeUnsignedInteger(long i)
    {
        writeInteger((int) (i + Integer.MIN_VALUE));
    }

    public void writeString(String s)
    {
        byte[] bytes = s.getBytes();

        writeInteger(bytes.length);
        writeBytes(bytes);
    }

    public void writeFloat(float f)
    {
        writeBytes(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(f).array());
    }

    public void writeDouble(double d)
    {
        writeBytes(ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putDouble(d).array());
    }

    public void writeShort(short s)
    {
        writeBytes(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(s).array());
    }

    public void writeUnsignedShort(int s)
    {
        writeShort((short) (s + Short.MIN_VALUE));
    }

    public byte readByte()
    {
        byte b = bytes[index];
        incrementIndex(1);
        return b;
    }

    public byte[] readBytes(int count)
    {
        byte[] bytes = new byte[count];

        for (int i = 0; i < count; i++) {
            bytes[i] = readByte();
        }

        return bytes;
    }

    public int readInteger()
    {
        return ByteBuffer.wrap(readBytes(4)).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    public long readUnsignedInteger()
    {
        return readInteger() - Integer.MIN_VALUE;
    }

    public String readString()
    {
        int length = readInteger();
        return new String(readBytes(length));
    }

    public float readFloat()
    {
        return ByteBuffer.wrap(readBytes(4)).order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }

    public double readDouble()
    {
        return ByteBuffer.wrap(readBytes(8)).order(ByteOrder.LITTLE_ENDIAN).getDouble();
    }

    public short readShort()
    {
        return ByteBuffer.wrap(readBytes(2)).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }

    public int readUnsignedShort()
    {
        return readShort() - Short.MIN_VALUE;
    }

    public void resetIndex()
    {
        index = 1; //First byte is id
    }

    public void incrementIndex(int amount)
    {
        index += amount;

        if (index > highestIndex)
        {
            highestIndex = index;
        }
    }

    public byte[] toBytes()
    {
        byte[] returnBytes = new byte[highestIndex];

        System.arraycopy(bytes, 0, returnBytes, 0, highestIndex);

        return returnBytes;
    }

    public boolean hasNext()
    {
        return index < bytes.length;
    }
}