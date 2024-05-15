package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DisplayProfile()

                }
            }
        }
    }
}

@Composable
fun DisplayProfile(modifier: Modifier = Modifier) {
    val auth = Firebase.auth
    val localContext = (LocalContext.current as? Activity)
    val name = remember { mutableStateOf(auth.currentUser?.displayName) }
    val email = remember { mutableStateOf(auth.currentUser?.email) }
    var passwordTitle by remember { mutableStateOf(R.string.password) }
    var passwordPlaceHolder by remember { mutableStateOf(R.string.password_hidden) }
    var displayChangePassword by remember { mutableStateOf(false) }
    val newName = remember { mutableStateOf("") }
    var readOnlyName by remember { mutableStateOf(true) }
    var newNameError by remember { mutableStateOf(false) }
    val newEmail = remember { mutableStateOf("") }
    val oldPassword = remember { mutableStateOf("") }
    var readOnlyPassword by remember { mutableStateOf(true) }
    var oldPasswordError by remember { mutableStateOf(false) }
    val newPassword = remember { mutableStateOf("") }
    var newPasswordError by remember { mutableStateOf(false) }
    val confirmNewPassword = remember { mutableStateOf("") }
    var confirmNewPasswordError by remember { mutableStateOf(false) }
    var displayDeleteAccount by remember { mutableStateOf(false) }
    newName.value = name.value.toString()
    newEmail.value = email.value.toString()

    Box(
        modifier = Modifier
            .fillMaxSize() // Fills the entire screen
            .paint( // Sets the background
                painterResource(id = R.drawable.bg_5),
                contentScale = ContentScale.FillBounds // Fills the box with the image
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(start = 10.dp, top = 10.dp, end = 0.dp, bottom = 60.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Start)
                    .padding(start = 30.dp, top = 10.dp, end = 30.dp, bottom = 12.dp),
            ) {
                Text(
                    text = stringResource(id = R.string.name),
                    modifier = modifier.padding(0.dp),
                    fontSize = 19.sp,
                    color = colorResource(id = R.color.aestheticBlue),
                )
                DisplayTextInputField(
                    input = newName,
                    placeHolderTextID = R.string.name,
                    editButton = true,
                    readOnly = readOnlyName,
                    isError = newNameError,
                    editContentDescription = R.string.edit_name,
                    clearContentDescription = R.string.clear_name,
                    onDoneClickAction = {
                        if (!readOnlyName) {
                            val checkNewNameResult =
                                checkNewName(name.value.toString(), newName.value)
                            when (checkNewNameResult) {
                                "passed" -> {
                                    auth.currentUser!!.updateProfile(
                                        userProfileChangeRequest {
                                            displayName = newName.value
                                        }
                                    ).addOnSuccessListener {
                                        Toast.makeText(
                                            localContext,
                                            "Name Updated!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        name.value = auth.currentUser?.displayName
                                        newNameError = false
                                        readOnlyName = true
                                    }.addOnFailureListener {
                                        Toast.makeText(
                                            localContext,
                                            "Failed to update name",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        newNameError = false
                                    }
                                }

                                "old name and new name match" -> {
                                    Toast.makeText(
                                        localContext,
                                        "The name is unchanged",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    newNameError = false
                                    readOnlyName = true
                                }

                                else -> {
                                    Toast.makeText(
                                        localContext,
                                        checkNewNameResult,
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    newNameError = true
                                    readOnlyName = false
                                }
                            }
                        } else {
                            newNameError = false
                            readOnlyName = false
                        }
                    }
                )
            }
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .align(Alignment.Start)
                    .padding(30.dp, 12.dp),
            ) {
                Text(
                    text = stringResource(id = R.string.email),
                    modifier = modifier.padding(0.dp),
                    fontSize = 19.sp,
                    color = colorResource(id = R.color.aestheticBlue),
                )
                DisplayTextInputField(
                    input = newEmail,
                    placeHolderTextID = R.string.email,
                    editButton = false,
                    readOnly = true,
                    isError = false,
                    editContentDescription = R.string.edit_email,
                    clearContentDescription = R.string.clear_email,
                    onDoneClickAction = {})

            }
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .align(Alignment.Start)
                    .padding(30.dp, 12.dp),
            ) {
                Text(
                    text = stringResource(id = passwordTitle),
                    modifier = modifier.padding(0.dp),
                    fontSize = 19.sp,
                    color = colorResource(id = R.color.aestheticBlue),
                )
                DisplayTextInputField(
                    input = oldPassword,
                    placeHolderTextID = passwordPlaceHolder,
                    editButton = true,
                    readOnly = readOnlyPassword,
                    isError = oldPasswordError,
                    editContentDescription = R.string.change_password,
                    clearContentDescription = R.string.clear_old_password,
                    onDoneClickAction = {
                        if (!readOnlyPassword) {
                            val checkNewPasswordResult = checkNewPassword(
                                oldPassword.value,
                                newPassword.value,
                                confirmNewPassword.value
                            )
                            when (checkNewPasswordResult) {
                                "passed" -> {
                                    val credential = EmailAuthProvider.getCredential(
                                        email.value.toString(),
                                        oldPassword.value
                                    )
                                    auth.currentUser!!.reauthenticate(credential)
                                        .addOnSuccessListener {
                                            auth.currentUser?.updatePassword(newPassword.value)!!
                                                .addOnSuccessListener {
                                                    Toast.makeText(
                                                        localContext,
                                                        "Password Updated!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    oldPasswordError = false
                                                    newPasswordError = false
                                                    confirmNewPasswordError = false
                                                    readOnlyPassword = true
                                                    displayChangePassword = false
                                                    passwordTitle = R.string.password
                                                    passwordPlaceHolder = R.string.password_hidden
                                                    oldPassword.value = ""
                                                }
                                                .addOnFailureListener {
                                                    Toast.makeText(
                                                        localContext,
                                                        "Failed to update password",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    oldPasswordError = false
                                                    newPasswordError = false
                                                    confirmNewPasswordError = false
                                                }
                                        }.addOnFailureListener {
                                            Toast.makeText(
                                                localContext,
                                                "Old password is wrong",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            oldPasswordError = true
                                            oldPassword.value = ""
                                        }
                                }

                                "old password and new password match" -> {
                                    val credential = EmailAuthProvider.getCredential(
                                        email.value.toString(),
                                        oldPassword.value
                                    )
                                    auth.currentUser!!.reauthenticate(credential)
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                localContext,
                                                "Password is unchanged",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            oldPasswordError = false
                                            newPasswordError = false
                                            confirmNewPasswordError = false
                                            readOnlyPassword = true
                                            displayChangePassword = false
                                            passwordTitle = R.string.password
                                            passwordPlaceHolder = R.string.password_hidden
                                            oldPassword.value = ""
                                        }.addOnFailureListener {
                                            oldPasswordError = true
                                            Toast.makeText(
                                                localContext,
                                                "Old password is wrong",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }

                                "no new password" -> {
                                    Toast.makeText(
                                        localContext,
                                        "No new password",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    oldPasswordError = false
                                    newPasswordError = true
                                    confirmNewPasswordError = true
                                    displayDeleteAccount = false
                                }

                                "new password is too short" -> {
                                    Toast.makeText(
                                        localContext,
                                        "New password is too short",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    oldPasswordError = false
                                    newPasswordError = true
                                    confirmNewPasswordError = true
                                    displayDeleteAccount = false
                                }

                                "new password and confirm new password do not match" -> {
                                    Toast.makeText(
                                        localContext,
                                        "New password and confirm new password do not match",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    oldPasswordError = false
                                    newPasswordError = false
                                    confirmNewPasswordError = true
                                    displayDeleteAccount = false
                                }

                                else -> {
                                    Toast.makeText(
                                        localContext,
                                        checkNewPasswordResult,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    oldPasswordError = false
                                    newPasswordError = false
                                    confirmNewPasswordError = false
                                    readOnlyPassword = false
                                    displayChangePassword = true
                                    passwordTitle = R.string.old_password
                                    passwordPlaceHolder = R.string.old_password
                                    displayDeleteAccount = false
                                }
                            }
                        } else {
                            oldPasswordError = false
                            newPasswordError = false
                            confirmNewPasswordError = false
                            readOnlyPassword = false
                            displayChangePassword = true
                            passwordTitle = R.string.old_password
                            passwordPlaceHolder = R.string.old_password
                            displayDeleteAccount = false
                        }
                    })
            }
            AnimatedVisibility(
                visible = displayChangePassword,
                enter = expandVertically(),
                exit = shrinkVertically(),
            ) {
                Column {
                    Column(
                        modifier = modifier
                            .fillMaxWidth()
                            .align(Alignment.Start)
                            .padding(30.dp, 12.dp),
                    ) {
                        Text(
                            text = stringResource(id = R.string.new_password),
                            modifier = modifier.padding(0.dp),
                            fontSize = 19.sp,
                            color = Color.White,
                        )
                        DisplayTextInputField(
                            input = newPassword,
                            placeHolderTextID = R.string.new_password,
                            editButton = false,
                            readOnly = false,
                            isError = newPasswordError,
                            editContentDescription = R.string.new_password,
                            clearContentDescription = R.string.clear_new_password,
                            onDoneClickAction = {})
                    }
                    Column(
                        modifier = modifier
                            .fillMaxWidth()
                            .align(Alignment.Start)
                            .padding(30.dp, 12.dp),
                    ) {
                        Text(
                            text = stringResource(id = R.string.confirm_password),
                            modifier = modifier.padding(0.dp),
                            fontSize = 19.sp,
                            color = Color.White,
                        )
                        DisplayTextInputField(
                            input = confirmNewPassword,
                            placeHolderTextID = R.string.confirm_password,
                            editButton = false,
                            readOnly = false,
                            isError = confirmNewPasswordError,
                            editContentDescription = R.string.confirm_new_password,
                            clearContentDescription = R.string.clear_confirm_new_password,
                            onDoneClickAction = {})
                    }
                }
            }
            Column(
                modifier = modifier.padding(top = 65.dp)
            ) {
                Button(
                    onClick = {
                        auth.signOut()
                        localContext?.startActivity(Intent(localContext, MainActivity2::class.java))
                        localContext?.finish()
                    },
                    modifier = modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.aestheticBlue))
                ) {
                    Text(
                        text = stringResource(id = R.string.log_out),
                        fontSize = 20.sp,
                    )
                }
            }
            Column(
                modifier = modifier.padding(top = 200.dp)
            ) {
                Button(
                    onClick = {
                        displayDeleteAccount = !displayDeleteAccount
                        passwordTitle = R.string.password
                        passwordPlaceHolder = R.string.password_hidden
                        readOnlyPassword = true
                        displayChangePassword = false
                    },
                    modifier = modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.aestheticBlue))
                ) {
                    Text(
                        text = stringResource(id = R.string.delete_account),
                        fontSize = 20.sp,
                    )
                }
            }
            AnimatedVisibility(
                visible = displayDeleteAccount,
                enter = expandVertically(),
                exit = shrinkVertically(),
                modifier = modifier.padding(top = 10.dp, bottom = 15.dp),
            ) {
                Column {
                    Button(
                        onClick = {
                            auth.currentUser!!.delete()
                                .addOnSuccessListener {
                                    localContext?.startActivity(
                                        Intent(
                                            localContext,
                                            MainActivity3::class.java
                                        )
                                    )
                                    localContext?.finish()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        localContext,
                                        "Failed to delete account",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        },
                        modifier = modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.rose))
                    ) {
                        Text(
                            text = stringResource(id = R.string.delete_account_sure),
                            fontSize = 20.sp,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayTextInputField(input: MutableState<String>, placeHolderTextID: Int, editButton: Boolean, readOnly: Boolean, isError: Boolean, editContentDescription: Int, clearContentDescription: Int, onDoneClickAction: () -> Unit, modifier: Modifier = Modifier){
    val interactionSource = remember { MutableInteractionSource() }
    var editOrSaveIcon by remember { mutableStateOf(R.drawable.baseline_edit_square_24) }
    editOrSaveIcon = if(readOnly){ R.drawable.baseline_edit_square_24 } else { R.drawable.baseline_save_24 }
    BasicTextField(
        value = input.value,
        onValueChange = { input.value = it },
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(top = 12.dp),
        interactionSource = interactionSource,
        singleLine = true,
        readOnly = readOnly,
        textStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = 19.sp),
        cursorBrush = SolidColor(Color.White),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        visualTransformation = VisualTransformation.None,
        keyboardActions = KeyboardActions(onDone = { onDoneClickAction() }),
    ) { innerTextField ->
        TextFieldDefaults.DecorationBox(
            value = input.value,
            visualTransformation = VisualTransformation.None,
            innerTextField = innerTextField,
            singleLine = true,
            isError = isError,
            enabled = true,
            interactionSource = interactionSource,
            contentPadding = PaddingValues(
                start = 12.dp,
                top = 8.dp,
                end = 12.dp,
                bottom = 8.dp
            ),
            placeholder = { Text(text = stringResource(id = placeHolderTextID)) },
            trailingIcon = {
                Row {
                    if (!TextUtils.isEmpty(input.value) and !readOnly) {
                        IconButton(modifier = modifier
                            .fillMaxWidth(.1f)
                            .padding(0.dp),
                            onClick = { input.value = "" }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.baseline_close_24),
                                contentDescription = stringResource(id = clearContentDescription),
                                modifier = modifier
                                    .padding(0.dp)
                                    .size(20.dp),
                            )
                        }
                    }
                    if(editButton) {
                        IconButton(modifier = modifier.padding(0.dp),
                            onClick = { onDoneClickAction() }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = editOrSaveIcon),
                                contentDescription = stringResource(id = editContentDescription),
                                modifier = modifier
                                    .padding(0.dp)
                                    .size(25.dp),
                            )
                        }
                    }
                }
            },
            shape = RoundedCornerShape(6.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colorResource(id = R.color.aestheticBlue),
                unfocusedContainerColor = colorResource(id = R.color.gray),
                errorContainerColor = colorResource(id = R.color.rose),
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                errorIndicatorColor = colorResource(id = R.color.rose),
                disabledIndicatorColor = Color.Transparent,
                focusedPlaceholderColor = Color.White,
                unfocusedPlaceholderColor = Color.White,
                errorPlaceholderColor = Color.White,
                disabledPlaceholderColor = Color.Transparent,
                focusedTrailingIconColor = colorResource(id = R.color.aestheticBlue),
                unfocusedTrailingIconColor = colorResource(id = R.color.gray),
                errorTrailingIconColor = Color.Red,
                disabledTrailingIconColor = Color.Transparent,
                errorTextColor = Color.White,
            ),
        )
    }
}

fun checkNewName(oldName: String, newName: String): String {
    if (newName == ""){
        return "no new name"
    }
    if (newName == oldName){
        return "old name and new name match"
    }
    return "passed"
}

fun checkNewPassword(oldPassword: String, newPassword: String, confirmPassword: String): String {
    if (oldPassword == ""){
        return "no new password"
    }
    if (newPassword == "") {
        return "no new password"
    }
    if (newPassword.length <= 8) {
        return "new password is too short"
    }
    if (newPassword == oldPassword){
        return "old password and new password match"
    }
    if (newPassword != confirmPassword) {
        return "new password and confirm new password do not match"
    }
    return "passed"
}
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        DisplayProfile()
    }
}
