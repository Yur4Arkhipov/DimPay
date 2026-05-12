package com.example.dimpay.core.data.secure

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.example.dimpay.core.domain.model.EncryptedData
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject

class CryptoManager @Inject constructor() {

    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
    }

    fun createKeyIfNotExists(alias: String) {

        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)

        if (keyStore.containsAlias(alias)) {
            return
        }

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )

        val spec = KeyGenParameterSpec.Builder(
            alias,
            KeyProperties.PURPOSE_ENCRYPT or
                    KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(
                KeyProperties.ENCRYPTION_PADDING_NONE
            )
            .setRandomizedEncryptionRequired(true)
            .build()

        keyGenerator.init(spec)
        keyGenerator.generateKey()
    }

    fun encrypt(
        alias: String,
        data: ByteArray
    ): EncryptedData {

        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)

        val secretKey =
            keyStore.getKey(alias, null) as SecretKey

        val cipher = Cipher.getInstance(TRANSFORMATION)

        cipher.init(
            Cipher.ENCRYPT_MODE,
            secretKey
        )

        val encryptedBytes = cipher.doFinal(data)

        return EncryptedData(
            ciphertext = encryptedBytes,
            iv = cipher.iv
        )
    }

    fun decrypt(
        alias: String,
        encryptedData: EncryptedData
    ): ByteArray {

        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)

        val secretKey =
            keyStore.getKey(alias, null) as SecretKey

        val cipher = Cipher.getInstance(TRANSFORMATION)

        val spec = GCMParameterSpec(
            128,
            encryptedData.iv
        )

        cipher.init(
            Cipher.DECRYPT_MODE,
            secretKey,
            spec
        )

        return cipher.doFinal(
            encryptedData.ciphertext
        )
    }
}