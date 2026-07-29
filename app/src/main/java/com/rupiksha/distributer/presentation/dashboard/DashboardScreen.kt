package com.rupiksha.distributer.presentation.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rupiksha.distributer.R
import com.rupiksha.distributer.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onLogout: () -> Unit
) {
    val viewModel: DashboardViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    var selectedTabIndex by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            DashboardTopBar()
        },
        bottomBar = {
            DashboardBottomNavigation()
        },
        floatingActionButton = {
            Surface(
                onClick = { },
                color = BrandPrimary,
                shape = RoundedCornerShape(24.dp),
                shadowElevation = 4.dp,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BadgedBox(
                        badge = { Badge { Text("7") } }
                    ) {
                        Image(
                            painter = painterResource(R.drawable.logo),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "FUND REQUESTS",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundLight)
                .verticalScroll(rememberScrollState())
        ) {
            DashboardHeader(selectedTabIndex) { selectedTabIndex = it }

            when (selectedTabIndex) {
                0 -> PortfolioSummaryTab()
                1 -> BusinessSummaryTab()
            }
        }
    }
}

@Composable
fun DashboardTopBar() {

    Surface(
        color = Color.White,
        shadowElevation = 2.dp,
        modifier = Modifier.statusBarsPadding()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.app_logo),
                        contentDescription = "Logo",
                        modifier = Modifier.size(40.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Rupiksha",
                            fontWeight = FontWeight.Bold,
                            color = BrandPrimary,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "Making Life Digital",
                            fontSize = 10.sp,
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = InteractionBg,
                        shape = RoundedCornerShape(16.dp),
                        onClick = {}
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("+", color = InteractionText, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "Interaction",
                                color = InteractionText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Black)
                    Spacer(Modifier.width(12.dp))
                    BadgedBox(
                        badge = { Badge { Text("7") } }
                    ) {
                        Icon(Icons.Outlined.Notifications, contentDescription = "Notifications")
                    }
                    Spacer(Modifier.width(12.dp))
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = Color.Black)
                }
            }
        }
    }
}

@Composable
fun DashboardHeader(selectedTabIndex: Int, onTabSelected: (Int) -> Unit) {
    Column(modifier = Modifier.background(Color.White)) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.White,
            contentColor = BrandPrimary,
            divider = {}
        ) {
            listOf("Portfolio Summary", "Business Summary").forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { onTabSelected(index) },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    }
                )
            }
        }

        HorizontalDivider(thickness = 1.dp, color = BorderLight)

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(InteractionBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = BrandPrimary)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Hello, Distributor", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Welcome back 👋", color = TextSecondary, fontSize = 12.sp)
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Today's Date", fontSize = 10.sp, color = TextSecondary)
                        Text("19 Jul 2025", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        Icons.Outlined.CalendarMonth,
                        contentDescription = null,
                        tint = BrandPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val stats = listOf(
            StatData(
                "Total SMAs",
                "12,845",
                "Registered",
                Icons.Default.Person,
                CardIcon1,
                CardIconBg1
            ),
            StatData(
                "Total AEPS Txns",
                "8,423",
                "This Month",
                Icons.Default.Fingerprint,
                CardIcon2,
                CardIconBg2
            ),
            StatData(
                "Total Payout (₹)",
                "₹48.62L",
                "This Month",
                Icons.Default.AccountBalanceWallet,
                CardIcon3,
                CardIconBg3
            ),
            StatData(
                "Wallet Balance",
                "₹7,845.50",
                "Available",
                Icons.Default.AccountBalanceWallet,
                CardIcon4,
                CardIconBg4
            )
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(stats) { stat ->
                StatCard(stat)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun DashboardBottomNavigation() {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Dashboard", fontSize = 10.sp) },
            selected = true,
            onClick = {},
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BrandPrimary,
                selectedTextColor = BrandPrimary,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = InteractionBg
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.People, contentDescription = null) },
            label = { Text("SMAs", fontSize = 10.sp) },
            selected = false,
            onClick = {}
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.SwapHoriz, contentDescription = null) },
            label = { Text("Transactions", fontSize = 10.sp) },
            selected = false,
            onClick = {}
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.BarChart, contentDescription = null) },
            label = { Text("Reports", fontSize = 10.sp) },
            selected = false,
            onClick = {}
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.GridView, contentDescription = null) },
            label = { Text("More", fontSize = 10.sp) },
            selected = false,
            onClick = {}
        )
    }
}
