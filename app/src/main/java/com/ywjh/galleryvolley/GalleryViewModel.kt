package com.ywjh.galleryvolley

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson

//呼叫仓库类 交给View呈现 DVM作用拿到application的context
class GalleryViewModel(application: Application) : AndroidViewModel(application) {
    //私有化vitable，开发非 Mutable
    private val _photoListLive= MutableLiveData<List<PhotoItem>>()
    //开放
    val photoListLive:LiveData<List<PhotoItem>>
    get() = _photoListLive//只能读取而不能进行数据源的更改

    //定义获取网络图片列表的发送方法
    fun fetchData(){
        val stringResult=StringRequest(
            Request.Method.GET,
            getUrl(),
            //解析后赋值给内容
            Response.Listener {
                _photoListLive.value=Gson().fromJson(it,Pixabay::class.java).hits.toList()
            },
            Response.ErrorListener {
                Log.d("hello",it.toString())
            }
        )//Request导Volley包
        //添加volley队列
        VolleySingleton.getInstance(getApplication()).requestQueue.add(stringResult)//加载 reponse会回调
    }



    private fun getUrl():String {
        return "https://pixabay.com/api/?key=12472743-874dc01dadd26dc44e0801d61&q=${keyWords.random()}&per_page=100"
    }
    //关键词随机化处理
    private val keyWords = arrayOf("cat", "dog", "panda", "beauty", "miku", "animal")
}