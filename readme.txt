README: MyBudget App

Author: Mathias Moyo
Student ID: D974238

Overview:
MyBudget is a personal budgeting app built with Jetpack Compose, designed to help users track their budget and spending. 
It provides an overview of the total budget, number of budget items, and specific budget information for the current month. 
The app also allows users to visualize key budgeting data through a clean and interactive UI.

Features:
- Displays the total budget and number of items
- Shows the earliest budget item added
- Displays the total budget spent in the current month
- Simple, user-friendly UI with budget summary and financial insights
- Dynamic updates to budget data as the user adds or edits items

UI Components:
1. **Logo**: Displays the app's logo at the top.
2. **Budget Summary Card**: Shows the total budget value and the number of items.
3. **First Item Card**: Displays the price of the first item added to the budget.
4. **This Month Card**: Displays the total budget spent for the current month.

Technical Information:
- Written in Kotlin using Jetpack Compose for the UI.
- Uses `BudgetViewModel` to manage the list of budget items.
- Budget items are dynamically retrieved from a data source and displayed in the UI.
- The app calculates the current month's budget total and displays it in the "This Month" section.

Libraries:
- Jetpack Compose: Modern toolkit for building Android UIs.
- DecimalFormat: For formatting monetary values.
- SimpleDateFormat: For date parsing and formatting.

Requirements:
- Android Studio 4.1 or higher
- Kotlin 1.4 or higher
- Jetpack Compose

To Run:
1. Clone the repository to your local machine.
2. Open the project in Android Studio.
3. Build and run the app on an Android device or emulator.

License:

Copyright (c) 2025 Mathias Moyo

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
