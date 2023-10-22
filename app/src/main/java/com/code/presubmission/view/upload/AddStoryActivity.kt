package com.code.presubmission.view.upload

import android.app.ProgressDialog.show
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.code.presubmission.R
import com.code.presubmission.data.pref.ResultState
import com.code.presubmission.databinding.ActivityAddStoryBinding
import com.code.presubmission.util.getImageUri
import com.code.presubmission.util.reduceFileImage
import com.code.presubmission.util.uriToFile
import com.code.presubmission.view.ViewModelFactory
import com.code.presubmission.view.detail.DetailViewModel
import com.code.presubmission.view.main.MainActivity
import com.code.presubmission.view.welcome.WelcomeActivity

class AddStoryActivity : AppCompatActivity() {
    private val uploadViewModel by viewModels<UploadViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var imageUri: Uri? = null
    private lateinit var binding: ActivityAddStoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivGallery.setOnClickListener{
            intentGallery()
        }

        binding.ivCamera.setOnClickListener{
            openCamera()
        }

        uploadViewModel.getSession().observe(this){ result ->
            if(!result.isLogin){
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }else{
                binding.btnUpload.setOnClickListener {
                    Log.i("cektoken", "onCreate: ${result.token}")
                    post(result.token)
                }
            }
        }
    }

    private val openGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            show()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private val openCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ){
        if(it){
            show()
        }
    }

     fun show() {
        imageUri.let {
            binding.ivPhoto.setImageURI(it)
        }
    }

    fun intentGallery(){
        openGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    fun openCamera(){
        imageUri = getImageUri(this)
        openCamera.launch(imageUri)
    }

    fun post(token:String){
        imageUri?.let {uri ->
            val file = uri.let { uriToFile(it, this).reduceFileImage() }
            val desc = binding.edtStory.text.toString()

            uploadViewModel.upload(token, file, desc).observe(this){result ->
                if(result != null){
                    when(result){
                        is ResultState.Success -> {
                            result.data.message.let {
                                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                            }
                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }
                        is ResultState.Error -> {
                            Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                        }
                        else -> {}
                    }
                }
            }

        } ?: Toast.makeText(this, "Gambar masih kosong", Toast.LENGTH_SHORT).show()
    }

}