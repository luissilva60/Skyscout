package com.example.skyscout

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.skyscout.data.models.UserSession
import com.example.skyscout.ui.screens.MainActivity2
import com.example.skyscout.ui.theme.SkyScoutTheme
import com.example.skyscout.ui.viewmodel.DBViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {

    private val dbViewModel: DBViewModel by viewModels()

    private var isRegisterScreen by mutableStateOf(false) // State to toggle between login and register screens

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SkyScoutTheme {
                if (isRegisterScreen) {
                    RegisterScreen(
                        onRegisterClick = { email, password ->
                            // Handle the registration logic here (call ViewModel to register the user)
                            dbViewModel.createUser(email, password)
                        },
                        onBackToLoginClick = { isRegisterScreen = false }
                    )
                } else {
                    var email by remember { mutableStateOf("") }
                    var password by remember { mutableStateOf("") }
                    var errorMessage by remember { mutableStateOf<String?>(null) }

                    val loginResponse by dbViewModel.loginResponse.collectAsState()
                    val error by dbViewModel.errorMessage.collectAsState()

                    LaunchedEffect(loginResponse) {
                        loginResponse?.let {
                            // Save user globally and navigate on successful login
                            UserSession.currentUser = it.user
                            UserSession.favorites = it.favorites
                            navigateToMainActivity2()
                        }
                    }

                    LaunchedEffect(error) {
                        errorMessage = error
                    }

                    Scaffold { innerPadding ->
                        LoginScreen(
                            modifier = Modifier.padding(innerPadding),
                            email = email,
                            onEmailChange = { email = it },
                            password = password,
                            onPasswordChange = { password = it },
                            onLoginClick = {
                                dbViewModel.loginUser(email, password)
                            },
                            errorMessage = errorMessage,
                            onRegisterClick = { isRegisterScreen = true } // Switch to Register Screen
                        )
                    }
                }
            }
        }
    }

    private fun navigateToMainActivity2() {
        val intent = Intent(this, MainActivity2::class.java)
        startActivity(intent)
    }
}

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    errorMessage: String?,
    onRegisterClick: () -> Unit // Added onRegisterClick callback
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF87CEEB))
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 64.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(350.dp)
                    .padding(bottom = 16.dp)
            )

            TextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            TextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                visualTransformation = PasswordVisualTransformation()
            )

            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text("Login")
            }

            Button(
                onClick = onRegisterClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Register")
            }

            errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}
@Composable
fun RegisterScreen(
    onRegisterClick: (String, String) -> Unit,
    onBackToLoginClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current // Get context for Toast

    // Boolean flag to check whether registration is successful
    var registrationSuccessful by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF87CEEB))
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 64.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(350.dp)
                    .padding(bottom = 16.dp)
            )

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                visualTransformation = PasswordVisualTransformation()
            )

            TextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                visualTransformation = PasswordVisualTransformation()
            )

            Button(
                onClick = {
                    if (password == confirmPassword) {
                        onRegisterClick(email, password) // Proceed with registration
                        registrationSuccessful = true // Indicate registration is successful
                    } else {
                        errorMessage = "Passwords do not match"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text("Register")
            }

            Button(
                onClick = onBackToLoginClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back to Login")
            }

            errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }

        // Show Toast after successful registration
        if (registrationSuccessful) {
            Toast.makeText(context, "Account created successfully!", Toast.LENGTH_SHORT).show()
            registrationSuccessful = false // Reset flag after showing the Toast
            onBackToLoginClick() // Go back to the login screen after successful registration
        }
    }
}