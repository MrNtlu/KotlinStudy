package com.mrntlu.kotlinstudy.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mrntlu.kotlinstudy.model.DogBreed
import com.mrntlu.kotlinstudy.model.DogDatabase
import com.mrntlu.kotlinstudy.model.DogsApiService
import com.mrntlu.kotlinstudy.util.NotificationsHelper
import com.mrntlu.kotlinstudy.util.SharedPreferencesHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import java.lang.NumberFormatException

class ListViewModel(application: Application) : BaseViewModel(application) {

    private val dogsService=DogsApiService()
    private val disposable=CompositeDisposable()
    private var prefHelper=SharedPreferencesHelper(getApplication())
    private var refreshTime=5*60*1000*1000*1000L

    val dogs=MutableLiveData<List<DogBreed>>()
    val dogsLoadError=MutableLiveData<Boolean>()
    val loading=MutableLiveData<Boolean>()

    fun refresh(){
        checkCacheDuration()
        val updateTime=prefHelper.getUpdateTime()
        if (updateTime!=null && updateTime!=0L && System.nanoTime()-updateTime<refreshTime){
            fetchFromDatabase()
        }else {
            fetchFromRemote()
        }
    }

    private fun checkCacheDuration() {
        val cachePreference=prefHelper.getCacheDuration()
        try{
            val cachePreferenceInt=cachePreference?.toInt() ?: 5 * 60
            refreshTime=cachePreferenceInt.times(60*1000*1000*1000L)
        }catch (e: NumberFormatException){
            e.printStackTrace()
        }
    }

    fun refreshBypassCache(){
        fetchFromRemote()
    }

    private fun fetchFromDatabase() {
        loading.value=true
        launch {
            val dogs=DogDatabase(getApplication()).dogDao().getAllDogs()
            dogsRetrieved(dogs)
            Toast.makeText(getApplication(),"Dogs retrieved from database",Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchFromRemote(){
        loading.value=true
        disposable.add(
            dogsService.getDogs().subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object: DisposableSingleObserver<List<DogBreed>>(){
                    override fun onSuccess(t: List<DogBreed>) {
                        storeDogsLocally(t)
                        Toast.makeText(getApplication(),"Dogs retrieved from endpoint",Toast.LENGTH_SHORT).show()
                        NotificationsHelper(getApplication()).createNotification()
                    }

                    override fun onError(e: Throwable) {
                        dogsLoadError.value=true
                        loading.value=false
                        e.printStackTrace()
                    }
                })
        )
    }

    private fun storeDogsLocally(dogList: List<DogBreed>) {
        launch {
            val dao=DogDatabase(getApplication()).dogDao()
            dao.deleteAllDogs()
            val result=dao.insertAll(*dogList.toTypedArray())
            var i=0
            while (i<dogList.size){
                dogList.get(i).uuid=result.get(i).toInt()
                ++i
            }
            dogsRetrieved(dogList)
        }
        prefHelper.saveUpdateTime(System.nanoTime())
    }

    private fun dogsRetrieved(dogList: List<DogBreed>){
        dogs.value=dogList
        dogsLoadError.value=false
        loading.value=false
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}