package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CropScan
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanDao {

    @Query("SELECT * FROM crop_scans ORDER BY timestamp DESC")
    fun getAllScans(): Flow<List<CropScan>>

    @Query("SELECT * FROM crop_scans WHERE id = :id LIMIT 1")
    suspend fun getScanById(id: String): CropScan?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: CropScan)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScans(scans: List<CropScan>)

    @Update
    suspend fun updateScan(scan: CropScan)

    @Delete
    suspend fun deleteScan(scan: CropScan)

    @Query("DELETE FROM crop_scans WHERE id = :id")
    suspend fun deleteScanById(id: String)

    @Query("SELECT COUNT(*) FROM crop_scans")
    fun getScansCount(): Flow<Int>
}
