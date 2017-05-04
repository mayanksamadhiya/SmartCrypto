package applets;

import javacard.framework.*;
import javacard.security.*;
import javacardx.crypto.*;
import simpleapdu.CardMngr;

//--------------------------------------------------------------------MAIN CLASS----------------------------------------------------
public class SimpleApplet extends javacard.framework.Applet {

    // MAIN INSTRUCTION CLASS
    final static byte CLA_SIMPLEAPPLET = (byte) 0xB0;

    // INSTRUCTIONS
    final static byte INS_ENCRYPT = (byte) 0x50;
    final static byte INS_DECRYPT = (byte) 0x51;
    final static byte INS_SETKEY = (byte) 0x52;
    final static byte INS_HASH = (byte) 0x53;
    final static byte INS_RANDOM = (byte) 0x54;
    final static byte INS_VERIFYPIN = (byte) 0x55;
    final static byte INS_SETPIN = (byte) 0x56;
    final static byte INS_RETURNDATA = (byte) 0x57;
    final static byte INS_SIGNDATA = (byte) 0x58;
    final static byte INS_GETAPDUBUFF = (byte) 0x59;
    final static byte INS_RETURNHOPT = (byte) 0x60;//MILAN : Instruction added for returning HOTP
    final static byte INS_ENCDECCBC = (byte) 0x70;// MILAN : For encrypt/decrypt CBC
    final static byte INS_SETPASS = (byte) 0x71;
    final static byte INS_ASYMSEC_PC_CARD_1 = (byte) 0x72;
    final static byte INS_ASYMSEC_PC_CARD_2 = (byte) 0x73;

    final static byte INS_SYMSEC_PC_CARD_1 = (byte) 0x76;
    final static byte INS_SYMSEC_PC_CARD_2 = (byte) 0x77;

    final static short ARRAY_LENGTH = (short) 0xff;
    final static short ARRAY_LENGTH_SHORT = (short) 0x80;// MILAN : Added for AES 128
    final static byte AES_BLOCK_LENGTH = (short) 0x16;

    final static short SW_BAD_TEST_DATA_LEN = (short) 0x6680;
    final static short SW_KEY_LENGTH_BAD = (short) 0x6715;
    final static short SW_CIPHER_DATA_LENGTH_BAD = (short) 0x6710;
    final static short SW_OBJECT_NOT_AVAILABLE = (short) 0x6711;
    final static short SW_BAD_PIN = (short) 0x6900;
    final static short SW_BAD_PASSWORD = (short) 0x6901;

    final static short SW_Exception = (short) 0xff01;
    final static short SW_ArrayIndexOutOfBoundsException = (short) 0xff02;
    final static short SW_ArithmeticException = (short) 0xff03;
    final static short SW_ArrayStoreException = (short) 0xff04;
    final static short SW_NullPointerException = (short) 0xff05;
    final static short SW_NegativeArraySizeException = (short) 0xff06;
    final static short SW_CryptoException_prefix = (short) 0xf100;
    final static short SW_SystemException_prefix = (short) 0xf200;
    final static short SW_PINException_prefix = (short) 0xf300;
    final static short SW_TransactionException_prefix = (short) 0xf400;
    final static short SW_CardRuntimeException_prefix = (short) 0xf500;

    private AESKey m_aesKey = null;

    private Cipher m_encryptCipherCBC = null;// MILAN
    private Cipher m_decryptCipherCBC = null;// MILAN

    private RandomData m_secureRandom = null;
    private MessageDigest m_hash = null;
    private OwnerPIN m_pin = null;

    private short m_apduLogOffset = (short) 0;
    // TEMPORARRY ARRAY IN RAM
    private byte m_ramArray[] = null;
    // PERSISTENT ARRAY IN EEPROM
    private byte m_dataArray[] = null;

    // Random data
    private byte m_counter[] = null;

