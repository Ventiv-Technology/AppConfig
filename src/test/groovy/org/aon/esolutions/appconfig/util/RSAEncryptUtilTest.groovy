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

import static org.junit.Assert.*;

import java.security.KeyPair;

import org.junit.Test;

class RSAEncryptUtilTest 
{
	KeyPair keyPair = RSAEncryptUtil.generateKey()
	String testText = """This is some sensitive text.  It needs to be encrypted"""
	

	@Test
	public void testEncryptDecryptStreamPrivateEncryption()
	{
		String encrypted = RSAEncryptUtil.encrypt(testText, keyPair.getPrivate())
		String decrypted = RSAEncryptUtil.decrypt(encrypted, keyPair.getPublic())
		
		assert testText.equals(decrypted)
	}
	
	@Test
	public void testEncryptDecryptStreamPrivateEncryptionFromKeyString()
	{
		String privateKeyAsStr = RSAEncryptUtil.getKeyAsString(keyPair.getPrivate())
		String publicKeyAsStr = RSAEncryptUtil.getKeyAsString(keyPair.getPublic())
		
		String encrypted = RSAEncryptUtil.encrypt(testText, RSAEncryptUtil.getPrivateKeyFromString(privateKeyAsStr))
		String decrypted = RSAEncryptUtil.decrypt(encrypted, RSAEncryptUtil.getPublicKeyFromString(publicKeyAsStr))
		
		assert testText.equals(decrypted)
	}
	
	@Test
	public void testEncryptDecryptStreamPrivateEncryptionFromKeyInputStream()
	{
		String privateKeyAsStr = RSAEncryptUtil.getKeyAsString(keyPair.getPrivate())
		String publicKeyAsStr = RSAEncryptUtil.getKeyAsString(keyPair.getPublic())
		
		String encrypted = RSAEncryptUtil.encrypt(testText, RSAEncryptUtil.getPrivateKeyFromInputStream(new ByteArrayInputStream(privateKeyAsStr.getBytes())))
		String decrypted = RSAEncryptUtil.decrypt(encrypted, RSAEncryptUtil.getPublicKeyFromInputStream(new ByteArrayInputStream(publicKeyAsStr.getBytes())))
		
		assert testText.equals(decrypted)
	}
	
	@Test
	public void testEncryptDecryptStreamPublicEncryption()
	{
		String encrypted = RSAEncryptUtil.encrypt(testText, keyPair.getPublic())
		String decrypted = RSAEncryptUtil.decrypt(encrypted, keyPair.getPrivate())
		
		assert testText.equals(decrypted)
	}
	
	@Test
	public void testEncryptDecryptStreamModifiedKey()
	{
		String encrypted = RSAEncryptUtil.encrypt(testText, keyPair.getPrivate())
		char original = encrypted.charAt(20)
		char newChar = original + 1
		
		// Modify the key
		encrypted = encrypted.substring(0,20) + newChar + encrypted.substring(21)

		try {
			String decrypted = RSAEncryptUtil.decrypt(encrypted, keyPair.getPublic())
			fail ("Modified key should not be able to be decrypted")
		} catch (Exception e) {
			assert true
		}
		
		// Put it back
		encrypted = encrypted.substring(0,20) + original + encrypted.substring(21)
		String decrypted = RSAEncryptUtil.decrypt(encrypted, keyPair.getPublic())
		
		assert testText.equals(decrypted)
	}
	
	@Test
	// This test ensures that clients will NOT be able to encrypt their own key files
	public void testEncryptDecryptWithPublicKey()
	{
		String encrypted = RSAEncryptUtil.encrypt(testText, keyPair.getPublic())
		
		try {
			String decrypted = RSAEncryptUtil.decrypt(encrypted, keyPair.getPublic())
			fail ("Should not be able to Encrypt and Decrypt with the same key")
		} catch (Exception e) {
			assert true
		}
	}
	
	@Test
	public void testDecryptFromStream()
	{
		String encrypted = RSAEncryptUtil.encrypt(testText, keyPair.getPrivate())
		InputStream input = new ByteArrayInputStream(encrypted.getBytes())
		
		String decrypted = RSAEncryptUtil.decryptBase64EncodedInputStream(input, keyPair.getPublic())
		
		assert testText.equals(decrypted)
	}
}
