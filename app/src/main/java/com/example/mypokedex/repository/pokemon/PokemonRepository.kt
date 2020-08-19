package com.example.mypokedex.repository.pokemon

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.mypokedex.database.PokemonDatabase
import com.example.mypokedex.model.pokemon.entity.PokemonEntity
import com.example.mypokedex.model.pokemon.ui.Pokemon
import com.example.mypokedex.model.pokemontype.PokemonTypeEntity
import com.example.mypokedex.model.pokemontype.PokemonWithTypes
import com.example.mypokedex.model.type.entity.TypeEntity
import com.example.mypokedex.repository.type.TypeLocalRepository
import com.example.mypokedex.repository.type.TypeRemoteRepository
import com.example.mypokedex.util.toPokemon
import com.example.mypokedex.util.toPokemonEntity
import com.example.mypokedex.util.toType
import com.example.mypokedex.util.toTypeEntity

class PokemonRepository(context: Context) {

    private val pokemonRemote = PokemonRemoteRepository()
    private val typeRemote = TypeRemoteRepository()

    private val pokemonLocal = PokemonLocalRepository(context)
    private val typeLocal = TypeLocalRepository(context)
    private val pokemonTypeLocal = PokemonDatabase.getDatabase(context).pokemonTypeDao()

    suspend fun getPokemonsFirstPageRemote(offset: Int = 0, limit: Int = 10) : String? {
        val pokemons = pokemonRemote.getPokemons(offset, limit)

        for (pokemon in pokemons!!.results!!) {
            val pokemon = pokemonRemote.searchPokemonByNameOrId(pokemon.name)

            pokemonLocal.insert(pokemon!!.toPokemonEntity())

            for (type in pokemon.types) {
                val t = typeLocal.getTypeByName(type.type.name)
                val p = pokemon.toPokemonEntity()

                pokemonTypeLocal.insertOrUpdate(PokemonTypeEntity(p.id, t.id))
            }

        }

        return pokemons.next
    }

    suspend fun getPokemonsNextPage(url: String): String? {
        val pokemons = pokemonRemote.getNextList(url)
        val pokemonsEntity = arrayListOf<PokemonEntity>()

        for (pokemon in pokemons!!.results!!) {
            pokemonsEntity.add(pokemonRemote.searchPokemonByNameOrId(pokemon.name)!!.toPokemonEntity())
        }

        pokemonLocal.insert(pokemonsEntity)

        return pokemons.next
    }

    suspend fun getTypes() {
        val response = typeRemote.getTypes()
        val types = arrayListOf<TypeEntity>()

        with(response) {
            for (type in response!!.results!!) {
                types.add(typeRemote.getType(type.name)!!.toTypeEntity())
            }
        }
        typeLocal.insert(types)
    }

    suspend fun getPokemon(id: Int): Pokemon {
        val pokemonEntity = pokemonTypeLocal.searchPokemon(id)

        val pokemon = pokemonEntity.toPokemon()

        for (typeEntity in pokemonEntity.type) {
            pokemon.types.add(typeEntity.toType())
        }

        return pokemon
    }

    fun getPokemonsLiveData(): LiveData<List<PokemonEntity>> {
        return pokemonLocal.getAll()
    }

    fun getTypesLiveData(): LiveData<List<TypeEntity>> {
        return typeLocal.getAll()
    }

}