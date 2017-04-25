package com.hotmail.frojasg1.applications.fileencoderapplication.simpleapdu;

//import com.hotmail.frojasg1.applications.fileencoderapplication.applets.SimpleApplet;
import com.sun.javafx.logging.PulseLogger;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.SecureRandom;
import javacard.security.RSAPublicKey;
import java.util.Random;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.JCSystem;
import javacard.framework.Util;
import javacard.security.AESKey;
import javacard.security.CryptoException;
import javacard.security.Key;
import javacard.security.KeyBuilder;
import javacard.security.KeyPair;
import javacard.security.MessageDigest;
import javacard.security.RSAPrivateKey;
import javacardx.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.smartcardio.ResponseAPDU;

/**
 *
 * @author xpatnaik
 */
public class SimpleAPDU {

    static CardMngr cardManager = new CardMngr();

    private static byte DEFAULT_USER_PIN[] = {(byte) 0x39, (byte) 0x39, (byte) 0x39, (byte) 0x39};// default PIN set to 9999
    private static byte NEW_USER_PIN[] = {(byte) 0x31, (byte) 0x31, (byte) 0x31, (byte) 0x31};// new PIN set to 1111
    private static byte APPLET_AID[] = {(byte) 0x73, (byte) 0x69, (byte) 0x6D, (byte) 0x70, (byte) 0x6C,
        (byte) 0x65, (byte) 0x61, (byte) 0x70, (byte) 0x70, (byte) 0x6C, (byte) 0x65, (byte) 0x74};
    private static byte SELECT_SIMPLEAPPLET[] = {(byte) 0x00, (byte) 0xa4, (byte) 0x04, (byte) 0x00, (byte) 0x0b,
        (byte) 0x73, (byte) 0x69, (byte) 0x6D, (byte) 0x70, (byte) 0x6C,
        (byte) 0x65, (byte) 0x61, (byte) 0x70, (byte) 0x70, (byte) 0x6C, (byte) 0x65, (byte) 0x74};

