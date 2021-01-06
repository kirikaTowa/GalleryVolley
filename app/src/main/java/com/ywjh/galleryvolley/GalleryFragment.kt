package com.ywjh.galleryvolley

import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.fragment_gallery.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GalleryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GalleryFragment : Fragment() {
    private lateinit var galleryViewModel: GalleryViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu,menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.swipeIndicator -> {
                swiperefreshlayout.isRefreshing = true
                Handler().postDelayed(Runnable {  galleryViewModel.fetchData()},1000)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val galleryAdapter=GalleryAdapter()
        setHasOptionsMenu(true)
        recycleView.apply{
            adapter=galleryAdapter
            //layoutManager= GridLayoutManager(requireContext(),2)//两列
            layoutManager= StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)//两列

        }


        //创建一个观察
        galleryViewModel=
            ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory(requireActivity().application)).get(GalleryViewModel::class.java)
        //创建一个观察 提交装配一下
        galleryViewModel.photoListLive.observe(this, Observer {
            galleryAdapter.submitList(it)
            //接收到数据就关闭刷新栏
            swiperefreshlayout.isRefreshing = false
        })
        //如果是空的 直接加载内容 不用手动拉一下
        galleryViewModel.photoListLive.value?:galleryViewModel.fetchData()
        //设置下拉获取数据
        swiperefreshlayout.setOnRefreshListener {
            galleryViewModel.fetchData()
        }
    }
}