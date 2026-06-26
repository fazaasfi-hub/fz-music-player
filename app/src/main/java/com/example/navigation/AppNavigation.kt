package com.example.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.components.FloatingPlayer
import com.example.screens.HomeScreen
import com.example.screens.LibraryScreen
import com.example.screens.PlayerScreen
import com.example.screens.SearchScreen
import com.example.screens.SettingsScreen
import com.example.viewmodel.MusicViewModel

@Composable
fun AppNavigation(viewModel: MusicViewModel) {
    val navController = rememberNavController()
    
    val currentTrack by viewModel.currentTrack.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val progress by viewModel.progress.collectAsState()
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route

    val isPlayerScreen = currentRoute == "player"

    // Settings collected from viewModel to make them functional
    val navBarStyle by viewModel.navBarStyle.collectAsState()
    val playerTheme by viewModel.playerTheme.collectAsState()
    val defaultTab by viewModel.defaultTab.collectAsState()

    val startDest = remember(defaultTab) {
        when (defaultTab) {
            "Home" -> "home"
            "Search" -> "search"
            else -> "library"
        }
    }

    Scaffold(
        bottomBar = {
            if (!isPlayerScreen) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding(),
                    contentAlignment = Alignment.Center
                ) {
                    NavigationBar(
                        containerColor = if (navBarStyle != "Full Width") MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.background,
                        tonalElevation = if (navBarStyle != "Full Width") 8.dp else 0.dp,
                        modifier = when (navBarStyle) {
                            "Floating Rounded" -> {
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .clip(RoundedCornerShape(24.dp))
                            }
                            "Compact" -> {
                                Modifier
                                    .width(280.dp)
                                    .padding(bottom = 12.dp)
                                    .clip(RoundedCornerShape(32.dp))
                            }
                            else -> Modifier.fillMaxWidth()
                        }
                    ) {
                        val items = listOf(
                            Triple("Home", "home", Icons.Default.Home),
                            Triple("Search", "search", Icons.Default.Search),
                            Triple("Library", "library", Icons.Default.LibraryMusic)
                        )
                        
                        items.forEach { (title, route, icon) ->
                            NavigationBarItem(
                                icon = { Icon(icon, contentDescription = title) },
                                label = { Text(title) },
                                selected = currentDestination?.hierarchy?.any { it.route == route } == true,
                                onClick = {
                                    navController.navigate(route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
        ) {
            key(startDest) {
                NavHost(
                    navController = navController,
                    startDestination = startDest,
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable("home") { 
                        HomeScreen(
                            viewModel = viewModel,
                            onNavigateToSettings = { navController.navigate("settings") }
                        ) 
                    }
                    composable("search") { SearchScreen(viewModel = viewModel) }
                    composable("library") { 
                        LibraryScreen(
                            viewModel = viewModel,
                            onNavigateToSettings = { navController.navigate("settings") }
                        ) 
                    }
                    composable("settings") { 
                        SettingsScreen(viewModel = viewModel, onBack = { navController.popBackStack() }) 
                    }
                    composable("player") { 
                        PlayerScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        ) 
                    }
                }
            }
            
            if (!isPlayerScreen && currentTrack != null) {
                val playerPadding = when (navBarStyle) {
                    "Floating Rounded" -> 88.dp
                    "Compact" -> 88.dp
                    else -> 8.dp
                }
                FloatingPlayer(
                    track = currentTrack,
                    isPlaying = isPlaying,
                    progress = progress,
                    playerTheme = playerTheme,
                    onPlayPause = { viewModel.togglePlayPause() },
                    onNext = { viewModel.nextTrack() },
                    onPrevious = { viewModel.previousTrack() },
                    onClick = { navController.navigate("player") },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = playerPadding)
                )
            }
        }
    }
}
