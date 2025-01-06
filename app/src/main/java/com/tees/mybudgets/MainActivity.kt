// Author: Mathias Moyo
// Student ID: D974238

package com.tees.mybudgets

import RegisterScreen
import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.tees.mybudgets.authentication.*
import com.tees.mybudgets.data.BudgetItem
import com.tees.mybudgets.data.BudgetViewModel
import com.tees.mybudgets.screens.*
import com.tees.mybudgets.ui.theme.GreenJC
import kotlinx.coroutines.launch
import java.util.*
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester



class MainActivity : ComponentActivity() {
    private val budgetViewModel: BudgetViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val firebaseAuth = FirebaseAuth.getInstance()
            val currentUser = firebaseAuth.currentUser

            //Container for all the navigable screens in the app
            NavHost(
                navController = navController,
                //determines the first screen shown when the app is launched.
                //If currentUser is not null (indicating a logged-in user), the start destination is "home".
                startDestination = if (currentUser != null) "home" else "login"
            ) {
                //Each composable block corresponds to a screen or destination in the app.
                //The navController allows dynamic navigation between  destinations.
                composable("login") { LoginScreen(navController = navController) }
                composable("register") { RegisterScreen(navController = navController) }
                composable("forgot_password") { ForgotPasswordScreen(navController = navController) }
                composable("home") {
                    NavBotSheet(budgetViewModel = budgetViewModel)
                }
            }
        }
    }
}

/*serve as the central navigation and user interface (UI) component for the app.
It integrates a navigation drawer, a bottom sheet, and navigation logic for handling screen
transitions and user actions like logout*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavBotSheet(budgetViewModel: BudgetViewModel) {
    val navigationController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val context = LocalContext.current

    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("") }
    var itemName by remember { mutableStateOf("") }
    var itemPrice by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf(Icons.Default.Home) }

    //Side navigation drawer and controls weather drawer is open or not
    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet {
                Box(
                    modifier = Modifier
                        .background(GreenJC)
                        .fillMaxWidth()
                        .height(150.dp)
                ) {
                    Text(
                        text = "myBudgets",
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                Divider()
                listOf(
                    Triple("Home", Screens.Home.screen, Icons.Default.Home),
                    Triple("Profile", Screens.Profile.screen, Icons.Default.Person),
                    Triple("Logout", null, Icons.Default.ExitToApp)
                ).forEach { (label, screen, icon) ->
                    NavigationDrawerItem(
                        label = { Text(text = label, color = GreenJC) },
                        selected = false,
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = GreenJC
                            )
                        },
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            if (screen != null) {
                                navigationController.navigate(screen) {
                                    popUpTo(navigationController.graph.startDestinationId) // Clear only up to the start destination
                                }
                            } else {
                                // Perform Firebase sign-out
                                val auth = FirebaseAuth.getInstance()
                                auth.signOut()
                                Toast.makeText(context, "Logging out...", Toast.LENGTH_SHORT).show()

                                // After signing out, navigate to the login screen
                                navigationController.navigate("login") {
                                    popUpTo(navigationController.graph.startDestinationId) { // Clear up to the start destination
                                        inclusive = true
                                    }
                                    launchSingleTop =
                                        true // Prevent duplicate instances of the login screen
                                }
                            }
                        }

                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        // containerColor = GreenJC,
                        //titleContentColor = Color.White
                    ),
                    navigationIcon = {
                        IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Rounded.Menu,
                                contentDescription = "MenuButton"
                            )
                        }
                    }
                )
            },
            bottomBar = {
                BottomAppBar(containerColor = GreenJC, modifier = Modifier.height(55.dp)) {
                    listOf(
                        Icons.Default.Home to Screens.Home.screen,
                        Icons.Default.DateRange to Screens.Search.screen,
                        Icons.Default.Search to Screens.Notification.screen,
                        Icons.Default.Person to Screens.Profile.screen
                    ).forEach { (icon, screen) ->
                        IconButton(
                            onClick = {
                                selectedIcon = icon
                                navigationController.navigate(screen) { popUpTo(0) }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (selectedIcon == icon) Color.White else Color.DarkGray
                            )
                        }
                    }

                    Box(
                        modifier = Modifier.weight(1f).padding(5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        //FAB Button implementation
                        FloatingActionButton(onClick = {
                            val calendar = Calendar.getInstance()
                            val datePickerDialog = DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    selectedDate = "$year-${month + 1}-$dayOfMonth"
                                    showBottomSheet = true
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            )
                            datePickerDialog.show()
                        }) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add Item",
                                tint = Color.Black
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navigationController,
                startDestination = Screens.Home.screen,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screens.Home.screen) { Home(budgetViewModel = budgetViewModel) }
                composable(Screens.Profile.screen) {
                    Profile(
                        navController = navigationController,
                        budgetViewModel = budgetViewModel
                    )
                }
                composable(Screens.Search.screen) { Search(budgetViewModel = budgetViewModel) }
                composable(Screens.Notification.screen) { Notification(budgetViewModel = budgetViewModel) }

            }
        }

        if (showBottomSheet) {
            val focusRequester = remember { FocusRequester() } // Declare a focus requester
            val context = LocalContext.current

            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Selected Date: $selectedDate")
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester), // Apply the focus requester
                        value = itemName,
                        onValueChange = { itemName = it },
                        label = { Text("Item Name") },
                        trailingIcon = {
                            if (itemName.isNotEmpty()) {
                                IconButton(onClick = { itemName = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear Item Name",
                                        tint = Color.Gray
                                    )
                                }
                            }
                        }
                    )

                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = itemPrice,
                        onValueChange = { itemPrice = it },
                        label = { Text("Price") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = {
                                showBottomSheet = false
                            },
                            modifier = Modifier.weight(1f),
                            border = BorderStroke(1.dp, Color.Gray)
                        ) {
                            Text("Done")
                        }
                        OutlinedButton(
                            onClick = {
                                val price = itemPrice.toDoubleOrNull()
                                if (selectedDate.isNotEmpty() && itemName.isNotEmpty() && price != null) {
                                    budgetViewModel.addItem(
                                        BudgetItem(
                                            date = selectedDate,
                                            item = itemName,
                                            price = price
                                        )
                                    )
                                    Toast.makeText(context, "Item Added", Toast.LENGTH_SHORT).show()
                                    itemName = ""
                                    itemPrice = ""
                                    focusRequester.requestFocus() // Request focus back to the "Item Name" field
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Please fill all fields correctly.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            border = BorderStroke(1.dp, Color.Gray)
                        ) {
                            Text("Add")
                        }
                    }

                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                    )
                }
            }
        }
    }
}