package net.gegy1000.agarbot.neural.save;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class SaveByteBuffer
{
    private long index;
    private FileChannel channel;

    public SaveByteBuffer(FileChannel channel) throws IOException
    {
        this.channel = channel;
    }

    public void writeByte(byte b) throws IOException
    {
        channel.position(index);
        index++;
        ByteBuffer buf = ByteBuffer.wrap(new byte[] { b });
        channel.write(buf);
    }

    public void writeBytes(byte[] bytes) throws IOException
    {
        channel.position(index);

        ByteBuffer buf = ByteBuffer.wrap(bytes);
        channel.write(buf);

        index += bytes.length;
    }

    public void writeInteger(int i) throws IOException
    {
        writeBytes(ByteBuffer.allocate(4).putInt(i).array());
    }

    public void writeFloat(float f) throws IOException
    {
        writeBytes(ByteBuffer.allocate(4).putFloat(f).array());
    }

    public void writeLong(long l) throws IOException
    {
        writeBytes(ByteBuffer.allocate(8).putLong(l).array());
    }

    public byte readByte() throws IOException
    {
        channel.position(index);
        index++;

        ByteBuffer buf = ByteBuffer.allocate(1);
        channel.read(buf);

        return buf.get(0);
    }

    public byte[] readBytes(int count) throws IOException
    {
        channel.position(index);

        ByteBuffer buf = ByteBuffer.allocate(count);

        channel.read(buf);

        index += count;

        return buf.array();
    }

    public int readInteger() throws IOException
    {
        return ByteBuffer.wrap(readBytes(4)).getInt();
    }

    public float readFloat() throws IOException
    {
        return ByteBuffer.wrap(readBytes(4)).getFloat();
    }

    public long readLong() throws IOException
    {
        return ByteBuffer.wrap(readBytes(8)).getLong();
    }

    public void resetIndex()
    {
        index = 0;
    }
}
