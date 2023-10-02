package com.example.myapplication

import FirebaseStorageManager
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.ActivityMainBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    private var firebaseDatabase: FirebaseDatabase? = null
    private var databaseReference: DatabaseReference?= null
    private var list = mutableListOf<User>()

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



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase?.getReference("data")


        getData()

        binding.btnSave.setOnClickListener{saveData()}
        binding.btnUpdate.setOnClickListener{updateData()}
        initUI()

        val foodPic = intent.getStringExtra("downloadUrl").toString()
        val imageUri: Uri = Uri.parse(foodPic)
        val food = intent.getStringExtra("foodName").toString()
        val steps = intent.getStringExtra("steps").toString()
        val material = intent.getStringExtra("material").toString()
        var tvFoodName:TextView = findViewById(R.id.edFoodName)
        var tvMaterial:TextView = findViewById(R.id.edMaterial)
        var tvSteps:TextView = findViewById(R.id.edMultiSteps)

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
            tvSteps?.text = material
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
        if (foodName.isNotEmpty() && material.isNotEmpty()) {
            val user = User(FoodName = foodName, Material = material, foodPic = foodPic, steps = steps)
            selectedID=intent.getStringExtra("id").toString()
            databaseReference?.child(selectedID.toString())?.setValue(user)

            Toast.makeText(this, "The Food Recipe has been update", Toast.LENGTH_SHORT).show()

        }else{

            Toast.makeText(this, "Please select the recipe before update", Toast.LENGTH_SHORT).show()
        }



    }

    private fun initRecyclerView(){
        adapter=UserAdapter()
        binding.apply {
            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
            recyclerView.adapter = adapter
        }
        adapter?.setOnClickView {
            binding.edFoodName.setText(it.FoodName.toString())
            binding.edMaterial.setText(it.Material.toString())
            binding.edMultiSteps.setText(it.steps.toString())
            val imageUri: Uri = Uri.parse(it.foodPic.toString())
            Glide.with(this)
                .load(imageUri)
                .into(imgPost)


            selectedID=it.id
          //  Toast.makeText(this,"Click Action View ${it.id}", Toast.LENGTH_SHORT).show()
        }

        adapter?.setOnClickDelete {
            selectedID=it.id
            databaseReference?.child(selectedID.orEmpty())?.removeValue()
        }

    }

    private fun saveData() {
        val foodName = binding.edFoodName.text.toString()
        val material = binding.edMaterial.text.toString()
        val steps = binding.edMultiSteps.text.toString()


        val foodPic = intent.getStringExtra("downloadUrl").toString()



        if (foodName.isNotEmpty() && material.isNotEmpty()) {

            if(steps.isEmpty()){
                Toast.makeText(this, "Please tell us the steps first", Toast.LENGTH_SHORT).show()
            }else{
                if (intent != null){
                    Log.e("saving", "saveData: ${foodPic.toString()}",)
                    val user = User(FoodName = foodName, Material = material, foodPic = foodPic, steps = steps)
                    Log.e("ooo", "before save name: foodpic  ${foodPic.toString()}",)

                    databaseReference?.child(getRandomString(5))?.setValue(user)
                    // Create a new User object and save it to Firebase
                    //  Log.e("ooo", "aftersavename:imguri ${imgURI.toString()}", )
                    // Assuming you have a databaseReference properly initialized
                    // Optionally, clear the EditText fields after saving data
                    binding.imageView.setImageDrawable(null)
                    binding.edFoodName.text.clear()
                    binding.edMaterial.text.clear()
                    binding.edMultiSteps.text.clear()
                    Toast.makeText(this, "The Food Recipe has been saved", Toast.LENGTH_SHORT).show()

                }else {
                    Toast.makeText(this, "Please Upload image first", Toast.LENGTH_SHORT).show()
                }
            }


        } else {
            Toast.makeText(this, "Please fill in the Food Name and Material", Toast.LENGTH_SHORT)
                .show()
        }

    }




    fun getRandomString(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }


    private fun getData(){
        databaseReference?.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
               // Log.e("ooooo", "onDataChange: $snapshot ")
                list.clear()
                for (ds in snapshot.children){
                    val id = ds.key
                    val foodName = ds.child("foodName").value.toString()
                    val material = ds.child("material").value.toString()
                    val foodPic = ds.child("foodPic").value.toString()
                    val steps = ds.child("steps").value.toString()


                    val user= User(id = id, FoodName= foodName, Material= material, foodPic=foodPic, steps = steps)
                    list.add(user)
                }
                Log.e("ooooo", "size${list.size} ")

                adapter?.setItems(list)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ooooo", "onCancelled: ${error.toException()}")
            }
        })
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
            if (imgURI != null) {
                FirebaseStorageManager().uploadImage(this, imgURI,edfoodText,edmaterial,steps, id)
            } else {
                Toast.makeText(this, "Please Select image first", Toast.LENGTH_SHORT).show()
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

//        if(imgURI == null){
//            Toast.makeText(this,"Please select image first", Toast.LENGTH_SHORT).show()
//        }else{
//            FirebaseStorageManager().uploadImage(this,imgURI)
//            Log.e("ooo", "before save name: foodpic  ${foodPic.toString()}", )
//            databaseReference?.child(getRandomString(5))?.setValue(user)
//            if (foodName.isNotEmpty() && material.isNotEmpty() ) {
//                // Create a new User object and save it to Firebase
//                Log.e("ooo", "aftersavename:imguri ${imgURI.toString()}", )
//                // Assuming you have a databaseReference properly initialized
//                // Optionally, clear the EditText fields after saving data
//                binding.edFoodName.text.clear()
//                binding.edMaterial.text.clear()
//            }
//
//        }    fun restartActivity() {
//        val intent = Intent(this, MainActivity::class.java)
//        finish()
//        startActivity(intent)
//    }
