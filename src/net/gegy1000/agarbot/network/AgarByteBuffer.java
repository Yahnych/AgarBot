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

    public byte readByte()
    {
        byte b = bytes[index];
        incrementIndex(1);
        return b;
    }

    public byte[] readBytes(int count)
    {
        byte[] bytes = new byte[count];

        for (int i = 0; i < count; i++)
        {
            bytes[i] = readByte();
        }

        return bytes;
    }

    public void writeNullStr16(String str)
    {
        writeEndStr16(str);
        writeShort((short) 0);
    }

    public void writeEndStr16(String str)
    {
        for (char c : str.toCharArray())
        {
            writeShort((short) c);
        }
    }

    public void writeNullStr8(String str)
    {
        writeEndStr8(str);
        writeByte((byte) 0);
    }

    public void writeEndStr8(String str)
    {
        for (char c : str.toCharArray())
        {
            writeByte((byte) c);
        }
    }

    public int readInteger()
    {
        return ByteBuffer.wrap(readBytes(4)).order(ByteOrder.LITTLE_ENDIAN).getInt();
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

    public String readNullStr16()
    {
        String str = "";

        short c;

        while ((c = readShort()) != 0)
        {
            str += (char) c;
        }

        return str;
    }

    public String readEndStr16()
    {
        String str = "";

        while (hasNext(2))
        {
            str += (char) readShort();
        }

        return str;
    }

    public String readNullStr8()
    {
        String str = "";

        byte c;

        while ((c = readByte()) != 0)
        {
            str += (char) c;
        }

        return str;
    }

    public String readEndStr8()
    {
        String str = "";

        while (hasNext(1))
        {
            str += (char) readByte();
        }

        return str;
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
        return hasNext(0);
    }

    public boolean hasNext(int count)
    {
        return index + count < bytes.length;
    }

    public int amountLeft()
    {
        return bytes.length - index;
    }
}