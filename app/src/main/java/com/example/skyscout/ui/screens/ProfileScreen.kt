package com.example.skyscout.ui.screens

import android.content.Intent
import androidx.compose.ui.platform.LocalContext

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skyscout.MainActivity
import com.example.skyscout.R
import com.example.skyscout.data.models.UserSession
import com.example.skyscout.ui.viewmodel.DBViewModel
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun ProfileScreen(
    dbViewModel: DBViewModel = viewModel() // ViewModel to handle logout functionality
) {
    // Get the current user's email from the session
    val email: String = UserSession.currentUser?.userEmail ?: "No email found"

    // Get the current context for navigation
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF87CEEB)) // Light blue background
            .padding(16.dp),
        contentAlignment = Alignment.Center // Center the entire content
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Image
            Image(
                painter = painterResource(id = R.drawable.man), // Replace with your profile image resource
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.Gray), // Placeholder background
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            // User Email
            Text(
                text = email, // Display dynamic user email
                fontSize = 16.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Logout Button
            Button(
                onClick = {
                    // Call the logout function in the ViewModel to clear the session and navigate
                    dbViewModel.onLogoutClick(context)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text(text = "Logout", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}
