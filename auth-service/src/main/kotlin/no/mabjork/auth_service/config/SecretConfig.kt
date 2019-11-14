package no.mabjork.auth_service.config


class SecretConfig (
    var expiration: String,
    var publicKey: String,
    var privateKey: String
)