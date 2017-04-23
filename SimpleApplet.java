package applets;

import javacard.framework.*;
import javacard.security.*;
import javacardx.crypto.*;

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

    //private   DESKey         m_desKey = null;// MILAN : Creating DES for Card 2.1.1
    private Cipher m_encryptCipherECB = null;// MILAN
    private Cipher m_decryptCipherECB = null;// MILAN

    private Cipher m_encryptCipherCBC = null;// MILAN
    private Cipher m_decryptCipherCBC = null;// MILAN

    private RandomData m_secureRandom = null;
    private MessageDigest m_hash = null;
    private Signature m_sessionMAC = null;
    private OwnerPIN m_pin = null;
    private Signature m_sign = null;
    private KeyPair m_keyPair = null;
    private Key m_privateKey = null;
    private Key m_publicKey = null;
    private OwnerPIN m_adminPIN = null;
    private short m_apduLogOffset = (short) 0;
    // TEMPORARRY ARRAY IN RAM
    private byte m_ramArray[] = null;
    // PERSISTENT ARRAY IN EEPROM
    private byte m_dataArray[] = null;

    // Random data
    private byte m_counter[] = null;

    //User password
    private byte m_password[] = null;

    //pin length
    private byte m_pin_length = (byte) 4;
    
    private Key randPriv = null;//(RSAPrivateKey) KeyBuilder.buildKey(KeyBuilder.TYPE_RSA_PRIVATE, KeyBuilder.LENGTH_RSA_2, false);
    private Key randPub = null;//(RSAPublicKey) KeyBuilder.buildKey(KeyBuilder.TYPE_RSA_PUBLIC, KeyBuilder.LENGTH_RSA_2048, false);
    private KeyPair randKeyPair = null;
    private Cipher randCipher = null;
    private byte[] cardRandom = null;
    
    private AESKey m_keyEncryptCard = null;
    private AESKey m_keyMacPcCard = null;
    private AESKey m_keyEncryptCardPc = null;
    
    private byte[] passPhrase = {(byte)0x01, (byte)0x02, (byte)0x03, (byte)0x04, (byte)0x05, (byte)0x06, (byte)0x07, (byte)0x08, (byte)0x09, (byte)0x0a, (byte)0x0b, (byte)0x0c};
    //private RSAPrivateKey b;
    //private Cipher dhCipher;

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

            //m_dataArray = new byte[ARRAY_LENGTH_SHORT];
            //Util.arrayFillNonAtomic(m_dataArray, (short) 0, ARRAY_LENGTH_SHORT, (byte) 0);
            // CREATE RANDOM DATA GENERATORS
            m_secureRandom = RandomData.getInstance(RandomData.ALG_SECURE_RANDOM);

            // TEMPORARY BUFFER USED FOR FAST OPERATION WITH MEMORY LOCATED IN RAM
            m_ramArray = JCSystem.makeTransientByteArray((short) 260, JCSystem.CLEAR_ON_DESELECT);

            // MILAN : CREATE DES OBJECT
            //m_desKey = (DESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_DES, KeyBuilder.LENGTH_DES3_2KEY, false);
            // SET KEY VALUE
            //m_desKey.setKey(m_dataArray, (short) 0);
            // CREATE AES KEY OBJECT
            m_aesKey = (AESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_AES, KeyBuilder.LENGTH_AES_256, false);
            // MILAN : CREATE OBJECTS FOR CBC CIPHERING
            m_encryptCipherCBC = Cipher.getInstance(Cipher.ALG_AES_BLOCK_128_CBC_NOPAD, false);
            m_decryptCipherCBC = Cipher.getInstance(Cipher.ALG_AES_BLOCK_128_CBC_NOPAD, false);

            // MILAN : CREATE OBJECTS FOR ECB CIPHERING
            m_encryptCipherECB = Cipher.getInstance(Cipher.ALG_AES_BLOCK_128_ECB_NOPAD, false);
            m_decryptCipherECB = Cipher.getInstance(Cipher.ALG_AES_BLOCK_128_ECB_NOPAD, false);

            // SET KEY VALUE
            m_aesKey.setKey(m_dataArray, (short) 0);

            // MILAN : INIT ECB CIPHERS WITH NEW KEY
            m_encryptCipherECB.init(m_aesKey, Cipher.MODE_ENCRYPT);
            m_decryptCipherECB.init(m_aesKey, Cipher.MODE_DECRYPT);

            // MILAN : INIT CBC CIPHERS WITH NEW KEY
            //m_encryptCipherCBC.init(m_aesKey, Cipher.MODE_ENCRYPT);
            //m_decryptCipherCBC.init(m_aesKey, Cipher.MODE_DECRYPT);
            
            m_pin = new OwnerPIN((byte) 3, m_pin_length); // 5 tries, 4 digits in pin
            m_pin.update(m_dataArray, (byte) 0, (byte) 4); // set initial random pin

            // CREATE RSA KEYS AND PAIR 
            m_keyPair = new KeyPair(KeyPair.ALG_RSA_CRT, KeyBuilder.LENGTH_RSA_2048);
            m_keyPair.genKeyPair(); // Generate fresh key pair on-card
            m_publicKey = m_keyPair.getPublic();
            m_privateKey = m_keyPair.getPrivate();
            // SIGNATURE ENGINE    
            m_sign = Signature.getInstance(Signature.ALG_RSA_SHA_PKCS1, false);
            // INIT WITH PRIVATE KEY
            m_sign.init(m_privateKey, Signature.MODE_SIGN);

            //m_adminPIN = new OwnerPIN((byte) 5, (byte) 4); // 5 tries, 4 digits in pin
            //byte ADMIN_PIN[] = {(byte) 0x31, (byte) 0x32, (byte) 0x33, (byte) 0x34};// admin PIN set to 1234
            //m_adminPIN.update(ADMIN_PIN, (short) 0, (byte) 4);
            // INIT HASH ENGINE
            m_hash = MessageDigest.getInstance(MessageDigest.ALG_SHA, false);
            //m_sessionMAC = Signature.getInstance(Signature.ALG_AES_MAC_128_NOPAD, false); // MILAN
            // update flag
            isOP2 = true;
            //Initialize g
            
            
            randKeyPair = new KeyPair(KeyPair.ALG_RSA_CRT, KeyBuilder.LENGTH_RSA_1024);
            randKeyPair.genKeyPair(); 
            randPriv = randKeyPair.getPrivate();
            randPub = randKeyPair.getPublic();
            randCipher = Cipher.getInstance(Cipher.ALG_RSA_PKCS1, false);            
            
            randCipher.init(randPriv, Cipher.MODE_DECRYPT);
            randCipher.init(randPub, Cipher.MODE_ENCRYPT);
            //randKeyPair.genKeyPair();
            //bPriv = dhKeyPair.getPrivate();
            //dhCipher = Cipher.getInstance(Cipher.ALG_RSA_NOPAD, false);

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
                    case INS_ENCRYPT:
                        Encrypt(apdu);
                        break;
                    case INS_DECRYPT:
                        Decrypt(apdu);
                        break;
                    case INS_HASH:
                        Hash(apdu);
                        break;
                    case INS_RANDOM:
                        Random(apdu);
                        break;
                    case INS_VERIFYPIN:
                        VerifyPIN(apdu);
                        break;
                    case INS_SETPIN:
                        SetPIN(apdu);
                        break;
                    case INS_RETURNDATA:
                        ReturnData(apdu);
                        break;// MILAN : Call returnHOTP()
                    case INS_SIGNDATA:
                        Sign(apdu);
                        break;
                    case INS_GETAPDUBUFF:
                        GetAPDUBuff(apdu);
                        break;
                    case INS_RETURNHOPT:
                        returnHOTP(apdu);
                        break;// MILAN : Call for INS_RETURNHOPT : FAILED due to non standart INS
                    case INS_ENCDECCBC:
                        EncryptDecryptCBC(apdu);
                        break;// MILAN : for AES CBC encrypt/ decrypt
                    case INS_SETPASS:
                        SetPassword(apdu);
                        break;
                    case INS_ASYMSEC_PC_CARD_1:
                        AsymSec_PC_Card_1(apdu);
                        break;
                    case INS_SYMSEC_PC_CARD_1:
                        SymSec_PC_Card_1(apdu);
                        break;
                    case INS_SYMSEC_PC_CARD_2:
                        SymSec_PC_Card_2(apdu);
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
        /*byte[] apdubuf = apdu.getBuffer();
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
        }
        else {
            ISOException.throwIt(SW_BAD_PASSWORD);
        }

        //
        byte[] cardPubKeyMod = null;
        byte[] cardPubKeyExp = null;
        short randPubKeyModSize = 0;
        short randPubKeyExpSize = 3;
        //short randPrivKeySize = 0;
        try {
            
            
            //randPrivKeySize = (short)(randPriv.getSize()/(short) 8);
            randPubKeyModSize = (short) (randPub.getSize()/(short) 8);
            
            cardPubKeyMod = JCSystem.makeTransientByteArray(randPubKeyModSize, JCSystem.CLEAR_ON_DESELECT);
            ((RSAPublicKey) randPub).getModulus(cardPubKeyMod, (short) 0);
            cardPubKeyExp = JCSystem.makeTransientByteArray(randPubKeyExpSize, JCSystem.CLEAR_ON_DESELECT);
            ((RSAPublicKey) randPub).getExponent(cardPubKeyExp, (short) 0);
            
            
            
            
        } catch (CryptoException e) {
            short reason = e.getReason();
            ISOException.throwIt(reason);
        }

        Util.arrayCopyNonAtomic(cardPubKeyMod, (short) 0, m_ramArray, (short) 0, (short)cardPubKeyMod.length);
        Util.arrayCopyNonAtomic(cardPubKeyExp, (short) 0, m_ramArray, (short)cardPubKeyMod.length, (short)cardPubKeyExp.length);
        Util.arrayCopyNonAtomic(m_counter, (short) 0, m_ramArray, (short)(cardPubKeyMod.length+cardPubKeyExp.length), (short) m_counter.length);
        
        m_encryptCipherCBC.doFinal(m_ramArray, (short) 0, (short) (m_counter.length + cardPubKeyMod.length+cardPubKeyExp.length+9), apdubuf, ISO7816.OFFSET_CDATA);
        
        
        //Util.arrayCopyNonAtomic(m_ramArray, (short) 0, apdubuf, ISO7816.OFFSET_CDATA, (short)(m_counter.length + cardPubKey.length));
        
        //apdu.setOutgoingAndSend(ISO7816.OFFSET_CDATA, (short)(maxLength+m_counter.length));
        //Util.arrayCopyNonAtomic(m_ramArray, (short) 0, apdubuf, ISO7816.OFFSET_CDATA, (short) (16));
        
        apdu.setOutgoingAndSend(ISO7816.OFFSET_CDATA, (short)(m_counter.length + cardPubKeyMod.length+ cardPubKeyExp.length+9));*/
    }
    
    void SymSec_PC_Card_1(APDU apdu) {
        byte[] apdubuf = apdu.getBuffer();
        short dataLen = apdu.setIncomingAndReceive();
        
        if ((apdubuf[ISO7816.OFFSET_P2] != 0) || (apdubuf[ISO7816.OFFSET_P1] != 0)) {
            ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2);
        }
        m_encryptCipherCBC.init(m_aesKey, Cipher.MODE_ENCRYPT);
        m_decryptCipherCBC.init(m_aesKey, Cipher.MODE_DECRYPT);
        m_decryptCipherCBC.doFinal(apdubuf, ISO7816.OFFSET_CDATA, dataLen, m_ramArray, (short) 0);
        
       if (m_pin.check(m_ramArray, (short) 0, m_pin_length) == false) {
            ISOException.throwIt(SW_KEY_LENGTH_BAD);
        }
        
        if (Util.arrayCompare(m_ramArray, m_pin_length, m_counter, (short) 0, (short) m_counter.length) == 0) {
            incrementCounter(m_counter);
        }
        else {
            ISOException.throwIt(SW_OBJECT_NOT_AVAILABLE);
        }

        
        // generating a random number of size 128 bytes
        cardRandom = new byte[randLength];
        m_secureRandom.generateData(cardRandom, (short)0, randLength);
        Util.arrayCopyNonAtomic(cardRandom, (short)0, m_ramArray, (short)0, randLength);
        Util.arrayCopyNonAtomic(m_counter, (short)0, m_ramArray, randLength, (short)m_counter.length);        
        
       
        m_encryptCipherCBC.doFinal(m_ramArray, (short) 0, (short) (m_counter.length + randLength), apdubuf, ISO7816.OFFSET_CDATA);
        
        m_keyEncryptCard = (AESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_AES, KeyBuilder.LENGTH_AES_256, false);
        byte[] hashBuffer = JCSystem.makeTransientByteArray((short) 20, JCSystem.CLEAR_ON_DESELECT);
        hashBuffer = generateHMAC(cardRandom, m_aesKey);
        byte[] keyFromHash = JCSystem.makeTransientByteArray((short) 32, JCSystem.CLEAR_ON_DESELECT);
        keyFromHash = hashToKey(hashBuffer);
        m_keyEncryptCard.setKey(keyFromHash, (short) 0);
        
        
        //Util.arrayCopyNonAtomic(m_ramArray, (short) 0, apdubuf, ISO7816.OFFSET_CDATA, (short)(m_counter.length + cardPubKey.length));
        
        //apdu.setOutgoingAndSend(ISO7816.OFFSET_CDATA, (short)(maxLength+m_counter.length));
        //Util.arrayCopyNonAtomic(m_ramArray, (short) 0, apdubuf, ISO7816.OFFSET_CDATA, (short) (16));
        
        apdu.setOutgoingAndSend(ISO7816.OFFSET_CDATA, (short)(m_counter.length + randLength));
    }
    
    void SymSec_PC_Card_2(APDU apdu) {
        byte[] apdubuf = apdu.getBuffer();
        short dataLen = apdu.setIncomingAndReceive();
        
        if ((apdubuf[ISO7816.OFFSET_P2] != 0) || (apdubuf[ISO7816.OFFSET_P1] != 0)) {
            ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2);
        }
        
        /*m_keyEncryptCard = (AESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_AES, KeyBuilder.LENGTH_AES_256, false);
        byte[] hashBuffer = JCSystem.makeTransientByteArray((short) 20, JCSystem.CLEAR_ON_DESELECT);
        hashBuffer = generateHMAC(cardRandom, m_aesKey);
        byte[] keyFromHash = JCSystem.makeTransientByteArray((short) 32, JCSystem.CLEAR_ON_DESELECT);
        keyFromHash = hashToKey(hashBuffer);
        m_keyEncryptCard.setKey(keyFromHash, (short) 0);
        */
        m_decryptCipherCBC.init(m_keyEncryptCard, Cipher.MODE_DECRYPT);        
        
        m_decryptCipherCBC.doFinal(apdubuf, ISO7816.OFFSET_CDATA, dataLen, m_ramArray, (short) 0);
       
        byte[] pcRandom = JCSystem.makeTransientByteArray(randLength, JCSystem.CLEAR_ON_DESELECT);//new byte[124];
        byte[] tempRandom = JCSystem.makeTransientByteArray((short) (2*randLength), JCSystem.CLEAR_ON_DESELECT);//new byte[248];
        //byte[] cardPcRandom = JCSystem.makeTransientByteArray((short) 248, JCSystem.CLEAR_ON_RESET);//new byte[248];
        byte[] pcRandomHMAC = JCSystem.makeTransientByteArray((short) 20, JCSystem.CLEAR_ON_DESELECT);//new byte[20];
        
        Util.arrayCopyNonAtomic(m_ramArray, (short) 0, pcRandom, (short) 0, randLength);
        Util.arrayCopyNonAtomic(m_ramArray, randLength, pcRandomHMAC, (short) 0, (short) 20);
        
        Util.arrayCopyNonAtomic(pcRandom, (short)0, tempRandom, (short)0, (short)pcRandom.length);
        Util.arrayCopyNonAtomic(cardRandom, (short)0, tempRandom, (short)pcRandom.length, (short)cardRandom.length);
        
        m_keyMacPcCard = (AESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_AES, KeyBuilder.LENGTH_AES_256, false); 
        byte[] hashBuffer = generateHMAC(tempRandom, m_aesKey);            
        byte[] keyFromHash = hashToKey(hashBuffer);
        m_keyMacPcCard.setKey(keyFromHash, (short) 0);
        
        byte[] checkPcRandomHMAC = JCSystem.makeTransientByteArray((short) 20, JCSystem.CLEAR_ON_DESELECT);//new byte[20];
        checkPcRandomHMAC = generateHMAC(pcRandom, m_keyMacPcCard);
        
        if(Util.arrayCompare(pcRandomHMAC, (short)0, checkPcRandomHMAC, (short)0, (short)pcRandomHMAC.length) != 0) {
            ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2);
        }
        
        
       Util.arrayCopyNonAtomic(cardRandom, (short)0, tempRandom, (short)0, (short)cardRandom.length);
        Util.arrayCopyNonAtomic(pcRandom, (short)0, tempRandom, (short)cardRandom.length, (short)pcRandom.length);
            
        m_keyEncryptCardPc = (AESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_AES, KeyBuilder.LENGTH_AES_256, false); 
        hashBuffer = generateHMAC(tempRandom, m_aesKey);            
        keyFromHash = hashToKey(hashBuffer);
        m_keyEncryptCardPc.setKey(keyFromHash, (short) 0);
        
       
        
        hashBuffer = generateHMAC(passPhrase, m_keyMacPcCard);
        byte[] finalPass = new byte[32];
        Util.arrayCopyNonAtomic(passPhrase, (short)0, finalPass, (short)0, (short)passPhrase.length);
        Util.arrayCopyNonAtomic(hashBuffer, (short)0, finalPass, (short)passPhrase.length, (short)hashBuffer.length);
        
        m_encryptCipherCBC.init(m_keyEncryptCardPc, Cipher.MODE_ENCRYPT);
        m_encryptCipherCBC.doFinal(finalPass, (short)0, (short)finalPass.length, apdubuf, ISO7816.OFFSET_CDATA);
        

        
        apdu.setOutgoingAndSend(ISO7816.OFFSET_CDATA, (short)finalPass.length);
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
    
    byte[] hashToKey(byte[] hash) {
        byte[] keyFromHash = new byte[32];
        Util.arrayCopyNonAtomic(hash, (short) 0, keyFromHash, (short) 0, (short) 20);
        Util.arrayCopyNonAtomic(hash, (short) 0, keyFromHash, (short) 20, (short) 12);        
        return keyFromHash;
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
        m_secureRandom.generateData(apdubuf, ISO7816.OFFSET_CDATA, (short) (keyLen * 8));

        // SET KEY VALUE
        m_aesKey.setKey(apdubuf, ISO7816.OFFSET_CDATA);

        //m_desKey.setKey(apdubuf, ISO7816.OFFSET_CDATA);
        // INIT CIPHERS WITH NEW KEY
        m_encryptCipherECB.init(m_aesKey, Cipher.MODE_ENCRYPT);
        m_decryptCipherECB.init(m_aesKey, Cipher.MODE_DECRYPT);
        
        m_encryptCipherCBC.init(m_aesKey, Cipher.MODE_ENCRYPT);
        m_decryptCipherCBC.init(m_aesKey, Cipher.MODE_DECRYPT);
        
        Util.arrayCopyNonAtomic(m_counter, (short) 0, apdubuf, (short) (ISO7816.OFFSET_CDATA + keyLen), (short) 4);//OFFSET_P2 == counterLen      
        apdu.setOutgoingAndSend(ISO7816.OFFSET_CDATA, (short) (counterLen + keyLen));
    }

