package com.willingdev;




public class ByteVector {
    /**
     * The content of this vector.
     */
    private byte[] data;

    private int size;
    private static boolean printText = false;
    private static boolean printHex = true;
    private static boolean printBinary = false;


    public ByteVector() {
        data = new byte[10];
        size = 0;
    }

    public ByteVector(int initialSize) {
        if (initialSize <= 0)
            initialSize = 10;
        data = new byte[initialSize];
        size = 0;
    }

    public ByteVector(byte[] data) {
        if (data == null) {
            data = new byte[10];
            size = 0;
            return;
        }
        this.data = new byte[data.length];
        System.arraycopy(data, 0, this.data, 0, data.length);
        size = data.length;
    }

    public ByteVector(String strData) {
        if (strData == null) {
            data = new byte[10];
            size = 0;
            return;
        }
        data = convertStringToByteArray(strData);
        size = data.length;
    }

    protected void finalize() throws Throwable {
        this.data = null;
        super.finalize();
    }

    public void add(String str) {
        synchronized (ByteVector.class) {

            add(convertStringToByteArray(str));
        }
    }

    /**
     * Appends a byte into this byte vector. The byte vector is automatically
     * enlarged if necessary.
     *
     * @param b
     *            a byte.
     */

    public void add(byte b) {
        synchronized (ByteVector.class) {
            add(new byte[] { b });
        }
    }

    /**
     * Appends a
     * automatically enlarged if necessary.
     *
     * @param array
     *            an array of byte.
     */
    public void add(byte[] array) {
        synchronized (ByteVector.class) {
            if (array == null)
                return;
            add(array, 0, array.length);
        }
    }

    /**
     * Appends a array of bytes into this byte vector. The byte vector is
     * automatically enlarged if necessary.
     *
     * @param array
     *            the array of bytes to be appended.
     * @param off
     *            the index of the first byte of <code>array</code> to append.
     * @param len
     *            the number of bytes to append.
     */
    public void add(byte[] array, int off, int len) {

        synchronized (ByteVector.class) {
            if (array == null)
                return;
            if (array.length - off < len)
                len = array.length - off;
            if (data.length < size + len) {
                int increaseSize = len + size;
                expandCapacity(increaseSize);
            }
            if (len == 1)
                data[size] = array[off];
            else if (len < 35) {
                // for loop is faster for small number of elements
                for (int i = 0; i < len; i++)
                    data[size + i] = array[off + i];
            } else
                System.arraycopy(array, off, data, size, len);
            size += len;
        }
    }

    /**
     * Inserts the byte into this vector at the first index.
     *
     * @param value
     *            a byte to be inserted
     */
    public void insert(byte value) {
        synchronized (ByteVector.class) {
            insert(new byte[] { value });
        }
    }

    /**
     * Inserts the array of byte into this vector. The position that array of
     * byte are inserted, starting at index 0.
     *
     * @param value
     *            array of bytes to be inserted
     */
    public void insert(byte[] value) {
        synchronized (ByteVector.class) {
            insert(value, 0);

        }
    }

    /**
     * Inserts the array of byte into this vector. The array of byte are
     * inserted at the position indicated by <code>pos</code>.
     *
     * @param value
     *            array of bytes to be inserted
     * @param pos
     *            the offset.
     */
    public void insert(byte[] value, int pos) {
        synchronized (ByteVector.class) {
            if (value == null)
                return;
            if (pos < 0 || pos > size) {
                throw new IndexOutOfBoundsException();
            }
            expandCapacity(size + value.length);
            byte[] temp = new byte[size - pos];
            System.arraycopy(data, pos, temp, 0, temp.length);
            System.arraycopy(temp, 0, data, pos + value.length, temp.length);
            System.arraycopy(value, 0, data, pos, value.length);
            size += value.length;
        }
    }

    /**
     * Removes the byte in this vector at <code>index</code> position.
     *
     * @param index
     *            the index of bytes to be removed.
     * @return the byte which is removed.
     */
    public byte[] remove(int index) {
        synchronized (ByteVector.class) {
            return remove(index, 1);
        }
    }

