package com.example.expensetracker.screens

import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow // IMPORT THIS
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.expensetracker.R
import com.example.expensetracker.utils.Utils
import com.example.expensetracker.data.model.ExpenseEntity
import com.example.expensetracker.ui.theme.FundTrackBrandFont
import com.example.expensetracker.ui.theme.Zinc
import com.example.expensetracker.viewmodel.HomeViewModel
import com.example.expensetracker.viewmodel.HomeViewModelFactory
import com.example.expensetracker.widget.ExpenseTextView
import java.util.Calendar

@Composable
fun HomeScreen(navController: NavController) {

    val viewModel: HomeViewModel =
        HomeViewModelFactory(LocalContext.current).create(HomeViewModel::class.java)

    Surface(modifier = Modifier.fillMaxSize()) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (nameRow, list, card, topBar, add) = createRefs()
            Image(painter = painterResource(R.drawable.ic_topbar), contentDescription = null, contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().constrainAs(topBar){
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                })
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp, start = 16.dp, end = 16.dp)
                    .constrainAs(nameRow) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }) {
                ExpenseTextView(
                    text = "Fund Trackr",
                    fontSize = 26.sp,
                    fontFamily = FundTrackBrandFont,
                    fontWeight = FontWeight.Normal,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
                Column {
                    Spacer(modifier = Modifier.height(44.dp))
                    GreetingText()
                }
            }

            val state = viewModel.expenses.collectAsState(initial = emptyList())
            val expense = viewModel.getTotalExpense(state.value)
            val income = viewModel.getTotalIncome(state.value)
            val balance = viewModel.getBalance(state.value)

            CardItem(modifier = Modifier.constrainAs(card) {
                top.linkTo(nameRow.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }, expense, income, balance)

            TranscationList(modifier = Modifier.fillMaxWidth().constrainAs(list) {
                top.linkTo(card.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
                height = Dimension.fillToConstraints
            }, list = state.value, viewModel, navController)

            FloatingActionButton(
                onClick = { navController.navigate(ExtraRoutes.ADD_EXPENSE) },
                modifier = Modifier
                    .constrainAs(add) {
                        bottom.linkTo(parent.bottom, margin = 16.dp)
                        end.linkTo(parent.end, margin = 16.dp)
                    }
                    .navigationBarsPadding(),
                containerColor = Color(0xFF009688),
                contentColor = Color.White,
                shape = RoundedCornerShape(50),
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Expense"
                )
            }
        }
    }
}


@Composable
fun CardItem(modifier: Modifier, expense: String, income: String, balance: String) {
    Column(modifier = modifier
        .padding(top = 18.dp)
        .padding(16.dp)
        .fillMaxWidth()
        .height(200.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(Zinc)
        .padding(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            Column(modifier = Modifier.align(Alignment.Center)) {
                ExpenseTextView(text = "Total Balance", fontSize = 16.sp, color = Color.White)
                ExpenseTextView(text = balance, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {

            RowCardItem(
                modifier = Modifier.align(Alignment.CenterStart),
                title = "Income",
                amount = income,
                image = R.drawable.ic_income
            )

            RowCardItem(
                modifier = Modifier.align(Alignment.CenterEnd),
                title = "Expense",
                amount = expense,
                image = R.drawable.ic_expense
            )
        }
    }
}


@Composable
fun RowCardItem(modifier: Modifier, title: String, amount: String, image: Int) {
    Column(modifier = modifier) {
        Row {
            Image(painter = painterResource(id = image), contentDescription = null)
            Spacer(modifier = Modifier.size(8.dp))
            ExpenseTextView(text = title, fontSize = 16.sp, color = Color.White)
        }
        Spacer(modifier = Modifier.height(6.dp))
        ExpenseTextView(text = amount, fontSize = 20.sp, color = Color.White)
    }
}


@Composable
fun TranscationList(
    modifier: Modifier,
    list: List<ExpenseEntity>,
    viewModel: HomeViewModel,
    navController: NavController
) {
    LazyColumn(modifier = modifier.padding(horizontal = 16.dp)) {
        item {
            Box(modifier = Modifier.fillMaxWidth()) {
                ExpenseTextView(
                    text = "Transaction History",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                ExpenseTextView(
                    text = "See all",
                    fontSize = 16.sp,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable {
                            navController.navigate(ExtraRoutes.ALL_TRANSACTIONS)
                        }
                )
            }
            Spacer(modifier = Modifier.size(12.dp))
        }
        items(list) { item ->
            val isIncome = item.type == "Income"
            val sign = if (isIncome) "+ ₹" else "- ₹"
            val amt = Utils.formatToDecimalValue(item.amount)
            TransactionItem(
                title = item.title,
                amountDisplay = "$sign$amt",
                image = viewModel.getItemIcon(item),
                date = Utils.formatDatetoHumanReadableForm(item.data),
                amountColor = if (isIncome) Color(0xFF2E7D32) else Color(0xFFD32F2F),
                onEditClick = { navController.navigate("edit_expense/${item.id}") },
                onDeleteClick = { viewModel.deleteExpense(item) }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionItem(
    title: String,
    amountDisplay: String,
    image: ImageVector,
    date: String,
    amountColor: Color,
    modifier: Modifier = Modifier,
    showMenu: Boolean = true,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onRowClick: () -> Unit = {}
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Row( // <--- Changed from Box to Row
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onRowClick() },
        verticalAlignment = Alignment.CenterVertically // Align items vertically
    ) {
        // Left: icon, title, date
        Icon(
            imageVector = image,
            contentDescription = title,
            modifier = Modifier.size(34.dp),
            tint = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.size(8.dp))
        Column(
            modifier = Modifier
                .weight(1f) // <--- Give column weight so it takes available space
                .padding(end = 8.dp) // <--- Add padding to separate from amount
        ) {
            ExpenseTextView(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1, // <--- Limit title to one line
                overflow = TextOverflow.Ellipsis // <--- Add ellipsis for overflow
            )
            Spacer(Modifier.size(3.dp))
            ExpenseTextView(
                text = date,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        // Right: amount + menu
        Row(
            verticalAlignment = Alignment.CenterVertically // Ensure amount and menu are aligned
        ) {
            ExpenseTextView(
                text = amountDisplay,
                fontSize = 18.sp,
                color = amountColor,
                fontWeight = FontWeight.SemiBold
            )
            if (showMenu) {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "More")
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { ExpenseTextView(text = "Edit") },
                        onClick = {
                            menuExpanded = false
                            onEditClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { ExpenseTextView(text = "Delete") },
                        onClick = {
                            menuExpanded = false
                            onDeleteClick()
                        }
                    )
                }
            }
        }
    }

    Spacer(Modifier.size(12.dp))
}

@Composable
fun GreetingText() {
    val greeting = remember { getGreetingMessage() }

    ExpenseTextView(
        text = greeting,
        fontSize = 18.sp,
        color = Color.White
    )
}

fun getGreetingMessage(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 0..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        in 17..20 -> "Good Evening"
        else -> "Good Night"
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    HomeScreen(rememberNavController())
}