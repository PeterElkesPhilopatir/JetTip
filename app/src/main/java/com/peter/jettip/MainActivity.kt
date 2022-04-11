package com.peter.jettip

import android.os.Bundle
import android.util.Log
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.peter.jettip.components.NumericInputField
import com.peter.jettip.components.RoundIconButton
import com.peter.jettip.ui.theme.JetTipTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetTipTheme {
                // A surface container using the 'background' color from the theme
                MyApp()
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    JetTipTheme {
        MyApp()

    }
}

@Composable
fun MyApp() {
    Column(modifier = Modifier.fillMaxSize()) {
        MainContent()
    }

}

@Composable
fun TopHeader(totalPerPerson: Double = 12.0) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(5.dp)
            .clip(shape = CircleShape.copy(CornerSize(12.dp))),
        color = Color(0xFFE9D7F7)
    ) {
        val total = "%.2f".format(totalPerPerson)
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Total Per Person", style = MaterialTheme.typography.h5)

            Text(
                text = "$$total",
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
fun MainContent() {
    BillForm {
        Log.i("BILL", it)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillForm(modifier: Modifier = Modifier, onValChanged: (String) -> Unit = {}) {
    val billState = remember { mutableStateOf("") }
    val personsCounter = remember { mutableStateOf(1) }
    val sliderPosition = remember { mutableStateOf(0.0f) }
    val tipPercentage = (sliderPosition.value * 100).toInt()
    val tipAmount = remember {
        mutableStateOf(0.0)
    }
    val totalPerPerson = remember {
        mutableStateOf(0.0)
    }
    val validState = remember(billState.value) {
        billState.value.trim().isNotEmpty()
    }
    TopHeader(totalPerPerson.value.toDouble())
    Surface(
        modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(CornerSize(8.dp)),
        border = BorderStroke(1.dp, color = MaterialTheme.colors.secondaryVariant)
    )
    {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.padding(5.dp)
        ) {
            NumericInputField(
                valueState = billState,
                labelID = "Bill",
                isSingleLine = true,
                enabled = true,
                onAction = KeyboardActions {
                    if (!validState) return@KeyboardActions
                    onValChanged(billState.value.trim())
                }
            )

            if (validState) {
                tipAmount.value = calculateTip(billState.value.toDouble(), tipPercentage)
                totalPerPerson.value = calculateTotalPerPerson(
                    billState.value.toDouble(),
                    personsCounter.value,
                    tipPercentage
                )
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        modifier = Modifier.align(alignment = Alignment.CenterVertically),
                        text = "Split"
                    )
                    Spacer(modifier = Modifier.width(120.dp))
                    Row(
                        modifier = Modifier.padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        RoundIconButton(imageVector = Icons.Default.Remove) {
                            if (personsCounter.value > 1)
                                personsCounter.value = personsCounter.value.dec()
                            totalPerPerson.value = calculateTotalPerPerson(
                                billState.value.toDouble(),
                                personsCounter.value,
                                tipPercentage
                            )
                        }
                        Text(
                            modifier = Modifier
                                .padding(start = 5.dp, end = 5.dp)
                                .align(alignment = Alignment.CenterVertically),
                            text = personsCounter.value.toString()
                        )
                        RoundIconButton(imageVector = Icons.Default.Add) {
                            personsCounter.value = personsCounter.value.inc()
                            totalPerPerson.value = calculateTotalPerPerson(
                                billState.value.toDouble(),
                                personsCounter.value,
                                tipPercentage
                            )
                        }
                    }
                }

                //Tip
                Column() {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            "Tip",
                            modifier = Modifier.align(alignment = Alignment.CenterVertically)
                        )
                        Spacer(modifier = Modifier.width(200.dp))
                        Text(
                            "$${tipAmount.value}",
                            modifier = Modifier.align(alignment = Alignment.CenterVertically)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "$tipPercentage %")
                        Spacer(modifier = Modifier.height(14.dp))
                        Slider(value = sliderPosition.value, onValueChange = {
                            if (validState)
                                tipAmount.value =
                                    calculateTip(billState.value.toDouble(), tipPercentage)
                            sliderPosition.value = it
                            totalPerPerson.value = calculateTotalPerPerson(
                                billState.value.toDouble(),
                                personsCounter.value,
                                tipPercentage
                            )
                        }, modifier = Modifier.padding(start = 16.dp, end = 16.dp), steps = 10)
                    }
                }
            } else Box() {}
        }
    }


}

fun calculateTip(bill: Double, tipPercentage: Int): Double {
    return (bill * tipPercentage) / 100
}

fun calculateTotalPerPerson(bill: Double, persons: Int, tipPercentage: Int) =
    (bill + calculateTip(bill, tipPercentage)) / persons