    /**
     * Removes the bytes in this vector. The bytes are removed at the specified
     * <code>index</code> and extends to number of bytes <code>len</code>
     *
     * @param index
     *            The beginning index.
     * @param len
     *            the number of bytes to removed.
     * @return the bytes which is removed.
     */
    public byte[] remove(int index, int len) {
        synchronized (ByteVector.class) {
            if (index < 0 || index > data.length)
                throw new IndexOutOfBoundsException("Index of Bounds.");

            if (len < 0 || len > size) {
                throw new IndexOutOfBoundsException(
                        "Length Nagative or over than size.");
            }

            if (index + len > size) {
                throw new IndexOutOfBoundsException("Length is over.");
            }

            if (size == 0)
                return new byte[1];

            byte[] firstPart = new byte[index];
            byte[] secondPart = new byte[size - (index + len)];
            byte[] buff = new byte[firstPart.length + secondPart.length];
            byte[] removedData = new byte[len];
            System.arraycopy(data, index, removedData, 0, len);
            System.arraycopy(data, 0, firstPart, 0, index);
            // String str = APByteVector.toHexString(firstPart);
            // System.out.println("Firt:" + str);
            System.arraycopy(data, index + len, secondPart, 0,
                    secondPart.length);
            // str = APByteVector.toHexString(secondPart);
            // System.out.println("Se:" + str);
            System.arraycopy(firstPart, 0, buff, 0, firstPart.length);
            // System.out.println("BuffFirst:" + str);

            System.arraycopy(secondPart, 0, buff, firstPart.length,
                    secondPart.length);
            data = buff;
            size = data.length;

            return removedData;
        }
    }

    /**
     * Returns the size of this vector
     *
     * @return the size of the vector.
     */
    public int size() {
        return size;
    }

    /**
     * Returns the byte at the specified <code>index</code>.
     *
     * @param index
     *            the index of the byte.
     * @return the byte at the specified <code>index</code> of this vector.
     */
    public byte get(int index) {
        return data[index];
    }

    /**
     * Replaces the byte in this vector with specified byte.
     *
     * @param index
     *            The index to be replaced.
     * @param newData
     *            the byte to replace.
     * @return the number of byte that is replaced.
     */
    public int replace(int index, byte newData) {
        synchronized (ByteVector.class) {
            return replace(index, new byte[] { newData });
        }
    }

    /**
     * Replaces the array of bytes in this vector with specified bytes. The
     * bytes begins at the specified <code>start</code> and extends to the bytes
     * at <code>index + newData.length</code>.
     *
     * @param index
     *            The beginning index.
     * @param newData
     *            the bytes that will replace previous contents.
     * @return the number of bytes that are replaced.
     */
    public int replace(int index, byte[] newData) {
        synchronized (ByteVector.class) {
            return replace(index, index + newData.length, newData);
        }
    }

    /**
     * Replaces the array of bytes in this vector with specified bytes. The
     * bytes begins at the specified <code>start</code> and extends to the bytes
     * at index <code>end - 1</code> or to the end of the bytes if no such byte
     * exists
     *
     * @param start
     *            The beginning index.
     * @param end
     *            The ending index
     * @param newData
     *            the bytes that will replace previous contents.
     * @return the number of bytes that are replaced.
     */
    public int replace(int start, int end, byte[] newData) {
        synchronized (ByteVector.class) {
            if (newData == null)
                throw new NullPointerException("newData is null.");
            if (start < 0)
                throw new StringIndexOutOfBoundsException(start);
            if (start > size)
                throw new StringIndexOutOfBoundsException("start > size()");
            if (start > end)
                throw new StringIndexOutOfBoundsException("start > end");

            int num = end - start;
            if (num > newData.length)
                num = newData.length;
            if (end > size)
                end = size;
            byte[] tmp = new byte[num];
            System.arraycopy(newData, 0, tmp, 0, num);
            if (start + num > size)
                num = size - start;
            remove(start, num);

            insert(tmp, start);
            return tmp.length;
        }

    }

