package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Farm
import com.example.data.model.Field
import kotlinx.coroutines.flow.Flow

@Dao
interface FarmDao {

    @Query("SELECT * FROM farms ORDER BY createdAt DESC")
    fun getAllFarms(): Flow<List<Farm>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFarm(farm: Farm)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFarms(farms: List<Farm>)

    @Update
    suspend fun updateFarm(farm: Farm)

    @Delete
    suspend fun deleteFarm(farm: Farm)

    @Query("SELECT * FROM fields WHERE farmId = :farmId")
    fun getFieldsForFarm(farmId: String): Flow<List<Field>>

    @Query("SELECT * FROM fields")
    fun getAllFields(): Flow<List<Field>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertField(field: Field)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFields(fields: List<Field>)
}
