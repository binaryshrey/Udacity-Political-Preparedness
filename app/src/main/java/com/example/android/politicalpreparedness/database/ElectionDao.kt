package com.example.android.politicalpreparedness.database

import androidx.room.*
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    // inserts a single election row in table
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertElectionElement(election: Election)

    // gets all election rows from table in order of electionDay col
    @Query("SELECT * FROM election_table ORDER BY electionDay")
    suspend fun getElectionElements(): List<Election>

    //gets a single election row that matches with the id col
    @Query("SELECT * FROM election_table WHERE id = :id")
    suspend fun getElectionElement(id: Int): Election?

    //deletes a single election row
    @Delete
    suspend fun deleteElectionElement(election: Election)

    //deletes all election rows
    @Query("DELETE FROM election_table")
    suspend fun clearElectionElements()
}