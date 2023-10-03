package com.example.myapplication

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage

class UpdatePicture {
    private val TAG = "FirebaseStorageManager"
    private val mStorageRef = FirebaseStorage.getInstance().reference
    private lateinit var mProgressDialog: ProgressDialog


    fun uploadImage(context: Context, imageFileUri: Uri, food: String, material: String, steps: String, id:String,foodType:String) {

        mProgressDialog = ProgressDialog(context)
        mProgressDialog.setMessage("Please wait, image being upload")
        mProgressDialog.show()

        val random:String?=getRandomString(3)
        val fileRef = mStorageRef.child("foods/${random}.png")

        val uploadTask = fileRef.putFile(imageFileUri)





        uploadTask.addOnSuccessListener {
            Log.e(TAG, "Image Upload success")
            mProgressDialog.dismiss()

            val uploadedURL = mStorageRef.child("foods/${random}.png").downloadUrl
            Log.e(TAG, "Uploaded $uploadedURL")

            fileRef.downloadUrl.addOnSuccessListener { uri->
                val downloadURL:String? = uri.toString()
                Log.e(TAG, "url: ${downloadURL.toString()}", )

                val intent = Intent(context, LoadUpdateRecepi::class.java)
                intent.putExtra("downloadUrl", downloadURL)
                intent.putExtra("foodName", food)
                intent.putExtra("material",material)
                intent.putExtra("steps", steps)
                intent.putExtra("id", id)
                intent.putExtra("foodType", foodType)

                context.startActivity(intent)
                Log.e(TAG, "firebase ${downloadURL.toString()}", )
                Log.e(TAG, "food ${food.toString()}", )
                Log.e(TAG, "material ${material.toString()}", )


            }


        }.addOnFailureListener {
            Log.e(TAG, "Image Upload fail")
            mProgressDialog.dismiss()
        }

    }



//        uploadTask.addOnSuccessListener {
//            Log.e(TAG, "Image Upload success")
//            mProgressDialog.dismiss()
//
//            val uploadedURL = mStorageRef.child("foods/${random}.png").downloadUrl
//            Log.e(TAG, "Uploaded ${uploadedURL.toString()}")
//
//            fileRef.downloadUrl.addOnSuccessListener { uri->
//                val downloadURL:String? = uri.toString()
//                Log.e(TAG, "url: ${downloadURL.toString()}", )
//
//                val intent = Intent(context, MainActivity::class.java)
//                intent.putExtra("downloadUrl", downloadURL)
//                context.startActivity(intent)
//                Log.e(TAG, "firebase ${downloadURL.toString()}", )
//            }
//
//
//        }.addOnFailureListener {
//            Log.e(TAG, "Image Upload fail")
//            mProgressDialog.dismiss()
//        }



    fun getRandomString(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }
}