    //User password
    private byte m_password[] = {(byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09, (byte) 0x0a, (byte) 0x0b, (byte) 0x0c};

    //pin length
    private byte m_pin_length = (byte) 4;

    private Key cardPriv = null;//(RSAPrivateKey) KeyBuilder.buildKey(KeyBuilder.TYPE_RSA_PRIVATE, KeyBuilder.LENGTH_RSA_2, false);
    private Key cardPub = null;//(RSAPublicKey) KeyBuilder.buildKey(KeyBuilder.TYPE_RSA_PUBLIC, KeyBuilder.LENGTH_RSA_2048, false);
    private KeyPair cardKeyPair = null;
    private Cipher cardCipher = null;
    private byte[] cardRandom = null;

    private Key pcPriv = null;//(RSAPrivateKey) KeyBuilder.buildKey(KeyBuilder.TYPE_RSA_PRIVATE, KeyBuilder.LENGTH_RSA_2, false);
    private Key pcPub = null;//(RSAPublicKey) KeyBuilder.buildKey(KeyBuilder.TYPE_RSA_PUBLIC, KeyBuilder.LENGTH_RSA_2048, false);
    private KeyPair pcKeyPair = null;
    private Cipher pcCipher = null;

    private AESKey m_keyEncryptCard = null;
    private AESKey m_keyMacPcCard = null;
    private AESKey m_keyEncryptCardPc = null;

    //private byte[] preHOTP = null;//MILAN : container to mix the user challenge and card PIN
    private byte ADMIN_PIN[] = {(byte) 0x31, (byte) 0x32, (byte) 0x33, (byte) 0x34};// admin PIN set to 1234

    public static final short randLength = 44;
    //private byte[] g = new byte[maxLength];
    //G[maxLength - 1] = (byte) 0x02;

//--------------------------------------------------------------CONSTRUCTORS----------------------------------
    /**
     * SimpleApplet default constructor Only this class's install method should
     * create the applet object.
     */
    protected SimpleApplet(byte[] buffer, short offset, byte length) {
        // data offset is used for application specific parameter.
        // initialization with default offset (AID offset).
        short dataOffset = offset;
        boolean isOP2 = false;

        if (length > 9) {
            // Install parameter detail. Compliant with OP 2.0.1.

            // | size | content
            // |------|---------------------------
            // |  1   | [AID_Length]
            // | 5-16 | [AID_Bytes]
            // |  1   | [Privilege_Length]
            // | 1-n  | [Privilege_Bytes] (normally 1Byte)
            // |  1   | [Application_Proprietary_Length]
            // | 0-m  | [Application_Proprietary_Bytes]
            // shift to privilege offset
            dataOffset += (short) (1 + buffer[offset]);
            // finally shift to Application specific offset
            dataOffset += (short) (1 + buffer[dataOffset]);

            // go to proprietary data
            dataOffset++;

            m_dataArray = new byte[ARRAY_LENGTH];
            Util.arrayFillNonAtomic(m_dataArray, (short) 0, ARRAY_LENGTH, (byte) 0);

            // CREATE RANDOM DATA GENERATORS
            m_secureRandom = RandomData.getInstance(RandomData.ALG_SECURE_RANDOM);

            // TEMPORARY BUFFER USED FOR FAST OPERATION WITH MEMORY LOCATED IN RAM
            m_ramArray = JCSystem.makeTransientByteArray((short) 260, JCSystem.CLEAR_ON_RESET);

            // MILAN : CREATE DES OBJECT
            //m_desKey = (DESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_DES, KeyBuilder.LENGTH_DES3_2KEY, false);
            // SET KEY VALUE
            //m_desKey.setKey(m_dataArray, (short) 0);
            // CREATE AES KEY OBJECT
            m_aesKey = (AESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_AES, KeyBuilder.LENGTH_AES_256, false);

            //m_hmacKey = (HMACKey)KeyBuilder.buildKey(KeyBuilder.TYPE_AES, KeyBuilder.LENGTH_HMAC_SHA_1_BLOCK_64, false); 
            // MILAN : CREATE OBJECTS FOR CBC CIPHERING
            m_encryptCipherCBC = Cipher.getInstance(Cipher.ALG_AES_BLOCK_128_CBC_NOPAD, false);
            m_decryptCipherCBC = Cipher.getInstance(Cipher.ALG_AES_BLOCK_128_CBC_NOPAD, false);

            // SET KEY VALUE
            m_aesKey.setKey(m_dataArray, (short) 0);

            // MILAN : INIT CBC CIPHERS WITH NEW KEY
            m_pin = new OwnerPIN((byte) 3, m_pin_length); // 5 tries, 4 digits in pin
            m_pin.update(m_dataArray, (byte) 0, (byte) 4); // set initial random pin

            // INIT HASH ENGINE
            m_hash = MessageDigest.getInstance(MessageDigest.ALG_SHA, false);
            //m_sessionMAC = Signature.getInstance(Signature.ALG_AES_MAC_128_NOPAD, false); // MILAN
            // update flag
            isOP2 = true;
            //Initialize g

            cardKeyPair = new KeyPair(KeyPair.ALG_RSA_CRT, KeyBuilder.LENGTH_RSA_512);
            //cardKeyPair.genKeyPair();
            //cardPriv = cardKeyPair.getPrivate();
            //cardPub = cardKeyPair.getPublic();
            cardCipher = Cipher.getInstance(Cipher.ALG_RSA_PKCS1, false);
            //cardCipher.init(cardPriv, Cipher.MODE_DECRYPT);
            //cardCipher.init(cardPub, Cipher.MODE_ENCRYPT);

            pcKeyPair = new KeyPair(KeyPair.ALG_RSA_CRT, KeyBuilder.LENGTH_RSA_512);
            pcKeyPair.genKeyPair();
            pcPriv = pcKeyPair.getPrivate();
            pcPub = pcKeyPair.getPublic();
            //pcPub = KeyBuilder.buildKey(KeyBuilder.TYPE_RSA_PUBLIC, KeyBuilder.LENGTH_RSA_512, true);
            pcCipher = Cipher.getInstance(Cipher.ALG_RSA_PKCS1, false);
            pcCipher.init(pcPub, Cipher.MODE_ENCRYPT);

        } else {
            // <IF NECESSARY, USE COMMENTS TO CHECK LENGTH >
            // if(length != <PUT YOUR PARAMETERS LENGTH> )
            //     ISOException.throwIt((short)(ISO7816.SW_WRONG_LENGTH + length));
        }

        // <PUT YOUR CREATION ACTION HERE>
        // register this instance
        register();
    }

//-----------------------------------INSTALL APPLET-----------------------------------------------------------
    /**
     * Method installing the applet.
     *
     * @param bArray the array constaining installation parameters
     * @param bOffset the starting offset in bArray
     * @param bLength the length in bytes of the data parameter in bArray
     */
    public static void install(byte[] bArray, short bOffset, byte bLength) throws ISOException {
        // applet  instance creation 
        new SimpleApplet(bArray, bOffset, bLength);
    }

//-----------------------------------------------SELECT--------------------------------------------------------
    /**
     * Select method returns true if applet selection is supported.
     *
     * @return boolean status of selection.
     */
    public boolean select() {
        // <PUT YOUR SELECTION ACTION HERE>

        return true;
    }

//----------------------------------------------------DESELECT------------------------------------------------
    /**
     * Deselect method called by the system in the deselection process.
     */
    public void deselect() {

        // <PUT YOUR DESELECTION ACTION HERE>
        return;
    }

//----------------------------------------------------PROCESS----------------------------------------------------
    /**
     * Method processing an incoming APDU.
     *
     * @see APDU
     * @param apdu the incoming APDU
     * @exception ISOException with the response bytes defined by ISO 7816-4
     */
    public void process(APDU apdu) throws ISOException {
        // get the APDU buffer
        byte[] apduBuffer = apdu.getBuffer();
        //short dataLen = apdu.setIncomingAndReceive();
        //Util.arrayCopyNonAtomic(apduBuffer, (short) 0, m_dataArray, m_apduLogOffset, (short) (5 + dataLen));
        //m_apduLogOffset = (short) (m_apduLogOffset + 5 + dataLen);

        // ignore the applet select command dispached to the process
        if (selectingApplet()) {
            return;
        }

        try {

            // APDU instruction parser
            if (apduBuffer[ISO7816.OFFSET_CLA] == CLA_SIMPLEAPPLET) {
                switch (apduBuffer[ISO7816.OFFSET_INS]) {
                    case INS_SETKEY:
                        SetKey(apdu);
                        break;
                    case INS_HASH:
                        Hash(apdu);
                        break;

                    case INS_VERIFYPIN:
                        VerifyPIN(apdu);
                        break;
                    case INS_SETPIN:
                        SetPIN(apdu);
                        break;

                    case INS_GETAPDUBUFF:
                        GetAPDUBuff(apdu);
                        break;

                    case INS_SETPASS:
                        SetPassword(apdu);
                        break;
                    case INS_ASYMSEC_PC_CARD_1:
                        AsymSec_PC_Card_1(apdu);
                        break;
                    case INS_ASYMSEC_PC_CARD_2:
                        AsymSec_PC_Card_2(apdu);
                        break;
                    default:
                        // The INS code is not supported by the dispatcher
                        ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
                        break;

                }
            } else {
                ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
            }

            // Capture all reasonable exceptions and change into readable ones (instead of 0x6f00) 
        } catch (ISOException e) {
            throw e; // Our exception from code, just re-emit
        } catch (ArrayIndexOutOfBoundsException e) {
            ISOException.throwIt(SW_ArrayIndexOutOfBoundsException);
        } catch (ArithmeticException e) {
            ISOException.throwIt(SW_ArithmeticException);
        } catch (ArrayStoreException e) {
            ISOException.throwIt(SW_ArrayStoreException);
        } catch (NullPointerException e) {
            ISOException.throwIt(SW_NullPointerException);
        } catch (NegativeArraySizeException e) {
            ISOException.throwIt(SW_NegativeArraySizeException);
        } catch (CryptoException e) {
            ISOException.throwIt((short) (SW_CryptoException_prefix | e.getReason()));
        } catch (SystemException e) {
            ISOException.throwIt((short) (SW_SystemException_prefix | e.getReason()));
        } catch (PINException e) {
            ISOException.throwIt((short) (SW_PINException_prefix | e.getReason()));
        } catch (TransactionException e) {
            ISOException.throwIt((short) (SW_TransactionException_prefix | e.getReason()));
        } catch (CardRuntimeException e) {
            ISOException.throwIt((short) (SW_CardRuntimeException_prefix | e.getReason()));
        } catch (Exception e) {
            ISOException.throwIt(SW_Exception);
        }

    }

    void AsymSec_PC_Card_1(APDU apdu) {
        byte[] apdubuf = apdu.getBuffer();
        short dataLen = apdu.setIncomingAndReceive();

        if ((apdubuf[ISO7816.OFFSET_P2] != 0) || (apdubuf[ISO7816.OFFSET_P1] != 0)) {
            ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2);
        }
        m_decryptCipherCBC.doFinal(apdubuf, ISO7816.OFFSET_CDATA, dataLen, m_ramArray, (short) 0);

        if (m_pin.check(m_ramArray, (short) 0, m_pin_length) == false) {
            ISOException.throwIt(SW_BAD_PASSWORD);
        }

        if (Util.arrayCompare(m_ramArray, m_pin_length, m_counter, (short) 0, (short) m_counter.length) == 0) {
            incrementCounter(m_counter);
        } else {
            ISOException.throwIt(SW_BAD_PASSWORD);
        }

        //cardKeyPair = new KeyPair(KeyPair.ALG_RSA_CRT, KeyBuilder.LENGTH_RSA_512);
        cardKeyPair.genKeyPair();
        cardPriv = cardKeyPair.getPrivate();
        cardPub = cardKeyPair.getPublic();
        //cardCipher = Cipher.getInstance(Cipher.ALG_RSA_PKCS1, false);
        //cardCipher.init(cardPriv, Cipher.MODE_DECRYPT);
        //cardCipher.init(cardPub, Cipher.MODE_ENCRYPT);

        byte[] cardPubKeyMod = null;
        byte[] cardPubKeyExp = null;
        short cardPubKeyModSize = 65;
        short cardPubKeyExpSize = 3;
        //short cardPrivKeySize = 0;
        try {

            //cardPrivKeySize = (short)(cardPriv.getSize()/(short) 8);
            //cardPubKeyModSize = (short) (cardPub.getSize()/(short) 8);

            cardPubKeyMod = JCSystem.makeTransientByteArray(cardPubKeyModSize, JCSystem.CLEAR_ON_DESELECT);
            ((RSAPublicKey) cardPub).getModulus(cardPubKeyMod, (short) 0);
            cardPubKeyExp = JCSystem.makeTransientByteArray(cardPubKeyExpSize, JCSystem.CLEAR_ON_DESELECT);
            ((RSAPublicKey) cardPub).getExponent(cardPubKeyExp, (short) 0);

        } catch (CryptoException e) {
            short reason = e.getReason();
            ISOException.throwIt(reason);
        }

        Util.arrayCopyNonAtomic(cardPubKeyMod, (short) 0, m_ramArray, (short) 0, (short) (cardPubKeyMod.length));
        Util.arrayCopyNonAtomic(cardPubKeyExp, (short) 0, m_ramArray, (short) (cardPubKeyMod.length), (short) cardPubKeyExp.length);
        Util.arrayCopyNonAtomic(m_counter, (short) 0, m_ramArray, (short) ((cardPubKeyMod.length) + cardPubKeyExp.length), (short) m_counter.length);

        m_encryptCipherCBC.doFinal(m_ramArray, (short) 0, (short) (m_counter.length + (cardPubKeyMod.length) + cardPubKeyExp.length + 8), apdubuf, ISO7816.OFFSET_CDATA);

        //Util.arrayCopyNonAtomic(m_ramArray, (short) 0, apdubuf, ISO7816.OFFSET_CDATA, (short)(m_counter.length + cardPubKey.length));
        //apdu.setOutgoingAndSend(ISO7816.OFFSET_CDATA, (short)(maxLength+m_counter.length));
        //Util.arrayCopyNonAtomic(m_ramArray, (short) 0, apdubuf, ISO7816.OFFSET_CDATA, (short) (16));
        apdu.setOutgoingAndSend(ISO7816.OFFSET_CDATA, (short) (m_counter.length + (cardPubKeyMod.length) + cardPubKeyExp.length + 8));
    }

