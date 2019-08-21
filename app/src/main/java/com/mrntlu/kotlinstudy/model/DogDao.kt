package com.mrntlu.kotlinstudy.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DogDao {
    @Insert
    suspend fun insertAll(vararg dogs:DogBreed): List<Long>

    @Query("Select * from dogbreed")
    suspend fun getAllDogs():List<DogBreed>

    @Query("Select * from dogbreed where uuid=:dogId")
    suspend fun getDog(dogId: Int):DogBreed

    @Query("Delete from dogbreed")
    suspend fun deleteAllDogs()
}