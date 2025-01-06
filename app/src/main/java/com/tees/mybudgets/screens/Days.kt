package com.tees.mybudgets.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.tees.mybudgets.R
import com.tees.mybudgets.data.BudgetItem
import com.tees.mybudgets.data.BudgetViewModel

// Function responsible for displaying an overview of all budget items managed by a BudgetViewModel.
@Composable
fun Notification(budgetViewModel: BudgetViewModel) {
    // Collecting the Flow of items from ViewModel
    val budgetItems by budgetViewModel.getItems()
        .collectAsState(initial = emptyList())

    // Handling item toggle and delete
    val onToggleChecked: (Boolean, BudgetItem) -> Unit = { isChecked, item ->
        budgetViewModel.updateItemStatus(item.copy(isChecked = isChecked))
    }

    val onDeleteItem: (BudgetItem) -> Unit = { item ->
        budgetViewModel.deleteItem(item)
    }

    // Wrapping the entire content inside a LazyColumn to make everything scrollable
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Icon Header for the screen
                Image(
                    modifier = Modifier
                        .padding(end = 5.dp)
                        .offset(y = -5.dp)
                        .size(70.dp),
                    painter = painterResource(id = R.drawable.evaluation),
                    contentScale = ContentScale.Fit,
                    contentDescription = "logo"
                )

                // Text Header for the screen
                Text(
                    text = "Overview of all items",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(16.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Displaying the list of items
        items(budgetItems) { item ->
            BudgetItemView(
                budgetItem = item,
                onDelete = { onDeleteItem(item) },
                onToggleChecked = { isChecked -> onToggleChecked(isChecked, item) }
            )
            Divider(color = Color.Gray.copy(alpha = 0.2f), thickness = 1.dp)
        }
    }
}

// Displaying a list of budget items using a vertical scrollable list (LazyColumn).
@Composable
fun BudgetItemView(
    budgetItem: BudgetItem,
    onDelete: () -> Unit,
    onToggleChecked: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onToggleChecked(!budgetItem.isChecked) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = budgetItem.isChecked,
                onCheckedChange = { onToggleChecked(it) }
            )
            Text(
                text = budgetItem.item,
                style = MaterialTheme.typography.bodyLarge.copy(
                    textDecoration = if (budgetItem.isChecked) TextDecoration.LineThrough else TextDecoration.None
                ),
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // A Row to align price next to the delete icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End, // Aligns content to the end (right side)
            modifier = Modifier.weight(1f)
        ) {
            // Price display with formatting
            Text(
                text = "£ ${String.format("%.2f", budgetItem.price)}", // Formatting price to 2 decimal places
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(end = 8.dp) // Padding to separate price and delete icon
            )

            // Delete Icon
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Delete Item",
                    tint = Color.Gray
                )
            }
        }
    }
}
