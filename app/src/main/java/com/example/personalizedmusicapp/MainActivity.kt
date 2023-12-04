package com.example.personalizedmusicapp

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.personalizedmusicapp.data.PlayListItemsResponse
import com.example.personalizedmusicapp.model.VideoViewModel
import com.example.personalizedmusicapp.room.VideoDatabase
import com.example.personalizedmusicapp.screen.FavouritesScreen
import com.example.personalizedmusicapp.screen.HomeScreen
import com.example.personalizedmusicapp.screen.PlayerScreen
import com.example.personalizedmusicapp.ui.theme.PersonalizedMusicAppTheme
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("playlistItems")
    suspend fun getPlaylistItems(
        @Query("part") part: String,
        @Query("maxResults") maxResults: String,
        @Query("playlistId") playlistId: String,
        @Query("key") key: String
    ): Response<PlayListItemsResponse>
}

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            VideoDatabase::class.java,
            "videos.db"
        ).build()
    }
    private val viewModel by viewModels<VideoViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return VideoViewModel(db.dao) as T
                }
            }
        }
    )

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PersonalizedMusicAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val state by viewModel.state.collectAsState()

                    Scaffold(
                        bottomBar = {
                            BottomNavigationBar(navController = navController)
                        }
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = "home"
                        ) {
                            composable("home") { HomeScreen(state = state, onEvent=viewModel::onEvent)}
                            composable("favourites") { FavouritesScreen(state=state, onEvent=viewModel::onEvent) }
                            composable("player") { PlayerScreen(state=state, onEvent=viewModel::onEvent) }
                        }
                    }
                }
            }
        }
    }
}

data class BottomNavItem(
    val label: String,
    val selectedIcon: ImageVector,
    var unselectedIcon: ImageVector,
    val route: String
)

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem("Home", Icons.Filled.Home, Icons.Outlined.Home, "home"),
        BottomNavItem("Favourites", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder, "favourites"),
        BottomNavItem("Player", Icons.Filled.PlayArrow, Icons.Outlined.PlayArrow, "player")
    )

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth(),
        containerColor = Color.White
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (currentRoute == item.route) item.selectedIcon
                        else item.unselectedIcon,
                        contentDescription = item.label,
                        tint = Color(0xFF00A9FF)
                    )
                },
                label = { Text(
                    item.label
                ) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        // Use NavOptions if you need to customize the navigation behavior
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

