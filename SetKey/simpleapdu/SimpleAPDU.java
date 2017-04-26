package simpleapdu;

import applets.SimpleApplet;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Random;
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

    private static final byte USER_CHALLENGE[] = { (byte) 0x41, (byte) 0x34, (byte) 0xff, (byte) 0x31, (byte) 0x02};
         
    private static byte NEW_KEY[] = {(byte) 0x00, (byte) 0xa4, (byte) 0x04, (byte) 0x00, (byte) 0x0b, 
        (byte) 0x73, (byte) 0x69, (byte) 0x6D, (byte) 0x70, (byte) 0x6C,
        (byte) 0x65, (byte) 0x61, (byte) 0x70, (byte) 0x70, (byte) 0x6C, (byte) 0x65, (byte) 0x74};
   
    public static void main(String[] args) {
        try {
            //
            // SIMULATED CARDS
            //
            
            // Prepare simulated card 
            byte[] installData = new byte[10]; // no special install data passed now - can be used to pass initial keys etc.
            cardManager.prepareLocalSimulatorApplet(APPLET_AID, installData, SimpleApplet.class);      
            
            //---------------------- TODO: apdu for INS_SETPIN to set a new PIN-------------------------------------------------
            

	    short additionalDataLenPIN = 4;
            byte apdu_setPIN[] = new byte[CardMngr.HEADER_LENGTH + additionalDataLenPIN];
            apdu_setPIN[CardMngr.OFFSET_CLA] = (byte) 0xB0;// class B0
            apdu_setPIN[CardMngr.OFFSET_INS] = (byte) 0x56;// for INS_SETPIN
            apdu_setPIN[CardMngr.OFFSET_P1] = (byte) 0x01;// randomly pass Admin PIN byte number
            apdu_setPIN[CardMngr.OFFSET_P2] = (byte) 0x37;// randomply pass Admin PIN byte + 5
            apdu_setPIN[CardMngr.OFFSET_LC] = (byte) additionalDataLenPIN;// 4 byte data for PIN
            //int i;


	    // TODO: if additional data are supplied (additionalDataLen != 0), then copy input data here starting from CardMngr.CardMngr.OFFSET_DATA
	    if (additionalDataLenPIN != 0) {
                
                System.arraycopy(NEW_USER_PIN, 0, apdu_setPIN, CardMngr.OFFSET_DATA, additionalDataLenPIN);
		
	    }
            
            // NOTE: we are using sendAPDUSimulator() instead of sendAPDU()
            //byte[] responseSetPIN = cardManager.sendAPDUSimulator(apdu_setPIN); 
            byte[] responseSetPIN = null;
            // TODO: Try same with real card
            if (cardManager.ConnectToCard()) {
                // Select our application on card
                cardManager.sendAPDU(SELECT_SIMPLEAPPLET);
                
                // TODO: send proper APDU
                ResponseAPDU output = cardManager.sendAPDU(apdu_setPIN);
                responseSetPIN = output.getBytes();
                cardManager.DisconnectFromCard();
            } else {
                System.out.println("Failed to connect to card");
            } 

	    System.out.println(CardMngr.bytesToHex(responseSetPIN));
	    
	    if((responseSetPIN[0]==-112)&&(responseSetPIN[1]==0)){
            	
		    System.out.println("ADMIN PIN SET !!");
	    }

            
             //---------------------- TODO: apdu for INS_SETKEY to set a new key K after Verification--------------------------------
            //short additionalDataLenPIN = 4;
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
                    //startTime = System.currentTimeMillis();
                    ResponseAPDU output = cardManager.sendAPDU(apdu_getKEY);
                    //endTime = System.currentTimeMillis();
                    responseGetKEY = output.getBytes();
                    cardManager.DisconnectFromCard();
                } else {
                    System.out.println("Failed to connect to card");
                }

                System.out.println(CardMngr.bytesToHex(responseGetKEY));

                if ((responseGetKEY[responseGetKEY.length - 2] == -112) && (responseGetKEY[responseGetKEY.length - 1] == 0)) {

                    System.out.println("RANDOM AES KEY SET !!");
                }
                //System.out.println("TOTAL TIME FOR KEY SETTING = " + (endTime - startTime) + " msecs");
                
                byte[] keyToWrite = new byte[keyLength];
                byte[] countToWrite = new byte[countLen];
                System.arraycopy(responseGetKEY, 0, keyToWrite, 0, keyLength);
                System.arraycopy(responseGetKEY, keyLength, countToWrite, 0, countLen);
                
                System.out.println(CardMngr.bytesToHex(keyToWrite));
                System.out.println(CardMngr.bytesToHex(countToWrite));

                File keyFile = new File("D:\\Masaryk\\SEM2\\PV204 Security Technologies\\PC_Application.v1.0\\_source\\FileEncoderApplication_SymSec1\\key.bin");
                File countFile = new File("D:\\Masaryk\\SEM2\\PV204 Security Technologies\\PC_Application.v1.0\\_source\\FileEncoderApplication_SymSec1\\count.bin");
                try {
                    try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(keyFile))) {
                        outputStream.write(keyToWrite);
                        outputStream.close();
                    }
                    try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(countFile))) {
                        outputStream.write(countToWrite);
                        outputStream.close();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                System.out.println("PIN VERIFICATION FAILED !!");
            }

     
            
            
        } catch (Exception ex) {
            System.out.println("Exception : " + ex);
        }
    }
}
