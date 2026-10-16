package com.sahseh.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePicker
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val green = Color.rgb(0, 91, 65)
    private val darkGreen = Color.rgb(0, 57, 43)
    private val cream = Color.rgb(247, 249, 245)
    private val muted = Color.rgb(105, 123, 115)
    private lateinit var targetInput: EditText
    private lateinit var timePicker: TimePicker
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = cream
        window.navigationBarColor = cream
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        val prefs = getSharedPreferences("sahseh", 0)
        val page = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(cream)
        }
        val scroll = ScrollView(this)
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(18), dp(20), dp(24))
        }

        val top = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(18), dp(18), dp(18), dp(18))
            background = rounded(darkGreen, 24)
        }
        val brand = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; layoutParams = LinearLayout.LayoutParams(0, -2, 1f) }
        brand.addView(label("صحصح", 30, Color.WHITE, true))
        brand.addView(label("ابدأ يومك بنشاط ☀️", 14, Color.rgb(213, 235, 220), false))
        top.addView(brand)
        top.addView(TextView(this).apply {
            text = "◉"; textSize = 32f; gravity = Gravity.CENTER
            setTextColor(Color.rgb(255, 198, 62))
            background = rounded(Color.rgb(0, 112, 78), 18)
            layoutParams = LinearLayout.LayoutParams(dp(58), dp(58))
        })
        content.addView(top)

        content.addView(label("صباح الخير 👋", 23, darkGreen, true, 0, 22))
        content.addView(label("خلّينا نضمن إنك تقوم من أول رنّة.", 14, muted, false, 0, 4))

        val alarmCard = card()
        alarmCard.addView(label("⏰  المنبّه القادم", 17, darkGreen, true))
        timePicker = TimePicker(this).apply {
            setIs24HourView(true)
            layoutParams = LinearLayout.LayoutParams(-1, dp(150))
        }
        alarmCard.addView(timePicker)
        alarmCard.addView(label("شو الغرض اللي بدك تصوّره لتوقف المنبّه؟", 15, darkGreen, true, 0, 12))
        targetInput = EditText(this).apply {
            hint = "اكتب الاسم بالإنجليزية، مثل: cup"
            setText(prefs.getString("target", "cup"))
            textSize = 16f
            singleLine = true
            setPadding(dp(14), 0, dp(14), 0)
            background = rounded(Color.rgb(242, 246, 241), 14, Color.rgb(224, 232, 224))
        }
        alarmCard.addView(targetInput, LinearLayout.LayoutParams(-1, dp(52)))
        alarmCard.addView(label("نصيحة: اكتب cup للكوب أو book للكتاب لنتيجة أفضل.", 12, muted, false, 0, 8))
        val save = Button(this).apply {
            text = "حفظ المنبّه"
            textSize = 16f
            isAllCaps = false
            setTextColor(Color.WHITE)
            background = rounded(green, 16)
            setOnClickListener { saveAlarm() }
        }
        val saveParams = LinearLayout.LayoutParams(-1, dp(54))
        saveParams.topMargin = dp(14)
        alarmCard.addView(save, saveParams)
        content.addView(alarmCard)

        val infoCard = card()
        infoCard.addView(label("كيف بيشتغل صحصح؟", 18, darkGreen, true))
        infoCard.addView(infoRow("١", "رنّة المنبّه", "لما يجي الوقت، افتح شاشة التحدّي."))
        infoCard.addView(infoRow("٢", "صوّر الغرض", "وجّه الكاميرا نحو الشيء اللي اخترته."))
        infoCard.addView(infoRow("٣", "ابدأ يومك", "إذا تعرّف عليه التطبيق، بتنتهي المحاولة."))
        content.addView(infoCard)

        statusText = label("جاهز لضبط منبّهك 🌿", 13, muted, false)
        statusText.gravity = Gravity.CENTER
        statusText.setPadding(0, dp(14), 0, dp(8))
        content.addView(statusText)

        scroll.addView(content)
        page.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        val nav = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(dp(8), dp(10), dp(8), dp(10))
            background = rounded(Color.WHITE, 18, Color.rgb(235, 239, 233))
        }
        nav.addView(navItem("⌂", "الرئيسية", true), LinearLayout.LayoutParams(0, dp(52), 1f))
        nav.addView(navItem("◷", "المنبّهات", false), LinearLayout.LayoutParams(0, dp(52), 1f))
        nav.addView(navItem("⚙", "الإعدادات", false), LinearLayout.LayoutParams(0, dp(52), 1f))
        page.addView(nav)
        setContentView(page)

        val last = prefs.getLong("alarm_time", 0L)
        if (last > System.currentTimeMillis()) {
            statusText.text = "منبّهك محفوظ: " + SimpleDateFormat("HH:mm", Locale.getDefault()).format(last)
        }
    }

    private fun saveAlarm() {
        val word = targetInput.text.toString().trim()
        if (word.isEmpty()) {
            targetInput.error = "اكتب اسم الغرض بالإنجليزية"
            return
        }
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, timePicker.hour)
            set(Calendar.MINUTE, timePicker.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
        }
        val manager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pending = PendingIntent.getBroadcast(this, 44, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        try {
            if (Build.VERSION.SDK_INT >= 31 && !manager.canScheduleExactAlarms()) {
                startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
                statusText.text = "اسمح بصلاحية المنبّه الدقيق، ثم ارجع واضغط حفظ المنبّه."
                return
            }
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pending)
            getSharedPreferences("sahseh", 0).edit().putString("target", word).putLong("alarm_time", calendar.timeInMillis).apply()
            statusText.text = "تم الحفظ! المنبّه الساعة " + SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)
            Toast.makeText(this, "تم ضبط المنبّه 🌞", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            statusText.text = "تعذّر ضبط المنبّه. تأكّد من صلاحيات الجهاز."
            Toast.makeText(this, "تعذّر ضبط المنبّه", Toast.LENGTH_LONG).show()
        }
    }

    private fun card(): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dp(16), dp(18), dp(16), dp(18))
        background = rounded(Color.WHITE, 22, Color.rgb(233, 239, 232))
        val p = LinearLayout.LayoutParams(-1, -2)
        p.topMargin = dp(18)
        layoutParams = p
    }

    private fun infoRow(number: String, title: String, detail: String): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(12), 0, dp(4))
        }
        row.addView(TextView(this).apply {
            text = number; textSize = 17f; gravity = Gravity.CENTER
            setTextColor(Color.WHITE); background = rounded(green, 14)
            layoutParams = LinearLayout.LayoutParams(dp(38), dp(38))
        })
        val texts = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), 0, 0, 0)
        }
        texts.addView(label(title, 15, darkGreen, true))
        texts.addView(label(detail, 12, muted, false, 0, 2))
        row.addView(texts)
        return row
    }

    private fun navItem(symbol: String, title: String, selected: Boolean): View =
        LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            addView(TextView(this@MainActivity).apply {
                text = symbol; textSize = 22f; gravity = Gravity.CENTER
                setTextColor(if (selected) green else muted)
            })
            addView(TextView(this@MainActivity).apply {
                text = title; textSize = 11f; gravity = Gravity.CENTER
                setTextColor(if (selected) green else muted)
                if (selected) setTypeface(null, Typeface.BOLD)
            })
        }

    private fun label(textValue: String, size: Int, color: Int, bold: Boolean, left: Int = 0, top: Int = 0): TextView =
        TextView(this).apply {
            text = textValue
            textSize = size.toFloat()
            setTextColor(color)
            if (bold) setTypeface(null, Typeface.BOLD)
            textDirection = View.TEXT_DIRECTION_LOCALE
            val p = LinearLayout.LayoutParams(-1, -2)
            p.leftMargin = dp(left); p.topMargin = dp(top)
            layoutParams = p
        }

    private fun rounded(color: Int, radius: Int, stroke: Int? = null): GradientDrawable =
        GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radius).toFloat()
            if (stroke != null) setStroke(dp(1), stroke)
        }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
