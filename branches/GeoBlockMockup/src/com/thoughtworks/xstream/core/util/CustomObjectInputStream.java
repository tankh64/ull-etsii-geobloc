package com.thoughtworks.xstream.core.util;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.NotActiveException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.ObjectStreamClass;
import java.io.StreamCorruptedException;
import java.util.Map;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.DataHolder;

public class CustomObjectInputStream extends ObjectInputStream {

    private FastStack callbacks = new FastStack(1);

    private static final String DATA_HOLDER_KEY = CustomObjectInputStream.class.getName();

    public static interface StreamCallback {
        Object readFromStream() throws IOException;
        Map readFieldsFromStream() throws IOException;
        void defaultReadObject() throws IOException;
        void registerValidation(ObjectInputValidation validation, int priority) throws NotActiveException, InvalidObjectException;
        void close() throws IOException;
    }

    public static synchronized CustomObjectInputStream getInstance(DataHolder whereFrom, CustomObjectInputStream.StreamCallback callback) {
        try {
            CustomObjectInputStream result = (CustomObjectInputStream) whereFrom.get(DATA_HOLDER_KEY);
            if (result == null) {
                result = new CustomObjectInputStream(callback);
                whereFrom.put(DATA_HOLDER_KEY, result);
            } else {
                result.pushCallback(callback);
            }
            return result;
        } catch (IOException e) {
            throw new ConversionException("Cannot create CustomObjectStream", e);
        }
    }

    /**
     * Warning, this object is expensive to create (due to functionality inherited from superclass).
     * Use the static fetch() method instead, wherever possible.
     *
     * @see #getInstance(com.thoughtworks.xstream.converters.DataHolder, com.thoughtworks.xstream.core.util.CustomObjectInputStream.StreamCallback)
     */
    public CustomObjectInputStream(StreamCallback callback) throws IOException, SecurityException {
        super();
        this.callbacks.push(callback);
    }

    /**
     * Allows the CustomObjectInputStream (which is expensive to create) to be reused.
     */
    public void pushCallback(StreamCallback callback) {
        this.callbacks.push(callback);
    }
    
    public StreamCallback popCallback(){
        return (StreamCallback) this.callbacks.pop();
    }
    
    public StreamCallback peekCallback(){
        return (StreamCallback) this.callbacks.peek();
    }
    
    @Override
	public void defaultReadObject() throws IOException {
        peekCallback().defaultReadObject();
    }

    @Override
	protected Object readObjectOverride() throws IOException {
        return peekCallback().readFromStream();
    }

    @Override
	public Object readUnshared() throws IOException, ClassNotFoundException {
        return readObject();
    }

    @Override
	public boolean readBoolean() throws IOException {
        return ((Boolean)peekCallback().readFromStream()).booleanValue();
    }

    @Override
	public byte readByte() throws IOException {
        return ((Byte)peekCallback().readFromStream()).byteValue();
    }

    @Override
	public int readUnsignedByte() throws IOException {
        int b = ((Byte)peekCallback().readFromStream()).byteValue();
        if (b < 0) {
            b += Byte.MAX_VALUE;
        }
        return b;
    }

    @Override
	public int readInt() throws IOException {
        return ((Integer)peekCallback().readFromStream()).intValue();
    }

    @Override
	public char readChar() throws IOException {
        return ((Character)peekCallback().readFromStream()).charValue();
    }

    @Override
	public float readFloat() throws IOException {
        return ((Float)peekCallback().readFromStream()).floatValue();
    }

    @Override
	public double readDouble() throws IOException {
        return ((Double)peekCallback().readFromStream()).doubleValue();
    }

    @Override
	public long readLong() throws IOException {
        return ((Long)peekCallback().readFromStream()).longValue();
    }

    @Override
	public short readShort() throws IOException {
        return ((Short)peekCallback().readFromStream()).shortValue();
    }

    @Override
	public int readUnsignedShort() throws IOException {
        int b = ((Short)peekCallback().readFromStream()).shortValue();
        if (b < 0) {
            b += Short.MAX_VALUE;
        }
        return b;
    }

    @Override
	public String readUTF() throws IOException {
        return (String)peekCallback().readFromStream();
    }

    @Override
	public void readFully(byte[] buf) throws IOException {
        readFully(buf, 0, buf.length);
    }

    @Override
	public void readFully(byte[] buf, int off, int len) throws IOException {
        byte[] b = (byte[])peekCallback().readFromStream();
        System.arraycopy(b, 0, buf, off, len);
    }

    @Override
	public int read() throws IOException {
        return readUnsignedByte();
    }

    @Override
	public int read(byte[] buf, int off, int len) throws IOException {
        byte[] b = (byte[])peekCallback().readFromStream();
        if (b.length != len) {
            throw new StreamCorruptedException("Expected " + len + " bytes from stream, got " + b.length);
        }
        System.arraycopy(b, 0, buf, off, len);
        return len;
    }

    @Override
	public int read(byte b[]) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
	public GetField readFields() throws IOException {
        return new CustomGetField(peekCallback().readFieldsFromStream());
    }

    private class CustomGetField extends GetField {

        private Map fields;

        public CustomGetField(Map fields) {
            this.fields = fields;
        }

        @Override
		public ObjectStreamClass getObjectStreamClass() {
            throw new UnsupportedOperationException();
        }

        private Object get(String name) {
            return fields.get(name);
        }

        @Override
		public boolean defaulted(String name) {
            return !fields.containsKey(name);
        }

        @Override
		public byte get(String name, byte val) {
            return defaulted(name) ? val : ((Byte)get(name)).byteValue();
        }

        @Override
		public char get(String name, char val) {
            return defaulted(name) ? val : ((Character)get(name)).charValue();
        }

        @Override
		public double get(String name, double val) {
            return defaulted(name) ? val : ((Double)get(name)).doubleValue();
        }

        @Override
		public float get(String name, float val) {
            return defaulted(name) ? val : ((Float)get(name)).floatValue();
        }

        @Override
		public int get(String name, int val) {
            return defaulted(name) ? val : ((Integer)get(name)).intValue();
        }

        @Override
		public long get(String name, long val) {
            return defaulted(name) ? val : ((Long)get(name)).longValue();
        }

        @Override
		public short get(String name, short val) {
            return defaulted(name) ? val : ((Short)get(name)).shortValue();
        }

        @Override
		public boolean get(String name, boolean val) {
            return defaulted(name) ? val : ((Boolean)get(name)).booleanValue();
        }

        @Override
		public Object get(String name, Object val) {
            return defaulted(name) ? val : get(name);
        }

    }

    @Override
	public void registerValidation(ObjectInputValidation validation, int priority) throws NotActiveException, InvalidObjectException {
        peekCallback().registerValidation(validation, priority);
    }

    @Override
	public void close() throws IOException {
        peekCallback().close();
    }

    /****** Unsupported methods ******/

    @Override
	public int available() {
        throw new UnsupportedOperationException();
    }

    @Override
	public String readLine() {
        throw new UnsupportedOperationException();
    }

    @Override
	public int skipBytes(int len) {
        throw new UnsupportedOperationException();
    }

    @Override
	public long skip(long n) {
        throw new UnsupportedOperationException();
    }

    @Override
	public void mark(int readlimit) {
        throw new UnsupportedOperationException();
    }

    @Override
	public void reset() {
        throw new UnsupportedOperationException();
    }

    @Override
	public boolean markSupported() {
        return false;
    }

}
