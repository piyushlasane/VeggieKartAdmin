# 🥬 VeggieKart Admin - Grocery Store Management App

<div align="center">
  
  ![Android](https://img.shields.io/badge/Platform-Android-green?logo=android)
  ![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple?logo=kotlin)
  ![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-blue)
  ![Firebase](https://img.shields.io/badge/Backend-Firebase-orange?logo=firebase)
  
</div>

---

## 📖 About

**VeggieKart Admin** is the companion admin application for the VeggieKart grocery delivery platform. Built with Jetpack Compose and Firebase, it provides store managers with a clean, intuitive interface to manage products, categories, and promotional banners — all synced in real-time to the customer-facing VeggieKart app.

---

## ✨ Features

### 🔐 Authentication
- **Email/Password Login**: Secure Firebase email-based admin authentication
- **Session Persistence**: Stay logged in across app restarts

### 📦 Product Management
- **View All Products**: Scrollable list with image, title, price, and category
- **Add Product**: Full form with title, description, MRP, sell price, category dropdown, and multiple image URLs with live preview
- **Edit Product**: Pre-filled form for quick updates
- **Delete Product**: Confirmation dialog before deletion

### 🗂️ Category Management
- **View All Categories**: List with image preview and name
- **Add Category**: Name and image URL with live preview
- **Edit Category**: Update existing category details
- **Delete Category**: Safe deletion with confirmation

### 🖼️ Banner Management
- **View Current Banners**: Live image preview for each banner URL
- **Add/Remove Banners**: Dynamic list with add and delete controls
- **Save Banners**: One-tap save to update banners across the user app instantly

### 🎨 Modern UI/UX
- **Material Design 3**: Clean and modern admin interface
- **Jetpack Compose**: Fully declarative UI
- **Bottom Navigation**: Quick switching between Products, Categories, and Banners
- **Loading States**: Progress indicators for all async operations
- **Snackbar Feedback**: Clear success/error messages for every action

---

## 🚀 Technologies Used

### 💻 Android Development
- **Kotlin** — Primary programming language
- **Jetpack Compose** — Declarative UI toolkit
- **MVVM Architecture** — Clean separation of concerns
- **Coroutines + Flow** — Asynchronous state management
- **Navigation Component** — Screen navigation
- **ViewModel** — Lifecycle-aware state holders

### 🔥 Firebase Services
- **Firebase Authentication** — Email/password admin login
- **Cloud Firestore** — Real-time NoSQL database (shared with user app)

### 📚 Libraries
- **Coil** — Image loading and caching
- **Material Icons Extended** — Rich icon set
- **Material 3** — Latest Material Design components

---

## 🎨 Screens Overview

| Screen | Description |
|--------|-------------|
| 🔐 **Login Screen** | Admin email/password authentication |
| 🏠 **Dashboard** | Bottom nav hub with Products, Categories, Banners |
| 📦 **Products Screen** | List all products with edit/delete actions |
| ➕ **Add/Edit Product** | Full product form with image preview |
| 🗂️ **Categories Screen** | List all categories with edit/delete actions |
| ➕ **Add/Edit Category** | Category form with image preview |
| 🖼️ **Banners Screen** | Manage home screen banner URLs |

---

## 🏗️ App Architecture

```
VeggieKartAdmin/
├── model/
│   ├── ProductModel.kt
│   ├── CategoryModel.kt
│   └── AdminModel.kt
├── viewmodel/
│   ├── AuthViewModel.kt
│   ├── ProductViewModel.kt
│   ├── CategoryViewModel.kt
│   └── BannerViewModel.kt
├── screens/
│   ├── auth/
│   │   └── LoginScreen.kt
│   ├── dashboard/
│   │   └── DashboardScreen.kt
│   ├── products/
│   │   ├── ProductsScreen.kt
│   │   └── AddEditProductScreen.kt
│   ├── categories/
│   │   ├── CategoriesScreen.kt
│   │   └── AddEditCategoryScreen.kt
│   └── banners/
│       └── BannersScreen.kt
├── navigation/
│   ├── AppNavigation.kt
│   └── Routes.kt
├── utils/
│   └── AppUtil.kt
└── ui/theme/
```

**Architecture Pattern**: MVVM (Model-View-ViewModel)
- **Model**: Data classes mirroring Firestore structure
- **View**: Composable functions
- **ViewModel**: Business logic, Firestore operations, StateFlow

---

## 🗄️ Database Structure

This app shares the same Firebase project as VeggieKart user app.

```
firestore/
└── data/
    ├── banners/
    │   └── urls: Array<String>
    └── stock/
        ├── categories/
        │   └── {categoryId}/
        │       ├── id: String
        │       ├── name: String
        │       └── imageUrl: String
        └── products/
            └── {productId}/
                ├── id: String
                ├── title: String
                ├── description: String
                ├── category: String (categoryId)
                ├── price: String
                ├── actualPrice: String
                └── images: Array<String>
```

---

## 🔒 Firestore Security Rules

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }

    match /data/{document=**} {
      allow read: if true;
      allow write: if request.auth != null;
    }
  }
}
```

---

## 🛠️ Setup & Installation

### Prerequisites
- Android Studio Hedgehog or later
- JDK 11 or higher
- Firebase project (same as VeggieKart user app)

### Installation Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/piyushlasane/veggiekart-admin.git
   cd veggiekart-admin
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned repository

3. **Configure Firebase**
   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Open your existing VeggieKart Firebase project
   - Add a new Android app with package `com.project.veggiekartadmin`
   - Download `google-services.json`
   - Place it in the `app/` directory
   - Enable Email/Password Authentication
   - Create an admin user in Firebase Auth → Users

4. **Sync Gradle and Run**
   ```
   Sync Project with Gradle Files → Run on device/emulator
   ```

---

## 🚧 Roadmap

### 📦 Orders Management (Planned)
- View all customer orders
- Update order status: Pending → Confirmed → Dispatched → Delivered
- Order details with customer info

### 📊 Dashboard Analytics (Planned)
- Total products, categories count
- Recent orders summary
- Revenue overview

### 🔔 Push Notifications (Planned)
- Notify customers on order status change
- Promotional notification broadcast

---

## 🤝 Related Project

This admin app is built to manage the **VeggieKart** customer app.  
👉 [VeggieKart User App](https://github.com/piyushlasane/veggiekart)

---

## 📄 License

© 2025 Piyush Lasane. All rights reserved.  
This project is for educational and portfolio purposes only.

---

## 👨‍💻 Developer

<table>
  <tbody>
    <tr>
      <td align="center">
        <a href="https://github.com/piyushlasane">
          <img src="https://github.com/piyushlasane.png" width="100" alt="Piyush Lasane"/><br />
          <sub><b>Piyush Lasane</b></sub>
        </a><br />
        <a href="#" title="Code">💻</a>
        <a href="#" title="Design">🎨</a>
        <a href="#" title="Architecture">🏗️</a>
        <a href="#" title="Documentation">📖</a>
      </td>
    </tr>
  </tbody>
</table>

---

## 📞 Contact

- **Email**: piyushlasane@gmail.com
- **LinkedIn**: https://www.linkedin.com/in/piyushlasane/
- **GitHub**: https://github.com/piyushlasane/

---

<div align="center">
  <p>⭐ Star this repository if you found it helpful!</p>
</div>