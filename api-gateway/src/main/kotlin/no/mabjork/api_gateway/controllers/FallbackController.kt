package no.mabjork.api_gateway.controllers

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class FallbackController {

    @RequestMapping("/fallback")
    fun fallback(): String{
        return "this is a message"
    }
}