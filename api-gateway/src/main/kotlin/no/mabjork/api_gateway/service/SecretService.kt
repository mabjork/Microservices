package no.mabjork.api_gateway.service

import no.mabjork.api_gateway.factories.YamlPropertySourceFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.context.annotation.PropertySources
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.security.MessageDigest






@Service
@PropertySources(
        PropertySource("classpath:security.yml", factory = YamlPropertySourceFactory::class),
        PropertySource("classpath:authService.yml", factory = YamlPropertySourceFactory::class)
)
class SecretService(
        @Value("\${security.expiration}")
        private val expiration: String,
        @Value("\${security.publicKey}")
        private val publicKey: String,
        @Value("\${security.privateKey}")
        private val privateKey: String,
        @Value("\${authService.publicKey}")
        private val authServicePublicKey: String,
        @Value("\${security.tokenEncryptionSecret}")
        private val tokenEncryptionSecret: String
) {

    fun getTokenPrivateKey(): PrivateKey {
        val privateKeyContent = privateKey
                .replace("\n", "")
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "");

        val kf = KeyFactory.getInstance("RSA")
        val keySpecPKCS8 = PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyContent));
        val privKey = kf.generatePrivate(keySpecPKCS8);

        return privKey
    }

    fun getTokenPublicKey(): PublicKey {
        val publicKeyContent = publicKey
                .replace("\n", "")
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")

        val kf = KeyFactory.getInstance("RSA")
        val keySpecX509 = X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyContent));
        val pubKey = kf.generatePublic(keySpecX509);

        return pubKey
    }

    fun getAuthServicePublicKey(): PublicKey {
        val publicKeyContent = authServicePublicKey
                .replace("\n", "")
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")

        val kf = KeyFactory.getInstance("RSA")
        val keySpecX509 = X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyContent));
        val pubKey = kf.generatePublic(keySpecX509);

        return pubKey
    }

    fun getTokenExpiration(): Int {
        return expiration.toInt()
    }

    fun encryptToken(token: String): String {
        val cipher = Cipher.getInstance("AES")
        val secretKey = SecretKeySpec(tokenEncryptionSecret.toByteArray(), "AES")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(token.toByteArray()).toString()
    }

    fun decryptToken(token: String): String {
        val cipher = Cipher.getInstance("AES")
        val secretKey = SecretKeySpec(tokenEncryptionSecret.toByteArray(), "AES")
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(token.toByteArray()).toString()
    }

    fun hashToken(token: String): String {
        val digest = MessageDigest.getInstance("SHA3_256")
        val hashbytes = digest.digest(token.toByteArray(StandardCharsets.UTF_8))
        return hashbytes.toString()
    }



}