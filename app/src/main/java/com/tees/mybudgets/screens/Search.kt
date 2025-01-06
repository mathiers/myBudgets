package com.tees.mybudgets.screens

import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.tees.mybudgets.R
import com.tees.mybudgets.data.BudgetItem
import com.tees.mybudgets.data.BudgetViewModel
import kotlin.math.abs

import java.text.SimpleDateFormat
import java.util.*

@Composable
fun Search(budgetViewModel: BudgetViewModel) {
    val budgetItems by budgetViewModel.getItems().collectAsState(initial = emptyList())

    // Track expanded dates
    val expandedDates = remember { mutableStateMapOf<String, Boolean>() }

    // Track checked items
    val checkedItems = remember { mutableStateMapOf<BudgetItem, Boolean>() }

    // Mutable state list for UI updates
    val itemsList = remember { mutableStateListOf<BudgetItem>() }
    LaunchedEffect(budgetItems) {
        itemsList.clear()
        itemsList.addAll(budgetItems)
    }

    // Parse the date string into a Date object for proper sorting
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // Group items by date, sorting them in ascending order
    val groupedItems = itemsList
        .groupBy { it.date }
        .toSortedMap { date1, date2 ->
            try {
                val parsedDate1 = dateFormat.parse(date1)
                val parsedDate2 = dateFormat.parse(date2)
                parsedDate1.compareTo(parsedDate2)
            } catch (e: Exception) {
                0
            }
        }

    // LazyColumn for the entire screen to allow scrolling
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF).copy(alpha = 0.2f)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Header with Image and Title
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 5.dp)
                    .offset(y = -5.dp)
            ) {
                Image(
                    modifier = Modifier
                        .size(70.dp)
                        .align(Alignment.Center),
                    painter = painterResource(id = R.drawable.daybudgets),
                    contentScale = ContentScale.Fit,
                    contentDescription = "logo"
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Budgets Grouped by Day",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Grouped budget items with sorted dates
        groupedItems.forEach { (date, items) ->
            val isExpanded = expandedDates.getOrDefault(date, false)
            val onToggle = { expandedDates[date] = !isExpanded }
            val total = items.sumOf { it.price }

            item {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = date,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .clickable { onToggle() },
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Total: £ ${String.format("%.2f", total)}",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 8.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                    }

                    // Right arrow button to toggle expand/collapse
                    IconButton(
                        onClick = { onToggle() },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.arrow),
                            contentDescription = "Toggle Expansion",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            if (isExpanded) {
                items(items) { budgetItem ->
                    BudgetItemRow(
                        item = budgetItem,
                        isChecked = checkedItems[budgetItem] ?: false,
                        onCheckedChange = { isChecked -> checkedItems[budgetItem] = isChecked },
                        onDelete = {
                            // Remove from the UI and ViewModel
                            itemsList.remove(budgetItem)
                            budgetViewModel.deleteItem(budgetItem)
                        }
                    )
                }
            }
        }

        // Display message if no items are found
        if (itemsList.isEmpty()) {
            item {
                Text(
                    text = "No budget items found.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 16.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}



//displaying a single row in a list of budget items.
//functionality for swiping the row horizontally to trigger a delete action with haptic feedback and vibration.
@Composable
fun BudgetItemRow(
    item: BudgetItem,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    val threshold = 200f
    val hapticFeedback = LocalHapticFeedback.current
    val context = LocalContext.current
    val vibrator = context.getSystemService(Vibrator::class.java)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp, vertical = 4.dp)
            .offset { IntOffset(offsetX.toInt(), 0) }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (abs(offsetX) > threshold) {
                            // Haptic Feedback
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)

                            // Fallback Vibrator API
                            vibrator?.vibrate(
                                VibrationEffect.createOneShot(
                                    100, VibrationEffect.DEFAULT_AMPLITUDE
                                )
                            )

                            // Delete action
                            onDelete()

                            // Show toast message
                            Toast.makeText(
                                context,
                                "${item.item} deleted",
                                Toast.LENGTH_SHORT
                            ).show()


                            offsetX = 0f
                        } else {
                            offsetX = 0f // Reset if threshold not reached
                        }
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        offsetX += dragAmount
                    }
                )
            }
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 2.dp, vertical = 2.dp)
                .background(
                    if (isChecked) {
                        Color(0xFFADD8E6).copy(alpha = 0.5f) // Light blue with 50% transparency
                    } else {
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.2f) // Surface color with 20% transparency
                    }
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = onCheckedChange
            )
            Spacer(modifier = Modifier.width(4.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.item,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isChecked) Color.Black else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "£ ${String.format("%.2f", item.price)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isChecked) Color.Black else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}
