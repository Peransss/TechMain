package com.example.techmain.firebase

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class StorageService {
    private val storage = FirebaseStorage.getInstance().reference

    suspend fun uploadQuizImage(userId: String, quizId: String, questionId: String, uri: Uri): Result<String> {
        return runCatching {
            val ref = storage.child("media/quizzes/$userId/$quizId/$questionId.jpg")
            ref.putFile(uri).await()
            ref.downloadUrl.await().toString()
        }
    }

    suspend fun deleteQuizMedia(userId: String, quizId: String): Result<Unit> {
        return runCatching {
            val folderRef = storage.child("media/quizzes/$userId/$quizId")
            val listResult = folderRef.listAll().await()
            
            for (item in listResult.items) {
                item.delete().await()
            }
        }
    }
}
