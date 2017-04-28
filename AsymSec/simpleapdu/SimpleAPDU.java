package com.hotmail.frojasg1.applications.fileencoderapplication.simpleapdu;

//import com.hotmail.frojasg1.applications.fileencoderapplication.applets.SimpleApplet;
//import com.sun.javafx.logging.PulseLogger;
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

    //private static Key randPCPriv = null;//(RSAPrivateKey) KeyBuilder.buildKey(KeyBuilder.TYPE_RSA_PRIVATE, KeyBuilder.LENGTH_RSA_2, false);
    //private static Key randPCPub = null;
    //private static Cipher randPCCipher = null;
    //KeyPair randPCKeyPair = null;
    private static MessageDigest hash = null;
    
    
    public static void main(String[] args) {

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
            
            //-----------------------------------------------------------------------------------------------------------
            //TODO: Read key.bin
            File keyFile = new File("key.bin");
            byte[] keyArray = new byte[(int) keyFile.length()];

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

            AESKey aesKey = (AESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_AES, KeyBuilder.LENGTH_AES_256, false);

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
            apdu_PC_Card_1[CardMngr.OFFSET_INS] = (byte) 0x72;// for INS_PC_CARD_1
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

            byte[] cardPubKeyMod = new byte[128];
            byte[] cardPubKeyExp = new byte[3];
            byte[] counterFromCard = new byte[4];
            byte[] tempBuff = new byte[response_PC_Card_1.length - 2];

            decryptCipherCBC.doFinal(response_PC_Card_1, (short) 0, (short) (response_PC_Card_1.length - 2), tempBuff, (short) 0);

            System.out.println(CardMngr.bytesToHex(tempBuff));
            System.arraycopy(tempBuff, 0, cardPubKeyMod, 0, 128);
            System.arraycopy(tempBuff, 128, cardPubKeyExp, 0, 3);
            System.arraycopy(tempBuff, 128 + 3, counterFromCard, 0, 4);

            System.out.println("Card Public Key Mod : "+CardMngr.bytesToHex(cardPubKeyMod));
            System.out.println("Card Public Key Exp : " + CardMngr.bytesToHex(cardPubKeyExp));
            System.out.println("Counter From Card   : " + CardMngr.bytesToHex(counterFromCard));

            byte[] countCheck = new byte[countArray.length];

            System.arraycopy(countArray, 0, countCheck, 0, countArray.length);

            incrementCounter(countCheck);

            if (Util.arrayCompare(countCheck, (short) 0, counterFromCard, (short) 0, (short) countArray.length) == 0) {
                System.out.println("COUNTER VERIFIED");
                incrementCounter(countArray);
            } else {
                System.out.println("COUNTER VERIFICATION FAILED");
                System.exit(1);
            }
            
            try {
                    
                    OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(countFile));
                    outputStream.write(countArray);
                    outputStream.close();
                    
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            System.out.println("TOTAL TIME FOR PC_CARD_1 = " + (endTime - startTime) + " msecs");
            
            RSAPublicKey cardPubKey = null;
            //KeyPair randCardKeyPair = null;
            //randCardKeyPair = new KeyPair(KeyPair.ALG_RSA_CRT, KeyBuilder.LENGTH_RSA_1024            Cipher cardRSACipher = Cipher.getInstance(Cipher.ALG_RSA_PKCS1, false);

            Cipher cardRSACipher = Cipher.getInstance(Cipher.ALG_RSA_PKCS1, false);

            cardPubKey = (RSAPublicKey) KeyBuilder.buildKey(KeyBuilder.TYPE_RSA_PUBLIC, KeyBuilder.LENGTH_RSA_1024, true);
            cardPubKey.setExponent(cardPubKeyExp, (short) 0, (short) cardPubKeyExp.length);
            System.out.println("Card Public Key Exp SET !!");
            cardPubKey.setModulus(cardPubKeyMod, (short) 0, (short) cardPubKeyMod.length);
            System.out.println("Card Public Key Mod SET !!");
            
            byte[] checkCardPubKeyMod = new byte[128];
            cardPubKey.getModulus(checkCardPubKeyMod, (short) 0);
            System.out.println("Card Public Key Mod : " + CardMngr.bytesToHex(checkCardPubKeyMod));
            
            
            cardRSACipher.init(cardPubKey, Cipher.MODE_ENCRYPT);
            //cardRSACipher.doFinal(keyArray, additionalDataLen_PC_Card_1, additionalDataLen_PC_Card_1, keyArray, additionalDataLen_PC_Card_1)
           
            //------------------------ PC_CARD_2 START----------------------------
            RSAPrivateKey randPCPriv = null;//
            randPCPriv = (RSAPrivateKey) KeyBuilder.buildKey(KeyBuilder.TYPE_RSA_PRIVATE, KeyBuilder.LENGTH_RSA_1024, true);
            RSAPublicKey randPCPub = null;
            randPCPub = (RSAPublicKey) KeyBuilder.buildKey(KeyBuilder.TYPE_RSA_PUBLIC, KeyBuilder.LENGTH_RSA_1024, true);
            //Cipher randPCCipher = null;
            KeyPair randPCKeyPair = null;
            randPCKeyPair = new KeyPair(KeyPair.ALG_RSA_CRT, KeyBuilder.LENGTH_RSA_1024);
            randPCKeyPair.genKeyPair();
            randPCPriv = (RSAPrivateKey)randPCKeyPair.getPrivate();
            randPCPub = (RSAPublicKey)randPCKeyPair.getPublic();
            //randPCCipher = Cipher.getInstance(Cipher.ALG_RSA_PKCS1, false);
            //b.setModulus(P, (short) 0, maxLength);
            //randPCCipher.init(randPCPriv, Cipher.MODE_DECRYPT);
            //randPCCipher.init(randPCPub, Cipher.MODE_ENCRYPT);
            byte[] pcPubKeyMod129 = null;
            byte[] pcPubKeyMod = null;
            byte[] pcPubKeyExp = null;
            short randPCPubKeyModSize = 0;
            short randPCPubKeyExpSize = 3;
            short len = (short) 0;

            try {

                //randPCPrivKeySize = (short) (randPCPriv.getSize() / (short) 8);
                randPCPubKeyModSize = (short) (randPCPub.getSize() / (short) 8);
                System.out.println("randPCPubKeyModSize : "+ (randPCPubKeyModSize));

                pcPubKeyMod129 = new byte[randPCPubKeyModSize+1];
                pcPubKeyMod = new byte[randPCPubKeyModSize];
                len = ((RSAPublicKey) randPCPub).getModulus(pcPubKeyMod129, (short) 0);
                System.out.println("len : "+ (len));
                System.arraycopy(pcPubKeyMod129, 1, pcPubKeyMod, 0, randPCPubKeyModSize);
                //randPCPub.getModulus(pcPubKeyMod, (short) 0);
                pcPubKeyExp = new byte[randPCPubKeyExpSize];
                ((RSAPublicKey) randPCPub).getExponent(pcPubKeyExp, (short) 0);
                //randPCPub.getExponent(pcPubKeyExp, (short) 0);

            } catch (CryptoException e) {
                short reason = e.getReason();
                ISOException.throwIt(reason);
            }

            System.out.println("PC Public Key Mod : "+CardMngr.bytesToHex(pcPubKeyMod));
            System.out.println("PC Public Key Exp : "+CardMngr.bytesToHex(pcPubKeyExp));
            
            byte[] pcPubKey = new byte[pcPubKeyMod.length + pcPubKeyExp.length];

            System.arraycopy(pcPubKeyMod, 0, pcPubKey, 0, pcPubKeyMod.length);
            System.arraycopy(pcPubKeyExp, 0, pcPubKey, pcPubKeyMod.length, pcPubKeyExp.length);
            
            System.out.println("PC Public Key : "+ CardMngr.bytesToHex(pcPubKey));
            
            //cardRSACipher.doFinal(pcPubKey, randPCPubKeySize, randPCPubKeySize, keyArray, randPCPubKeySize)
            byte[] data_PC_Card_2 = new byte[pcPubKey.length + 4 + 9];

            //encryptCipherCBC.doFinal(pcPubKey, (short) 0, (short) (pcPubKeyMod.length + pcPubKeyExp.length + 13), data_PC_Card_2, (short) 0);
            //cardRSACipher.doFinal(pcPubKey, (short) 0, (short) (128), data_PC_Card_2, (short) 0);
            System.arraycopy(pcPubKey,0, data_PC_Card_2, 0, pcPubKey.length);
            //System.arraycopy(pcPubKey,0, data_PC_Card_2, 0, pcPubKey.length);
            System.arraycopy(countArray,0, data_PC_Card_2, pcPubKey.length, countArray.length);
            System.out.println("data_PC_Card_2 : " + CardMngr.bytesToHex(data_PC_Card_2));
            System.out.println("additionalDataLen_PC_Card_2 : " + data_PC_Card_2.length);
            
            short additionalDataLen_PC_Card_2 = (short) (data_PC_Card_2.length); //128 pub mod + 3 pub exp + 4 count + 9 pad
            //System.out.println()
            byte[] apdu_PC_Card_2 = new byte[CardMngr.HEADER_LENGTH + additionalDataLen_PC_Card_2];
            apdu_PC_Card_2[CardMngr.OFFSET_CLA] = (byte) 0xB0;// class B0
            apdu_PC_Card_2[CardMngr.OFFSET_INS] = (byte) 0x73;// for INS_PC_CARD_2
            apdu_PC_Card_2[CardMngr.OFFSET_P1] = (byte) 0x00;
            apdu_PC_Card_2[CardMngr.OFFSET_P2] = (byte) 0x00;
            apdu_PC_Card_2[CardMngr.OFFSET_LC] = (byte) additionalDataLen_PC_Card_2;// PUB Key of PC           

            if (additionalDataLen_PC_Card_2 != 0) {

                encryptCipherCBC.doFinal(data_PC_Card_2, (short) 0, additionalDataLen_PC_Card_2, apdu_PC_Card_2, CardMngr.OFFSET_DATA);
                //cardRSACipher.doFinal(data_PC_Card_2, (short) 0, additionalDataLen_PC_Card_2, apdu_PC_Card_2, CardMngr.OFFSET_DATA);
            }
            System.out.println("apdu_PC_Card_2 : " + CardMngr.bytesToHex(apdu_PC_Card_2));
            
            
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
            
                
            decryptCipherCBC.doFinal(response_PC_Card_2, (short) 0, (short) (response_PC_Card_2.length - 2), finalPass, (short) 0);
            
            //System.out.println("final Pass: "+CardMngr.bytesToHex(finalPass));
            
            byte[] passPhrase = new byte[12];
            byte[] hashBuffer = new byte[20];
            System.arraycopy(finalPass, 0, passPhrase, 0, passPhrase.length);
            System.arraycopy(finalPass, passPhrase.length, hashBuffer, 0, hashBuffer.length);
            
            System.out.println("pass Phrase : " + CardMngr.bytesToHex(passPhrase));
            System.out.println("pass Phrase Hash: " + CardMngr.bytesToHex(hashBuffer));
            
            //byte[] passPhraseMac = generateHMAC(passPhrase, keyMacPcCard);
            
            byte[] passPhraseHash = new byte[20];
            
            hash = MessageDigest.getInstance(MessageDigest.ALG_SHA, false);
            hash.doFinal(passPhrase, (short) 0, (short) passPhrase.length, passPhraseHash, (short) 0);
            
            //System.out.println("pass Phrase Hash in PC: "+CardMngr.bytesToHex(passPhraseHash));
            
            if(Util.arrayCompare(passPhraseHash, (short)0, hashBuffer, (short)0, (short)hashBuffer.length) == 0) {
                System.out.println("PASS PHRASE RECEIVED CORRECTLY");
            } else {
                System.out.println("WRONG PASS PHRASE RECEIVED");
            }
            
            //byte[] passPhrase = new byte[response_PC_Card_1.length - 2];

           // decryptCipherCBC.doFinal(response_PC_Card_2, (short) 0, (short) (response_PC_Card_2.length - 2), passPhrase, (short) 0);
           
        } catch (Exception ex) {
            System.out.println("Exception : " + ex);
        }
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
}
