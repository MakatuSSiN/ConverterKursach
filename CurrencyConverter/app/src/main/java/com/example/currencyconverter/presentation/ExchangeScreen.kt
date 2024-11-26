package com.example.currencyconverter.presentation
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.automirrored.rounded.Backspace
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import com.example.currencyconverter.presentation.ui.theme.CurrencyConverterTheme

@Composable
fun ExchangeScreenCore(
    viewModel: ExchangeVM = koinViewModel()
) {
    ExchangeScreen (
        state = viewModel.state,
        onAction = viewModel::onAction
    )
}

@Composable
private fun ExchangeScreen (
    state: ExchangeState,
    onAction: (ExchangeAction) -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(modifier = Modifier.weight(0.7f).fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxSize().offset(200.dp, (-40).dp).clip(CircleShape).background(MaterialTheme.colorScheme.tertiary.copy(0.1f))
            )
            Box(modifier = Modifier.fillMaxSize().offset((-280).dp, 150.dp).clip(CircleShape).background(MaterialTheme.colorScheme.inversePrimary.copy(0.3f)
            )
            )
            var isDropDownOpen by rememberSaveable {
                mutableStateOf(false)
            }
            var isFromCurrencyPicker by rememberSaveable {
                mutableStateOf(false)
            }

            ExchangeSection(
                state = state,
                onOpenCurrencyPicker = {isFromCurrency ->
                    isFromCurrencyPicker = isFromCurrency
                    isDropDownOpen = true
                }
            )



            DropdownMenu(
                modifier = Modifier.heightIn(max = 600.dp),
                expanded = isDropDownOpen,
                onDismissRequest = {
                    isDropDownOpen = false
                }
            ) {
                if (state.allCurrencies.isNotEmpty()) {
                    state.allCurrencies.forEachIndexed { index, currency ->
                        Column {
                            if (index == 0) {
                                HorizontalDivider()
                            }
                            Text(
                                text = "${currency.code} - ${currency.name}",
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier
                                    .clickable {
                                        isDropDownOpen = false
                                        if (isFromCurrencyPicker) {
                                            onAction(ExchangeAction.SelectedFrom(index))
                                        } else {
                                            onAction(ExchangeAction.SelectedTo(index))
                                        }
                                    }
                                    .padding(16.dp)
                            )
                            HorizontalDivider()
                        }
                    }
                } else {
                    Text("Валюты не загружены", modifier = Modifier.padding(16.dp))
                }
            }
        }
        InputSection(
            modifier = Modifier.weight(1.3f),
            onAction = onAction
        )
    }
}
@Composable
fun InputSection(
    modifier: Modifier = Modifier,
    onAction: (ExchangeAction) -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
            .background(MaterialTheme.colorScheme.background)
    ){
        Row (
            modifier = Modifier.fillMaxWidth().weight(1f)
        ){
            Input(text = "7") {
                onAction(ExchangeAction.Input(it))
            }
            Input(text = "8") {
                onAction(ExchangeAction.Input(it))
            }
            Input(text = "9") {
                onAction(ExchangeAction.Input(it))
            }
            Input(
                text = "C",
                color = Color.Red
            ) {
                onAction(ExchangeAction.Clear)
            }
        }
        Row (
            modifier = Modifier.fillMaxWidth().weight(1f)
        ){
            Input(text = "4") {
                onAction(ExchangeAction.Input(it))
            }
            Input(text = "5") {
                onAction(ExchangeAction.Input(it))
            }
            Input(text = "6") {
                onAction(ExchangeAction.Input(it))
            }
            Input()
        }
        Row (
            modifier = Modifier.fillMaxWidth().weight(1f)
        ){
            Input(text = "1") {
                onAction(ExchangeAction.Input(it))
            }
            Input(text = "2") {
                onAction(ExchangeAction.Input(it))
            }
            Input(text = "3") {
                onAction(ExchangeAction.Input(it))
            }
            Input()
        }
        Row (
            modifier = Modifier.fillMaxWidth().weight(1f)
        ){
            Input(text = "00") {
                onAction(ExchangeAction.Input(it))
            }
            Input(text = "0") {
                onAction(ExchangeAction.Input(it))
            }
            Input(text = ".") {
                onAction(ExchangeAction.Input(it))
            }
            Input(
                icon = Icons.AutoMirrored.Rounded.Backspace,
                color = MaterialTheme.colorScheme.primary
            ) {
                onAction(ExchangeAction.Delete)
            }
        }

    }
}

@Composable
fun RowScope.Input(
    modifier: Modifier = Modifier,
    text: String? = null,
    icon: ImageVector? = null,
    color: Color = MaterialTheme.colorScheme.onBackground,
    onClick: (String) -> Unit = {}
){
    Box(modifier = modifier.weight(1f).fillMaxHeight().clickable { onClick(text ?: "") }, contentAlignment = Alignment.Center)
    {
        if(text != null) {
            Text(text = text, fontSize = 35.sp, fontFamily = FontFamily.Monospace, color = color)
        } else if (icon != null) {
            Icon(imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(30.dp))
        }
    }
}

@Composable
fun ExchangeSection(
    modifier: Modifier = Modifier,
    state: ExchangeState,
    onOpenCurrencyPicker: (Boolean) -> Unit
) {

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .clickable {
                        onOpenCurrencyPicker(true )
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = state.from.code,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 30.sp
                )
                Spacer(modifier = Modifier.width(3.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                    contentDescription = "Selected from currency"
                )
            }

            Text(
                text = state.amount,
                fontFamily = FontFamily.SansSerif,
                fontSize = 30.sp,
                textAlign = TextAlign.End,
                maxLines = 1,
                color = MaterialTheme.colorScheme.primary,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .clickable {

                        onOpenCurrencyPicker(false)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = state.to.code,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 30.sp
                )
                Spacer(modifier = Modifier.width(3.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                    contentDescription = "Selected to currency"
                )
            }

            Text(
                text = state.result,
                fontFamily = FontFamily.SansSerif,
                fontSize = 30.sp,
                textAlign = TextAlign.End,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview
@Composable
private fun ExchangeScreenPreview() {
    CurrencyConverterTheme {
        ExchangeScreen(
            state = ExchangeState(),
            onAction = {}
        )
    }
}
