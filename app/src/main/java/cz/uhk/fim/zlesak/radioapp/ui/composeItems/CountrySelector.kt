package cz.uhk.fim.zlesak.radioapp.ui.composeItems

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import cz.uhk.fim.zlesak.radioapp.api.ApiResult
import cz.uhk.fim.zlesak.radioapp.data.Country

@Composable
fun CountrySelector(
    countries: ApiResult<List<Country>>,
    selectedIndex: Int,
    onCountrySelected: (String) -> Unit,
    onItemSelectedIndex: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    when (countries) {
        is ApiResult.Error -> Text("Error loading countries")
        is ApiResult.Loading -> CircularProgressIndicator(modifier = Modifier.size(24.dp))
        is ApiResult.Success -> {
            Box(modifier = Modifier.fillMaxWidth()) {
                // Clickable selector
                Row(
                    modifier = Modifier
                        .clickable { expanded = !expanded }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = countries.data[selectedIndex].name,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        Icons.Filled.ExpandMore,
                        contentDescription = "Country dropdown"
                    )
                }

                // Custom dropdown implementation
                CustomDropdown(
                    items = countries.data.map { it.name },
                    selectedIndex = selectedIndex,
                    onItemSelected = onCountrySelected,
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    onItemSelectedIndex = onItemSelectedIndex,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = screenHeight * 0.5f) // 50% of screen height
                )
            }
        }
    }
}