//-----------------------------------------------------------ENCRYPT/DECRYPT------------------------------
    void EncryptDecryptCBC(APDU apdu) {
        byte[] apdubuf = apdu.getBuffer();
        short dataLen = apdu.setIncomingAndReceive();
        // CHECK EXPECTED LENGTH (MULTIPLY OF 64 bites)
        if ((dataLen % 16) != 0) {
            ISOException.throwIt(SW_CIPHER_DATA_LENGTH_BAD);// MILAN : comment this check for 255 Bytes
        }
        if (apdubuf[ISO7816.OFFSET_P1] == (byte) 0x00) {//MILAN : FOR ENCRYPT
            // ENCRYPT 
            if (apdubuf[ISO7816.OFFSET_P2] == (byte) 0x00)// P1 P2 = 0x01 0x00
            {
                m_encryptCipherCBC.update(apdubuf, ISO7816.OFFSET_CDATA, dataLen, m_ramArray, (short) 0);
            }
            if (apdubuf[ISO7816.OFFSET_P2] == (byte) 0x01)// P1 P2 = 0x01 0x01
            {
                m_encryptCipherCBC.doFinal(apdubuf, ISO7816.OFFSET_CDATA, dataLen, m_ramArray, (short) 0);
            }
        }
        
        if (apdubuf[ISO7816.OFFSET_P1] == (byte) 0x01) {//MILAN : FOR DECRYPT
            //DECRYPT
            m_decryptCipherCBC.doFinal(apdubuf, ISO7816.OFFSET_CDATA, dataLen, m_ramArray, (short) 0);
        }

        // COPY ENCRYPTED DATA INTO OUTGOING BUFFER
        Util.arrayCopyNonAtomic(m_ramArray, (short) 0, apdubuf, ISO7816.OFFSET_CDATA, dataLen);

        // SEND OUTGOING BUFFER
        apdu.setOutgoingAndSend(ISO7816.OFFSET_CDATA, dataLen);
        
    }