    void AsymSec_PC_Card_2(APDU apdu) {
        byte[] apdubuf = apdu.getBuffer();
        short dataLen = apdu.setIncomingAndReceive();

        if ((apdubuf[ISO7816.OFFSET_P2] != 0) || (apdubuf[ISO7816.OFFSET_P1] != 0)) {
            ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2);
        }
        
                       
        //m_decryptCipherCBC.doFinal(apdubuf, ISO7816.OFFSET_CDATA, dataLen, m_ramArray, (short) 0);
        
        cardCipher.init(cardPriv, Cipher.MODE_DECRYPT);
        cardCipher.doFinal(apdubuf, ISO7816.OFFSET_CDATA, dataLen, m_ramArray, (short) 0);
        
        
        short pcPubKeyModSize = (short) 65;
        //short pcPubKeyMod129Size = (short) 129;
        short pcPubKeyExpSize = (short) 3;
        short pcPubKeySize = (short) (pcPubKeyModSize + pcPubKeyExpSize);
        byte[] pcPubKey = null;

        pcPubKey = JCSystem.makeTransientByteArray(pcPubKeySize, JCSystem.CLEAR_ON_DESELECT);
        Util.arrayCopy(m_ramArray, (short) 0, pcPubKey, (short) 0, (short) pcPubKey.length);

