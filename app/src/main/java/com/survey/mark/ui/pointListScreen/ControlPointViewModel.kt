package com.survey.mark.ui.pointListScreen

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ControlPointViewModel: ViewModel(){
   private val _state = MutableStateFlow(ControlPointState())
    val state = _state.asStateFlow()
}
