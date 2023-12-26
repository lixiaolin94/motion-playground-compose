package work.xiaolin.motionplayground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import work.xiaolin.motionplayground.ui.MotionPlaygroundApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MotionPlaygroundApp()
        }
    }
}