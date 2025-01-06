package com.tees.mybudgets.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.tees.mybudgets.R
import com.tees.mybudgets.data.BudgetItem
import com.tees.mybudgets.data.BudgetViewModel
import com.tees.mybudgets.ui.theme.GreenJC
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


//Fetch User Information
//Observe Budget Items an Manage Checkbox State
@Composable
fun Profile(navController: NavHostController, budgetViewModel: BudgetViewModel) {
    val firebaseUser = FirebaseAuth.getInstance().currentUser
    val email = firebaseUser?.email ?: "No email available"
    val creationTimestamp = firebaseUser?.metadata?.creationTimestamp ?: 0L
    val creationDate = if (creationTimestamp > 0) {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        sdf.format(Date(creationTimestamp))
    } else {
        "Unknown"
    }

    val budgetItems by budgetViewModel.getItems().collectAsState(initial = emptyList())
    var isConfirmed by remember { mutableStateOf(false) } // Checkbox state
    val context = navController.context

    // File picker launcher
    val saveLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri: Uri? ->
        if (uri != null) {
            exportDataToCSV(budgetItems, context, uri)
        } else {
            Toast.makeText(context, "No location selected.", Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Enabling vertical scrolling
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()), // Scrollable content
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Heading for the screen
            Image(
                modifier = Modifier
                    .padding(end = 5.dp)
                    .size(50.dp)
                    .align(alignment = Alignment.CenterHorizontally),
                painter = painterResource(id = R.drawable.user),
                contentScale = ContentScale.Fit,
                contentDescription = "user"
            )
            Text(
                text = "My Profile",
                fontSize = 25.sp,
                color = GreenJC
            )
            Spacer(modifier = Modifier.height(20.dp))

            // User profile details
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email",
                            tint = GreenJC
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Email: $email", fontSize = 12.sp, color = Color.Black)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Account Created Date",
                            tint = GreenJC
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Account Created: $creationDate",
                            fontSize = 12.sp,
                            color = Color.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Backup Data to CSV Button
            OutlinedButton(
                onClick = {
                    saveLauncher.launch("BudgetBackup_${System.currentTimeMillis()}.csv")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                border = BorderStroke(1.dp, GreenJC), // Define the border color and width
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = GreenJC // Text and icon color
                )
            ) {
                Text(text = "Backup Data to CSV", color = GreenJC)
            }

            Spacer(modifier = Modifier.height(5.dp))

            // Sign Out Button
            OutlinedButton(
                onClick = {
                    try {
                        val auth = FirebaseAuth.getInstance()
                        auth.signOut()
                        navController.popBackStack()
                        navController.navigate("login")
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error signing out: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                border = BorderStroke(1.dp, GreenJC),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = GreenJC)
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Sign Out",
                    tint = GreenJC
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Sign Out", color = GreenJC)
            }

            Spacer(modifier = Modifier.height(5.dp))

            // Confirmation Checkbox
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
            ) {
                Checkbox(
                    checked = isConfirmed,
                    onCheckedChange = { isConfirmed = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = GreenJC,
                        uncheckedColor = Color.Gray
                    )
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "I want to delete my account and understand that this action cannot be undone.",
                    color = Color.Black
                )
            }

            //Spacer(modifier = Modifier.height(5.dp))

            // Delete Account Button
            Button(
                onClick = { deleteUserAccount(navController.context) },
                colors = ButtonDefaults.buttonColors(containerColor = GreenJC),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                enabled = isConfirmed // Enabled only if checkbox is checked
            ) {
                Text(text = "Delete Account", color = Color.White)
            }
        }
    }
}

// Function to delete the user account
fun deleteUserAccount(context: Context) {
    val user = FirebaseAuth.getInstance().currentUser

    if (user != null) {
        user.delete().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Account deleted successfully.", Toast.LENGTH_SHORT).show()
                FirebaseAuth.getInstance().signOut()
            } else {
                Toast.makeText(
                    context,
                    "Error deleting account: ${task.exception?.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    } else {
        Toast.makeText(context, "User not logged in.", Toast.LENGTH_SHORT).show()
    }
}

// Function to export data to a CSV file at the selected location
fun exportDataToCSV(budgetItems: List<BudgetItem>, context: Context, uri: Uri) {
    try {
        val outputStream = context.contentResolver.openOutputStream(uri)
        val writer = OutputStreamWriter(outputStream)

        // Write CSV headers
        writer.append("Date,Item,Price,Status\n")

        // Write each budget item
        for (item in budgetItems) {
            writer.append("${item.date},${item.item},${item.price},${if (item.isChecked) "Checked" else "Unchecked"}\n")
        }

        // Flush and close the writer
        writer.flush()
        writer.close()

        Toast.makeText(context, "Backup saved successfully.", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Error exporting data: ${e.message}", Toast.LENGTH_LONG).show()
    }
}