        //MILAN : Create the PC public key
        byte[] pcPubKeyMod = JCSystem.makeTransientByteArray(pcPubKeyModSize, JCSystem.CLEAR_ON_DESELECT);
        Util.arrayCopy(pcPubKey, (short) 0, pcPubKeyMod, (short) 0, (short) pcPubKeyMod.length);
        
        
        byte[] pcPubKeyExp = JCSystem.makeTransientByteArray(pcPubKeyExpSize, JCSystem.CLEAR_ON_DESELECT);
        Util.arrayCopy(pcPubKey, (short) pcPubKeyMod.length, pcPubKeyExp, (short) 0, (short) pcPubKeyExp.length);
        
        

        try {

            //pcPubKeyMod = JCSystem.makeTransientByteArray(cardPubKeyModSize, JCSystem.CLEAR_ON_DESELECT);
            ((RSAPublicKey) pcPub).setModulus(pcPubKeyMod, (short) 0, pcPubKeyModSize);
            //cardPubKeyExp = JCSystem.makeTransientByteArray(cardPubKeyExpSize, JCSystem.CLEAR_ON_DESELECT);
            ((RSAPublicKey) pcPub).setExponent(pcPubKeyExp, (short) 0, pcPubKeyExpSize);

        } catch (CryptoException e) {
            short reason = e.getReason();
            ISOException.throwIt(reason);
        }

