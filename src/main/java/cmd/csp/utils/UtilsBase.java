package cmd.csp.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.UUID;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.*;

public class UtilsBase {

	public UtilsBase() {
		// TODO Auto-generated constructor stub
	}

    /**
     * Checks if return type is a Vert.x event bus compatible type
     *
     * @param o
     * @return
     */
    public static boolean isCompatibleEventBusType(final Object o) {
        if (o instanceof JsonObject
                || o instanceof Byte
                || o instanceof Character
                || o instanceof Double
                || o instanceof Float
                || o instanceof Integer
                || o instanceof JsonArray
                || o instanceof Long
                || o instanceof Short
                || o instanceof String
                || o instanceof byte[]
                || o instanceof Buffer
                || o instanceof Boolean) {
            return true;
        }

        return false;
    }
    
    /**
     * Checks if return type is a Vert.x event bus compatible type
     *
     * @param o
     * @return
     */
    @SuppressWarnings("rawtypes")
	public static boolean isCompatibleEventBusClass(final Class o) {
        if (o.equals(JsonObject.class )
                || o.equals(Byte.class )
                || o.equals(Character.class )
                || o.equals(Double.class )
                || o.equals(Float.class )
                || o.equals(Integer.class )
                || o.equals(JsonArray.class )
                || o.equals(Long.class )
                || o.equals(Short.class )
                || o.equals(String.class )
                || o.equals(byte[].class )
                || o.equals(Buffer.class )
                || o.equals(Boolean.class ) ) {
            return true;
        }

        return false;
    }
    public static String createUUIDString() {
 		return UUID.randomUUID().toString();
 	}

	@SuppressWarnings("unused")
	private String toHexString(byte[] bytes) {
		StringBuilder hexString = new StringBuilder();
		for (byte aByte : bytes) {
			String hex = Integer.toHexString(0xff & aByte);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}
		return hexString.toString();
	}
	
    public static byte[] serializeToByte(Object obj) throws IOException {
        final ByteArrayOutputStream b = new ByteArrayOutputStream();
        final ObjectOutputStream o = new ObjectOutputStream(b);
        o.writeObject(obj);
        o.close();
        return b.toByteArray();
    }

    public static Object deserializeFromByte(byte[] bytes) throws IOException, ClassNotFoundException {
        final ByteArrayInputStream b = new ByteArrayInputStream(bytes);
        final ObjectInputStream o = new ObjectInputStream(b);
        return o.readObject();
    }
    /**
     * Calculate a MD5 hash from the given input string.
     * @param data String with some data
     * @return 32 Bytes MD5 Hash
     */
    @SuppressWarnings("finally")
	public static String getHashMD5( String data ){
        String md5 = null;
        try {
            if(data != null){
                MessageDigest m = MessageDigest.getInstance("MD5");
                m.reset();
                m.update(data.getBytes());
                byte[] digest = m.digest();
                BigInteger bigInt = new BigInteger(1,digest);
                md5 = bigInt.toString(16);
                while(md5.length() < 32 ){
                    md5 = "0"+md5;
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } finally {
            return md5;
        }
    }

    /**
     * Calculate a SHA-512 hash from the given input string.
     * @param data String with some data
     * @return 128 Bytes SHA-512 Hash
     */
    public static String getHashSHA512(String data){
        return getHashSHA(data,"SHA-512");
    }

    /**
     * Calculate a SHA-256 hash from the given input string.
     * @param data String with some data
     * @return 64 Bytes SHA-256 Hash
     */
    public static String getHashSHA256(String data){
        return getHashSHA(data,"SHA-256");
    }

    /**
     * Calculate a SHA based hash from the given input string.
     * @param data String with some data
     * @param algorithm SHA-256, SHA-512
     * @return 64 Bytes SHA Hash
     */
    public static String getHashSHA( String data, String algorithm ){
        if(algorithm == null){
            algorithm = "SHA-256";
        }
        String encoding = "UTF-8";
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.update(data.getBytes(encoding));
            byte[] digest = md.digest();
            if(algorithm.equalsIgnoreCase("SHA-512")){
                return String.format("%0128x", new java.math.BigInteger(1, digest));
            } else {
                return String.format("%064x", new java.math.BigInteger(1, digest));
            }

        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("Hashing algorithm "+algorithm+" not available.");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException("Unsupported encoding "+encoding);
        }
    }

    /**
     * Hashing function for secure password storage, source: https://www.owasp.org/index.php/Hashing_Java
     * @param password Password
     * @param salt Salt Should be at least 32 bytes long, must be saved with the password.
     * @param iterations Iterations Depends on Hardware, must be saved with the password.
     * @param keyLength Key Length, should be at least 256
     * @return Byte Array with an hashed password.
     */
    public static byte[] getHashPassword(final char[] password, final byte[] salt, final int iterations, final int keyLength) {
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
            SecretKey key = skf.generateSecret(spec);
            byte[] res = key.getEncoded();
            return res;

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }
    

    /**
     * Convert a java.util.Calendar object to an ISO8601 UTC string.
     * @param calendar Calendar object or null if we want to convert now.
     * @return ISO8601 UTC string
     */
    public static String convertCalendarToISO8601( Calendar calendar ){
        if(calendar == null){
            calendar = GregorianCalendar.getInstance();
        }
        Date date = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        String formatted = formatter.format(date);
        return formatted.substring(0, 22) + ":" + formatted.substring(22);
    }

    /**
     * Convert an ISO8601 UTC string to a Calendar object.
     * @param iso8601string ISO8601 UTC string
     * @return Calendar object
     */
    @SuppressWarnings("finally")
	public static Calendar convertISO8610ToCalendar(String iso8601string ){
        Calendar calendar = GregorianCalendar.getInstance();
        String s = iso8601string.replace("Z", "+00:00");
        try {
            s = s.substring(0, 22) + s.substring(23);  // to get rid of the ":"
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = formatter.parse(s);
            calendar.setTime(date);
        }catch(IndexOutOfBoundsException e){
            //e.printStackTrace();
        } catch (ParseException e) {
            //e.printStackTrace();
        } finally {
            return calendar;
        }
    }


    /**
     * Convert an ISO8601 UTC string to an UNIX timestamp.
     * @param iso8601string ISO8601 UTC string
     * @return long UNIX timestamp
     */
    public static long convertISO8610ToTimestamp( String iso8601string ){
        if(iso8601string != null){
            Calendar calendar = convertISO8610ToCalendar(iso8601string);
            return calendar.getTimeInMillis()/1000;
        }
        return 0;
    }	
	
	
	
}
