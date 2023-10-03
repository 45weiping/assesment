package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.ActivityLoadUpdateRecepiBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class LoadUpdateRecepi : AppCompatActivity() {
    private lateinit var binding: ActivityLoadUpdateRecepiBinding
    private var firebaseDatabase: FirebaseDatabase? = null
    private var databaseReference: DatabaseReference?= null
    private var selectedID:String?= null

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
    private val btnBack: AppCompatButton by lazy {
        findViewById(R.id.btn_back)
    }
    private val spinnerFoodType: Spinner by lazy {
        findViewById(R.id.foodType)
    }
    private var foodtype:String?= null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoadUpdateRecepiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase?.getReference("data")

        SpinnerLoad()
        getData()
        initUI()
       // setData()

        binding.btnUpdate.setOnClickListener{updateData()}
        binding.btnBack.setOnClickListener{navigateBack()}

            //  Toast.makeText(this,"Click Action View ${it.id}", Toast.LENGTH_SHORT).show()
    }
    private fun navigateBack(){
        val intent = Intent(this, RecipeList::class.java)
        startActivity(intent)
    }

    private fun getData(){
        val foodPic = intent.getStringExtra("downloadUrl").toString()
        val imageUri: Uri = Uri.parse(foodPic)
        val FireBasefoodType = intent.getStringExtra("foodType").toString()
        Log.e("ooooo", "open loadupadte ")
        selectedID=intent.getStringExtra("id")
        binding.edFoodName.setText(intent.getStringExtra("foodName"))
        binding.edMaterial.setText(intent.getStringExtra("material"))
        binding.edMultiSteps.setText(intent.getStringExtra("steps"))
        when (FireBasefoodType) {
            "Asian" -> spinnerFoodType.setSelection(1)
            "Western" -> spinnerFoodType.setSelection(2)
            "Korean" -> spinnerFoodType.setSelection(3)
            "Japanese" -> spinnerFoodType.setSelection(4)
            else -> spinnerFoodType.setSelection(0)
        }
        Log.e("ooooo", "foodtype:${foodtype} ")
        Glide.with(this)
            .load(imageUri)
            .into(imgPost)
    }
    private fun initUI() {
        btnSelectImage.setOnClickListener {
            Log.e("photo", "initUI: hihihiclicked",)
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()

        }
        btnUpload.setOnClickListener {
            val id = selectedID.toString()
            val imgURI = btnUpload.tag as Uri?
            val edfoodText = binding.edFoodName.text.toString()
            val edmaterial = binding.edMaterial.text.toString()
            val steps =binding.edMultiSteps.text.toString()
            val firebaseFoodType = foodtype.toString()
            if (imgURI != null) {

                UpdatePicture().uploadImage(this, imgURI,edfoodText,edmaterial,steps, id, firebaseFoodType)


            } else {
                Toast.makeText(this, "Please Select image first", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun setData(){
        val foodPic = intent.getStringExtra("downloadUrl").toString()
        val imageUri: Uri = Uri.parse(foodPic)
        val food = intent.getStringExtra("foodName").toString()
        val steps = intent.getStringExtra("steps").toString()
        val material = intent.getStringExtra("material").toString()

        val firebaseFoodType = intent.getStringExtra("foodType").toString()
        var tvFoodName: TextView = findViewById(R.id.edFoodName)
        var tvMaterial: TextView = findViewById(R.id.edMaterial)
        var tvSteps: TextView = findViewById(R.id.edMultiSteps)

        if(food=="null"){
            tvFoodName?.text=""
        }else{
            tvFoodName?.text = food
        }
        if(material=="null"){
            tvMaterial?.text = ""
        }else{
            tvMaterial?.text = material
        }
        if(steps=="null"){
            tvSteps?.text = ""
        }else{
            tvSteps?.text = steps
        }
        if(firebaseFoodType=="null"){
            Log.e("000", "onItemSelected: ${foodtype}", )
            spinnerFoodType.setSelection(0)
        }else{
            //"Asian", "Western", "Korean", "Japanese"
            Log.e("000", "onItemSelected: ${intent.getStringExtra("foodType")}", )
            when (firebaseFoodType) {
                "Asian" -> spinnerFoodType.setSelection(1)
                "Western" -> spinnerFoodType.setSelection(2)
                "Korean" -> spinnerFoodType.setSelection(3)
                "Japanese" -> spinnerFoodType.setSelection(4)
                else -> println("Value is something else")
            }

        }


        Glide.with(this)
            .load(imageUri)
            .into(imgPost)

    }

    private fun updateData(){
        val foodName = binding.edFoodName.text.toString()
        val material = binding.edMaterial.text.toString()
        val foodPic = intent.getStringExtra("downloadUrl").toString()
        val steps = binding.edMultiSteps.text.toString()
        val firebaseFoodType = foodtype.toString()
        if (foodName.isNotEmpty() && material.isNotEmpty()) {
            val user = User(FoodName = foodName, Material = material, foodPic = foodPic, steps = steps,foodType = firebaseFoodType)
            selectedID=intent.getStringExtra("id").toString()
            databaseReference?.child(selectedID.toString())?.setValue(user)
            navigateBack()
            Toast.makeText(this, "The Food Recipe has been update", Toast.LENGTH_SHORT).show()

        }else{

            Toast.makeText(this, "Please select the recipe before update", Toast.LENGTH_SHORT).show()
        }



    }
    private fun SpinnerLoad(){
        val options = arrayOf("Select Food Type", "Asian", "Western", "Korean", "Japanese")
        spinnerFoodType.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,options)
        spinnerFoodType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                foodtype=options.get(position)
                Log.e("000", "onItemSelected: ${foodtype}", )
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                foodtype="hi"
            }

        }
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