package com.sahseh.alarm
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
class ChallengeActivity:AppCompatActivity(){private val code=51;override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);val target=getSharedPreferences("sahseh",0).getString("target","cup")?: "cup";val l=LinearLayout(this).apply{orientation=1;gravity=Gravity.CENTER;setPadding(24,24,24,24)};l.addView(TextView(this).apply{text="يلا قوم! 📸\nصوّر: $target";textSize=26f;gravity=Gravity.CENTER});l.addView(Button(this).apply{text="افتح الكاميرا";setOnClickListener{try{startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE),code)}catch(e:Exception){Toast.makeText(this@ChallengeActivity,"تعذّر فتح الكاميرا",Toast.LENGTH_LONG).show()}}});setContentView(l)}
@Suppress("DEPRECATION") override fun onActivityResult(requestCode:Int,resultCode:Int,data:Intent?){super.onActivityResult(requestCode,resultCode,data);if(requestCode!=code||resultCode!=Activity.RESULT_OK)return;val b=data?.extras?.get("data") as? android.graphics.Bitmap?:return;val l=ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);l.process(InputImage.fromBitmap(b,0)).addOnSuccessListener{labels->val target=getSharedPreferences("sahseh",0).getString("target","cup")!!.lowercase();if(labels.any{it.text.lowercase().contains(target)||target.contains(it.text.lowercase())}){Toast.makeText(this,"أحسنت! 🌞",Toast.LENGTH_LONG).show();finish()}else AlertDialog.Builder(this).setTitle("جرّب مرة ثانية").setMessage("لم نتعرّف على الشيء المطلوب بوضوح. جرّب كلمة إنجليزية مثل cup أو book، وصوّر من قريب.").setPositiveButton("حسناً",null).show()}.addOnCompleteListener{l.close()}}}