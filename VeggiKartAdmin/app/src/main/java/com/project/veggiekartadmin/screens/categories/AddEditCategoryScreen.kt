package com.project.veggiekartadmin.screens.categories

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
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
import com.project.veggiekartadmin.viewmodel.CategoryViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCategoryScreen(
    navController: NavHostController,
    categoryId: String? = null,
    categoryViewModel: CategoryViewModel = viewModel()
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var isUploading by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(categoryId != null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            isUploading = true
            scope.launch {
                val result = CloudinaryUploader.uploadImage(context, uri)
                result.fold(
                    onSuccess = { url ->
                        imageUrl = url
                        isUploading = false
                    },
                    onFailure = { e ->
                        snackbarHostState.showSnackbar("Upload failed: ${e.localizedMessage}")
                        isUploading = false
                    }
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        if (categoryId != null) {
            categoryViewModel.getCategoryById(categoryId) { category ->
                category?.let {
                    name = it.name
                    imageUrl = it.imageUrl
                }
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (categoryId == null) "Add Category" else "Edit Category",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Category Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isSaving && !isUploading
                )

                // Image picker button
                OutlinedButton(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isSaving && !isUploading
                ) {
                    if (isUploading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Uploading...")
                    } else {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (imageUrl.isEmpty()) "Pick Image" else "Change Image")
                    }
                }

                // Image preview
                if (imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Preview",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        when {
                            name.trim().isEmpty() -> {
                                scope.launch { snackbarHostState.showSnackbar("Please enter category name") }
                                return@Button
                            }
                            imageUrl.trim().isEmpty() -> {
                                scope.launch { snackbarHostState.showSnackbar("Please pick an image") }
                                return@Button
                            }
                        }
                        isSaving = true
                        categoryViewModel.saveCategory(
                            categoryId = categoryId,
                            name = name.trim(),
                            imageUrl = imageUrl.trim()
                        ) { success, message ->
                            isSaving = false
                            scope.launch {
                                snackbarHostState.showSnackbar(message)
                                if (success) {
                                    kotlinx.coroutines.delay(800)
                                    navController.navigateUp()
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isSaving && !isUploading
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(
                            if (categoryId == null) "Add Category" else "Update Category",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}