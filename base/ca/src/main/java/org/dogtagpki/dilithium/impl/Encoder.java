package org.dogtagpki.dilithium.impl;

import java.io.InvalidObjectException;
import java.math.BigInteger;

public class Encoder {
    private static boolean isPositiveInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        if (str.charAt(0) == '-') {
            return false;
        }
        for (int i = 0; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    private static byte[] OIDByteArray(int value) {
        if (value < 128){
            return new byte[]{(byte)value};
        }else {
            return concat(OIDByteArray(value/128), (byte)(value%128));
        }

    }

    public static byte[] concat(byte[] a, byte[] b){
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public static byte[] concat(byte[] a, byte b){
        byte[] c = new byte[a.length + 1];
        System.arraycopy(a, 0, c, 0, a.length);
        c[a.length] = b;
        return c;
    }

    public static byte[] concat(byte a, byte[] b){
        byte[] c = new byte[b.length + 1];
        c[0] = a;
        System.arraycopy(b, 0, c, 1, b.length);
        return c;
    }

    public static byte[] OIDtoBytes(String OID) throws InvalidObjectException {
        String[] parts = OID.split("\\.");
        if (parts.length < 3){
            throw new InvalidObjectException("OID doesn't match format: Not enough parts");
        }
        int[] oidInts= new int[parts.length - 1];
        for (int i = 0; i < oidInts.length; i++) {
            if (isPositiveInteger(parts[i])){
                if (i == 0){
                    oidInts[i] = 40*Integer.parseInt(parts[i]);
                }
                oidInts[i] += Integer.parseInt(parts[i + 1]);
            }else {
                throw new InvalidObjectException("OID doesn't match format: Parts aren't positive integers");
            }
        }

        byte[] oidBytes = new byte[0];
        for (int part : oidInts) {
            byte[] partArray = OIDByteArray(part);
            for (int i = 0; i < partArray.length - 1; i++) {
                partArray[i] += (byte) 128;
            }
            oidBytes = concat(oidBytes, partArray);
        }
        return oidBytes;
    }

    public static byte[] intToBytes(int number){
        return intToBytes(BigInteger.valueOf(number));
    }
    public static byte[] intToBytes(BigInteger number){
        return number.toByteArray();
    }

    public static byte[] encodeLength(byte[] value){
        byte[] tagLength;
        if (value.length < 128) {
            tagLength = new byte[]{(byte)value.length};
        }else {
            byte[] length = intToBytes(value.length);
            byte longLength = (byte)(length.length + 128);
            tagLength = concat(longLength, length);
        }
        return concat(tagLength, value);
    }

    public static byte[] encode(byte[] value, byte tag, String tagClass, int instructions, boolean constructed, boolean explicit, boolean noLength){
        byte[] valueWithLength = value;
        if (noLength){
            valueWithLength = encodeLength(value);
        }
        byte[] newValue = valueWithLength;
        if (explicit){
            newValue = encodeLength(concat(tag, valueWithLength));
        }
        byte newTag = (byte) instructions;
        if (constructed){
            newTag |= (byte) (1 << 5);
        }
        switch (tagClass){
            case "Application":
                newTag |= (byte) (1 << 6);
                break;
            case "Context":
                newTag |= (byte) (2 << 6);
                break;
            case "Private":
                newTag |= (byte) (3 << 6);
                break;
        }

        return concat(newTag, newValue);
    }

    public static byte[] encode(byte[] value, byte tag, boolean constructed, boolean noLength){
        byte[] valueWithLength = value;
        if (noLength){
            valueWithLength = encodeLength(value);
        }
        byte newTag = tag;
        if (constructed){
            newTag |= (byte) (1 << 5);
        }
        return concat(newTag, valueWithLength);
    }
}
