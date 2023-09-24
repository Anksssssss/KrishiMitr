package com.example.krishimitr

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.postDelayed
import com.example.krishimitr.ai.SolutionsActivity
import com.example.krishimitr.databinding.ActivityMainBinding
import com.example.krishimitr.db.AppPreffManager
import com.example.krishimitr.presentation.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var mClassifier: Classifier
    private lateinit var mBitmap: Bitmap
    private val mCameraRequestCode = 0
    private val mGalleryRequestCode = 2
    private lateinit var sharePref: AppPreffManager

    private val mInputSize = 224
    private val mModelPath = "plant_disease_model.tflite"
    private val mLabelPath = "plant_labels.txt"
    private val mSamplePath = "automn.jpg"


//    private var pname: String? = ""
//    private var pSymptoms: String? = ""
//    private var pManage: String? = ""




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(binding.root)

        sharePref = AppPreffManager(this@MainActivity)
        auth = FirebaseAuth.getInstance()
        binding.SignOut.setOnClickListener {
            auth.signOut()
            Intent(this, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }.also { startActivity(it) }
        }

        mClassifier = Classifier(assets,mModelPath,mLabelPath,mInputSize)

        resources.assets.open(mSamplePath).use{
            mBitmap = BitmapFactory.decodeStream(it)
            mBitmap = Bitmap.createScaledBitmap(mBitmap,mInputSize,mInputSize,false)
            binding.imgCrop.setImageBitmap(mBitmap)
        }

        binding.btnCamera.setOnClickListener {
            val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(callCameraIntent,mCameraRequestCode)
        }

        binding.btnGallery.setOnClickListener {
            val callGalleryIntent = Intent(Intent.ACTION_PICK)
            callGalleryIntent.type = "image/*"
            startActivityForResult(callGalleryIntent,mGalleryRequestCode)
        }

        binding.btnDetect.setOnClickListener {
            val progressDialog = ProgressDialog(this@MainActivity)
            progressDialog.setTitle("Please Wait")
            progressDialog.setMessage("Processing")
            progressDialog.show()
            val handler = Handler()
            handler.postDelayed(Runnable {progressDialog.dismiss()
                val results = mClassifier.recognizeImage(mBitmap).firstOrNull()
                binding.resultTV.text = results?.title+"\n Confidence:"+results?.confidence!!.toFloat()*100
                val intent = Intent(this@MainActivity,SolutionsActivity::class.java)
                intent.putExtra("Disease",results?.title.toString())
                startActivity(intent)
            },2000)
        }
        
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == mCameraRequestCode){
            if(resultCode == Activity.RESULT_OK && data != null){
                mBitmap = data.extras!!.get("data") as Bitmap
                mBitmap = scaleImage(mBitmap)
                binding.imgCrop.setImageBitmap(mBitmap)
                binding.resultTV.text = "Your Photo is set now"
            }else{
                Toast.makeText(this,"Camera Cancel..",Toast.LENGTH_SHORT).show()
            }
        }else if(requestCode === mGalleryRequestCode){
            if(data!=null){
                val uri = data.data
                try{
                    mBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,uri)
                }catch (e:IOException){
                    e.printStackTrace()
                }
                mBitmap = scaleImage(mBitmap)
                binding.imgCrop.setImageBitmap(mBitmap)
                binding.resultTV.text = "Your Photo is set now"
            }
        }
    }
    private fun scaleImage(bitmap: Bitmap):Bitmap{
        val originalWidth  = bitmap!!.width
        val originalHEight  = bitmap!!.height
        val scaleWidth = mInputSize.toFloat()
        val scaleHeight = mInputSize.toFloat()
        val matrix = Matrix()
        matrix.postScale(scaleWidth / originalWidth, scaleHeight / originalHEight)
        return Bitmap.createBitmap(bitmap,0,0,originalWidth,originalHEight,matrix,true)
    }
}
