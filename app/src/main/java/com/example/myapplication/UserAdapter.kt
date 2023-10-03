package com.example.myapplication

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class UserAdapter : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    private var userList = mutableListOf<User>()
    private var onClickView: ((User)-> Unit)? = null
    private var onClickDelete:((User)-> Unit)?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val itemView = inflater.inflate(R.layout.user_item_holder, parent,false)
        return UserViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.setItem(user)

        holder.setOnClickView {
            onClickView?.invoke(it)
        }
        holder.setOnClickDelete {
            onClickDelete?.invoke(it)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun setItems(list: MutableList<User>){
        this.userList=list
        notifyDataSetChanged()
    }

    fun setOnClickView(callback: (User)-> Unit){
        this.onClickView = callback
    }

    fun setOnClickDelete(callback: (User) -> Unit){
        this.onClickDelete= callback
    }

    class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private var tvFoodName: TextView? =null
        private var tvMaterial: TextView? = null
        private var actionView: ImageView? = null
        private var actionDelete: ImageView? = null
        private var imgFood: ImageView?=null


        private var onClickView:((User)->Unit)? = null
        private var onClickDelete:((User)->Unit)?  =null

        fun setItem(data: User){
            tvFoodName = itemView.findViewById(R.id.tv_food_name)
            tvMaterial = itemView.findViewById(R.id.tv_material)
            actionView = itemView.findViewById(R.id.ic_view)
            actionDelete = itemView.findViewById(R.id.ic_delete)
            imgFood = itemView.findViewById<ImageView>(R.id.imageViewfd)
            if (data.foodPic=="null"){
                data.foodPic="https://firebasestorage.googleapis.com/v0/b/food-recipe-3ab30.appspot.com/o/Artboard%20%E2%80%93%201.jpg?alt=media&token=8c50d86f-8260-43e2-814b-7143f02e62d9&_gl=1*1weh6y9*_ga*MzI4Mjc3OTAwLjE2OTU2MzI1MzQ.*_ga_CW55HF8NVT*MTY5NjA4NTc5Ny4yMi4xLjE2OTYwODU4MTYuNDEuMC4w"
            }
            tvFoodName?.text = data.FoodName
            tvMaterial?.text = data.Material
            val imageUri: Uri = Uri.parse(data.foodPic)


            imgFood?.let { imageView ->
                // Load the image using Glide if imgFood is not null
                Glide.with(itemView.context)
                    .load(imageUri)
                    .into(imageView)
            }

            actionView?.setOnClickListener{
                onClickView?.invoke(data)
            }

            actionDelete?.setOnClickListener{
                onClickDelete?.invoke(data)
            }
        }
        fun setOnClickView(callback: (User)-> Unit){
            this.onClickView = callback
        }
        fun setOnClickDelete(callback: (User)->Unit){
            this.onClickDelete = callback
        }
    }
}