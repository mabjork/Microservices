package no.mabjork.UserService.services

import no.mabjork.UserService.repositories.UserRepository
import org.springframework.stereotype.Service

interface UserService {
    fun findUserById()
    fun findAllUsers()
}

@Service
class UserServiceImpl(
        userRepository: UserRepository
) : UserService {

    override fun findAllUsers() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findUserById() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}