    // private void doubleCapacity() {
    // byte[] tmp = new byte[data.length + 1];
    // System.arraycopy(data, 0, tmp, 0, data.length);
    // data = tmp;
    //
    // }

    private void expandCapacity(int minimumCapacity) {
        int newCapacity = (data.length + 1) * 2;
        if (newCapacity < 0) {
            newCapacity = Integer.MAX_VALUE;
        } else if (minimumCapacity > newCapacity) {
            newCapacity = minimumCapacity;
        }

        byte[] tmp = new byte[newCapacity];
        System.arraycopy(data, 0, tmp, 0, data.length);

        data = tmp;
    }

    /**
     * Returns a array of bytes representing the data in this vector.
     *
     * @return a array of bytes representation of this vector.
     */
    public byte[] toByteArray() {
        synchronized (ByteVector.class) {
            data = cropData(data, size);
            return data.clone();
        }
    }

    private byte[] cropData(byte[] data, int size) {
        if (size < data.length) {
            byte[] buff = new byte[size];
            System.arraycopy(data, 0, buff, 0, size);
            return buff;
        }
        return data;
    }

    /**
     * Returns a string representation of the array of bytes argument as a
     * hexadecimal.
     *
     * @param data
     *            an array of bytes to be converted to a string
     * @return a string representation of the array of bytes argument as a
     *         hexadecimal.
     */
    public static String toHexString(byte[] data) {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < data.length; i++) {
            byte b = data[i];
            sb.append(String.format("%1$02X", b) + " ");
        }
        return sb.toString().trim();
    }

    /**
     * Returns a string representation of the byte argument as a hexadecimal.
     *
     * @param b
     *            a byte to be converted to a string
     * @return a string representation of the byte argument as a hexadecimal.
     */
    public static String toHexString(byte b) {
        return String.format(String.format("%1$02X", b));
    }

    /**
     * Copies the array from the specified position <code> offset</code> through
     * <code>offet+len</code>.
     *
     * @param offset
     *            the beginning index.
     * @param len
     *            the number of byte to be copied.
     * @return the bytes are copied.
     */
    public byte[] copyArray(int offset, int len) {
        synchronized (ByteVector.class) {
            if (offset > data.length)
                throw new ArrayIndexOutOfBoundsException();
            if (offset + len > data.length)
                len = Math.min(offset + len, data.length - offset);
            byte buff[] = new byte[len];
            System.arraycopy(data, offset, buff, 0, len);
            return buff;
        }
    }

    public byte[] toByteArray(int offset, int len) {
        synchronized (ByteVector.class) {
            return copyArray(offset, len);
        }
    }

    /**
     * Copies the array from the specified position <code> offset</code> through
     * the end of this vector.
     *
     * @param offset
     *            the beginning index.
     * @return the bytes are copied.
     */
    public byte[] copyArray(int offset) {
        synchronized (ByteVector.class) {
            return copyArray(offset, size - offset);
        }
    }

    /**
     * Searches for the first occurence of the given argumen
     *
     * @param b
     *            a byte.
     * @return the index of the first occurrence of the argument in this vector;
     *         if it does not occur, -1 is returned.
     */
    public int indexOf(byte b) {
        return indexOf(data, b);
    }

    /**
     * Searches for the first occurence of the given argument, beginning the
     * search at <code>offset</code>,
     *
     * @param b
     *            a byte
     * @param offset
     *            the index to start searching from.
     * @return the index of the first occurrence of the argument in this vector;
     *         if it does not occur, -1 is returned.
     */
    public int indexOf(byte b, int offset) {
        synchronized (ByteVector.class) {
            return indexOf(data, new byte[] { b }, offset);
        }
    }

    /**
     * Searches for the first occurence of <code> b </code> in
     * <code>array</code>.
     *
     * @param array
     *            an array of bytes.
     * @param b
     *            a byte.
     * @return the index of the first occurrence ; if it does not occur, -1 is
     *         returned.
     */
    public static int indexOf(byte array[], byte b) {
        return indexOf(array, new byte[] { b }, 0);
    }

    /**
     * Searches for the first occurence of <code> ele </code> in
     * <code>data</code>.
     *
     * @param data
     *            an array of bytes.
     * @param ele
     *            an array of bytes.
     * @return the index of the first occurrence ; if it does not occur, -1 is
     *         returned.
     */
    public static int indexOf(byte data[], byte ele[]) {
        return indexOf(data, ele, 0);
    }

    /**
     * Searches for the first occurence of <code> ele </code> in
     * <code>data</code>, beginning the search at <code>offset</code>.
     *
     * @param data
     *            an array of bytes.
     * @param ele
     *            an array of bytes.
     * @param offset
     *            the begining index of search.
     * @return the index of the first occurrence ; if it does not occur, -1 is
     *         returned.
     */
    public static int indexOf(byte data[], byte ele[], int offset) {

        if (ele == null || ele.length == 0)
            return -1;
        if (offset + ele.length > data.length)
            return -1;
        for (int iIndex = offset; iIndex <= data.length - ele.length; iIndex++) {
            int iStrIndex;
            for (iStrIndex = 0; iStrIndex < ele.length
                    && data[iIndex + iStrIndex] == ele[iStrIndex]; iStrIndex++)
                ;
            if (iStrIndex == ele.length)
                return iIndex;
        }

        return -1;

    }

    /**
     * Reverses data from given parameter.
     *
     * @return the array of bytes are reversed.
     */
    public static byte[] reverse(byte[] data) {
        if (data == null)
            return null;
        byte[] buff = new byte[data.length];
        for (int i = 0; i < buff.length; i++)
            buff[i] = data[i];
        int n = buff.length - 1;
        byte temp;
        for (int j = (n - 1) >> 1; j >= 0; --j) {
            temp = buff[j];
            buff[j] = buff[n - j];
            buff[n - j] = temp;
        }
        return buff;

    }

    /**
     * Reverses data in this vector.
     *
     * @return the new object of reversed data.
     */
    public ByteVector reverse() {
        byte[] buff = this.toByteArray();
        int n = buff.length - 1;
        byte temp;
        for (int j = (n - 1) >> 1; j >= 0; --j) {
            temp = buff[j];
            buff[j] = buff[n - j];
            buff[n - j] = temp;
        }
        return new ByteVector(buff);

    }

    public static String toBinaryString(byte[] data) {
        synchronized (ByteVector.class) {
            int grpLen = 8;
            StringBuilder sb = new StringBuilder();
            String ss;
            for (int i = 0; i < data.length; i++) {

                ss = Integer.toBinaryString((int) data[i]);
                if (ss.length() > 8) {
                    ss = ss.substring(ss.length() - 8, ss.length());
                }
                if (ss.length() < 8) {
                    int len = 8 - ss.length();
                    for (int j = 0; j < len; j++) {
                        sb.append("0");
                    }
                }
                sb.append(ss);

            }

            String str = sb.toString();

            sb = new StringBuilder();

            int strLen = str.length();
            int stringIndex = 0;
            int len = strLen;
            while (len > 0) {
                String s = str.substring(len - grpLen, strLen - stringIndex);
                sb.insert(0, s + " ");
                stringIndex += grpLen;
                len -= grpLen;

            }

            return sb.toString();
        }
    }

    /**
     * Returns a string representation of this vector as a hexadecimal.
     *
     * @return a string representation of this vector as a hexadecimal.
     */
    public String toHexString() {
        data = cropData(data, size);
        return toHexString(data);
    }

    /**
     * Parses the hexadecimal string argument (i.e. FF) as an byte
     *
     * @param s
     *            a hexadecimal string
     * @return the byte representing the hexadecimal string.
     */
    public static byte parseHex(String s) {
        if (s.length() > 2 || s.length() < 2)
            throw new IllegalArgumentException();
        byte firstNibble = Byte.parseByte(s.substring(0, 1), 16);
        byte secondNibble = Byte.parseByte(s.substring(1, 2), 16);
        int finalByte = (secondNibble) | (firstNibble << 4); // bit-operations
        return (byte) finalByte;
    }

    /**
     * Parses the hexadecimal string argument (i.e. FF FF) as an array of bytes.
     *
     * @param s
     *            a hexadecimal string
     * @return the array of bytes representing the hexadecimal string.
     */
    public static byte[] parseHexSringToByteArray(String s) {

        /** ******************** */
        final int HEX_WIDTH = 3;
        StringBuffer sb = new StringBuffer();
        int stringIndex = 0, spaceIndex = 0;
        String ss;
        while (stringIndex < s.length()) {
            ss = s.substring(spaceIndex, spaceIndex + 2);
            spaceIndex = s.indexOf(' ', stringIndex) + 1;
            sb.append(ss);
            stringIndex += HEX_WIDTH;
        }
        /** ********************* */

        String hexStr = sb.toString();

        byte bArray[] = new byte[hexStr.length() / 2];
        for (int i = 0; i < (hexStr.length() / 2); i++) {
            byte firstNibble = Byte.parseByte(
                    hexStr.substring(2 * i, 2 * i + 1), 16); // [x,y)
            byte secondNibble = Byte.parseByte(
                    hexStr.substring(2 * i + 1, 2 * i + 2), 16);
            int finalByte = (secondNibble) | (firstNibble << 4); // bit-operations
            // only with
            // numbers,
            // not
            // bytes.
            bArray[i] = (byte) finalByte;
        }

        return bArray;
    }

    private byte[] convertStringToByteArray(String str) {
        if (str == null)
            return null;
        int length = str.length();
        byte[] buff = new byte[length];
        for (int i = 0; i < length; i++)
            buff[i] = (byte) str.charAt(i);
        return buff;
        // Charset charset = Charset.forName("US-ASCII");
        // CharsetDecoder decoder = charset.newDecoder();
        // CharsetEncoder encoder = charset.newEncoder();
        // encoder.onUnmappableCharacter(CodingErrorAction.IGNORE);
        // try
        // {
        // // Convert a string to bytes in a ByteBuffer
        // ByteBuffer bbuf = encoder.encode(CharBuffer.wrap(str));
        //
        // // Convert bytes in a ByteBuffer to a character ByteBuffer and then
        // // to a string.
        // CharBuffer cbuf = decoder.decode(bbuf);
        // return cbuf.toString().getBytes();
        //
        // }
        // catch (CharacterCodingException e)
        // {
        // e.printStackTrace();
        // }
        // return null;

    }

    /**
     * Set to enable printing Text String in method {@link #toString()} <br>
     * <code>Default is false</code>
     *
     * @param printText
     */
    public static void setPrintText(boolean printText) {
        ByteVector.printText = printText;
    }

    /**
     * Set to enable printing Hex String in method {@link #toString()}<br>
     * <code>Default is true</code>
     *
     */
    public static void setPrintHex(boolean printHex) {
        ByteVector.printHex = printHex;
    }

    /**
     * Set to enable printing Binary String in method {@link #toString()}<br>
     * <code>Default is false</code>
     *
     */
    public static void setPrintBinary(boolean printBinary) {
        ByteVector.printBinary = printBinary;
    }

    /**
     * Returns a Hex String, Binary String, and Text String representing the
     * data in this vector.
     *
     * @return a string representing the data in this vector.
     */
    public String toString() {
        byte[] cropedData = cropData(data, size);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Byte Vector Data::[");
        if (printText) {
            stringBuilder.append(String.format("Text string: [%s] ",
                    new String(cropedData)));
        }
        if (printHex) {
            stringBuilder.append(String.format("Hex string: [%s] ",
                    toHexString(cropedData)));
        }
        if (printBinary) {
            stringBuilder.append(String.format("Binary string: [%s]",
                    toBinaryString(cropedData)));
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    public static void main(String[] args) {
        byte[] data = new byte[60000];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) i;
        }
        ByteVector byteVector = new ByteVector();
        byteVector.add(data);
    }

}
