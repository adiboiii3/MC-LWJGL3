package net.gudenau.lwjgl3.compatibility;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;

import java.lang.reflect.Field;
import java.nio.*;
import java.nio.charset.*;


/**
 * Created by gudenau on 5/30/2017.
 * <p>
 * LWJGL3
 */
public class MemoryUtil{
    private static final Charset ascii;
    private static final Charset utf8;
    private static final Charset utf16;

    static {
        ascii = Charset.forName("ISO-8859-1");
        utf8 = Charset.forName("UTF-8");
        utf16 = Charset.forName("UTF-16LE");
    }

    private static final Accessor memUtil;

    static {
        Accessor util;
        try {
            // Depends on java.nio.Buffer#address and sun.misc.Unsafe
            util = loadAccessor("org.lwjgl.MemoryUtilSun$AccessorUnsafe");
        } catch (Exception e0) {
            try {
                // Depends on java.nio.Buffer#address and sun.reflect.FieldAccessor
                util = loadAccessor("org.lwjgl.MemoryUtilSun$AccessorReflectFast");
            } catch (Exception e1) {
                try {
                    // Depends on java.nio.Buffer#address
                    util = new AccessorReflect();
                } catch (Exception e2) {
                    LWJGLUtil.log("Unsupported JVM detected, this will likely result in low performance. Please inform LWJGL developers.");
                    util = new AccessorJNI();
                }
            }
        }

        LWJGLUtil.log("MemoryUtil Accessor: " + util.getClass().getSimpleName());
        memUtil = util;

		/*
		BENCHMARK RESULTS - Oracle Server VM:
		Unsafe: 4ns
		ReflectFast: 8ns
		Reflect: 10ns
		JNI: 82ns
		BENCHMARK RESULTS - Oracle Client VM:
		Unsafe: 5ns
		ReflectFast: 81ns
		Reflect: 85ns
		JNI: 87ns
		On non-Oracle VMs, Unsafe should be the fastest implementation as well. In the absence
		of Unsafe, performance will depend on how reflection and JNI are implemented. For now
		we'll go with what we see on the Oracle VM (that is, we'll prefer reflection over JNI).
		 */
    }

    private MemoryUtil() {
    }

    /**
     * Returns the memory address of the specified buffer. [INTERNAL USE ONLY]
     *
     * @param buffer the buffer
     *
     * @return the memory address
     */
    public static long getAddress0(Buffer buffer) { return memUtil.getAddress(buffer); }

    public static long getAddress0Safe(Buffer buffer) { return buffer == null ? 0L : memUtil.getAddress(buffer); }

    public static long getAddress0(PointerBuffer buffer) { return buffer.address(); }

    public static long getAddress0Safe(PointerBuffer buffer) { return buffer == null ? 0L : buffer.address(); }

    // --- [ API utilities ] ---

    public static long getAddress(ByteBuffer buffer) { return getAddress(buffer, buffer.position()); }

    public static long getAddress(ByteBuffer buffer, int position) { return getAddress0(buffer) + position; }

    public static long getAddress(ShortBuffer buffer) { return getAddress(buffer, buffer.position()); }

    public static long getAddress(ShortBuffer buffer, int position) { return getAddress0(buffer) + (position << 1); }

    public static long getAddress(CharBuffer buffer) { return getAddress(buffer, buffer.position()); }

    public static long getAddress(CharBuffer buffer, int position) { return getAddress0(buffer) + (position << 1); }

    public static long getAddress(IntBuffer buffer) { return getAddress(buffer, buffer.position()); }

    public static long getAddress(IntBuffer buffer, int position) { return getAddress0(buffer) + (position << 2); }

    public static long getAddress(FloatBuffer buffer) { return getAddress(buffer, buffer.position()); }

    public static long getAddress(FloatBuffer buffer, int position) { return getAddress0(buffer) + (position << 2); }

    public static long getAddress(LongBuffer buffer) { return getAddress(buffer, buffer.position()); }

    public static long getAddress(LongBuffer buffer, int position) { return getAddress0(buffer) + (position << 3); }

    public static long getAddress(DoubleBuffer buffer) { return getAddress(buffer, buffer.position()); }

    public static long getAddress(DoubleBuffer buffer, int position) { return getAddress0(buffer) + (position << 3); }

    public static long getAddress(PointerBuffer buffer) { return getAddress(buffer, buffer.position()); }

