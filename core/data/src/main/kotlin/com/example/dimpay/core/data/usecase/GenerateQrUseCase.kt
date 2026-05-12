package com.example.dimpay.core.data.usecase

import android.util.Base64
import android.util.Log
import com.example.dimpay.core.data.secure.CryptoManager
import com.example.dimpay.core.data.secure.CryptoManager.Companion.TRANSFORMATION
import com.example.dimpay.core.domain.model.EncryptedData
import com.example.dimpay.core.domain.secure.CardSecureStorage
import com.example.dimpay.core.domain.secure.SecureTokenStorage
import java.nio.ByteBuffer
import javax.inject.Inject
import java.util.UUID
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.toKotlinUuid

class GenerateQrUseCase @Inject constructor(
    private val cryptoManager: CryptoManager,
    private val secureTokenStorage: SecureTokenStorage,
    private val cardSecureStorage: CardSecureStorage
) {

    suspend operator fun invoke(
        cardId: String,
        amount: Double
    ): String {

        val token = secureTokenStorage.getNextToken(cardId)
            ?: throw IllegalStateException("No tokens")

        Log.d("QR", "Token: ${token.tokenId}")

        val cardInstance = cardSecureStorage.getCardInstance(cardId)
            ?: throw IllegalStateException("No cardInstance")

        val plaintext = ByteBuffer.allocate(32).apply {
            put(cardInstance.toUuidBytes().copyOf(16))
            putDouble(amount)
            putLong(System.currentTimeMillis() / 1000)
        }.array()

        var encryptedEncryptionKey = EncryptedData(token.encryptedKey, token.iv)
        var decryptedEncryptionKey = cryptoManager.decrypt("card_storage_key", encryptedEncryptionKey)

        val cipher = Cipher.getInstance(TRANSFORMATION)

        Log.d("QR", "key: ${decryptedEncryptionKey.toHexString()}")

        cipher.init(
            Cipher.ENCRYPT_MODE,
            SecretKeySpec(decryptedEncryptionKey, "AES")
        )

        val encryptedBytes = cipher.doFinal(plaintext)

        val finalBytes = token.tokenId.toUuidBytes() + cipher.iv + encryptedBytes

        Log.d("QR", "bytes: $finalBytes")

        val base64 = Base64.encodeToString(finalBytes, Base64.NO_WRAP)

        Log.d("QR", "bytes: $base64")

        secureTokenStorage.deleteToken(token.tokenId)

        return base64
    }
}

@OptIn(ExperimentalUuidApi::class)
fun uuidStringToBytes(uuidStr: String): ByteArray {
    val uuid = UUID.fromString(uuidStr) // accepts standard form "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"
    return uuid.toKotlinUuid().toByteArray()
//    val bb = ByteBuffer.allocate(16)
//    bb.putLong(uuid.mostSignificantBits)
//    bb.putLong(uuid.leastSignificantBits)
//    return bb.array() // 16 bytes, big-endian (network order)
}

// extension version
fun String.toUuidBytes(): ByteArray = uuidStringToBytes(this)