//---------------------------------------------------------------ENCRYPT----------------------------------   
    // ENCRYPT INCOMING BUFFER

    void Encrypt(APDU apdu) {
        byte[] apdubuf = apdu.getBuffer();
        short dataLen = apdu.setIncomingAndReceive();

        // CHECK EXPECTED LENGTH (MULTIPLY OF 64 bites)
        if ((dataLen % 16) != 0) {
            ISOException.throwIt(SW_CIPHER_DATA_LENGTH_BAD);
        }
        
        if (apdubuf[ISO7816.OFFSET_P1] == (byte) 0x00) {//MILAN : FOR ECB_NO_PAD
            // ENCRYPT 
            if (apdubuf[ISO7816.OFFSET_P2] == (byte) 0x00)// P1 P2 = 0x00 0x00
            {
                m_encryptCipherECB.update(apdubuf, ISO7816.OFFSET_CDATA, dataLen, m_ramArray, (short) 0);
            }
            if (apdubuf[ISO7816.OFFSET_P2] == (byte) 0x01)// P1 P2 = 0x00 0x01
            {
                m_encryptCipherECB.doFinal(apdubuf, ISO7816.OFFSET_CDATA, dataLen, m_ramArray, (short) 0);
            }
            
        }
        if (apdubuf[ISO7816.OFFSET_P1] == (byte) 0x01) {//MILAN : FOR CBC_NO_PAD
            // ENCRYPT 
            if (apdubuf[ISO7816.OFFSET_P2] == (byte) 0x00)// P1 P2 = 0x01 0x00
            {
                m_encryptCipherCBC.update(apdubuf, ISO7816.OFFSET_CDATA, dataLen, m_ramArray, (short) 0);
            }
            if (apdubuf[ISO7816.OFFSET_P2] == (byte) 0x01)// P1 P2 = 0x01 0x01
            {
                m_encryptCipherCBC.doFinal(apdubuf, ISO7816.OFFSET_CDATA, dataLen, m_ramArray, (short) 0);
            }
        }

        // ENCRYPT INCOMING BUFFER
        //m_encryptCipher.doFinal(apdubuf, ISO7816.OFFSET_CDATA, dataLen, m_ramArray, (short) 0);
        // COPY ENCRYPTED DATA INTO OUTGOING BUFFER
        Util.arrayCopyNonAtomic(m_ramArray, (short) 0, apdubuf, ISO7816.OFFSET_CDATA, dataLen);

        // SEND OUTGOING BUFFER
        apdu.setOutgoingAndSend(ISO7816.OFFSET_CDATA, dataLen);
    }

