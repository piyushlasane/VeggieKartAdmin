package com.project.veggiekartadmin.screens.banners

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.project.veggiekartadmin.utils.CloudinaryUploader
import com.project.veggiekartadmin.viewmodel.BannerViewModel
import kotlinx.coroutines.launch

@Composable
fun BannersScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    bannerViewModel: BannerViewModel = viewModel()
) {
    val context = LocalContext.current
    val banners by bannerViewModel.banners.collectAsState()
    val isLoading by bannerViewModel.isLoading.collectAsState()
    val isSaving by bannerViewModel.isSaving.collectAsState()
    val scope = rememberCoroutineScope()

    var localBanners by remember { mutableStateOf(listOf("")) }
    var uploadingIndex by remember { mutableStateOf<Int?>(null) }
    var pickingIndex by remember { mutableStateOf<Int?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        val index = pickingIndex
        if (uri != null && index != null) {
            uploadingIndex = index
            scope.launch {
                val result = CloudinaryUploader.uploadImage(context, uri)
                result.fold(
                    onSuccess = { url ->
                        localBanners = localBanners.toMutableList().also { it[index] = url }
                        uploadingIndex = null
                    },
                    onFailure = { e ->
                        scope.launch { snackbarHostState.showSnackbar("Upload failed: ${e.localizedMessage}") }
                        uploadingIndex = null
                    }
                )
            }
        }
        pickingIndex = null
    }

    LaunchedEffect(banners) {
        localBanners = if (banners.isEmpty()) listOf("") else banners.toMutableList()
    }

    LaunchedEffect(Unit) {
        bannerViewModel.loadBanners()
    }

    if (isLoading) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                Text("Manage Banner Images", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Pick images for the home screen banners",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            itemsIndexed(localBanners) { index, url ->
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                pickingIndex = index
                                imagePickerLauncher.launch("image/*")
                            },
                            modifier = Modifier.weight(1f),
                            enabled = !isSaving && uploadingIndex == null
                        ) {
                            if (uploadingIndex == index) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Uploading...")
                            } else {
                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (url.isEmpty()) "Pick Banner ${index + 1}" else "Change Banner ${index + 1}")
                            }
                        }
                        if (localBanners.size > 1) {
                            IconButton(
                                onClick = {
                                    localBanners = localBanners.toMutableList().also { it.removeAt(index) }
                                },
                                enabled = uploadingIndex == null
                            ) {
                                Icon(Icons.Outlined.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }

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
                    enabled = !isSaving && uploadingIndex == null
                ) {
                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Banner")
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        val filteredBanners = localBanners.filter { it.trim().isNotEmpty() }
                        if (filteredBanners.isEmpty()) {
                            scope.launch { snackbarHostState.showSnackbar("Please add at least one banner image") }
                            return@Button
                        }
                        bannerViewModel.saveBanners(filteredBanners) { success, message ->
                            scope.launch { snackbarHostState.showSnackbar(message) }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isSaving && uploadingIndex == null
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Save Banners", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}