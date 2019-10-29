package no.mabjork.ApiGateway.repositories


interface TokenRepository {
    fun getToken()
    fun storeToken()
}

class InMemoryTokenRepository: TokenRepository {
    override fun getToken() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun storeToken() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

class RedisTokenRepository: TokenRepository {
    override fun getToken() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun storeToken() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}