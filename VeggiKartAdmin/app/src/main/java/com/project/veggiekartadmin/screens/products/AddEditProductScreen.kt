package com.project.veggiekartadmin.screens.products

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.project.veggiekartadmin.viewmodel.CategoryViewModel
import com.project.veggiekartadmin.viewmodel.ProductViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductScreen(
    navController: NavHostController,
    productId: String? = null,
    productViewModel: ProductViewModel = viewModel(),
    categoryViewModel: CategoryViewModel = viewModel()
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var actualPrice by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var imageUrls by remember { mutableStateOf(listOf<String>()) }
    var uploadingIndex by remember { mutableStateOf<Int?>(null) }
    var isSaving by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(productId != null) }
    var categoryExpanded by remember { mutableStateOf(false) }

    val categories by categoryViewModel.categories.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // tracks which index the picker was opened for
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
                        imageUrls = imageUrls.toMutableList().also { it[index] = url }
                        uploadingIndex = null
                    },
                    onFailure = { e ->
                        snackbarHostState.showSnackbar("Upload failed: ${e.localizedMessage}")
                        uploadingIndex = null
                    }
                )
            }
        }
        pickingIndex = null
    }

    LaunchedEffect(Unit) {
        categoryViewModel.loadCategories()
        if (productId != null) {
            productViewModel.getProductById(productId) { product ->
                product?.let {
                    title = it.title
                    description = it.description
                    price = it.price
                    actualPrice = it.actualPrice
                    selectedCategory = it.category
                    imageUrls = if (it.images.isEmpty()) listOf("") else it.images.toMutableList()
                }
                isLoading = false
            }
        } else {
            imageUrls = listOf("")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (productId == null) "Add Product" else "Edit Product",
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Product Title *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = !isSaving
                    )
                }

                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description *") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5,
                        enabled = !isSaving
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = price,
                            onValueChange = { price = it },
                            label = { Text("MRP (₹) *") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            enabled = !isSaving
                        )
                        OutlinedTextField(
                            value = actualPrice,
                            onValueChange = { actualPrice = it },
                            label = { Text("Sell Price (₹) *") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            enabled = !isSaving
                        )
                    }
                }

                item {
                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = categories.find { it.id == selectedCategory }?.name ?: selectedCategory,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category *") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                            },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            enabled = !isSaving
                        )
                        ExposedDropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false }
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category.name) },
                                    onClick = {
                                        selectedCategory = category.id
                                        categoryExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    Text("Images", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }

                itemsIndexed(imageUrls) { index, url ->
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
                                    Text(if (url.isEmpty()) "Pick Image ${index + 1}" else "Change Image ${index + 1}")
                                }
                            }
                            if (imageUrls.size > 1) {
                                IconButton(
                                    onClick = {
                                        imageUrls = imageUrls.toMutableList().also { it.removeAt(index) }
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
                                contentDescription = "Preview",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                        }
                    }
                }

                item {
                    OutlinedButton(
                        onClick = { imageUrls = imageUrls + "" },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = uploadingIndex == null && !isSaving
                    ) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Another Image")
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            when {
                                title.trim().isEmpty() -> {
                                    scope.launch { snackbarHostState.showSnackbar("Please enter product title") }
                                    return@Button
                                }
                                description.trim().isEmpty() -> {
                                    scope.launch { snackbarHostState.showSnackbar("Please enter description") }
                                    return@Button
                                }
                                price.trim().isEmpty() -> {
                                    scope.launch { snackbarHostState.showSnackbar("Please enter MRP") }
                                    return@Button
                                }
                                actualPrice.trim().isEmpty() -> {
                                    scope.launch { snackbarHostState.showSnackbar("Please enter sell price") }
                                    return@Button
                                }
                                selectedCategory.isEmpty() -> {
                                    scope.launch { snackbarHostState.showSnackbar("Please select a category") }
                                    return@Button
                                }
                            }

                            val filteredImages = imageUrls.filter { it.trim().isNotEmpty() }
                            if (filteredImages.isEmpty()) {
                                scope.launch { snackbarHostState.showSnackbar("Please add at least one image") }
                                return@Button
                            }

                            isSaving = true
                            productViewModel.saveProduct(
                                productId = productId,
                                title = title.trim(),
                                description = description.trim(),
                                category = selectedCategory,
                                price = price.trim(),
                                actualPrice = actualPrice.trim(),
                                images = filteredImages
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
                        enabled = !isSaving && uploadingIndex == null
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(
                                if (productId == null) "Add Product" else "Update Product",
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