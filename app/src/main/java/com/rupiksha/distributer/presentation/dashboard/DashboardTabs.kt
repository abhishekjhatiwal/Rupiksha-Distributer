package com.rupiksha.distributer.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rupiksha.distributer.ui.theme.*

val mockPortfolioItems = listOf(
    SummaryItem("SMAs", "Count of all registered SMAs mapped to distributor", "9%", true),
    SummaryItem("AEPS Star Platinum Churn", "Count of potential growth SMAs churn in respective period", "", null, true),
    SummaryItem("AEPS Winback Opportunity", "Count of Adhikaris who have churned in AEPS during the respective period", "", null, true),
    SummaryItem("Transacting", "Count of all SMAs mapped to user who have total_gtv_amount>0 in respective period", "10%", true),
    SummaryItem("Non-transacting", "Non-transacting Distributors", "0%", true),
    SummaryItem("Gross Adds", "Number of new Distributor IDs created in respective period", "100%", false),
    SummaryItem("Spice Platinum", "SMA with GTV of >2.5 Lakh and < 10 Lakh", "0%", true),
    SummaryItem("Last Month Onboarded Business", "New Distributors registered LastMonth with gtv", "100%", false),
    SummaryItem("Spice Star", "SMA with GTV>10 Lakh", "0%", true),
    SummaryItem("SMA at Risk", "SMAs Present in churn prediction with Severe very high and high risk", "0%", false),
    SummaryItem("Churned", "SMAs with 0 gtv this month but were transacting last month", "900%", false)
)

@Composable
fun PortfolioSummaryTab(items: List<SummaryItem> = mockPortfolioItems) {
    var selectedToggle by remember { mutableStateOf("Daily") }
    
    Column(modifier = Modifier.fillMaxWidth().background(Color.White)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            DashboardToggle(
                selectedOption = selectedToggle,
                onOptionSelected = { selectedToggle = it }
            )
        }
        
        Column(modifier = Modifier.fillMaxWidth()) {
            items.forEach { item ->
                SummaryItemCard(item)
            }
        }
    }
}

@Composable
fun BusinessSummaryTab() {
    Box(modifier = Modifier.fillMaxSize().background(BackgroundLight), contentAlignment = Alignment.Center) {
        Text("Business Summary Content", color = TextSecondary)
    }
}