        byte[] hashBuffer = JCSystem.makeTransientByteArray((short) 20, JCSystem.CLEAR_ON_RESET);
        m_hash.doFinal(m_password, (short) 0, (short) m_password.length, hashBuffer, (short) 0);
        //hashBuffer = JCSystem.makeTransientByteArray((short) 20, JCSystem.CLEAR_ON_RESET);
        byte[] finalPass = JCSystem.makeTransientByteArray((short) 32, JCSystem.CLEAR_ON_RESET);//new byte[32];
        Util.arrayCopyNonAtomic(m_password, (short) 0, finalPass, (short) 0, (short) m_password.length);
        Util.arrayCopyNonAtomic(hashBuffer, (short) 0, finalPass, (short) m_password.length, (short) hashBuffer.length);

        pcCipher.init(pcPub, Cipher.MODE_ENCRYPT);
        m_encryptCipherCBC.doFinal(finalPass, (short)0, (short)finalPass.length, apdubuf, ISO7816.OFFSET_CDATA);
        //pcCipher.doFinal(finalPass, (short) 0, (short) finalPass.length, apdubuf, ISO7816.OFFSET_CDATA);

        //Util.arrayCopyNonAtomic(keyFromHash, (short) 0, apdubuf, ISO7816.OFFSET_CDATA, (short) 20);
        //apdu.setOutgoingAndSend(ISO7816.OFFSET_CDATA, (short) finalPass.length);
        apdu.setOutgoingAndSend(ISO7816.OFFSET_CDATA, (short) 128);

    }

    void incrementCounter(byte[] counter) {
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

//---------------------------------------------------------SETKEY---------------------------------------
    // SET ENCRYPTION & DECRYPTION KEY
    void SetKey(APDU apdu) {
        byte[] apdubuf = apdu.getBuffer();
        short dataLen = apdu.setIncomingAndReceive();
        //System.out.println(dataLen);
        // CHECK EXPECTED LENGTH
        //if ((short) (dataLen * 8) !=  KeyBuilder.LENGTH_AES_256) ISOException.throwIt(SW_KEY_LENGTH_BAD);
        if ((apdubuf[ISO7816.OFFSET_P2] == 0) || (apdubuf[ISO7816.OFFSET_P1] == 0)) {
            ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2);
        }//MILAN : To avoid side channel

        short counterLen = apdubuf[ISO7816.OFFSET_P2];
        m_counter = new byte[counterLen];
        //copy random number to counter
        Util.arrayCopyNonAtomic(apdubuf, ISO7816.OFFSET_CDATA, m_counter, (short) 0, counterLen);

        // GENERATE RANDOM KEY
        short keyLen = apdubuf[ISO7816.OFFSET_P1];
        System.out.println(keyLen);
        m_secureRandom.generateData(apdubuf, ISO7816.OFFSET_CDATA, (short) (255));

        // SET KEY VALUE
        m_aesKey.setKey(apdubuf, ISO7816.OFFSET_CDATA);

        m_encryptCipherCBC.init(m_aesKey, Cipher.MODE_ENCRYPT);
        m_decryptCipherCBC.init(m_aesKey, Cipher.MODE_DECRYPT);

        Util.arrayCopyNonAtomic(m_counter, (short) 0, apdubuf, (short) (ISO7816.OFFSET_CDATA + keyLen), (short) 4);//OFFSET_P2 == counterLen      
        apdu.setOutgoingAndSend(ISO7816.OFFSET_CDATA, (short) (counterLen + keyLen));
    }

