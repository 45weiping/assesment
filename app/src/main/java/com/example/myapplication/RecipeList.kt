package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivityRecipeListBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class RecipeList : AppCompatActivity() {


    private lateinit var binding: ActivityRecipeListBinding
    private var firebaseDatabase: FirebaseDatabase? = null
    private var databaseReference: DatabaseReference?= null
    private var list = mutableListOf<User>()
    private var Asian = mutableListOf<User>()
    private var Western = mutableListOf<User>()
    private var Korean = mutableListOf<User>()
    private var Japanese = mutableListOf<User>()
    private var adapter:UserAdapter?= null
    private var selectedID:String?= null

    private val imageView: ImageView? = null
    private val uploadButton: Button? = null

    private val storage = FirebaseStorage.getInstance()

    private val btnSelectImage: AppCompatButton by lazy {
        findViewById(R.id.selectPhotoButton)
    }

    private val imgPost: AppCompatImageView by lazy {
        findViewById(R.id.imageView)
    }

    private val btnUpload: AppCompatButton by lazy {
        findViewById(R.id.uploadButton)
    }
    private val spinnerFoodType: Spinner by lazy {
        findViewById(R.id.foodType)
    }
    private var foodtype:String?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()
        binding.addNewRecipe.setOnClickListener{navigateBack()}
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase?.getReference("data")


        getData()

    }


    private fun navigateBack(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
    private fun initRecyclerView(){
        adapter=UserAdapter()
        binding.apply {
            recyclerView.layoutManager = LinearLayoutManager(this@RecipeList)
            recyclerView.adapter = adapter
        }
        adapter?.setOnClickView {

            val intent = Intent(this, LoadUpdateRecepi::class.java)
            val food =it.FoodName.toString()
            val material = it.Material.toString()
            val steps = it.steps.toString()
            val downloadURL = it.foodPic.toString()
            val id = it.id.toString()
            val foodType = it.foodType.toString()
            intent.putExtra("downloadUrl", downloadURL)
            intent.putExtra("foodName", food)
            intent.putExtra("material",material)
            intent.putExtra("steps", steps)
            intent.putExtra("id", id)
            intent.putExtra("foodType", foodType)

            Log.e("ooooo", "foodtype:${foodType} ")

            startActivity(intent)

        }

        adapter?.setOnClickDelete {
            selectedID=it.id
            databaseReference?.child(selectedID.orEmpty())?.removeValue()
        }
        SpinnerLoad()

    }




    private fun SpinnerLoad(){
        val options = arrayOf("All", "Asian", "Western", "Korean", "Japanese")


        spinnerFoodType.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,options)
        spinnerFoodType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                foodtype=options.get(position)

                Log.e("000", "onItemSelected: ${foodtype}", )
                getData()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                foodtype="hi"
            }

        }

    }

    private fun getData(){
        databaseReference?.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                Asian.clear()
                Korean.clear()
                Japanese.clear()
                Western.clear()
                for (ds in snapshot.children){
                    val id = ds.key
                    val foodName = ds.child("foodName").value.toString()
                    val material = ds.child("material").value.toString()
                    val foodPic = ds.child("foodPic").value.toString()
                    val steps = ds.child("steps").value.toString()
                    val foodType = ds.child("foodType").value.toString()


                    val user= User(id = id, FoodName= foodName, Material= material, foodPic=foodPic, steps = steps, foodType = foodType)
                    list.add(user)
//                    if (foodType=="Asian"){
//                        Asian.add(user)
//                    }

                    when (foodType) {
                        "Asian" -> Asian.add(user)
                        "Western" -> Western.add(user)
                        "Korean" -> Korean.add(user)
                        "Japanese" -> Japanese.add(user)
                        else -> println("Value is something else")
                    }

                }
                Log.e("ooooo", "all size${list.size} ")
                Log.e("ooooo", "Asian size${Asian.size} ")
//                val filteredAsian = list.filter { it.foodType == "Asian" }
//                val filteredWestern = list.filter { it.foodType == "Western" }
//                val filteredKorean = list.filter { it.foodType == "Korean" }
//                val filteredJapanese = list.filter { it.foodType == "Japanese" }
                when (foodtype) {
                    "All" -> adapter?.setItems(list)
                    "Asian" -> adapter?.setItems(Asian)
                    "Western" -> adapter?.setItems(Western)
                    "Korean" -> adapter?.setItems(Korean)
                    "Japanese" -> adapter?.setItems(Japanese)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ooooo", "onCancelled: ${error.toException()}")
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            val uri: Uri = data?.data!!

            // Use Uri object instead of File to avoid storage permissions
            imgPost.setImageURI(uri)
            btnUpload.setTag(uri)
            Log.e("ooo", "onActivityResult12: ${uri.toString()}", )
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }
}


//            binding.edFoodName.setText(it.FoodName.toString())
//            binding.edMaterial.setText(it.Material.toString())
//            binding.edMultiSteps.setText(it.steps.toString())
//            val imageUri: Uri = Uri.parse(it.foodPic.toString())
//            Glide.with(this)
//                .load(imageUri)
//                .into(imgPost)
//
//
//            selectedID=it.id
//            //  Toast.makeText(this,"Click Action View ${it.id}", Toast.LENGTH_SHORT).show()
