package com.project.veggiekartadmin.screens.banners

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.project.veggiekartadmin.viewmodel.BannerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BannersScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    bannerViewModel: BannerViewModel = viewModel()
) {
    val banners by bannerViewModel.banners.collectAsState()
    val isLoading by bannerViewModel.isLoading.collectAsState()
    val isSaving by bannerViewModel.isSaving.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var localBanners by remember { mutableStateOf(listOf("")) }

    // Sync local state when banners load
    LaunchedEffect(banners) {
        localBanners = if (banners.isEmpty()) listOf("") else banners.toMutableList()
    }

    LaunchedEffect(Unit) {
        bannerViewModel.loadBanners()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text("Banners", fontWeight = FontWeight.Bold)
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                item {
                    Text(
                        "Manage Banner Images",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Add image URLs for the home screen banners",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                itemsIndexed(localBanners) { index, url ->
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = url,
                                onValueChange = {
                                    localBanners = localBanners.toMutableList().also { list ->
                                        list[index] = it
                                    }
                                },
                                label = { Text("Banner URL ${index + 1}") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                enabled = !isSaving
                            )
                            if (localBanners.size > 1) {
                                IconButton(onClick = {
                                    localBanners = localBanners.toMutableList()
                                        .also { it.removeAt(index) }
                                }) {
                                    Icon(
                                        Icons.Outlined.Delete,
                                        contentDescription = "Remove",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }

                        // Image preview
                        if (url.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            AsyncImage(
                                model = url,
                                contentDescription = "Banner Preview",
                                contentScale = ContentScale.FillBounds,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                        }
                    }
                }

                item {
                    OutlinedButton(
                        onClick = { localBanners = localBanners + "" },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isSaving
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Banner URL")
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            val filteredBanners = localBanners.filter { it.trim().isNotEmpty() }
                            if (filteredBanners.isEmpty()) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Please add at least one banner URL")
                                }
                                return@Button
                            }
                            bannerViewModel.saveBanners(filteredBanners) { success, message ->
                                scope.launch {
                                    snackbarHostState.showSnackbar(message)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isSaving
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(
                                "Save Banners",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}