//---------------------------------------------------------HASH--------------------------------------------
    // HASH INCOMING BUFFER
    void Hash(APDU apdu) {
        byte[] apdubuf = apdu.getBuffer();
        short dataLen = apdu.setIncomingAndReceive();

        if (m_hash != null) {
            m_hash.doFinal(apdubuf, ISO7816.OFFSET_CDATA, dataLen, m_ramArray, (short) 0);
        } else {
            ISOException.throwIt(SW_OBJECT_NOT_AVAILABLE);
        }

        // COPY ENCRYPTED DATA INTO OUTGOING BUFFER
        Util.arrayCopyNonAtomic(m_ramArray, (short) 0, apdubuf, ISO7816.OFFSET_CDATA, m_hash.getLength());

        // SEND OUTGOING BUFFER
        apdu.setOutgoingAndSend(ISO7816.OFFSET_CDATA, m_hash.getLength());
    }

//---------------------------------------------------------VERIFY PIN---------------------------------------
    // VERIFY PIN
    void VerifyPIN(APDU apdu) {
        byte[] apdubuf = apdu.getBuffer();
        short dataLen = apdu.setIncomingAndReceive();

        if (dataLen != 4) {// MILAN : Check for length equal to size passed
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }
        // VERIFY PIN
        if (m_pin.check(apdubuf, ISO7816.OFFSET_CDATA, (byte) dataLen) == false) {
            ISOException.throwIt(SW_BAD_PIN);
        }
    }

