package no.mabjork.auth_service.services

import no.mabjork.auth_service.factories.YamlPropertySourceFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.stereotype.Service
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*


@Service
@PropertySource( "classpath:security.yml", factory = YamlPropertySourceFactory::class)
class SecretService(
        @Value("\${security.expiration}")
        private val expiration: String,
        @Value("\${security.publicKey}")
        private val publicKey: String,
        @Value("\${security.privateKey}")
        private val privateKey: String
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

    fun getTokenExpiration(): Int {
        return expiration.toInt()
    }

}