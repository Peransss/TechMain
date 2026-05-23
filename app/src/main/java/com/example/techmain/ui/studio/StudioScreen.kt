package com.example.techmain.ui.studio

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.techmain.ui.theme.NeonHackerBackground
import com.example.techmain.ui.theme.NeonHackerBorder
import com.example.techmain.ui.theme.NeonHackerSecondary

@Composable
fun StudioScreen(viewModel: CreatorViewModel = viewModel(), onNavigateToWizard: () -> Unit) {
    val state by viewModel.state.collectAsState()
    Scaffold(
        containerColor = NeonHackerBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToWizard,
                containerColor = NeonHackerSecondary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Kuis", tint = Color.Black)
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
            items(state.myQuizzes) { quiz ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .border(1.dp, NeonHackerBorder, MaterialTheme.shapes.medium)
                        .padding(16.dp)
                ) {
                    Text(text = quiz.title, color = Color.White)
                }
            }
        }
    }
}
