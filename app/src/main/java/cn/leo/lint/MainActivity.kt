package cn.leo.lint

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val test = Test()
        val text = getString(R.string.hardcoded_text)
        val t = "硬编"
        Toast.makeText(this, "硬编码", Toast.LENGTH_SHORT).show()
        Toast.makeText(this, "硬编码1", Toast.LENGTH_SHORT).show()
    }
}