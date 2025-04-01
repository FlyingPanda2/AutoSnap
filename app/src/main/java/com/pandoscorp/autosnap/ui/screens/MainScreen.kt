package com.pandoscorp.autosnap.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.pandoscorp.autosnap.ui.components.DrawerBody
import com.pandoscorp.autosnap.ui.components.DrawerHeader
import com.pandoscorp.autosnap.ui.components.TopAppBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainForm(navController: NavHostController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val menuOpenClose: () -> Unit = {
        coroutineScope.launch {
            if (drawerState.isClosed) {
                drawerState.open()
            } else {
                drawerState.close()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                NavController = navController,
                menuOpenClose = menuOpenClose,
            )
        },
        content = { padding ->

            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    Column(
                        modifier = Modifier.fillMaxWidth(0.7f)
                    ) {
                        DrawerHeader()
                        DrawerBody()
                    }
                }
            ) {}

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Text(
                    text = "Основной контент",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    )
}