package br.edu.ifsp.scl.sdm.currencyconverter.ui

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.ArrayAdapter
import br.edu.ifsp.scl.sdm.currencyconverter.R
import br.edu.ifsp.scl.sdm.currencyconverter.databinding.ActivityMainBinding
import br.edu.ifsp.scl.sdm.currencyconverter.model.livedata.CurrencyConverterLiveData
import br.edu.ifsp.scl.sdm.currencyconverter.service.ConvertService
import br.edu.ifsp.scl.sdm.currencyconverter.service.CurrenciesService

class MainActivity : AppCompatActivity() {
    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val currenciesServiceIntent by lazy {
        Intent(this, CurrenciesService::class.java)
    }

    private var convertService: ConvertService? = null
    private val convetServiceConnection = object : ServiceConnection{
        override fun onServiceConnected(p0: ComponentName?, serivce: IBinder?) {
            convertService = (serivce as ConvertService.ConvertServiceBinder).getConvertService()
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            //NSA
        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)
        setSupportActionBar(amb.mainTb.apply { title = getString(R.string.app_name) })

        var fromQuote = ""
        var toQuote = ""
        val currenciesAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf<String>())

        with(amb){
            fromQuoteMactv.apply {
                setAdapter(currenciesAdapter)
                setOnItemClickListener{_,_,_,_ -> fromQuote = text.toString()}
            }
            toQuoteMactv.apply {
                setAdapter(currenciesAdapter)
                setOnItemClickListener{_,_,_,_ -> toQuote = text.toString()}
            }
            convertBt.setOnClickListener{
                convertService?.convert(fromQuote, toQuote, amountTiet.text.toString())
            }
        }
        CurrencyConverterLiveData.currenciesLiveData.observe(this){currencyList ->
            currenciesAdapter.clear()
            currenciesAdapter.addAll(currencyList.currencies.keys.sorted())
            currenciesAdapter.getItem(0)?.also { quote->
                amb.fromQuoteMactv.setText(quote , false)
                fromQuote = quote
            }
            currenciesAdapter.getItem(currenciesAdapter.count -1)?.also { quote->
                amb.toQuoteMactv.setText(quote , false)
                toQuote = quote
            }

        }

        CurrencyConverterLiveData.conversionResultLiveData.observe(this){conversionResult ->
            with(amb){
                conversionResult.rates.values.first().rateForAmount.also {
                    resultTiet.setText(it)
                }
            }

        }

        startService(currenciesServiceIntent)
    }

    override fun onStart() {
        super.onStart()
        Intent(this@MainActivity, ConvertService::class.java).also { intent ->
            bindService(intent, convetServiceConnection, BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(convetServiceConnection)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(currenciesServiceIntent)
    }
}