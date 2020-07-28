package com.example.mypokedex.repository

import android.util.Log
import com.example.mypokedex.model.ListObjectResponse
import com.example.mypokedex.model.dto.PokemonDto
import com.example.mypokedex.network.PokemonApiService
import com.example.mypokedex.network.retrofit

class PokemonRemoteRepository {

    private val retrofitService: PokemonApiService by lazy {
        retrofit.create(PokemonApiService::class.java)
    }

    suspend fun getPokemons(offset: Int, limit: Int): List<ListObjectResponse>? {
        val getPropertiesDeferred = retrofitService.getList(offset, limit)
        return try {
            getPropertiesDeferred.await().results!!
        } catch (e: Exception) {
            Log.e("ERRO", "Failure: ${e.message}", e)
            null
        }
    }

    suspend fun searchPokemonByNameOrId(param: String): PokemonDto? {
        val getPropertiesDeferred = retrofitService.getPokemonByNameOrId(param)
        return try {
            getPropertiesDeferred.await()
        } catch (e: Exception) {
            Log.e("ERRO", "Failure: ${e.message}", e)
            null
        }
    }
}