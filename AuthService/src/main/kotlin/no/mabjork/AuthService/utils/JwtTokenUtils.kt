package no.mabjork.AuthService.utils

import org.springframework.beans.factory.annotation.Value
import java.io.Serializable


class JwtTokenUtils(@Value("\${jwt.secret}") private val secret: String) : Serializable {

    private val serialVersionUID = -2550185165626007488L

    val JWT_TOKEN_VALIDITY = 5 * 60 * 60


}