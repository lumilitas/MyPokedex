package com.example.mypokedex.model.pokemon.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.mypokedex.model.pokemon.entity.PokemonEntity
import com.example.mypokedex.model.pokemontype.PokemonWithTypes
import com.example.mypokedex.model.pokemontype.TypeWithPokemons

@Dao
interface PokemonDao {

    companion object {
        const val DATA_NOT_INSERTED = -1L
    }

    @Transaction
    @Query("SELECT * FROM type WHERE nome = :typeName")
    suspend fun searchPokemonsByType(typeName: String): TypeWithPokemons

    @Query("SELECT * FROM pokemon")
    fun getAll(): LiveData<List<PokemonWithTypes>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pokemonEntity: PokemonEntity): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(pokemonEntity: PokemonEntity)

    @Transaction
    suspend fun insertOrUpdate(data: List<PokemonEntity>) {
        data.forEach { if (insert(it) == DATA_NOT_INSERTED)  update(it) }
    }

}