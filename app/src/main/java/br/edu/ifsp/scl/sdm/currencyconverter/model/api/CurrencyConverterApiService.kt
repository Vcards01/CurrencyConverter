package br.edu.ifsp.scl.sdm.currencyconverter.model.api

import br.edu.ifsp.scl.sdm.currencyconverter.model.domain.ConversionResult
import br.edu.ifsp.scl.sdm.currencyconverter.model.domain.CurrencyList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface CurrencyConverterApiService {

    @Headers(
        "x-rapidapi-host:currency-converter5.p.rapidapi.com",
        "x-rapidapi-key: b08c93d4f1msh68b6a529a1336dap104c62jsn08db81124f64"
    )
    @GET("list")
    fun getCurrencies(): Call<CurrencyList>

    @Headers(
        "x-rapidapi-host:currency-converter5.p.rapidapi.com",
        "x-rapidapi-key: b08c93d4f1msh68b6a529a1336dap104c62jsn08db81124f64"
    )
    @GET("convert")
    fun convert(
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("amount") amount: String
    ): Call<ConversionResult>
}