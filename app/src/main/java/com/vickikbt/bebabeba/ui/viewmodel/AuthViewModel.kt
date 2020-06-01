package com.vickikbt.bebabeba.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vickikbt.bebabeba.data.model.Riders
import com.vickikbt.bebabeba.data.repository.AuthRepository
import com.vickikbt.bebabeba.utils.network.EmptyResource


/*
*  This is an abstraction class that depends on AuthRepository
*  This class will be injected to Login and Register Activity or fragment
*  This are the functions that will there
*    1. login(email: String, password: String)
*    2. register(email: String, password: String)
*    3. saveUser(username: String)
*    4. onSignupError(exception: Exception? = null)
*
 */
class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val loginResult by lazy {
        MutableLiveData<EmptyResource>()
    }

    private val signUpResult by lazy {
        MutableLiveData<EmptyResource>()
    }

    /*
    * This is a function that enables login functionality
    * This function depends ont AuthRepository that is being injected
    * The injection is coming from the constructor on the class
    *    class AuthViewModel(
    *        private val authRepository: AuthRepository
    *    )
    * We are using EmptyResource thats located in utils/network/EmptyResource.kt
    * so this checks the status of the request then returns it back when u will
    * be observing it on  activity or fragment
    * */
    fun login(email: String, password: String) {

        loginResult.value = EmptyResource.loading()

        authRepository.login(email, password)
            .addOnSuccessListener {
                loginResult.value = EmptyResource.success()
            }
            .addOnFailureListener {
                loginResult.value = EmptyResource.error(it)
            }
    }

    /*
    * This is a function that enables registration functionality
    * This function depends ont AuthRepository that is being injected
    * The injection is coming from the constructor on the class
    *    class AuthViewModel(
    *        private val authRepository: AuthRepository
    *    )
    * We are using EmptyResource thats located in utils/network/EmptyResource.kt
    * so this checks the status of the request then returns it back when u will
    * be observing it on  activity or fragment
    * */
    fun register(email: String, password: String) {

        signUpResult.value = EmptyResource.loading()

        authRepository.register(email, password)
            .addOnSuccessListener {
                signUpResult.value = EmptyResource.success()
            }
            .addOnFailureListener {
                signUpResult.value = EmptyResource.error(it)
            }
    }

    /*
    * This is a function that enables saving of users object in firebase
    * This function depends ont AuthRepository that is being injected
    * The injection is coming from the constructor on the class
    *    class AuthViewModel(
    *        private val authRepository: AuthRepository
    *    )
    * We are using EmptyResource thats located in utils/network/EmptyResource.kt
    * so this checks the status of the request then returns it back when u will
    * be observing it on  activity or fragment
    * */
    fun saveUser(username: String) {
        val currentUser = authRepository.getCurrentUser() ?: run {
            onSignupError()
            return
        }

        val riders = Riders(currentUser.uid, username)

        authRepository.saveUser(riders)
            .addOnSuccessListener {
                signUpResult.value = EmptyResource.success()
            }
            .addOnFailureListener {
                onSignupError(it)
            }

    }

    /*
    * This is a function that retuns a response on user signup request
    * This function depends ont AuthRepository that is being injected
    * The injection is coming from the constructor on the class
    *    class AuthViewModel(
    *        private val authRepository: AuthRepository
    *    )
    * We are using EmptyResource that's located in utils/network/EmptyResource.kt
    * so this checks the status of the request then returns it back when u will
    * be observing it on  activity or fragment
    * */
    private fun onSignupError(exception: Exception? = null) {
        //"Signup Error: $exception".logError()
        authRepository.signOut()
        signUpResult.value = EmptyResource.error(exception)
    }
}