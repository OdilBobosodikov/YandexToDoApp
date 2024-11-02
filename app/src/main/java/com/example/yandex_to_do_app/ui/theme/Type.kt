package com.example.yandex_to_do_app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yandex_to_do_app.R

val robotoFontFamily = FontFamily(
    Font(R.font.roboto_regular, FontWeight.Normal),
    Font(R.font.roboto_bold, FontWeight.Bold),
    Font(R.font.roboto_black, FontWeight.Black),
    Font(R.font.roboto_thin, FontWeight.Thin),
    Font(R.font.roboto_light, FontWeight.Light),
    Font(R.font.roboto_medium, FontWeight.Medium),
)


val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
    ),
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)
@Composable
fun AppTypography(): Typography {
    return Typography(
        titleLarge = TextStyle(
            fontFamily = robotoFontFamily,
            fontSize = 32.sp,
            fontWeight = FontWeight.Medium,
            color = colorResource(id = R.color.primary)
        ),
        titleMedium = TextStyle(
            fontFamily = robotoFontFamily,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = colorResource(id = R.color.primary)
        ),
        //For button I used titleSmall
        titleSmall = TextStyle(
            fontFamily = robotoFontFamily,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = colorResource(id = R.color.primary)
        ),
        bodyMedium = TextStyle(
            fontFamily = robotoFontFamily,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Black
        ),
        headlineSmall = TextStyle(
            fontFamily = robotoFontFamily,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Black
        ),

    )
}