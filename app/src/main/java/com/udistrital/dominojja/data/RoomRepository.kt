package com.udistrital.dominojja.data

import com.google.firebase.firestore.FirebaseFirestore
import com.udistrital.dominojja.common.Constants
import com.udistrital.dominojja.model.Room
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class RoomRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val rooms = db.collection(Constants.COLLECTION_ROOMS)

    private fun generateCode(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789" // sin O, 0, I, 1
        return (1..Constants.ROOM_CODE_LENGTH).map { chars.random() }.joinToString("")
    }

    suspend fun createRoom(hostId: String): Result<String> = runCatching {
        val code = generateCode()
        val room = Room(id = code, hostId = hostId, players = listOf(hostId))
        rooms.document(code).set(room).await()
        code
    }

    suspend fun joinRoom(code: String, playerId: String): Result<Unit> = runCatching {
        val ref = rooms.document(code.uppercase())
        db.runTransaction { tx ->
            val room = tx.get(ref).toObject(Room::class.java)
                ?: throw IllegalStateException("La sala no existe")
            check(room.status == Constants.STATUS_WAITING) { "La sala ya inició" }
            if (playerId !in room.players) {
                check(room.players.size < Constants.MAX_PLAYERS) { "La sala está llena" }
                tx.update(ref, "players", room.players + playerId)
            }
        }.await()
    }

    fun observeWaitingRooms(): Flow<List<Room>> = callbackFlow {
        val reg = rooms
            .whereEqualTo("status", Constants.STATUS_WAITING)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    close(err); return@addSnapshotListener
                }
                trySend(snap?.toObjects(Room::class.java).orEmpty())
            }
        awaitClose { reg.remove() }
    }

    fun observeRoom(code: String): Flow<Room?> = callbackFlow {
        val reg = rooms.document(code).addSnapshotListener { snap, err ->
            if (err != null) {
                close(err); return@addSnapshotListener
            }
            trySend(snap?.toObject(Room::class.java))
        }
        awaitClose { reg.remove() }
    }
}