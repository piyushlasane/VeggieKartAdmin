package com.project.veggiekartadmin.screens.categories

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
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
import com.project.veggiekartadmin.model.CategoryModel
import com.project.veggiekartadmin.navigation.Routes
import com.project.veggiekartadmin.viewmodel.CategoryViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    categoryViewModel: CategoryViewModel = viewModel()
) {
    val categories by categoryViewModel.categories.collectAsState()
    val isLoading by categoryViewModel.isLoading.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf<CategoryModel?>(null) }

    LaunchedEffect(Unit) {
        categoryViewModel.loadCategories()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = {
                Text("Categories", fontWeight = FontWeight.Bold)
            })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Routes.addEditCategory()) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Category")
            }
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
        } else if (categories.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No categories yet. Tap + to add.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(categories) { category ->
                    CategoryCard(
                        category = category,
                        onEdit = {
                            navController.navigate(Routes.addEditCategory(category.id))
                        },
                        onDelete = {
                            showDeleteDialog = category
                        }
                    )
                }
            }
        }

        showDeleteDialog?.let { category ->
            AlertDialog(
                onDismissRequest = { showDeleteDialog = null },
                title = { Text("Delete Category") },
                text = { Text("Are you sure you want to delete '${category.name}'?") },
                confirmButton = {
                    TextButton(onClick = {
                        showDeleteDialog = null
                        categoryViewModel.deleteCategory(category.id) { success, message ->
                            scope.launch {
                                snackbarHostState.showSnackbar(message)
                            }
                        }
                    }) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun CategoryCard(
    category: CategoryModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = category.imageUrl,
                contentDescription = category.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = category.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = onEdit) {
                Icon(
                    Icons.Outlined.Edit,
                    contentDescription = "Edit",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Outlined.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}