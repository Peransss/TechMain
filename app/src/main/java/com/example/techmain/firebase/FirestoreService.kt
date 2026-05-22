package com.example.techmain.firebase

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

class FirestoreService {
    private val db = FirebaseModule.db
    private val usersCollection = db.collection("users")
    private val gamesCollection = db.collection("games")
    private val roomsCollection = db.collection("rooms")

    suspend fun createOrUpdateUser(userId: String, displayName: String) {
        val userRef = usersCollection.document(userId)
        val snapshot = userRef.get().await()
        if (!snapshot.exists()) {
            userRef.set(
                mapOf(
                    "userId" to userId,
                    "displayName" to displayName,
                    "rating" to 1000,
                    "wins" to 0,
                    "losses" to 0,
                    "totalGames" to 0,
                    "correctAnswers" to 0,
                    "totalAnswers" to 0,
                    "createdAt" to FieldValue.serverTimestamp()
                )
            ).await()
        }
    }

    suspend fun updateDisplayName(userId: String, name: String) {
        usersCollection.document(userId).update("displayName", name).await()
    }

    suspend fun createRoom(hostId: String, hostName: String, categoryId: String): String {
        val roomCode = generateRoomCode()
        val room = mapOf(
            "roomCode" to roomCode,
            "hostId" to hostId,
            "categoryId" to categoryId,
            "players" to mapOf(
                hostId to mapOf(
                    "userId" to hostId,
                    "displayName" to hostName,
                    "isReady" to false
                )
            ),
            "status" to "waiting",
            "gameId" to "",
            "createdAt" to FieldValue.serverTimestamp()
        )
        roomsCollection.document(roomCode).set(room).await()
        return roomCode
    }

    suspend fun joinRoom(roomCode: String, userId: String, displayName: String): Boolean {
        val roomRef = roomsCollection.document(roomCode)
        val snapshot = roomRef.get().await()
        val room = snapshot.toObject<GameRoom>() ?: return false
        if (room.status != "waiting") return false

        roomRef.update(
            "players.$userId",
            mapOf("userId" to userId, "displayName" to displayName, "isReady" to false)
        ).await()
        return true
    }

    suspend fun leaveRoom(roomCode: String, userId: String) {
        val roomRef = roomsCollection.document(roomCode)
        roomRef.update("players.$userId", FieldValue.delete()).await()
    }

    fun listenRoom(roomCode: String): Flow<GameRoom> = callbackFlow {
        val listener = roomsCollection.document(roomCode)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(GameRoom(status = "error"))
                    return@addSnapshotListener
                }
                val room = snapshot?.toObject<GameRoom>() ?: GameRoom()
                trySend(room)
            }
        awaitClose { listener.remove() }
    }

    suspend fun startGameFromRoom(roomCode: String): String {
        val roomRef = roomsCollection.document(roomCode)
        val snapshot = roomRef.get().await()
        val room = snapshot.toObject<GameRoom>() ?: return ""

        val categoryId = room.categoryId
        val questions = QuestionBank.getQuestions(categoryId)
        val gameRef = gamesCollection.document()

        val players = mutableMapOf<String, Map<String, Any>>()
        room.players.forEach { (uid, player) ->
            players[uid] = mapOf(
                "userId" to uid,
                "displayName" to player.displayName,
                "score" to 0,
                "correctCount" to 0,
                "totalAnswered" to 0,
                "isReady" to false
            )
        }

        val game = mapOf(
            "gameId" to gameRef.id,
            "players" to players,
            "categoryId" to categoryId,
            "status" to "playing",
            "currentRound" to 0,
            "totalRounds" to questions.size,
            "questions" to questions.mapIndexed { index, q ->
                mapOf(
                    "id" to q.id,
                    "categoryId" to q.categoryId,
                    "question" to q.question,
                    "options" to q.options,
                    "correctAnswer" to q.correctAnswer,
                    "difficulty" to q.difficulty,
                    "timeLimit" to q.timeLimit,
                    "round" to index
                )
            },
            "roundStartTime" to (System.currentTimeMillis() + 3000),
            "roundTimeLimit" to 20,
            "winnerId" to "",
            "createdAt" to FieldValue.serverTimestamp()
        )
        gameRef.set(game, SetOptions.merge()).await()

        roomRef.update(mapOf("status" to "playing", "gameId" to gameRef.id)).await()
        return gameRef.id
    }

    fun listenGame(gameId: String): Flow<GameSession> = callbackFlow {
        val listener = gamesCollection.document(gameId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(GameSession(status = "error"))
                    return@addSnapshotListener
                }
                val game = snapshot?.toObject<GameSession>() ?: GameSession()
                trySend(game)
            }
        awaitClose { listener.remove() }
    }

    suspend fun submitAnswer(gameId: String, playerId: String, round: Int, selectedAnswer: Int, isCorrect: Boolean) {
        val gameRef = gamesCollection.document(gameId)
        gameRef.update(
            mapOf(
                "players.$playerId.totalAnswered" to FieldValue.increment(1L),
                "players.$playerId.correctCount" to FieldValue.increment(if (isCorrect) 1L else 0L),
                "players.$playerId.score" to FieldValue.increment(if (isCorrect) 100L else 0L),
                "players.$playerId.isReady" to true
            )
        ).await()
    }

    suspend fun advanceRound(gameId: String, currentRound: Int, totalRounds: Int) {
        val nextRound = currentRound + 1
        if (nextRound >= totalRounds) {
            endGame(gameId)
        } else {
            gamesCollection.document(gameId).update(
                mapOf(
                    "currentRound" to nextRound,
                    "roundStartTime" to (System.currentTimeMillis() + 2000)
                )
            ).await()
        }
    }

    private suspend fun endGame(gameId: String) {
        val gameRef = gamesCollection.document(gameId)
        val snapshot = gameRef.get().await()
        val game = snapshot.toObject<GameSession>() ?: return
        val players = game.players
        val winner = players.maxByOrNull { it.value.score }
        val winnerId = winner?.key ?: ""

        for ((uid, player) in players) {
            val userRef = usersCollection.document(uid)
            val isWinner = uid == winnerId
            userRef.update(
                mapOf(
                    "totalGames" to FieldValue.increment(1L),
                    "wins" to FieldValue.increment(if (isWinner) 1L else 0L),
                    "losses" to FieldValue.increment(if (!isWinner && players.size > 1) 1L else 0L),
                    "correctAnswers" to FieldValue.increment(player.correctCount.toLong()),
                    "totalAnswers" to FieldValue.increment(player.totalAnswered.toLong()),
                    "rating" to FieldValue.increment(if (isWinner) 15L else -5L)
                )
            ).await()
        }

        gameRef.update(mapOf("status" to "finished", "winnerId" to winnerId)).await()
    }

    fun getLeaderboard(): Flow<List<Map<String, Any?>>> = callbackFlow {
        val listener = usersCollection
            .orderBy("rating", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val users = snapshot?.documents?.map { it.data ?: emptyMap() } ?: emptyList()
                trySend(users)
            }
        awaitClose { listener.remove() }
    }

    fun getUserStats(userId: String): Flow<Map<String, Any?>?> = callbackFlow {
        val listener = usersCollection.document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.data)
            }
        awaitClose { listener.remove() }
    }

    private fun generateRoomCode(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        return (1..6).map { chars[Random.nextInt(chars.length)] }.joinToString("")
    }
}
