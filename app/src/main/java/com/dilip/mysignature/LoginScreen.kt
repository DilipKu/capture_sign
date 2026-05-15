package com.dilip.mysignature

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.painterResource

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(onLoginSuccess = {})
}

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    val sageGreen = Color(0xFF869D89)
    val lightGray = Color(0xFFF1F1F1)
    
    var email by remember { mutableStateOf("maildilip45@gmail.com") }
    var password by remember { mutableStateOf("dilip45@#") }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(value = false) }
    var isLoginTab by remember { mutableStateOf(true) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Header Section
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 40.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Go ahead and set up\nyour account",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 36.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Sign in-up to enjoy the best managing experience",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }

        // White Card Section
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Login/Register Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(lightGray, RoundedCornerShape(28.dp))
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(24.dp))
                            .background(if (isLoginTab) Color.White else Color.Transparent)
                            .clickable { isLoginTab = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Login",
                            fontWeight = FontWeight.Bold,
                            color = if (isLoginTab) Color.Black else Color.Gray
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(24.dp))
                            .background(if (!isLoginTab) Color.White else Color.Transparent)
                            .clickable { isLoginTab = false },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Register",
                            fontWeight = FontWeight.Bold,
                            color = if (!isLoginTab) Color.Black else Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Input Fields
                if (!isLoginTab) {
                    CustomInputField(
                        label = "Full Name",
                        value = name,
                        onValueChange = { name = it },
                        icon = Icons.Default.Person
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    CustomInputField(
                        label = "Phone Number",
                        value = phone,
                        onValueChange = { phone = it },
                        icon = Icons.Default.Phone
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                CustomInputField(
                    label = "Email Address",
                    value = email,
                    onValueChange = { email = it },
                    icon = Icons.Default.Email
                )

                Spacer(modifier = Modifier.height(20.dp))

                CustomInputField(
                    label = "Password",
                    value = password,
                    onValueChange = { password = it },
                    icon = Icons.Default.Lock,
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Remember Me & Forgot Password
                if (isLoginTab) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = rememberMe,
                                onCheckedChange = { rememberMe = it },
                                colors = CheckboxDefaults.colors(checkedColor = sageGreen)
                            )
                            Text(text = "Remember me", fontSize = 14.sp, color = Color.Black)
                        }
                        val forgotText = "Forgot Password?"
                        Text(
                            text = forgotText,
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.clickable {
                                Toast.makeText(
                                    context,
                                    "$forgotText is in progress",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(if (isLoginTab) 32.dp else 16.dp))

                // Action Button (Login or Register)
                Button(
                    onClick = { 
                        if (isLoginTab) {
                            onLoginSuccess() 
                        } else {
                            Toast.makeText(context, "Registration logic later", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = sageGreen)
                ) {
                    Text(
                        text = if (isLoginTab) "Login" else "Register", 
                        color = Color.White, 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Divider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(modifier = Modifier.weight(1f), color = Color.LightGray)
                    Text(
                        text = "Or login with",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                    Divider(modifier = Modifier.weight(1f), color = Color.LightGray)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Social Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SocialButton(
                        text = "Google",
                        iconRes = R.drawable.ic_google,
                        modifier = Modifier.weight(1f)
                    )
                    SocialButton(
                        text = "Facebook",
                        iconRes = R.drawable.ic_facebook,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun CustomInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    isPassword: Boolean = false
) {
    var passwordVisible by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF869D89), // Matching icon color from image
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = label, fontSize = 12.sp, color = Color.Gray)
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    ),
                    visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
                    singleLine = true
                )
            }
            if (isPassword) {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun SocialButton(text: String, iconRes: Int, modifier: Modifier = Modifier) {

    val context = LocalContext.current
    OutlinedButton(
        onClick = {
            Toast.makeText(
                context,
                "$text button clicked",
                Toast.LENGTH_SHORT
            ).show()
                  },
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text, color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}
