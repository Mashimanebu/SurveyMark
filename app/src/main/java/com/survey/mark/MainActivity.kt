package com.survey.mark

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.survey.mark.routing.SurveyMarkApp
import com.survey.mark.ui.home.HomeScreen
import com.survey.mark.ui.theme.SurveyMarkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SurveyMarkApp()
        }
    }
}



