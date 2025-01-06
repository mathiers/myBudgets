// Author: Mathias Moyo
// Student ID: D974238

package com.tees.mybudgets.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tees.mybudgets.R
import com.tees.mybudgets.data.BudgetViewModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun Home(budgetViewModel: BudgetViewModel) {
    val budgetItems by budgetViewModel.getItems().collectAsState(initial = emptyList())
    val totalBudget = budgetItems.sumOf { it.price }
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val earliestItem = budgetItems.minByOrNull { it.date }

    // Current Month & Year
    val currentCalendar = Calendar.getInstance()
    val currentMonth = currentCalendar.get(Calendar.MONTH)
    val currentYear = currentCalendar.get(Calendar.YEAR)

    // Calculate this month's total
    val thisMonthTotal = budgetItems.filter {
        val itemDate = dateFormat.parse(it.date)
        val itemCalendar = Calendar.getInstance().apply { time = itemDate }
        itemCalendar.get(Calendar.MONTH) == currentMonth && itemCalendar.get(Calendar.YEAR) == currentYear
    }.sumOf { it.price }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo image
            Image(
                modifier = Modifier
                    .size(170.dp)
                    .align(Alignment.CenterHorizontally),
                painter = painterResource(id = R.drawable.logo),
                contentScale = ContentScale.Fit,
                contentDescription = "logo"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Budget Summary Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .height(200.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFADD8E6))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        modifier = Modifier.size(80.dp),
                        painter = painterResource(id = R.drawable.bigfinance),
                        contentScale = ContentScale.Fit,
                        contentDescription = "finance icon"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Total Budget: £ ${DecimalFormat("#,###.00").format(totalBudget)}",
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Number of Items: ${budgetItems.size}",
                        fontSize = 18.sp
                    )
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            // Display "First Item" and "This Month" cards next to each other
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f) // Ensures each card takes up equal space horizontally
                        .fillMaxHeight() // Ensures the Box takes up the full height
                        .padding(8.dp)
                ) {
                    BudgetInfoCard(
                        title = "First Item",
                        value = earliestItem?.let {
                            "£ ${DecimalFormat("#,###.00").format(it.price)}"
                        } ?: "No Items Found",
                        iconResId = R.drawable.cash
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight() // Ensures the Box takes up the full height
                        .padding(8.dp) // Padding around each card
                ) {
                    BudgetInfoCard(
                        title = "This Month",
                        value = "£ ${DecimalFormat("#,###.00").format(thisMonthTotal)}",
                        iconResId = R.drawable.finance
                    )
                }
            }

        }
    }
}

//Function that displays a card UI component containing an icon, a title, and a value.
@Composable
fun BudgetInfoCard(title: String, value: String, iconResId: Int) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .height(130.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFADD8E6))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier.size(40.dp),
                painter = painterResource(id = iconResId),
                contentScale = ContentScale.Fit,
                contentDescription = "icon"
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontSize = 16.sp)
        }
    }
}

