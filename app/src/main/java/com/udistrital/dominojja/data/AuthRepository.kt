package com.udistrital.dominojja.data

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.udistrital.dominojja.R
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    /** nombre de la cuenta de Google null si juega como anonim */
    val displayName: String? get() = auth.currentUser?.displayName?.takeIf { it.isNotBlank() }

    suspend fun ensureSignedIn(): String {
        auth.currentUser?.let { return it.uid }
        return auth.signInAnonymously().await().user!!.uid
    }

    suspend fun signInWithGoogle(context: Context): String {
        val option = GetSignInWithGoogleOption
            .Builder(context.getString(R.string.default_web_client_id))
            .build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(option)
            .build()

        val credential = CredentialManager.create(context)
            .getCredential(context, request)
            .credential

        require(
            credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) { "Credencial no soportada" }

        val idToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)

        val user = auth.currentUser
        return try {
            if (user != null && user.isAnonymous) {

                user.linkWithCredential(firebaseCredential).await().user!!.uid
            } else {
                auth.signInWithCredential(firebaseCredential).await().user!!.uid
            }
        } catch (e: FirebaseAuthUserCollisionException) {

            auth.signInWithCredential(firebaseCredential).await().user!!.uid
        }
    }

    fun signOut() = auth.signOut()
}