//-----------------------------------------------------------SET PIN-----------------------------------------
    // SET PIN 
    // Be aware - this method will allow attacker to set own PIN - need to protected. 
    // E.g., by additional Admin PIN or all secret data of previous user needs to be reased 
    void SetPIN(APDU apdu) {
        byte[] apdubuf = apdu.getBuffer();
        short dataLen = apdu.setIncomingAndReceive();

        if (dataLen != 4) {// MILAN : Check for length equal to size passed
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }

        short adminKeyByteNumber = apdubuf[ISO7816.OFFSET_P1];
        short adminKeyByte = apdubuf[ISO7816.OFFSET_P2];

        if ((ADMIN_PIN[adminKeyByteNumber] + 5) == adminKeyByte) {// MILAN : Additional Admin PIN protection
            // SET NEW PIN
            m_pin.update(apdubuf, ISO7816.OFFSET_CDATA, (byte) dataLen);
        } else {
            ISOException.throwIt(SW_BAD_PIN);
        }
    }

    //-----------------------------------------------------------SET Password-----------------------------------------
    // SET Password 
    // Be aware - this method will allow attacker to set own Password - need to protected. 
    void SetPassword(APDU apdu) {
        byte[] apdubuf = apdu.getBuffer();
        short dataLen = apdu.setIncomingAndReceive();

        if (dataLen != 12) {// MILAN : Check for length equal to size passed
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }

        Util.arrayCopyNonAtomic(apdubuf, ISO7816.OFFSET_CDATA, m_password, (short) 0, dataLen);

    }

//--------------------------------------------------GENERATE BUFFER-------------------------------------
    void GetAPDUBuff(APDU apdu) {
        byte[] apdubuf = apdu.getBuffer();

        // COPY ENCRYPTED DATA INTO OUTGOING BUFFER
        Util.arrayCopyNonAtomic(m_dataArray, (short) 0, apdubuf, ISO7816.OFFSET_CDATA, m_apduLogOffset);
        short tempLength = m_apduLogOffset;
        m_apduLogOffset = 0;
        // SEND OUTGOING BUFFER
        apdu.setOutgoingAndSend(ISO7816.OFFSET_CDATA, tempLength);
    }
}

