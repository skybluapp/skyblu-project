package com.skyblu.data.authentication

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import java.lang.IllegalArgumentException

class FirebaseAuthentication : AuthenticationInterface {

    override val loggedInFlow : Flow<String?> = flow<String?> {
        while(true){
            emit(getCurrentUser())
            delay(1000)
        }
    }






    val auth = FirebaseAuth.getInstance()
    override fun getCurrentUser(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }

    override fun login(
        email: String,
        password: String,
        onSucces: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {

        try {
            auth.signInWithEmailAndPassword(
                email,
                password
            )
                .addOnSuccessListener {
                    onSucces(it.user!!.uid)
                }
                .addOnFailureListener { exception ->
                    when(exception){
                        is FirebaseAuthInvalidCredentialsException -> {
                            Timber.d(exception)
                            onFailure("Incorrect Email or password")
                        }
                        else -> {
                            onFailure(exception.toString())
                        }
                    }
                }
        } catch (exception : Exception){
            when(exception){
                is IllegalArgumentException -> {
                    Timber.d(exception)
                    onFailure("Email or Username is empty")
                }
                else -> {
                    onFailure(exception.toString())
                }
            }
        }

    }

    override fun createAccount(
        email: String,
        password: String,
        confirm: String,
        onSucces: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {

        if (password != confirm) {
            onFailure("Passwords do not match.")
            return
        }

        try {
            auth.createUserWithEmailAndPassword(
                email.trim(),
                password
            ).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSucces(auth.currentUser!!.uid)
                } else {
                    onFailure(task.exception?.message.toString())
                }
            }
        } catch (exception: Exception) {
            when (exception.message) {
                "Given String is empty or null" -> {
                    onFailure("Email or Username is empty")
                }
                else -> {
                    onFailure(exception.toString())
                }
            }
        }
    }

    override fun logout() {
        auth.signOut()
    }

    override fun initialise(context: Context) {
        FirebaseApp.initializeApp(context)
    }
}