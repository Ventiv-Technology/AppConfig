/**
 * Copyright (c) 2012 Aon eSolutions
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.aon.esolutions.appconfig.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class RSAEncryptUtil 
{
	protected static final String ALGORITHM = "RSA";
	private static Log _log = LogFactory.getLog(RSAEncryptUtil.class);
	
	static {
		init();
	}

    private RSAEncryptUtil()
    {
    }

    /**
     * Init java security to add BouncyCastle as an RSA provider
     */
    public static void init()
    {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * Generate key which contains a pair of privae and public key using 1024 bytes
     * @return key pair
     * @throws NoSuchAlgorithmException
     */
    public static KeyPair generateKey(String keyPhrase) throws GeneralSecurityException
    {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
        
        SecureRandom randomAlg = SecureRandom.getInstance( "SHA1PRNG", "SUN" );
	    randomAlg.setSeed(keyPhrase.getBytes());
        
        keyGen.initialize(1024, randomAlg);        
        KeyPair key = keyGen.generateKeyPair();
        return key;
    }
    
    public static KeyPair generateKey() throws GeneralSecurityException
    {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyGen.initialize(1024);        
        KeyPair key = keyGen.generateKeyPair();
        return key;
    }


    /**
     * Encrypt a text using public key.
     * @param text The original unencrypted text
     * @param key The public key
     * @return Encrypted text
     * @throws java.lang.Exception
     */
    public static byte[] encrypt(byte[] text, Key key) throws Exception
    {
        byte[] cipherText = null;
        try
        {
            //
            // get an RSA cipher object and print the provider
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            if (_log.isDebugEnabled())
            {
                _log.debug("\nProvider is: " + cipher.getProvider().getInfo());
                _log.debug("\nStart encryption with public key");
            }

            // encrypt the plaintext using the public key
            cipher.init(Cipher.ENCRYPT_MODE, key);
            cipherText = cipher.doFinal(text);
        }
        catch (Exception e)
        {
            _log.error(e, e);
            throw e;
        }
        return cipherText;
    }

    /**
     * Encrypt a text using key. The result is enctypted BASE64 encoded text
     * @param text The original unencrypted text
     * @param key The public / private key
     * @return Encrypted text encoded as BASE64
     * @throws java.lang.Exception
     */
    public static String encrypt(String text, Key key) throws Exception
    {
        String encryptedText;
        try
        {
        	ByteArrayInputStream input = new ByteArrayInputStream(text.getBytes());
        	ByteArrayOutputStream output = new ByteArrayOutputStream();
        	
            encryptDecryptStream(input, output, key, Cipher.ENCRYPT_MODE);
            encryptedText = encodeBASE64(output.toByteArray());
            _log.debug("Enctypted text is: " + encryptedText);
        }
        catch (Exception e)
        {
            _log.error(e, e);
            throw e;
        }
        return encryptedText;
    }

    /**
     * Decrypt text using private key
     * @param text The encrypted text
     * @param key The private key
     * @return The unencrypted text
     * @throws java.lang.Exception
     */
    public static byte[] decrypt(byte[] text, Key key) throws Exception
    {
        byte[] dectyptedText = null;
        try
        {
            // decrypt the text using the private key
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            _log.debug("Start decryption");
            cipher.init(Cipher.DECRYPT_MODE, key);
            dectyptedText = cipher.doFinal(text);
        }
        catch (Exception e)
        {
            _log.error(e, e);
            throw e;
        }
        return dectyptedText;

    }

    /**
     * Decrypt BASE64 encoded text using the key
     * @param text The encrypted text, encoded as BASE64
     * @param key The private / public key
     * @return The unencrypted text encoded as UTF8
     * @throws java.lang.Exception
     */
    public static String decrypt(String text, Key key) throws Exception
    {
        String result;
        try
        {
        	ByteArrayInputStream input = new ByteArrayInputStream(decodeBASE64(text));
        	ByteArrayOutputStream output = new ByteArrayOutputStream();
        	
            // decrypt the text using the private key
        	encryptDecryptStream(input, output, key, Cipher.DECRYPT_MODE);
            byte[] dectyptedText = output.toByteArray();
            result = new String(dectyptedText, "UTF8");
            _log.debug("Decrypted text is: " + result);
        }
        catch (Exception e)
        {
            _log.error(e, e);
            throw e;
        }
        return result;

    }

    /**
     * Convert a Key to string encoded as BASE64
     * @param key The key (private or public)
     * @return A string representation of the key
     */
    public static String getKeyAsString(Key key)
    {
        // Get the bytes of the key
        byte[] keyBytes = key.getEncoded();
        // Convert key to BASE64 encoded string
        return encodeBASE64(keyBytes);
    }

    /**
     * Generates Private Key from BASE64 encoded string
     * @param key BASE64 encoded string which represents the key
     * @return The PrivateKey
     * @throws java.lang.Exception
     */
    public static PrivateKey getPrivateKeyFromString(String key) throws Exception
    {
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(decodeBASE64(key));
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
        return privateKey;
    }

    /**
     * Generates Public Key from BASE64 encoded string
     * @param key BASE64 encoded string which represents the key
     * @return The PublicKey
     * @throws java.lang.Exception
     */
    public static PublicKey getPublicKeyFromString(String key) throws Exception
    {
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(decodeBASE64(key));
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
        return publicKey;
    }
    
    /**
     * Generates Public Key from BASE64 encoded string
     * @param key BASE64 encoded string which represents the key
     * @return The PublicKey
     * @throws java.lang.Exception
     */
    public static PublicKey getPublicKeyFromInputStream(InputStream key) throws Exception
    {
    	StringWriter writer = new StringWriter();
    	IOUtils.copy(key, writer);
    	String base64String = writer.toString();
    	
        return getPublicKeyFromString(base64String);
    }
    
    /**
     * Generates Private Key from BASE64 encoded string
     * @param key BASE64 encoded string which represents the key
     * @return The PublicKey
     * @throws java.lang.Exception
     */
    public static PrivateKey getPrivateKeyFromInputStream(InputStream key) throws Exception
    {
    	StringWriter writer = new StringWriter();
    	IOUtils.copy(key, writer);
    	String base64String = writer.toString();
    	
        return getPrivateKeyFromString(base64String);
    }
    
    public static PrivateKey getPrivateKeyFromURL(URL keyUrl)
    {
    	InputStream keyStream = null;
		
		try {
			keyStream = keyUrl.openStream();
			return getPrivateKeyFromInputStream(keyStream);
		} catch (Exception e) {
			_log.error("Error getting Key From URL", e);
		} finally {				
			try {
				if (keyStream != null)
					keyStream.close();
			} catch (Exception e) {
				_log.error("Error closing Stream", e);
			}
		}
		
		return null;
    }
    
    public static PublicKey getPublicKeyFromURL(URL keyUrl)
    {
    	InputStream keyStream = null;
		
		try {
			keyStream = keyUrl.openStream();
			return getPublicKeyFromInputStream(keyStream);
		} catch (Exception e) {
			_log.error("Error getting Key From URL", e);
		} finally {				
			try {
				if (keyStream != null)
					keyStream.close();
			} catch (Exception e) {
				_log.error("Error closing Stream", e);
			}
		}
		
		return null;
    }

    /**
     * Encode bytes array to BASE64 string
     * @param bytes
     * @return Encoded string
     */
    private static String encodeBASE64(byte[] bytes)
    {
        return new String(Base64.encodeBase64(bytes));
    }

    /**
     * Decode BASE64 encoded string to bytes array
     * @param text The string
     * @return Bytes array
     * @throws IOException
     */
    private static byte[] decodeBASE64(String text) throws IOException
    {
        return Base64.decodeBase64(text.getBytes());
    }

    /**
     * Encrypt file using 1024 RSA encryption
     *
     * @param srcFileName Source file name
     * @param destFileName Destination file name
     * @param key The key. For encryption this is the Private Key and for decryption this is the public key
     * @param cipherMode Cipher Mode
     * @throws Exception
     */
    public static void encryptFile(String srcFileName, String destFileName, Key key) throws Exception
    {
        encryptDecryptFile(srcFileName,destFileName, key, Cipher.ENCRYPT_MODE);
    }

    /**
     * Decrypt file using 1024 RSA encryption
     *
     * @param srcFileName Source file name
     * @param destFileName Destination file name
     * @param key The key. For encryption this is the Private Key and for decryption this is the public key
     * @param cipherMode Cipher Mode
     * @throws Exception
     */
    public static void decryptFile(String srcFileName, String destFileName, Key key) throws Exception
    {
        encryptDecryptFile(srcFileName,destFileName, key, Cipher.DECRYPT_MODE);
    }
    
    public static String decryptBase64EncodedInputStream(InputStream input, Key key) throws Exception
    {
    	StringWriter writer = new StringWriter();
    	IOUtils.copy(input, writer);
    	String base64String = writer.toString();
    	
    	return decrypt(base64String, key);
    }
    
    public static String decryptBase64EncodedUrl(URL input, Key key)
    {
    	InputStream contentsStream = null;
		
		try {
			contentsStream = input.openStream();
			return decryptBase64EncodedInputStream(contentsStream, key);
		} catch (Exception e) {
			_log.error("Error getting File From URL", e);
		} finally {				
			try {
				if (contentsStream != null)
					contentsStream.close();
			} catch (Exception e) {
				_log.error("Error closing Stream", e);
			}
		}
		
		return null;
    }
    
    /**
     * Encrypt and Decrypt files using 1024 RSA encryption
     *
     * @param srcFileName Source file name
     * @param destFileName Destination file name
     * @param key The key. For encryption this is the Private Key and for decryption this is the public key
     * @param cipherMode Cipher Mode
     * @throws Exception
     */
    public static void encryptDecryptFile(String srcFileName, String destFileName, Key key, int cipherMode) throws Exception
    {
    	OutputStream outputWriter = new FileOutputStream(destFileName);
    	InputStream inputReader = new FileInputStream(srcFileName);
    	
    	encryptDecryptStream(inputReader, outputWriter, key, cipherMode);
    }

    /**
     * Encrypt and Decrypt streams using 1024 RSA encryption
     *
     * @param inputReader Source InputStream
     * @param outputWriter Destination OutputStream
     * @param key The key. Either the Public Key or Private Key
     * @param cipherMode Cipher Mode
     * @throws Exception
     */
    public static void encryptDecryptStream(InputStream inputReader, OutputStream outputWriter, Key key, int cipherMode) throws Exception
    {
        try
        {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            //RSA encryption data size limitations are slightly less than the key modulus size,
            //depending on the actual padding scheme used (e.g. with 1024 bit (128 byte) RSA key,
            //the size limit is 117 bytes for PKCS#1 v 1.5 padding. (http://www.jensign.com/JavaScience/dotnet/RSAEncrypt/)
            byte[] buf = cipherMode == Cipher.ENCRYPT_MODE? new byte[100] : new byte[128];
            int bufl;
            // init the Cipher object for Encryption...
            cipher.init(cipherMode, key);

            // start FileIO
            while ( (bufl = inputReader.read(buf)) != -1)
            {
                byte[] encText = null;
                if (cipherMode == Cipher.ENCRYPT_MODE)
                {
                      encText = encrypt(copyBytes(buf,bufl),key);
                }
                else
                {
                    if (_log.isDebugEnabled())
                    {
                        _log.debug("buf = " + new String(buf));
                    }
                    encText = decrypt(copyBytes(buf,bufl),key);
                }
                outputWriter.write(encText);
                if (_log.isDebugEnabled())
                {
                    _log.debug("encText = " + new String(encText));
                }
            }
            outputWriter.flush();

        }
        catch (Exception e)
        {
            _log.error(e,e);
            throw e;
        }
        finally
        {
            try
            {
                if (outputWriter != null)
                {
                    outputWriter.close();
                }
                if (inputReader != null)
                {
                    inputReader.close();
                }
            }
            catch (Exception e)
            {
                // do nothing...
            } // end of inner try, catch (Exception)...
        }
    }

    public static byte[] copyBytes(byte[] arr, int length)
    {
        byte[] newArr = null;
        if (arr.length == length)
        {
            newArr = arr;
        }
        else
        {
            newArr = new byte[length];
            for (int i = 0; i < length; i++)
            {
                newArr[i] = (byte) arr[i];
            }
        }
        return newArr;
    }
    
    public static void main(String...args) throws Exception { 
    	if (args.length < 2) {
    		System.out.println("Usage: java org.aon.esolutions.appconfig.util.RSAEncryptUtil generateKeys <passphrase>");
    		System.out.println("Usage: java org.aon.esolutions.appconfig.util.RSAEncryptUtil encryptPrivate <passphrase> <encryptText>");
    		System.out.println("Usage: java org.aon.esolutions.appconfig.util.RSAEncryptUtil encryptPublic <passphrase> <encryptText>");
    		return;
    	}
    	
    	String method = args[0];
    	
    	if (method.equals("generateKeys")) {
    		String passphrase = args[1];
    		
    		KeyPair keyPair = generateKey(passphrase);
    		System.out.println("Keys for Passphrase: " + passphrase);
    		System.out.println("\nPUBLIC KEY:");
    		System.out.println(getKeyAsString(keyPair.getPublic()));
    		
    		System.out.println("\nPRIVATE KEY:");
    		System.out.println(getKeyAsString(keyPair.getPrivate()));
    	} else if (method.startsWith("encrypt")) {
    		String passphrase = args[1];
    		String toEncrypt = args[2];
    		KeyPair keyPair = generateKey(passphrase);
    		Key toUse = null;
    		if (method.toLowerCase().endsWith("private")) {
    			System.out.println("USING PRIVATE KEY (" + passphrase + "):");
    			toUse = keyPair.getPrivate();
    		}
    		else {
    			System.out.println("USING PUBLIC KEY (" + passphrase + "):");
    			toUse = keyPair.getPublic();
    		}
    			
    		System.out.println(getKeyAsString(toUse));
    		
    		String encrypted = encrypt(toEncrypt, toUse);
    		System.out.println("\nUN-ENCRYPTED STRING:");
    		System.out.println(toEncrypt);
    		System.out.println("\nENCRYPTED STRING:");
    		System.out.println(encrypted);    		
    	} else {
    		System.out.println(method + " is not a known command");
    	}
    	
    		
    }
}
