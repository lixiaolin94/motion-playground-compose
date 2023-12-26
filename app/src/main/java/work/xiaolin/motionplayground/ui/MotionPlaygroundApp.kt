package work.xiaolin.motionplayground.ui

import androidx.annotation.StringRes
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import work.xiaolin.motionplayground.R
import work.xiaolin.motionplayground.ui.components.SpringConfiguration
import work.xiaolin.motionplayground.ui.theme.MotionPlaygroundTheme

sealed class Screen(
    val route: String,
    @StringRes val label: Int,
    val content: @Composable (Float, Float) -> Unit
) {
    data object SpringAnimationPlayground : Screen(
        route = "SpringAnimationPlayground",
        label = R.string.spring_animation_playground,
        content = { dampingRatio, stiffness ->
            SpringAnimationPlayground(
                dampingRatio = dampingRatio,
                stiffness = stiffness
            )
        }
    )

    data object SpringDynamics : Screen(
        route = "SpringDynamics",
        label = R.string.spring_dynamics,
        content = { dampingRatio, stiffness ->
            SpringDynamics(
                dampingRatio = dampingRatio,
                stiffness = stiffness
            )
        }
    )

    data object DraggableFloatWindow : Screen(
        route = "DraggableFloatWindow",
        label = R.string.draggable_float_window,
        content = { dampingRatio, stiffness ->
            DraggableFloatWindow(
                dampingRatio = dampingRatio,
                stiffness = stiffness
            )
        }
    )

    data object PopToggleButton : Screen(
        route = "PopToggleButton",
        label = R.string.pop_toggle_button,
        content = { dampingRatio, stiffness ->
            PopToggleButton(
                dampingRatio = dampingRatio,
                stiffness = stiffness
            )
        }
    )

    data object InstantResponseButton : Screen(
        route = "InstantResponseButton",
        label = R.string.instant_response_button,
        content = { dampingRatio, stiffness ->
            InstantResponseButton(
                dampingRatio = dampingRatio,
                stiffness = stiffness
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun MotionPlaygroundApp() {
    val screens = listOf(
        Screen.SpringAnimationPlayground,
        Screen.SpringDynamics,
        Screen.DraggableFloatWindow,
        Screen.PopToggleButton,
        Screen.InstantResponseButton
    )

    val scope = rememberCoroutineScope()

    val navController = rememberNavController()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Open)

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var showBottomSheet by remember { mutableStateOf(false) }

    var dampingRatio by rememberSaveable { mutableFloatStateOf(0.7f) }

    var stiffness by rememberSaveable { mutableFloatStateOf(300f) }

    MotionPlaygroundTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            ModalNavigationDrawer(
                drawerState = drawerState,
                gesturesEnabled = drawerState.isOpen,
                drawerContent = {
                    ModalDrawerSheet(Modifier.width(320.dp)) {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination
                        Spacer(Modifier.height(dimensionResource(R.dimen.padding_large)))
                        screens.forEach { screen ->
                            NavigationDrawerItem(modifier = Modifier.padding(
                                horizontal = dimensionResource(
                                    R.dimen.padding_medium
                                )
                            ),
                                label = { Text(stringResource(screen.label)) },
                                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }

                                    scope.launch { drawerState.close() }
                                })
                        }
                    }
                },
            ) {
                Scaffold(
                    bottomBar = {
                        BottomAppBar(actions = {
                            IconButton(onClick = {
                                scope.launch {
                                    drawerState.apply {
                                        if (isClosed) open() else close()
                                    }
                                }
                            }) {
                                Icon(Icons.Rounded.Menu, contentDescription = "Show drawer")
                            }
                        }, floatingActionButton = {
                            FloatingActionButton(
                                onClick = {
                                    showBottomSheet = true
                                },
                                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                            ) {
                                Icon(Icons.Rounded.Settings, "Show bottom sheet")
                            }
                        })
                    },
                ) { innerPadding ->
                    NavHost(
                        modifier = Modifier.padding(innerPadding),
                        navController = navController,
                        startDestination = Screen.SpringAnimationPlayground.route,
                        enterTransition = { EnterTransition.None },
                        exitTransition = { ExitTransition.None },
                        popEnterTransition = { EnterTransition.None },
                        popExitTransition = { ExitTransition.None }
                    ) {
                        screens.forEach { screen ->
                            composable(screen.route) {
                                screen.content(dampingRatio, stiffness)
                            }
                        }
                    }

                    if (showBottomSheet) {
                        ModalBottomSheet(
                            onDismissRequest = { showBottomSheet = false },
                            sheetState = bottomSheetState
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(bottom = dimensionResource(R.dimen.bottom_sheet_padding_bottom))
                                    .padding(horizontal = dimensionResource(R.dimen.padding_medium))
                            ) {
                                SpringConfiguration(
                                    dampingRatio = dampingRatio,
                                    stiffness = stiffness,
                                    onDampingRatioChange = { dampingRatio = it },
                                    onStiffnessChange = { stiffness = it },
                                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

