package com.example.flutter_mapquiz

import android.content.Intent
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity: FlutterActivity() {
    private val CHANNEL = "com.flutter.mapquiz/mapquiz"

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler {
            // Note: this method is invoked on the main thread.
            call, result ->
            if (call.method == "Kesfet") {
                val intent = Intent(this, GlobeActivity::class.java)
                startActivity(intent)
                this.finish()
                result.success("Successful!")
            } else {
                result.notImplemented()
            }
        }
    }


}