    private static byte NEW_KEY_256[] = {(byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06,
        (byte) 0x07, (byte) 0x08, (byte) 0x09, (byte) 0x10, (byte) 0x11,
        (byte) 0x12, (byte) 0x13, (byte) 0x15, (byte) 0x15, (byte) 0x16, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06,
        (byte) 0x07, (byte) 0x08, (byte) 0x09, (byte) 0x10, (byte) 0x11,
        (byte) 0x12, (byte) 0x13, (byte) 0x15, (byte) 0x15, (byte) 0x16};

    private static Key randPCPriv = null;//(RSAPrivateKey) KeyBuilder.buildKey(KeyBuilder.TYPE_RSA_PRIVATE, KeyBuilder.LENGTH_RSA_2, false);
    private static Key randPCPub = null;
    private static Cipher randPCCipher = null;
    private static AESKey aesKey = null;
    private static MessageDigest pc_hash = null;
    
    private static AESKey keyEncryptCard = null;
    private static AESKey keyMacPcCard = null;
    private static AESKey keyEncryptCardPc = null;
    
    //KeyPair randPCKeyPair = null;
    
    /*protected SimpleAPDU() {
        //aesKey = (AESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_AES, KeyBuilder.LENGTH_AES_256, false);
        keyEncryptCard = (AESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_AES, KeyBuilder.LENGTH_AES_256, false);
        keyMacPcCard = (AESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_AES, KeyBuilder.LENGTH_AES_256, false);
        keyEncryptCardPc = (AESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_AES, KeyBuilder.LENGTH_AES_256, false);
    }*/

    public static String main(String[] args) {    
        
        String passwordAsString = new String();
        
        try {
            //
            // SIMULATED CARDS
            //

            // Prepare simulated card 
            byte[] installData = new byte[10]; // no special install data passed now - can be used to pass initial keys etc.
            //cardManager.prepareLocalSimulatorApplet(APPLET_AID, installData, SimpleApplet.class);
            long startTime = 0;
            //long elapsedTime = 0;
            long endTime = 0;
            //---------------------- TODO: apdu for INS_SETKEY to set a new key K after Verification--------------------------------
            /*short additionalDataLenPIN = 4;
            byte apdu_verifyPIN[] = new byte[CardMngr.HEADER_LENGTH + additionalDataLenPIN];
            apdu_verifyPIN[CardMngr.OFFSET_CLA] = (byte) 0xB0;// class B0
            apdu_verifyPIN[CardMngr.OFFSET_INS] = (byte) 0x55;// for INS_VERIFYPIN
            apdu_verifyPIN[CardMngr.OFFSET_P1] = (byte) 0x00;
            apdu_verifyPIN[CardMngr.OFFSET_P2] = (byte) 0x00;
            apdu_verifyPIN[CardMngr.OFFSET_LC] = (byte) additionalDataLenPIN;// 4 byte data for PIN

            // TODO: if additional data are supplied (additionalDataLen != 0), then copy input data here starting from CardMngr.CardMngr.OFFSET_DATA
            if (additionalDataLenPIN != 0) {
                System.arraycopy(NEW_USER_PIN, 0, apdu_verifyPIN, CardMngr.OFFSET_DATA, additionalDataLenPIN);
            }

            // NOTE: we are using sendAPDUSimulator() instead of sendAPDU()
            //byte[] responseVerifyPIN = cardManager.sendAPDUSimulator(apdu_verifyPIN); 
            byte[] responseVerifyPIN = null;
            //
            // REAL CARDS
            //
            // TODO: Try same with real card
            if (cardManager.ConnectToCard()) {

                // Select our application on card
                cardManager.sendAPDU(SELECT_SIMPLEAPPLET);

                // TODO: send proper APDU
                ResponseAPDU output = cardManager.sendAPDU(apdu_verifyPIN);
                responseVerifyPIN = output.getBytes();

                cardManager.DisconnectFromCard();

            } else {
                System.out.println("Failed to connect to card");
            }

            System.out.println(CardMngr.bytesToHex(responseVerifyPIN));

            if ((responseVerifyPIN[0] == -112) && (responseVerifyPIN[1] == 0)) {

                System.out.println("PIN VERIFIED !!");

                //------------------------GENERATE RANDOM KEY ON CARD------------------------------------------
                short keyLength = 32;
                short countLen = 4;
                byte apdu_getKEY[] = new byte[CardMngr.HEADER_LENGTH + countLen];
                apdu_getKEY[CardMngr.OFFSET_CLA] = (byte) 0xB0;
                apdu_getKEY[CardMngr.OFFSET_INS] = (byte) 0x52;// Set Key
                apdu_getKEY[CardMngr.OFFSET_P1] = (byte) keyLength;
                apdu_getKEY[CardMngr.OFFSET_P2] = (byte) countLen;
                apdu_getKEY[CardMngr.OFFSET_LC] = (byte) countLen;

                byte[] randCount = new byte[countLen];
                SecureRandom sRandom = new SecureRandom();
                sRandom.nextBytes(randCount);
                //System.out.println(CardMngr.bytesToHex(randCount));

                if (countLen != 0) {
                    System.arraycopy(randCount, 0, apdu_getKEY, CardMngr.OFFSET_DATA, countLen);
                }

                System.out.println(CardMngr.bytesToHex(randCount));

                byte[] responseGetKEY = null;
                // NOTE: we are using sendAPDUSimulator() instead of sendAPDU()
                //      response = cardManager.sendAPDUSimulator(Randapdu); 
                if (cardManager.ConnectToCard()) {
                    // Select our application on card
                    cardManager.sendAPDU(SELECT_SIMPLEAPPLET);

                    // TODO: send proper APDU
                    startTime = System.currentTimeMillis();
                    ResponseAPDU output = cardManager.sendAPDU(apdu_getKEY);
                    endTime = System.currentTimeMillis();
                    responseGetKEY = output.getBytes();
                    cardManager.DisconnectFromCard();
                } else {
                    System.out.println("Failed to connect to card");
                }

                System.out.println(CardMngr.bytesToHex(responseGetKEY));

                if ((responseGetKEY[responseGetKEY.length - 2] == -112) && (responseGetKEY[responseGetKEY.length - 1] == 0)) {

                    System.out.println("RANDOM AES KEY SET !!");
                }
                System.out.println("TOTAL TIME FOR KEY SETTING = " + (endTime - startTime) + " msecs");
                
                byte[] keyToWrite = new byte[keyLength];
                byte[] countToWrite = new byte[countLen];
                System.arraycopy(responseGetKEY, 0, keyToWrite, 0, keyLength);
                System.arraycopy(responseGetKEY, keyLength, countToWrite, 0, countLen);

                File keyFile = new File("key.bin");
                File countFile = new File("count.bin");
                try {
                    OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(keyFile));
                        outputStream.write(keyToWrite);
                        outputStream.close();
                    
                    OutputStream outputStream1 = new BufferedOutputStream(new FileOutputStream(countFile));
                        outputStream1.write(countToWrite);
                        outputStream1.close();
                    
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                System.out.println("PIN VERIFICATION FAILED !!");
            }*/
            //------------------------------------------------------------------------------------------------------

            /*RSAPrivateKey bPriv = null;
            RSAPublicKey bPub = null;//(RSAPublicKey) KeyBuilder.buildKey(KeyBuilder.TYPE_RSA_PUBLIC, KeyBuilder.LENGTH_RSA_2048, false);
            KeyPair dhKeyPair = null;
            Cipher dhCipher = null;

            try {
                dhKeyPair = new KeyPair(KeyPair.ALG_RSA_CRT, KeyBuilder.LENGTH_RSA_1024);
                dhKeyPair.genKeyPair();

                bPriv = (RSAPrivateKey) dhKeyPair.getPrivate();
                //System.out.println(CardMngr.bytesToHex(bPriv.));
                bPub = (RSAPublicKey) dhKeyPair.getPublic();
                dhCipher = Cipher.getInstance(Cipher.ALG_RSA_PKCS1, false);
                dhCipher.init(bPriv, Cipher.MODE_DECRYPT);
                byte[] t1 = {(byte) 0x02, (byte) 0x03, (byte) 0x02, (byte) 0x03, (byte) 0x02, (byte) 0x03, (byte) 0x02, (byte) 0x03, (byte) 0x02, (byte) 0x03, (byte) 0x02, (byte) 0x03, (byte) 0x02, (byte) 0x03, (byte) 0x02, (byte) 0x03};
                byte[] temp = new byte[16];
                dhCipher.doFinal(t1, (short) 0, (short) 16, temp, (short) 0);
            } catch (CryptoException e) {
                short reason = e.getReason();
                ISOException.throwIt(reason);
            }*/
            //System.out.println(CardMngr.bytesToHex(temp));
            //-----------------------------------------------------------------------------------------------------------
            //TODO: Read key.bin
            File keyFile = new File("key.bin");
            byte[] keyArray = new byte[(int) keyFile.length()];
            System.out.println(keyFile.length());

            try {
                FileInputStream fis = new FileInputStream(keyFile);
                fis.read(keyArray);
                fis.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println(CardMngr.bytesToHex(keyArray));
            //TODO: Read count.bin
            File countFile = new File("count.bin");
            byte[] countArray = new byte[(int) countFile.length()];

            try {
                FileInputStream fis = new FileInputStream(countFile);
                fis.read(countArray);
                fis.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println(CardMngr.bytesToHex(countArray));

            Cipher encryptCipherCBC = null;// MILAN
            Cipher decryptCipherCBC = null;

            aesKey = (AESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_AES, KeyBuilder.LENGTH_AES_256, false);

            aesKey.setKey(keyArray, (short) 0);

            encryptCipherCBC = Cipher.getInstance(Cipher.ALG_AES_BLOCK_128_CBC_NOPAD, false);
            decryptCipherCBC = Cipher.getInstance(Cipher.ALG_AES_BLOCK_128_CBC_NOPAD, false);

            encryptCipherCBC.init(aesKey, Cipher.MODE_ENCRYPT);
            decryptCipherCBC.init(aesKey, Cipher.MODE_DECRYPT);

            // MILAN : CREATE OBJECTS FOR CBC CIPHERING
            byte[] data_PC_Card_1 = new byte[NEW_USER_PIN.length + countArray.length + 8];

            System.arraycopy(NEW_USER_PIN, 0, data_PC_Card_1, 0, NEW_USER_PIN.length);
            System.arraycopy(countArray, 0, data_PC_Card_1, NEW_USER_PIN.length, countArray.length);

            System.out.println(CardMngr.bytesToHex(data_PC_Card_1));
            short additionalDataLen_PC_Card_1 = (short) (NEW_USER_PIN.length + countArray.length + 8); //PIN=4 + count=4
            //System.out.println()
            byte[] apdu_PC_Card_1 = new byte[CardMngr.HEADER_LENGTH + additionalDataLen_PC_Card_1];
            apdu_PC_Card_1[CardMngr.OFFSET_CLA] = (byte) 0xB0;// class B0
            apdu_PC_Card_1[CardMngr.OFFSET_INS] = (byte) 0x76;// for INS_PC_CARD_1
            apdu_PC_Card_1[CardMngr.OFFSET_P1] = (byte) 0x00;
            apdu_PC_Card_1[CardMngr.OFFSET_P2] = (byte) 0x00;
            apdu_PC_Card_1[CardMngr.OFFSET_LC] = (byte) additionalDataLen_PC_Card_1;// 4 byte data for PIN           

            if (additionalDataLen_PC_Card_1 != 0) {
                //encryptCipherCBC.doFinal(data_PC_Card_1, (short) 0, (short) data_PC_Card_1.length, apdu_PC_Card_1, CardMngr.OFFSET_DATA);
                //System.arraycopy(data_PC_Card_1, 0, apdu_PC_Card_1, CardMngr.OFFSET_DATA, additionalDataLen_PC_Card_1);
                encryptCipherCBC.doFinal(data_PC_Card_1, (short) 0, additionalDataLen_PC_Card_1, apdu_PC_Card_1, CardMngr.OFFSET_DATA);
            }

            byte[] response_PC_Card_1 = null;

            if (cardManager.ConnectToCard()) {
                // Select our application on card
                cardManager.sendAPDU(SELECT_SIMPLEAPPLET);

                // TODO: send proper APDU
                startTime = System.currentTimeMillis();
                ResponseAPDU output = cardManager.sendAPDU(apdu_PC_Card_1);
                endTime = System.currentTimeMillis();
                response_PC_Card_1 = output.getBytes();
                cardManager.DisconnectFromCard();
            } else {
                System.out.println("Failed to connect to card");
            }

            System.out.println(CardMngr.bytesToHex(response_PC_Card_1));

            if ((response_PC_Card_1[response_PC_Card_1.length - 2] == -112) && (response_PC_Card_1[response_PC_Card_1.length - 1] == 0)) {

                System.out.println("PC_CARD_1 DONE !!");
            }

            byte[] tempBuff = new byte[response_PC_Card_1.length - 2];

            decryptCipherCBC.doFinal(response_PC_Card_1, (short) 0, (short) (response_PC_Card_1.length - 2), tempBuff, (short) 0);

            System.out.println(CardMngr.bytesToHex(tempBuff));
            
            byte[] cardRandom = new byte[44];
            byte[] counterFromCard = new byte[4];
            
            System.arraycopy(tempBuff, 0, cardRandom, 0, 44);
            System.arraycopy(tempBuff, 44, counterFromCard, 0, 4);
            

            System.out.println(CardMngr.bytesToHex(cardRandom));
            System.out.println(CardMngr.bytesToHex(counterFromCard));
           

            byte[] countCheck = new byte[countArray.length];

            System.arraycopy(countArray, 0, countCheck, 0, countArray.length);

            incrementCounter(countCheck);

            if (Util.arrayCompare(countCheck, (short) 0, counterFromCard, (short) 0, (short) countArray.length) == 0) {
                System.out.println("COUNTER VERIFIED");
                incrementCounter(countArray);
                
                //countFile = new File("D:\\Masaryk\\SEM2\\PV204 Security Technologies\\PC_Application.v1.0\\_source\\FileEncoderApplication_SymSec1\\count.bin");
                try {
                    
                    OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(countFile));
                    outputStream.write(countArray);
                    outputStream.close();
                    
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
                
            } else {
                System.out.println("COUNTER VERIFICATION FAILED");
                System.exit(1);
            }

            System.out.println("TOTAL TIME FOR PC_CARD_1 = " + (endTime - startTime) + " msecs");
            
            
            
            byte[] pcRandom = new byte[44];
            byte[] pcCardRandom = new byte[88];
            byte[] cardPcRandom = new byte[88];
            SecureRandom sRandom = SecureRandom.getInstance("SHA1PRNG");
            sRandom.nextBytes(pcRandom);
            System.out.println(CardMngr.bytesToHex(pcRandom));
            
            System.arraycopy(pcRandom, 0, pcCardRandom, 0, pcRandom.length);
            System.arraycopy(cardRandom, 0, pcCardRandom, pcRandom.length, cardRandom.length);
            
            System.arraycopy(cardRandom, 0, cardPcRandom, 0, cardRandom.length);
            System.arraycopy(pcRandom, 0, cardPcRandom, cardRandom.length, pcRandom.length);
            
            
            byte[] hashBuffer = new byte[20];
            hashBuffer = generateHMAC(cardRandom, aesKey);            
            //System.out.println(CardMngr.bytesToHex(hashBuffer));
            
            keyEncryptCard = (AESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_AES, KeyBuilder.LENGTH_AES_256, false);                       
            byte[] keyFromHash = new byte[32];
            keyFromHash = hashToKey(hashBuffer);
            keyEncryptCard.setKey(keyFromHash, (short) 0);
            
            keyMacPcCard = (AESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_AES, KeyBuilder.LENGTH_AES_256, false); 
            hashBuffer = generateHMAC(pcCardRandom, aesKey);            
            keyFromHash = hashToKey(hashBuffer);
            keyMacPcCard.setKey(keyFromHash, (short) 0);
            
            keyEncryptCardPc = (AESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_AES, KeyBuilder.LENGTH_AES_256, false); 
            hashBuffer = generateHMAC(cardPcRandom, aesKey);            
            keyFromHash = hashToKey(hashBuffer);
            keyEncryptCardPc.setKey(keyFromHash, (short) 0);
            
            

            

            //------------------------ PC_CARD_2 START----------------------------
            
            
            byte[] pcRandomHMAC = generateHMAC(pcRandom, keyMacPcCard); 
          
            byte[] data_PC_Card_2 = new byte[64]; 
            
            System.arraycopy(pcRandom, 0, data_PC_Card_2, 0, pcRandom.length);
            System.arraycopy(pcRandomHMAC, 0, data_PC_Card_2, pcRandom.length, pcRandomHMAC.length);           

            encryptCipherCBC.init(keyEncryptCard, Cipher.MODE_ENCRYPT);
            decryptCipherCBC.init(keyEncryptCard, Cipher.MODE_DECRYPT);
            
            //encryptCipherCBC.doFinal(data_PC_Card_2, (short) 0, (short) data_PC_Card_2.length, data_PC_Card_2, (short) 0);
           
            //System.out.println(CardMngr.bytesToHex(data_PC_Card_2));

            short additionalDataLen_PC_Card_2 = (short) (data_PC_Card_2.length);            
            byte[] apdu_PC_Card_2 = new byte[CardMngr.HEADER_LENGTH + additionalDataLen_PC_Card_2];
            apdu_PC_Card_2[CardMngr.OFFSET_CLA] = (byte) 0xB0;// class B0
            apdu_PC_Card_2[CardMngr.OFFSET_INS] = (byte) 0x77;// for INS_PC_CARD_2
            apdu_PC_Card_2[CardMngr.OFFSET_P1] = (byte) 0x00;
            apdu_PC_Card_2[CardMngr.OFFSET_P2] = (byte) 0x00;
            apdu_PC_Card_2[CardMngr.OFFSET_LC] = (byte) additionalDataLen_PC_Card_2;           

            if (additionalDataLen_PC_Card_2 != 0) {

                encryptCipherCBC.doFinal(data_PC_Card_2, (short) 0, additionalDataLen_PC_Card_2, apdu_PC_Card_2, CardMngr.OFFSET_DATA);
            }

            byte[] response_PC_Card_2 = null;

            if (cardManager.ConnectToCard()) {
                // Select our application on card
                cardManager.sendAPDU(SELECT_SIMPLEAPPLET);

                // TODO: send proper APDU
                startTime = System.currentTimeMillis();
                ResponseAPDU output = cardManager.sendAPDU(apdu_PC_Card_2);
                endTime = System.currentTimeMillis();
                response_PC_Card_2 = output.getBytes();
                cardManager.DisconnectFromCard();
            } else {
                System.out.println("Failed to connect to card");
            }

            System.out.println(CardMngr.bytesToHex(response_PC_Card_2));

            if ((response_PC_Card_2[response_PC_Card_2.length - 2] == -112) && (response_PC_Card_2[response_PC_Card_2.length - 1] == 0)) {

                System.out.println("PC_CARD_2 DONE !!");
            }

            
            byte[] finalPass = new byte[response_PC_Card_2.length - 2];
            
            decryptCipherCBC.init(keyEncryptCardPc, Cipher.MODE_DECRYPT);
            decryptCipherCBC.doFinal(response_PC_Card_2, (short) 0, (short) (response_PC_Card_2.length - 2), finalPass, (short) 0);
            
            System.out.println(CardMngr.bytesToHex(finalPass));
            
            byte[] passPhrase = new byte[12];
            
            System.arraycopy(finalPass, 0, passPhrase, 0, passPhrase.length);
            System.arraycopy(finalPass, passPhrase.length, hashBuffer, 0, hashBuffer.length);
            
            //byte[] passPhraseMac = generateHMAC(passPhrase, keyMacPcCard);
            
            if(Util.arrayCompare(generateHMAC(passPhrase, keyMacPcCard), (short)0, hashBuffer, (short)0, (short)hashBuffer.length) == 0) {
                System.out.println("PASS PHRASE RECEIVED CORRECTLY");
            } else {
                System.out.println("WRONG PASS PHRASE RECEIVED");
            }
            
            
            passwordAsString = CardMngr.convertHexToString(CardMngr.bytesToHex2(passPhrase));
            System.out.println(passwordAsString);
            
            //return passwordAsString;
             
        } catch (Exception ex) {
            System.out.println("Exception : " + ex);
        }
        return passwordAsString;
    }

    public static void incrementCounter(byte[] counter) {
        counter[3]++;
        if (counter[3] == 0) {
            counter[2]++;
            if (counter[2] == 0) {
                counter[1]++;
                if (counter[1] == 0) {
                    counter[0]++;
                }
            }
        }
    }
    
    public static byte[] generateHMAC(byte[] buffer, AESKey aKey) {
        //byte[] apdubuf = apdu.getBuffer();
        short dataLen = (short)buffer.length;
        
        //short sigLength = 0;
        //short hotpLen = 4;
        short hashLen = 20;
        //short macLen = 8;
        short keyLen = 32;        
        
        pc_hash = MessageDigest.getInstance(MessageDigest.ALG_SHA, false);
        

        byte[] mac = new byte[20];//JCSystem.makeTransientByteArray((short) 20, JCSystem.CLEAR_ON_RESET);
        
        //byte[] trunc_hash = JCSystem.makeTransientByteArray(hotpLen, JCSystem.CLEAR_ON_RESET);
        
        //byte[] hotp = JCSystem.makeTransientByteArray(hotpLen, JCSystem.CLEAR_ON_RESET);
        
        byte[] key = new byte[keyLen];//JCSystem.makeTransientByteArray(keyLen, JCSystem.CLEAR_ON_RESET);
        
        byte[] challenge = new byte[dataLen];//JCSystem.makeTransientByteArray(dataLen, JCSystem.CLEAR_ON_RESET);

        // byte[] temp16         = JCSystem.makeTransientByteArray((short) 16, JCSystem.CLEAR_ON_RESET);
        byte[] hash = new byte[hashLen];//JCSystem.makeTransientByteArray(hashLen, JCSystem.CLEAR_ON_RESET);
        
        byte[] temp32 = new byte[32];//JCSystem.makeTransientByteArray((short) 32, JCSystem.CLEAR_ON_RESET);

        //byte[] temp36         = JCSystem.makeTransientByteArray((short) 36, JCSystem.CLEAR_ON_RESET);
        byte[] tempDataKey = new byte[dataLen+keyLen];//JCSystem.makeTransientByteArray((short) 64, JCSystem.CLEAR_ON_RESET);
        
        byte[] temp52 = new byte[52];//JCSystem.makeTransientByteArray((short) 52, JCSystem.CLEAR_ON_RESET);
        
        aKey.getKey(key, (short) 0);
        
        Util.arrayCopyNonAtomic(buffer, (short) 0, challenge, (short) 0, dataLen);

        // KEY XOR 0x363636....
        for (short i = 0; i < keyLen; i++) {
            //temp16[i] = (byte)(key[i] ^ 0x36);
            temp32[i] = (byte) (key[i] ^ 0x36);
        }
        Util.arrayCopyNonAtomic(temp32, (short) 0, tempDataKey, (short) 0, keyLen);
        Util.arrayCopyNonAtomic(challenge, (short) 0, tempDataKey, keyLen, dataLen);

        // HASH((KEY XOR 0x363636...) || CHALLENEGE)
        if (pc_hash != null) {
            // m_hash.doFinal(temp32, (short)0, (short)(dataLen + keyLen), hash, (short) 0);
            pc_hash.doFinal(tempDataKey, (short) 0, (short) (dataLen + keyLen), hash, (short) 0);
        }

        //KEY XOR 0x5c5c5c5c....
        for (short i = 0; i < keyLen; i++) {
            //temp16[i] = (byte)(key[i] ^ 0x5c);
            temp32[i] = (byte) (key[i] ^ 0x5c);
        }
        Util.arrayCopyNonAtomic(temp32, (short) 0, temp52, (short) 0, keyLen);
        Util.arrayCopyNonAtomic(hash, (short) 0, temp52, keyLen, hashLen);

        // HASH((KEY XOR 0x5c5c5c5c...) || (HASH((KEY XOR 0x363636...) || CHALLENEGE)))
        if (pc_hash != null) {
            //m_hash.doFinal(temp36, (short)0, (short)(dataLen + keyLen), hash, (short) 0);
            pc_hash.doFinal(temp52, (short) 0, (short) (hashLen + keyLen), hash, (short) 0);
        }
        
        System.out.println(CardMngr.bytesToHex(hash));

        return hash;
        
    }    
    
    public static byte[] hashToKey(byte[] hash) {
        byte[] keyFromHash = new byte[32];
        System.arraycopy(hash, 0, keyFromHash, 0, 20);
        System.arraycopy(hash, 0, keyFromHash, 20, 12);
        return keyFromHash;
    }
}