    public static long getAddress(PointerBuffer buffer, int position) { return buffer.address(position); }

    // --- [ API utilities - Safe ] ---

    public static long getAddressSafe(ByteBuffer buffer) { return buffer == null ? 0L : getAddress(buffer); }

    public static long getAddressSafe(ByteBuffer buffer, int position) { return buffer == null ? 0L : getAddress(buffer, position); }

    public static long getAddressSafe(ShortBuffer buffer) { return buffer == null ? 0L : getAddress(buffer); }

    public static long getAddressSafe(ShortBuffer buffer, int position) { return buffer == null ? 0L : getAddress(buffer, position); }

    public static long getAddressSafe(CharBuffer buffer) { return buffer == null ? 0L : getAddress(buffer); }

    public static long getAddressSafe(CharBuffer buffer, int position) { return buffer == null ? 0L : getAddress(buffer, position); }

    public static long getAddressSafe(IntBuffer buffer) { return buffer == null ? 0L : getAddress(buffer); }

    public static long getAddressSafe(IntBuffer buffer, int position) { return buffer == null ? 0L : getAddress(buffer, position); }

    public static long getAddressSafe(FloatBuffer buffer) { return buffer == null ? 0L : getAddress(buffer); }

    public static long getAddressSafe(FloatBuffer buffer, int position) { return buffer == null ? 0L : getAddress(buffer, position); }

    public static long getAddressSafe(LongBuffer buffer) { return buffer == null ? 0L : getAddress(buffer); }

    public static long getAddressSafe(LongBuffer buffer, int position) { return buffer == null ? 0L : getAddress(buffer, position); }

    public static long getAddressSafe(DoubleBuffer buffer) { return buffer == null ? 0L : getAddress(buffer); }

    public static long getAddressSafe(DoubleBuffer buffer, int position) { return buffer == null ? 0L : getAddress(buffer, position); }

    public static long getAddressSafe(PointerBuffer buffer) { return buffer == null ? 0L : getAddress(buffer); }

    public static long getAddressSafe(PointerBuffer buffer, int position) { return buffer == null ? 0L : getAddress(buffer, position); }

    // --- [ String utilities ] ---

    /**
     * Returns a ByteBuffer containing the specified text ASCII encoded and null-terminated.
     * If text is null, null is returned.
     *
     * @param text the text to encode
     *
     * @return the encoded text or null
     *
     * @see String#getBytes()
     */
    public static ByteBuffer encodeASCII(final CharSequence text) {
        return encode(text, ascii);
    }

    /**
     * Returns a ByteBuffer containing the specified text UTF-8 encoded and null-terminated.
     * If text is null, null is returned.
     *
     * @param text the text to encode
     *
     * @return the encoded text or null
     *
     * @see String#getBytes()
     */
    public static ByteBuffer encodeUTF8(final CharSequence text) {
        return encode(text, utf8);
    }

    /**
     * Returns a ByteBuffer containing the specified text UTF-16LE encoded and null-terminated.
     * If text is null, null is returned.
     *
     * @param text the text to encode
     *
     * @return the encoded text
     */
    public static ByteBuffer encodeUTF16(final CharSequence text) {
        return encode(text, utf16);
    }

    /**
     * Wraps the specified text in a null-terminated CharBuffer and encodes it using the specified Charset.
     *
     * @param text    the text to encode
     * @param charset the charset to use for encoding
     *
     * @return the encoded text
     */
    private static ByteBuffer encode(final CharSequence text, final Charset charset) {
        if ( text == null )
            return null;

        return encode(CharBuffer.wrap(new CharSequenceNT(text)), charset);
    }

