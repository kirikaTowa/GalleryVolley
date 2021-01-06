package com.ywjh.galleryvolley


import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.insertImage
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.fragment_pager_photo.*
import kotlinx.android.synthetic.main.pager_photo_view.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class PagerPhotoFragment : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pager_photo, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val phootList: ArrayList<PhotoItem>? =arguments?.getParcelableArrayList<PhotoItem>("PHOTO_LIST")
        PagerPhotoListAdapter().apply {
            viewPager2.adapter=this//二代viewpager可接受listadapter
            //再给个数据
            submitList(phootList)
        }

        viewPager2.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                photoTag.text="${position+1}/${phootList?.size}"
            }
        })
        viewPager2.setCurrentItem(arguments?.getInt("PHOTO_POSITION") ?: 0, false)
        //保存到系统相册 公共空间
        saveButton.setOnClickListener {
            //toast()
            if (Build.VERSION.SDK_INT < 29 && ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(//只有一个参数也要用数组arrayof
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
            }else{
                viewLifecycleOwner.lifecycleScope.launch {
                    savePhoto()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //跟随Fragment生命周期线程变化 .launch启动
                    viewLifecycleOwner.lifecycleScope.launch {
                        savePhoto()
                    }
                    } else{
                    Toast.makeText(requireContext(), "存储失败", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private suspend fun savePhoto() {
        withContext(Dispatchers.IO){
            val holder=(viewPager2[0] as RecyclerView).findViewHolderForAdapterPosition(viewPager2.currentItem)//获取第一个元素 转为RecycleView
                    as PagerPhotoListAdapter.PagerPhotoViewHolder
            val bitmap=holder.itemView.pagerPhoto.drawable.toBitmap()

            //1. 定义路径 占位
            val saveUri=requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    ContentValues()
            )?:kotlin.run {
                Toast.makeText(requireContext(), "存储失败", Toast.LENGTH_SHORT).show()
                return@withContext
                   }//里面可以空也可以具体化
            //2.真正写入  use用好后会自动关闭流  90压缩率
            requireContext().contentResolver.openOutputStream(saveUri).use {
                if (bitmap.compress(Bitmap.CompressFormat.JPEG,90,it)==true){
                    MainScope().launch {  Toast.makeText(requireContext(),"存储成功",Toast.LENGTH_SHORT).show()}
                }else{
                    MainScope().launch {  Toast.makeText(requireContext(), "存储失败", Toast.LENGTH_SHORT).show()}
                }
            }
        }

    }


}