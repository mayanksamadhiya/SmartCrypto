package simpleapdu;

import applets.SimpleApplet;
import java.security.SecureRandom;
import java.util.Random;
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
         (byte) 0x12, (byte) 0x13, (byte) 0x15, (byte) 0x15, (byte) 0x16 };

    
    public static void main(String[] args) {
        try {
            //
            // SIMULATED CARDS
            //
            
            // Prepare simulated card 
            byte[] installData = new byte[10]; // no special install data passed now - can be used to pass initial keys etc.
            cardManager.prepareLocalSimulatorApplet(APPLET_AID, installData, SimpleApplet.class);      
            long startTime = 0;
            //long elapsedTime = 0;
            long endTime = 0;
            
            //---------------------- TODO: apdu for INS_SETKEY to set a new key K after Verification--------------------------------
            short additionalDataLenPIN = 4;
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

	    if((responseVerifyPIN[0]==-112)&&(responseVerifyPIN[1]==0)){
            	
		    System.out.println("PIN VERIFIED !!");
            
                    //------------------------GENERATE RANDOM KEY ON CARD------------------------------------------
                    short keyLength = 32;
                    byte apdu_getKEY[] = new byte[CardMngr.HEADER_LENGTH ];
                    apdu_getKEY[CardMngr.OFFSET_CLA] = (byte) 0xB0;
                    apdu_getKEY[CardMngr.OFFSET_INS] = (byte) 0x52;// Set Key
                    apdu_getKEY[CardMngr.OFFSET_P1] = (byte) keyLength;
                    apdu_getKEY[CardMngr.OFFSET_P2] = (byte) 0x00;
                    apdu_getKEY[CardMngr.OFFSET_LC] = (byte) 0x00;

                    
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
                        responseGetKEY=output.getBytes();
                        cardManager.DisconnectFromCard();
                    } else {
                        System.out.println("Failed to connect to card");
                    }

                    System.out.println(CardMngr.bytesToHex(responseGetKEY));

                    if((responseGetKEY[0]==-112)&&(responseGetKEY[1]==0)){

                            System.out.println("RANDOM AES KEY SET !!");
                    }
                    System.out.println("TOTAL TIME FOR KEY SETTING = " + (endTime - startTime) + " msecs");
                   
                
            }
	   else{
		   System.out.println("PIN VERIFICATION FAILED !!"); 
	   }
             

            
        } catch (Exception ex) {
            System.out.println("Exception : " + ex);
        }
    }
}