//--------------------------------------------------------DECRYPT---------------------------------------
    // DECRYPT INCOMING BUFFER
    void Decrypt(APDU apdu) {
        byte[] apdubuf = apdu.getBuffer();
        short dataLen = apdu.setIncomingAndReceive();
        //short     i;

        // CHECK EXPECTED LENGTH (MULTIPLY OF 64 bites)
        if ((dataLen % 16) != 0) {
            ISOException.throwIt(SW_CIPHER_DATA_LENGTH_BAD);
        }
        
        if (apdubuf[ISO7816.OFFSET_P1] == (byte) 0x00) {//MILAN : FOR ECB_NO_PAD
            // ENCRYPT 
            if (apdubuf[ISO7816.OFFSET_P2] == (byte) 0x00)// P1 P2 = 0x00 0x00
            {
                m_decryptCipherECB.update(apdubuf, ISO7816.OFFSET_CDATA, dataLen, m_ramArray, (short) 0);
            }
            if (apdubuf[ISO7816.OFFSET_P2] == (byte) 0x01)// P1 P2 = 0x00 0x01
            {
                m_decryptCipherECB.doFinal(apdubuf, ISO7816.OFFSET_CDATA, dataLen, m_ramArray, (short) 0);
            }
            
        }
        if (apdubuf[ISO7816.OFFSET_P1] == (byte) 0x01) {//MILAN : FOR CBC_NO_PAD
            // ENCRYPT 
            //if(apdubuf[ISO7816.OFFSET_P2] == (byte) 0x00)// P1 P2 = 0x01 0x00
            //m_decryptCipherCBC.update(apdubuf, ISO7816.OFFSET_CDATA, dataLen, m_ramArray, (short) 0);
            //if(apdubuf[ISO7816.OFFSET_P2] == (byte) 0x01)// P1 P2 = 0x01 0x01
            m_decryptCipherCBC.doFinal(apdubuf, ISO7816.OFFSET_CDATA, dataLen, m_ramArray, (short) 0);
        }

        // ENCRYPT INCOMING BUFFER
        //m_decryptCipher.doFinal(apdubuf, ISO7816.OFFSET_CDATA, dataLen, m_ramArray, (short) 0);
        // COPY ENCRYPTED DATA INTO OUTGOING BUFFER
        Util.arrayCopyNonAtomic(m_ramArray, (short) 0, apdubuf, ISO7816.OFFSET_CDATA, dataLen);

        // SEND OUTGOING BUFFER
        apdu.setOutgoingAndSend(ISO7816.OFFSET_CDATA, dataLen);
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

//-----------------------------------------------------------RANDOM DATA-------------------------------------
    // GENERATE RANDOM DATA
    void Random(APDU apdu) {
        byte[] apdubuf = apdu.getBuffer();
        short dataLen = apdu.setIncomingAndReceive();

        // GENERATE DATA
        short randomDataLen = apdubuf[ISO7816.OFFSET_P1];
        m_secureRandom.generateData(apdubuf, ISO7816.OFFSET_CDATA, randomDataLen);

        // SEND OUTGOING BUFFER
        apdu.setOutgoingAndSend(ISO7816.OFFSET_CDATA, randomDataLen);
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
        
        if (dataLen != 4) {// MILAN : Check for length equal to size passed
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }
        
        short adminKeyByteNumber = apdubuf[ISO7816.OFFSET_P1];
        short adminKeyByte = apdubuf[ISO7816.OFFSET_P2];
        
        if ((ADMIN_PIN[adminKeyByteNumber] + 5) == adminKeyByte) {// MILAN : Additional Admin PIN protection
            // SET NEW PIN
            //m_pin.update(apdubuf, ISO7816.OFFSET_CDATA, (byte) dataLen);
            Util.arrayCopyNonAtomic(apdubuf, ISO7816.OFFSET_CDATA, m_password, (short) 0, dataLen);
        } else {
            ISOException.throwIt(SW_BAD_PASSWORD);
        }
    }

//-------------------------------------------------RETURN DATA SAME AS INCOMING-----------------------------
    void ReturnData(APDU apdu) {
        byte[] apdubuf = apdu.getBuffer();
        short dataLen = apdu.setIncomingAndReceive();

        // RETURN INPUT DATA UNCHANGED
        apdu.setOutgoingAndSend(ISO7816.OFFSET_CDATA, dataLen);
    }

    
//------------------------------HMAC

byte[] generateHMAC(byte[] buffer, AESKey aKey) {
    //byte[] hash = JCSystem.makeTransientByteArray((short)20, JCSystem.CLEAR_ON_RESET);
        //byte[] apdubuf = apdu.getBuffer();
        short dataLen = (short)buffer.length;
        
        //short sigLength = 0;
        //short hotpLen = 4;
        short hashLen = 20;
        short macLen = 8;
        short keyLen = 32;        
        
        m_hash = MessageDigest.getInstance(MessageDigest.ALG_SHA, false);
        

        byte[] mac = JCSystem.makeTransientByteArray((short) 20, JCSystem.CLEAR_ON_DESELECT);
        
        //byte[] trunc_hash = JCSystem.makeTransientByteArray(hotpLen, JCSystem.CLEAR_ON_RESET);
        
        //byte[] hotp = JCSystem.makeTransientByteArray(hotpLen, JCSystem.CLEAR_ON_RESET);
        
        byte[] key = JCSystem.makeTransientByteArray(keyLen, JCSystem.CLEAR_ON_DESELECT);
        
        byte[] challenge = JCSystem.makeTransientByteArray(dataLen, JCSystem.CLEAR_ON_DESELECT);

         byte[] temp16         = JCSystem.makeTransientByteArray((short) 16, JCSystem.CLEAR_ON_DESELECT);
        byte[] hash = JCSystem.makeTransientByteArray(hashLen, JCSystem.CLEAR_ON_DESELECT);
        
        byte[] temp32 = JCSystem.makeTransientByteArray((short) 32, JCSystem.CLEAR_ON_DESELECT);

        //byte[] temp36         = JCSystem.makeTransientByteArray((short) 36, JCSystem.CLEAR_ON_RESET);
        byte[] tempDataKey = JCSystem.makeTransientByteArray((short) (dataLen+keyLen), JCSystem.CLEAR_ON_DESELECT);
        
        byte[] temp52 = JCSystem.makeTransientByteArray((short) 52, JCSystem.CLEAR_ON_DESELECT);
        
        
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
        if (m_hash != null) {
            // m_hash.doFinal(temp32, (short)0, (short)(dataLen + keyLen), hash, (short) 0);
            m_hash.doFinal(tempDataKey, (short) 0, (short) (dataLen + keyLen), hash, (short) 0);
        }

        //KEY XOR 0x5c5c5c5c....
        for (short i = 0; i < keyLen; i++) {
            //temp16[i] = (byte)(key[i] ^ 0x5c);
            temp32[i] = (byte) (key[i] ^ 0x5c);
        }
        Util.arrayCopyNonAtomic(temp32, (short) 0, temp52, (short) 0, keyLen);
        Util.arrayCopyNonAtomic(hash, (short) 0, temp52, keyLen, hashLen);

        // HASH((KEY XOR 0x5c5c5c5c...) || (HASH((KEY XOR 0x363636...) || CHALLENEGE)))
        if (m_hash != null) {
            //m_hash.doFinal(temp36, (short)0, (short)(dataLen + keyLen), hash, (short) 0);
            m_hash.doFinal(temp52, (short) 0, (short) (hashLen + keyLen), hash, (short) 0);
        }
        
        //System.out.println(CardMngr.bytesToHex(hash));
       

        return hash;
        //return null;
        
    } 

    
//------------------------------------------GENERATE HOTP-----------------
    void returnHOTP(APDU apdu) {
       /* byte[] apdubuf = apdu.getBuffer();
        short dataLen = apdu.setIncomingAndReceive();
        
        short sigLength = 0;
        short hotpLen = 4;
        short hashLen = 20;
        short macLen = 8;
        short keyLen = 32;
        
        if ((apdubuf[ISO7816.OFFSET_P1] != 0) || (apdubuf[ISO7816.OFFSET_P2] != 0)) {
            ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2);
        }//MILAN : To avoid side channel

        byte[] mac = JCSystem.makeTransientByteArray((short) 20, JCSystem.CLEAR_ON_RESET);
        
        byte[] trunc_hash = JCSystem.makeTransientByteArray(hotpLen, JCSystem.CLEAR_ON_RESET);
        
        byte[] hotp = JCSystem.makeTransientByteArray(hotpLen, JCSystem.CLEAR_ON_RESET);
        
        byte[] key = JCSystem.makeTransientByteArray(keyLen, JCSystem.CLEAR_ON_RESET);
        
        byte[] challenge = JCSystem.makeTransientByteArray(dataLen, JCSystem.CLEAR_ON_RESET);

        // byte[] temp16         = JCSystem.makeTransientByteArray((short) 16, JCSystem.CLEAR_ON_RESET);
        byte[] hash = JCSystem.makeTransientByteArray(hashLen, JCSystem.CLEAR_ON_RESET);
        
        byte[] temp32 = JCSystem.makeTransientByteArray((short) 32, JCSystem.CLEAR_ON_RESET);

        //byte[] temp36         = JCSystem.makeTransientByteArray((short) 36, JCSystem.CLEAR_ON_RESET);
        byte[] temp64 = JCSystem.makeTransientByteArray((short) 64, JCSystem.CLEAR_ON_RESET);
        
        byte[] temp52 = JCSystem.makeTransientByteArray((short) 52, JCSystem.CLEAR_ON_RESET);
        
        m_aesKey.getKey(key, (short) 0);
        
        Util.arrayCopyNonAtomic(apdubuf, ISO7816.OFFSET_CDATA, challenge, (short) 0, dataLen);

        // KEY XOR 0x363636....
        for (short i = 0; i < keyLen; i++) {
            //temp16[i] = (byte)(key[i] ^ 0x36);
            temp32[i] = (byte) (key[i] ^ 0x36);
        }
        Util.arrayCopyNonAtomic(temp32, (short) 0, temp64, (short) 0, dataLen);
        Util.arrayCopyNonAtomic(challenge, (short) 0, temp64, dataLen, dataLen);

        // HASH((KEY XOR 0x363636...) || CHALLENEGE)
        if (m_hash != null) {
            // m_hash.doFinal(temp32, (short)0, (short)(dataLen + keyLen), hash, (short) 0);
            m_hash.doFinal(temp64, (short) 0, (short) (dataLen + keyLen), hash, (short) 0);
        }

        //KEY XOR 0x5c5c5c5c....
        for (short i = 0; i < keyLen; i++) {
            //temp16[i] = (byte)(key[i] ^ 0x5c);
            temp32[i] = (byte) (key[i] ^ 0x5c);
        }
        Util.arrayCopyNonAtomic(temp32, (short) 0, temp52, (short) 0, dataLen);
        Util.arrayCopyNonAtomic(hash, (short) 0, temp52, dataLen, hashLen);

        // HASH((KEY XOR 0x5c5c5c5c...) || (HASH((KEY XOR 0x363636...) || CHALLENEGE)))
        if (m_hash != null) {
            //m_hash.doFinal(temp36, (short)0, (short)(dataLen + keyLen), hash, (short) 0);
            m_hash.doFinal(temp64, (short) 0, (short) (dataLen + keyLen), hash, (short) 0);
        }

        //MILAN : TRUNCATE THE HMAC
        byte lowerNibble = (byte) (hash[19] & 0x0f);
        
        Util.arrayCopyNonAtomic(hash, (short) (lowerNibble), trunc_hash, (short) 0, hotpLen);

        //System.out.println("TRUNCATED_HMAC = " + CardMngr.bytesToHex(trunc_hash));
        //MILAN : GENERATE HOTP
        hotp[0] = (byte) (trunc_hash[0] & 0x7F);
        hotp[1] = (byte) (trunc_hash[1] & 0xFF);
        hotp[2] = (byte) (trunc_hash[2] & 0xFF);
        hotp[3] = (byte) (trunc_hash[3] & 0xFF);

        // System.out.println("HOTP = " + CardMngr.bytesToHex(hotp));
        //COPY HOTP INTO OUTGOING BUFFER
        Util.arrayCopyNonAtomic(hotp, (short) 0, apdubuf, ISO7816.OFFSET_CDATA, hotpLen);

        //SEND OUTGOING BUFFER
        apdu.setOutgoingAndSend(ISO7816.OFFSET_CDATA, hotpLen);*/
        
    }
//------------------------------------------SIGN : GENERATE DES OR AES HMAC-----------------

    void Sign(APDU apdu) {
        /*byte[] apdubuf = apdu.getBuffer();
        short dataLen = apdu.setIncomingAndReceive();
        //short     signLen = 0;
        short sigLength = 0;
        short hotpLen = 4;
        short hashLen = 20;
        //short macLen = 8;
        short keyLen = 16;
        //byte[] temp = new byte[(short)(dataLen + 16)];

        if ((apdubuf[ISO7816.OFFSET_P1] != 0) || (apdubuf[ISO7816.OFFSET_P2] != 0)) {
            ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2);
        }//MILAN : To avoid side channel

        // byte[] mac         = JCSystem.makeTransientByteArray((short) 20, JCSystem.CLEAR_ON_RESET);
        byte[] trunc_hash = JCSystem.makeTransientByteArray(hotpLen, JCSystem.CLEAR_ON_RESET);
        
        byte[] hotp = JCSystem.makeTransientByteArray(hotpLen, JCSystem.CLEAR_ON_RESET);
        
        byte[] key = JCSystem.makeTransientByteArray(keyLen, JCSystem.CLEAR_ON_RESET);
        
        byte[] challenge = JCSystem.makeTransientByteArray(dataLen, JCSystem.CLEAR_ON_RESET);
        
        byte[] temp16 = JCSystem.makeTransientByteArray((short) 16, JCSystem.CLEAR_ON_RESET);
        
        byte[] hash = JCSystem.makeTransientByteArray(hashLen, JCSystem.CLEAR_ON_RESET);
        
        byte[] temp32 = JCSystem.makeTransientByteArray((short) 32, JCSystem.CLEAR_ON_RESET);
        
        byte[] temp36 = JCSystem.makeTransientByteArray((short) 36, JCSystem.CLEAR_ON_RESET);
        
        m_aesKey.getKey(key, (short) 0);
        
        Util.arrayCopyNonAtomic(apdubuf, ISO7816.OFFSET_CDATA, challenge, (short) 0, dataLen);

        // KEY XOR 0x363636....
        for (short i = 0; i < keyLen; i++) {
            temp16[i] = (byte) (key[i] ^ 0x36);
            //temp32[i] = (byte)(key[i] ^ 0x36);
        }
        Util.arrayCopyNonAtomic(temp16, (short) 0, temp32, (short) 0, dataLen);
        Util.arrayCopyNonAtomic(challenge, (short) 0, temp32, dataLen, dataLen);

        // HASH((KEY XOR 0x363636...) || CHALLENEGE)
        if (m_hash != null) {
            m_hash.doFinal(temp32, (short) 0, (short) (dataLen + keyLen), hash, (short) 0);
            //  m_hash.doFinal(temp64, (short)0, (short)(dataLen + keyLen), hash, (short) 0);
        }

        //KEY XOR 0x5c5c5c5c....
        for (short i = 0; i < keyLen; i++) {
            temp16[i] = (byte) (key[i] ^ 0x5c);
            //temp32[i] = (byte)(key[i] ^ 0x5c);
        }
        Util.arrayCopyNonAtomic(temp16, (short) 0, temp36, (short) 0, dataLen);
        Util.arrayCopyNonAtomic(hash, (short) 0, temp36, dataLen, hashLen);

        // HASH((KEY XOR 0x5c5c5c5c...) || (HASH((KEY XOR 0x363636...) || CHALLENEGE)))
        if (m_hash != null) {
            m_hash.doFinal(temp36, (short) 0, (short) (dataLen + keyLen), hash, (short) 0);
            //m_hash.doFinal(temp64, (short)0, (short)(dataLen + keyLen), hash, (short) 0);
        }

        //MILAN : TRUNCATE THE HMAC
        byte lowerNibble = (byte) (hash[19] & 0x0f);
        
        Util.arrayCopyNonAtomic(hash, (short) (lowerNibble), trunc_hash, (short) 0, hotpLen);

        //System.out.println("TRUNCATED_HMAC = " + CardMngr.bytesToHex(trunc_hash));
        //MILAN : GENERATE HOTP
        hotp[0] = (byte) (trunc_hash[0] & 0x7F);
        hotp[1] = (byte) (trunc_hash[1] & 0xFF);
        hotp[2] = (byte) (trunc_hash[2] & 0xFF);
        hotp[3] = (byte) (trunc_hash[3] & 0xFF);

        // System.out.println("HOTP = " + CardMngr.bytesToHex(hotp));
        //COPY HOTP INTO OUTGOING BUFFER
        Util.arrayCopyNonAtomic(hotp, (short) 0, apdubuf, ISO7816.OFFSET_CDATA, hotpLen);

        //SEND OUTGOING BUFFER
        apdu.setOutgoingAndSend(ISO7816.OFFSET_CDATA, hotpLen);*/
        
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
