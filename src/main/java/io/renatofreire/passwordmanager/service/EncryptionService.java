package io.renatofreire.passwordmanager.service;

import io.renatofreire.passwordmanager.exception.InvalidFieldException;
import io.renatofreire.passwordmanager.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;

@Service
public class EncryptionService {

    private final static String ALGORITHM = "RSA";

    private static final Logger logger = LoggerFactory.getLogger(EncryptionService.class);

    public HashMap<String, byte[]> generateKeyPairValue(){
        HashMap<String, byte[]> retval = new HashMap<>();
        KeyPairGenerator generator = null;
        try {
            generator = KeyPairGenerator.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new InvalidFieldException("ALGORITHM does not exists");
        }
        generator.initialize(2048);
        KeyPair pair = generator.generateKeyPair();
        retval.put("private", pair.getPrivate().getEncoded());
        retval.put("public", pair.getPublic().getEncoded());
        return retval;
    }

    public byte[] encode(String toEncode, User user) {
        Cipher encryptCipher;
        KeyFactory keyFactory;
        PublicKey userPublicKey;

        try {
            encryptCipher = Cipher.getInstance(ALGORITHM);

            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(user.getPublicKey());

            keyFactory = KeyFactory.getInstance(ALGORITHM);

            userPublicKey = keyFactory.generatePublic(keySpec);

            encryptCipher.init(Cipher.ENCRYPT_MODE, userPublicKey);

            byte[] secretMessageBytes = toEncode.getBytes(StandardCharsets.UTF_8);

            return encryptCipher.doFinal(secretMessageBytes);
        } catch (IllegalBlockSizeException
                 | InvalidKeySpecException
                 |  BadPaddingException
                 | NoSuchAlgorithmException
                 | NoSuchPaddingException
                 | InvalidKeyException e) {
            logger.error("Something went wrong while trying to encode user's password:");
            e.printStackTrace();
            return new byte[0];
        }

    }

    public String decode(byte[] toDecode, User user) {
        Cipher decryptCipher;
        KeyFactory keyFactory;
        PrivateKey userPrivateKey;

        try {
            decryptCipher = Cipher.getInstance(ALGORITHM);


            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(user.getPrivateKey());

            keyFactory = KeyFactory.getInstance(ALGORITHM);
            userPrivateKey = keyFactory.generatePrivate(keySpec);


            decryptCipher.init(Cipher.DECRYPT_MODE, userPrivateKey);

            byte[] decryptedBytes = decryptCipher.doFinal(toDecode);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (IllegalBlockSizeException
                 | InvalidKeySpecException
                 |  BadPaddingException
                 | NoSuchAlgorithmException
                 | NoSuchPaddingException
                 | InvalidKeyException e) {
            logger.error("Something went wrong while trying to decoding user's password:");
            e.printStackTrace();
            return "";
        }

    }

}
