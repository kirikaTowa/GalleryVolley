package com.ywjh.galleryvolley

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.gallery_cell.view.*

class GalleryAdapter : ListAdapter<PhotoItem, GalleryAdapter.MyViewHolder>(DIFFCALLBACK) {

    //初始化
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        //创建holder
        val holder = MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.gallery_cell, parent, false))
        holder.itemView.setOnClickListener {

            Bundle().apply {
                putParcelableArrayList("PHOTO_LIST", ArrayList(currentList))
                putInt("PHOTO_POSITION", holder.adapterPosition)
                holder.itemView.findNavController()
                    .navigate(R.id.action_galleryFragment_to_pagerPhotoFragment, this)
            }
        }


        return holder
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val photoItem=getItem(position)
        //使用with语句
        with(holder.itemView) {
            shimmerLayoutCell.apply {
                setShimmerColor(0x55FFFFFF)
                setShimmerAngle(0)//闪动角度
                startShimmerAnimation()
            }
            textViewUser.text=photoItem.photoUser
            textViewLikes.text=photoItem.photoLikes.toString()
            textViewFavorites.text=photoItem.photoFavorites.toString()
            imageView.layoutParams.height=photoItem.photoHeight
        }

        Glide.with(holder.itemView)
            .load(getItem(position).previewUrl)//photo对象
            .placeholder(R.drawable.photo_placeholder)
            .listener(object : RequestListener<Drawable> {
                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false.also { holder.itemView.shimmerLayoutCell?.stopShimmerAnimation() }//判断空， 图片未加载完全就切走 但listenrer还在监听
                }

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false//都return false 不然不显图
                }//监听加载完成后停止shimmer
            }
            )
            .into(holder.itemView.imageView)//加载上去
    }

    object DIFFCALLBACK : DiffUtil.ItemCallback<PhotoItem>() {
        override fun areItemsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem === newItem //判断是否为同一个对象 三等于号
        }

        override fun areContentsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem.photoId == newItem.photoId//判断内容是否相同

        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}
