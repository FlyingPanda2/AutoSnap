package com.pandoscorp.autosnap.ui.components

import android.graphics.drawable.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.pandoscorp.autosnap.navigation.ScreenObject

@Composable
fun DrawerBody(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .shadow(2.dp, shape = RectangleShape)
                .clickable { navController.navigate(ScreenObject.SheduleScreen.route) },
            contentAlignment = Alignment.CenterStart
        ){
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(start = 10.dp)
            ){
                Icon(
                    Icons.Filled.DateRange,
                    contentDescription = "Меню",
                    tint = Color.White,
                )
                Text(
                    text = "Записи",
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = Modifier.padding(start = 20.dp),
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .shadow(2.dp, shape = RectangleShape)
                .clickable { navController.navigate(ScreenObject.ClientsScreen.route) },
            contentAlignment = Alignment.CenterStart
        ){
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(start = 10.dp)
            ){
                Icon(
                    Icons.Filled.Face,
                    contentDescription = "Клиенты",
                    tint = Color.White,
                )
                Text(
                    text = "Клиенты",
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = Modifier.padding(start = 20.dp),
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .shadow(2.dp, shape = RectangleShape)
                .clickable {  },
            contentAlignment = Alignment.CenterStart
        ){
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(start = 10.dp)
            ){
                Icon(
                    Icons.Filled.MailOutline,
                    contentDescription = "Меню",
                    tint = Color.White,
                )
                Text(
                    text = "Сообщения",
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = Modifier.padding(start = 20.dp),
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .shadow(2.dp, shape = RectangleShape)
                .clickable {  },
            contentAlignment = Alignment.CenterStart
        ){
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(start = 10.dp)
            ){
                Icon(
                    Icons.Filled.Settings,
                    contentDescription = "Меню",
                    tint = Color.White,
                )
                Text(
                    text = "Настройки",
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = Modifier.padding(start = 20.dp),
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .shadow(2.dp, shape = RectangleShape)
                .clickable {  },
            contentAlignment = Alignment.CenterStart
        ){
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(start = 10.dp)
            ){
                Icon(
                    Icons.Filled.ThumbUp,
                    contentDescription = "Меню",
                    tint = Color.White,
                )
                Text(
                    text = "Отзывы",
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = Modifier.padding(start = 20.dp),
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .shadow(2.dp, shape = RectangleShape)
                .clickable {  },
            contentAlignment = Alignment.CenterStart
        ){
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(start = 10.dp)
            ){
                Icon(
                    Icons.Filled.Build,
                    contentDescription = "Меню",
                    tint = Color.White,
                )
                Text(
                    text = "Помощь",
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = Modifier.padding(start = 20.dp),
                )
            }
        }
    }
}