    /**
     * A {@link CharsetEncoder#encode(java.nio.CharBuffer)} implementation that uses {@link BufferUtils#createByteBuffer(int)}
     * instead of {@link ByteBuffer#allocate(int)}.
     *
     * @see CharsetEncoder#encode(java.nio.CharBuffer)
     */
    private static ByteBuffer encode(final CharBuffer in, final Charset charset) {
        final CharsetEncoder encoder = charset.newEncoder(); // encoders are not thread-safe, create a new one on every call

        int n = (int)(in.remaining() * encoder.averageBytesPerChar());
        ByteBuffer out = BufferUtils.createByteBuffer(n);

        if ( n == 0 && in.remaining() == 0 )
            return out;

        encoder.reset();
        while ( true ) {
            CoderResult cr = in.hasRemaining() ? encoder.encode(in, out, true) : CoderResult.UNDERFLOW;
            if ( cr.isUnderflow() )
                cr = encoder.flush(out);

            if ( cr.isUnderflow() )
                break;

            if ( cr.isOverflow() ) {
                n = 2 * n + 1;    // Ensure progress; n might be 0!
                ByteBuffer o = BufferUtils.createByteBuffer(n);
                out.flip();
                o.put(out);
                out = o;
                continue;
            }

            try {
                cr.throwException();
            } catch (CharacterCodingException e) {
                throw new RuntimeException(e);
            }
        }
        out.flip();
        return out;
    }

    public static String decodeASCII(final ByteBuffer buffer) {
        return decode(buffer, ascii);
    }

    public static String decodeUTF8(final ByteBuffer buffer) {
        return decode(buffer, utf8);
    }

    public static String decodeUTF16(final ByteBuffer buffer) {
        return decode(buffer, utf16);
    }

    private static String decode(final ByteBuffer buffer, final Charset charset) {
        if ( buffer == null )
            return null;

        return decodeImpl(buffer, charset);
    }

    private static String decodeImpl(final ByteBuffer in, final Charset charset) {
        final CharsetDecoder decoder = charset.newDecoder(); // decoders are not thread-safe, create a new one on every call

        int n = (int)(in.remaining() * decoder.averageCharsPerByte());
        CharBuffer out = BufferUtils.createCharBuffer(n);

        if ( (n == 0) && (in.remaining() == 0) )
            return "";

        decoder.reset();
        for (; ; ) {
            CoderResult cr = in.hasRemaining() ? decoder.decode(in, out, true) : CoderResult.UNDERFLOW;
            if ( cr.isUnderflow() )
                cr = decoder.flush(out);

            if ( cr.isUnderflow() )
                break;
            if ( cr.isOverflow() ) {
                n = 2 * n + 1;    // Ensure progress; n might be 0!
                CharBuffer o = BufferUtils.createCharBuffer(n);
                out.flip();
                o.put(out);
                out = o;
                continue;
            }
            try {
                cr.throwException();
            } catch (CharacterCodingException e) {
                throw new RuntimeException(e);
            }
        }
        out.flip();
        return out.toString();
    }

    /** A null-terminated CharSequence. */
    private static class CharSequenceNT implements CharSequence {

        final CharSequence source;

        CharSequenceNT(CharSequence source) {
            this.source = source;
        }

        public int length() {
            return source.length() + 1;

        }

        public char charAt(final int index) {
            return index == source.length() ? '\0' : source.charAt(index);

        }

        public CharSequence subSequence(final int start, final int end) {
            return new CharSequenceNT(source.subSequence(start, Math.min(end, source.length())));
        }

    }

    interface Accessor {
        long getAddress(Buffer buffer);
    }

    private static Accessor loadAccessor(final String className) throws Exception {
        return (Accessor)Class.forName(className).newInstance();
    }

    /** Default implementation. */
    private static class AccessorJNI implements Accessor {
        public long getAddress(final Buffer buffer) {
            //FIXME
            //return BufferUtils.getBufferAddress(buffer);
            return 0;
        }
    }

    /** Implementation using reflection on ByteBuffer. */
    private static class AccessorReflect implements Accessor {

        private final Field address;

        AccessorReflect() {
            try {
                address = getAddressField();
            } catch (NoSuchFieldException e) {
                throw new UnsupportedOperationException(e);
            }
            address.setAccessible(true);
        }

        public long getAddress(final Buffer buffer) {
            try {
                return address.getLong(buffer);
            } catch (IllegalAccessException e) {
                // cannot happen
                return 0L;
            }
        }

    }

    static Field getAddressField() throws NoSuchFieldException {
        return getDeclaredFieldRecursive(ByteBuffer.class, "address");
    }

    private static Field getDeclaredFieldRecursive(final Class<?> root, final String fieldName) throws NoSuchFieldException {
        Class<?> type = root;

        do {
            try {
                return type.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                type = type.getSuperclass();
            }
        } while ( type != null );

        throw new NoSuchFieldException(fieldName + " does not exist in " + root.getSimpleName() + " or any of its superclasses.");
    }
}
